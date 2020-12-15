package org.open.medgen.dart.core.model.rdbms.entity.report;

import org.open.medgen.dart.core.controller.annotation.RelatedSamplesAnnotator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "related_sample")
public class RelatedSample implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private RelatedSamplePk id;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex")
    private RelatedSamplesAnnotator.RelatedSampleSex sex = RelatedSamplesAnnotator.RelatedSampleSex.UNK;

    @Column(name = "affected")
    private boolean affected = false;

    public RelatedSamplePk getId() {
        return id;
    }

    public void setId(RelatedSamplePk id) {
        this.id = id;
    }

    public RelatedSamplesAnnotator.RelatedSampleSex getSex() {
        return sex;
    }

    public void setSex(RelatedSamplesAnnotator.RelatedSampleSex sex) {
        this.sex = sex;
    }

    public boolean isAffected() {
        return affected;
    }

    public void setAffected(boolean affected) {
        this.affected = affected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelatedSample that = (RelatedSample) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
