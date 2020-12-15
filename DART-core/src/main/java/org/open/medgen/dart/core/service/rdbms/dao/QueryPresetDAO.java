/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.dao;

import org.open.medgen.dart.core.model.query.QueryRule;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.presets.*;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFField;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 *
 * @author dbarreca
 */
@Stateless
public class QueryPresetDAO extends DAO{

    public List<QueryPreset> retrievePresets(UserGroup userGroup) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<QueryPreset> userCriteria = cb.createQuery(QueryPreset.class);

        Root<QueryPreset> queryRoot = userCriteria.from(QueryPreset.class);
        userCriteria.where(cb.and(
                cb.equal(queryRoot.get(QueryPreset_.userGroup), userGroup),
                cb.isNull(queryRoot.get(QueryPreset_.userTo))
        ));

        return em.createQuery(userCriteria).getResultList();
    }
    
    public QueryPreset retrievePreset(UserGroup userGroup, QueryRule theRule) {        
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<QueryPreset> userCriteria = cb.createQuery(QueryPreset.class);

        Root<QueryPreset> queryRoot = userCriteria.from(QueryPreset.class);
        userCriteria.where(cb.and(
                cb.equal(queryRoot.get(QueryPreset_.userGroup), userGroup),
                cb.equal(queryRoot.get(QueryPreset_.queryHash), theRule.hashCode())),
                cb.isNull(queryRoot.get(QueryPreset_.userTo))
        );
        
        for (QueryPreset preset:  em.createQuery(userCriteria).getResultList()){
            if (preset.getRule().equals(theRule)) {
                return preset;
            }
        }
        
        return null;
    }
    
    
    public QueryPreset retrieveOrSaveQueryRule(User theUser, UserGroup userGroup, QueryRule theRule, String mnemonic) throws RDBMSServiceException {

        QueryPreset thePreset = retrievePreset(userGroup, theRule);

        if (thePreset == null) {
            thePreset = new QueryPreset();
            thePreset.setUserFrom(theUser);
            thePreset.setUserGroup(userGroup);
            thePreset.setQueryHash(theRule.hashCode());
            thePreset.setRule(theRule);
            
            for (Integer id: theRule.getFieldIds()){
                VCFField field = em.find(VCFField.class, id);
                if(field == null) throw new RDBMSServiceException("Field Not found: "+id);
                thePreset.getFieldList().add(field);
            }
            em.persist(thePreset);
        }
        if (mnemonic!=null) thePreset.setMnemonic(mnemonic);

        em.flush();
        
        return thePreset;

    }

    public QueryPreset retrievePresetOrNull(Integer presetId) {
        return em.find(QueryPreset.class, presetId);
    }
        
}
