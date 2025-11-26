package ru.yandex.practicum.telemetry.analyzer.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "scenario_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ScenarioConditionId.class)
public class ScenarioCondition {

    @Id
    @Column(name = "scenario_id")
    private Long scenarioId;

    @Id
    @Column(name = "sensor_id")
    private String sensorId;

    @Id
    @Column(name = "condition_id")
    private Long conditionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", insertable = false, updatable = false)
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", insertable = false, updatable = false)
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_id", insertable = false, updatable = false)
    private Condition condition;

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
        this.scenarioId = scenario != null ? scenario.getId() : null;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
        this.sensorId = sensor != null ? sensor.getId() : null;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
        this.conditionId = condition != null ? condition.getId() : null;
    }
}