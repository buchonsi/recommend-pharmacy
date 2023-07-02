package com.example.recophar.api.service

import com.example.recophar.AbstractIntegrationContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired

class KakaoCategorySearchServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private KakaoCategorySearchService kakaoCategorySearchService

    def "정상적인 주소를 입력했을 경우, 정상적으로 위도, 경도로 변환 된다."() {
        given:
        boolean actualResult = false

        when:
        def searchResult = kakaoCategorySearchService.requestPharmacyCategorySearch(latitude, longitude, radius)

        then:
        if(searchResult == null) {
            actualResult = false
            println searchResult.getDocumentDtoList().size()
        }
        else {
            actualResult = searchResult.getDocumentDtoList().size() > 0
            println searchResult.getDocumentDtoList().toString()
        }

        actualResult == expectResult

        where:
        latitude | longitude | radius | expectResult
        37.5960650456809 | 127.037033003036 | 10.0 | true
        37.604980  | 127.30192  | 0.0 | false
    }
}
