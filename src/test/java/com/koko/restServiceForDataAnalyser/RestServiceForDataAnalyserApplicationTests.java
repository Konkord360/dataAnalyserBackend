package com.koko.restServiceForDataAnalyser;

import com.koko.restServiceForDataAnalyser.fileOperations.BinaryFileReader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RestServiceForDataAnalyserApplicationTests {
    private String pathToFile = "D:\\Projects\\inz\\rest\\restServiceForDataAnalyser\\src\\test\\java\\com\\koko\\restServiceForDataAnalyser\\testFile.ccrdf";

    @Test
    void contextLoads() {
//        BigInteger valueToWrite = BigInteger.valueOf(45123);
//        Integer
//        try (FileOutputStream stream = new FileOutputStream(pathToFile)) {
//            for(int i = 0; i < 16; i++){
//                byte[] bytesToWrite = BigInteger.valueOf(23111 + i).toByteArray();
//                stream.write(bytesToWrite);
//            }
//        }
//        catch (Exception e){
//            assertEquals("a", "b");
//        }
    }

    @Test
    void checkIfBinaryFileReaderProperlyConvertsBinaryNumbers(){
//        BinaryFileReader binaryFileReader = BinaryFileReader.INSTANCE;
//        String fileJson = binaryFileReader.readFileToJSON(pathToFile);
//        System.out.println(fileJson);
    }

}