package code.project.repository;

import code.project.domain.Facility;
import code.project.domain.Hospital;
import code.project.domain.Pharmacy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Commit
class FacilityHospitalPharmacyTest {

    @Autowired FacilityRepository facilityRepository;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired PharmacyRepository pharmacyRepository;

    @Test
    @DisplayName("Facility 먼저 저장 후 Hospital @MapsId 로 연결")
    void hospitalMapsId() {
        Facility f = Facility.builder()
                .name("성남중앙병원")
                .type("HOSPITAL")
                .phone("02-123-4567")
                .address("성남시 어쩌구 저쩌구")
                .latitude(new BigDecimal("37.5665350"))
                .longitude(new BigDecimal("126.9779692"))
                .regionCode("성남")
                .build();

        Facility savedF = facilityRepository.save(f);

        Hospital h = Hospital.builder()
                .facility(savedF)     // @MapsId: Facility PK 공유
                .department("내과,외과")
                .institutionType("상급종합")
                .hasEmergency(true)
                .build();

        Hospital savedH = hospitalRepository.save(h);

        assertThat(savedH.getHospitalId()).isEqualTo(savedF.getFacilityId());
        assertThat(savedH.getFacility().getName()).isEqualTo("성남중앙병원");
    }

    @Test
    @DisplayName("Facility 먼저 저장 후 Pharmacy @MapsId 로 연결")
    void pharmacyMapsId() {
        Facility f = Facility.builder()
                .name("한빛약국")
                .type("PHARMACY")
                .address("성남시 어쩌구 저쩌구")
                .latitude(new BigDecimal("37.55"))
                .longitude(new BigDecimal("126.97"))
                .build();

        Facility savedF = facilityRepository.save(f);

        Pharmacy p = Pharmacy.builder()
                .facility(savedF)     // @MapsId
//                .businessHours("월-금 10:00~19:00\n토 09:00~16:00")
                .build();

        Pharmacy savedP = pharmacyRepository.save(p);

        assertThat(savedP.getPharmacyId()).isEqualTo(savedF.getFacilityId());
        assertThat(savedP.getFacility().getName()).isEqualTo("한빛약국");
    }
}
