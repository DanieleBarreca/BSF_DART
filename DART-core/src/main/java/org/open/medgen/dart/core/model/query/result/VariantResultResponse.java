/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.query.result;

import org.open.medgen.dart.core.model.cache.QueryStatus;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 *
 * @author dbarreca
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VariantResultResponse {
    
    private final QueryStatus status;
    private final QueryErrorResponse error;
    private final List<VariantModel> variants;
    private final Integer queryCount;

    public VariantResultResponse(QueryStatus status, Integer queryCount, Throwable error, List<VariantModel> variants) {
        this.status = status;
        
        switch (status) {
            case FINISHED:
                this.queryCount = queryCount;
                this.variants = variants;
                this.error = null;
                break;
            case ERROR:
                this.queryCount = null;
                this.variants = null;
                this.error = new QueryErrorResponse(error);
                break;
            default:
                this.queryCount = null;
                this.variants = null;
                this.error = null;
                break;
        }
                 
    }

    public QueryStatus getStatus() {
        return status;
    }

    public QueryErrorResponse getError() {
        return error;
    }

    public List<VariantModel> getVariants() {
        return variants;
    }

    public Integer getQueryCount() {
        return queryCount;
    }
        
}
