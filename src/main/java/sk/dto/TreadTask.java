package sk.dto;

import sk.enums.MeasurementType;

import java.util.List;
import java.util.Map;

public record TreadTask(MeasurementType measurementType, List<Measurement> measurements) {

    public static TreadTask from(Map.Entry<MeasurementType, List<Measurement>> entry) {
        return new TreadTask(entry.getKey(), entry.getValue());
    }

}
