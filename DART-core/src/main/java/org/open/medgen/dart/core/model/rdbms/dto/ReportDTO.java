package org.open.medgen.dart.core.model.rdbms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.open.medgen.dart.core.model.query.FullQuery;
import org.open.medgen.dart.core.model.query.QueryFilter;
import org.open.medgen.dart.core.model.query.QueryPanel;
import org.open.medgen.dart.core.model.query.RelatedSampleInfo;
import org.open.medgen.dart.core.model.rdbms.dto.annotation.ConditionTermDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.model.rdbms.entity.report.RelatedSample;
import org.open.medgen.dart.core.model.rdbms.entity.report.Report;

import java.util.*;

public class ReportDTO {
    
    private final static String REF_ID= "REF_ID";
    private final static String CREATION_USER= "CREATION_USER";
    private final static String CREATION_DATE= "CREATION_DATE";
    private final static String VCF_FILE= "VCF_FILE";
    private final static String QUERY= "QUERY";
    private final static String HAS_COVERAGE= "HAS_COVERAGE";
    private final static String COVERAGE_ENTRIES= "COVERAGE_ENTRIES";
    private final static String VARIANTS= "VARIANTS";
    private static final String CONDITIONS = "CONDITIONS";

    @JsonProperty(REF_ID)
    private final Integer reportId;

    @JsonProperty(CREATION_USER)
    private final String user;

    @JsonProperty(CREATION_DATE)
    private final Date date;

    @JsonProperty(VCF_FILE)
    private final VCFFileDTO vcfFileDTO;

    @JsonProperty(QUERY)
    private final FullQuery fullQuery;

    @JsonProperty(HAS_COVERAGE)
    boolean hasCoverage;

    @JsonProperty(VARIANTS)
    private final Long totalVariants;

    @JsonProperty(COVERAGE_ENTRIES)
    private final Long totalCoverage;
    
    @JsonProperty(CONDITIONS)
    private final Set<ConditionTermDTO> conditions = new HashSet<>();

    public ReportDTO(Report report) {
        
        this.reportId = report.getReportId();
        this.user = report.getUserFrom().getLogin();
        this.date = report.getDateFrom();
        
        this.vcfFileDTO = new VCFFileDTO(report.getSample().getVcf(), false);
        this.fullQuery = new FullQuery();
        
        this.fullQuery.setVcfFileId(this.vcfFileDTO.getSqlId());
        this.fullQuery.setVcfFileName(this.vcfFileDTO.getVcfFileName());
        
        this.fullQuery.setSample(report.getSample().getSampleName());
        this.fullQuery.setSampleRefId(report.getSample().getSampleId());
        
        for (RelatedSample relatedSample: report.getRelatedSamples()) {
            RelatedSampleInfo relatedSampleInfo = new RelatedSampleInfo();
            relatedSampleInfo.setSample(relatedSample.getId().getSample().getSampleName());
            relatedSampleInfo.setSex(relatedSample.getSex());
            relatedSampleInfo.setAffected(relatedSample.isAffected());

            this.fullQuery.getRelatedSamples().add(relatedSampleInfo);
        }
        
        this.fullQuery.setPanel(new QueryPanel(report.getPanel()));
        this.fullQuery.setQueryFilter(new QueryFilter(report.getQuery()));
        
        this.hasCoverage = report.getHasCoverage();
        this.totalCoverage = report.getCoverageLoaded();
        
        this.totalVariants = report.getVariantsLoaded();
    }

    public Integer getReportId() {
        return reportId;
    }

    public String getUser() {
        return user;
    }

    public Date getDate() {
        return date;
    }

    public VCFFileDTO getVcfFileDTO() {
        return vcfFileDTO;
    }

    public FullQuery getFullQuery() {
        return fullQuery;
    }

    public boolean isHasCoverage() {
        return hasCoverage;
    }

    public Long getTotalVariants() {
        return totalVariants;
    }

    public Long getTotalCoverage() {
        return totalCoverage;
    }

    public Set<ConditionTermDTO> getConditions() {
        return conditions;
    }
    
    public void addConditions(Collection<ConditionTermDTO> conditions) {
        this.conditions.addAll(conditions);
    }
}
