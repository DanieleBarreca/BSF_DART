/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.query.result;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author dbarreca
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FilterResultResponse {
    
    private final CountResultResponse countResult;
    private final VariantResultResponse variantResult;
    private final CoverageResultResponse coverageResult;
            
    public FilterResultResponse(CountResultResponse countResult, VariantResultResponse variantResult, CoverageResultResponse coverageResult) {
        this.countResult = countResult;
        this.variantResult = variantResult;
        this.coverageResult = coverageResult;
    }
    

    public CountResultResponse getCountResult() {
        return countResult;
    }

    public VariantResultResponse getVariantResult() {
        return variantResult;
    }

    public CoverageResultResponse getCoverageResult() {
        return coverageResult;
    }
}
