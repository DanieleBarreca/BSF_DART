package org.open.medgen.dart.core.controller.annotation;

import org.open.medgen.dart.core.model.mongo.variant.VariantModel;

import java.util.LinkedList;
import java.util.List;

public class CompositeAnnotator implements Annotator {
    
    private final List<Annotator> annotatorList = new LinkedList<>();

    public CompositeAnnotator(Annotator... annotators) {
        for (Annotator annotator: annotators) {
            annotatorList.add(annotator);   
        }
    }

    @Override
    public void annotate(VariantModel variant) {
        for (Annotator annotator: annotatorList) {
            annotator.annotate(variant);
        }
    }
}
