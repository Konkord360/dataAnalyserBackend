package com.koko.restServiceForDataAnalyser.controller;

import com.koko.restServiceForDataAnalyser.fileOperations.BinaryFileToJSONReader;
import com.koko.restServiceForDataAnalyser.fileOperations.interfaces.FileToJSONReader;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;

import java.io.IOException;

@Controller
public class FileDataHandlingController {
    Logger logger = LoggerFactory.getLogger(FileDataHandlingController.class);

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/uploadFile", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadFile(@RequestParam("files") MultipartFile[] files) throws IOException {
        StringBuilder allFilesJson = new StringBuilder();
        allFilesJson.append("[");

        String fileJson;
        FileToJSONReader fileToJSONReader = new BinaryFileToJSONReader();
        for (MultipartFile file : files) {
            logger.info("Trying to process file: " + file.getOriginalFilename());
            fileJson = fileToJSONReader.readFileToJSON(file.getBytes(), file.getOriginalFilename());

            if (fileJson.equals("no sensors found in given file"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"errorMessage\":\"" + fileJson + "\"}");
            allFilesJson.append(fileJson).append(",");
            logger.info("File processing succeeded");
        }
        allFilesJson.deleteCharAt(allFilesJson.length() - 1);
        allFilesJson.append("]");
        return ResponseEntity.status(HttpStatus.OK).body(allFilesJson.toString());
    }
}
