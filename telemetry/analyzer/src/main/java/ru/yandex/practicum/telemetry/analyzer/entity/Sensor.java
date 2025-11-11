package ru.yandex.practicum.telemetry.analyzer.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "sensors")
public class Sensor {

    @Id
    private String id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;

    public Sensor(String id, String hubId) {
        this.id = id;
        this.hubId = hubId;
    }
}