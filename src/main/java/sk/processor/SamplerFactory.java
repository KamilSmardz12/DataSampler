package sk.processor;

import org.springframework.stereotype.Service;
import sk.interfaces.MeasurementProcessor;

@Service
public class SamplerFactory {

    private static final int THREAD_THRESHOLD = 1000;
    private static final int RECORDS_PER_THREAD = 1000;

    public static MeasurementProcessor createSampler(int measurementsSize) {
        if (measurementsSize > THREAD_THRESHOLD) {
            int threads = calculateThreadCount(measurementsSize);
            return new MultiThreadMeasurementProcessor(threads);
        } else {
            return new SingleThreadMeasurementProcessor();
        }
    }

    private static int calculateThreadCount(int dataSize) {
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        // 1000 records per 1 thread
        final int threads = Math.max(1, dataSize / RECORDS_PER_THREAD);
        return Math.min(threads, availableProcessors * 2);
    }
}
