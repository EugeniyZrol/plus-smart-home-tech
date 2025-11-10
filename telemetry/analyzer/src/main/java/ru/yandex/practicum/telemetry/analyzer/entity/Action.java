package ru.yandex.practicum.telemetry.analyzer.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "actions")
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ActionTypeAvro type;

    @Column(name = "value")
    private Integer value;

    public Action(ActionTypeAvro type, Integer value) {
        this.type = type;
        this.value = value;
    }
}