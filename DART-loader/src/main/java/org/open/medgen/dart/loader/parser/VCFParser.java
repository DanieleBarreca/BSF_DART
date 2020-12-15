/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.loader.parser;

import org.open.medgen.dart.core.controller.utils.ChecksumException;
import org.open.medgen.dart.core.controller.utils.FileDigester;
import org.open.medgen.dart.core.controller.vcf.VCFInsertControllerBean;
import org.open.medgen.dart.core.model.mongo.variant.AlleleModel;
import org.open.medgen.dart.core.model.mongo.variant.AttributeMap;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.mongo.variant.Zygosity;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.SampleDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFAttributeNumber;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFType;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.FieldLocation;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.UnsupportedFieldException;
import org.open.medgen.dart.loader.annotation.CIVICAnnotator;
import org.open.medgen.dart.loader.annotation.HotspotAnnotator;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFContigHeaderLine;
import htsjdk.variant.vcf.VCFFilterHeaderLine;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import java.io.File;
import java.util.*;

import org.apache.commons.io.FilenameUtils;
import org.bson.types.ObjectId;
import org.open.medgen.dart.loader.job.model.JobConfigParser;
import org.open.medgen.dart.loader.parser.vep_config.VEPPreferences;

import static java.util.Comparator.*;

/**
 *
 * @author dbarreca
 */
public class VCFParser {
    
    public final static Set<String> EMPTY_VALUES = new HashSet<>(Arrays.asList(".","-","?"));
    public final static String SAMPLE_VARIABLE = "{sample}";
    public static final String FORMAT_PREFIX = "FORMAT:";
    public static final String INFO_PREFIX = "INFO:";

    private final VCFFileDTO parsedHeader;
    private final Map<String, VCFInfoDTO> infoFields = new HashMap();
    private final Map<String, VCFInfoDTO> formatFields= new HashMap();
    private final Map<String, String> sampleAlias = new LinkedHashMap<>();

    private VEPCSQParser csqParser = null;
    private VCFType type = null;
    private HotspotAnnotator oncoKBAnnotator = null;
    private CIVICAnnotator civicAnnotator = null;
    private VEPPreferences vepPreferences = null;

    public VCFParser() {
        this.parsedHeader = null;
    }
        
    public VCFParser(
            VEPPreferences vepPreferences,
            VCFHeader header, 
            String vcfName, 
            VCFType type, 
            JobConfigParser jobConfig,
            String geneField, 
            String genomicChangeField, 
            String codingChangeField,
            List<String> chromosomeWhitelist,
            File staticContentFolder
            ) throws ParserInitializationException {

        this.vepPreferences = vepPreferences;
        this.type=type;

        this.parsedHeader = buildHeader(header, vcfName, jobConfig, geneField, genomicChangeField, codingChangeField,chromosomeWhitelist,staticContentFolder);
        // fil the format / info fields
        this.parsedHeader.getVcfFields().forEach(f -> {
            String fieldName = f.getVcfFieldName();
            if (fieldName.startsWith(FORMAT_PREFIX)) {
                fieldName = fieldName.replace(FORMAT_PREFIX, "");
                this.formatFields.put(fieldName, f);
            } else if (fieldName.startsWith(INFO_PREFIX)) {
                fieldName = fieldName.replace(INFO_PREFIX, "");
                this.infoFields.put(fieldName, f);
            }
        });
    }

