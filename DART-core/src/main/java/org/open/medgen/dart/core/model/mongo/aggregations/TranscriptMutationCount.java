/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.mongo.aggregations;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 *
 * @author dbarreca
 */
public class TranscriptMutationCount {
    public final static String POS = "pos";
    public final static String IMPACT = "impact";
    public final static String COUNT = "count";
    
    public static enum Impact {
        HIGH,
        MODERATE,
        LOW
    }
    
    public static class MutationMetaData{
        
        @BsonProperty(value=POS)
        private Integer pos;
        
        @BsonProperty(value=IMPACT)
        private Impact impact;

        public MutationMetaData() {
        }

        public Integer getPos() {
            return pos;
        }

        public void setPos(Integer pos) {
            this.pos = pos;
        }

        public Impact getImpact() {
            return impact;
        }

        public void setImpact(Impact impact) {
            this.impact = impact;
        }        
        
    }
    
    @BsonId
    private MutationMetaData id;
    
    @BsonProperty(value=COUNT)
    private Integer count;

    public MutationMetaData getId() {
        return id;
    }

    public void setId(MutationMetaData id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    
    
}
