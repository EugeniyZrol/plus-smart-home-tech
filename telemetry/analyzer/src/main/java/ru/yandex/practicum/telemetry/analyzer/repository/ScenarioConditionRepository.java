package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioConditionId;
import java.util.List;

@Repository
public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, ScenarioConditionId> {

    @Query("SELECT sc FROM ScenarioCondition sc WHERE sc.scenario.id = :scenarioId")
    List<ScenarioCondition> findByScenarioId(@Param("scenarioId") Long scenarioId);

    @Modifying
    @Query("DELETE FROM ScenarioCondition sc WHERE sc.scenario.id = :scenarioId")
    void deleteByScenarioId(@Param("scenarioId") Long scenarioId);

    @Modifying
    @Query("DELETE FROM ScenarioCondition sc WHERE sc.sensor.id = :sensorId")
    void deleteBySensorId(@Param("sensorId") String sensorId);
}