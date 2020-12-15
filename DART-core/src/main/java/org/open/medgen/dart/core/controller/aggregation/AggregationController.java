/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.aggregation;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.model.mongo.aggregations.TranscriptMutationCount;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.service.mongo.service.SyncAggregationService;
import org.open.medgen.dart.core.controller.permission.EntityNotFoundException;
import org.open.medgen.dart.core.controller.permission.PermissionController;
import java.util.List;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 *
 * @author dbarreca
 */
@Stateless
@Default
public class AggregationController {
    
    @Inject private SyncAggregationService aggregationService;
    
    @Inject private PermissionController permissionService;
    
    public List<TranscriptMutationCount> getMutationByVCFAndTranscript(Integer vcfId,  String transcriptId,String sampleName, String userName) throws AuthorizationException, ControllerException{
                
        VCFFileDTO vcf;
        try {
            vcf = permissionService.getVcfForAnalysis(vcfId, userName);
        } catch (EntityNotFoundException ex) {
            throw new ControllerException(ex);
        }
        
        if (vcf==null) throw new AuthorizationException();
        
        
        return aggregationService.getMutationsInTranscript(vcf.getMongoId(), transcriptId, sampleName);
    }
    
}