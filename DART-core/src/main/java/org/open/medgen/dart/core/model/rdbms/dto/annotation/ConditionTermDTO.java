package org.open.medgen.dart.core.model.rdbms.dto.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.ConditionDictionary;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.SampleCondition;

import java.io.Serializable;
import java.util.Objects;

public class ConditionTermDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public final static String REF_ID = "REF_ID";
    public final static String LABEL = "LABEL";
    public final static String DESCRIPTION = "DESCRIPTION";
    public final static String CODE = "CODE";
    
    @JsonProperty(REF_ID)
    private Integer dbId;

    @JsonProperty(LABEL)
    private String label;

    @JsonProperty(DESCRIPTION)
    private String description;
    
    @JsonProperty(CODE)
    private String code;

    public ConditionTermDTO() {
    }

    public ConditionTermDTO(ConditionDictionary conditionDictionary) {
        this.dbId = conditionDictionary.getDbId();
        this.label = conditionDictionary.getConditionLabel();
        this.description = conditionDictionary.getConditionDescription();
        this.code = conditionDictionary.getConditionId();
    }

    
    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConditionTermDTO that = (ConditionTermDTO) o;
        return Objects.equals(dbId, that.dbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbId);
    }
}