    private VCFFileDTO buildHeader(
            VCFHeader header,
            String vcfName,
            JobConfigParser jobConfig,
            String geneField,
            String genomicChangeField,
            String codingChangeField,
            List<String> chromosomeWhitelist,
            File staticContentFolder
    ) throws ParserInitializationException {
        
        VCFHeaderLine vepHeader =  (header.getOtherHeaderLine("VEP"));
        if (vepHeader == null) throw new ParserInitializationException("Only VEP annotated files are supported at the moment");
        Map<String, String> vepMetadata = VEPCSQParser.getMetadata(vepHeader);

        VCFFileDTO vcfFileDTO = new VCFFileDTO(vcfName,VEPCSQParser.buildDescription(vepMetadata),VEPCSQParser.extractGenome(vepMetadata), type );
        vcfFileDTO.setCreationDate(VEPCSQParser.extractRunDate(vepMetadata));


        Set<String> contigs = new LinkedHashSet();
        for (VCFContigHeaderLine contig: header.getContigLines()){
            if (chromosomeWhitelist==null || chromosomeWhitelist.contains(contig.getID())){
                contigs.add(contig.getSAMSequenceRecord().getSequenceName());
            }
        }
      
        Set<String> filters = new LinkedHashSet();
        for (VCFFilterHeaderLine filter : header.getFilterLines()) {
            filters.add(filter.getID());
        }
      

        for (String sampleHeaderName : header.getSampleNamesInOrder()) {
            JobConfigParser.SampleConfig sampleConfig = jobConfig.getSampleConfig(sampleHeaderName);
            if (sampleConfig!=null) {
                
                String sampleName;
                if (sampleConfig.getSampleAlias()!=null && !sampleConfig.getSampleAlias().trim().isEmpty()) {
                    sampleName = sampleConfig.getSampleAlias();
                } else if (sampleHeaderName.endsWith(".bam")){
                    sampleName = FilenameUtils.getBaseName(sampleHeaderName);
                } else {
                    sampleName = sampleHeaderName;
                }

                this.sampleAlias.put(sampleHeaderName, sampleName);
                
                String bamMD5sum = getMD5sumForResource(sampleConfig.getBamUrl(),staticContentFolder);
                System.out.println(String.format("BAM FILE: %s, MD5: %s",sampleConfig.getBamUrl(), bamMD5sum));
                
                String vcfMD5sum = getMD5sumForResource(sampleConfig.getVcfUrl(),staticContentFolder);
                System.out.println(String.format("VCF FILE: %s, MD5: %s",sampleConfig.getVcfUrl(), vcfMD5sum));
                
                String coverageTrackMD5sum = getMD5sumForResource(sampleConfig.getCoverageTrackUrl(),staticContentFolder);
                System.out.println(String.format("COVERAGE TRACK FILE: %s, MD5: %s",sampleConfig.getCoverageTrackUrl(), coverageTrackMD5sum));
                
                vcfFileDTO.addSample(new SampleDTO(
                        sampleName,
                        sampleConfig.getBamUrl(),
                        bamMD5sum,
                        sampleConfig.getVcfUrl(),
                        vcfMD5sum,
                        sampleConfig.getCoverageTrackUrl(),
                        coverageTrackMD5sum,
                        sampleConfig.getCoverageFile()!=null? ObjectId.get().toHexString() : null));
            }
        }
        
                
        List<VCFInfoDTO> fixedFields = VCFFixedFields.getFixedFields(contigs, filters, this.type);
        List<VCFInfoDTO> variantFields = new LinkedList();
        List<VCFInfoDTO> alleleFields = new LinkedList();
        List<VCFInfoDTO> sampleFields = new LinkedList();
        List<VCFInfoDTO> csqFields = new LinkedList();
        
        for (VCFInfoHeaderLine info : header.getInfoHeaderLines()) {
            if (info.getID().equalsIgnoreCase(vepPreferences.getCsqFieldKey())) {
                VEPCSQParser csqParser = new VEPCSQParser(info, vepPreferences, geneField, genomicChangeField, codingChangeField);
                if (csqParser.hasAlleleIndex() && csqParser.isIsValid()){
                    this.csqParser = csqParser;
                    csqFields.addAll(this.csqParser.getCsqFields().values());
                    if (this.isSomatic()) {
                        try {
                            oncoKBAnnotator = new HotspotAnnotator();
                            csqFields.addAll(HotspotAnnotator.getFields());
                        } catch (Exception ex) {
                            oncoKBAnnotator = null;
                            System.out.println("Could not initialize OncoKBAnnotator");
                            ex.printStackTrace();
                        }
                        try {
                            civicAnnotator = new CIVICAnnotator();
                            csqFields.addAll(CIVICAnnotator.getFields());
                        } catch (Exception ex) {
                            civicAnnotator = null;
                            System.out.println("Could not initialize CIVICAnnotator");
                            ex.printStackTrace();
                        }
                    }
                    continue;
                }else if (!csqParser.hasAlleleIndex()) {
                    System.out.println("WARN: VEP CSQ Field does not have ALLELE_NUM field. Cannot parse CSQ info");
                }
            }
            
            try{
                Integer count = null;
                if (VCFHeaderLineCount.INTEGER.equals(info.getCountType())){
                    count = info.getCount();
                }
                VCFAttributeNumber number = VCFAttributeNumber.decode(info.getCountType().name(), count);

                String id = INFO_PREFIX + info.getID();
                switch(number){
                    case R:
                        // only REF_ALLELE for R
                        alleleFields.add(new VCFInfoDTO(id,info.getDescription(),FieldLocation.REF_ALLELE,number,Utils.convertType(info.getType())));
                    case A:
                        // and ALLELE for A and R
                        VCFInfoDTO alleleInfo = new VCFInfoDTO(id, info.getDescription(), FieldLocation.ALLELE, number, Utils.convertType(info.getType()));
                        alleleFields.add(alleleInfo);
                        break;
                    case G:
                        System.out.println("WARN: G-type field "+info.getID()+" found in INFO fields");
                        break;
                    default:
                        VCFInfoDTO infoDTO = new VCFInfoDTO(id, info.getDescription(), FieldLocation.VARIANT_INFO, number, Utils.convertType(info.getType()));
                        variantFields.add(infoDTO);
                }
            }catch (UnsupportedFieldException e){
                System.out.println("WARN: "+e.getMessage());
            }
  
        }
        
        for (VCFFormatHeaderLine format : header.getFormatHeaderLines()) {
            if (format.getID().equals("GT")) {
                continue;
            }
            try {
                Integer count = null;
                if (VCFHeaderLineCount.INTEGER.equals(format.getCountType())){
                    count = format.getCount();
                }
                VCFAttributeNumber number = VCFAttributeNumber.decode(format.getCountType().name(), count);
                if (isSomatic() && "QSS".equalsIgnoreCase(format.getID())) {
                    number = VCFAttributeNumber.R;
                }

                String id = FORMAT_PREFIX + format.getID();
                switch (number) {
                    case A:
                        VCFInfoDTO field = new VCFInfoDTO(id,format.getDescription(),FieldLocation.ALLELE,number,Utils.convertType(format.getType()));
                        alleleFields.add(field);
                        break;
                    case R:
                        VCFInfoDTO alleleField = new VCFInfoDTO(id,format.getDescription(),FieldLocation.ALLELE,number,Utils.convertType(format.getType()));
                        alleleFields.add(alleleField);
                        alleleFields.add(new VCFInfoDTO(id, format.getDescription(), FieldLocation.REF_ALLELE, number, Utils.convertType(format.getType())));
                        break;
                    case G:
                        System.out.println("WARN: G-type field " + format.getID() + " found in FORMAT fields. Ignored");
                        break;
                    default:
                        VCFInfoDTO sampelFormatField = new VCFInfoDTO(id, format.getDescription(), FieldLocation.SAMPLE_FORMAT, number, Utils.convertType(format.getType()));
                        sampleFields.add(sampelFormatField);
                }
            } catch (UnsupportedFieldException e) {
                System.out.println("WARN: " + e.getMessage());
            }
            
            Collections.sort(variantFields, comparing(VCFInfoDTO::getDisplayName));
            
            Collections.sort(alleleFields, comparing(VCFInfoDTO::getDisplayName));
            
            Collections.sort(sampleFields, comparing(VCFInfoDTO::getDisplayName));
            
            Collections.sort(csqFields, comparing(VCFInfoDTO::getDisplayName));
             
        }

        vcfFileDTO.addFields(fixedFields);
        vcfFileDTO.addFields(variantFields);
        vcfFileDTO.addFields(sampleFields);
        vcfFileDTO.addFields(alleleFields);
        vcfFileDTO.addFields(csqFields);
        vcfFileDTO.setMongoId(new ObjectId());
        return vcfFileDTO;
    }

