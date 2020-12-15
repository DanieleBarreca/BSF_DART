/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.dao;

import org.open.medgen.dart.core.model.query.QueryPanel;
import org.open.medgen.dart.core.model.rdbms.dto.bed.BedFileDTO;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BedFile;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BedFile_;
import org.open.medgen.dart.core.model.rdbms.entity.presets.PanelPreset;
import org.open.medgen.dart.core.model.rdbms.entity.presets.PanelPreset_;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.*;

/**
 *
 * @author dbarreca
 */
@Stateless
public class PanelPresetDAO extends DAO{

    public List<PanelPreset > retrievePresets(UserGroup userGroup)  {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<PanelPreset> panelCriteria = cb.createQuery(PanelPreset.class);

        Root<PanelPreset> queryRoot = panelCriteria.from(PanelPreset.class);

        panelCriteria.where(cb.and(
                cb.equal(queryRoot.get(PanelPreset_.userGroup), userGroup),
                cb.isNull(queryRoot.get(PanelPreset_.userTo))
        ));

        return em.createQuery(panelCriteria).getResultList();
    }
    
    public PanelPreset retrievePreset(UserGroup userGroup, Set<String> genes, Integer bedFileId)  {
        
        if (genes==null){
            genes = new HashSet<>();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<PanelPreset> panelCriteria = cb.createQuery(PanelPreset.class);

        Root<PanelPreset> queryRoot = panelCriteria.from(PanelPreset.class);
        Join<PanelPreset,BedFile> bedFile = queryRoot.join(PanelPreset_.bedFile, JoinType.LEFT);

        panelCriteria.where(cb.and(
                cb.equal(queryRoot.get(PanelPreset_.userGroup), userGroup),
                cb.equal(queryRoot.get(PanelPreset_.genesHash), genes.hashCode()),
                bedFileId == null? cb.isNull(queryRoot.get(PanelPreset_.bedFile)) : cb.equal(bedFile.get(BedFile_.bedId), bedFileId),
                cb.isNull(queryRoot.get(PanelPreset_.userTo))
        ));

        for (PanelPreset preset:  em.createQuery(panelCriteria).getResultList()){
            if ( genes.equals(preset.getGenesSet()) && 
                    Objects.equals(bedFileId, preset.getBedFile() == null? null : preset.getBedFile().getBedId()) ) {
                return preset;
            }
        }

        return null;
    }

    public PanelPreset retrieveOrSavePanelPreset(User theUser, UserGroup userGroup, BedFile theBedFile, Set<String> genes, String mnemonic) throws RDBMSServiceException {

        PanelPreset thePreset = retrievePreset(userGroup, genes, theBedFile==null? null : theBedFile.getBedId());

        if (thePreset == null) {
            thePreset = new PanelPreset();
            thePreset.setUserFrom(theUser);
            thePreset.setUserGroup(userGroup);
            thePreset.setGenesHash(genes.hashCode());
            thePreset.setGenesSet(genes);
            if (theBedFile!=null) thePreset.setBedFile(theBedFile);
            thePreset.setMnemonic(mnemonic);
            
            em.persist(thePreset);
        }

        em.flush();

        return thePreset;

    }
    
    public PanelPreset retrievePresetOrNull(Integer presetId) {
        return em.find(PanelPreset.class, presetId);
    }
    
        
}
