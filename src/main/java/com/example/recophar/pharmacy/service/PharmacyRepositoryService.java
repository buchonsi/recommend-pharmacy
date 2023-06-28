package com.example.recophar.pharmacy.service;

import com.example.recophar.pharmacy.entity.Pharmacy;
import com.example.recophar.pharmacy.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyRepositoryService {

    private final PharmacyRepository pharmacyRepository;
    private final TestService testService;

    // self invocation test
    // self invacation 해결방법
    // 1. 트랜젝션 위치를 외부에서 호출 하는 bar() 메소드로 이동
    //@Transactional
    public void bar(List<Pharmacy> pharmacyList) {
        log.info("bar CurrentTransactionName : " + TransactionSynchronizationManager.getCurrentTransactionName());
        //self invocation 발생!
        foo(pharmacyList);

        //개선 방법
        //2.객체의 책임을 최대한 분리하여 외부 호출 하도록 리팩토링
        //testService.foo(pharmacyList);
    }

    //self invocation test
    @Transactional
    public void foo(List<Pharmacy> pharmacyList) {
        log.info("foo CurrentTransactionName: " + TransactionSynchronizationManager.getCurrentTransactionName());
        pharmacyList.forEach(pharmacy -> {
            pharmacyRepository.save(pharmacy);
            throw new RuntimeException("error");    //transaction 동작을 보기위해 exception 발생시킴
        });
    }

    //read only test
    @Transactional(readOnly = true)
    public void startReadOnlyMethod(Long id) {
        pharmacyRepository.findById(id).ifPresent(pharmacy -> pharmacy.changePharmacyAddress("서울 특별시 광진구"));
    }

    @Transactional
    public void updateAddress(Long id, String address) {
        
        Pharmacy entity = pharmacyRepository.findById(id).orElse(null);

        if (Objects.isNull(entity)) {
            log.error("[PharmacyRepositoryService updateAddress] not found id: {}", id);
            return;
        }

        entity.changePharmacyAddress(address);
    }

    //for test
    public void updateAddressWithoutTransaction(Long id, String address) {
        Pharmacy entity = pharmacyRepository.findById(id).orElse(null);

        if (Objects.isNull(entity)) {
            log.error("[PharmacyRepositoryService updateAddress] not found id: {}", id);
            return;
        }

        entity.changePharmacyAddress(address);
    }

    @Transactional(readOnly = true)
    public List<Pharmacy> findAll() {
        return pharmacyRepository.findAll();
    }
}
