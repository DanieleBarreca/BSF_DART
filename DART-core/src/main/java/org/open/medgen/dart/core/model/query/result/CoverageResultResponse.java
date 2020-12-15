/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.query.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.open.medgen.dart.core.model.cache.CoverageStatus;
import org.open.medgen.dart.core.model.cache.QueryStatus;
import org.open.medgen.dart.core.model.mongo.aggregations.CoverageResult;
import org.open.medgen.dart.core.model.mongo.coverage.CoverageEntry;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;

import java.util.List;

/**
 *
 * @author dbarreca
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CoverageResultResponse {

    private final CoverageStatus status;
    private final QueryErrorResponse error;
    private final List<CoverageResult> entries;
    private final Integer count;
    private final Integer filteredCount;
    private final Boolean hasCoverage;

    public CoverageResultResponse(CoverageStatus status, Integer coverageCount, Throwable error, List<CoverageResult> variants) {
        this(status, coverageCount, null, error, variants);
    }

    public CoverageResultResponse(CoverageStatus status, Integer coverageCount,  Integer filteredCount, Throwable error, List<CoverageResult> variants) {
        this.status = status;
        
        switch (status) {
            case NOT_PRESENT:
                this.hasCoverage = false;
                this.count = null;
                this.filteredCount = null;
                this.entries = null;
                this.error = null;
                break;
            case FINISHED:
                this.hasCoverage = true;
                this.count = coverageCount;
                if (filteredCount==null){
                    this.filteredCount = this.count;
                }else {
                    this.filteredCount = filteredCount;
                }
                this.entries = variants;
                this.error = null;
                break;
            case ERROR:
                this.hasCoverage = true;
                this.count = null;
                this.filteredCount = null;
                this.entries = null;
                this.error = new QueryErrorResponse(error);
                break;
            default: //RUNNING
                this.hasCoverage = true;
                this.count = null;
                this.filteredCount = null;
                this.entries = null;
                this.error = null;
                break;
        }
                 
    }

    public CoverageStatus getStatus() {
        return status;
    }

    public QueryErrorResponse getError() {
        return error;
    }

    public List<CoverageResult> getEntries() {
        return entries;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getFilteredCount() {
        return filteredCount;
    }
}
