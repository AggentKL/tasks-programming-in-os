package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException {
        int size;
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost("localhost");
        try (Connection connection = cf.newConnection();
             Channel channel = connection.createChannel()) {

            File pictures = new File("../Lab6_Client/images");

            channel.queueDeclare("thumbnails_tasks", false, false, false, null);
            for (final File fileEntry : Objects.requireNonNull(pictures.listFiles())) {
                String pic;

                pic = fileEntry.getPath();
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter new size(in pixels) for (" + fileEntry.getName() + "):");
                size = scanner.nextInt();
                pic += "," + size;

                channel.basicPublish("", "thumbnails_tasks", false, null, pic.getBytes());

                System.out.println(fileEntry.getName() + " sent");
            }

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        Connection connection = cf.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("thumbnails_results", false, false, false, null);
        DeliverCallback dc = (consumerTag, msg) -> {
            String path = new String(msg.getBody(), StandardCharsets.UTF_8);

            System.out.println("Path of resized picture is " + path);
        };
        channel.basicConsume("thumbnails_results", true, dc, consumerTag -> {
        });


    }
}
