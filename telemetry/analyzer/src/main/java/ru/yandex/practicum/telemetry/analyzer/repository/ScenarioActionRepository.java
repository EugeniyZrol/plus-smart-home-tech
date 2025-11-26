package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioAction;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioActionId;
import java.util.List;

@Repository
public interface ScenarioActionRepository extends JpaRepository<ScenarioAction, ScenarioActionId> {

    List<ScenarioAction> findByScenarioId(Long scenarioId);

    @Query("SELECT sa FROM ScenarioAction sa JOIN FETCH sa.action WHERE sa.scenarioId = :scenarioId")
    List<ScenarioAction> findByScenarioIdWithAction(@Param("scenarioId") Long scenarioId);

    void deleteByScenarioId(Long scenarioId);
    void deleteBySensorId(String sensorId);
}