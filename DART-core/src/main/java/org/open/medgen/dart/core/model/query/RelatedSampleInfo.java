package org.open.medgen.dart.core.model.query;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.open.medgen.dart.core.controller.annotation.RelatedSamplesAnnotator;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RelatedSampleInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final String SAMPLE="SAMPLE";
    private static final String AFFECTED = "AFFECTED";
    private static final String SEX = "SEX";

    @Transient
    @JsonProperty(SAMPLE)
    private String sample = null;

    @Transient
    @JsonProperty(AFFECTED)
    private boolean isAffected = false;

    @Transient
    @JsonProperty(SEX)
    private RelatedSamplesAnnotator.RelatedSampleSex sex = RelatedSamplesAnnotator.RelatedSampleSex.UNK;

    
    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public RelatedSamplesAnnotator.RelatedSampleSex getSex() {
        return sex;
    }

    public void setSex(RelatedSamplesAnnotator.RelatedSampleSex sex) {
        this.sex = sex;
    }

    @JsonGetter(AFFECTED)
    public boolean isAffected() {
        return isAffected;
    }

    @JsonSetter(AFFECTED)
    public void setAffected(boolean affected) {
        isAffected = affected;
    }
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelatedSampleInfo that = (RelatedSampleInfo) o;
        return isAffected == that.isAffected &&
                Objects.equals(sample, that.sample) &&
                Objects.equals(sex, that.sex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sample, isAffected,sex);
    }
}
