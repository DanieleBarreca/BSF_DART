package org.open.medgen.dart.core.model.rdbms.entity.report;

import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RelatedSamplePk implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    @OneToOne
    @JoinColumn(name = "sample_id")
    private VCFSample sample;

    public RelatedSamplePk() {

    }

    public RelatedSamplePk(Report report, VCFSample sample) {
        this.report = report;
        this.sample = sample;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public VCFSample getSample() {
        return sample;
    }

    public void setSample(VCFSample sample) {
        this.sample = sample;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelatedSamplePk pk = (RelatedSamplePk) o;
        return Objects.equals(report, pk.report) &&
                Objects.equals(sample, pk.sample);
    }

    @Override
    public int hashCode() {
        return Objects.hash(report, sample);
    }
}