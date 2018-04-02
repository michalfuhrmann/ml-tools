package com.mfuhrmann.ml.tools.neuralnetworks.api;

import org.agrona.concurrent.status.AtomicCounter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CsvFileTransformer {
    private final List<String> mapColumns = List.of();
    private List<String> removeColumns = List.of();

    public CsvFileTransformer(List<String> removeColumns) {
        this.removeColumns = removeColumns;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {




        URI inputFile = CsvFileTransformer.class.getResource("/classification/titanic/train.csv").toURI();
        URI outputFile = new URI(CsvFileTransformer.class.getResource("/classification/titanic/").toURI() + "train-out.csv");



        Path inputPath = Paths.get(inputFile);
        Path outputPath = Paths.get("test-out.csv");

        new CsvTransformerBuilder()
                .setRemoveColumns("Name", "Ticket","PassengerId","Fare")

                .build().transformCSV(inputPath, outputPath);


    }

    private static String getFilePath(String name) {
        return new File("resources/classification/titanic/" + name).getAbsolutePath();
    }


    public void transformCSV(Path path, Path outputPath) throws IOException {


        try (CSVParser csvParser = new CSVParser(Files.newBufferedReader(path), CSVFormat.DEFAULT
                .withIgnoreHeaderCase()
                .withFirstRecordAsHeader()
                .withTrim())
        ) {

            System.out.println(csvParser.getHeaderMap());
            List<CSVRecord> records = csvParser.getRecords();

            Map<String, List<Integer>> mappedCsv = csvParser.getHeaderMap().keySet().stream()
                    .filter(name -> !removeColumns.contains(name))
                    .map(name -> new Tuple2<>(name, transformColumnValues(records, name)))
                    .sorted((o1, o2) -> o1._1.compareTo(o2._1))
                    .collect(Collectors.toMap(t -> t._1, t -> t._2));

            try (CSVPrinter csvPrinter = new CSVPrinter(Files.newBufferedWriter(outputPath), CSVFormat.DEFAULT
                    .withIgnoreHeaderCase()
                    .withFirstRecordAsHeader()
                    .withTrim())
            ) {


                Map<String, Integer> headerMap = csvParser.getHeaderMap();
                List<List<Integer>> collect = mappedCsv.entrySet().stream()
                        .sorted(Comparator.comparing(o -> headerMap.get(o.getKey())))
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());

                List<List<Integer>> transposedElements = transpose(collect);

                List<String> headers = headerMap.entrySet().stream()
                        .filter(e -> !removeColumns.contains(e.getKey()))
                        .sorted(Comparator.comparing(Map.Entry::getValue))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                csvPrinter.printRecord(headers);

                csvPrinter.printRecords(transposedElements);

                csvPrinter.flush();
            }

        }


    }

    public static <T> List<List<T>> transpose(List<List<T>> list) {
        final int N = list.stream().mapToInt(List::size).max().orElse(-1);
        List<Iterator<T>> iterList = list.stream().map(List::iterator).collect(Collectors.toList());
        return IntStream.range(0, N)
                .mapToObj(n -> iterList.stream()
                        .filter(Iterator::hasNext)
                        .map(Iterator::next)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private List<Integer> transformColumnValues(List<CSVRecord> records, String colummName) {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        Map<String, Integer> mappingMap = records.stream()
                .map(record -> record.get(colummName))
                .distinct()
                .collect(Collectors.toMap(Function.identity(), x -> atomicInteger.getAndIncrement()));

        mappingMap.put("", -1);

        System.out.println(mappingMap);

        return records.stream()
                .map(record -> mappingMap.get(record.get(colummName)))
                .collect(Collectors.toList());
    }


    static class CsvTransformerBuilder {


        private List<String> mapColumns = List.of();
        private List<String> removeColumns = List.of();

        CsvTransformerBuilder setMapColumns(String... mapColumns) {
            this.mapColumns = Arrays.asList(mapColumns);
            return this;
        }

        CsvTransformerBuilder setRemoveColumns(String... removeColumns) {
            this.removeColumns = Arrays.asList(removeColumns);
            return this;
        }


        public CsvFileTransformer build() {
            return new CsvFileTransformer(removeColumns);
        }


        //remove column
        //propagate column
        //transform column
        //string to number
        //with grouping
        //replace nulls with number of choice


    }

}
