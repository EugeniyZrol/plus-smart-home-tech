package ru.yandex.practicum.telemetry.analyzer.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcCommandService {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void sendDeviceAction(String hubId, String scenarioName, DeviceActionAvro actionAvro) {
        try {
            DeviceActionProto actionProto = convertToProto(actionAvro);

            Instant now = Instant.now();
            Timestamp timestamp = Timestamp.newBuilder()
                    .setSeconds(now.getEpochSecond())
                    .setNanos(now.getNano())
                    .build();

            DeviceActionRequestProto request = DeviceActionRequestProto.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenarioName)
                    .setAction(actionProto)
                    .setTimestamp(timestamp)
                    .build();

            Empty response = hubRouterClient.handleDeviceAction(request);

            log.info("Команда успешно отправлена для хаба: {}, сценарий: {}, устройство: {}, тип: {}",
                    hubId, scenarioName, actionAvro.getSensorId(), actionAvro.getType());

        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                log.error("gRPC сервер недоступен для хаба: {}. Проверьте запущен ли hub-router на порту 59090", hubId);
            } else {
                log.error("Ошибка отправки команды для хаба: {}, устройство: {}, статус: {}",
                        hubId, actionAvro.getSensorId(), e.getStatus(), e);
            }
        } catch (Exception e) {
            log.error("Неожиданная ошибка при отправке команды для хаба: {}, устройство: {}",
                    hubId, actionAvro.getSensorId(), e);
        }
    }

    private DeviceActionProto convertToProto(DeviceActionAvro actionAvro) {
        DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                .setSensorId(actionAvro.getSensorId())
                .setType(convertActionType(actionAvro.getType()));

        if (actionAvro.getValue() != null) {
            builder.setValue(actionAvro.getValue());
        }

        return builder.build();
    }

    private ActionTypeProto convertActionType(ActionTypeAvro avroType) {
        return switch (avroType) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
            default -> throw new IllegalArgumentException("Неизвестный тип действия: " + avroType);
        };
    }
}