package org.open.medgen.dart.core.controller.annotation;

import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.dto.annotation.PathogenicityAnnotationDTO;
import org.open.medgen.dart.core.model.rdbms.dto.annotation.VariantSampleAnnotationDTO;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.VariantAnnotation;
import org.open.medgen.dart.core.model.rdbms.entity.annotations.VariantSampleAnnotation;
import org.open.medgen.dart.core.service.rdbms.service.AnnotationService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DbAnnotationsAnnotator implements Annotator {
    
    private final static String DB_PATHOGENICITY_DATA = "DB_PATHOGENICITY";
    private final static String DB_VALIDATION_DATA = "DB_VALIDATION";
    
    private final AnnotationService annotationService;
    private final String refGenome;
    private final String userGroup;
    private final Integer sample;
    private final Date asOf;

    public DbAnnotationsAnnotator(AnnotationService annotationService, String refGenome, String userGroup, Integer sampleId, Date asOf) {
        this.annotationService = annotationService;
        this.refGenome = refGenome;
        this.userGroup = userGroup;
        this.sample = sampleId;
        this.asOf= asOf;
    }

    @Override
    public void annotate(VariantModel variant ) {
        
        List<VariantAnnotation> annotationList = this.annotationService.getExistingAnnotationsForVariant(
                refGenome,
                variant.getAttributeAsString(VariantModel.GENOMIC_CHANGE_FIELD),
                variant.getAttributeAsString(VariantModel.CODING_CHANGE_FIELD),
                userGroup,
                asOf
        );

        VariantSampleAnnotation variantSampleAnnotation = this.annotationService.getExistingAnnotationsForVariantSample(
                refGenome,
                variant.getAttributeAsString(VariantModel.GENOMIC_CHANGE_FIELD),
                variant.getAttributeAsString(VariantModel.CODING_CHANGE_FIELD),
                sample,
                userGroup,
                asOf
        );
        
        variant.getAnnotations().setAttribute(
                DB_PATHOGENICITY_DATA,
                annotationList.stream().map(PathogenicityAnnotationDTO::new).collect(Collectors.toList())
        );

        if (variantSampleAnnotation!=null) {
            variant.getAnnotations().setAttribute(
                    DB_VALIDATION_DATA,
                    new VariantSampleAnnotationDTO(variantSampleAnnotation)
            );
        }
        
    }
}
