package com.koko.restServiceForDataAnalyser;

import com.koko.restServiceForDataAnalyser.controller.FileDataHandlingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@WebMvcTest(FileDataHandlingController.class)
public class FileDataHandlingControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Test
    public void testControllerReturnsHTTP400WithMessageWhenRequestedWithFileWithoutSensors() throws Exception {
        MockMultipartFile[] files = new MockMultipartFile[2];
        files[0] = new MockMultipartFile(
                "files",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        MvcResult mvcResult = mockMvc.perform(multipart("/uploadFile").file(files[0]).param("files", "file")).andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("{\"errorMessage\":\"no sensors found in given file\"}");
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
    }

    @Test
    public void testControllerReturnsHTTP200WithFileConvertedToJSONWhenRequestedWithValidFile() throws Exception {
        byte[] testFileWith3Sensors = Files.readAllBytes(Paths.get("src\\test\\java\\com\\koko\\restServiceForDataAnalyser\\testFileWith3Sensors.ccrdf"));

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "files",
                "Module1.ccrdf",
                MediaType.TEXT_PLAIN_VALUE,
                testFileWith3Sensors
        );

        MvcResult mvcResult = mockMvc.perform(multipart("/uploadFile").file(mockMultipartFile).param("files", "file")).andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("[{\"NumberOfSensors\":3,\"FileName\":\"Module1.ccrdf\",\"SeriesValues\":[[30039,30194],[30361,30744],[38022,37962]]}]");
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    public void testControllerReturnsHTTP200WithFileConvertedToJSONWhenRequestedWithTwoValidFiles() throws Exception {
        byte[] testFileWith3Sensors = Files.readAllBytes(Paths.get("src\\test\\java\\com\\koko\\restServiceForDataAnalyser\\testFileWith3Sensors.ccrdf"));
        byte[] testFileWith16Sensors = Files.readAllBytes(Paths.get("src\\test\\java\\com\\koko\\restServiceForDataAnalyser\\testFileWith16Sensors.ccrdf"));

        MockMultipartFile mockMultipartFile1 = new MockMultipartFile(
                "files",
                "Module1.ccrdf",
                MediaType.TEXT_PLAIN_VALUE,
                testFileWith3Sensors
        );

        MockMultipartFile mockMultipartFile2 = new MockMultipartFile(
                "files",
                "Module2.ccrdf",
                MediaType.TEXT_PLAIN_VALUE,
                testFileWith16Sensors
        );

        MvcResult mvcResult = mockMvc.perform(multipart("/uploadFile")
                .file(mockMultipartFile1)
                .file(mockMultipartFile2)
                .param("files", "file")).andReturn();
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo("[{\"NumberOfSensors\":3,\"FileName\":\"Module1.ccrdf\",\"SeriesValues\":[[30039,30194],[30361,30744]," +
                        "[38022,37962]]},{\"NumberOfSensors\":16,\"FileName\":\"Module2.ccrdf\",\"SeriesValues\":[[30039,30194]," +
                        "[30361,30744],[38022,37962],[29178,29308],[20841,20990],[22137,22686],[19705,19742],[25856,25919]," +
                        "[26223,26382],[26405,26692],[23500,23740],[27699,27956],[22351,22463],[5992,5855],[18995,19116],[13515,13083]]}]");
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }
}
