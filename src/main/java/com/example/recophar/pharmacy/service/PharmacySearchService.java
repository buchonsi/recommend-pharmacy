package com.example.recophar.pharmacy.service;

import com.example.recophar.pharmacy.cache.PharmacyRedisTemplateService;
import com.example.recophar.pharmacy.dto.PharmacyDto;
import com.example.recophar.pharmacy.entity.Pharmacy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacySearchService {

    private final PharmacyRepositoryService pharmacyRepositoryService;
    private final PharmacyRedisTemplateService pharmacyRedisTemplateService;

    public List<PharmacyDto> searchPharmacyDtoList() {
        //redis
        List<PharmacyDto> pharmacyDtoList = pharmacyRedisTemplateService.findAll();
        if(!pharmacyDtoList.isEmpty()) {
            log.info("[PharmacySearchService searchPharmacyDtoList] redis findAll success!");
            return pharmacyDtoList;
        }

        //db
        return pharmacyRepositoryService.findAll()
                .stream()
                .map(entity -> convertToPharmacyDto(entity))
                .collect(Collectors.toList());
    }

    private PharmacyDto convertToPharmacyDto(Pharmacy pharmacy) {
        return PharmacyDto.builder()
                .id(pharmacy.getId())
                .pharmacyAddress(pharmacy.getPharmacyAddress())
                .pharmacyName(pharmacy.getPharmacyName())
                .latitude(pharmacy.getLatitude())
                .longitude(pharmacy.getLongitude())
                .build();
    }

}
