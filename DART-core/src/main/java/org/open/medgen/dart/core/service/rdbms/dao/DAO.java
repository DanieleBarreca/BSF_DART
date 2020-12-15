/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.open.medgen.dart.core.DartCoreConfig;

/**
 *
 * @author dbarreca
 */
public class DAO {
    
    @PersistenceContext(unitName=DartCoreConfig.PERSISTENCE_UNIT)
    protected EntityManager em;

    public DAO() {
    }

    public DAO(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEm() {
        return em;
    }
       
    
}
