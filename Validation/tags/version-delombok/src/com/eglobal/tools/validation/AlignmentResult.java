package com.eglobal.tools.validation;


public class AlignmentResult {
    private int totalAligned;
    private int greenCount;
    private int nonGreenCount;

    public AlignmentResult(int totalAligned, int greenCount, int nonGreenCount) {
        this.totalAligned = totalAligned;
        this.greenCount = greenCount;
        this.nonGreenCount = nonGreenCount;
    }

    public int getTotalAligned() {
        return totalAligned;
    }

    public int getGreenCount() {
        return greenCount;
    }

    public int getNonGreenCount() {
        return nonGreenCount;
    }
}