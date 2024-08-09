package sk.processor;

import org.springframework.stereotype.Service;
import sk.dto.Measurement;
import sk.dto.TreadTask;
import sk.enums.MeasurementType;
import sk.interfaces.MeasurementProcessor;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
//@Primary
public class MultiThreadMeasurementProcessor extends CommonProcessor implements MeasurementProcessor {
    private final ExecutorService executorService;

    public MultiThreadMeasurementProcessor(final int threadPoolSize) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public MultiThreadMeasurementProcessor() {
        this.executorService = Executors.newFixedThreadPool(MeasurementType.values().length);
    }

    private static boolean canProcess(final List<Measurement> unsampledMeasurements) {
        return unsampledMeasurements == null || unsampledMeasurements.isEmpty();
    }

    @Override
    public Map<MeasurementType, List<Measurement>> process(final List<Measurement> unsampledMeasurements) {
        return process(Instant.now(), unsampledMeasurements);
    }

    @Override
    public Map<MeasurementType, List<Measurement>> process(final Instant startOfSampling, final List<Measurement> unsampledMeasurements) {
        if (canProcess(unsampledMeasurements))
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
        return groupedMeasurements.entrySet()
                .stream()
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
            return Optional.empty();
        }
    }

    private void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
