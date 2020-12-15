/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.mongo.aggregations;

import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 *
 * @author dbarreca
 */
public class VariantResult implements Serializable {
    
    @BsonId
    private ObjectId id;
    
    @BsonProperty(VariantModel.VARIANT_ID)
    private String variantId;

    public VariantResult() {
    }

    public VariantResult(ObjectId id, String variantId) {
        this.id = id;
        this.variantId = variantId;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }
    
    
}
