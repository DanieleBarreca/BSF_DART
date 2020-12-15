package org.open.medgen.dart.loader.job.model;

import org.junit.Assert;
import org.junit.Test;
import org.open.medgen.dart.core.model.mongo.coverage.CoverageEntry;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CoverageFileTest {
    private ClassLoader classLoader = getClass().getClassLoader();
    private File coverageFile = new File(classLoader.getResource("coverageFile.tsv").getFile());


    @Test
    public void testParsingConfig() throws IOException {

        CoverageFileParser coverageFileParser = new CoverageFileParser(coverageFile);

        List<CoverageEntry> entryList = coverageFileParser.parse();

        Assert.assertEquals(entryList.size(), 34);

        CoverageEntry firstEntry = entryList.get(0);
        Assert.assertNotNull(firstEntry);
        Assert.assertEquals(firstEntry.getChrom(),"1");
        Assert.assertEquals(firstEntry.getStart(), 12099);
        Assert.assertEquals(firstEntry.getEnd(), 12258);
        Assert.assertEquals(firstEntry.getMappingStatus(), CoverageEntry.MappingStatus.POOR_MAPPING_QUALITY);
        Assert.assertEquals(firstEntry.getBucket(),"1-0");

        Assert.assertEquals(firstEntry.getGeneNames().size(),1);
        Assert.assertEquals(firstEntry.getGeneNames().get(0),"DDX11L1");

        Assert.assertEquals(firstEntry.getGeneIds().size(),1);
        Assert.assertEquals(firstEntry.getGeneIds().get(0),"ENSG00000223972");

        Assert.assertEquals(firstEntry.getTranscriptIds().size(),1);
        Assert.assertEquals(firstEntry.getTranscriptIds().get(0),"ENST00000456328");

        Assert.assertEquals(firstEntry.getExonIds().size(),1);
        Assert.assertEquals(firstEntry.getExonIds().get(0),"ENSE00002234944");

        CoverageEntry otherEntry = entryList.get(33);
        Assert.assertNotNull(otherEntry);
        Assert.assertEquals(otherEntry.getChrom(),"1");
        Assert.assertEquals(otherEntry.getStart(), 1222886);
        Assert.assertEquals(otherEntry.getEnd(), 1222944);
        Assert.assertEquals(otherEntry.getMappingStatus(), CoverageEntry.MappingStatus.LOW_COVERAGE);
        Assert.assertEquals(otherEntry.getBucket(),"1-1");

        Assert.assertEquals(otherEntry.getGeneNames().size(),1);
        Assert.assertEquals(otherEntry.getGeneNames().get(0),"SCNN1D");

        Assert.assertEquals(otherEntry.getGeneIds().size(),1);
        Assert.assertEquals(otherEntry.getGeneIds().get(0),"ENSG00000162572");

        Assert.assertEquals(otherEntry.getTranscriptIds().size(),4);
        Assert.assertEquals(otherEntry.getTranscriptIds().get(0),"ENST00000325425");
        Assert.assertEquals(otherEntry.getTranscriptIds().get(3),"ENST00000400928");

        Assert.assertEquals(otherEntry.getExonIds().size(),1);
        Assert.assertEquals(otherEntry.getExonIds().get(0),"ENSE00003678929");

    }

}
