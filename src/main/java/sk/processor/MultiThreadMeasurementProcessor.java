package sk.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sk.dto.Measurement;
import sk.dto.ThreadTask;
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

        final Map<MeasurementType, List<Measurement>> measurementsByType = updatedMeasurements
                .parallelStream()
                .collect(Collectors.groupingBy(Measurement::getMeasurementType));

        final List<Future<ThreadTask>> futures = measurementsByType.values()
                .parallelStream()
                .map(ThreadTask::new)
                .map(task -> executorService.submit(() -> task.processMeasurements(findLastMeasurement())))
                .collect(Collectors.toList());

        final Map<MeasurementType, List<Measurement>> result = getMeasurementTypeListMap(futures);

        shutdown();

        return result;
    }

    private Map<MeasurementType, List<Measurement>> getMeasurementTypeListMap(final List<Future<ThreadTask>> futures) {
        Map<MeasurementType, List<Measurement>> resultMap = new EnumMap<>(MeasurementType.class);

        for (Future<ThreadTask> future : futures) {
            getResponseFromFuture(future)
                    .map(ThreadTask::measurements)
                    .ifPresent(measurements -> {
                        for (Measurement measurement : measurements) {
                            resultMap
                                    .computeIfAbsent(measurement.getMeasurementType(), k -> new ArrayList<>())
                                    .add(measurement);
                        }
                    });
        }

        return resultMap;
    }

    private Optional<ThreadTask> getResponseFromFuture(Future<ThreadTask> future) {
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
