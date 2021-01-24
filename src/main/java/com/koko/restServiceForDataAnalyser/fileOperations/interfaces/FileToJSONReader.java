package com.koko.restServiceForDataAnalyser.fileOperations.interfaces;

public interface FileToJSONReader {
    String readFileToJSON(byte[] fileAsBytes, String fileName);
}
