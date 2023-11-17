package controller;

import network.MessageHandler;
import view.BrokerMainView;
import view.ClientsView;

import javax.jms.JMSException;
import java.awt.*;
import java.util.HashMap;

public class BrokerViewController {

    public static void createQueue(String name) {
        try {
            MessageHandler.createQueue(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTopic(String name) {
        try {
            MessageHandler.createTopic(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteQueue(String name) {
        try {
            MessageHandler.deleteQueue(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteTopic(String name) {
        try {
            MessageHandler.deleteTopic(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteConsumer(String name) {
        try {
            MessageHandler.deleteConsumer(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createConsumer(String clientQueue, String clientName) throws JMSException {
        MessageHandler.createConsumerClient(clientQueue, clientName);
    }

    public static void createSubscriber(String topicName, String clientName) throws Exception {
        MessageHandler.createSubscriber(topicName, clientName);
    }

    public static Long updatePendingMessages(String queueName) {
        var messageCount = 0L;
        try {
            messageCount = MessageHandler.getMessageCount(queueName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return messageCount;
    }

    public static void showMessageInThePanel(String sender, String clientName, String message, Color color) {
        for (HashMap.Entry<String, ClientsView> entry : BrokerMainView.clientsView.entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase(clientName)) {
                var clientView = entry.getValue();
                clientView.addMessageToPane(sender, clientName, message, color);
            }
        }
    }

    public static void addQueueToTable(String queueName) {
        BrokerMainView.addRow(BrokerMainView.queueModel, BrokerMainView.queueTable, new Object[]{queueName, 0}, BrokerMainView.TableType.Queue);
    }

    public static void addTopicToTable(String topicName) {
        BrokerMainView.addRow(BrokerMainView.topicModel, BrokerMainView.topicTable, new Object[]{topicName}, BrokerMainView.TableType.Topic);
    }

    public static void sendMessageToTopic(String topicName, String message) throws JMSException {
        MessageHandler.sendMessageToTopic(topicName, message);
    }

    public static void sendMessageToQueue(String queueName, String message) throws JMSException {
        MessageHandler.sendMessageToQueue(queueName, message);
    }
}
