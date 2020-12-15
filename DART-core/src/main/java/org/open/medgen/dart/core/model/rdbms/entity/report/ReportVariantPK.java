package org.open.medgen.dart.core.model.rdbms.entity.report;

import org.open.medgen.dart.core.model.rdbms.entity.annotations.VariantSample;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ReportVariantPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "report_id", referencedColumnName = "report_id")
    @ManyToOne(optional = false)
    private Report report;

    @JoinColumn(name = "variant_sample", referencedColumnName = "variant_sample_id")
    @ManyToOne(optional = false)
    private VariantSample variantSample;

    public ReportVariantPK() {
    }

    public ReportVariantPK(Report report, VariantSample variantSample) {
        this.report = report;
        this.variantSample = variantSample;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public VariantSample getVariantSample() {
        return variantSample;
    }

    public void setVariantSample(VariantSample variantSample) {
        this.variantSample = variantSample;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportVariantPK that = (ReportVariantPK) o;
        return Objects.equals(report, that.report) &&
                Objects.equals(variantSample, that.variantSample);
    }

    @Override
    public int hashCode() {
        return Objects.hash(report, variantSample);
    }
}
