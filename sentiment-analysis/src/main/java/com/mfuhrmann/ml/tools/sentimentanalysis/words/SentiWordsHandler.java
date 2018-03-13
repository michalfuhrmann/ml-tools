package com.mfuhrmann.ml.tools.sentimentanalysis.words;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SentiWordsHandler {


    @Autowired
    private SentiWordDao sentiWordDao;

    @PostConstruct
    void init() throws IOException, URISyntaxException {

        long count = sentiWordDao.count();

        if (count <= 0) {
            initializeDb();
        }
        System.out.println("Finished initialization of SentiWord database");

    }

    private void initializeDb() throws URISyntaxException, IOException {
        Stream<String> lines = Files.lines(Paths.get(ClassLoader.getSystemResource("SentiWord.txt").toURI()));

        List<SentiWord> sentiWords = lines
                .dropWhile(line -> line.startsWith("#"))
                .map(s -> {
                    String[] row = s.split("\\t");

                    PosType posType = PosType.parse(row[0]);
                    Integer wordId = Integer.parseInt(row[1]);
                    BigDecimal positivityScore = new BigDecimal(row[2]);
                    BigDecimal negativittyScore = new BigDecimal(row[3]);


                    List<SentiWord.Word> words = Arrays.stream(row[4].split("\\s")).map(word -> {
                        String[] wordString = word.split("#");
                        return new SentiWord.Word(wordString[0], Integer.parseInt(wordString[1]));

                    }).collect(Collectors.toList());

                    String metaData = row[5];

                    String[] metadaSplit = metaData.split(";");
                    String description = metadaSplit[0];

                    List<String> exampleSentences = Arrays.stream(Arrays.copyOfRange(metadaSplit, 1, metadaSplit.length))
                            .collect(Collectors.toList());

                    return new SentiWord(posType, wordId, positivityScore, negativittyScore, words, description, exampleSentences);

                }).collect(Collectors.toList());


        sentiWordDao.saveAll(sentiWords);


    }
}
