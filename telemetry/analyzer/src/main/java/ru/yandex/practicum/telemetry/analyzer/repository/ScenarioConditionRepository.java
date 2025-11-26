package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioConditionId;
import java.util.List;

@Repository
public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, ScenarioConditionId> {

    List<ScenarioCondition> findByScenarioId(Long scenarioId);

    @Query("SELECT sc FROM ScenarioCondition sc JOIN FETCH sc.condition WHERE sc.scenarioId = :scenarioId")
    List<ScenarioCondition> findByScenarioIdWithCondition(@Param("scenarioId") Long scenarioId);

    void deleteByScenarioId(Long scenarioId);
    void deleteBySensorId(String sensorId);
}