package org.open.medgen.dart.core.model.rdbms.entity.report;

import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.log.Job;
import org.open.medgen.dart.core.model.rdbms.entity.presets.PanelPreset;
import org.open.medgen.dart.core.model.rdbms.entity.presets.QueryPreset;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "report")
public class Report implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "report_id")
    private Integer reportId;

    @JoinColumn(name = "sample_id", referencedColumnName = "sample_id")
    @ManyToOne(optional = false)
    private VCFSample sample;

    @JoinColumn(name = "group_id", referencedColumnName = "group_id")
    @ManyToOne(optional = false)
    private UserGroup group;

    @JoinColumn(name = "user_from", referencedColumnName = "userId")
    @ManyToOne(optional = false)
    private User userFrom;

    @Basic(optional = false)
    @Column(name = "date_from")
    private Date dateFrom;

    @JoinColumn(name = "variants_job", referencedColumnName = "id")
    @ManyToOne()
    private Job variantsJob;

    @Basic
    @Column(name = "variants_loaded")
    private Long variantsLoaded = null;

    @JoinColumn(name = "coverage_job", referencedColumnName = "id")
    @ManyToOne()
    private Job coverageJob;
    
    @Basic(optional = false)
    @Column(name = "has_coverage")
    private Boolean hasCoverage =false;

    @Basic
    @Column(name = "coverage_loaded")
    private Long coverageLoaded = null;

    @JoinColumn(name = "query_id", referencedColumnName = "query_id")
    @ManyToOne(optional = false)
    private QueryPreset query;

    @JoinColumn(name = "panel_id", referencedColumnName = "panel_id")
    @ManyToOne(optional = false)
    private PanelPreset panel;

    @OneToMany(mappedBy="id.report")
    private Set<RelatedSample> relatedSamples;
    

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public VCFSample getSample() {
        return sample;
    }

    public void setSample(VCFSample sample) {
        this.sample = sample;
    }

    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Long getVariantsLoaded() {
        return variantsLoaded;
    }

    public void setVariantsLoaded(Long variantsLoaded) {
        this.variantsLoaded = variantsLoaded;
    }

    public Boolean getHasCoverage() {
        return hasCoverage;
    }

    public void setHasCoverage(Boolean hasCoverage) {
        this.hasCoverage = hasCoverage;
    }

    public Long getCoverageLoaded() {
        return coverageLoaded;
    }

    public void setCoverageLoaded(Long coverageLoaded) {
        this.coverageLoaded = coverageLoaded;
    }

    public QueryPreset getQuery() {
        return query;
    }

    public void setQuery(QueryPreset query) {
        this.query = query;
    }

    public PanelPreset getPanel() {
        return panel;
    }

    public void setPanel(PanelPreset panel) {
        this.panel = panel;
    }

    public Set<RelatedSample> getRelatedSamples() {
        return relatedSamples;
    }

    public void setRelatedSamples(Set<RelatedSample> relatedSamples) {
        this.relatedSamples = relatedSamples;
    }

    public Job getVariantsJob() {
        return variantsJob;
    }

    public void setVariantsJob(Job variantsJob) {
        this.variantsJob = variantsJob;
    }

    public Job getCoverageJob() {
        return coverageJob;
    }

    public void setCoverageJob(Job coverageJob) {
        this.coverageJob = coverageJob;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.reportId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Report other = (Report) obj;
        if (!Objects.equals(this.reportId, other.reportId)) {
            return false;
        }
        return true;
    }

}
