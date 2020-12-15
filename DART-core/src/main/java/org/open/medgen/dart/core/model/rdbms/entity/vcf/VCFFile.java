/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.vcf;


import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFType;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.model.rdbms.entity.log.Job;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import static javax.persistence.EnumType.STRING;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.bson.types.ObjectId;

/**
 *
 * @author dbarreca
 */
@Entity
@Table(name = "vcf_file")
public class VCFFile implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "vcf_id")
    private Integer vcfId;
    
    @Size(max = 255)
    @Column(name = "file_name")
    @NotNull
    private String fileName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private MongoFileStatus status;
     
    @Size(max = 45)
    @Column(name = "mongo_id")
    @NotNull
    private String mongoId;
    
    @Size(max=32)
    @Column(name="md5")
    @NotNull
    private String md5;
    
    @Column(name="creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    
    @JoinColumn(name = "latest_job", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Job latestJob;
    
    @Size(max=500)
    @Column(name = "pipeline_details")
    private String pipelineDetails;
    
    @Column(name="type")
    @Enumerated(STRING)
    private VCFType type;
    
    @Column(name="ref_genome")
    private String refGenome;
    
    @JoinTable(name = "vcf_fields", 
            joinColumns = {@JoinColumn(name = "vcf_id", referencedColumnName = "vcf_id")}, 
            inverseJoinColumns = {@JoinColumn(name = "field_id", referencedColumnName = "field_id")}
    )
    @ManyToMany
    @OrderBy("id ASC")
    private List<VCFField> fieldList;
     
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vcf")
    private List<VCFSample> sampleList;
    
    @Lob
    @Size(max = 65535)
    @Column(name = "contigs")
    private String contigs;

    @Lob
    @Size(max = 65535)
    @Column(name = "filter")
    private String filter;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "key.file")
    private List<VCFFilePermission> permissions = new LinkedList();


    public VCFFile() {
    }
    
    
    public Integer getVcfId() {
        return vcfId;
    }

    public void setVcfId(Integer vcfId) {
        this.vcfId = vcfId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MongoFileStatus getStatus() {
        return status;
    }

    public void setStatus(MongoFileStatus status) {
        this.status = status;
    }

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public List<VCFSample> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<VCFSample> sampleList) {
        this.sampleList = sampleList;
    }

    public List<VCFFilePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<VCFFilePermission> permissions) {
        this.permissions = permissions;
    }

    
     public ObjectId getMongoIdObj() {
        if (mongoId.trim().isEmpty()) return null;
               
        return new ObjectId(mongoId);
    }

    public void setMongoId(ObjectId mongoId) {
        this.mongoId = mongoId.toHexString();

    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Job getLatestJob() {
        return latestJob;
    }

    public void setLatestJob(Job uploadJob) {
        this.latestJob = uploadJob;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getPipelineDetails() {
        return pipelineDetails;
    }

    public void setPipelineDetails(String pipelineDetails) {
        this.pipelineDetails = pipelineDetails;
    }

    public VCFType getType() {
        return type;
    }

    public void setType(VCFType type) {
        this.type = type;
    }

    public String getRefGenome() {
        return refGenome;
    }

    public void setRefGenome(String refGenome) {
        this.refGenome = refGenome;
    }

    public List<VCFField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<VCFField> fieldList) {
        this.fieldList = fieldList;
    }

    public String getContigs() {
        return contigs;
    }

    public void setContigs(String contigs) {
        this.contigs = contigs;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
    
    
    
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vcfId != null ? vcfId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VCFFile)) {
            return false;
        }
        VCFFile other = (VCFFile) object;
        if ((this.vcfId == null && other.vcfId != null) || (this.vcfId != null && !this.vcfId.equals(other.vcfId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.open.medgen.vcfcore.model.v1.annotation.entity.Vcf[ vcfId=" + vcfId + " ]";
    }

   
}