    public boolean isSV() {
        return isOfType(VCFType.SV);
    }

    public boolean isSomatic() {
        return isOfType(VCFType.SOMATIC);
    }

    public boolean isOfType(VCFType target) {
        if (!Objects.isNull(this.type) && target.equals(type)) {
            return true;
        }
        return false;
    }

    public VCFFileDTO getHeader(){
        return parsedHeader;
    }
    
    public List<VariantModel> parseContext(VariantContext context) {

            List<VariantModel> result = new LinkedList<>();

            String chrom = context.getContig();
            Integer position = context.getStart();
            
            try{
                Set<String> ids = new HashSet<>(Arrays.asList(context.getID().split(",")));
                ids.remove(".");
                double quality = context.getCommonInfo().getPhredScaledQual();
                Set<String> filters = context.getFilters();

                AttributeMap commonInfo = new AttributeMap();

                LinkedHashMap<Integer, AlleleModel> alleleInfo = new LinkedHashMap<>();
                for (Allele allele : context.getAlleles()) {
                    Integer index = context.getAlleleIndex(allele);
                    if (index >= 0) {
                        AlleleModel model = new AlleleModel();
                        model.put(AlleleModel.ALLELE, allele.getDisplayString());
                        model.put(AlleleModel.INDEX, index);
                        alleleInfo.put(index, model);
                    }
                }
                
                List<AttributeMap> transcripts = new ArrayList<>();
                //PARSE INFO FIELDS
                for (String field : context.getCommonInfo().getAttributes().keySet()) {
                    if (field.equals(this.vepPreferences.getCsqFieldKey()) && csqParser!=null){
                        transcripts = csqParser.parseTranscripts(context.getCommonInfo().getAttributeAsStringList(field, ""));
                    }else{
                        VCFInfoDTO fieldMetaInfo = infoFields.get(field);
                        if (fieldMetaInfo != null) {
                            Object attribute = context.getCommonInfo().getAttribute(field);
                            addAttributeToMap(fieldMetaInfo, attribute, commonInfo, alleleInfo);
                        }
                    }
                }
                               
                for (Genotype genotype : context.getGenotypesOrderedByName()) {

                    String sampleName = sampleAlias.get( genotype.getSampleName());
                    if (sampleName==null){
                        continue;
                    }
                   
                    Map<Integer, AttributeMap> alleleFormat = new HashMap<>();
                    for (AlleleModel model : alleleInfo.values()) {
                        AlleleModel newModel = new AlleleModel();
                        newModel.merge(model);
                        alleleFormat.put(newModel.getAlleleIndex(), model);
                    }

                    //PARSE FORMAT FIELDS
                    AttributeMap sampleAttributes = new AttributeMap();

                    for (String field : genotype.getExtendedAttributes().keySet()) {
                        VCFInfoDTO fieldMetaInfo = formatFields.get(field);
                        if (fieldMetaInfo != null) {
                            Object attribute = genotype.getExtendedAttribute(field);
                            addAttributeToMap(fieldMetaInfo, attribute, sampleAttributes, alleleFormat);
                        } 
                    }

                    int calculatedDP = 0;
                    if (genotype.hasAD()) {
                        for (int i = 0; i < genotype.getAD().length; i++) {
                            int alleleAD = genotype.getAD()[i];
                            alleleFormat.get(i).put("FORMAT:AD", alleleAD);
                            calculatedDP += alleleAD;
                        }
                    }
                    
                    if (genotype.hasDP()) {
                        sampleAttributes.put("FORMAT:DP", genotype.getDP());
                    }else{
                        sampleAttributes.put("FORMAT:DP", calculatedDP);
                    }
                    
                    if (genotype.hasGQ()) {
                        sampleAttributes.put("FORMAT:GQ", genotype.getGQ());
                    }

                    
                    List<Integer> gt = new LinkedList<>();
                    
                    if(!isSV()){
                        for (Allele allele : genotype.getAlleles()) {
                            gt.add(context.getAlleleIndex(allele));
                        }
                    }else{
                        gt.add(0);
                        gt.add(1);
                    }

                    boolean phased = genotype.isPhased();
                                       
                    Set<Integer> previousAlleles = new HashSet<>();
                    for (Integer alleleIndex : gt) {
                        if (alleleIndex==-1 || alleleIndex==0 || previousAlleles.contains(alleleIndex)) {
                            //IGNORE REF AND UNKOWN ALLELES AND ALLELES ALREADY CONSIDERED
                            continue;
                        }
                        previousAlleles.add(alleleIndex);
                        
                        List<AttributeMap> pickedTranscripts = new ArrayList<>(); 
                        
                        for (AttributeMap transcript : transcripts) {
                            Integer transcriptAlleleIndex = transcript.getAttributeAsInt("CSQ:ALLELE_NUM");
                            Boolean isPicked = transcript.getAttributeAsFlag("CSQ:PICK");
                            if (transcriptAlleleIndex.equals(alleleIndex) && isPicked != null && isPicked) {
                                pickedTranscripts.add(transcript);
                            }
                        }                        
                        
                        if (pickedTranscripts.isEmpty()) {
                            for (AttributeMap transcript : transcripts) {
                                Integer transcriptAlleleIndex = transcript.getAttributeAsInt("CSQ:ALLELE_NUM");
                                if (transcriptAlleleIndex.equals(alleleIndex)) {
                                    pickedTranscripts.add(transcript);
                                }
                            }  
                        }
                        
                        if (pickedTranscripts.isEmpty()) {
                            pickedTranscripts.add(new AttributeMap());
                        }
                        
                        for (AttributeMap transcript: pickedTranscripts){
                            VariantModel variant = new VariantModel();

                            variant.setChromosome(chrom);
                            variant.setPosition(position);
                            variant.setIds(ids);
                            variant.setQuality(quality);
                            variant.setFilter(filters);

                            variant.getInfo().merge(commonInfo);

                            variant.getSample().setName(sampleName);
                            
                            if(!isSV()){
                                variant.getSample().getSampleGenotype().setGenotype(gt, phased);
                            }else{
                                variant.getSample().getSampleGenotype().setGenotype("./.");
                            }
                            
                            variant.getSample().getFormat().merge(sampleAttributes);

                            variant.getRefAllele().merge(alleleFormat.get(0));
                            if (alleleIndex >= 0) {
                                variant.getAllele().merge(alleleFormat.get(alleleIndex));
                            }
                            
                            variant.getTranscript().merge(transcript);
                            variant.calculateVariantId();
                            
                            if (oncoKBAnnotator!=null && variant.getTranscript().getAttributeAsStringArray("CSQ:Consequence")!=null){
                                oncoKBAnnotator.annotateTranscript(variant.getTranscript(), variant.getRefAllele().getAllele(), variant.getAllele().getAllele());
                            }
                            
                            if (civicAnnotator!=null && variant.getTranscript().getAttributeAsStringArray("CSQ:Consequence")!=null){
                                civicAnnotator.annotateTranscript(variant.getTranscript(), chrom, position,variant.getRefAllele().getAllele(), variant.getAllele().getAllele() );
                            }
                            
                           
                            Set<String> otherSamples = new HashSet<>();
                            if (!isSV() && context.getSampleNames().size() > 1) {
                                for (Genotype otherGenotype : context.getGenotypesOrderedByName()) {
                                    String otherGenotypeName = sampleAlias.get(otherGenotype.getSampleName());
                                    if (otherGenotypeName==null || otherGenotypeName.equals(sampleName)){
                                        continue;
                                    }
                                    
                                    List<Integer> otherGenotypeGT = new LinkedList<>();
                                    otherGenotype.getAlleles().forEach((allele) -> {
                                        otherGenotypeGT.add(context.getAlleleIndex(allele));
                                    });
                                    if (!otherGenotypeGT.contains(alleleIndex)) {
                                        continue;
                                    }
                                    
                                    switch (Zygosity.decode(otherGenotypeGT)){
                                        case HOMOZYGOUS_ALT:
                                            otherSamples.add(otherGenotypeName+":HOM");
                                            break;
                                        case HETEROZYGOUS_REF:
                                        case HETEROZYGOUS_ALT:
                                            otherSamples.add(otherGenotypeName+":HET");
                                            break;
                                    }
                                }
                            }
                            
                            variant.setOtherSamples(otherSamples);

                            
                            result.add(variant);
                            
                        }  
                    }
                }
            }catch(Exception e){
                System.out.println("ERROR FOR CHROM "+chrom+" AND POS "+position);
                e.printStackTrace();
            }    
     
            return result;
      
    }
    
