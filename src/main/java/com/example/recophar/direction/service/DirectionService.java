package com.example.recophar.direction.service;

import com.example.recophar.api.dto.DocumentDto;
import com.example.recophar.api.service.KakaoCategorySearchService;
import com.example.recophar.direction.entity.Direction;
import com.example.recophar.direction.repository.DirectionRepository;
import com.example.recophar.pharmacy.dto.PharmacyDto;
import com.example.recophar.pharmacy.service.PharmacySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectionService {

    private static final int MAX_SEARCH_COUNT = 3;  //약국 최대 검색 갯수 : 3
    private static final double RADIUS_KM = 10.0;   // 변경 10km

    private final PharmacySearchService pharmacySearchService;
    private final DirectionRepository directionRepository;
    private final KakaoCategorySearchService kakaoCategorySearchService;

    @Transactional
    public List<Direction> saveAll(List<Direction> directionList) {
        if (CollectionUtils.isEmpty(directionList)) {
            return Collections.emptyList();
        }
        return directionRepository.saveAll(directionList);
    }

    public List<Direction> buildDirectionListByCategoryApi(DocumentDto inputDocumentDto) {

        if (Objects.isNull(inputDocumentDto)) {
            return Collections.emptyList();
        }

        return kakaoCategorySearchService
                .requestPharmacyCategorySearch(inputDocumentDto.getLatitude(), inputDocumentDto.getLongitude(), RADIUS_KM)
                .getDocumentDtoList()
                .stream().map(resultDocument ->
                        Direction.builder()
                                .inputAddress(inputDocumentDto.getAddressName())
                                .inputLatitude(inputDocumentDto.getLatitude())
                                .inputLongitude(inputDocumentDto.getLongitude())
                                .targetAddress(resultDocument.getAddressName())
                                .targetPharmacyName(resultDocument.getPlaceName())
                                .targetLatitude(resultDocument.getLatitude())
                                .targetLongitude(resultDocument.getLongitude())
                                .distance(resultDocument.getDistance() * 0.001)     //km단위
                                .build())
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
    }

    public List<Direction> buildDirectionList(DocumentDto documentDto) {

        if (Objects.isNull(documentDto)) {
            return Collections.emptyList();
        }

        //약국 데이터 조회
        List<PharmacyDto> pharmacyDtoList = pharmacySearchService.searchPharmacyDtoList();
        //거리 계산 알고리즘을 이용하여, 고객과 약국 사이의 거리를 계산하고 sort
        return pharmacyDtoList
                .stream().map(pharmacyDto ->
                        Direction.builder()
                                .inputAddress(documentDto.getAddressName())
                                .inputLatitude(documentDto.getLatitude())
                                .inputLongitude(documentDto.getLongitude())
                                .targetAddress(pharmacyDto.getPharmacyAddress())
                                .targetPharmacyName(pharmacyDto.getPharmacyName())
                                .targetLatitude(pharmacyDto.getLatitude())
                                .targetLongitude(pharmacyDto.getLongitude())
                                .distance(
                                        calculateDistance(documentDto.getLatitude(), documentDto.getLongitude(),
                                                pharmacyDto.getLatitude(), pharmacyDto.getLongitude())
                                )
                                .build())
                .filter(direction -> direction.getDistance() <= RADIUS_KM)
                .sorted(Comparator.comparing(direction -> direction.getDistance()))
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
    }

    //Haversine formula 알고리즘 사용
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);    //고객 위도
        lon1 = Math.toRadians(lon1);    //고객 경도
        lat2 = Math.toRadians(lat2);    //약국 위도
        lon2 = Math.toRadians(lon2);    //약국 경도

        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
