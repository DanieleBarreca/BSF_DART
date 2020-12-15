/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.bed;

import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.model.rdbms.entity.log.Job;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFFilePermission;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dbarreca
 */
@Entity
@Table(name = "bed_file")
public class BedFile implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "bed_id")
    private Integer bedId;
    
    @Size(max = 255)
    @Column(name = "file_name")
    @NotNull
    private String fileName;
    
    @Size(max = 45)
    @Column(name = "mongo_id")
    @NotNull
    private String mongoId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private MongoFileStatus status;
    
    @Size(max=32)
    @Column(name="md5")
    @NotNull
    private String md5;
    
    @JoinColumn(name = "latest_job", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Job latestJob;

    @Size(max=32)
    @Column(name="genome")
    @NotNull
    private String genome;
    
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "key.file")
    private List<BEDFilePermission> permissions = new LinkedList();
  
    public BedFile() {
    }

    public BedFile(Integer bedId) {
        this.bedId = bedId;
    }

    public Integer getBedId() {
        return bedId;
    }

    public void setBedId(Integer bedId) {
        this.bedId = bedId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<BEDFilePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<BEDFilePermission> permissions) {
        this.permissions = permissions;
    }


    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public MongoFileStatus getStatus() {
        return status;
    }

    public void setStatus(MongoFileStatus status) {
        this.status = status;
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

    public void setLatestJob(Job latestJob) {
        this.latestJob = latestJob;
    }

    public String getGenome() {
        return genome;
    }

    public void setGenome(String genome) {
        this.genome = genome;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bedId != null ? bedId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BedFile)) {
            return false;
        }
        BedFile other = (BedFile) object;
        if ((this.bedId == null && other.bedId != null) || (this.bedId != null && !this.bedId.equals(other.bedId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.open.medgen.vcfcore.model.v1.annotation.entity.BedFile[ bedId=" + bedId + " ]";
    }

    
    
    
    
}
