package sk.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sk.dto.Measurement;
import sk.dto.TreadTask;
import sk.enums.MeasurementType;
import sk.interfaces.MeasurementProcessor;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j // it can be replaced to field and can be tested in unit tests
@Service
//@Primary
public class MultiThreadMeasurementProcessor extends CommonProcessor implements MeasurementProcessor {
    private final ExecutorService executorService;

    public MultiThreadMeasurementProcessor(final int maxThreadPoolSize) {
        log.info(maxThreadPoolSize + " threads can be started");
        this.executorService = Executors.newFixedThreadPool(maxThreadPoolSize);
    }

    public MultiThreadMeasurementProcessor() {
        int maxThreadPoolSize = MeasurementType.values().length;
        log.info(maxThreadPoolSize + " threads can be started");
        this.executorService = Executors.newFixedThreadPool(maxThreadPoolSize);
    }

    @Override
    public Map<MeasurementType, List<Measurement>> process(final List<Measurement> unsampledMeasurements) {
        return process(Instant.now(), unsampledMeasurements);
    }

    @Override
    public Map<MeasurementType, List<Measurement>> process(final Instant startOfSampling, final List<Measurement> unsampledMeasurements) {
        if (canNotProcess(unsampledMeasurements))
            return Collections.emptyMap();

        final List<Measurement> updatedMeasurements = filterAndUpdateMeasurements(unsampledMeasurements, startOfSampling);
        final Map<MeasurementType, List<Measurement>> groupedMeasurements = groupAndProcessMeasurements(updatedMeasurements);

        final List<Callable<TreadTask>> callables = createCallables(groupedMeasurements);
        final List<Future<TreadTask>> futures = callables.stream()
                .map(executorService::submit)
                .collect(Collectors.toList());

        final Map<MeasurementType, List<Measurement>> result = getMeasurementTypeListMap(futures);

        shutdown();

        return result;
    }

    private List<Callable<TreadTask>> createCallables(final Map<MeasurementType, List<Measurement>> groupedMeasurements) {
        return groupedMeasurements
                .entrySet()
                .stream()
                .peek(e -> log.info("A data type will be processed: " + e.getKey() + " in quantity " + e.getValue().size()))
                .map(TreadTask::from)
                .map(this::createTask)
                .toList();
    }

    private Callable<TreadTask> createTask(TreadTask treadTask) {
        return () -> {
            List<Measurement> processedMeasurements = processMeasurements(treadTask.measurements());
            return new TreadTask(treadTask.measurementType(), processedMeasurements);
        };
    }

    private List<Measurement> processMeasurements(final List<Measurement> measurements) {
        return measurements
                .stream()
                .collect(Collectors.groupingBy(Measurement::getNearestFiveMinutesForward))
                .values()
                .stream()
                .map(findLastMeasurement())
                .sorted()
                .collect(Collectors.toList());
    }

    private Map<MeasurementType, List<Measurement>> getMeasurementTypeListMap(final List<Future<TreadTask>> futures) {
        return futures.stream()
                .map(this::getResponseFromFuture)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(treadTask -> log.info("Data type processed: " + treadTask.measurementType() + " in quantity " + treadTask.measurements().size()))
                .collect
                        (Collectors.toMap
                                (
                                        TreadTask::measurementType,
                                        TreadTask::measurements,
                                        (oldValue, newValue) -> newValue,
                                        () -> new EnumMap<>(MeasurementType.class)
                                )
                        );
    }

    private Optional<TreadTask> getResponseFromFuture(Future<TreadTask> future) {
        try {
            return Optional.ofNullable(future.get());
        } catch (InterruptedException | ExecutionException e) {
            log.info("Something has gone wrong. with Future<TreadTask>: " + e.getMessage());
            return Optional.empty();
        }
    }

    private void shutdown() {
        executorService.shutdown();
        log.info("The Executor was trying to be shut down.");
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            log.info("Executor has been closed.");
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            log.info("Executor has been closed.");
        }
    }

}
