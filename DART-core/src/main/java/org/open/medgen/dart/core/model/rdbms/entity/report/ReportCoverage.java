package org.open.medgen.dart.core.model.rdbms.entity.report;

import org.open.medgen.dart.core.model.mongo.aggregations.CoverageResult;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "report_coverage")
public class ReportCoverage implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name="coverage_id")
    private Integer coverageId;

    @JoinColumn(name = "report_id", referencedColumnName = "report_id")
    @ManyToOne(optional = false)
    private Report report;

    @Lob
    @Column(name="coverage_data",length=16777216)
    @Basic(fetch = FetchType.LAZY)
    private CoverageResult coverageData;

    public Integer getCoverageId() {
        return coverageId;
    }

    public void setCoverageId(Integer coverageId) {
        this.coverageId = coverageId;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public CoverageResult getCoverageData() {
        return coverageData;
    }

    public void setCoverageData(CoverageResult coverageData) {
        this.coverageData = coverageData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportCoverage that = (ReportCoverage) o;
        return Objects.equals(coverageId, that.coverageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coverageId);
    }
}
