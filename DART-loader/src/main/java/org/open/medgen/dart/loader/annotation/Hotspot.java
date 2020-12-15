/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.annotation;

import java.util.Map;

/**
 *
 * @author dbarreca
 */
public class Hotspot {
    
    private String hugoSymbol;
    private String residue;
    private Integer tumorTypeCount;
    private Integer tumorCount;
    private String transcriptId;
    private AminoPosition aminoAcidPosition;   
    private String qValue;
    private String qValuePancan;
    private String qValueCancerType;
    private String type;
    private Map<String, Integer> variantAminoAcid;
    private Map<String, Integer> tumorTypeComposition;
    
    private String pValue;
    private String clusterCount;
    private String classification;

    public String getHugoSymbol() {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol) {
        this.hugoSymbol = hugoSymbol;
    }

    public String getResidue() {
        return residue;
    }

    public void setResidue(String residue) {
        this.residue = residue;
    }

    public Integer getTumorTypeCount() {
        return tumorTypeCount;
    }

    public void setTumorTypeCount(Integer tumorTypeCount) {
        this.tumorTypeCount = tumorTypeCount;
    }

    public Integer getTumorCount() {
        return tumorCount;
    }

    public void setTumorCount(Integer tumorCount) {
        this.tumorCount = tumorCount;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public AminoPosition getAminoAcidPosition() {
        return aminoAcidPosition;
    }

    public void setAminoAcidPosition(AminoPosition aminoAcidPosition) {
        this.aminoAcidPosition = aminoAcidPosition;
    }

    public String getqValue() {
        return qValue;
    }

    public void setqValue(String qValue) {
        this.qValue = qValue;
    }

    public String getqValuePancan() {
        return qValuePancan;
    }

    public void setqValuePancan(String qValuePancan) {
        this.qValuePancan = qValuePancan;
    }

    public String getqValueCancerType() {
        return qValueCancerType;
    }

    public void setqValueCancerType(String qValueCancerType) {
        this.qValueCancerType = qValueCancerType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Integer> getVariantAminoAcid() {
        return variantAminoAcid;
    }

    public void setVariantAminoAcid(Map<String, Integer> variantAminoAcid) {
        this.variantAminoAcid = variantAminoAcid;
    }

    public Map<String, Integer> getTumorTypeComposition() {
        return tumorTypeComposition;
    }

    public void setTumorTypeComposition(Map<String, Integer> tumorTypeComposition) {
        this.tumorTypeComposition = tumorTypeComposition;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getpValue() {
        return pValue;
    }

    public void setpValue(String pValue) {
        this.pValue = pValue;
    }

    public String getClusterCount() {
        return clusterCount;
    }

    public void setClusterCount(String clusterCount) {
        this.clusterCount = clusterCount;
    }
    
    

    @Override
    public String toString() {
        return "Hotspot{" + "hugoSymbol=" + hugoSymbol + ", residue=" + residue + ", tumorTypeCount=" + tumorTypeCount + ", tumorCount=" + tumorCount + ", transcriptId=" + transcriptId + ", aminoAcidPosition=" + aminoAcidPosition + ", qValue=" + qValue + ", qValuePancan=" + qValuePancan + ", qValueCancerType=" + qValueCancerType + ", type=" + type + ", variantAminoAcid=" + variantAminoAcid + ", tumorTypeComposition=" + tumorTypeComposition + '}';
    }
    
    
  
}
