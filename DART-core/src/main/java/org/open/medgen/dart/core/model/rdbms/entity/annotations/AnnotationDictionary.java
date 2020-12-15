package org.open.medgen.dart.core.model.rdbms.entity.annotations;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "annotation_dictionary")
public class AnnotationDictionary {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "db_id")
    private Integer dbId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "annotation_type")
    private String annotationType;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "annotation_label")
    private String annotationLabel;

    @Basic(optional = false)
    @NotNull
    @Column(name = "rank")
    private Integer rank;

    @Column(name = "annotation_description")
    private String annotationDescription;

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    public String getAnnotationLabel() {
        return annotationLabel;
    }

    public void setAnnotationLabel(String annotationLabel) {
        this.annotationLabel = annotationLabel;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getAnnotationDescription() {
        return annotationDescription;
    }

    public void setAnnotationDescription(String annotationDescription) {
        this.annotationDescription = annotationDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationDictionary that = (AnnotationDictionary) o;
        return dbId.equals(that.dbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbId);
    }
}
