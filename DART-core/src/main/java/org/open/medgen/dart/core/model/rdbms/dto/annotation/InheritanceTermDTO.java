package org.open.medgen.dart.core.model.rdbms.dto.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.ConditionDictionary;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.InheritanceDictionary;

public class InheritanceTermDTO {

    public final static String REF_ID = "REF_ID";
    public final static String LABEL = "LABEL";
    
    @JsonProperty(REF_ID)
    private Integer dbId;

    @JsonProperty(LABEL)
    private String label;

    public InheritanceTermDTO() {
    }

    public InheritanceTermDTO(InheritanceDictionary inheritanceDictionary) {
        this.dbId = inheritanceDictionary.getDbId();
        this.label = inheritanceDictionary.getInheritanceLabel();
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
}
