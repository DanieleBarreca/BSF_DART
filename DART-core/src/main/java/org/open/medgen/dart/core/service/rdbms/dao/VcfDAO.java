/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.dao;

import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.*;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.bson.types.ObjectId;


/**
 *
 * @author dbarreca
 */
@Stateless
public class VcfDAO extends DAO {

    public VcfDAO() {
    }

    public VcfDAO(EntityManager em) {
        super(em);
    }

    public void save(VCFFile theFile) {
        em.persist(theFile);
        em.flush();
    }
    
    public VCFField getField(String path, String description) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<VCFField> fieldCriteria = cb.createQuery(VCFField.class);
        Root<VCFField> field = fieldCriteria.from(VCFField.class);
        fieldCriteria.where(cb.and(
                cb.equal(field.get(VCFField_.fieldPath), path),
                cb.equal(field.get(VCFField_.description), description)
        ));
        try{
            VCFField result = em.createQuery(fieldCriteria).getSingleResult();
            return result;
        }catch(NoResultException e){
            return null;
        }

    }
    
    public VCFField saveOrGetField(VCFField theField) {
        VCFField existing = this.getField(theField.getFieldPath(), theField.getDescription());
        if (existing!=null){
            return existing;
        }else{
            em.persist(theField);
            em.flush();
            
            return theField;
        }
    }

    public VCFFile getFileByMD5OrNull(String vcfMD5) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<VCFFile> userCriteria = cb.createQuery(VCFFile.class);

        Root<VCFFile> userRoot = userCriteria.from(VCFFile.class);
        userCriteria.where(cb.equal(userRoot.get(VCFFile_.md5), vcfMD5));

        try{
            return em.createQuery(userCriteria).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public VCFFile getFileById(Integer theFileId) {
        return em.find(VCFFile.class, theFileId);
    }
  
    public List<VCFFile> getAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<VCFFile> vcfCriteria = cb.createQuery(VCFFile.class);
        Root<VCFFile> vcfRoot = vcfCriteria.from(VCFFile.class);
        CriteriaQuery<VCFFile> all = vcfCriteria.select(vcfRoot);
        
        return em.createQuery(all).getResultList();
    }

    public void saveVCFSample(VCFSample vcfSampleEntity) {
        em.persist(vcfSampleEntity);
        em.flush();
    }
    
    public VCFFile getFileByMongoId(ObjectId vcfId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<VCFFile> userCriteria = cb.createQuery(VCFFile.class);

        Root<VCFFile> userRoot = userCriteria.from(VCFFile.class);
        userCriteria.where(cb.equal(userRoot.get(VCFFile_.mongoId), vcfId.toHexString()));

        try{
            return em.createQuery(userCriteria).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public VCFFilePermission getOrCreatePermission(VCFFile theFile, UserGroup userGroup) {
        VCFFilePermissionPK theKey = new VCFFilePermissionPK(theFile, userGroup);
        VCFFilePermission existingPermission = em.find(VCFFilePermission.class, theKey);
        if (existingPermission == null){
            existingPermission = new VCFFilePermission(theKey);
            em.persist(existingPermission);
            em.flush();
        }
        
        return existingPermission;
    }

    public void persist(VCFFile theVCF) {
        em.persist(theVCF);
        em.flush();
    }

    public VCFSample getVCFSampleById(Integer dbId) {
        return  em.find(VCFSample.class, dbId);
    }

    public List<VCFSample> getVCFSampleByFileURI(String fileURI) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        CriteriaQuery<VCFSample> sampleCriteria = cb.createQuery(VCFSample.class);

        Root<VCFSample>  vcfSample = sampleCriteria.from(VCFSample.class);

        sampleCriteria.where(cb.or(
                cb.equal(vcfSample.get(VCFSample_.BAM_URL),fileURI),
                cb.equal(vcfSample.get(VCFSample_.COVERAGE_TRACK_URL),fileURI),
                cb.equal(vcfSample.get(VCFSample_.SAMPLE_VCF_URL),fileURI)
        ));

        return em.createQuery(sampleCriteria).getResultList();

    }
}
