package com.mfuhrmann.ml.tools.neuralnetworks.signlanguagerecognizer.net;

import com.mfuhrmann.ml.tools.neuralnetworks.api.NetworkBuilderTemplate;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.LearningRatePolicy;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SignLanguageNetworkBuilder implements NetworkBuilderTemplate {

    int height = 28;
    int width = 28;
    int channels = 1; // single channel for grayscale images
    int outputNum = 10; // 10 digits classification
    int batchSize = 50;
    int iterations = 1;


    int seed = ThreadLocalRandom.current().nextInt();


    @Override
    public DataSetIterator createTrainDataSetIterator() throws IOException, InterruptedException {


        String filenameTrain = new ClassPathResource("/classification/mnist/train.csv").getFile().getPath();

        RecordReader rrTest = new CSVRecordReader(1);
        rrTest.initialize(new FileSplit(new File(filenameTrain)));
        RecordReaderDataSetIterator trainIter = new RecordReaderDataSetIterator(rrTest, batchSize, 0, 10);

        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        scaler.fit(trainIter);
        trainIter.setPreProcessor(scaler);

        return trainIter;
    }

    @Override
    public DataSetIterator createTestDataSetIterator() throws IOException, InterruptedException {
        String filenameTest = new ClassPathResource("/classification/mnist/test.csv").getFile().getPath();

        RecordReader rrTest = new CSVRecordReader(1);
        rrTest.initialize(new FileSplit(new File(filenameTest)));
        RecordReaderDataSetIterator trainIter = new RecordReaderDataSetIterator(rrTest, batchSize);

        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        scaler.fit(trainIter);
        trainIter.setPreProcessor(scaler);

        return trainIter;
    }

    @Override
    public MultiLayerConfiguration createNeuralNetConfig() {

        Map<Integer, Double> lrSchedule = new HashMap<>();
        lrSchedule.put(0, 0.04); // iteration #, learning rate
        lrSchedule.put(200, 0.035);
        lrSchedule.put(400, 0.019);
        lrSchedule.put(600, 0.0050);
        lrSchedule.put(800, 0.001);

        return new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations)
                .regularization(true).l2(0.0005)
                .learningRate(.01)
                .learningRateDecayPolicy(LearningRatePolicy.Schedule)
                .learningRateSchedule(lrSchedule) // overrides the rate set in learningRate
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.NESTEROVS)
                .list()
                .layer(0, new ConvolutionLayer.Builder(5, 5)
                        .nIn(channels)
                        .stride(1, 1)
                        .nOut(20)
                        .activation(Activation.IDENTITY)
                        .build())
                .layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2)
                        .stride(2, 2)
                        .build())
                .layer(2, new ConvolutionLayer.Builder(5, 5)
                        .stride(1, 1) // nIn need not specified in later layers
                        .nOut(50)
                        .activation(Activation.IDENTITY)
                        .build())
                .layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2)
                        .stride(2, 2)
                        .build())
                .layer(4, new DenseLayer.Builder().activation(Activation.RELU)
                        .nOut(500).build())
                .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(outputNum)
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutionalFlat(28, 28, 1)) // InputType.convolutional for normal image
                .backprop(true).pretrain(false).build();
    }

    @Override
    public List<String> getLabels() {
        return IntStream.range(0, 10).mapToObj(String::valueOf).collect(Collectors.toList());
    }

    @Override
    public List<IterationListener> getListeners() {
        return List.of(new ScoreIterationListener(10));
    }


}
