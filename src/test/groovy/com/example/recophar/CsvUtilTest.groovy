package com.example.recophar

import com.example.recophar.pharmacy.dto.PharmacyDto
import spock.lang.Specification

class CsvUtilTest extends Specification {

    def "CSV Read Test"() {
        given:
        println "test 시작"

        when:
        def list = CsvUtil.convertToPharmacyDtoList()

        then:
        for (PharmacyDto pharmacyDto : list) {
//            println pharmacyDto.toString()
        }
    }
}
