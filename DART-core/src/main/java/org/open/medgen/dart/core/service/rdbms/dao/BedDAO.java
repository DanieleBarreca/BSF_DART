/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.dao;

import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BEDFilePermission;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BEDFilePermissionPK;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BedFile;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BedFile_;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


/**
 *
 * @author dbarreca
 */
@Stateless
public class BedDAO extends DAO {

    public BedDAO() {
    }

    public BedDAO(EntityManager em) {
        super(em);
    }

    public void save(BedFile theFile) {
        em.persist(theFile);
        em.flush();
    }

    public BedFile getFileByMD5OrNull(String md5) {
            
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        CriteriaQuery<BedFile> userCriteria = cb.createQuery(BedFile.class);     

        Root<BedFile> userRoot = userCriteria.from(BedFile.class);        
        userCriteria.where(cb.equal(userRoot.get(BedFile_.md5), md5));
        
        try{
            return em.createQuery(userCriteria).getSingleResult();          
        }catch(NoResultException e){
            return null;
        }
    }
    
    public BedFile getFileById(Integer theFileId) {
        return em.find(BedFile.class, theFileId);
    }

    public void delete(BedFile theFile) {
        em.remove(theFile);
    }

    public List<BedFile> getAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BedFile> criteria = cb.createQuery(BedFile.class);
        Root<BedFile> root = criteria.from(BedFile.class);
        CriteriaQuery<BedFile> all = criteria.select(root);
        
        return em.createQuery(all).getResultList();
    }
    
     public BEDFilePermission getOrCreatePermission(BedFile theFile, UserGroup userGroup) {
        BEDFilePermissionPK theKey = new BEDFilePermissionPK(theFile, userGroup);
        BEDFilePermission existingPermission = em.find(BEDFilePermission.class, theKey);
        if (existingPermission == null){
            existingPermission = new BEDFilePermission(theKey);
            em.persist(existingPermission);
            em.flush();
        }
        
        return existingPermission;
    }
     
    public void persist(BedFile theBED) {
        em.persist(theBED);
        em.flush();
    }
}
