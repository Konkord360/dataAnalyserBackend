package com.koko.restServiceForDataAnalyser.fileOperations;


import com.koko.restServiceForDataAnalyser.fileOperations.interfaces.FileToJSONReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryFileToJSONReader implements FileToJSONReader {
    private final int headerSize = 4500;
    private int seriesLength = 16;
    private int seriesLengthInBytes = 32;

    @Override
    public String readFileToJSON(byte[] fileAsBytes, String fileName) {
        this.seriesLength = detectNumberOfSensors(fileAsBytes);

        if(this.seriesLength == 0)
            return "no sensors found in given file";

        this.seriesLengthInBytes = this.seriesLength * 2;

        StringBuilder fileJSON = new StringBuilder();
        String[] seriesMeasures = readContent(fileAsBytes);
        fileJSON.append("{\"NumberOfSensors\":").append(this.seriesLength).append(",");
        fileJSON.append("\"FileName\":\"").append(fileName).append("\",");
        fileJSON.append("\"SeriesValues\":[");
        for (int i = 0; i < seriesLength; i++) {
            fileJSON.append(seriesMeasures[i]);
        }
        fileJSON.append("]}");
        return String.valueOf(fileJSON);
    }

    public String readHeader(byte[] fileAsBytes) {
        StringBuilder headerBuilder = new StringBuilder();
        for (int i = 0; i < this.headerSize; i++) {
            char readSign = (char) fileAsBytes[i];
            if ((int) readSign != 0 && readSign != ' ' && readSign != '\t' && readSign != '\r' && readSign != '\n' && readSign != 0x0b)
                headerBuilder.append(readSign);
        }

        return String.valueOf(headerBuilder);
    }

    public String[] readContent(byte[] fileAsBytes) {
        String[] seriesMeasures = new String[seriesLength];
        StringBuilder[] seriesBuilder = new StringBuilder[seriesLength];

        for (int i = this.headerSize; i < fileAsBytes.length; i = i + seriesLengthInBytes) {
            readSeries(seriesBuilder, i, fileAsBytes);
            i = i + 2;
        }

        for (int j = 0; j < seriesLength - 1; j++) {
            seriesBuilder[j].deleteCharAt(seriesBuilder[j].length() - 1);
            seriesMeasures[j] = String.valueOf(seriesBuilder[j].append("],"));
        }

        seriesBuilder[seriesLength - 1].deleteCharAt(seriesBuilder[seriesLength - 1].length() - 1);
        seriesMeasures[seriesLength - 1] = String.valueOf(seriesBuilder[seriesLength - 1].append("]"));

        return seriesMeasures;
    }

    public void readSeries(StringBuilder[] seriesBuilder, int startingPosition, byte[] fileAsBytes) {
        int sensorNumber = 1;
        for (int i = startingPosition; i < startingPosition + seriesLengthInBytes; i = i + 2) {

            if (seriesBuilder[sensorNumber - 1] == null)
                seriesBuilder[sensorNumber - 1] = new StringBuilder("[");

            int redValue = 0;
            if (i < fileAsBytes.length - 1) {
                redValue = readValueFromBytesInLittleEndianOrder(fileAsBytes[i], fileAsBytes[i + 1]);
            }

            seriesBuilder[sensorNumber - 1].append(redValue);
            seriesBuilder[sensorNumber - 1].append(",");
            sensorNumber++;
        }
    }

    public int detectNumberOfSensors(byte[] fileAsBytes) {
        int redValue;
        int i = this.headerSize;
        int numberOfSensors = 0;

        if (i + 1 > fileAsBytes.length)
            return 0;

        while (i < fileAsBytes.length) {

            redValue = readValueFromBytesInLittleEndianOrder(fileAsBytes[i], fileAsBytes[i + 1]);

            if (redValue == 0)
                break;
            numberOfSensors++;
            i = i + 2;
        }
        return numberOfSensors;
    }

    public int readValueFromBytesInLittleEndianOrder(byte firstByte, byte secondByte) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(firstByte);
        buffer.put(secondByte);
        buffer.put((byte) 0x00);
        buffer.put((byte) 0x00);
        buffer.flip();
        return buffer.getInt();
    }
}