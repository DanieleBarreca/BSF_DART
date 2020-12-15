package org.open.medgen.dart.core.controller.annotation;


import org.open.medgen.dart.core.controller.AuthorizationException;
import org.open.medgen.dart.core.controller.ControllerException;
import org.open.medgen.dart.core.controller.permission.PermissionController;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.dto.PermissionsDTO;
import org.open.medgen.dart.core.model.rdbms.dto.annotation.*;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.UserRole;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.*;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import org.open.medgen.dart.core.service.mongo.service.SyncVariantService;
import org.open.medgen.dart.core.service.rdbms.service.AnnotationService;
import org.open.medgen.dart.core.service.rdbms.service.DbVCFService;
import org.open.medgen.dart.core.service.rdbms.service.UserService;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Default
public class AnnotationController {

    @Inject
    PermissionController permissionService;

    @Inject
    UserService userService;

    @Inject
    AnnotationService annotationService;

    @Inject
    SyncVariantService variantService;
    
    @Inject
    DbVCFService vcfService;
    
    public void annotateVariant(String userName, String userGroup, AnnotatedPathogenicityDTO variantDTO)  throws AuthorizationException, ControllerException {
        PermissionsDTO permissionsDTO = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissionsDTO == null || !permissionsDTO.isCanAnnotatePathogenicity()){
            throw new AuthorizationException();
        }
        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();
        

        VCFSample sample = vcfService.retrieveVCFSampleEntity(variantDTO.getSample().getDbId());
        if (sample == null) throw new ControllerException("Sample with id "+variantDTO.getSample().getDbId()+" not found");
        if (!userGroupEntity.getVcfList().contains(sample.getVcf())){
            throw new AuthorizationException("User is not allowed to access the VCF");
        }

        AnnotationDictionary annotation = annotationService.getAnnotationDictionaryTerm(variantDTO.getAnnotationTerm().getDbId());
        if (annotation == null) throw new ControllerException("Annotation dictionary with id "+variantDTO.getSample().getDbId()+" not found");

        ConditionDictionary condition = annotationService.getConditionTerm(variantDTO.getCondition().getDbId());
        if (condition == null) throw new ControllerException("Annotation dictionary with id "+variantDTO.getSample().getDbId()+" not found");

        InheritanceDictionary inheritance = annotationService.getInheritanceTerm(variantDTO.getInheritance().getDbId());
        if (condition == null) throw new ControllerException("Annotation dictionary with id "+variantDTO.getSample().getDbId()+" not found");
        
        VariantModel variantModel = variantService.getVariant(variantDTO.getVariantModelRef());
        if (variantModel == null) throw new ControllerException("Variant model with id "+variantDTO.getVariantModelRef()+" not found");

