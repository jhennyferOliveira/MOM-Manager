package controller;

import network.ConnectionHandler;

public class BrokerViewController {

    public static void createQueue(String name) {
        try {
            ConnectionHandler.createQueue(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTopic(String name) {
        try {
            ConnectionHandler.createTopic(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteQueue(String name) {
        try {
            ConnectionHandler.deleteQueue(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteTopic(String name) {
        try {
            ConnectionHandler.deleteTopic(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Long updatePendingMessages(String queueName) {
        var messageCount = 0L;
        try {
            messageCount = ConnectionHandler.getMessageCount(queueName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return messageCount;
    }

}
