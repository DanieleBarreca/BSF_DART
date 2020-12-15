package org.open.medgen.dart.core.service.rdbms.dao;

import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup_;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.*;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample_;
import org.open.medgen.dart.core.service.rdbms.service.exception.RDBMSServiceException;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Stateless
public class AnnotationDAO extends DAO{
    
    private static final Pattern HGVSg_PATTERN = Pattern.compile("(.*):(g|m).([0-9]+)(([ACGT]+>[ACGT]+)|((_[0-9]+)?(dup|\\[[0-9]+\\]|del|inv|((ins|delins)[ACGT]+))))");
    
    
    public Variant getVariantOrNull(String refGenome, String hgvsg, String hgvsc) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Variant> variantCriteria = cb.createQuery(Variant.class);

        Root<Variant> variant = variantCriteria.from(Variant.class);
        if (hgvsc!=null && !hgvsc.isEmpty()) {
            variantCriteria.where(cb.and(
                    cb.equal(variant.get(Variant_.refGenome), refGenome),
                    cb.equal(variant.get(Variant_.hgvsG), hgvsg),
                    cb.equal(variant.get(Variant_.hgvsC), hgvsc)
            ));
        }else {
            variantCriteria.where(cb.and(
                    cb.equal(variant.get(Variant_.refGenome), refGenome),
                    cb.equal(variant.get(Variant_.hgvsG), hgvsg),
                    cb.or(
                            cb.isNull(variant.get(Variant_.hgvsC)),
                            cb.equal(variant.get(Variant_.hgvsC), "")
                    )
            ));
        }

        try{
            return em.createQuery(variantCriteria).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public Variant retrieveOrSaveVariant(String refGenome, String hgvsg, String hgvsc, String gene) throws RDBMSServiceException {

        Variant theVariant = getVariantOrNull(refGenome, hgvsg, hgvsc);
        
        if (theVariant == null) {

            Matcher hgvsgMatcher = HGVSg_PATTERN.matcher(hgvsg);

            if (!hgvsgMatcher.matches()){
                throw new RDBMSServiceException("Invalid HGVSg string: "+hgvsg);
            }

            theVariant = new Variant();
            theVariant.setRefGenome(refGenome);
            theVariant.setContig(hgvsgMatcher.group(1));
            theVariant.setPos(Integer.parseInt(hgvsgMatcher.group(3)));
            theVariant.setHgvsC(hgvsc);
            theVariant.setHgvsG(hgvsg);
            theVariant.setGene(gene);
            
            em.persist(theVariant);
        }
        return theVariant;

    }

    public VariantAnnotation getVariantAnnotationOrNull(Integer id) {

        return em.find(VariantAnnotation.class, id);
    }

    public VariantAnnotation retrieveAnnotationForVariantAndCondition(Variant variant, UserGroup userGroup, ConditionDictionary condition) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<VariantAnnotation> annotationCriteria = cb.createQuery(VariantAnnotation.class);

        Root<VariantAnnotation> annotation = annotationCriteria.from(VariantAnnotation.class);
        
        annotationCriteria.where(cb.and(
                cb.equal(annotation.get(VariantAnnotation_.variant), variant),
                cb.equal(annotation.get(VariantAnnotation_.group), userGroup),
                cb.isNull(annotation.get(VariantAnnotation_.userTo)),
                cb.equal(annotation.get(VariantAnnotation_.condition), condition)
        ));

        return em.createQuery(annotationCriteria).setMaxResults(1).getResultList().stream().findFirst().orElse(null);

    }

    public List<VariantAnnotation> retrieveVariantAnnotations(String refGenome, String hgvsg, String hgvsc, String userGroup, Date asOf) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<VariantAnnotation> annotationCriteria = cb.createQuery(VariantAnnotation.class);

        Root<VariantAnnotation> annotation = annotationCriteria.from(VariantAnnotation.class);
        Join<VariantAnnotation, Variant> variant = annotation.join(VariantAnnotation_.variant);
        Join<VariantAnnotation, AnnotationDictionary> annotationTerm = annotation.join(VariantAnnotation_.annotation);
        Join<VariantAnnotation, UserGroup> group = annotation.join(VariantAnnotation_.group);


