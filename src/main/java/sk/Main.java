package sk;

import sk.dto.Measurement;
import sk.enums.MeasurementType;
import sk.processor.SamplerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        List<Measurement> measurements = Arrays.asList(
                new Measurement(MeasurementType.TEMP, Instant.parse("2017-01-03T10:04:45Z"), BigDecimal.valueOf(35.79)),
                new Measurement(MeasurementType.SPO2, Instant.parse("2017-01-03T10:01:18Z"), BigDecimal.valueOf(98.78)),
                new Measurement(MeasurementType.TEMP, Instant.parse("2017-01-03T10:09:07Z"), BigDecimal.valueOf(35.01)),
                new Measurement(MeasurementType.SPO2, Instant.parse("2017-01-03T10:03:34Z"), BigDecimal.valueOf(96.49)),
                new Measurement(MeasurementType.TEMP, Instant.parse("2017-01-03T10:02:01Z"), BigDecimal.valueOf(35.82)),
                new Measurement(MeasurementType.SPO2, Instant.parse("2017-01-03T10:05:00Z"), BigDecimal.valueOf(97.17)),
                new Measurement(MeasurementType.SPO2, Instant.parse("2017-01-03T10:05:01Z"), BigDecimal.valueOf(95.08))
        );

        Instant startOfSampling = Instant.parse("2017-01-03T10:00:00Z");

        Map<MeasurementType, List<Measurement>> sampledData = SamplerFactory
                .createSampler(measurements.size())
                .process(startOfSampling, measurements);

        sampledData.forEach((type, list) -> {
            System.out.println("Measurement type: " + type);
            list.forEach(System.out::println);
        });
    }

}
