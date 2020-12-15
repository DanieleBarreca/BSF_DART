/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.bed;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.permission.PermissionController;
import org.open.medgen.dart.core.model.rdbms.dto.bed.BedFileDTO;
import org.open.medgen.dart.core.service.rdbms.service.DbBEDService;
import org.open.medgen.dart.core.service.rdbms.service.UserService;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class BEDController {
    
    @Inject DbBEDService dbBedService;
    @Inject UserService userService;
    @Inject private PermissionController permissionService;
    
    @Transactional
    public List<BedFileDTO> getAll(String user, String userGroup, String genome) throws AuthorizationException{
  
        return permissionService.getAllBEDsForQuery(user, userGroup, BedGenomeVersion.getCorrectedVersion(genome));
    }
}
