package sk.processor;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import sk.dto.Measurement;
import sk.enums.MeasurementType;
import sk.interfaces.MeasurementProcessor;

import java.time.Instant;
import java.util.List;
import java.util.Map;


@Service
@Primary
 public class SingleThreadMeasurementProcessor extends CommonProcessor implements MeasurementProcessor {

    @Override
    public Map<MeasurementType, List<Measurement>> process(List<Measurement> unsampledMeasurements) {
        return process(Instant.now(), unsampledMeasurements);
    }

    @Override
    public Map<MeasurementType, List<Measurement>> process(Instant startOfSampling, List<Measurement> unsampledMeasurements) {
        List<Measurement> updatedMeasurements = filterAndUpdateMeasurements(unsampledMeasurements, startOfSampling);
        return groupAndProcessMeasurements(updatedMeasurements);
    }
}
