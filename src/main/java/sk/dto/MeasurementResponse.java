package sk.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import sk.enums.MeasurementType;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MeasurementResponse {
    private Map<MeasurementType, List<Measurement>> measurements;

    public MeasurementResponse() {
        measurements = null;
    }

}
