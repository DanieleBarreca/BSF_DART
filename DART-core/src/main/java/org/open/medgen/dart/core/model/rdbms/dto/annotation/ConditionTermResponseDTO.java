package org.open.medgen.dart.core.model.rdbms.dto.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ConditionTermResponseDTO {
    
    private final static String TOTAL_COUNT="TOTAL_COUNT";
    private final static String RESULTS="RESULTS";
    
    @JsonProperty(RESULTS)
    private final List<ConditionTermDTO> results;

    @JsonProperty(TOTAL_COUNT)
    private final Long totalCount;

    public ConditionTermResponseDTO(List<ConditionTermDTO> results, Long totalCount) {
        this.results = results;
        this.totalCount = totalCount;
    }

    public List<ConditionTermDTO> getResults() {
        return results;
    }

    public Long getTotalCount() {
        return totalCount;
    }
}
