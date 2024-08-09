package sk.processor;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import sk.dto.Measurement;
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

    @Override
    public Map<MeasurementType, List<Measurement>> process(List<Measurement> unsampledMeasurements) {
        return process(Instant.now(), unsampledMeasurements);
    }

    @Override
    public Map<MeasurementType, List<Measurement>> process(Instant startOfSampling, List<Measurement> unsampledMeasurements) {
        if (unsampledMeasurements == null || unsampledMeasurements.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Measurement> updatedMeasurements = filterAndUpdateMeasurements(unsampledMeasurements, startOfSampling);
        Map<MeasurementType, List<Measurement>> groupedMeasurements = groupAndProcessMeasurements(updatedMeasurements);

        List<Callable<Map.Entry<MeasurementType, List<Measurement>>>> callables = groupedMeasurements.entrySet()
                .stream()
                .map(this::createTask)
                .toList();

        List<Future<Map.Entry<MeasurementType, List<Measurement>>>> futures = new ArrayList<>();

        callables.forEach(c -> futures.add(executorService.submit(c)));

        Map<MeasurementType, List<Measurement>> result = getMeasurementTypeListMap(futures);

        shutdown();

        return result;
    }

    private Callable<Map.Entry<MeasurementType, List<Measurement>>> createTask(Map.Entry<MeasurementType, List<Measurement>> entry) {
        return () -> {
            List<Measurement> processedMeasurements = processMeasurements(entry.getValue());
            return new AbstractMap.SimpleEntry<>(entry.getKey(), processedMeasurements);
        };
    }

    private List<Measurement> processMeasurements(List<Measurement> measurements) {
        return measurements
                .stream()
                .collect(Collectors.groupingBy(Measurement::getNearestFiveMinutesForward))
                .values()
                .stream()
                .map(findLastMeasurement())
                .sorted()
                .collect(Collectors.toList());
    }

    private Map<MeasurementType, List<Measurement>> getMeasurementTypeListMap(List<Future<Map.Entry<MeasurementType, List<Measurement>>>> futures) {
        Map<MeasurementType, List<Measurement>> result = new EnumMap<>(MeasurementType.class);

        futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(entry -> result.put(entry.getKey(), entry.getValue()));

        return result;
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
