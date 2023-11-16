package controller;

import network.ConnectionHandler;
import view.BrokerMainView;
import view.ClientsView;

import javax.jms.JMSException;
import java.awt.*;
import java.util.HashMap;

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
        ConnectionHandler.sendMessageToTopic(topicName, message);
    }

    public static void sendMessageToQueue(String queueName, String message) throws JMSException {
        ConnectionHandler.sendMessageToQueue(queueName, message);
    }

}
