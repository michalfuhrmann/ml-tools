package com.mfuhrmann.ml.tools.sentimentanalysis.words;

import java.util.Arrays;

public enum PosType {

    NOUN("n"), VERB("v"), ADJECTIVE("a"), ADJECTIVE_SATELITE("s"), ADVERB("r");

    private final String stringCode;


    PosType(String stringCode) {
        this.stringCode = stringCode;
    }

    public static PosType parse(String code) {
        return Arrays.stream(PosType.values())
                .filter(posType -> posType.stringCode.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported pos type " + code));
    }
}
