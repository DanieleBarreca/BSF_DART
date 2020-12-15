/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.preset;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.model.query.QueryFilter;
import org.open.medgen.dart.core.model.query.QueryPanel;
import org.open.medgen.dart.core.model.query.QueryRule;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserRole;
import org.open.medgen.dart.core.model.rdbms.entity.presets.PanelPreset;
import org.open.medgen.dart.core.model.rdbms.entity.presets.QueryPreset;
import org.open.medgen.dart.core.controller.permission.EntityNotFoundException;
import org.open.medgen.dart.core.controller.permission.PermissionController;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFType;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFField;
import org.open.medgen.dart.core.service.rdbms.service.PresetService;
import org.open.medgen.dart.core.service.rdbms.service.UserService;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;

import java.util.*;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * @author dbarreca
 */
@Stateless
@Default
public class PresetController {

    @Inject
    private PresetService presetService;

    @Inject
    private UserService userService;

    @Inject
    private PermissionController permissionService;

    public void addQueryToGroup(String user, String groupName, QueryFilter query) throws ControllerException, AuthorizationException {
        User theUser = permissionService.canSavePreset(user, groupName);

        if (theUser != null) {
            Optional<UserRole> maybeUserRole = theUser.getUserRoles().stream().filter((UserRole userRole) -> 
                userRole.getGroup().getGroup().equals(groupName)
            ).findFirst();

            try {
                presetService.addPresetToGroup(theUser, maybeUserRole.get().getGroup(), query);
            } catch (RDBMSServiceException ex) {
                throw new ControllerException(ex);
            }
           
        } else {
            throw new AuthorizationException();
        }
    }

    public QueryFilter getPresetForQuery(String userName, String userGroup, QueryRule theQuery) throws AuthorizationException, ControllerException {
        User theUser = userService.getUserOrNull(userName);
        if (theUser == null) throw new AuthorizationException();

        Optional<UserRole> roleInGroup = theUser.getUserRoles().stream().filter(
                (UserRole role) -> role.getGroup().getGroup().equals(userGroup)
        ).findFirst();

        if (roleInGroup.isPresent()) {
            QueryPreset queryPreset = presetService.getPreset(roleInGroup.get().getGroup(), theQuery);
            return new QueryFilter(queryPreset);
        } else {
            return new QueryFilter();
        }
    }

    public QueryPanel getPresetForPanel(String userName, String userGroup, QueryPanel thePanel) throws AuthorizationException, ControllerException {
        User theUser = userService.getUserOrNull(userName);
        if (theUser == null) throw new AuthorizationException();

        Optional<UserRole> roleInGroup = theUser.getUserRoles().stream().filter(
                (UserRole role) -> role.getGroup().getGroup().equals(userGroup)
        ).findFirst();

        if (roleInGroup.isPresent()) {
            PanelPreset panelPreset = presetService.getPanelPreset(roleInGroup.get().getGroup(), thePanel);
            return new QueryPanel(panelPreset);
        } else {
            return new QueryPanel();
        }
    }

    public List<QueryFilter> getQueryPresetsForUserAndVCF(String user, String userGroup, Integer vcfId) throws AuthorizationException, ControllerException {
        User theUser = userService.getUserOrNull(user);
        if (theUser == null) throw new AuthorizationException();

        VCFFileDTO vcfFile;
        try {
            vcfFile = permissionService.getVcfForQuery(vcfId, user, userGroup);
        } catch (EntityNotFoundException ex) {
            throw new ControllerException("VCF file not found");
        }

        Optional<UserRole> roleInGroup = theUser.getUserRoles().stream().filter(
                (UserRole role) -> role.getGroup().getGroup().equals(userGroup)
        ).findFirst();

        if (roleInGroup.isPresent()) {
            final Set<Integer> fields = new HashSet();
            final Set<String> chromosomes = new HashSet();
            final Set<String> filters = new HashSet();

            for (VCFInfoDTO field : vcfFile.getVcfFields()) {
                fields.add(field.getId());
                if (field.getFieldPath().equals(VariantModel.CHROM)) {
                    chromosomes.addAll(field.getPossibleValues());
                } else if (field.getFieldPath().equals(VariantModel.FILTER)) {
                    filters.addAll(field.getPossibleValues());
                }
            }
            
            List<QueryPreset> presets = presetService.getPresets(roleInGroup.get().getGroup());
            
            //We select only those presets whose fields are all contained in the VCF
            return  presets.stream().filter(
                    (QueryPreset queryPreset) -> fields.containsAll(
                            queryPreset.getFieldList().stream().map(VCFField::getFieldId).collect(Collectors.toSet())
                    )
            ).map(
                    (QueryPreset preset) -> new QueryFilter(preset, chromosomes, filters)  
            ).collect(
                    Collectors.toList()
            );

        } else {
            return new LinkedList<>();
        }

    }

