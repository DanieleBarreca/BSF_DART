/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.parser;
import org.open.medgen.dart.core.model.query.FullQuery;
import org.open.medgen.dart.core.model.query.QueryRule;
import org.open.medgen.dart.core.service.mongo.exception.QueryParsingException;

/**
 *
 * @author dbarreca
 */

public class QueryValidator {
    
    public static void validate(FullQuery query) throws QueryParsingException{
        
        if (query == null) throw new QueryParsingException("Null Query");
        if (query.getVcfFileId()==null) throw new QueryParsingException("Null Vcf Id");        
        if (query.getSampleRefId() == null) throw new QueryParsingException("Null Sample");
         
        validateJsonRule(((FullQuery) query).getQueryFilter().getFilter());
              
    }
    
    public static void validateJsonRule(QueryRule jsonQuery) throws QueryParsingException {
        if (jsonQuery == null) return;
        
        if (jsonQuery.isGroup()) {
            if (jsonQuery.getCondition() == null) {
                throw new QueryParsingException("Empty Condition for group query");
            }
            for (QueryRule subQuery: jsonQuery.getRules()){
                validateJsonRule(subQuery);
            }
        }else{
            if (jsonQuery.getOperator()==null)
                throw new QueryParsingException("Empty Condition for filter query");
            if (jsonQuery.getField()==null)
                throw new QueryParsingException("Empty Field for filter query");
            if (jsonQuery.getType()==null)
                throw new QueryParsingException("Empty Type for filter query");
            
            BsonQueryParser.getValue(jsonQuery);
        }
    }
    
}
