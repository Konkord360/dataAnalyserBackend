package com.koko.restServiceForDataAnalyser.fileOperations;


import com.koko.restServiceForDataAnalyser.fileOperations.interfaces.FileToObjectReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public enum BinaryFileReader implements FileToObjectReader {
    INSTANCE;
    private byte[] fileAsBytes;
    private final int headerSize = 4500;
    private int seriesLength = 16;
    private int seriesLengthInBytes = 32;


//    @Override
//    public String readFileToJSON(String filePath) {
//        openFile(filePath);
//        StringBuilder fileJSON = new StringBuilder("");
//        String seriesMeasures[] = readContent();
//        fileJSON.append("{");
//        for(int i = 0; i < seriesLength; i++){
//            fileJSON.append(seriesMeasures[i]);
//        }
//        fileJSON.append("}");
//        return String.valueOf(fileJSON);
//    }

    @Override
    public String readFileToJSON(byte[] fileAsBytes, String fileName){
        this.fileAsBytes = fileAsBytes;
        detectNumberOfSensors();

        StringBuilder fileJSON = new StringBuilder("");
        String seriesMeasures[] = readContent();
        fileJSON.append("{\"NumberOfSensors\":").append(this.seriesLength).append(",");
        fileJSON.append("\"FileName\":\"").append(fileName).append("\",");
        fileJSON.append("\"SeriesValues\":[");
        for(int i = 0; i < seriesLength; i++){
            fileJSON.append(seriesMeasures[i]);
        }
        fileJSON.append("]}");
        return String.valueOf(fileJSON);
    }

    public void openFile(String file) {
        try {
            this.fileAsBytes = Files.readAllBytes(Paths.get(String.valueOf(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readHeader() {
        StringBuilder headerBuilder = new StringBuilder("");
        for (int i = 0; i < this.headerSize; i++) {
            char readSign = (char) this.fileAsBytes[i];
            headerBuilder.append(readSign);
        }

        return String.valueOf(headerBuilder);
    }

    public String[] readContent() {
        String[] seriesMeasures = new String[seriesLength];
        StringBuilder[] seriesBuilder = new StringBuilder[seriesLength];
        long measurementPosition = 0;

        for (int i = this.headerSize; i < this.fileAsBytes.length; i = i + seriesLengthInBytes) {
            readSeries(seriesBuilder, i, measurementPosition);
            measurementPosition++;
            i = i + 2;
        }

        for (int j = 0; j < seriesLength - 1; j++) {
            seriesBuilder[j].deleteCharAt(seriesBuilder[j].length() - 1);
            seriesMeasures[j] = String.valueOf(seriesBuilder[j].append("],"));
        }

        seriesBuilder[seriesLength  - 1].deleteCharAt(seriesBuilder[seriesLength - 1].length() - 1);
        seriesMeasures[seriesLength - 1] = String.valueOf(seriesBuilder[seriesLength - 1].append("]"));

        return seriesMeasures;
    }

    private void readSeries(StringBuilder[] seriesBuilder, int startingPosition, long measurementPosition) {
        int sensorNumber = 1;
        byte[] bytesToRead = new byte[2];

        for (int i = startingPosition; i < startingPosition + seriesLengthInBytes; i = i + 2) {

            if(sensorNumber > seriesLength)
                sensorNumber = 1;

            if (seriesBuilder[sensorNumber - 1] == null)
                seriesBuilder[sensorNumber - 1] = new StringBuilder("[");

            int redValue = 0;
            if (i < this.fileAsBytes.length - 1){
                ByteBuffer buffer = ByteBuffer.allocateDirect(4);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                bytesToRead[0] = this.fileAsBytes[i];
                bytesToRead[1] = this.fileAsBytes[i + 1];
                buffer.put(this.fileAsBytes[i]);
                buffer.put(this.fileAsBytes[i + 1]);
                buffer.put((byte)0x00);
                buffer.put((byte)0x00);
                buffer.flip();
                redValue = buffer.getInt();
            }
            seriesBuilder[sensorNumber - 1].append(redValue);
            seriesBuilder[sensorNumber - 1].append(",");
            sensorNumber++;
        }
    }

    private void detectNumberOfSensors(){
        int redValue = 0;
        int i = this.headerSize;
        int numberOfSensors = 0;
        byte[] bytesToRead = new byte[2];

        for(;;){
            ByteBuffer buffer = ByteBuffer.allocateDirect(4);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            bytesToRead[0] = this.fileAsBytes[i];
            bytesToRead[1] = this.fileAsBytes[i + 1];
            buffer.put(this.fileAsBytes[i]);
            buffer.put(this.fileAsBytes[i + 1]);
            buffer.put((byte)0x00);
            buffer.put((byte)0x00);
            buffer.flip();
            redValue = buffer.getInt();

            if(redValue == 0){
                buffer.clear();
                break;
            }


            numberOfSensors++;
            i = i + 2;
        }
        this.seriesLength = numberOfSensors;
        this.seriesLengthInBytes = numberOfSensors * 2;
    }
}