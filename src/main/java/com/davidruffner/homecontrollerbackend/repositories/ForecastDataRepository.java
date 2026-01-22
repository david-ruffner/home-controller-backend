package com.davidruffner.homecontrollerbackend.repositories;

import com.davidruffner.homecontrollerbackend.dtos.ForecastData;
import com.davidruffner.homecontrollerbackend.entities.ForecastDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ForecastDataRepository extends JpaRepository<ForecastDataEntity, String> {
    @Query("""
    SELECT f
    FROM ForecastDataEntity f
    WHERE f.generatedTime >= :start
      AND f.generatedTime <= :end
""")
    List<ForecastDataEntity> findInRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

//    @Query("""
//        delete ForecastDataEntity f
//        where f.type = "today"
//    """)
//    void deleteTodayRecord
}
