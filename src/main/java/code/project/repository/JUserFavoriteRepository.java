package code.project.repository;

import code.project.domain.Hospital;
import code.project.domain.Pharmacy;
import code.project.domain.JUserFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JUserFavoriteRepository extends JpaRepository<JUserFavorite, Long> {

    // 내가 찜한 병원 ID 리스트
    @Query("""
        SELECT f.hospital.hospitalId
        FROM JUserFavorite f
        WHERE f.user.userId = :userId
          AND f.type = code.project.domain.FacilityType.HOSPITAL
    """)
    List<Long> findHospitalIdsByUserId(@Param("userId") Long userId);

    // 내가 찜한 약국 ID 리스트
    @Query("""
        SELECT f.pharmacy.pharmacyId
        FROM JUserFavorite f
        WHERE f.user.userId = :userId
          AND f.type = code.project.domain.FacilityType.PHARMACY
    """)
    List<Long> findPharmacyIdsByUserId(@Param("userId") Long userId);

    // 즐겨찾기 존재 여부 확인 (병원, 약국 각각 따로)
    boolean existsByUser_UserIdAndHospital_HospitalId(Long userId, Long hospitalId);
    boolean existsByUser_UserIdAndPharmacy_PharmacyId(Long userId, Long pharmacyId);

    // 즐겨찾기 삭제
    void deleteByUser_UserIdAndHospital_HospitalId(Long userId, Long hospitalId);
    void deleteByUser_UserIdAndPharmacy_PharmacyId(Long userId, Long pharmacyId);

    // 병원 즐겨찾기 (페이지네이션)
    @EntityGraph(attributePaths = {
            "hospital",
            "hospital.facility",
            "hospital.facility.businessHours"
    })
    @Query("""
        SELECT h FROM JUserFavorite f
        JOIN f.hospital h
        WHERE f.user.username = :username
    """)
    Page<Hospital> findHospitalsPageByUsername(
            @Param("username") String username,
            Pageable pageable
    );

    // 약국 즐겨찾기 (페이지네이션)
    @EntityGraph(attributePaths = {
            "pharmacy",
            "pharmacy.facility",
            "pharmacy.facility.businessHours"
    })
    @Query("""
        SELECT p FROM JUserFavorite f
        JOIN f.pharmacy p
        WHERE f.user.username = :username
    """)
    Page<Pharmacy> findPharmaciesPageByUsername(
            @Param("username") String username,
            Pageable pageable
    );
}
