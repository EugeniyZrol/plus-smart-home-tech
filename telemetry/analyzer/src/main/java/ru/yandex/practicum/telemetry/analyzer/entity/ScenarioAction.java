package ru.yandex.practicum.telemetry.analyzer.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "scenario_actions")
@IdClass(ScenarioActionId.class)
public class ScenarioAction {

    @Id
    @Column(name = "scenario_id")
    private Long scenarioId;

    @Id
    @Column(name = "sensor_id")
    private String sensorId;

    @Id
    @Column(name = "action_id")
    private Long actionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", insertable = false, updatable = false)
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", insertable = false, updatable = false)
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", insertable = false, updatable = false)
    private Action action;

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
        this.scenarioId = scenario != null ? scenario.getId() : null;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
        this.sensorId = sensor != null ? sensor.getId() : null;
    }

    public void setAction(Action action) {
        this.action = action;
        this.actionId = action != null ? action.getId() : null;
    }
}