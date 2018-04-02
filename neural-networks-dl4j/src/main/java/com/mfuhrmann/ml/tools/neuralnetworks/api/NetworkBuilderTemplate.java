package com.mfuhrmann.ml.tools.neuralnetworks.api;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.IOException;
import java.util.List;

public interface NetworkBuilderTemplate {


    DataSetIterator createTrainDataSetIterator() throws Exception;

    DataSetIterator createTestDataSetIterator()throws Exception;

    MultiLayerConfiguration createNeuralNetConfig()throws Exception;

    List<String> getLabels();

    List<IterationListener> getListeners();


}
