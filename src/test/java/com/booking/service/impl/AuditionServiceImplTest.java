package com.booking.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Strictness;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.booking.model.Audition;
import com.booking.model.User;
import com.booking.model.Attachment;
import com.booking.model.AuditionRequest;
import com.booking.model.Round;
import com.booking.model.RoundStatus;
import com.booking.model.AuditionStatus;
import com.booking.model.Weeks;
import com.booking.repository.AuditionRepository;
import com.booking.repository.RoundRepository;
import com.booking.service.LoginUserService;
import com.booking.service.AttachmentService;
import com.booking.service.AttachmentRepository;
import com.booking.model.AuditionResponse;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import java.util.Collections;
import java.util.List;
import com.booking.model.AuditionSearch;
import com.booking.model.PaginationResponse;
import com.booking.model.RoundAddRequest;
import com.booking.model.RoundResponse;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuditionServiceImplTest {

    @Mock
    private AuditionRepository auditionRepository;
    @Mock
    private RoundRepository roundRepository;
    @Mock
    private LoginUserService loginUserService;
    @Mock
    private ModelMapper mapper;
    @Mock
    private AttachmentService attachmentService;
    @Mock
    private AttachmentRepository attachmentRepository;

    @InjectMocks
    private AuditionServiceImpl auditionService;

    private Audition audition;
    private User user;
    private Attachment attachment;
    private AuditionRequest auditionRequest;
    private Set<Round> rounds;

    @BeforeEach
    void setUp() {
        // Setup test data
        user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");

        attachment = new Attachment();
        attachment.setId(1L);
        attachment.setFileName("test.jpg");
        attachment.setFileType("image/jpeg");
        attachment.setFileSize(1024L);

        // Setup rounds
        rounds = new HashSet<>();
        Round round1 = new Round();
        round1.setId(1L);
        round1.setName("Round 1");
        round1.setStatus(RoundStatus.ACTIVE);
        rounds.add(round1);

        Round round2 = new Round();
        round2.setId(2L);
        round2.setName("Round 2");
        round2.setStatus(RoundStatus.ACTIVE);
        rounds.add(round2);

        audition = new Audition();
        audition.setId(1L);
        audition.setName("Test Audition");
        audition.setDescription("Test Description");
        audition.setStatus(AuditionStatus.ACTIVE);
        audition.setStartTime("10:00");
        audition.setWeeks(Weeks.WEEK1);
        audition.setStartDate(LocalDate.now());
        audition.setEndDate(LocalDate.now().plusDays(7));
        audition.setCreatedByUser(user);
        audition.setUpdatedByUser(user);
        audition.setCreatedAt(LocalDateTime.now());
        audition.setUpdatedAt(LocalDateTime.now());
        audition.setIsActive(true);
        audition.setAttachment(attachment);
        audition.setRounds(rounds);

        // Setup AuditionRequest
        auditionRequest = new AuditionRequest(
            "Test Audition",
            "Test Description",
            AuditionStatus.ACTIVE,
            "10:00",
            LocalDate.now(),
            LocalDate.now().plusDays(7),
            true,
            Weeks.WEEK1
        );
    }

    // ... existing test methods ...

    @Test
    @Order(6)
    void testUpdate_shouldUpdateAuditionSuccessfully() {
        // Given
        Long id = 1L;
        AuditionRequest updateRequest = new AuditionRequest(
                "Updated Audition",
                "Updated Description",
                AuditionStatus.FINISHED,
                "11:00",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(8),
                true,
                Weeks.TUESDAY
        );

        when(auditionRepository.findById(id)).thenReturn(Optional.of(audition));
        when(loginUserService.getLoginUser()).thenReturn(Optional.of(user));
        
        // Mock the mapper to actually update the audition object
        doAnswer(invocation -> {
            AuditionRequest request = invocation.getArgument(0);
            Audition targetAudition = invocation.getArgument(1);
            targetAudition.setName(request.name());
            targetAudition.setDescription(request.description());
            targetAudition.setStatus(request.status());
            targetAudition.setStartTime(request.startTime());
            targetAudition.setWeeks(request.weeks());
            targetAudition.setStartDate(request.startDate());
            targetAudition.setEndDate(request.endDate());
            targetAudition.setIsActive(request.isActive());
            return null;
        }).when(mapper).map(updateRequest, audition);

        when(auditionRepository.save(any(Audition.class))).thenAnswer(invocation -> {
            Audition savedAudition = invocation.getArgument(0);
            return savedAudition;
        });

        // When
        AuditionResponse response = auditionService.update(id, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals(updateRequest.name(), response.name());
        assertEquals(updateRequest.description(), response.description());
        assertEquals(updateRequest.status(), response.status());
        assertEquals(updateRequest.startTime(), response.startTime());
        assertEquals(updateRequest.weeks(), response.weeks());
        assertEquals(updateRequest.startDate(), response.startDate());
        assertEquals(updateRequest.endDate(), response.endDate());
        assertEquals(updateRequest.isActive(), response.isActive());

        verify(auditionRepository).findById(id);
        verify(mapper).map(updateRequest, audition);
        verify(loginUserService).getLoginUser();
        verify(auditionRepository).save(audition);
    }

    @Test
    @Order(7)
    void testUpdate_shouldThrowNoSuchElementException_whenAuditionNotFound() {
        // Given
        Long id = 99L;
        when(auditionRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            auditionService.update(id, auditionRequest);
        });

        assertEquals("Audition not found", exception.getMessage());
        verify(auditionRepository).findById(id);
        verify(mapper, never()).map(any(), any());
        verify(loginUserService, never()).getLoginUser();
        verify(auditionRepository, never()).save(any());
    }

    @Test
    @Order(8)
    void testUpdate_shouldThrowNoSuchElementException_whenUserNotFound() {
        // Given
        Long id = 1L;
        when(auditionRepository.findById(id)).thenReturn(Optional.of(audition));
        when(loginUserService.getLoginUser()).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            auditionService.update(id, auditionRequest);
        });

        assertEquals("User not found", exception.getMessage());
        verify(auditionRepository).findById(id);
        verify(mapper).map(auditionRequest, audition);
        verify(loginUserService).getLoginUser();
        verify(auditionRepository, never()).save(any());
    }

    @Test
    @Order(9)
    void testUpdate_shouldUpdateRoundStatus_whenAuditionStatusIsFinished() {
        // Given
        Long id = 1L;
        AuditionRequest updateRequest = new AuditionRequest(
            "Updated Audition",
            "Updated Description",
            AuditionStatus.FINISHED,
            "11:00",
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(8),
            true,
            Weeks.WEEK2
        );

        when(auditionRepository.findById(id)).thenReturn(Optional.of(audition));
        when(loginUserService.getLoginUser()).thenReturn(Optional.of(user));
        when(auditionRepository.save(any(Audition.class))).thenAnswer(invocation -> {
            Audition savedAudition = invocation.getArgument(0);
            return savedAudition;
        });

        // When
        AuditionResponse response = auditionService.update(id, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals(AuditionStatus.FINISHED, response.status());
        
        // Verify that all rounds are updated to FINISHED status
        audition.getRounds().forEach(round -> 
            assertEquals(RoundStatus.FINISHED, round.getStatus())
        );

        verify(auditionRepository).findById(id);
        verify(mapper).map(updateRequest, audition);
        verify(loginUserService).getLoginUser();
        verify(auditionRepository).save(audition);
    }

    @Test
    @Order(10)
    void testSearch_shouldReturnPaginationResponse_whenSearchCriteriaProvided() {
        // Given
        AuditionSearch search = new AuditionSearch("Test", true, AuditionStatus.VOTABLE);
        Pageable pageable = PageRequest.of(0, 10);

        List<Audition> auditions = List.of(audition);
        Page<Audition> auditionPage = new PageImpl<>(auditions, pageable, auditions.size());

        when(auditionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(auditionPage);

        // When
        PaginationResponse<AuditionResponse> response = auditionService.search(search, pageable);

        // Then
        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals(0, response.pageNumber());
        assertEquals(10, response.pageSize());
        assertEquals(0, response.offset());
        assertEquals(1, response.totalElements());
        assertEquals(1, response.totalPages());

        verify(auditionRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @Order(11)
    void testSearch_shouldReturnEmptyPaginationResponse_whenNoResultsFound() {
        // Given
        AuditionSearch search = new AuditionSearch("NonExistent", true, AuditionStatus.VOTABLE);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Audition> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(auditionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        // When
        PaginationResponse<AuditionResponse> response = auditionService.search(search, pageable);

        // Then
        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertEquals(0, response.pageNumber());
        assertEquals(10, response.pageSize());
        assertEquals(0, response.offset());
        assertEquals(0, response.totalElements());
        assertEquals(0, response.totalPages());

        verify(auditionRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @Order(12)
    void testSearch_shouldHandleNullSearchCriteria() {
        // Given
        AuditionSearch search = new AuditionSearch(null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        List<Audition> auditions = List.of(audition);
        Page<Audition> auditionPage = new PageImpl<>(auditions, pageable, auditions.size());

        when(auditionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(auditionPage);

        // When
        PaginationResponse<AuditionResponse> response = auditionService.search(search, pageable);

        // Then
        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals(0, response.pageNumber());
        assertEquals(10, response.pageSize());
        assertEquals(0, response.offset());
        assertEquals(1, response.totalElements());
        assertEquals(1, response.totalPages());

        verify(auditionRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @Order(13)
    void testDeleteById_shouldDeleteAuditionSuccessfully() {
        // Given
        Long id = 1L;
        when(auditionRepository.findById(id)).thenReturn(Optional.of(audition));
        doNothing().when(auditionRepository).delete(audition);

        // When
        Long deletedId = auditionService.deleteById(id);

        // Then
        assertEquals(id, deletedId);
        verify(auditionRepository).findById(id);
        verify(auditionRepository).delete(audition);
    }

    @Test
    @Order(14)
    void testDeleteById_shouldThrowNoSuchElementException_whenAuditionNotFound() {
        // Given
        Long id = 99L;
        when(auditionRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            auditionService.deleteById(id);
        });

        assertEquals("Audition not found", exception.getMessage());
        verify(auditionRepository).findById(id);
        verify(auditionRepository, never()).delete(any());
    }

    @Test
    @Order(15)
    void testAddRounds_shouldAddRoundsSuccessfully() {
        // Given
        Long auditionId = 1L;
        List<Long> roundIds = List.of(1L, 2L);
        RoundAddRequest request = new RoundAddRequest(roundIds);

        Round newRound1 = new Round();
        newRound1.setId(1L);
        newRound1.setName("New Round 1");
        newRound1.setStatus(RoundStatus.ACTIVE);
        newRound1.setAudition(null); // Not assigned to any audition

        Round newRound2 = new Round();
        newRound2.setId(2L);
        newRound2.setName("New Round 2");
        newRound2.setStatus(RoundStatus.ACTIVE);
        newRound2.setAudition(null); // Not assigned to any audition

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.of(audition));
        when(roundRepository.findById(1L)).thenReturn(Optional.of(newRound1));
        when(roundRepository.findById(2L)).thenReturn(Optional.of(newRound2));
        when(roundRepository.save(any(Round.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(auditionRepository.save(any(Audition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        auditionService.addRounds(auditionId, request);

        // Then
        verify(auditionRepository).findById(auditionId);
        verify(roundRepository).findById(1L);
        verify(roundRepository).findById(2L);
        verify(roundRepository, times(2)).save(any(Round.class));
        verify(auditionRepository).save(audition);

        // Verify that rounds are added to audition
        assertTrue(audition.getRounds().contains(newRound1));
        assertTrue(audition.getRounds().contains(newRound2));
        assertEquals(audition, newRound1.getAudition());
        assertEquals(audition, newRound2.getAudition());
    }

    @Test
    @Order(16)
    void testAddRounds_shouldThrowNoSuchElementException_whenAuditionNotFound() {
        // Given
        Long auditionId = 99L;
        RoundAddRequest request = new RoundAddRequest(List.of(1L));

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            auditionService.addRounds(auditionId, request);
        });

        assertEquals("Audition Not Found", exception.getMessage());
        verify(auditionRepository).findById(auditionId);
        verify(roundRepository, never()).findById(any());
        verify(roundRepository, never()).save(any());
        verify(auditionRepository, never()).save(any());
    }

    @Test
    @Order(17)
    void testAddRounds_shouldThrowNoSuchElementException_whenRoundNotFound() {
        // Given
        Long auditionId = 1L;
        List<Long> roundIds = List.of(99L);
        RoundAddRequest request = new RoundAddRequest(roundIds);

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.of(audition));
        when(roundRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            auditionService.addRounds(auditionId, request);
        });

        assertEquals("Round Not Found", exception.getMessage());
        verify(auditionRepository).findById(auditionId);
        verify(roundRepository).findById(99L);
        verify(roundRepository, never()).save(any());
        verify(auditionRepository, never()).save(any());
    }

    @Test
    @Order(18)
    void testAddRounds_shouldThrowIllegalArgumentException_whenRoundAlreadyAssigned() {
        // Given
        Long auditionId = 1L;
        List<Long> roundIds = List.of(1L);
        RoundAddRequest request = new RoundAddRequest(roundIds);

        Round assignedRound = new Round();
        assignedRound.setId(1L);
        assignedRound.setName("Assigned Round");
        assignedRound.setStatus(RoundStatus.ACTIVE);
        assignedRound.setAudition(new Audition()); // Already assigned to an audition

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.of(audition));
        when(roundRepository.findById(1L)).thenReturn(Optional.of(assignedRound));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            auditionService.addRounds(auditionId, request);
        });

        assertEquals("Round already assigned to an audition", exception.getMessage());
        verify(auditionRepository).findById(auditionId);
        verify(roundRepository).findById(1L);
        verify(roundRepository, never()).save(any());
        verify(auditionRepository, never()).save(any());
    }

    @Test
    @Order(19)
    void testAddRounds_shouldHandleEmptyRoundIdsList() {
        // Given
        Long auditionId = 1L;
        RoundAddRequest request = new RoundAddRequest(Collections.emptyList());

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.of(audition));

        // When
        auditionService.addRounds(auditionId, request);

        // Then
        verify(auditionRepository).findById(auditionId);
        verify(roundRepository, never()).findById(any());
        verify(roundRepository, never()).save(any());
        verify(auditionRepository).save(audition);
    }

    @Test
    @Order(20)
    void testDeleteByRoundId_shouldRemoveRoundsSuccessfully() {
        // Given
        Long auditionId = 1L;
        List<Long> roundIds = List.of(1L, 2L);
        RoundAddRequest request = new RoundAddRequest(roundIds);

        // Setup rounds in audition
        Round round1 = new Round();
        round1.setId(1L);
        round1.setName("Round 1");
        round1.setStatus(RoundStatus.ACTIVE);
        round1.setAudition(audition);

        Round round2 = new Round();
        round2.setId(2L);
        round2.setName("Round 2");
        round2.setStatus(RoundStatus.ACTIVE);
        round2.setAudition(audition);

        audition.getRounds().add(round1);
        audition.getRounds().add(round2);

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.of(audition));
        when(roundRepository.findById(1L)).thenReturn(Optional.of(round1));
        when(roundRepository.findById(2L)).thenReturn(Optional.of(round2));
        when(roundRepository.save(any(Round.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(auditionRepository.save(any(Audition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        auditionService.deleteByRoundId(auditionId, request);

        // Then
        verify(auditionRepository).findById(auditionId);
        verify(roundRepository).findById(1L);
        verify(roundRepository).findById(2L);
        verify(roundRepository, times(2)).save(any(Round.class));
        verify(auditionRepository).save(audition);

        // Verify that rounds are removed from audition
        assertFalse(audition.getRounds().contains(round1));
        assertFalse(audition.getRounds().contains(round2));
        assertNull(round1.getAudition());
        assertNull(round2.getAudition());
    }

    @Test
    @Order(21)
    void testDeleteByRoundId_shouldThrowNoSuchElementException_whenAuditionNotFound() {
        // Given
        Long auditionId = 99L;
        RoundAddRequest request = new RoundAddRequest(List.of(1L));

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            auditionService.deleteByRoundId(auditionId, request);
        });

        assertEquals("Audition Not Found", exception.getMessage());
        verify(auditionRepository).findById(auditionId);
        verify(roundRepository, never()).findById(any());
        verify(roundRepository, never()).save(any());
        verify(auditionRepository, never()).save(any());
    }

    @Test
    @Order(22)
    void testDeleteByRoundId_shouldThrowNoSuchElementException_whenRoundNotFound() {
        // Given
        Long auditionId = 1L;
        List<Long> roundIds = List.of(99L);
        RoundAddRequest request = new RoundAddRequest(roundIds);

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.of(audition));
        when(roundRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            auditionService.deleteByRoundId(auditionId, request);
        });

        assertEquals("Round Not Found", exception.getMessage());
        verify(auditionRepository).findById(auditionId);
        verify(roundRepository).findById(99L);
        verify(roundRepository, never()).save(any());
        verify(auditionRepository, never()).save(any());
    }

    @Test
    @Order(23)
    void testDeleteByRoundId_shouldHandleEmptyRoundIdsList() {
        // Given
        Long auditionId = 1L;
        RoundAddRequest request = new RoundAddRequest(Collections.emptyList());

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.of(audition));

        // When
        auditionService.deleteByRoundId(auditionId, request);

        // Then
        verify(auditionRepository).findById(auditionId);
        verify(roundRepository, never()).findById(any());
        verify(roundRepository, never()).save(any());
        verify(auditionRepository).save(audition);
    }

    @Test
    @Order(24)
    void testDeleteByRoundId_shouldHandleNonExistentRoundIdsInAudition() {
        // Given
        Long auditionId = 1L;
        List<Long> roundIds = List.of(99L);
        RoundAddRequest request = new RoundAddRequest(roundIds);

        Round round = new Round();
        round.setId(99L);
        round.setName("Non-existent Round");
        round.setStatus(RoundStatus.ACTIVE);
        round.setAudition(null); // Not in the audition

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.of(audition));
        when(roundRepository.findById(99L)).thenReturn(Optional.of(round));
        when(roundRepository.save(any(Round.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(auditionRepository.save(any(Audition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        auditionService.deleteByRoundId(auditionId, request);

        // Then
        verify(auditionRepository).findById(auditionId);
        verify(roundRepository).findById(99L);
        verify(roundRepository).save(round);
        verify(auditionRepository).save(audition);
        
        // Verify that the round's audition is set to null
        assertNull(round.getAudition());
    }

    @Test
    @Order(25)
    void testGetRounds_shouldReturnSortedRounds_whenAuditionHasRounds() {
        // Given
        Long auditionId = 1L;
        
        // Create rounds with different start dates
        Round round1 = new Round();
        round1.setId(1L);
        round1.setName("Round 1");
        round1.setDescription("First Round");
        round1.setVotingStartDate(LocalDate.now());
        round1.setVotingEndDate(LocalDate.now().plusDays(2));
        round1.setStartDate(LocalDate.now().plusDays(1));
        round1.setEndDate(LocalDate.now().plusDays(3));
        round1.setCreatedAt(LocalDateTime.now());
        round1.setUpdatedAt(LocalDateTime.now());
        round1.setCreatedByUser(user);
        round1.setUpdatedByUser(user);
        round1.setIsActive(true);
        round1.setStatus(RoundStatus.ACTIVE);
        round1.setAudition(audition);

        Round round2 = new Round();
        round2.setId(2L);
        round2.setName("Round 2");
        round2.setDescription("Second Round");
        round2.setVotingStartDate(LocalDate.now().plusDays(3));
        round2.setVotingEndDate(LocalDate.now().plusDays(5));
        round2.setStartDate(LocalDate.now().plusDays(4));
        round2.setEndDate(LocalDate.now().plusDays(6));
        round2.setCreatedAt(LocalDateTime.now());
        round2.setUpdatedAt(LocalDateTime.now());
        round2.setCreatedByUser(user);
        round2.setUpdatedByUser(user);
        round2.setIsActive(true);
        round2.setStatus(RoundStatus.ACTIVE);
        round2.setAudition(audition);

        // Add rounds to audition in reverse order to test sorting
        audition.getRounds().clear();
        audition.getRounds().add(round2);
        audition.getRounds().add(round1);

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.of(audition));

        // When
        List<RoundResponse> responses = auditionService.getRounds(auditionId);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        
        // Verify sorting by start date
        assertTrue(responses.get(0).startDate().compareTo(responses.get(1).startDate()) < 0);
        
        // Verify first round details
        RoundResponse firstResponse = responses.get(0);
        assertEquals(round1.getId(), firstResponse.id());
        assertEquals(round1.getName(), firstResponse.name());
        assertEquals(round1.getDescription(), firstResponse.description());
        assertEquals(round1.getVotingStartDate().toString(), firstResponse.votingStartDate());
        assertEquals(round1.getVotingEndDate().toString(), firstResponse.votingEndDate());
        assertEquals(round1.getStartDate().toString(), firstResponse.startDate());
        assertEquals(round1.getEndDate().toString(), firstResponse.endDate());
        assertEquals(round1.getCreatedAt().toString(), firstResponse.createdAt());
        assertEquals(round1.getUpdatedAt().toString(), firstResponse.updatedAt());
        assertEquals(round1.getCreatedByUser().getName(), firstResponse.createdByUser());
        assertEquals(round1.getUpdatedByUser().getName(), firstResponse.updatedByUser());
        assertEquals(round1.getIsActive(), firstResponse.isActive());
        assertEquals(round1.getStatus().name(), firstResponse.status());

        verify(auditionRepository).findById(auditionId);
    }

    @Test
    @Order(26)
    void testGetRounds_shouldReturnEmptyList_whenAuditionHasNoRounds() {
        // Given
        Long auditionId = 1L;
        audition.getRounds().clear(); // Ensure audition has no rounds
        when(auditionRepository.findById(auditionId)).thenReturn(Optional.of(audition));

        // When
        List<RoundResponse> responses = auditionService.getRounds(auditionId);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(auditionRepository).findById(auditionId);
    }

    @Test
    @Order(27)
    void testGetRounds_shouldThrowNoSuchElementException_whenAuditionNotFound() {
        // Given
        Long auditionId = 99L;
        when(auditionRepository.findById(auditionId)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            auditionService.getRounds(auditionId);
        });

        assertEquals("Audition Not Found", exception.getMessage());
        verify(auditionRepository).findById(auditionId);
    }

    @Test
    @Order(28)
    void testGetRounds_shouldHandleNullFieldsInRound() {
        // Given
        Long auditionId = 1L;
        
        Round round = new Round();
        round.setId(1L);
        round.setName("Round with null fields");
        round.setDescription(null);
        round.setVotingStartDate(null);
        round.setVotingEndDate(null);
        round.setStartDate(LocalDate.now());
        round.setEndDate(null);
        round.setCreatedAt(LocalDateTime.now());
        round.setUpdatedAt(null);
        round.setCreatedByUser(user);
        round.setUpdatedByUser(null);
        round.setIsActive(true);
        round.setStatus(RoundStatus.ACTIVE);
        round.setAudition(audition);

        audition.getRounds().clear();
        audition.getRounds().add(round);

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.of(audition));

        // When
        List<RoundResponse> responses = auditionService.getRounds(auditionId);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        
        RoundResponse response = responses.get(0);
        assertEquals(round.getId(), response.id());
        assertEquals(round.getName(), response.name());
        assertNull(response.description());
        assertNull(response.votingStartDate());
        assertNull(response.votingEndDate());
        assertEquals(round.getStartDate().toString(), response.startDate());
        assertNull(response.endDate());
        assertEquals(round.getCreatedAt().toString(), response.createdAt());
        assertNull(response.updatedAt());
        assertEquals(round.getCreatedByUser().getName(), response.createdByUser());
        assertNull(response.updatedByUser());
        assertEquals(round.getIsActive(), response.isActive());
        assertEquals(round.getStatus().name(), response.status());

        verify(auditionRepository).findById(auditionId);
    }

    @Test
    @Order(29)
    void testGetRounds_shouldHandleNullStartDateInSorting() {
        // Given
        Long auditionId = 1L;
        
        // Create rounds with one having null start date
        Round round1 = new Round();
        round1.setId(1L);
        round1.setName("Round 1");
        round1.setStartDate(LocalDate.now());
        round1.setCreatedByUser(user);
        round1.setIsActive(true);
        round1.setStatus(RoundStatus.ACTIVE);
        round1.setAudition(audition);

        Round round2 = new Round();
        round2.setId(2L);
        round2.setName("Round 2");
        round2.setStartDate(null); // Null start date
        round2.setCreatedByUser(user);
        round2.setIsActive(true);
        round2.setStatus(RoundStatus.ACTIVE);
        round2.setAudition(audition);

        audition.getRounds().clear();
        audition.getRounds().add(round1);
        audition.getRounds().add(round2);

        when(auditionRepository.findById(auditionId)).thenReturn(Optional.of(audition));

        // When
        List<RoundResponse> responses = auditionService.getRounds(auditionId);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        
        // Verify that the round with start date comes first
        assertNotNull(responses.get(0).startDate());
        assertNull(responses.get(1).startDate());
        
        // Verify round details
        RoundResponse firstResponse = responses.get(0);
        assertEquals(round1.getId(), firstResponse.id());
        assertEquals(round1.getName(), firstResponse.name());
        assertEquals(round1.getStartDate().toString(), firstResponse.startDate());

        RoundResponse secondResponse = responses.get(1);
        assertEquals(round2.getId(), secondResponse.id());
        assertEquals(round2.getName(), secondResponse.name());
        assertNull(secondResponse.startDate());

        verify(auditionRepository).findById(auditionId);
    }
} 