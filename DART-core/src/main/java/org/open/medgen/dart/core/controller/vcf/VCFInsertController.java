/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.vcf;

import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.permission.EntityNotFoundException;
import org.open.medgen.dart.core.model.mongo.coverage.CoverageEntry;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author dbarreca
 */
@Remote
public interface VCFInsertController {
    
    public Integer saveHeader(VCFFileDTO header, String userGroup) throws VCFLoadingException,AuthorizationException;
    
    public void insertVariants(List<VariantModel> variants) throws VCFLoadingException, AuthorizationException, EntityNotFoundException;

    public void addCoverageRegions(List<CoverageEntry> coverageEntries, String sampleName, Integer theFileId) throws VCFLoadingException, AuthorizationException, EntityNotFoundException;
    
    public void finishLoading(Integer theVcfId) throws VCFLoadingException, AuthorizationException, EntityNotFoundException;
    
    public void finishLoadingeExceptionally(Integer theVcfId, String message) throws VCFLoadingException, AuthorizationException, EntityNotFoundException ;
        
    public String ping(String testString);
}