        try {
            annotationService.annotateVariant(
                    variantDTO.getRefGenome(), 
                    variantDTO.getHgvsg(), 
                    variantDTO.getHgvsc(), 
                    variantDTO.getGene(),
                    condition,
                    inheritance,
                    user,
                    userGroupEntity,
                    sample,
                    annotation,
                    variantModel
            );
        } catch (RDBMSServiceException e) {
            throw new ControllerException(e);
        }

    }

    public void deleteAnnotation(Integer annotationId, String userName, String userGroup) throws AuthorizationException{
        PermissionsDTO permissionsDTO = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissionsDTO == null || !permissionsDTO.isCanAnnotatePathogenicity()){
            throw new AuthorizationException();
        }
        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();

        VariantAnnotation annotation = annotationService.retrieveAnnotation(annotationId);
        if (annotation!=null && annotation.getGroup().equals(userGroupEntity)){
            annotationService.invalidateAnnotation(annotation, user);
        }
    }

    public void annotateVariantSample(String userName, String userGroup, AnnotatedVariantSampleDTO variantSampleDTO)  throws AuthorizationException, ControllerException {
        PermissionsDTO permissionsDTO = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissionsDTO == null || !permissionsDTO.isCanValidateVariants()){
            throw new AuthorizationException();
        }
        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();
        
        VCFSample sample = vcfService.retrieveVCFSampleEntity(variantSampleDTO.getSample().getDbId());
        if (sample == null) throw new ControllerException("Sample with id "+variantSampleDTO.getSample().getDbId()+" not found");
        if (!userGroupEntity.getVcfList().contains(sample.getVcf())){
            throw new AuthorizationException("User is not allowed to access the VCF");
        }
        
        VariantModel variantModel = variantService.getVariant(variantSampleDTO.getVariantModelRef());
        if (variantModel == null) throw new ControllerException("Variant model with id "+variantSampleDTO.getVariantModelRef()+" not found");

        try {
            annotationService.annotateVariantSample(
                    variantSampleDTO.getRefGenome(),
                    variantSampleDTO.getHgvsg(),
                    variantSampleDTO.getHgvsc(),
                    variantSampleDTO.getGene(),
                    sample,
                    user,
                    userGroupEntity,
                    variantSampleDTO.getValidationStatus(),
                    variantModel
            );
        } catch (RDBMSServiceException e) {
            throw new ControllerException(e);
        }

    }

    public void deleteVariantSampleAnnotation(Integer annotationId, String userName, String userGroup) throws AuthorizationException{
        PermissionsDTO permissionsDTO = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissionsDTO == null || !permissionsDTO.isCanValidateVariants()){
            throw new AuthorizationException();
        }
        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();

        VariantSampleAnnotation annotation = annotationService.retrieveVariantSampleAnnotation(annotationId);
        if (annotation!=null && annotation.getGroup().equals(userGroupEntity)){
            annotationService.invalidateVariantSampleAnnotation(annotation, user);
        }
    }

    public List<ConditionTermDTO> addConditionToSample(String userName, String userGroup, Integer sampleId, Integer conditionId)  throws AuthorizationException, ControllerException {
        PermissionsDTO permissionsDTO = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissionsDTO == null || !permissionsDTO.isCanAnnotatePathogenicity()){
            throw new AuthorizationException();
        }
        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();

        VCFSample sample = vcfService.retrieveVCFSampleEntity(sampleId);
        if (sample == null) throw new ControllerException("Sample with id "+sampleId+" not found");
        if (!userGroupEntity.getVcfList().contains(sample.getVcf())){
            throw new AuthorizationException("User is not allowed to access the VCF");
        }

        ConditionDictionary condition = annotationService.getConditionTerm(conditionId);
        if (condition == null) throw new ControllerException("Annotation dictionary with id "+conditionId+" not found");

        annotationService.addConditionToSample(sample, condition, user,userGroupEntity);
        
        return annotationService.getConditionsForSample(sample, userGroupEntity);
        
    }

    public List<ConditionTermDTO> removeConditionFromSample(String userName, String userGroup, Integer sampleId, Integer conditionId) throws AuthorizationException, ControllerException{
        PermissionsDTO permissionsDTO = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissionsDTO == null || !permissionsDTO.isCanValidateVariants()){
            throw new AuthorizationException();
        }
        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();

        VCFSample sample = vcfService.retrieveVCFSampleEntity(sampleId);
        if (sample == null) throw new ControllerException("Sample with id "+sampleId+" not found");
        if (!userGroupEntity.getVcfList().contains(sample.getVcf())){
            throw new AuthorizationException("User is not allowed to access the VCF");
        }

        ConditionDictionary condition = annotationService.getConditionTerm(conditionId);
        if (condition == null) throw new ControllerException("Annotation dictionary with id "+conditionId+" not found");


        SampleCondition annotation = annotationService.retrieveSampleCondition(sample, condition, userGroupEntity);
        if (annotation!=null){
            annotationService.invalidateSampleCondition(annotation, user);
            
        }

        return annotationService.getConditionsForSample(sample, userGroupEntity);
    }

    public List<ConditionTermDTO> getSampleConditions(String userName, String userGroup, Integer sampleId)  throws AuthorizationException, ControllerException {
        PermissionsDTO permissionsDTO = permissionService.getPermissionsForUser(userName, userGroup);
        if (permissionsDTO == null || !permissionsDTO.isCanAnnotatePathogenicity()){
            throw new AuthorizationException();
        }
        User user = userService.getUserOrNull(userName);
        UserGroup userGroupEntity = user.getUserRoles().stream().map(UserRole::getGroup).filter(
                (UserGroup group) -> group.getGroup().equalsIgnoreCase(userGroup)
        ).findFirst().get();

        VCFSample sample = vcfService.retrieveVCFSampleEntity(sampleId);
        if (sample == null) throw new ControllerException("Sample with id "+sampleId+" not found");
        if (!userGroupEntity.getVcfList().contains(sample.getVcf())){
            throw new AuthorizationException("User is not allowed to access the VCF");
        }

        return annotationService.getConditionsForSample(sample, userGroupEntity);
    }

   

    public List<String> getAnnotationDictionaries(){
        return annotationService.getAnnotationDictionaries();
    }

    public List<AnnotationTermDTO> getAnnotationDictionaryTerm(String annotationDictionary){
        return annotationService.getAnnotationDictionaryTerms(annotationDictionary).stream().map(
                AnnotationTermDTO::new
        ).collect(Collectors.toList());
    }
    
    public List<String> getConditionDictionaries(){
        return annotationService.getConditionDictionaries();
    }

    public ConditionTermResponseDTO getConditionTerms(String dictionary, String queryString, Integer first, Integer pageSize){

        return new ConditionTermResponseDTO(
                annotationService.searchConditionTerm(dictionary,queryString,first, pageSize).stream().map(
                    ConditionTermDTO::new
                ).collect(Collectors.toList()),
                annotationService.searchConditionTermCount(dictionary,queryString)
        );
    }

    public List<InheritanceTermDTO> getInheritanceDictionaryTerm(String annotationDictionary){
        return annotationService.getInheritanceDictionaryTerms(annotationDictionary).stream().map(
                InheritanceTermDTO::new
        ).collect(Collectors.toList());
    }
}
