package com.example.BookingSystem.features.booking.domain.service.impl;

import com.example.BookingSystem.common.dto.PaginationDTO;
import com.example.BookingSystem.common.enums.BookingStatus;
import com.example.BookingSystem.exception.CoreApiException;
import com.example.BookingSystem.features.booking.domain.entity.Booking;
import com.example.BookingSystem.features.booking.domain.repository.BookingRepository;
import com.example.BookingSystem.features.booking.domain.response.BookingResponse;
import com.example.BookingSystem.features.booking.domain.service.BookService;
import com.example.BookingSystem.features.classes.domain.entity.Classes;
import com.example.BookingSystem.features.classes.domain.repository.ClassRepository;
import com.example.BookingSystem.features.packages.domain.entity.PurchasePackage;
import com.example.BookingSystem.features.packages.domain.repository.PurchasePackageRepository;
import com.example.BookingSystem.features.user.domain.response.LoginUserInfo;
import com.example.BookingSystem.features.user.domain.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private UserServices userServices;
    @Autowired
    private PurchasePackageRepository purchasePackageRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public BookingResponse bookClass(Long classId) {
        Classes cs = classRepository.findById(classId).orElseThrow(() -> new NoSuchElementException("Class Not Found"));
        LoginUserInfo loginUserInfo = userServices.getLoginUserInfo();
        List<PurchasePackage> userPackages = purchasePackageRepository
                .findByUserIdAndPkg_CountryAndExpiredFalseAndRemainingCreditsGreaterThan(
                        loginUserInfo.id(), cs.getCountry(), cs.getRequiredCredits()
                );
        if (userPackages.isEmpty()) {
            throw new CoreApiException("No valid package for this country");
        }
        PurchasePackage up = userPackages.get(0);

        if (!up.getPkg().getCountry().equals(cs.getCountry())) {
            throw new CoreApiException("Package country does not match class country");
        }

        if (up.isExpired() || up.getExpiryDateTime().isBefore(LocalDateTime.now())) {
            throw new CoreApiException("Package expired");
        }

        if (up.getRemainingCredits() < cs.getRequiredCredits()) {
            throw new CoreApiException("Not enough credits");
        }

        List<Booking> userBookings = bookingRepository.findByUserId(loginUserInfo.id());
        for (Booking b : userBookings) {
            if (b.getStatus() == BookingStatus.BOOKED &&
                    b.getClasses().getStartTime().isBefore(cs.getEndTime()) &&
                    b.getClasses().getEndTime().isAfter(cs.getStartTime())) {
                throw new CoreApiException("Overlapping booking");
            }
        }

        String redisKey = "class_slots:" + classId;
        Long bookedCount = redisTemplate.opsForValue().increment(redisKey, 1);
        if ( bookedCount > cs.getMaxSlots()) {
            redisTemplate.opsForValue().decrement(redisKey, 1); // revert increment
            redisTemplate.opsForList().rightPush("waitlist:" + classId, loginUserInfo.id().toString());
            
            // Deduct credits for waitlist users
            up.setRemainingCredits(up.getRemainingCredits() - cs.getRequiredCredits());
            purchasePackageRepository.save(up);
            
            Booking booking = new Booking();
            booking.setUser(up.getUser());
            booking.setClasses(cs);
            booking.setPurchasePackage(up);
            booking.setStatus(BookingStatus.WAITLISTED);
            booking.setBookingTime(LocalDateTime.now());
            bookingRepository.save(booking);
            return BookingResponse.from(booking);
        }

        up.setRemainingCredits(up.getRemainingCredits() - cs.getRequiredCredits());
        purchasePackageRepository.save(up);

        Booking booking = new Booking();
        booking.setUser(up.getUser());
        booking.setClasses(cs);
        booking.setPurchasePackage(up);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setBookingTime(LocalDateTime.now());
        bookingRepository.save(booking);
        return BookingResponse.from(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow( () -> new NoSuchElementException("Booking Cannot Found"));
        Classes cs = booking.getClasses();
        LocalDateTime now = LocalDateTime.now();

        if (booking.getStatus() == BookingStatus.BOOKED) {
            if (now.isBefore(cs.getStartTime().minusHours(4))) {
                PurchasePackage up = booking.getPurchasePackage();
                up.setRemainingCredits(up.getRemainingCredits() + cs.getRequiredCredits());
                purchasePackageRepository.save(up);
            }
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            // This is Promotion from waitlist
            String nextUserId = redisTemplate.opsForList().leftPop("waitlist:" + cs.getId());
            if (nextUserId != null) {
                Long nextUid = Long.parseLong(nextUserId);
                // Finding waitlist booking for this user and class
                List<Booking> waitlistBookings = bookingRepository.findByClasses_IdAndUser_IdAndStatusIn(
                        cs.getId(), nextUid, List.of(BookingStatus.WAITLISTED));
                if (!waitlistBookings.isEmpty()) {
                    Booking waitlistBooking = waitlistBookings.get(0);
                    waitlistBooking.setStatus(BookingStatus.BOOKED);
                    // Deduct credits from user's package
                    PurchasePackage up = waitlistBooking.getPurchasePackage();
                    up.setRemainingCredits(up.getRemainingCredits() - cs.getRequiredCredits());
                    purchasePackageRepository.save(up);
                    bookingRepository.save(waitlistBooking);
                    redisTemplate.opsForValue().increment("class_slots:" + cs.getId(), 1);
                }
            } else {
                // Free up slot if there is no waitlist for this class
                redisTemplate.opsForValue().decrement("class_slots:" + cs.getId(), 1);
            }
        } else if (booking.getStatus() == BookingStatus.WAITLISTED) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            // Remove from waitlist if exit
            redisTemplate.opsForList().remove("waitlist:" + cs.getId(), 1, booking.getUser().getId().toString());
        }

        // After cancellation, verify and reset slot count if needed
        verifyAndResetClassSlots(cs.getId());
    }

    private void verifyAndResetClassSlots(Long classId) {
        // Get current booked count from database
        int actualBookedCount = bookingRepository.findByClasses_IdAndStatus(classId, BookingStatus.BOOKED).size();
        
        // Get current slot count from Redis
        String redisKey = "class_slots:" + classId;
        String slotsStr = redisTemplate.opsForValue().get(redisKey);
        int redisSlotCount = slotsStr != null ? Integer.parseInt(slotsStr) : 0;

        // If Redis count doesn't match actual count, reset it
        if (redisSlotCount != actualBookedCount) {
            redisTemplate.opsForValue().set(redisKey, String.valueOf(actualBookedCount));
        }
    }

    @Override
    @Transactional
    public void checkIn(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow( () -> new NoSuchElementException("Booking Not Found"));
        Classes cs = booking.getClasses();
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Now: " + now);
        System.out.println("Start: " + cs.getStartTime());
        System.out.println("End: " + cs.getEndTime());
        if (now.isBefore(cs.getStartTime()) || now.isAfter(cs.getEndTime())) {
            throw new CoreApiException("Check-in only allowed during class time");
        }
        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new CoreApiException("Only booked users can check in");
        }
        booking.setStatus(BookingStatus.CHECKED_IN);
        bookingRepository.save(booking);
    }


    @Override
    @Transactional
    public void refundWaitlistCredits(Long classId) {
        List<Booking> waitlist = bookingRepository.findByClasses_IdAndStatus(classId, BookingStatus.WAITLISTED);
        for (Booking b : waitlist) {
            PurchasePackage up = b.getPurchasePackage();
            up.setRemainingCredits(up.getRemainingCredits() + b.getClasses().getRequiredCredits());
            purchasePackageRepository.save(up);
            b.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(b);
        }
        redisTemplate.delete("waitlist:" + classId);
        // Also delete the class slots key to ensure clean state
        redisTemplate.delete("class_slots:" + classId);
    }

    @Override
    public PaginationDTO<BookingResponse> getUserBookingList(Pageable pageable) {
        LoginUserInfo loginUserInfo = userServices.getLoginUserInfo();
        Page<Booking> bookings = bookingRepository.findAllByUserId(loginUserInfo.id(),pageable);
        List<BookingResponse> bookingResponseList = bookings.stream().map(BookingResponse::from).toList();
        return new PaginationDTO<>(bookingResponseList,bookings.getNumber(), bookings.getSize(),
                bookings.getPageable().getOffset(), bookings.getTotalElements(), bookings.getTotalPages());
    }
}
