package com.mfuhrmann.ml.tools.neuralnetworks.kaggle.mnist;

import com.mfuhrmann.ml.tools.neuralnetworks.api.NetworkRunner;
import org.deeplearning4j.eval.Evaluation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.LinkedList;
import java.util.List;

public class MnistNumberClassifier {


    public static void main(String[] args) throws Exception {


        MnistNetworkBuilder networkBuilderTemplate = new MnistNetworkBuilder();
        try (NetworkRunner networkRunner = new NetworkRunner(networkBuilderTemplate)) {


            networkRunner.train();
            Evaluation test = networkRunner.test();


            if (test.accuracy() > 0.99) {

                DataSetIterator testDataSetIterator = networkBuilderTemplate.createTestDataSetIterator();

                List<DataSet> records = new LinkedList<>();
                testDataSetIterator.forEachRemaining(records::add);

                List<String> predictions = networkRunner.predict(records);
                networkRunner.savePredictionToFile(predictions, "predictions.csv");

            }


        }


    }


}
