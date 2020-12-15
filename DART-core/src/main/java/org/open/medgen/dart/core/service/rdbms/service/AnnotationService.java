package org.open.medgen.dart.core.service.rdbms.service;

import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.dto.annotation.ConditionTermDTO;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.*;
import org.open.medgen.dart.core.model.rdbms.entity.enums.ValidationStatus;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import org.open.medgen.dart.core.service.rdbms.dao.AnnotationDAO;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AnnotationService {
    
    @Inject
    private AnnotationDAO annotationDAO;
    
    public void annotateVariant(String refGenome, 
                                String hgvsg, 
                                String hgvsc, 
                                String gene,
                                ConditionDictionary condition,
                                InheritanceDictionary inheritance,
                                User user, 
                                UserGroup userGroup, 
                                VCFSample sample,
                                AnnotationDictionary annotationDictionary,
                                VariantModel variantModel) throws RDBMSServiceException {
        
        Variant variant = annotationDAO.retrieveOrSaveVariant(refGenome, hgvsg, hgvsc, gene);
        VariantAnnotation exisitngAnnotation = annotationDAO.retrieveAnnotationForVariantAndCondition(variant, userGroup, condition);

        boolean createAnnotation = (exisitngAnnotation==null);
        if (
                !createAnnotation && 
                (!exisitngAnnotation.getAnnotation().equals(annotationDictionary) || !exisitngAnnotation.getInheritance().equals(inheritance))
        ){
            this.invalidateAnnotation(exisitngAnnotation, user);
            createAnnotation= true;
        }
        
        if (createAnnotation) {
            VariantAnnotation newAnnotation = new VariantAnnotation();
            newAnnotation.setVariant(variant);
            newAnnotation.setDateFrom(new Date());
            newAnnotation.setGroup(userGroup);
            newAnnotation.setUserFrom(user);
            newAnnotation.setAnnotation(annotationDictionary);
            newAnnotation.setCondition(condition);
            newAnnotation.setInheritance(inheritance);
            newAnnotation.setSupportingData(variantModel);

            annotationDAO.persistAnnotation(newAnnotation);
        }

        annotationDAO.retrieveOrPersistVariantSample(variant, sample);
        
    }

    public void annotateVariantSample(
            String refGenome,
            String hgvsg,
            String hgvsc,
            String gene,
            VCFSample sample,
            User user,
            UserGroup userGroup,
            ValidationStatus validationStatus,
            VariantModel variantModel) throws RDBMSServiceException {

        Variant variant = annotationDAO.retrieveOrSaveVariant(refGenome, hgvsg, hgvsc, gene);
        VariantSample variantSample = annotationDAO.retrieveOrPersistVariantSample(variant, sample);
        VariantSampleAnnotation existingAnnotation = annotationDAO.retrieveAnnotationForVariantSample(variantSample, userGroup);

        boolean createAnnotation = (existingAnnotation==null);
        if (!createAnnotation && 
                (!existingAnnotation.getValidationStatus().equals(validationStatus))
        ){
            this.invalidateVariantSampleAnnotation(existingAnnotation, user);
            createAnnotation= true;
        }

        if (createAnnotation) {
            VariantSampleAnnotation newAnnotation = new VariantSampleAnnotation();
            newAnnotation.setVariantSample(variantSample);
            newAnnotation.setDateFrom(new Date());
            newAnnotation.setGroup(userGroup);
            newAnnotation.setUserFrom(user);
            newAnnotation.setValidationStatus(validationStatus);

            annotationDAO.persistVariantSampleAnnotation(newAnnotation);
        }
        
    }
    
    public List<VariantAnnotation> getExistingAnnotationsForVariant(String refGenome, String hgvsg, String hgvsc, String userGroup, Date asOf) {
        return annotationDAO.retrieveVariantAnnotations(refGenome, hgvsg, hgvsc, userGroup, asOf);
    }

    public VariantAnnotation retrieveAnnotation(Integer annotationId) {
        return annotationDAO.getVariantAnnotationOrNull(annotationId);
    }

    public void invalidateAnnotation(VariantAnnotation annotation, User user){
        annotation.setUserTo(user);
        annotation.setDateTo(new Date());
    }


    public VariantSampleAnnotation getExistingAnnotationsForVariantSample(String refGenome, String hgvsg, String hgvsc, Integer sampleId, String userGroup, Date asOf) {
        return annotationDAO.retrieveAnnotationForVariantSample(refGenome, hgvsg, hgvsc, sampleId, userGroup, asOf);
    }

    public VariantSampleAnnotation retrieveVariantSampleAnnotation(Integer annotationId) {
        return annotationDAO.getVariantSampleAnnotationOrNull(annotationId);
    }

    
    public void invalidateVariantSampleAnnotation(VariantSampleAnnotation annotation, User user){
        annotation.setUserTo(user);
        annotation.setDateTo(new Date());
    }
    
    public List<String> getAnnotationDictionaries(){
        return annotationDAO.getAnnotationDictionaries();
    }
    
    public AnnotationDictionary getAnnotationDictionaryTerm(Integer annotationDictionaryId) {
        return annotationDAO.getAnnotationDictionaryOrNull(annotationDictionaryId);
    }
      
    public List<AnnotationDictionary> getAnnotationDictionaryTerms(String annotationType) {
        return annotationDAO.getAnnotationDictionaryByType(annotationType);
    }

    public List<String> getConditionDictionaries(){
        return annotationDAO.getConditionDictionaries();
    }

    public List<ConditionDictionary> searchConditionTerm(String dictionaryName, String queryString, Integer first, Integer pageSize) {
        return annotationDAO.queryConditions(dictionaryName, queryString, first, pageSize);
    }

    public Long searchConditionTermCount(String dictionaryName, String queryString) {
        return annotationDAO.queryConditionsCount(dictionaryName, queryString);
    }

    public ConditionDictionary getConditionTerm(Integer conditionDictionaryId) {
        return annotationDAO.getConditionDictionaryOrNull(conditionDictionaryId);
    }

    public InheritanceDictionary getInheritanceTerm(Integer inheritanceTermId) {
        return annotationDAO.getInheritanceDictionaryOrNull(inheritanceTermId);
    }

    public List<InheritanceDictionary> getInheritanceDictionaryTerms(String annotationType) {
        return annotationDAO.getInheritanceDictionaryByType(annotationType);
    }

    public void addConditionToSample(VCFSample sample, ConditionDictionary condition, User  user, UserGroup userGroup) {
        SampleCondition sampleCondition = annotationDAO.retrieveSampleCondition(sample, condition,userGroup);
        if (sampleCondition == null) {
            sampleCondition = new SampleCondition();
            sampleCondition.setSample(sample);
            sampleCondition.setCondition(condition);
            sampleCondition.setUserFrom(user);
            sampleCondition.setDateFrom(new Date());
            sampleCondition.setGroup(userGroup);

            annotationDAO.persistSampleCondition(sampleCondition);
        }
        
        annotationDAO.getEm().flush();
    }

    public List<ConditionTermDTO> getConditionsForSample(VCFSample sample, UserGroup userGroup) {
        List<SampleCondition> sampleConditions = annotationDAO.retrieveConditionsForSample(sample, userGroup);
        return sampleConditions.stream().map(
                (SampleCondition sampleCondition) -> new ConditionTermDTO(sampleCondition.getCondition())
        ).collect(Collectors.toList());
        
    }

    public SampleCondition retrieveSampleCondition(VCFSample sample, ConditionDictionary condition, UserGroup userGroup){
        return annotationDAO.retrieveSampleCondition(sample, condition, userGroup);
    }
    
    public void invalidateSampleCondition(SampleCondition sampleCondition, User user){
        sampleCondition.setUserTo(user);
        sampleCondition.setDateTo(new Date());
        
        annotationDAO.getEm().flush();
        
    }
}
