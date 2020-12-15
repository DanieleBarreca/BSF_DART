/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.dao;

import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFType;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.User_;
import org.open.medgen.dart.core.model.rdbms.entity.presets.FieldsPreset;
import org.open.medgen.dart.core.model.rdbms.entity.presets.FieldsPresetPK;
import org.open.medgen.dart.core.model.rdbms.entity.presets.FieldsPresetPK_;
import org.open.medgen.dart.core.model.rdbms.entity.presets.FieldsPreset_;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFField;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

/**
 *
 * @author dbarreca
 */
@Stateless
public class FieldsPresetDAO extends DAO{ 

    public List<FieldsPreset> retrievePreset(String userName, VCFType vcfType)  {       
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FieldsPreset> cq = cb.createQuery(FieldsPreset.class);
        Root<FieldsPreset> preset = cq.from(FieldsPreset.class);
        Join<FieldsPreset,FieldsPresetPK> presetPK = preset.join(FieldsPreset_.key);        
        Join<FieldsPresetPK, User> user = presetPK.join(FieldsPresetPK_.user, JoinType.LEFT);
                
        cq.select(preset);
        cq.where(cb.and(
            cb.equal(user.get(User_.login),userName),
            cb.equal(presetPK.get(FieldsPresetPK_.type), vcfType)
        ));
        
        cq.orderBy(cb.asc(preset.get(FieldsPreset_.rank)));
        
        return em.createQuery(cq).getResultList();
    }
    
    public void removePreset(String userName, VCFType vcfType) throws RDBMSServiceException {
       this.retrievePreset(userName, vcfType).forEach((FieldsPreset preset) -> {em.remove(preset);});
       em.flush();
    }
   
     
    public void persistPresets(FieldsPreset preset) {
        em.persist(preset);
        em.flush();
    }    

    public VCFField getField(Integer fieldId) {
        return em.find(VCFField.class, fieldId);
    }
        
}
