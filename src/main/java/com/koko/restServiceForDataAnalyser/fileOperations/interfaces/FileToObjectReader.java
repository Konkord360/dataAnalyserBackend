package com.koko.restServiceForDataAnalyser.fileOperations.interfaces;

public interface FileToObjectReader {
    public String readFileToJSON(byte[] fileAsBytes, String fileName);
}
