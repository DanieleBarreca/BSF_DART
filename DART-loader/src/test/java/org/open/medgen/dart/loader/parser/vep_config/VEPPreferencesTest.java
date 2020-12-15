package org.open.medgen.dart.loader.parser.vep_config;

import org.junit.Before;
import org.junit.Test;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.FieldType;
import org.open.medgen.dart.core.model.rdbms.dto.vcf.field.VCFInfoDTO;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.*;

public class VEPPreferencesTest {

    private VEPPreferences vepPreferences;

    @Before
    public void init() {
        this.vepPreferences = new VEPPreferences.Builder().build();
    }

    @Test
    public void compareWithFile() {
        VEPPreferences.Builder builder = new VEPPreferences.Builder();
        builder.setEnumFile(new File("./src/main/resources/VEPEnums.tsv"));
        builder.setVepFieldsFile(new File("./src/main/resources/VEPOutputFields.tsv"));
        VEPPreferences preferences = builder.build();
        assertEquals(preferences.getVepEnums().keySet(), this.vepPreferences.getVepEnums().keySet());
        assertEquals(preferences.getVepFields().keySet(), this.vepPreferences.getVepFields().keySet());

        assertEquals(preferences.getVepEnums(), this.vepPreferences.getVepEnums());
        assertEquals(preferences.getVepFields(), this.vepPreferences.getVepFields());
    }

    @Test
    public void overwriteDefaultValue() {
        VEPPreferences.Builder builder = new VEPPreferences.Builder();
        builder.setVepFieldsStream(new ByteArrayInputStream(
                ("FieldName\tFieldType\tFieldNumber\tFieldDescription\n" +
                 "Allele\tString\t1\tTEST\n")
                        .getBytes()));
        VEPPreferences preferences = builder.build();
        assertEquals("TEST", preferences.getVepFields().get("allele").getDescription());
    }

    @Test(expected = RuntimeException.class)
    public void loadNofile() {
        VEPPreferences.Builder builder = new VEPPreferences.Builder();
        builder.setEnumFile(new File("FILE_DOES_NOT_EXIST"));
        VEPPreferences preferences = builder.build();
    }

    @Test
    public void getVepFields() {
        Map<String, VCFInfoDTO> vepFields = this.vepPreferences.getVepFields();
        VCFInfoDTO gnomad_af = vepFields.get("gnomad_af");
        assertNotNull(gnomad_af);
        assertEquals(FieldType.ARRAY_DOUBLE, gnomad_af.getType());
        assertEquals("Float", gnomad_af.getVcfType().name());
    }

    @Test
    public void getSplitFields() {
        Map<String, VEPSplitField> splitFields = this.vepPreferences.getSplitFields();
        VEPSplitField sift = splitFields.get("sift");
        assertNotNull(sift);
        assertEquals("\\(", sift.getSeparator());
        assertArrayEquals(new String[]{"SIFT_prediction", "SIFT_score"},sift.getSubFieldsNames().toArray(new String[0]));

    }

    @Test
    public void getVepEnums() {
        Map<String, Set<OntologyTerm>> vepEnums = this.vepPreferences.getVepEnums();
        Set<OntologyTerm> sift_prediction = vepEnums.get("CSQ:SIFT_prediction");
        assertNotNull(sift_prediction);
        assertTrue(sift_prediction.stream().map(OntologyTerm::getTerm).anyMatch(p -> Objects.equals(p, "deleterious")));
    }
}