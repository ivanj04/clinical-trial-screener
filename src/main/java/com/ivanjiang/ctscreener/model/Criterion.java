package com.ivanjiang.ctscreener.model;

import com.ivanjiang.ctscreener.model.enums.CriterionType;
import com.ivanjiang.ctscreener.model.enums.Operator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Criterion {

    private String criterionId;
    private CriterionType type;
    private Operator operator;
    private String value;
    private String description;
}
