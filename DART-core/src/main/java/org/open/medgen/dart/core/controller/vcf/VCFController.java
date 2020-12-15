/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.vcf;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.model.rdbms.entity.enums.MongoFileStatus;
import org.open.medgen.dart.core.controller.permission.EntityNotFoundException;
import org.open.medgen.dart.core.controller.permission.PermissionController;
import java.util.List;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author dbarreca
 */
@Stateless
@Default
public class VCFController {
    
    @Inject private PermissionController permissionService;
    
    @Transactional
    public List<VCFFileDTO> getAll(String user, String userGroup) throws AuthorizationException {
        return permissionService.getAllVCFsForQuery(user, userGroup);
    }
    
    @Transactional
    public VCFFileDTO getVCFInfo(Integer vcfFileId, String user) throws AuthorizationException{
        
        try {
            VCFFileDTO theVCF = permissionService.getVcfForAnalysis(vcfFileId, user);
            if (theVCF==null) throw new AuthorizationException();
            
            if (!MongoFileStatus.AVAILABLE.equals(theVCF.getStatus())) return null;
            
            return theVCF;
        } catch (EntityNotFoundException ex) {
            return null;
        }        
    }

}
