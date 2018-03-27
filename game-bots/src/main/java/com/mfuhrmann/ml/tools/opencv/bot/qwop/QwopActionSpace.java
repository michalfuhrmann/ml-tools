package com.mfuhrmann.ml.tools.opencv.bot.qwop;

import org.deeplearning4j.rl4j.space.ActionSpace;

class QwopActionSpace implements ActionSpace<QwopAction> {

    @Override
    public QwopAction randomAction() {
        return null;
    }

    @Override
    public void setSeed(int seed) {

    }

    @Override
    public Object encode(QwopAction action) {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public QwopAction noOp() {
        return null;
    }
}