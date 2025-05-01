package com.example.BookingSystem.features.classes.domain.service;

import com.example.BookingSystem.common.dto.PaginationDTO;
import com.example.BookingSystem.common.enums.Country;
import com.example.BookingSystem.features.classes.domain.request.ClassRequest;
import com.example.BookingSystem.features.classes.domain.response.ClassesResponse;
import org.springframework.data.domain.Pageable;

public interface ClassesService {

    ClassesResponse createClass(ClassRequest classRequest, Country country);

    PaginationDTO<ClassesResponse> getAllClasses(Pageable pageable);

}
