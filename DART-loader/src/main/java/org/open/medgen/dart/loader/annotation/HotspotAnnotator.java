/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.annotation;

import org.open.medgen.dart.core.model.mongo.variant.AttributeMap;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFAttributeNumber;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.FieldLocation;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.UnsupportedFieldException;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFHeaderLineType;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author dbarreca
 */
public class HotspotAnnotator {

    private ObjectMapper  mapper = new ObjectMapper();
        
    private Map<String,Set<Hotspot>> missenseSingleHotspots;
    private Map<String,Set<Hotspot>> indelSingelHotspots;
    private Map<String,Set<Hotspot>> hotspots3D;
    
    public HotspotAnnotator() throws IOException {
        missenseSingleHotspots = getHotspots(HotspotAnnotator.class.getClassLoader().getResourceAsStream("Hotspots"), "single residue");
        indelSingelHotspots = getHotspots(HotspotAnnotator.class.getClassLoader().getResourceAsStream("Hotspots"), "in-frame indel");
        hotspots3D = getHotspots(HotspotAnnotator.class.getClassLoader().getResourceAsStream("Hotspots3D"), null);
    }
    
    private Map<String, Set<Hotspot>> getHotspots(InputStream in, String type) throws IOException{
        
        Map<String, Set<Hotspot>> result = new HashMap<>();
        Iterator<Hotspot> it = mapper.reader(Hotspot.class).readValues(in);
        
        while (it.hasNext()){
            Hotspot hotspot = it.next();
            if (type==null || hotspot.getType().equals(type)){
                if (!result.containsKey(hotspot.getHugoSymbol())){
                    result.put(hotspot.getHugoSymbol(), new HashSet<>());
                }
                result.get(hotspot.getHugoSymbol()).add(hotspot);
            }
        }
        return result;      
    }
  
    
    public static List<VCFInfoDTO> getFields() throws UnsupportedFieldException{
        List<VCFInfoDTO> result = new LinkedList<>();
       
        result.add(new VCFInfoDTO("MAF:VARIANT_CLASSIFICATION", "VARIANT_CLASSIFICATION according to the MAF format", FieldLocation.TRANSCRIPT, VCFAttributeNumber.VALUE, VCFHeaderLineType.String));
        result.add(new VCFInfoDTO("Hotspot", "flag indicating if the variant lies in an hotspot", FieldLocation.TRANSCRIPT, VCFAttributeNumber.VALUE, VCFHeaderLineType.Flag));
        result.add(new VCFInfoDTO("3DHotspot", "flag indicating if the variant lies in a 3D hotspot", FieldLocation.TRANSCRIPT, VCFAttributeNumber.VALUE, VCFHeaderLineType.Flag));
        return result;
    }
    
