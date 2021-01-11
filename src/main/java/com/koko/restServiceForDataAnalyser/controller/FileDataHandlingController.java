package com.koko.restServiceForDataAnalyser.controller;

import com.koko.restServiceForDataAnalyser.fileOperations.BinaryFileReader;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Objects;

@Controller
@CrossOrigin(origins = "*")
public class FileDataHandlingController {
    Logger logger = LoggerFactory.getLogger(FileDataHandlingController.class);

    @GetMapping("/getFileData")
    public ResponseEntity<String> getDataFromFile() {
        logger.info("getFileData invoked");
        String fileJson = null;
        try {
            Path fileName = Path.of("Module1.JSON");
            fileJson = Files.readString(fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"No files for analysis are available on the server\"}");
        }

        return ResponseEntity.status(HttpStatus.OK).body(fileJson);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/uploadFile", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("upload file Invoked with" + file.getOriginalFilename());

        BinaryFileReader binaryFileReader = BinaryFileReader.INSTANCE;
        logger.info("Trying to process file: " + file.getOriginalFilename());
        try {
            String pathToFile = "src\\main\\resources\\".concat(Objects.requireNonNull(file.getOriginalFilename()));
            file.transferTo(Path.of(pathToFile));
            String fileJson = binaryFileReader.readFileToJSON(pathToFile);
            try {
                Path fileName = Path.of("Module1.JSON");
                Files.writeString(fileName, fileJson);
                return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"OK\",\"status\":\"success\"}");
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("Failed to process files");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Error with file handling\",\"status\":\"failed\"}");
            }
        } catch (Exception e) {
            logger.info(e.toString());
            logger.info("Failed to process files");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Error with file handling\",\"status\":\"failed\"}");
        }
    }
}
