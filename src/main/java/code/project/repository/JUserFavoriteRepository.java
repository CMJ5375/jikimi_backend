package code.project.repository;

import code.project.domain.FacilityType;
import code.project.domain.JUserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JUserFavoriteRepository extends JpaRepository<JUserFavorite, Long> {

    // 내가 찜한 병원/약국 ID만 뽑아오기
    @Query("select f.hospital.hospitalId from JUserFavorite f " +
            "where f.user.userId = :userId and f.type = code.project.domain.FacilityType.HOSPITAL")
    List<Long> findHospitalIdsByUserId(Long userId);

    @Query("select f.pharmacy.pharmacyId from JUserFavorite f " +
            "where f.user.userId = :userId and f.type = code.project.domain.FacilityType.PHARMACY")
    List<Long> findPharmacyIdsByUserId(Long userId);

    // 존재 여부 (중복 방지)
    boolean existsByUser_UserIdAndHospital_HospitalId(Long userId, Long hospitalId);
    boolean existsByUser_UserIdAndPharmacy_PharmacyId(Long userId, Long pharmacyId);

    // 삭제
    void deleteByUser_UserIdAndHospital_HospitalId(Long userId, Long hospitalId);
    void deleteByUser_UserIdAndPharmacy_PharmacyId(Long userId, Long pharmacyId);
}
