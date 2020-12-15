package org.open.medgen.dart.core.model.rdbms.entity.annotations;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "condition_dictionary")
public class ConditionDictionary {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "db_id")
    private Integer dbId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "dictionary_name")
    private String dictionaryName;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "condition_id")
    private String conditionId;
    
    @Size(min = 1, max = 200)
    @Column(name = "condition_label")
    private String conditionLabel;
    
    @Column(name = "condition_description")
    private String conditionDescription;

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }

    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
    }

    public String getConditionId() {
        return conditionId;
    }

    public void setConditionId(String dictionaryId) {
        this.conditionId = dictionaryId;
    }

    public String getConditionLabel() {
        return conditionLabel;
    }

    public void setConditionLabel(String dictionaryLabel) {
        this.conditionLabel = dictionaryLabel;
    }

    public String getConditionDescription() {
        return conditionDescription;
    }

    public void setConditionDescription(String dictionaryDescription) {
        this.conditionDescription = dictionaryDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConditionDictionary that = (ConditionDictionary) o;
        return dbId.equals(that.dbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbId);
    }
}
