package org.open.medgen.dart.core.model.rdbms.entity.report;

import org.open.medgen.dart.core.model.mongo.variant.VariantModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "report_variant")
public class ReportVariant implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private ReportVariantPK reporVariantPK;

    @Lob
    @Column(name="variant_data",length=16777216)
    @Basic(fetch = FetchType.LAZY)
    private VariantModel variantData;

    public ReportVariantPK getReporVariantPK() {
        return reporVariantPK;
    }

    public void setReporVariantPK(ReportVariantPK reporVariantPK) {
        this.reporVariantPK = reporVariantPK;
    }

    public VariantModel getVariantData() {
        return variantData;
    }

    public void setVariantData(VariantModel variantData) {
        this.variantData = variantData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportVariant that = (ReportVariant) o;
        return Objects.equals(reporVariantPK, that.reporVariantPK);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reporVariantPK);
    }
}
