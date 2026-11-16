package com.ivanjiang.ctscreener.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CreatePatientRequest {
    @NotBlank
    private String patientId;

    @Min(0)
    private int age;

    private String gender;

    private List<String> diagnoses = new ArrayList<>();
    private Map<String, Double> labValues = new HashMap<>();
    private List<String> currentMedications = new ArrayList<>();

    @Min(0)
    private int ecogScore;
}
