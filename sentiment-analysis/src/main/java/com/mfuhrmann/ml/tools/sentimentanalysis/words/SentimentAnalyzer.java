package com.mfuhrmann.ml.tools.sentimentanalysis.words;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SentimentAnalyzer {

    private final SentiWordDao sentiWordDao;

    @Autowired
    public SentimentAnalyzer(SentiWordDao sentiWordDao) {
        this.sentiWordDao = sentiWordDao;
    }


    public SentimentReport getSentimentDataForSentence(List<String> sentenceTokens) {


        Map<String, List<SentiWord>> sentiWords = sentenceTokens.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        s -> sentiWordDao.findByWords_Word(s).collect(Collectors.toList()),
                        (s1, s2) -> Stream.concat(s1.stream(), s2.stream())
                                .collect(Collectors.toList())));

        System.out.println("found tokens");

        sentiWords.entrySet().stream().filter(e -> !e.getValue().isEmpty()).forEach(e -> System.out.println(e.getKey()));

        System.out.println("finished tokens");


        BigDecimal positivityScore = getScore(sentiWords, SentiWord::getPositivityScore);

        BigDecimal negativityScore = getScore(sentiWords, SentiWord::getNegativityScore);


        return new SentimentReport(positivityScore, negativityScore);
    }


    private BigDecimal getScore(Map<String, List<SentiWord>> sentiWords, Function<SentiWord, BigDecimal> function) {
        return sentiWords.entrySet().stream()
                .filter(stringListEntry -> !stringListEntry.getValue().isEmpty())
                .map(entry -> entry.getValue().stream()
                        .map(function)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO)
                        .divide(BigDecimal.valueOf(sentiWords.get(entry.getKey()).size()), RoundingMode.HALF_UP))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }


    static class SentimentReport {


        private final BigDecimal positivityScore;
        private final BigDecimal negativityScore;

        SentimentReport(BigDecimal positivityScore, BigDecimal negativityScore) {
            this.positivityScore = positivityScore;
            this.negativityScore = negativityScore;
        }

        public BigDecimal getPositivityScore() {
            return positivityScore;
        }

        public BigDecimal getNegativityScore() {
            return negativityScore;
        }


        @Override
        public String toString() {
            return "SentimentReport{" +
                    "positivityScore=" + positivityScore +
                    ", negativityScore=" + negativityScore +
                    '}';
        }
    }
}


