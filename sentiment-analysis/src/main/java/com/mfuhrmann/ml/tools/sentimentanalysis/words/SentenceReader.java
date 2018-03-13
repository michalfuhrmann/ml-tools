package com.mfuhrmann.ml.tools.sentimentanalysis.words;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SentenceReader {


    private final SentimentAnalyzer sentimentAnalyzer;

    @Autowired
    public SentenceReader(SentimentAnalyzer sentimentAnalyzer) {
        this.sentimentAnalyzer = sentimentAnalyzer;
    }


    @Scheduled(fixedDelay= 10_000)
    public void readSentence() throws IOException, URISyntaxException {


        Path path = Paths.get(ClassLoader.getSystemResource("en-token.bin").toURI());


        TokenizerModel model = new TokenizerModel(path);
        Tokenizer tokenizer = new TokenizerME(model);


        System.out.println("Provide input:");

        Scanner sc = new Scanner(System.in);
        String sentence = sc.nextLine();


        if (hasNamedEntity(sentence)) {

            String[] tokenize = tokenizer.tokenize(sentence);


            System.out.println("Tokenized sentence " + Arrays.toString(tokenize));


            SentimentAnalyzer.SentimentReport sentimentReport = sentimentAnalyzer.getSentimentDataForSentence(Arrays.stream(tokenize).collect(Collectors.toList()));


            System.out.println(sentimentReport);

            System.out.println();
        }else{
            System.out.println("No named entity recognized");

        }

    }

    private boolean hasNamedEntity(String sentence) {
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
        props.setProperty("coref.algorithm", "neural");
        // build pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // create a document object

        System.out.println(
                "Provide input:"
        );
        System.out.println();


        CoreDocument document = new CoreDocument(sentence);
        // annnotate the document

        pipeline.annotate(document);


        Map<String, List<CoreEntityMention>> collect = document.entityMentions().stream().collect(Collectors.toMap(CoreEntityMention::entityType,
                e -> {
                    LinkedList<CoreEntityMention> coreEntityMentions = new LinkedList<>();
                    coreEntityMentions.add(e);
                    return coreEntityMentions;
                },
                (o1, o2) -> Stream.concat(o1.stream(), o2.stream()).collect(Collectors.toList())));


        System.out.println(Arrays.toString(collect.entrySet().toArray()));

        System.out.println("==========================================================");
        System.out.println();

        return !collect.entrySet().isEmpty();

    }
}
