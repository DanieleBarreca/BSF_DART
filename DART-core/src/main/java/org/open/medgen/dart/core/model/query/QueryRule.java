/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.query;


import org.open.medgen.dart.core.model.query.constants.Operator;
import org.open.medgen.dart.core.model.query.constants.GroupCondition;
import org.open.medgen.dart.core.model.query.constants.FilterType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.*;


/**
 *
 * @author dbarreca
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class QueryRule implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //-----RULE------ 
    private Integer id;
    private String fieldPath;
    private FilterType type;
    private Operator operator;
    private Object value;

    //-----GROUP------ 
    private GroupCondition condition;
    private Set<QueryRule> rules = new HashSet<>();    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getField() {
        return fieldPath;
    }

    public void setField(String field) {
        this.fieldPath = field;
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }


    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if (value instanceof List){
            Collections.sort((List) value);
        }
        
        this.value = value;
    }

    public GroupCondition getCondition() {
        return condition;
    }

    public void setCondition(GroupCondition condition) {
        this.condition = condition;
    }

    public Collection<QueryRule> getRules() {
        return rules;
    }

    public void setRules(Collection<QueryRule> rules) {
        this.rules = new HashSet(rules);
    }
    
    @JsonIgnore
    public boolean isGroup(){
        return condition!=null;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.fieldPath);
        hash = 97 * hash + (this.type == null ? 0 : this.type.toString().hashCode());
        hash = 97 * hash + (this.operator == null ? 0 : this.operator.toString().hashCode());
        hash = 97 * hash + Objects.hashCode(this.value);
        hash = 97 * hash + (this.condition == null ? 0 : this.condition.toString().hashCode());
        hash = 97 * hash + Objects.hashCode(this.rules);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QueryRule other = (QueryRule) obj;
        
        if (!Objects.equals(this.fieldPath, other.fieldPath)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.operator != other.operator) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (this.condition != other.condition) {
            return false;
        }
        if (!Objects.equals(this.rules, other.rules)) {
            return false;
        }
        return true;
    }
    
    @JsonIgnore
    public Set<Integer> getFieldIds(){
        Set<Integer> fields = new HashSet();
        
        if (this.isGroup()){
            for (QueryRule rule: this.rules){
                fields.addAll(rule.getFieldIds());
            }
        }else{
            fields.add(this.id);
        }
        
        return fields;
    }

}
