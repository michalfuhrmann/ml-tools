package com.mfuhrmann.ml.tools.opencv.bot;

import java.io.IOException;

//
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfByte;
//import org.opencv.imgcodecs.Imgcodecs;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.awt.image.DataBufferByte;
//import java.awt.image.DataBufferInt;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//
public final class Utils {

    public static void main(String[] args) throws IOException {



    }
}
//
//
//    public static Mat bufferedImage2Mat(BufferedImage image) throws IOException {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ImageIO.write(image, "jpg", byteArrayOutputStream);
//        byteArrayOutputStream.flush();
//        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
//    }
//
//    public static Mat bufferedImage2Mat2(BufferedImage image) {
//
//        int rows = image.getWidth();
//        int cols = image.getHeight();
//        int type = CvType.CV_32S;
//        Mat newMat = new Mat(rows, cols, type);
//
//        for (int r = 0; r < rows; r++) {
//            for (int c = 0; c < cols; c++) {
//                newMat.put(r, c, image.getRGB(r, c));
//            }
//        }
//
//
//        return newMat;
//    }
//
//    public static Mat bufferedImageToMat(BufferedImage bi) {
//        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
//        int[] data = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
//        mat.put(0, 0, data);
//        return mat;
//    }
//
//    //most efficient so far
//    public static Mat fastBufferedImageToMat(BufferedImage in) {
//        Mat out;
//        byte[] data;
//        int r, g, b;
//
//        if (in.getType() == BufferedImage.TYPE_INT_RGB) {
//            out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
//            data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
//            int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
//            for (int i = 0; i < dataBuff.length; i++) {
//                data[i * 3] = (byte) ((dataBuff[i] >> 0) & 0xFF);
//                data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
//                data[i * 3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
//            }
//        } else {
//            out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC1);
//            data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
//            int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
//            for (int i = 0; i < dataBuff.length; i++) {
//                r = (byte) ((dataBuff[i] >> 0) & 0xFF);
//                g = (byte) ((dataBuff[i] >> 8) & 0xFF);
//                b = (byte) ((dataBuff[i] >> 16) & 0xFF);
//                data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b));
//            }
//        }
//        out.put(0, 0, data);
//        return out;
//    }
//
//    public static BufferedImage matToBufferedImage(Mat original) {
//        // init
//        BufferedImage image;
//        int width = original.width(), height = original.height(), channels = original.channels();
//        byte[] sourcePixels = new byte[width * height * channels];
//        original.get(0, 0, sourcePixels);
//
//        if (original.channels() > 1) {
//            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
//        } else {
//            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//        }
//        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
//
//        return image;
//    }
//}