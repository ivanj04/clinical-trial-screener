package com.ivanjiang.ctscreener.controller;

import com.ivanjiang.ctscreener.dto.BatchScreeningRequest;
import com.ivanjiang.ctscreener.dto.ScreeningRequest;
import com.ivanjiang.ctscreener.model.ScreeningResult;
import com.ivanjiang.ctscreener.service.ScreeningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screen")
@RequiredArgsConstructor
@Tag(name = "Screening", description = "Patient eligibility screening engine")
@SecurityRequirement(name = "bearerAuth")
public class ScreeningController {

    private final ScreeningService screeningService;

    @PostMapping
    @Operation(summary = "Screen a patient against a specific trial")
    public ResponseEntity<ScreeningResult> screen(@Valid @RequestBody ScreeningRequest request) {
        ScreeningResult result = screeningService.screen(request.getTrialId(), request.getPatientId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/batch")
    @Operation(summary = "Screen a patient against all currently RECRUITING trials")
    public ResponseEntity<List<ScreeningResult>> batchScreen(@Valid @RequestBody BatchScreeningRequest request) {
        return ResponseEntity.ok(screeningService.batchScreen(request.getPatientId()));
    }

    @GetMapping("/results/{patientId}")
    @Operation(summary = "Get all past screening results for a patient")
    public ResponseEntity<List<ScreeningResult>> getResults(@PathVariable String patientId) {
        return ResponseEntity.ok(screeningService.getResultsForPatient(patientId));
    }
}
