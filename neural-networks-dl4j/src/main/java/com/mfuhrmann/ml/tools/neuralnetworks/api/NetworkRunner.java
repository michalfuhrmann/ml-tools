package com.mfuhrmann.ml.tools.neuralnetworks.api;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NetworkRunner implements AutoCloseable {
    private final DataSetIterator trainIterator;
    private final DataSetIterator testIterator;
    private final MultiLayerNetwork net;
    private final List<String> labels;

    public NetworkRunner(NetworkBuilderTemplate networkBuilderTemplate) throws Exception {
        this.trainIterator = networkBuilderTemplate.createTrainDataSetIterator();
        this.testIterator = networkBuilderTemplate.createTestDataSetIterator();
        this.net = new MultiLayerNetwork(networkBuilderTemplate.createNeuralNetConfig());
        this.labels = networkBuilderTemplate.getLabels();
        this.net.init();

        StatsStorage ss = new InMemoryStatsStorage();
        UIServer.getInstance().attach(ss);

        List<IterationListener> listeners = Stream.concat(Stream.of(new StatsListener(ss)), networkBuilderTemplate.getListeners().stream()).collect(Collectors.toList());
        this.net.setListeners(listeners);


    }

    public void train() {

        net.fit(trainIterator);
    }


    //predictForInput

    public void trainUntil( // Condition
    ) {

    }

    public void savePredictionToFile(List<String> predictions, String fileName) {
        Path outputPath = Paths.get(fileName);
        try (CSVPrinter csvPrinter = new CSVPrinter(Files.newBufferedWriter(outputPath), CSVFormat.DEFAULT
                .withIgnoreHeaderCase()
                .withFirstRecordAsHeader()
                .withTrim())) {

            AtomicInteger atomicInteger = new AtomicInteger(1);
            csvPrinter.printRecord("ImageId", "Label");


            predictions.forEach(pred -> {
                try {
                    csvPrinter.printRecord(atomicInteger.getAndIncrement(), pred);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Evaluation test() {
        Evaluation eval = net.evaluate(trainIterator);
        System.out.println(eval.stats());


        return eval;

    }

    public List<String> predict(List<DataSet> dataSets) {

        dataSets.forEach(dataSet -> dataSet.setLabelNames(labels));

        List<String> predictions = dataSets.stream()
                .map(net::predict).flatMap(Collection::stream)
                .collect(Collectors.toList());

        System.out.println(predictions.size());


        System.out.println("finnished");
        return predictions;
    }


    public void saveModel() {


    }
    //TODO load Model


    @Override
    public void close() throws Exception {
        UIServer.getInstance().stop();
    }
}