    public List<QueryFilter> getAllQueryPresetsForUser(String user, String userGroup) throws AuthorizationException, ControllerException {
        User theUser = userService.getUserOrNull(user);
        if (theUser == null) throw new AuthorizationException();

        Optional<UserRole> roleInGroup = theUser.getUserRoles().stream().filter(
                (UserRole role) -> role.getGroup().getGroup().equals(userGroup)
        ).findFirst();

        if (roleInGroup.isPresent()) {

            return presetService.getPresets(roleInGroup.get().getGroup()).stream().map(
                    (QueryPreset preset) -> new QueryFilter(preset, new HashSet<>(), new HashSet<>())
            ).collect(
                    Collectors.toList()
            );

        } else {
            return new LinkedList<>();
        }
    }

    public void deleteQueryPreset(String userName, Integer presetId) throws AuthorizationException {

        QueryPreset thePreset = presetService.getPreset(presetId);
        if (thePreset == null) throw new AuthorizationException("Preset permissions not found");
        
        User user = permissionService.canDeletePreset(userName, thePreset);
        
        if (user!=null) {
            presetService.deleteQueryPreset(user,thePreset);
        } else {
            throw new AuthorizationException();
        }
    }

    public List<Integer> getFieldsPreset(String userName, VCFType vcfType) {
        return presetService.getFieldsPreset(userName, vcfType);
    }

    public void saveFieldsPreset(String userName, VCFType vcfType, List<Integer> preset) throws ControllerException {
        try {
            presetService.saveFieldsPreset(userName, vcfType, preset);
        } catch (RDBMSServiceException ex) {
            throw new ControllerException(ex);
        }
    }

    public void addPanelPresetToGroup(String user, String groupName, QueryPanel panel) throws ControllerException, AuthorizationException {

        User theUser = permissionService.canSavePanel(user, groupName);

        if (theUser != null) {
            try {
                if (panel.getBedFileId() != null && !permissionService.bedIsVisibleToGroup(panel.getBedFileId(), groupName))
                    throw new AuthorizationException();
            }catch (EntityNotFoundException e){
                throw new AuthorizationException();
            }

            Optional<UserRole> maybeUserRole = theUser.getUserRoles().stream().filter((UserRole userRole) ->
                    userRole.getGroup().getGroup().equals(groupName)
            ).findFirst();

            try {
                presetService.addPanelToGroup(theUser, maybeUserRole.get().getGroup(), panel);
            } catch (RDBMSServiceException ex) {
                throw new ControllerException(ex);
            }

        } else {
            throw new AuthorizationException();
        }
    }

    public List<QueryPanel> getPanelsForUser(String user, final String userGroup) throws AuthorizationException {
        User theUser = userService.getUserOrNull(user);
        if (theUser == null) throw new AuthorizationException();


        Optional<UserRole> roleInGroup = theUser.getUserRoles().stream().filter(
                (UserRole role) -> role.getGroup().getGroup().equals(userGroup)
        ).findFirst();

        if (roleInGroup.isPresent()) {
            return presetService.getPanelPresets(roleInGroup.get().getGroup()).stream().map(QueryPanel::new).collect(Collectors.toList());
        } else {
            return new LinkedList();
        }

    }

    public void deletePanelPreset(String userName, Integer presetId) throws AuthorizationException {

        PanelPreset thePreset = presetService.getPanelPreset(presetId);
        if (thePreset == null) throw new AuthorizationException("Preset not found");

        User user = permissionService.canDeletePanel(userName, thePreset);

        if (user!=null) {
            presetService.deletePanelPreset(user,thePreset);
        } else {
            throw new AuthorizationException();
        }

    }

}