package com.mfuhrmann.ml.tools.neuralnetworks.kaggle.titanic;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.arbiter.optimize.api.data.DataProvider;
import org.deeplearning4j.arbiter.ui.listener.ArbiterStatusListener;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
//import org.deeplearning4j.examples.utilities.MnistDownloader;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.shade.jackson.annotation.JsonProperty;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * This is a basic hyperparameter optimization example using Arbiter to conduct random search on two network hyperparameters.
 * The two hyperparameters are learning rate and layer size, and the search is conducted for a simple multi-layer perceptron
 * on MNIST data.
 * <p>
 * Note that this example is set up to use Arbiter's UI: http://localhost:9000/arbiter
 *
 * @author Alex Black
 */
public class TitanicKaggleExcercise {

    public static void main(String[] args) throws Exception {


        StatsStorage ss = new InMemoryStatsStorage();
        UIServer.getInstance().attach(ss);


        creatNet(ss);


        UIServer.getInstance().stop();
    }

    private static void creatNet(StatsStorage ss) throws IOException, InterruptedException {
        Nd4j.ENFORCE_NUMERICAL_STABILITY = true;
        int batchSize = 891;
        int seed = 123;
        double learningRate = 0.01;
        double l2 = 0.01;
        //Number of epochs (full passes of the data)
        int nEpochs = 100_000;
        //First: Set up the hyperparameter configuration space. This is like a MultiLayerConfiguration, but can have either
        // fixed values or values to optimize, for each hyperparameter
        int numInputs = 7;
        int numOutputs = 2;
        int numHiddenNodes = 30;

        final String filenameTrain = new ClassPathResource("/classification/titanic/train-out.csv").getFile().getPath();
        final String filenameTest = new ClassPathResource("/classification/titanic/train-out.csv").getFile().getPath();

        //Load the training data:
        RecordReader rr = new CSVRecordReader(1);
        rr.initialize(new FileSplit(new File(filenameTrain)));
        DataSetIterator trainIter = createDataSetiterator(batchSize, filenameTrain);

        DataNormalization dataNormalization = new NormalizerStandardize();
        dataNormalization.fit(trainIter);
        trainIter.setPreProcessor(dataNormalization);

        //log.info("Build model....");
        MultiLayerConfiguration conf = buildNet(seed, learningRate, numInputs, numOutputs, numHiddenNodes);


        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(1000), new StatsListener(ss));    //Print score every 10 parameter updates

        DataSetIterator testIter = createDataSetiterator(batchSize, filenameTest);
        testIter.setPreProcessor(dataNormalization);
        DataSet next = testIter.next();

        for (int i = 0; i < 10; i++) {
            model.fit(trainIter);
        }

        long counter = 0;

        double score = 1.0;

        while (score > 0.001) {

            model.fit(trainIter);
            score = model.score(next);

            if (counter++ % 1000 == 0) {
                ModelSerializer.writeModel(model, new File("titanicNet.ml"), true);
                System.out.println("Score is " + score);
                Evaluation eval = evaluteModel(numOutputs, createDataSetiterator(batchSize, filenameTest), model);
                System.out.println(eval.stats());

            }

        }

        Evaluation eval = evaluteModel(numOutputs, createDataSetiterator(batchSize, filenameTest), model);


        System.out.println(eval.stats());


    }

    private static MultiLayerConfiguration buildNet(int seed, double learningRate, int numInputs, int numOutputs, int numHiddenNodes) {
        return new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(1)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(learningRate)
                .l2(0.1)
                .regularization(true)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
                .updater(Updater.NESTEROVS)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(2, new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(3, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                .pretrain(false).backprop(true).build();
    }

    @NotNull
    private static DataSetIterator createDataSetiterator(int batchSize, String filenameTest) throws IOException, InterruptedException {
        RecordReader rrTest = new CSVRecordReader(1);
        rrTest.initialize(new FileSplit(new File(filenameTest)));
        return new RecordReaderDataSetIterator(rrTest, batchSize, 0, 2);
    }

    @NotNull
    private static Evaluation evaluteModel(int numOutputs, DataSetIterator testIter, MultiLayerNetwork model) {
        System.out.println("Evaluate model....");
        Evaluation eval = new Evaluation(numOutputs);
        while (testIter.hasNext()) {
            DataSet t = testIter.next();
            INDArray features = t.getFeatureMatrix();
            INDArray lables = t.getLabels();
            INDArray predicted = model.output(features, false);
            eval.eval(lables, predicted);


        }
        return eval;
    }


    public static class ExampleDataProvider implements DataProvider {
        private int numEpochs;
        private int batchSize;

        public ExampleDataProvider(@JsonProperty("numEpochs") int numEpochs, @JsonProperty("batchSize") int batchSize) {
            this.numEpochs = numEpochs;
            this.batchSize = batchSize;
        }

        private ExampleDataProvider() {

        }


        @Override
        public Object trainData(Map<String, Object> dataParameters) {
            try {
                return new MultipleEpochsIterator(numEpochs, new MnistDataSetIterator(batchSize, true, 12345));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object testData(Map<String, Object> dataParameters) {
            try {
                return new MnistDataSetIterator(batchSize, false, 12345);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Class<?> getDataType() {
            return DataSetIterator.class;
        }
    }
}