    private static  <T extends AttributeMap> void addAttributeToMap (
            VCFInfoDTO metaInfo, 
            Object attribute, 
            AttributeMap entityAttributes,
            Map<Integer, T> alleleAttributes ) {
        
        switch (metaInfo.getVcfNumber()) {
            case VALUE:
                entityAttributes.put(metaInfo.getVcfFieldName(), getTypedValue(metaInfo.getVcfType(), attribute, false));
                break;
            case ARRAY:
                entityAttributes.put(metaInfo.getVcfFieldName(), getTypedValue(metaInfo.getVcfType(), attribute, true));
                break;
            case A:
                List alleleValues = (List) getTypedValue(metaInfo.getVcfType(),attribute,true);
                if (alleleValues != null) {
                    for (int i = 1; i <= alleleValues.size(); i++) {
                        alleleAttributes.get(i).put(metaInfo.getVcfFieldName(), alleleValues.get(i - 1));
                    }
                }
                break;
            case R:
                List alleleValuesWithRef = (List) getTypedValue(metaInfo.getVcfType(),attribute,true);
                if (alleleValuesWithRef != null) {
                    for (int i = 0; i < alleleValuesWithRef.size(); i++) {
                        alleleAttributes.get(i).put(metaInfo.getVcfFieldName(), alleleValuesWithRef.get(i));
                    }
                }
                break;
            case G:
                break;
        }
       
    }
    
