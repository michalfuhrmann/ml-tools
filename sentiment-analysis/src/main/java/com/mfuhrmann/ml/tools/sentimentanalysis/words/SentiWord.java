package com.mfuhrmann.ml.tools.sentimentanalysis.words;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Document(collection = "sentiword")
public class SentiWord {


    @NotNull
    private PosType posType;
    @NotNull
    private Integer wordId;
    @NotNull
    private BigDecimal positivityScore;
    @NotNull
    private BigDecimal negativityScore;
    @NotEmpty
    private List<Word> words;
    @NotEmpty
    private String description;
    @NotEmpty
    private List<String> examples;


    public SentiWord(PosType posType, Integer wordId, BigDecimal positivityScore, BigDecimal negativityScore, List<Word> words, String description, List<String> examples) {
        this.posType = posType;
        this.wordId = wordId;
        this.positivityScore = positivityScore;
        this.negativityScore = negativityScore;
        this.words = words;
        this.description = description;
        this.examples = examples;
    }

    public PosType getPosType() {
        return posType;
    }

    public Integer getWordId() {
        return wordId;
    }

    public BigDecimal getPositivityScore() {
        return positivityScore;
    }

    public BigDecimal getNegativityScore() {
        return negativityScore;
    }

    public List<Word> getWords() {
        return words;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getExamples() {
        return examples;
    }

    public static class Word {

        @Indexed
        private String word;
        private Integer rank;

        private Word() {
        }

        public Word(String word, Integer rank) {
            this.word = word;
            this.rank = rank;
        }

        public String getWord() {
            return word;
        }

        public Integer getRank() {
            return rank;
        }

        @Override
        public String toString() {
            return "Word{" +
                    "word='" + word + '\'' +
                    ", rank=" + rank +
                    '}';
        }
    }
}
