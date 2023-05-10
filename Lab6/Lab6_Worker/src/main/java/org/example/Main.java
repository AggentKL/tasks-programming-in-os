package org.example;

import com.rabbitmq.client.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.println("In standby mode.");
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost("localhost");
        Connection connection = cf.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("thumbnails_tasks", false, false, false, null);

        AtomicReferenceArray<String> pic = new AtomicReferenceArray<>(new String[100]);

        DeliverCallback dc = (consumerTag, msg) -> {
            String data = new String(msg.getBody(), StandardCharsets.UTF_8);
            String[] imgInf = data.split(",");
            for (int i = 0; i < 2; i++) {
                pic.set(i, imgInf[i]);
            }

            System.out.println("Picture received: " + pic.get(0) + ". Size: " + pic.get(1) + "px");
            try {
                sendResult(pic, cf, connection, channel);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
        channel.basicConsume("thumbnails_tasks", true, dc, consumerTag -> {
        });

    }

    public static BufferedImage NewPictureSize(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputPic = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = outputPic.createGraphics();
        graphics2D.drawImage(resultingImage, 0, 0, null);
        graphics2D.dispose();
        return outputPic;
    }

    private static void sendResult(AtomicReferenceArray<String> pic, ConnectionFactory cf, Connection connection, Channel channel) throws IOException, TimeoutException {
        int picSize;

        String picPath = pic.get(0);
        String[] path = picPath.split(".jpg"); //cut .jpg in origin filename
        File inputFile = new File(picPath);
        BufferedImage inputPicture = ImageIO.read(inputFile);
        picSize = (Integer.parseInt(pic.get(1)));

        String saveFilePath = path[0] + "_" + picSize + "x" + picSize + "px" + ".png";
        BufferedImage resPicture = NewPictureSize(inputPicture, picSize, picSize);
        String formatName = picPath.substring(picPath.lastIndexOf(".") + 1);

        ImageIO.write(resPicture, formatName, new File(saveFilePath));

        connection = cf.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare("thumbnails_results", false, false, false, null);
        channel.basicPublish("", "thumbnails_results", false, null, saveFilePath.getBytes());
    }
}
