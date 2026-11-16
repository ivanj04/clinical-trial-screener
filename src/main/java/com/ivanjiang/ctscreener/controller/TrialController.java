package com.ivanjiang.ctscreener.controller;

import com.ivanjiang.ctscreener.dto.CreateTrialRequest;
import com.ivanjiang.ctscreener.model.ClinicalTrial;
import com.ivanjiang.ctscreener.service.TrialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trials")
@RequiredArgsConstructor
@Tag(name = "Clinical Trials", description = "Manage clinical trial protocols and eligibility criteria")
@SecurityRequirement(name = "bearerAuth")
public class TrialController {

    private final TrialService trialService;

    @PostMapping
    @Operation(summary = "Create a new clinical trial with inclusion/exclusion criteria")
    public ResponseEntity<ClinicalTrial> createTrial(@Valid @RequestBody CreateTrialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trialService.createTrial(request));
    }

    @GetMapping
    @Operation(summary = "List all trials (pageable)")
    public ResponseEntity<Page<ClinicalTrial>> listTrials(Pageable pageable) {
        return ResponseEntity.ok(trialService.listTrials(pageable));
    }

    @GetMapping("/{trialId}")
    @Operation(summary = "Get a trial by its protocol ID")
    public ResponseEntity<ClinicalTrial> getTrial(@PathVariable String trialId) {
        return ResponseEntity.ok(trialService.getTrialByTrialId(trialId));
    }

    @PutMapping("/{trialId}")
    @Operation(summary = "Update trial metadata and criteria")
    public ResponseEntity<ClinicalTrial> updateTrial(@PathVariable String trialId,
                                                      @Valid @RequestBody CreateTrialRequest request) {
        return ResponseEntity.ok(trialService.updateTrial(trialId, request));
    }
}