    protected static Object getTypedValue(org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFHeaderLineType type, Object value, boolean isArray) {

        if (value == null) {
            if (isArray) {
                return new LinkedList<>();
            } else {
                return null;
            }
        }

        if (value instanceof String
                && (((String) value).trim().isEmpty() || EMPTY_VALUES.contains(((String) value).trim()))) {
            if (isArray) {
                return new LinkedList<>();
            } else {
                return null;
            }
        }

        switch (type) {
            case Integer:
                if (isArray) {
                    List<Integer> returnValue = new LinkedList<>();
                    for (Object obj : getValueList(value)) {
                        if (((String) obj).trim().isEmpty() || EMPTY_VALUES.contains(((String) obj).trim())) {
                            returnValue.add(null);
                        } else {
                            if (!EMPTY_VALUES.contains((String) obj)) {
                                returnValue.add(Integer.parseInt((String) obj));
                            }
                        }
                    }

                    return returnValue;
                } else {
                    return Integer.parseInt((String) value);

                }
            case Float:
                if (isArray) {
                    List<Double> returnValue = new LinkedList<>();
                    for (Object obj : getValueList(value)) {
                        if (((String) obj).trim().isEmpty() || EMPTY_VALUES.contains(((String) obj).trim())) {
                            returnValue.add(null);
                        } else {
                            if (!EMPTY_VALUES.contains((String) obj)) {
                                returnValue.add(Double.parseDouble((String) obj));
                            }
                        }
                    }

                    return returnValue;
                } else {
                    return Double.parseDouble((String) value);

                }
            case String:
            case Character:
                if (isArray) {
                    List<String> returnValue = new LinkedList<>();
                    for (Object obj : getValueList(value)) {
                        if (!((String) obj).trim().isEmpty() && !EMPTY_VALUES.contains(((String) obj).trim())) {
                            returnValue.add((String) obj);
                        }
                    }

                    return returnValue;
                } else {
                    return (String) value;

                }
            case Flag:
                if (isArray || value instanceof List) {
                    return null;
                }

                if (value instanceof Boolean) {
                    return value;
                }

                String stringValue = (String) value;
                if (stringValue.equalsIgnoreCase("true") || stringValue.equalsIgnoreCase("yes") || stringValue.equalsIgnoreCase("Y")) {
                    return true;
                } else if (stringValue.equalsIgnoreCase("false") || stringValue.equalsIgnoreCase("no") || stringValue.equalsIgnoreCase("N")) {
                    return false;
                } else {
                    Integer intValue = Integer.parseInt(stringValue);
                    if (intValue == 0) {
                        return false;
                    } else if (intValue == 1) {
                        return true;
                    } else {
                        return null;
                    }
                }
        }

        return null;
    }

