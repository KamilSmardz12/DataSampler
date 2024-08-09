package sk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.dto.MeasurementRequest;
import sk.dto.MeasurementResponse;
import sk.dto.Response;
import sk.enums.MeasurementType;
import sk.services.MeasurementService;

@RestController
@RequestMapping("/v1/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;

    @Autowired
    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @GetMapping("/process")
    public ResponseEntity<EntityModel<Response>> processMeasurements(@RequestBody MeasurementRequest measurementRequest) {
        Response processedMeasurements = measurementService.processMeasurements(measurementRequest);

        EntityModel<Response> responseModel = EntityModel.of(processedMeasurements);
        responseModel.add
                (
                        WebMvcLinkBuilder.linkTo
                                        (
                                                WebMvcLinkBuilder
                                                        .methodOn(MeasurementController.class)
                                                        .processMeasurements(measurementRequest)
                                        )
                                .withSelfRel()
                );

        responseModel.add
                (
                        WebMvcLinkBuilder.linkTo
                                        (
                                                WebMvcLinkBuilder
                                                        .methodOn(MeasurementController.class)
                                                        .getMeasurementByTypeAndTimestamp(null, null)
                                        )
                                .withRel("getMeasurementByTypeAndTimestamp"));

        return ResponseEntity.ok(responseModel);
    }

    @GetMapping("/{type}/{timestamp}")
    public ResponseEntity<EntityModel<MeasurementResponse>> getMeasurementByTypeAndTimestamp(
            @PathVariable MeasurementType type,
            @PathVariable String timestamp) {
        MeasurementResponse measurement = findMeasurementByTypeAndTimestamp(type, timestamp);
        if (measurement != null) {
            return ResponseEntity.ok(toModel(measurement));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private EntityModel<MeasurementResponse> toModel(MeasurementResponse measurement) {
        EntityModel<MeasurementResponse> model = EntityModel.of(measurement);

        model.add(WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(MeasurementController.class).getMeasurementByTypeAndTimestamp(
                                measurement.measurementType(),
                                measurement.nearestFiveMinutesForward().toString()))
                .withSelfRel());

        model.add(WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(MeasurementController.class).processMeasurements(null))
                .withRel("process"));

        return model;
    }

    // Null because that was not part of the task.
    // I added this to show which way I was going and why I needed HATEOAS.
    private MeasurementResponse findMeasurementByTypeAndTimestamp(MeasurementType type, String timestamp) {
        return null;
    }
}
