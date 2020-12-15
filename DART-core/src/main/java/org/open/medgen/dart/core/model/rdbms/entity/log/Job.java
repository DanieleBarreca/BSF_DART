/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.log;

import org.open.medgen.dart.core.model.rdbms.entity.User;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import static javax.persistence.EnumType.STRING;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 *
 * @author dbarreca
 */
@Entity
@Table(name = "job")
public class Job implements Serializable {
     public static enum JobType{
        VCF_UPLOAD,
        VCF_DELETE,
        BED_UPLOAD,
        BED_DELETE,
        REPORT_VARIANTS,
        REPORT_COVERAGE 
    }
     
    private static final long serialVersionUID = 1L;
    
    @Id   
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    
    @Column(name="type")
    @Enumerated(STRING)
    private JobType type;
    
    @Column(name="creation_date")
    @Temporal(TIMESTAMP)
    private Date creationDate = new Date();
    
    @JoinColumn(name = "user", referencedColumnName = "userId")
    @ManyToOne(optional = false)    
    private User user;
    
    @JoinColumn(name = "previous_job", referencedColumnName = "id")
    @ManyToOne()    
    private Job previousJob;
    
    @OneToMany(mappedBy="job")
    private List<JobMessage> messages;

    public Job() {
    }

    public Job(JobType type, User user) {
        this.type = type;
        this.user = user;
    }

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public List<JobMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<JobMessage> messages) {
        this.messages = messages;
    }

    public Job getPreviousJob() {
        return previousJob;
    }

    public void setPreviousJob(Job previousJob) {
        this.previousJob = previousJob;
    }
    
    
    
    
    
    
}
