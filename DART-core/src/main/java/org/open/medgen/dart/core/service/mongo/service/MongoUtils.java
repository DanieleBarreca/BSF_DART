/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.service;

import java.util.Arrays;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author dbarreca
 */
public interface MongoUtils {
    
    public static final String ID = "_id";    
    public static final String TRUNC = "$trunc";
    public static final String DIVIDE = "$divide";
    public static final String CONCAT = "$concat";
    public static final String SUBSTR = "$substr";
    public static final String FILTER = "$filter";
    public static final String FILTER_INPUT= "input";
    public static final String FILTER_AS = "as";
    public static final String FILTER_COND = "cond";
    public static final String AND = "$and";
    public static final String OR = "$or";
    public static final String LTE = "$lte";
    public static final String GTE = "$gte";
    
    public static Bson substr(Bson expr, Integer start, Integer end){
        return new Document(SUBSTR, Arrays.asList(expr, start, end));
    }
    
    public static Bson trunc(Bson expr){
        return new Document(TRUNC, expr);
    }
    
    public static Bson divide(String fieldValue, Integer divisor){
        return new Document(DIVIDE, Arrays.asList(fieldValue, divisor));
    }
    
    public static Bson concat (Object ...args){
        return new Document(CONCAT,Arrays.asList(args));
    }
    
    public static String valueOf(String field){
        return "$"+field;
    }
    
    public static String toVar(String field){
        return "$$"+field;
    }
    
    public static Bson filter(String arrayValues, String asField, Bson condition){
        return new Document(
            FILTER,
            new Document(FILTER_INPUT,arrayValues)
                .append(FILTER_AS, asField)
                .append(FILTER_COND, condition)
        );
    }
    
    public static Bson genericExpr(String operator, Object...args ){
        return new Document(operator, Arrays.asList(args));
    }
    
}

