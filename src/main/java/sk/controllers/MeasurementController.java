package sk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;
import sk.dto.Measurement;
import sk.dto.MeasurementRequest;
import sk.enums.MeasurementType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/measurements")
public class MeasurementController {

    private final sk.service.MeasurementService measurementService;

    @Autowired
    public MeasurementController(sk.service.MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @PostMapping("/process")
    public CollectionModel<EntityModel<Measurement>> processMeasurements(@RequestBody MeasurementRequest measurementRequest) {
        Map<MeasurementType, List<Measurement>> processedMeasurements = measurementService.processMeasurements(measurementRequest);

        List<EntityModel<Measurement>> measurementResources = processedMeasurements.values().stream()
                .flatMap(List::stream)
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(measurementResources);
    }

    private EntityModel<Measurement> toModel(Measurement measurement) {
        EntityModel<Measurement> model = EntityModel.of(measurement);

        model.add(WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(MeasurementController.class).getMeasurementByTypeAndTimestamp(
                                measurement.getMeasurementType(),
                                measurement.getTimestamp().toString()))
                .withSelfRel());

        model.add(WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(MeasurementController.class).processMeasurements(null))
                .withRel("process"));

        return model;
    }

    @GetMapping("/{type}/{timestamp}")
    public EntityModel<Measurement> getMeasurementByTypeAndTimestamp(
            @PathVariable MeasurementType type,
            @PathVariable String timestamp) {
        Measurement measurement = findMeasurementByTypeAndTimestamp(type, timestamp);
        return toModel(measurement);
    }

    private Measurement findMeasurementByTypeAndTimestamp(MeasurementType type, String timestamp) {
        return null;
    }
}
