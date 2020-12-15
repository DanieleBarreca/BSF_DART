package org.open.medgen.dart.core.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.open.medgen.dart.core.controller.utils.VCFFieldsUtils;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;
import org.open.medgen.dart.core.model.rdbms.entity.presets.QueryPreset;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Embeddable
public class QueryFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static String REF_ID = "REF_ID";
    private final static String MNEMONIC = "MNEMONIC";
    private final static String RULE = "RULE";
    private final static String FIELDS = "FIELDS";
    private final static String VALID = "VALID";
    
    @Transient
    @JsonProperty(REF_ID)
    private Integer refId = null;

    @Transient
    @JsonProperty(MNEMONIC)
    private String mnemonic = null;

    @Transient
    @JsonProperty(RULE)
    private QueryRule filter = null;

    @Transient
    @JsonProperty(VALID)
    private boolean valid = true;

    @Transient
    @JsonProperty(FIELDS)
    private List<VCFInfoDTO> fields = new LinkedList<>();
    
    public QueryFilter(){}
    
    public QueryFilter(QueryPreset preset){
        if (preset!=null) {
            this.refId = preset.getQueryId();
            this.mnemonic = preset.getMnemonic();
            this.filter = preset.getRule();
            this.valid = preset.getDateTo() == null;
        }
    }

    public QueryFilter(QueryPreset preset, Set<String> vcfChromosomesStringList, Set<String> vcfFilterStringList) {
        if (preset!=null) {
            this.refId = preset.getQueryId();
            this.mnemonic = preset.getMnemonic();
            this.filter = preset.getRule();
            this.fields = VCFFieldsUtils.getInfoFields(preset.getFieldList(), vcfChromosomesStringList, vcfFilterStringList);
            this.valid = preset.getDateTo() == null;
        }
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public QueryRule getFilter() {
        return filter;
    }

    public void setFilter(QueryRule filter) {
        this.filter = filter;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryFilter that = (QueryFilter) o;
        
        return Objects.equals(filter, that.filter);
    }

    @Override
    public int hashCode() {
        return this.filter.hashCode();
    }
}
