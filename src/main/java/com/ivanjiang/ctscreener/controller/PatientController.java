package com.ivanjiang.ctscreener.controller;

import com.ivanjiang.ctscreener.dto.CreatePatientRequest;
import com.ivanjiang.ctscreener.model.PatientProfile;
import com.ivanjiang.ctscreener.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Register and manage patient profiles")
@SecurityRequirement(name = "bearerAuth")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @Operation(summary = "Register a new patient profile")
    public ResponseEntity<PatientProfile> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(request));
    }

    @GetMapping("/{patientId}")
    @Operation(summary = "Get patient profile by patient ID")
    public ResponseEntity<PatientProfile> getPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(patientService.getPatientByPatientId(patientId));
    }

    @PutMapping("/{patientId}")
    @Operation(summary = "Update patient profile")
    public ResponseEntity<PatientProfile> updatePatient(@PathVariable String patientId,
                                                         @Valid @RequestBody CreatePatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(patientId, request));
    }
}
