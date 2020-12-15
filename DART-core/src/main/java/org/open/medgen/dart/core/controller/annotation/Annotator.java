package org.open.medgen.dart.core.controller.annotation;

import org.open.medgen.dart.core.model.mongo.variant.VariantModel;

public interface Annotator {

    public void annotate(VariantModel variant);
}
