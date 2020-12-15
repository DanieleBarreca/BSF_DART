/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.mongo.variant;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dbarreca
 */
public class AttributeMap extends LinkedHashMap<String,Object> implements Serializable{
    
    private static final long serialVersionUID = 1L;
        
    public AttributeMap() {
        super();
    }

    public AttributeMap(Map<? extends String, ? extends Object> m) {
        super(m);
    }
    
    public Integer getAttributeAsInt(String attr){
        try{
            return (Integer) get(attr);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Integer> getAttributeAsIntArray(String attr){
        try{
            return ( List<Integer>) get(attr);
        }catch(Exception e){
            return null;
        }
    }
        
    public Double getAttributeAsFloat(String attr){
        try{
            return (Double) get(attr);
        }catch(Exception e){
            return null;
        }
    }
    
    public List<Double> getAttributeAsFloatArray(String attr){
        try{
            return (List<Double>) get(attr);
        }catch (Exception e){
            return null;
        }
    }
    
    public String getAttributeAsString(String attr){
        try{
            return (String) get(attr);
        }catch(Exception e){
            return null;
        }
    }
    
    public List<String> getAttributeAsStringArray(String attr){
        try{
            return (List<String>) get(attr);
        }catch(Exception e){
            return null;
        }
    }
    
    public Boolean getAttributeAsFlag(String attr){
        try{
            return (Boolean) get(attr);
        }catch (Exception e) {
            return null;
        }
    }
    
    public void setAttribute(String name, Object value) {
        put(name, value);
    }
        
    public void merge(AttributeMap other) {
        for (Map.Entry<String,Object> prop: other.entrySet()){
            this.put(prop.getKey(),prop.getValue());
        }
    }
    
    public String parseAttributeAsString(String attr) {
        Object attribute = get(attr);

        return parseObjectAsString(attribute);
    }

    public static String parseObjectAsString(Object toParse) {
        try {

            if (toParse == null) {
                return "";
            } else if (toParse instanceof List) {
                return getArrayAsString((List) toParse);
            } else {
                return toParse.toString();
            }

        } catch (Exception e) {
            return "";
        }
    }

    public static String getArrayAsString(List array) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object id : array) {
            if (first) {
                first = false;
            } else {
                sb.append('\n');
            }
            sb.append(id.toString());
        }

        return sb.toString();
    }

}
