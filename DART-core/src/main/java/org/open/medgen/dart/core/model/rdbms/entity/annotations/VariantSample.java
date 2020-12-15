package org.open.medgen.dart.core.model.rdbms.entity.annotations;


import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "variant_sample")
public class VariantSample {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "variant_sample_id")
    private Integer dbId;

    @JoinColumn(name = "sample_id", referencedColumnName = "sample_id")
    @ManyToOne(optional = false)
    private VCFSample sample;

    @JoinColumn(name = "variant_id", referencedColumnName = "variant_id")
    @ManyToOne(optional = false)
    private Variant variant;


    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public VCFSample getSample() {
        return sample;
    }

    public void setSample(VCFSample sample) {
        this.sample = sample;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariantSample that = (VariantSample) o;
        return dbId.equals(that.dbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbId);
    }
}
