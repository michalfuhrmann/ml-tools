package com.mfuhrmann.ml.tools.opencv.bot.qwop;
//
//import net.sourceforge.tess4j.ITesseract;
//import net.sourceforge.tess4j.Tesseract;
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.imgproc.Imgproc;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//
//public class QwopBot {
//
//    public static final String TESSERACT_PATH = "C:\\dev\\Tesseract-OCR\\tessdata";
//
//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//    }
//
//    private static final Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
////    private static final Rectangle screenRect = new Rectangle(800, 600);
//
//
//    public static void main(String[] args) throws Exception {
//        System.out.println("QWOP bot");
//
//        ITesseract instance = new Tesseract();  // JNA Interface Mapping
//
//        instance.setDatapath(TESSERACT_PATH);
//        instance.setLanguage("eng");
//
//
//        while (true) {
//
//            Robot robot = new Robot();
//            BufferedImage screenCapture = robot.createScreenCapture(screenRect).getSubimage(1100, 380, 150, 50);
//
//
////        Mat frame = Utils.fastBufferedImageToMat(screenCapture);
//
////        BufferedImage bufferedImage = Utils.matToBufferedImage(doCanny(frame));
//
//
////        opencv_text.OCRTesseract ocrTesseract = opencv_text.OCRTesseract.create("C:\\dev\\Tesseract-OCR\\tessdata\\", null, null, 3, 3);
////        opencv_text.OCRTesseract ocrTesseract = opencv_text.OCRTesseract.create(
////                "C:\\dev\\Tesseract-OCR\\tessdata\\", "eng",
////                "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", 11, 3);
//
//
//            String scoreString = instance.doOCR(screenCapture);
//
//
//           getLabel(screenCapture);
//
//            System.out.println(scoreString);
//
//            Thread.sleep(1000);
//            try {
//
//                double v = Double.parseDouble(scoreString.replace(",", "."));
//
//                System.out.println(v);
//            } catch (NumberFormatException e) {
//                System.out.println("couldn't read " + scoreString);
//            }
//        }
//    }
//
//    private static Mat doCanny(Mat frame) {
//        // init
//        Mat grayImage = new Mat();
////        Mat detectedEdges = new Mat();
//
//        // convert to grayscale
//        Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
//
//        // reduce noise with a 3x3 kernel
////        Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));
//
//        // canny detector, with ratio of lower:upper threshold of 3:1
////        Imgproc.Canny(grayImage, detectedEdges, 100, 300);
//
//        // using Canny's output as a mask, display the result
//        Mat dest = new Mat();
//        frame.copyTo(dest, grayImage);
//
////        return dest;
//
//        return dest;
//    }
//
//
//    private static JLabel getLabel(BufferedImage bufferedImage) throws AWTException, IOException {
//        ImageIcon imageIcon = new ImageIcon(bufferedImage);
//
//
//        JFrame editorFrame = new JFrame("Image Demo");
//        editorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//
//        JLabel jLabel = new JLabel();
//        jLabel.setIcon(imageIcon);
//        editorFrame.getContentPane().add(jLabel, BorderLayout.CENTER);
//
//
//        editorFrame.pack();
//        editorFrame.setLocationRelativeTo(null);
//        editorFrame.setVisible(true);
//        return jLabel;
//    }
//}
