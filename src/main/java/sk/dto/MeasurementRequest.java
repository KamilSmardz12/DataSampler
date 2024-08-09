package sk.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MeasurementRequest {

    List<Measurement> measurements;
    String startOfSampling;


    public MeasurementRequest() {
        measurements = null;
        startOfSampling = null;
    }

}
