package ru.yandex.practicum.telemetry.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.telemetry.collector.converter.ProtobufToDtoConverter;
import ru.yandex.practicum.telemetry.collector.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.telemetry.collector.handler.hub.HubEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final List<SensorEventHandler> sensorEventHandlers;
    private final List<HubEventHandler> hubEventHandlers;
    private final ProtobufToDtoConverter converter;

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            SensorEventProto.PayloadCase payloadCase = request.getPayloadCase();
            log.info("Получено gRPC событие от датчика: {}, тип: {}", request.getId(), payloadCase);

            SensorEventHandler handler = sensorEventHandlers.stream()
                    .filter(h -> h.canHandle(payloadCase))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Не найден обработчик для события типа: " + payloadCase));

            var sensorEvent = converter.convertToDto(request);
            handler.handle(sensorEvent);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            log.info("Событие от датчика {} успешно обработано", request.getId());

        } catch (Exception e) {
            log.error("Ошибка обработки gRPC события от датчика {}", request.getId(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Ошибка обработки события: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            HubEventProto.PayloadCase payloadCase = request.getPayloadCase();
            log.info("Получено gRPC событие от хаба: {}, тип: {}", request.getHubId(), payloadCase);

            HubEventHandler handler = hubEventHandlers.stream()
                    .filter(h -> h.canHandle(payloadCase))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Не найден обработчик для события типа: " + payloadCase));

            var hubEvent = converter.convertToDto(request);
            handler.handle(hubEvent);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            log.info("Событие от хаба {} успешно обработано", request.getHubId());

        } catch (Exception e) {
            log.error("Ошибка обработки gRPC события от хаба {}", request.getHubId(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Ошибка обработки события: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}