/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.rdbms.service;

import org.open.medgen.dart.core.model.query.QueryFilter;
import org.open.medgen.dart.core.model.query.QueryPanel;
import org.open.medgen.dart.core.model.query.QueryRule;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFType;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BedFile;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.presets.FieldsPreset;
import org.open.medgen.dart.core.model.rdbms.entity.presets.FieldsPresetPK;
import org.open.medgen.dart.core.model.rdbms.entity.presets.PanelPreset;
import org.open.medgen.dart.core.model.rdbms.entity.presets.QueryPreset;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFField;
import org.open.medgen.dart.core.service.rdbms.dao.BedDAO;
import org.open.medgen.dart.core.service.rdbms.dao.FieldsPresetDAO;
import org.open.medgen.dart.core.service.rdbms.dao.PanelPresetDAO;
import org.open.medgen.dart.core.service.rdbms.dao.QueryPresetDAO;
import org.open.medgen.dart.core.service.rdbms.dao.UserDAO;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author dbarreca
 */
@Stateless
public class PresetService {
    
    @Inject UserDAO userDAO;
    @Inject QueryPresetDAO queryPresetDAO;
    @Inject PanelPresetDAO panelPresetDAO;
    @Inject BedDAO bedDAO;
    @Inject FieldsPresetDAO fieldsPresetDAO;
    
    public void addPresetToGroup(User theUser, UserGroup theGroup, QueryFilter queryPreset) throws RDBMSServiceException {
        
        queryPresetDAO.retrieveOrSaveQueryRule(theUser, theGroup, queryPreset.getFilter(), queryPreset.getMnemonic());
    }

    public QueryPreset getPreset(Integer presetId) {
        return queryPresetDAO.retrievePresetOrNull(presetId);
    }

    public QueryPreset getPreset(UserGroup userGroup, QueryRule queryRule) {
        return queryPresetDAO.retrievePreset(userGroup, queryRule);
    }

    public List<QueryPreset> getPresets(UserGroup userGroup) {
        return queryPresetDAO.retrievePresets(userGroup);
    }

    public void deleteQueryPreset(User user ,QueryPreset queryPreset) {
        queryPreset.setUserTo(user);
        queryPreset.setDateTo(new Date());
    }
    
    public List<Integer> getFieldsPreset(String userName, VCFType vcfType) {
        return fieldsPresetDAO.retrievePreset(userName,vcfType).stream()
                .map((FieldsPreset entity) ->{return entity.getKey().getField().getFieldId();})
                .collect(Collectors.toList());
    }
    
    public void saveFieldsPreset(String userName,VCFType vcfType, final List<Integer> preset) throws RDBMSServiceException{
        
        User user = userDAO.getUserOrNull(userName);
        if (user == null) throw new RDBMSServiceException("Pipeline not found!");
        
        fieldsPresetDAO.removePreset(userName, vcfType);
        
        Integer rank = 0;
        for (Integer fieldId: preset){
            VCFField field = fieldsPresetDAO.getField(fieldId);
            if (field == null) throw new RDBMSServiceException("Field with id "+fieldId+" not found");
            FieldsPresetPK key = new FieldsPresetPK(user, field, vcfType);
            FieldsPreset fieldPreset = new FieldsPreset(key, rank);
            fieldsPresetDAO.persistPresets(fieldPreset);

            rank +=1;
        }
    }
    
     public void addPanelToGroup(User theUser, UserGroup theGroup, QueryPanel panelPreset) throws RDBMSServiceException {
        
        BedFile bed = null;
        
        if (panelPreset.getBedFileId()!=null ){
            bed = bedDAO.getFileById(panelPreset.getBedFileId());
            if (bed==null) throw new RDBMSServiceException("Bed not found");
        }

        panelPresetDAO.retrieveOrSavePanelPreset(theUser, theGroup, bed, panelPreset.getGeneList(), panelPreset.getMnemonic());
        
    }

    public PanelPreset getPanelPreset(Integer presetId) {
        return panelPresetDAO.retrievePresetOrNull(presetId);
    }

    public PanelPreset getPanelPreset(UserGroup userGroup, QueryPanel thePanel) {
        return panelPresetDAO.retrievePreset(userGroup, thePanel.getGeneList(), thePanel.getBedFileId());
    }

    public List<PanelPreset> getPanelPresets(UserGroup userGroup) {
        return panelPresetDAO.retrievePresets(userGroup);
    }

    public void deletePanelPreset(User user ,PanelPreset thePreset) {
        thePreset.setUserTo(user);
        thePreset.setDateTo(new Date());
    }
    
        
}
