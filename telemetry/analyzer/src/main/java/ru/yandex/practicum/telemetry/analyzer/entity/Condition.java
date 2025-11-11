package ru.yandex.practicum.telemetry.analyzer.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "conditions")
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ConditionTypeAvro type;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false)
    private ConditionOperationAvro operation;

    @Column(name = "value")
    private Integer value;

    public Condition(ConditionTypeAvro type, ConditionOperationAvro operation, Integer value) {
        this.type = type;
        this.operation = operation;
        this.value = value;
    }
}