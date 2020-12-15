package org.open.medgen.dart.core.controller.annotation;

import org.open.medgen.dart.core.model.query.FullQuery;
import org.open.medgen.dart.core.model.query.RelatedSampleInfo;
import org.open.medgen.dart.core.service.rdbms.service.AnnotationService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;


@Stateless
public class AnnotatorFactory {

    @Inject
    AnnotationService annotationService;

    public Annotator getAnnotator(FullQuery theQuery, String refGenome, String userGroup, Integer sampleId){
        return this.getAnnotator(theQuery,refGenome, userGroup, sampleId, null);
    }
    
    public Annotator getAnnotator(FullQuery theQuery, String refGenome, String userGroup, Integer sampleId, Date asof){
        
        Annotator result = new DbAnnotationsAnnotator(annotationService,refGenome, userGroup, sampleId, asof);
        
        if (theQuery.getRelatedSamples()!=null && theQuery.getRelatedSamples().size()!=0) {
            Annotator relatedSamplesAnnotator = new RelatedSamplesAnnotator(theQuery.getRelatedSamples());
            result= new CompositeAnnotator(result, relatedSamplesAnnotator);
        }
       
        return result;
    }
}
