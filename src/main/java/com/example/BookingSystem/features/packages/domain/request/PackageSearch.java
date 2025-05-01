package com.example.BookingSystem.features.packages.domain.request;

import com.example.BookingSystem.common.enums.Country;
import com.example.BookingSystem.features.packages.domain.entity.Packages;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public record PackageSearch (
        String name
) {
    public static Specification<Packages> search (PackageSearch search){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search.name() != null && !search.name().isEmpty()){
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + search.name() +"%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
