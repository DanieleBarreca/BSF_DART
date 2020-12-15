package org.open.medgen.dart.core.controller.annotation;

import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.mongo.variant.Zygosity;
import org.open.medgen.dart.core.model.query.RelatedSampleInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RelatedSamplesAnnotator implements Annotator {

    private final List<RelatedSampleInfo> relatedSampleInfos;


    public final static String INHERITANCE =  "INHERITANCE";

    public enum RelatedSampleZygosity {
        HOM,
        HET,
        NONE
    }

    public enum RelatedSampleSex {
        M,
        F,
        UNK
    }

    protected RelatedSamplesAnnotator(List<RelatedSampleInfo> relatedSampleInfos) {
        this.relatedSampleInfos=relatedSampleInfos;
    }

    public void annotate(VariantModel result) {
        Map<String,RelatedSampleAnnotation> relatedSampleAnnotations= new HashMap<>();
        
        for (RelatedSampleInfo relatedSampleInfo: relatedSampleInfos){
            
            if (relatedSampleInfo.getSample() != null && !relatedSampleInfo.getSample().isEmpty()) {

                final RelatedSampleZygosity relatedSampleZygosity;
                if (result.getOtherSamples().contains(relatedSampleInfo.getSample() + ":HET")) {
                    relatedSampleZygosity = RelatedSampleZygosity.HET;
                } else if (result.getOtherSamples().contains(relatedSampleInfo.getSample() + ":HOM")) {
                    relatedSampleZygosity = RelatedSampleZygosity.HOM;
                } else {
                    relatedSampleZygosity = RelatedSampleZygosity.NONE;
                }

                relatedSampleAnnotations.put(
                        relatedSampleInfo.getSample(),
                        new RelatedSampleAnnotation(
                                relatedSampleZygosity,
                                relatedSampleInfo.getSex(),
                                relatedSampleInfo.isAffected())
                );
            }
            
             
        }

        result.getAnnotations().put(INHERITANCE,relatedSampleAnnotations);
        
    }
}
