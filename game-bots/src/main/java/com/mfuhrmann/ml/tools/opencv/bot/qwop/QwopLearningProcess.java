package com.mfuhrmann.ml.tools.opencv.bot.qwop;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ActionSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

public class QwopLearningProcess implements MDP<QwopObservation, QwopAction, QwopActionSpace> {


    @Override
    public ObservationSpace<QwopObservation> getObservationSpace() {
        return null;
    }

    @Override
    public QwopActionSpace getActionSpace() {
        return null;
    }

    @Override
    public QwopObservation reset() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<QwopObservation> step(QwopAction action) {
        return null;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public MDP<QwopObservation, QwopAction, QwopActionSpace> newInstance() {
        return null;
    }
}

