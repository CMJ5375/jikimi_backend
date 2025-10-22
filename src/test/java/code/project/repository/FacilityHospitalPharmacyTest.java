package code.project.repository;

import code.project.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
public class FacilityHospitalPharmacyTest {

//    @Autowired
//    private FacilityRepository facilityRepository;
//
//    @Autowired
//    private HospitalRepository hospitalRepository;
//
//    @Autowired
//    private PharmacyRepository pharmacyRepository;
//
//    @Test
//    @DisplayName("Facility + Hospital 연결 테스트 (BigDecimal 좌표 사용)")
//    void saveHospital() {
//        Facility facility = Facility.builder()
//                .name("성남중앙병원")
//                .type(FacilityType.HOSPITAL)
//                .phone("031-111-1111")
//                .address("성남시 수정구 산성대로 267")
//                .latitude(new BigDecimal("37.4455000"))
//                .longitude(new BigDecimal("127.1311000"))
//                .regionCode("SEONGNAM_SUJEONG")
//                .orgType("종합병원")
//                .build();
//
//        Facility savedFacility = facilityRepository.save(facility);
//
//        Hospital hospital = Hospital.builder()
//                .facility(savedFacility)
//                .hospitalName(savedFacility.getName())
//                .businessHour("평일 09:00~18:00")
//                .hasEmergency(true)
//                .build();
//
//        Hospital savedHospital = hospitalRepository.save(hospital);
//
//        assertThat(savedHospital.getFacility().getFacilityId())
//                .isEqualTo(savedFacility.getFacilityId());
//    }
//
//    @Test
//    @DisplayName("Facility + Pharmacy 연결 테스트 (BigDecimal 좌표 사용)")
//    void savePharmacy() {
//        Facility facility = Facility.builder()
//                .name("성남드림약국")
//                .type(FacilityType.PHARMACY)
//                .phone("031-222-2222")
//                .address("성남시 수정구 신흥동 245")
//                .latitude(new BigDecimal("37.4462000"))
//                .longitude(new BigDecimal("127.1308000"))
//                .regionCode("SEONGNAM_SUJEONG")
//                .orgType("일반약국")
//                .build();
//
//        Facility savedFacility = facilityRepository.save(facility);
//
//        Pharmacy pharmacy = Pharmacy.builder()
//                .facility(savedFacility)
//                .pharmacyName(savedFacility.getName())
////                .businessHour("평일 09:00~20:00")
//                .build();
//
//        Pharmacy savedPharmacy = pharmacyRepository.save(pharmacy);
//
//        assertThat(savedPharmacy.getFacility().getFacilityId())
//                .isEqualTo(savedFacility.getFacilityId());
//    }
}
