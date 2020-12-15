/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.vcf;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 *
 * @author dbarreca
 */
@Entity
@Table(name = "sample_vcf")
public class VCFSample implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "sample_id")
    private Integer sampleId;

    @JoinColumn(name = "vcf_id", referencedColumnName = "vcf_id")
    @ManyToOne(optional = false)
    private VCFFile vcf;

    @Size(max = 500)
    @Column(name = "sample_name")
    private String sampleName;
   
    @Size(max = 255)
    @Column(name = "bam_url")
    private String bamUrl;
     
    @Size(max = 255)
    @Column(name = "sample_vcf_url")
    private String sampleVcfUrl;

    @Size(max = 255)
    @Column(name = "sample_coverage_track_url")
    private String coverageTrackUrl;

    @Size(max = 45)
    @Column(name = "sample_coverage_mongo_id")
    private String coverageMongoId;

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public String getBamUrl() {
        return bamUrl;
    }

    public void setBamUrl(String bamUrl) {
        this.bamUrl = bamUrl;
    }

    public String getSampleVcfUrl() {
        return sampleVcfUrl;
    }

    public void setSampleVcfUrl(String sampleVcfUrl) {
        this.sampleVcfUrl = sampleVcfUrl;
    }

    public VCFFile getVcf() {
        return vcf;
    }

    public void setVcf(VCFFile vcf) {
        this.vcf = vcf;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getCoverageTrackUrl() {
        return coverageTrackUrl;
    }

    public void setCoverageTrackUrl(String coverageTrackUrl) {
        this.coverageTrackUrl = coverageTrackUrl;
    }

    public String getCoverageMongoId() {
        return coverageMongoId;
    }

    public void setCoverageMongoId(String coverageMongoId) {
        this.coverageMongoId = coverageMongoId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.sampleId);
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
        final VCFSample other = (VCFSample) obj;
        if (!Objects.equals(this.sampleId, other.sampleId)) {
            return false;
        }
        return true;
    }

   
    
    
    
    
}
