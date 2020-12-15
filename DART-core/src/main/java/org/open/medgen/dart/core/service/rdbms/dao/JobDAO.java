/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.dao;

import org.open.medgen.dart.core.model.rdbms.entity.log.Job;
import org.open.medgen.dart.core.model.rdbms.entity.log.JobMessage;
import javax.ejb.Stateless;

/**
 *
 * @author dbarreca
 */
@Stateless
public class JobDAO extends DAO{
    
    public Job persistJob(Job job) {
        em.persist(job);
        em.flush();
        
        return job;
    }

    public JobMessage persistMessage(JobMessage theMessage) {
        em.persist(theMessage);
        em.flush();
        
        return theMessage;
    }
}
