package org.yanzhe.robomaster.recodaemon.utils;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

public class MnistReader implements Iterator<Object[]>, Iterable<Object[]> {
    private String labelFile;
    private String imageFile;

    private FileInputStream labelIO;
    private FileInputStream imageIO;

    private int labelSize;
    private int imageSize;
    private int imageX;
    private int imageY;

    public int readInt(FileInputStream is) throws Exception {
        byte[] int32Full = new byte[4];
        is.read(int32Full);
        ByteBuffer wrapped = ByteBuffer.wrap(int32Full);
        return wrapped.getInt();
    }

    @Override
    public Iterator iterator() {
        return this;
    }

    public int size() {
        return imageSize;
    }

    public MnistReader(String labelFile, String imageFile) {
        try {
            this.labelFile = labelFile;
            this.imageFile = imageFile;
            labelIO = new FileInputStream(labelFile);
            imageIO = new FileInputStream(imageFile);
            if (readInt(labelIO) != 2049) throw new Exception("Label file header missing");
            if (readInt(imageIO) != 2051) throw new Exception("Image file header missing");

            labelSize = readInt(labelIO);
            imageSize = readInt(imageIO);

            if (labelSize != imageSize) throw new Exception("Labels and images don't match in number.");

            imageY = readInt(imageIO);
            imageX = readInt(imageIO);

            System.out.println(
                    "LSZ " + labelSize + " ISZ " + imageSize + " Y " + imageY + " X " + imageX);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        try {
            labelIO.close();
            imageIO.close();

            labelIO = new FileInputStream(labelFile);
            imageIO = new FileInputStream(imageFile);

            readInt(labelIO);
            readInt(labelIO);
            readInt(imageIO);
            readInt(imageIO);
            readInt(imageIO);
            readInt(imageIO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int readNextLabel() {
        try {
            return labelIO.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean hasNext() {
        try {
            return imageIO.available() != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Object[] next() {
        try {
            return new Object[]{readNextImage(), readNextLabel()};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] readNextImage() throws Exception {
        byte[] imageArray = new byte[imageX * imageY];
        Arrays.fill(imageArray, (byte) 0);
        imageIO.read(imageArray, 0, imageX * imageY);
        return imageArray;
    }

    public static void main(String[] argv) {
        MnistReader mr =
                new MnistReader("mnist/t10k-labels.idx1-ubyte", "mnist/t10k-images.idx3-ubyte");

        for (int i = 0; i < 200; i++) {
            System.out.print(mr.readNextLabel());
        }
        System.out.print(mr.readNextLabel());

        for (int i = 0; i < 200; i++) {
            try {
                mr.readNextImage();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Crash at " + i);
            }
        }

        try {
            byte[] b = mr.readNextImage();
            for (int j = 0; j < b.length; j++) {
                if (j % 28 == 0) System.out.println();
                System.out.print((b[j] & 0xFF) + " ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
