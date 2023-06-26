package com.example.recophar.api.service

import spock.lang.Specification

import java.nio.charset.StandardCharsets

class KakaoUriBuilderServiceTest extends Specification {

    private KakaoUriBuilderService kakaoUriBuilderService

//    def setupSpec() { // run before the first feature method }

    def setup() {
        // run before every feature method
        kakaoUriBuilderService = new KakaoUriBuilderService()
    }

//    def cleanup() { // run after every feature method }
//    def cleanupSpec() { // run after the last feature method }

    def "buildUriByAdressSearch - 한글 파라미터의 경우 정상적으로 인코딩"() {
        given:
        String address = "서울 성북구"
        def charset = StandardCharsets.UTF_8

        when:
        def uri  = kakaoUriBuilderService.buildUriByAddressSearch(address)
        def decodeResult = URLDecoder.decode(uri.toString(), charset)

        then:
        decodeResult == "https://dapi.kakao.com/v2/local/search/address.json?query=서울 성북구"
    }
}
