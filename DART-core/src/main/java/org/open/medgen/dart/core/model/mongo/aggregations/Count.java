/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.mongo.aggregations;

import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 *
 * @author dbarreca
 */
public class Count {
    
    @BsonProperty("count")
    private long count;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
    
    
}
