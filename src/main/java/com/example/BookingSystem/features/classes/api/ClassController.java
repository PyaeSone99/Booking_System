package com.example.BookingSystem.features.classes.api;

import com.example.BookingSystem.annotation.APIVersion;
import com.example.BookingSystem.common.dto.ApiResponseDTO;
import com.example.BookingSystem.common.dto.PaginationDTO;
import com.example.BookingSystem.common.enums.Country;
import com.example.BookingSystem.features.classes.domain.request.ClassRequest;
import com.example.BookingSystem.features.classes.domain.response.ClassesResponse;
import com.example.BookingSystem.features.classes.domain.service.ClassesService;
import com.example.BookingSystem.features.packages.domain.request.PackageRequest;
import com.example.BookingSystem.features.packages.domain.response.PackageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@APIVersion
@Tag(name = "Classes", description = "This is all Classes Api")
public class ClassController {

    @Autowired
    private ClassesService classesService;

    @PostMapping("/classes")
    @Operation(summary = "Create Classes")
    public ResponseEntity<ApiResponseDTO<ClassesResponse>> createClass(
            @Valid @RequestBody ClassRequest classRequest,
            @RequestParam Country country
    ){
        return ResponseEntity.ok(new ApiResponseDTO<>(classesService.createClass(classRequest,country)));
    }


    @GetMapping("/classes/classes-list")
    @Operation(summary = "Get All Classes")
    public ResponseEntity<ApiResponseDTO<PaginationDTO<ClassesResponse>>> getAllClasses(
            Pageable pageable
    ){
        return ResponseEntity.ok(new ApiResponseDTO<>(classesService.getAllClasses(pageable)));
    }


}
