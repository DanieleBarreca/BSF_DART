/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.log;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.TIMESTAMP;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dbarreca
 */
@Entity
@Table(name = "job_message")
public class JobMessage implements Serializable {
    public static enum Type {
        WARNING,
        ERROR,
        INFO
    }
    
    
    private static final long serialVersionUID = 1L;

    
    @Id   
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
      
    @JoinColumn(name = "upload_job", referencedColumnName = "id")
    @ManyToOne(optional = false)    
    private Job job;
    
    @Enumerated(EnumType.STRING)
    @Column(name="type")
    @NotNull
    private Type messageType;
    
    @Size(max=200)
    @Column(name="message")
    private String message;
    
    @Column(name="date_time")
    @Temporal(TIMESTAMP)
    @NotNull
    private Date dateTime = new Date();

    public JobMessage(Type messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public JobMessage() {
    }

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
    
    
    
    
}
