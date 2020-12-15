package org.open.medgen.dart.loader.parser;

import htsjdk.variant.vcf.*;
import org.junit.Before;
import org.junit.Test;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFFileDTO;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.VCFType;
import org.open.medgen.dart.loader.job.model.JobConfigParser;
import org.open.medgen.dart.loader.parser.vep_config.VEPPreferences;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class VCFParserTest {

    public static final String VCF_FILE_NAME = "vcf_file_name.vep";
    public static final String GENE_FIELD = "SYMBOL";
    public static final String GENOMIC_CHANGE_FIELD = "HGVSg";
    public static final String CODING_CHANGE_FIELD = "HGVSc";
    private VEPPreferences vepPreferences;

    @Test(expected = ParserInitializationException.class)
    public void constractHeaderFailNoVep() throws ParserInitializationException, IOException {
        VCFHeader header = new VCFHeader();
        JobConfigParser jobConfig = new JobConfigParser();
        jobConfig.load(null);
        String geneField = "";
        new VCFParser(vepPreferences, header, "vcf_file_name.vep", VCFType.GERMLINE, jobConfig, geneField, "", "",null,null);
    }

    @Before
    public void init() {
        this.vepPreferences = new VEPPreferences.Builder().build();
    }

    @Test
    public void constractHeader() throws ParserInitializationException, IOException {
        VCFParser parser = prepareParser();
        VCFFileDTO fileDTO = parser.getHeader();

        assertEquals(parser.getSampleAlias().keySet(), new HashSet<>(Collections.singleton("TEST")));
        assertEquals(fileDTO.getVcfFileName(), VCF_FILE_NAME);
        assertTrue(fileDTO.getVcfFields().stream().map(e -> e.getVcfFieldName())
                .collect(Collectors.toSet()).contains("CSQ:gnomAD_SAS_AF"));
        assertTrue(fileDTO.getVcfFields().stream().map(e -> e.getVcfFieldName())
                .collect(Collectors.toSet()).contains("CSQ:Allele"));

        assertFalse(parser.isSV());
        assertFalse(parser.isSomatic());
        assertTrue(parser.isOfType(VCFType.GERMLINE));
    }

    @Test
    public void checkFields() throws ParserInitializationException {
        VCFParser vcfParser = prepareParser(header -> {
            header.addMetaDataLine(
                    new VCFInfoHeaderLine("AF", VCFHeaderLineCount.A, VCFHeaderLineType.Float, "bla"));
            header.addMetaDataLine(
                    new VCFInfoHeaderLine("DP", 1, VCFHeaderLineType.Integer, "bla"));
            header.addMetaDataLine(
                    new VCFFormatHeaderLine("GQ", 1, VCFHeaderLineType.Integer, "bla")
            );
        });
        assertEquals(vcfParser.getFormatFields().size(), 1);
        assertEquals(vcfParser.getInfoFields().size(), 2);
        assertEquals(vcfParser.getFormatFields().keySet(), new HashSet<>(Arrays.asList("GQ")));
        assertEquals(vcfParser.getInfoFields().keySet(), new HashSet<>(Arrays.asList("AF", "DP")));
    }

    public VCFParser prepareParser() throws ParserInitializationException {
        return prepareParser(null);
    }
    public VCFParser prepareParser(Consumer<VCFHeader> consumer) throws ParserInitializationException {
        VCFHeader header = new VCFHeader();
        // VEP meta info
        header.addMetaDataLine(new VCFHeaderLine("VEP","\"v91\" time=\"2019-05-14 16:13:11\" assembly=\"GRCh37.p13\""));
        // VEP CSQ annotation
        header.addMetaDataLine(new VCFInfoHeaderLine(
               "CSQ", VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String,
                "Consequence annotations from Ensembl VEP. Format: Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|gnomAD_SAS_AF|ALLELE_NUM"
        ));
        header.getGenotypeSamples().add("TEST");
        header.getSampleNamesInOrder().add("TEST");
        if (!Objects.isNull(consumer)) {
            consumer.accept(header);
        }
        JobConfigParser jobConfig = new JobConfigParser();
        jobConfig.addSample(new JobConfigParser.SampleConfig("*","TEST_ALIAS","test-{sample}-vcf","test-{sample}-bam","test-{sample}-url","test-{sample}-cov"));
        return new VCFParser(this.vepPreferences, header, VCF_FILE_NAME, VCFType.GERMLINE, jobConfig, GENE_FIELD, GENOMIC_CHANGE_FIELD, CODING_CHANGE_FIELD,null,null);
    }
}