package com.erod.assignment;

public enum CSVDataType {
    DATETIME(1), LATITUDE(2), LONGITUDE(3);

    private final int columnIndex;

    CSVDataType(int columnIndex) {
        this.columnIndex = columnIndex;
    }

}
