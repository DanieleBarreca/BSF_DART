/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.parser;


import org.open.medgen.dart.core.model.query.QueryRule;
import org.open.medgen.dart.core.model.query.constants.FilterType;
import org.open.medgen.dart.core.model.query.constants.GroupCondition;
import org.open.medgen.dart.core.service.mongo.exception.QueryParsingException;
import static com.mongodb.client.model.Filters.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.bson.BsonType;
import org.bson.conversions.Bson;


/**
 *
 * @author dbarreca
 */
public class BsonQueryParser {  
    
    
    public static Bson getCriteria(QueryRule jsonQuery) throws QueryParsingException {

        if (jsonQuery == null) {
            return null;
        }

        Bson result = null;
        if (jsonQuery.isGroup()) {
            GroupCondition queryCondition = jsonQuery.getCondition();

            List<Bson> parsedQueries = new LinkedList();
            for (QueryRule singleQuery : jsonQuery.getRules()) {
                Bson parsedQuery = getCriteria(singleQuery);
                if (parsedQuery != null) {
                    parsedQueries.add(parsedQuery);
                }
            }

            switch (queryCondition) {
                case OR:
                    result = or(parsedQueries);
                    break;
                case AND:
                    result = and(parsedQueries);
                    break;
            }

        } else {
            switch (jsonQuery.getOperator()) {

                case EQUAL:
                    result = eq(jsonQuery.getField(),getValue(jsonQuery));
                    if (jsonQuery.getType().equals(FilterType.BOOLEAN) && !(Boolean) getValue(jsonQuery)){
                        result = or(
                                result,
                                not(exists(jsonQuery.getField())),
                                type(jsonQuery.getField(), BsonType.NULL)
                        );
                    }
                    break;
                case NOT_EQUAL:
                    result = or(
                        ne(jsonQuery.getField(),getValue(jsonQuery)),
                        not(exists(jsonQuery.getField())),
                        type(jsonQuery.getField(), BsonType.NULL)
                    );
                    break;
                case IN:
                    result = in(jsonQuery.getField(), (List) getValue(jsonQuery));
                    break;
                case NOT_IN:
                    result = or(
                        nin(jsonQuery.getField(), (List) getValue(jsonQuery)),
                        not(exists(jsonQuery.getField())),
                        type(jsonQuery.getField(), BsonType.NULL)    
                    );
                    break;
                case LESS:
                    result = or(
                        lt(jsonQuery.getField(), getValue(jsonQuery)),
                        not(exists(jsonQuery.getField())),
                        type(jsonQuery.getField(), BsonType.NULL)    
                    );
                    break;
                case LESS_OR_EQUAL:
                     result = or(
                        lte(jsonQuery.getField(), getValue(jsonQuery)),
                        not(exists(jsonQuery.getField())),
                        type(jsonQuery.getField(), BsonType.NULL)    
                    );
                    break;
                case GREATER:
                    result = gt(jsonQuery.getField(),getValue(jsonQuery));
                    break;
                case GREATER_OR_EQUAL:
                    result = gte(jsonQuery.getField(),getValue(jsonQuery));
                    break;
                case BETWEEN:
                    result = and(
                            gte(jsonQuery.getField(), ((List) getValue(jsonQuery)).get(0)),
                            lte(jsonQuery.getField(), ((List) getValue(jsonQuery)).get(1))
                    );
                    break;
                case NOT_BETWEEN:
                    result = or(
                            lt(jsonQuery.getField(), ((List) getValue(jsonQuery)).get(0)),
                            gt(jsonQuery.getField(), ((List) getValue(jsonQuery)).get(1)),
                            not(exists(jsonQuery.getField())),
                            type(jsonQuery.getField(), BsonType.NULL)
                    );
                    break;
                case BEGINS_WITH:
                    result = regex(jsonQuery.getField(),"^"+getValue(jsonQuery));
                    break;
                case NOT_BEGINS_WITH:
                    result = or(
                            not(regex(jsonQuery.getField(),"^"+getValue(jsonQuery).toString())),
                            not(exists(jsonQuery.getField())),
                            type(jsonQuery.getField(), BsonType.NULL)
                    );
                    break;
                case CONTAINS:
                    result = regex(jsonQuery.getField(),getValue(jsonQuery).toString());
                    break;
                case NOT_CONTAINS:
                     result = or(
                            not(regex(jsonQuery.getField(),getValue(jsonQuery).toString())),
                            not(exists(jsonQuery.getField())),
                            type(jsonQuery.getField(), BsonType.NULL)
                    );
                    break;
                case ENDS_WITH:
                    result = regex(jsonQuery.getField(),getValue(jsonQuery)+"$");
                    break;
                case NOT_ENDS_WITH:
                    result = or(
                            not(regex(jsonQuery.getField(),getValue(jsonQuery).toString()+"$")),
                            not(exists(jsonQuery.getField())),
                            type(jsonQuery.getField(), BsonType.NULL)
                    );
                    break;
                case IS_NULL:
                    result = or(
                            not(exists(jsonQuery.getField())),
                            type(jsonQuery.getField(), BsonType.NULL)
                    );
                    break;
                case IS_NOT_NULL:
                    result = and(
                            exists(jsonQuery.getField()),
                            not(type(jsonQuery.getField(), BsonType.NULL))
                    );
                    break;
                case ARRAY_SIZE:
                    Integer theSize = (Integer) getValue(FilterType.INTEGER, jsonQuery.getValue());
                    if (theSize == 0) {
                        result = or(
                                size(jsonQuery.getField(), theSize),
                                not(exists(jsonQuery.getField())),
                                type(jsonQuery.getField(), BsonType.NULL)
                        );
                    } else {
                        result = size(jsonQuery.getField(), theSize);
                    }
                    break;
                case IS_NOT_EMPTY:
                    result = and(
                            not(size(jsonQuery.getField(), 0)),
                            exists(jsonQuery.getField()),
                            not(type(jsonQuery.getField(), BsonType.NULL))
                    );
                    break;
            }

        }
        
        return result;
    }
    
    
    protected static Object getValue(QueryRule query) throws QueryParsingException{
        try {
            switch (query.getOperator()) {
                case IN:
                case NOT_IN:
                    if (!(query.getValue() instanceof List)) {
                        throw new QueryParsingException("IN AND NOT_IN operators needs a list of values");
                    } else {
                        return getListValue(query.getType(), query.getValue());
                    }
                case BETWEEN:
                case NOT_BETWEEN:
                    if (!(query.getValue() instanceof List)) {
                        throw new QueryParsingException("BETWEEN AND NOT_BETWEEN operators needs a list of 2 values");
                    } else {
                        List returnValue = getListValue(query.getType(), query.getValue());
                        if (returnValue.size() != 2) {
                            throw new QueryParsingException("BETWEEN AND NOT_BETWEEN operators needs a list of 2 values");
                        }
                        return returnValue;
                    }
                case IS_NULL:
                case IS_NOT_NULL:
                case IS_NOT_EMPTY:
                    return null;
                case ARRAY_SIZE:
                    try {
                        return getValue(FilterType.INTEGER, query.getValue());
                    } catch (NumberFormatException e) {
                        throw new QueryParsingException("ARRAY_SIZE operator needs an integer value");
                    }
                default:
                    return getValue(query.getType(), query.getValue());
            }
        } catch (Exception e) {
            throw new QueryParsingException(
                    "Error while retrieving value for field "+query.getField()+
                            " of type "+query.getType()+
                            " for operator "+query.getOperator()+
                            " with value "+query.getValue(),e);
        }
    }
    
    private static List getListValue(FilterType type, Object value){
        List result = new ArrayList();
        for (Object obj: (List) value){
            result.add(getValue(type,obj));
        }
        
        return result;
    }
    
    private static Object getValue(FilterType type, Object value){
        switch (type) {
            case STRING:
                if (value instanceof String) return (String) value;
                
                return value.toString();
                
            case INTEGER:
                if (value instanceof Integer) return value;
                if (value instanceof Double) return ((Double) value).intValue();
                
                return Integer.parseInt((String) value);
             case DOUBLE:
                if (value instanceof Double) return value;
                if (value instanceof Integer) return ((Integer) value).doubleValue();
                 
                return Double.parseDouble((String) value);
             case BOOLEAN:
                if (value instanceof Boolean) return value;
                
                return "true".equalsIgnoreCase((String) value);
        }
        return value;
    }
}
