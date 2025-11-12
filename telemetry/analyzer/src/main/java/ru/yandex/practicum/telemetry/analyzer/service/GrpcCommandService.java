package ru.yandex.practicum.telemetry.analyzer.service;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.telemetry.analyzer.config.GrpcProperties;
import ru.yandex.practicum.telemetry.analyzer.config.ScenarioProperties;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class GrpcCommandService {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    private final ScenarioProperties scenarioProperties;
    private final GrpcProperties grpcProperties;
    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool();

    public void sendDeviceAction(String hubId, String scenarioName, DeviceActionAvro actionAvro) {
        asyncExecutor.submit(() -> sendDeviceActionWithRetry(hubId, scenarioName, actionAvro, 0));
    }

    private void sendDeviceActionWithRetry(String hubId, String scenarioName, DeviceActionAvro actionAvro, int attempt) {
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

            hubRouterClient.handleDeviceAction(request);

        } catch (StatusRuntimeException e) {
            if (grpcProperties.getRetryStatusCodes().contains(e.getStatus().getCode().name())) {
                if (attempt < scenarioProperties.getMaxRetryAttempts()) {
                    try {
                        Thread.sleep(grpcProperties.getRetryDelayMs() * (attempt + 1));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    sendDeviceActionWithRetry(hubId, scenarioName, actionAvro, attempt + 1);
                }
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
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