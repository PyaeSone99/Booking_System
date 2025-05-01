package com.example.BookingSystem.features.classes.domain.service.impl;

import com.example.BookingSystem.common.dto.PaginationDTO;
import com.example.BookingSystem.common.enums.BookingStatus;
import com.example.BookingSystem.common.enums.Country;
import com.example.BookingSystem.features.booking.domain.repository.BookingRepository;
import com.example.BookingSystem.features.classes.domain.entity.Classes;
import com.example.BookingSystem.features.classes.domain.repository.ClassRepository;
import com.example.BookingSystem.features.classes.domain.request.ClassRequest;
import com.example.BookingSystem.features.classes.domain.response.ClassesResponse;
import com.example.BookingSystem.features.classes.domain.service.ClassScheduleService;
import com.example.BookingSystem.features.classes.domain.service.ClassesService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClassesServiceImpl implements ClassesService {

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ClassScheduleService classScheduleService;


    @Override
    public ClassesResponse createClass(ClassRequest classRequest, Country country) {
        Classes classes = mapper.map(classRequest,Classes.class);
        classes.setCountry(country);
        classScheduleService.scheduleWaitlistRefundJob(classes.getId(), classes.getEndTime());
        return ClassesResponse.from(classRepository.save(classes));
    }

    @Override
    public PaginationDTO<ClassesResponse> getAllClasses(Pageable pageable) {
        Page<Classes> classes = classRepository.findAll(pageable);

        List<ClassesResponse> list = classes.stream().map(
                cs -> {
                    String redisKey = "class_slots:" + cs.getId();
                    String slotsStr = redisTemplate.opsForValue().get(redisKey);
                    int bookedCount = slotsStr != null ? Integer.parseInt(slotsStr) :
                            bookingRepository.findByClasses_IdAndStatus(cs.getId(), BookingStatus.BOOKED).size();
                    return ClassesResponse.from(cs,bookedCount);
                }
        ).toList();
        return new PaginationDTO<>(list,classes.getNumber(), classes.getSize(),
                classes.getPageable().getOffset(), classes.getTotalElements(), classes.getTotalPages());
    }
}