    public void annotateTranscript(AttributeMap transcript ,String ref, String alt){
        String symbol = transcript.getAttributeAsString("CSQ:SYMBOL");        
        if (symbol==null || symbol.isEmpty()){
            symbol = transcript.getAttributeAsString("CSQ:Feature");
        }
        if (symbol==null || symbol.isEmpty()){
            symbol = "Unknown";
        }
        
        
        String variantClassification = getVariantClassification(ref,alt,transcript.getAttributeAsStringArray("CSQ:Consequence").get(0));
        transcript.put("MAF:VARIANT_CLASSIFICATION", variantClassification);
        
        String consequence = mapMAFToOncoKB(variantClassification);
        
        Integer startProteinPos = transcript.getAttributeAsInt("CSQ:Protein_position_start");
        Integer endProteinPos = transcript.getAttributeAsInt("CSQ:Protein_position_end");
        
        if (endProteinPos == null && startProteinPos!=null){
            endProteinPos = startProteinPos;
        }else if (startProteinPos==null && endProteinPos !=null){
            startProteinPos = endProteinPos;
        }
        
        Hotspot singleHs = retrieveSingleHotspot(symbol, consequence, startProteinPos, endProteinPos);
        Hotspot hotspot3d = retrieve3Dhotspot(symbol, consequence, startProteinPos, endProteinPos);
        
        if (singleHs!=null)  transcript.put("Hotspot", true);
        if (hotspot3d!=null)  transcript.put("3DHotspot", true);
        
    }
    
    
    //# Converts Sequence Ontology variant types to MAF variant classifications from  https://github.com/mskcc/vcf2maf/blob/master/vcf2maf.pl
    private String getVariantClassification(String ref, String alt, String consequence){
        //System.out.println("REF: "+ref+",ALT"+alt+"CSQ: "+consequence);
        if (consequence == null) return "Targeted_Region";
                
        Integer refLength = ref.length();
        Integer altLength = alt.length();
        
        while (!ref.isEmpty() && !alt.isEmpty() && ref.charAt(0)==alt.charAt(0) && !ref.equals(alt)){
            if (ref.length() == 1){
                ref = "-";
            }else{
                ref = ref.substring(1);
            }
            if (alt.length() == 1){
                alt = "-";
            }else{
                alt = alt.substring(1);
            }            
            refLength -=1;
            altLength -=1;
        }
        
        String varType;
        Boolean inframe = true;
        
        if (Objects.equals(refLength, altLength)){
            switch(altLength){
                case(1):
                    varType = "SNP";
                    break;
                case(2):
                    varType = "DNP";
                    break;
                case(3):
                    varType = "TNP";
                    break;
                default:
                    varType = "ONP";                            
            }                    
        }else {
            if (refLength<altLength){
                varType = "INS";
            }else{
                varType = "DEL";
            }
        }
        
        inframe = Math.abs(refLength - altLength) % 3 == 0;
        
        //System.out.println("REF: "+ref+",ALT"+alt+" REFLEN: "+refLength+" ALTLEN: "+altLength+" VARTYPE: "+varType+" INFRME: " + inframe);
        switch(consequence.toLowerCase()){
            case("splice_acceptor_variant"):
            case("splice_donor_variant"):
            case("transcript_ablation"):
            case("exon_loss_variant"):
                return "Splice_Site";
            case("stop_gained"):
                return "Nonsense_Mutation";
            case("frameshift_variant"):
                if (varType.equals("DEL")) return "Frame_Shift_Del";
                if (varType.equals("INS")) return "Frame_Shift_Ins";
                break;
            case("protein_altering_variant"):
                if (!inframe && varType.equals("DEL") ) return "Frame_Shift_Del";
                if (!inframe && varType.equals("INS") ) return "Frame_Shift_Ins";
                if (inframe && varType.equals("DEL") ) return "In_Frame_Del";
                if (inframe && varType.equals("INS") ) return "In_Frame_Ins";
                break;
            case("stop_lost"):
                return "Nonstop_Mutation";
            case("initiator_codon_variant"):
            case("start_lost"):
                return "Translation_Start_Site";
            case("inframe_insertion"):
            case("disruptive_inframe_insertion"):
                return "In_Frame_Ins";
            case("missense_variant"):
            case("coding_sequence_variant"):
            case("conservative_missense_variant"):
            case("rare_amino_acid_variant"):
                return "Missense_Mutation";
            case("transcript_amplification"):
            case("intron_variant"):
            case("intragenic"):
            case("intragenic_variant"):   
                return "Intron";
            case("splice_region_variant"):
                return "Splice_Region";
            case("incomplete_terminal_codon_variant"):
            case("synonymous_variant"):
            case("stop_retained_variant"):
            case("nmd_transcript_variant"):
                return "Silent";
            case("mature_mirna_variant"):
            case("exon_variant"):
            case("non_coding_exon_variant"):
            case("non_coding_transcript_exon_variant"):
            case("non_coding_transcript_variant"):
            case("nc_transcript_variant"):
                return "RNA";
            case("5_prime_utr_variant"):
            case("5_prime_utr_premature_start_codon_gain_variant"):
                return "5'UTR";
            case("3_prime_utr_variant"):
                return "3'UTR";
            case("tf_binding_site_variant"):
            case("regulatory_region_variant"):
            case("regulatory_region"):
            case("intergenic_variant"):
            case("intergenic_region"):
                return "IGR";
            case("upstream_gene_variant"):
                return "5'Flank";
            case("downstream_gene_variant"):
                return "3'Flank";
        }
         
         
        return "Targeted_Region";
    }
    
    private String mapMAFToOncoKB(String variantClassification){
        switch(variantClassification){
            case "3'Flank":
            case "5'Flank":
                return "any";
            case "Targeted_Region":
                return "inframe_deletion"+"%2B"+"inframe_insertion";
            case "Frame_Shift_Del":
            case "Frame_Shift_Ins":
                return "frameshift_variant";
            case "In_Frame_Del":
                return "inframe_deletion";
            case "In_Frame_Ins":
                return "inframe_insertion";    
            case "Missense_Mutation":
                return "missense_variant";
            case "Nonsense_Mutation":
                return "stop_gained";
            case "Nonstop_Mutation":
                return "stop_lost";
            case "Splice_Site":
                return "splice_region_variant";
            case "Translation_Start_Site":
                return "start_lost";
            default:
                return variantClassification;
        }
    }
    
    private Hotspot retrieveSingleHotspot(String hugoSymbol, String consequence, Integer start, Integer end){
        Set<Hotspot> hss = new HashSet<>();
        if (this.missenseSingleHotspots.keySet().contains(hugoSymbol) && consequence.equalsIgnoreCase("missense_variant")){
           hss = this.missenseSingleHotspots.get(hugoSymbol);
        }else if (this.indelSingelHotspots.keySet().contains(hugoSymbol) && 
                (consequence.equalsIgnoreCase("inframe_insertion") || consequence.equalsIgnoreCase("inframe_deletion"))){
            hss = this.indelSingelHotspots.get(hugoSymbol);
        }
        
        for (Hotspot hs: hss){
            if (hs.getAminoAcidPosition().getEnd()>= start && hs.getAminoAcidPosition().getStart()<=end){
                return hs;
            }
        }
        
        return null;
    }
    
    private Hotspot retrieve3Dhotspot(String hugoSymbol, String consequence, Integer start, Integer end){
        Set<Hotspot> hss = new HashSet<>();
        if (this.hotspots3D.keySet().contains(hugoSymbol) && consequence.equalsIgnoreCase("missense_variant")){
           hss = this.hotspots3D.get(hugoSymbol);
        }
        
        for (Hotspot hs: hss){
            if (hs.getAminoAcidPosition().getEnd()>= start && hs.getAminoAcidPosition().getStart()<=end){
                return hs;
            }
        }
        
        return null;
    }
}
