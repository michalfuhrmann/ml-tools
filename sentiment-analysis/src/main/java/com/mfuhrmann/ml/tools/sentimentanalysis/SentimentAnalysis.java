package com.mfuhrmann.ml.tools.sentimentanalysis;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.Tree;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class SentimentAnalysis {

    public static void main(String[] args) throws IOException, URISyntaxException {
        SpringApplication.run(SentimentAnalysis.class, args);
//        tokenize();

    }


    static void tokenize() throws IOException, URISyntaxException {
        Path path = Paths.get(ClassLoader.getSystemResource("en-token.bin").toURI());


        TokenizerModel model = new TokenizerModel(path);
        Tokenizer tokenizer = new TokenizerME(model);

//        String s = "American journalist John Seigenthaler (1927–2014), subject of the Seigenthaler incident." +
//                "Wikimania, 60 Minutes, CBS, 20 minutes, April 5, 2015, co-founder Jimmy Wales at Fosdem." +
//                "Video of Wikimania 2005 – an annual conference for users of Wikipedia and other projects operated by the Wikimedia Foundation, was held in Frankfurt am Main, Germany from August 4 to 8." +
//                "" +
//
        String s = "Incredible LED matrix table and tutorial";

        String tokens[] = tokenizer.tokenize(s + "\n");


        System.out.println(Arrays.toString(tokens));


        NameFinderME nameFinder = new NameFinderME(new TokenNameFinderModel(Paths.get(ClassLoader.getSystemResource("en-ner-person.bin").toURI())));

        Span[] spans = nameFinder.find(tokens);


//        System.out.println(Arrays.toString(spans));
//        String[] strings = Arrays.copyOfRange(tokens, spans[0].getStart(), spans[0].getEnd());
//        System.out.println(Arrays.toString(strings));


        stanfordNLP( "qwe");


    }

    private static void stanfordNLP(String text) throws IOException {

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

        Scanner sc = new Scanner(System.in);
        while (true) {


            CoreDocument document = new CoreDocument(sc.nextLine());
            // annnotate the document

            pipeline.annotate(document);


//        System.out.println(Arrays.toString(document.entityMentions().get(0)..toArray()));


            System.out.println();
            List<CoreEntityMention> person = document.entityMentions().stream().filter(coreEntityMention -> coreEntityMention.entityType().equals("PERSON")).collect(Collectors.toList());

//        Map<String, List<CoreEntityMention>> collect = document.entityMentions().stream().collect(Collectors.toMap(e->e,
//                e -> {
//                    Function.identity();
//                }
//        ), (l1, l2) -> Stream.concat(Stream.of(l1), Stream.of(l2)));


            Map<String, List<CoreEntityMention>> collect = document.entityMentions().stream().collect(Collectors.toMap(CoreEntityMention::entityType,
                    e -> {
                        LinkedList<CoreEntityMention> coreEntityMentions = new LinkedList<>();

                        coreEntityMentions.add(e);
                        return coreEntityMentions;
                    },
                    (o1, o2) -> Stream.concat(o1.stream(), o2.stream()).collect(Collectors.toList())));


            System.out.println(Arrays.toString(collect.entrySet().toArray()));

            System.out.println();
            System.out.println();


            // examples


            // text of the first sentence
            String sentenceText = document.sentences().get(0).text();
            System.out.println("Example: sentence");
            System.out.println(sentenceText);
            System.out.println();

            // second sentence
            CoreSentence sentence = document.sentences().get(0);

            // list of the part-of-speech tags for the second sentence
            List<String> posTags = sentence.posTags();
            System.out.println("Example: pos tags");
            System.out.println(posTags);
            System.out.println();

            // list of the ner tags for the second sentence
            List<String> nerTags = sentence.nerTags();

            System.out.println("Example: ner tags");
            System.out.println(nerTags);
            System.out.println();

            // constituency parse for the second sentence
            Tree constituencyParse = sentence.constituencyParse();
            System.out.println("Example: constituency parse");
            System.out.println(constituencyParse);
            System.out.println();

            // dependency parse for the second sentence
            SemanticGraph dependencyParse = sentence.dependencyParse();
            System.out.println("Example: dependency parse");
            System.out.println(dependencyParse);
            System.out.println();
        }
    }

}