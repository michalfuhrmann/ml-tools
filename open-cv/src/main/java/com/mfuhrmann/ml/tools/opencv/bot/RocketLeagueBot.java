package com.mfuhrmann.ml.tools.neuralnetworks.bot;

import com.google.common.base.Stopwatch;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RocketLeagueBot {


    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws AWTException, IOException {

        Stopwatch stopwatch = Stopwatch.createStarted();
        JLabel label = getLabel();
        while (true) {
            stopwatch.reset().start();
            label.setIcon(getImage());
            long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);

            System.out.println(elapsed);

        }

    }

    private static JLabel getLabel() throws AWTException, IOException {
        ImageIcon imageIcon = getImage();


        JFrame editorFrame = new JFrame("Image Demo");
        editorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);
        editorFrame.getContentPane().add(jLabel, BorderLayout.CENTER);


        editorFrame.pack();
        editorFrame.setLocationRelativeTo(null);
        editorFrame.setVisible(true);
        return jLabel;
    }

    @NotNull
    private static ImageIcon getImage() throws AWTException, IOException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = new Robot().createScreenCapture(screenRect);
//        File temp = File.createTempFile("temp", ".png");
//
//        System.out.println(temp.getAbsolutePath());
//
//        ImageIO.write(capture, "bmp", temp);


//        return new ImageIcon(capture.getScaledInstance(1920, 1080, Image.SCALE_DEFAULT));
        return new ImageIcon(capture);
    }
}
