/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.query.result;

import org.open.medgen.dart.core.model.cache.CountStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author dbarreca
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CountResultResponse {
    
    private final CountStatus status;
    private final QueryErrorResponse error;
    private final Long totalCount;

    public CountResultResponse(CountStatus status, Long totalCount, Throwable error) {
        this.status = status;
        
        switch (status) {
            case FINISHED:
                this.totalCount = totalCount;
                this.error = null;
                break;
            case ERROR:
                this.totalCount = null;
                this.error = new QueryErrorResponse(error);
                break;
            default:
                this.totalCount = null;
                this.error = null;
                break;
        }
                 
    }

    public CountStatus getStatus() {
        return status;
    }

    public QueryErrorResponse getError() {
        return error;
    }

    public Long getTotalCount() {
        return totalCount;
    }
    
    
        
}
