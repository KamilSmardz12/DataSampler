package sk.dto;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import sk.enums.MeasurementType;

import java.util.List;
import java.util.Map;

@ToString
@EqualsAndHashCode(callSuper = false)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public record TreadTask(MeasurementType measurementType, List<Measurement> measurements) {

    public static TreadTask from(Map.Entry<MeasurementType, List<Measurement>> entry) {
        return new TreadTask(entry.getKey(), entry.getValue());
    }

}