        List<Predicate> filters = new LinkedList<>();
        filters.add(cb.equal(variant.get(Variant_.refGenome), refGenome));
        filters.add(cb.equal(variant.get(Variant_.hgvsG), hgvsg));
        filters.add(cb.equal(variant.get(Variant_.hgvsC), hgvsc));
        filters.add(cb.equal(group.get(UserGroup_.group), userGroup));

        if (asOf == null){
            filters.add(cb.isNull(annotation.get(VariantAnnotation_.dateTo)));
        }else{
            filters.add(cb.lessThanOrEqualTo(annotation.get(VariantAnnotation_.dateFrom), asOf));
            filters.add(
                    cb.or(
                            cb.isNull(annotation.get(VariantAnnotation_.dateTo)),
                            cb.greaterThanOrEqualTo(annotation.get(VariantAnnotation_.dateTo), asOf)
                    )
            );
        }

        Predicate[] predicates = new Predicate[filters.size()];
        annotationCriteria.where(cb.and( filters.toArray(predicates) ));
        
        annotationCriteria.orderBy(
                cb.asc(annotationTerm.get(AnnotationDictionary_.rank)),
                cb.asc(annotationTerm.get(AnnotationDictionary_.annotationType))
        );

        return em.createQuery(annotationCriteria).getResultList();

    }
    
    
    public void persistAnnotation(VariantAnnotation annotation) {
        em.persist(annotation);
    }

    public VariantSample getVariantSampleOrNull(Variant variant, VCFSample vcfSample) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<VariantSample> variantSampleCriteria = cb.createQuery(VariantSample.class);

        Root<VariantSample> variantSample = variantSampleCriteria.from(VariantSample.class);
        variantSampleCriteria.where(cb.and(
                cb.equal(variantSample.get(VariantSample_.variant), variant),
                cb.equal(variantSample.get(VariantSample_.sample), vcfSample)
        ));
        
        try{
            return em.createQuery(variantSampleCriteria).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public VariantSampleAnnotation getVariantSampleAnnotationOrNull(Integer id) {

        return em.find(VariantSampleAnnotation.class, id);
    }
    
    
    public VariantSample retrieveOrPersistVariantSample(Variant variant, VCFSample vcfSample){
        VariantSample variantSample = getVariantSampleOrNull(variant,vcfSample);
        if (variantSample==null){
            variantSample = new VariantSample();
            variantSample.setSample(vcfSample);
            variantSample.setVariant(variant);
            em.persist(variantSample);

            em.flush();
        }
        
        return variantSample;
    }

    public VariantSampleAnnotation retrieveAnnotationForVariantSample(VariantSample variantSample, UserGroup userGroup) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<VariantSampleAnnotation> annotationCriteria = cb.createQuery(VariantSampleAnnotation.class);

        Root<VariantSampleAnnotation> annotation = annotationCriteria.from(VariantSampleAnnotation.class);

        annotationCriteria.where(cb.and(
                cb.equal(annotation.get(VariantSampleAnnotation_.variantSample), variantSample),
                cb.equal(annotation.get(VariantSampleAnnotation_.group), userGroup),
                cb.isNull(annotation.get(VariantSampleAnnotation_.userTo))
        ));

        return em.createQuery(annotationCriteria).setMaxResults(1).getResultList().stream().findFirst().orElse(null);

    }

    public VariantSampleAnnotation retrieveAnnotationForVariantSample(String refGenome, String hgvsg, String hgvsc, Integer sampleId, String groupName, Date asOf) {
        
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<VariantSampleAnnotation> annotationCriteria = cb.createQuery(VariantSampleAnnotation.class);

        Root<VariantSampleAnnotation> annotation = annotationCriteria.from(VariantSampleAnnotation.class);
        
        Join<VariantSampleAnnotation, UserGroup>  userGroup = annotation.join(VariantSampleAnnotation_.group);
        Join<VariantSampleAnnotation, VariantSample>  variantSample = annotation.join(VariantSampleAnnotation_.variantSample);
        Join<VariantSample, Variant> variant = variantSample.join(VariantSample_.variant);
        Join<VariantSample, VCFSample>  sample = variantSample.join(VariantSample_.sample);

        List<Predicate> filters = new LinkedList<>();
        filters.add(cb.equal(variant.get(Variant_.refGenome), refGenome)); 
        filters.add(cb.equal(variant.get(Variant_.hgvsG), hgvsg));
        filters.add(cb.equal(variant.get(Variant_.hgvsC), hgvsc));
        filters.add(cb.equal(userGroup.get(UserGroup_.group), groupName));
        filters.add(cb.equal(sample.get(VCFSample_.sampleId), sampleId));
        
        if (asOf == null){
            filters.add(cb.isNull(annotation.get(VariantSampleAnnotation_.dateTo)));
        }else{
            filters.add(cb.lessThanOrEqualTo(annotation.get(VariantSampleAnnotation_.dateFrom), asOf));
            filters.add(
                    cb.or(
                            cb.isNull(annotation.get(VariantSampleAnnotation_.dateTo)),
                            cb.greaterThanOrEqualTo(annotation.get(VariantSampleAnnotation_.dateTo), asOf)
                    )
            );
        }

        Predicate[] predicates = new Predicate[filters.size()];
        annotationCriteria.where(cb.and( filters.toArray(predicates) ));

        return em.createQuery(annotationCriteria).setMaxResults(1).getResultList().stream().findFirst().orElse(null);

    }

    public void persistVariantSampleAnnotation(VariantSampleAnnotation annotation) {
        em.persist(annotation);
    }
    
    public List<String> getAnnotationDictionaries(){
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        CriteriaQuery<String> dictinaryTermCriteria = cb.createQuery(String.class);

        Root<AnnotationDictionary> dictionary = dictinaryTermCriteria.from(AnnotationDictionary.class);
        dictinaryTermCriteria.select(dictionary.get(AnnotationDictionary_.annotationType));
        dictinaryTermCriteria.distinct(true);
        
        return em.createQuery(dictinaryTermCriteria).getResultList();
    }

    public AnnotationDictionary getAnnotationDictionaryOrNull(Integer id) {

        return em.find(AnnotationDictionary.class, id);
    }

    public List<AnnotationDictionary> getAnnotationDictionaryByType(String type) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<AnnotationDictionary> annotationDictionaryCriteria = cb.createQuery(AnnotationDictionary.class);

        Root<AnnotationDictionary> annotationDict = annotationDictionaryCriteria.from(AnnotationDictionary.class);
        annotationDictionaryCriteria.where(cb.equal(annotationDict.get(AnnotationDictionary_.annotationType),type));
        
        return em.createQuery(annotationDictionaryCriteria).getResultList();

    }

    public List<String> getConditionDictionaries(){

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<String> dictinaryTermCriteria = cb.createQuery(String.class);

        Root<ConditionDictionary> dictionary = dictinaryTermCriteria.from(ConditionDictionary.class);
        dictinaryTermCriteria.select(dictionary.get(ConditionDictionary_.dictionaryName));
        dictinaryTermCriteria.distinct(true);

        return em.createQuery(dictinaryTermCriteria).getResultList();
    }

    public ConditionDictionary getConditionDictionaryOrNull(Integer id) {
        if (id == null) return null;
        
        return em.find(ConditionDictionary.class, id);
    }

    public InheritanceDictionary getInheritanceDictionaryOrNull(Integer id) {
        if (id == null) return null;

        return em.find(InheritanceDictionary.class, id);
    }

    public List<InheritanceDictionary> getInheritanceDictionaryByType(String type) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<InheritanceDictionary> inheritanceDictionaryCriteria = cb.createQuery(InheritanceDictionary.class);

        Root<InheritanceDictionary> inheritanceDict = inheritanceDictionaryCriteria.from(InheritanceDictionary.class);
        inheritanceDictionaryCriteria.where(cb.equal(inheritanceDict.get(InheritanceDictionary_.annotationType),type));

        return em.createQuery(inheritanceDictionaryCriteria).getResultList();

    }

    public List<ConditionDictionary> queryConditions(String dictionaryName, String queryTerm, Integer first, Integer pageSize){
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<ConditionDictionary> dictinaryTermCriteria = cb.createQuery(ConditionDictionary.class);

        Root<ConditionDictionary> dictionary = dictinaryTermCriteria.from(ConditionDictionary.class);
        if (queryTerm != null && !queryTerm.isEmpty()){
            String queryString = String.format("%%%s%%",queryTerm);
            dictinaryTermCriteria.where(
                    cb.and(
                            cb.or(
                                    cb.like(dictionary.get(ConditionDictionary_.conditionLabel), queryString),
                                    cb.like(dictionary.get(ConditionDictionary_.conditionId), queryString)
                                    ),
                            cb.equal(dictionary.get(ConditionDictionary_.dictionaryName),dictionaryName)
                    )
            );
        }else{
            dictinaryTermCriteria.where(
                    cb.equal(dictionary.get(ConditionDictionary_.dictionaryName),dictionaryName)
            );
        }
        
        return em.createQuery(dictinaryTermCriteria).setFirstResult(first).setMaxResults(pageSize).getResultList();
    }
  
    public Long queryConditionsCount(String dictionaryName, String queryTerm){

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> dictinaryTermCriteria = cb.createQuery(Long.class);

        Root<ConditionDictionary> dictionary = dictinaryTermCriteria.from(ConditionDictionary.class);
        dictinaryTermCriteria.select(cb.count(dictionary));

        if (queryTerm != null && !queryTerm.isEmpty()){
            String queryString = String.format("%%%s%%",queryTerm);
            dictinaryTermCriteria.where(
                    cb.and(
                            cb.or(
                                    cb.like(dictionary.get(ConditionDictionary_.conditionLabel), queryString),
                                    cb.like(dictionary.get(ConditionDictionary_.conditionId), queryString)
                            ),
                            cb.equal(dictionary.get(ConditionDictionary_.dictionaryName),dictionaryName)
                    )
            );
        }else{
            dictinaryTermCriteria.where(
                    cb.equal(dictionary.get(ConditionDictionary_.dictionaryName),dictionaryName)
            );
        }

        return em.createQuery(dictinaryTermCriteria).getSingleResult();
    }

    public SampleCondition retrieveSampleCondition(VCFSample sample, ConditionDictionary condition, UserGroup userGroup) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        

        CriteriaQuery<SampleCondition> sampleConditionCriteria = cb.createQuery(SampleCondition.class);

        Root<SampleCondition> sampleCondition = sampleConditionCriteria.from(SampleCondition.class);
        sampleConditionCriteria.where(cb.and(
                cb.equal(sampleCondition.get(SampleCondition_.sample), sample),
                cb.equal(sampleCondition.get(SampleCondition_.condition), condition),
                cb.equal(sampleCondition.get(SampleCondition_.group), userGroup),
                cb.isNull(sampleCondition.get(SampleCondition_.dateTo))
        ));

        return  em.createQuery(sampleConditionCriteria).setMaxResults(1).getResultList().stream().findFirst().orElse(null);
    }
    
    public void persistSampleCondition(SampleCondition sampleCondition) {
        em.persist(sampleCondition);
    }

    public List<SampleCondition> retrieveConditionsForSample(VCFSample sample, UserGroup userGroup) {
       return  this.retrieveConditionsForSample(sample, userGroup, null);
    }

    public List<SampleCondition> retrieveConditionsForSample(VCFSample sample, UserGroup userGroup, Date asOf) {
        CriteriaBuilder cb = em.getCriteriaBuilder();


        CriteriaQuery<SampleCondition> sampleConditionCriteria = cb.createQuery(SampleCondition.class);

        Root<SampleCondition> sampleCondition = sampleConditionCriteria.from(SampleCondition.class);
        List<Predicate> filters = new LinkedList<>();
        filters.add(cb.equal(sampleCondition.get(SampleCondition_.sample), sample));
        filters.add(cb.equal(sampleCondition.get(SampleCondition_.group), userGroup));
        if (asOf == null){
            filters.add(cb.isNull(sampleCondition.get(SampleCondition_.dateTo)));
        }else{
            filters.add(cb.lessThanOrEqualTo(sampleCondition.get(SampleCondition_.dateFrom), asOf));
            filters.add(
                    cb.or(
                            cb.isNull(sampleCondition.get(SampleCondition_.dateTo)),
                            cb.greaterThanOrEqualTo(sampleCondition.get(SampleCondition_.dateTo), asOf)
                    )
            );
        }
    
        Predicate[] predicates = new Predicate[filters.size()];
        sampleConditionCriteria.where(cb.and( filters.toArray(predicates) ));
        return  em.createQuery(sampleConditionCriteria).getResultList();
    }
}
