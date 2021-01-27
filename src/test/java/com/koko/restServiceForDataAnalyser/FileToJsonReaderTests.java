package com.koko.restServiceForDataAnalyser;

import com.koko.restServiceForDataAnalyser.fileOperations.BinaryFileToJSONReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileToJsonReaderTests {
    BinaryFileToJSONReader fileToJSONReader;
    byte[] testFileWith16Sensors;
    byte[] testFileWith3Sensors;
    byte[] testFileWithHeaderOnly;
    byte[] testFileWithOneSeriesOnly;

    @BeforeAll
    public void readFilesToByteArrays() throws IOException {
        testFileWith16Sensors = Files.readAllBytes(Paths.get("src/test/java/com/koko/restServiceForDataAnalyser/testFileWith16Sensors.ccrdf"));
        testFileWith3Sensors = Files.readAllBytes(Paths.get("src/test/java/com/koko/restServiceForDataAnalyser/testFileWith3Sensors.ccrdf"));
        testFileWithHeaderOnly = Files.readAllBytes(Paths.get("src/test/java/com/koko/restServiceForDataAnalyser/testFileWithHeaderOnly.ccrdf"));
        testFileWithOneSeriesOnly = Files.readAllBytes(Paths.get("src/test/java/com/koko/restServiceForDataAnalyser/testFileWithOneSeriesOnly.ccrdf"));
        this.fileToJSONReader = new BinaryFileToJSONReader();
    }

    @Test
    public void testReadHeaderReturnsCorrectString() {
        assertEquals("V3.05DemoAFR-L2020-08-3118.43.43DemoAMeasurements10Test120138,373100", fileToJSONReader.readHeader(testFileWith16Sensors));
    }

    @Test
    public void testReadValueFromBytesInLittleEndianOrderReadsTheNumberCorrectly() {
        //4ABC = 19132 in decimal system in little endian it would be written BC4A
        byte a = (byte) 0x4A;
        byte b = (byte) 0xBC;

        assertEquals(fileToJSONReader.readValueFromBytesInLittleEndianOrder(b, a), 19132);
    }

    @Test
    public void testDetectNumberOfSensorsDetectsTheNumberCorrectlyForFileWith16Sensors() {
        assertEquals(16, fileToJSONReader.detectNumberOfSensors(testFileWith16Sensors));
    }

    @Test
    public void testDetectNumberOfSensorsReturns0ForFileWithNoSensors() {
        assertEquals(0, fileToJSONReader.detectNumberOfSensors(testFileWithHeaderOnly));
    }

    @Test
    public void testDetectNumberOfSensorsReturnsCorrectNumberWhenOnlyOneSeriesPresent() {
        assertEquals(16, fileToJSONReader.detectNumberOfSensors(testFileWithOneSeriesOnly));
    }

    @Test
    public void testReadSeriesBeginsCorrectlyEachSeriesByOpeningJSONArray() {
        StringBuilder[] testSeriesBuilder = new StringBuilder[16];
        String[] firstNumbersOfEachSeries = {"[30039,", "[30361,", "[38022,", "[29178,", "[20841,", "[22137,", "[19705,",
                "[25856,", "[26223,", "[26405,", "[23500,", "[27699,", "[22351,", "[5992,", "[18995,", "[13515,"};

        fileToJSONReader.readSeries(testSeriesBuilder, 4500, testFileWith16Sensors);
        for (int i = 0; i < firstNumbersOfEachSeries.length; ++i) {
            assertEquals(firstNumbersOfEachSeries[i], testSeriesBuilder[i].toString());
        }
    }

    @Test
    public void testReadSeriesReadsCorrectlyMiddleValuesInArrays() {
        StringBuilder[] testSeriesBuilder = new StringBuilder[16];
        String[] firstNumbersOfEachSeries = {"[30039,30194,", "[30361,30744,", "[38022,37962,", "[29178,29308,", "[20841,20990,", "[22137,22686,", "[19705,19742,",
                "[25856,25919,", "[26223,26382,", "[26405,26692,", "[23500,23740,", "[27699,27956,", "[22351,22463,", "[5992,5855,", "[18995,19116,", "[13515,13083,"};

        fileToJSONReader.readSeries(testSeriesBuilder, 4500, testFileWith16Sensors);
        fileToJSONReader.readSeries(testSeriesBuilder, 4534, testFileWith16Sensors);

        for (int i = 0; i < firstNumbersOfEachSeries.length; ++i) {
            assertEquals(firstNumbersOfEachSeries[i], testSeriesBuilder[i].toString());
        }
    }

    @Test
    public void testReadContentReturnsPreparedJSONArrays() {
        String[] firstNumbersOfEachSeries = {"[30039,30194],", "[30361,30744],", "[38022,37962],", "[29178,29308],", "[20841,20990],", "[22137,22686],",
                "[19705,19742],", "[25856,25919],", "[26223,26382],", "[26405,26692],", "[23500,23740],", "[27699,27956],", "[22351,22463],", "[5992,5855],",
                "[18995,19116],", "[13515,13083]"};
        System.out.println(firstNumbersOfEachSeries.length);
        String[] content = fileToJSONReader.readContent(testFileWith16Sensors);
        for (int i = 0; i < content.length; ++i) {
            assertEquals(firstNumbersOfEachSeries[i], content[i]);
        }
    }

    @Test
    public void testReadFileToJSONReturnsCorrectJSONWhenRedFileWith16Sensors() {
        assertEquals("{\"NumberOfSensors\":16,\"FileName\":\"testFile\",\"SeriesValues\":[[30039,30194],[30361,30744]," +
                "[38022,37962],[29178,29308],[20841,20990],[22137,22686],[19705,19742],[25856,25919]," +
                "[26223,26382],[26405,26692],[23500,23740],[27699,27956],[22351,22463],[5992,5855],[18995,19116],[13515,13083]]}",
                fileToJSONReader.readFileToJSON(testFileWith16Sensors, "testFile"));

    }

    @Test
    public void testReadFileToJSONReturnsCorrectJSONWhenRedFileWith3Sensors() {
        assertEquals("{\"NumberOfSensors\":3,\"FileName\":\"testFile\",\"SeriesValues\":[[30039,30194],[30361,30744],[38022,37962]]}",
                fileToJSONReader.readFileToJSON(testFileWith3Sensors, "testFile"));
    }

    @Test
    public void testReadFileToJSONReturnsErrorMessageWhenNoSensorsArePresentInFile(){
        assertEquals("no sensors found in given file", fileToJSONReader.readFileToJSON(testFileWithHeaderOnly, "testFile"));
    }

    @AfterEach
    public void afterEach(){
        this.fileToJSONReader = new BinaryFileToJSONReader();
    }
}