    private static List<Object> getValueList(Object value) {
        List<Object> valueList = new ArrayList();

        if (value instanceof List) {
            valueList = (List) value;
        } else {
            valueList = Arrays.asList(((String) value).split(","));
        }

        return valueList;
    }
    
    public Map<String, String> getSampleAlias() {
        return sampleAlias;
    }

    public Map<String, VCFInfoDTO> getInfoFields() {
        return infoFields;
    }

    public Map<String, VCFInfoDTO> getFormatFields() {
        return formatFields;
    }
    
    private String getMD5sumForResource(String resourceURL,  File staticContentFolder) throws ParserInitializationException{
        
        if (resourceURL!=null && resourceURL.startsWith("local://")){
            if (staticContentFolder==null){
                throw new ParserInitializationException("staticContentBase parameter must be specificied for file served locally");
            }
            File bamFile = new File(staticContentFolder, resourceURL.replace("local://",""));
            if (!bamFile.exists() || !bamFile.canRead()){
                throw new ParserInitializationException(String.format("BAM file %s was not found or is not readable",bamFile.getAbsolutePath()));
            }
            try {
                return FileDigester.md5Digest(bamFile, VCFInsertControllerBean.checksumByteLimit);
            }catch (ChecksumException e){
                throw new ParserInitializationException(e);
            }
        }
        
        return null;
    }
}
