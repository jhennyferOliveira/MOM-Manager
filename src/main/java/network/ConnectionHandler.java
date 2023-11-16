package network;

import controller.BrokerViewController;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import view.extensions.CustomColor;


import javax.jms.*;
import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ConnectionHandler {

    public static MBeanServerConnection connectionMBean;
    public static Connection connection;
    public static Session session;

    public static HashMap<String, Destination> topics = new HashMap<>();

    public static HashMap<String, MessageConsumer> subscribers = new HashMap<>();

    public static HashMap<String, MessageProducer> producers = new HashMap<>();
    public static HashMap<String, MessageConsumer> consumers = new HashMap<>();

    public static HashMap<String, MessageProducer> publishers = new HashMap<>();
    public static HashMap<String, Destination> queues = new HashMap<>();

    public static void connectToActiveMQ() throws Exception {

        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url);
        connectionMBean = jmxc.getMBeanServerConnection();

        var urlDefaultBroker = ActiveMQConnection.DEFAULT_BROKER_URL;
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(urlDefaultBroker);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public static void createQueue(String name) throws JMSException {
        Destination destination = session.createQueue(name);
        // Create a consumer to see the queue in the ActiveMQ console
        MessageConsumer consumer = session.createConsumer(destination);
        queues.put(name, destination);
        consumer.close();
    }

    public static long getMessageCount(String queueName) throws JMSException {
        long messageCount = 0;
        QueueBrowser browser = null;
        Enumeration<?> enumeration;

        try {
            Queue queue = session.createQueue(queueName);
            browser = session.createBrowser(queue);
            enumeration = browser.getEnumeration();

            while (enumeration.hasMoreElements()) {
                messageCount++;
                enumeration.nextElement();
            }
        } finally {
            if (browser != null) {
                try {
                    browser.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }

        return messageCount;
    }


    public static void createTopic(String name) throws Exception {
        Destination destination = session.createTopic(name);
        // Create a consumer to see the queue in the ActiveMQ console
        MessageConsumer consumer = session.createConsumer(destination);
        topics.put(name, destination);
        consumer.close();
    }


    private static ExecutorService createExecutorService() {
        return Executors.newCachedThreadPool();
    }
    public static void createConsumerClient(String queueName, String clientName) throws JMSException {
        var queueExists = false;
        Destination destination = null;
        for (HashMap.Entry<String, Destination> entry : queues.entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase(queueName)) {
                queueExists = true;
                destination = entry.getValue();
            }
        }

        MessageConsumer consumer;
        if (!queueExists) {
            Destination newDestination = session.createQueue(queueName);
            consumer = session.createConsumer(newDestination);
            queues.put(queueName, newDestination);
            BrokerViewController.addQueueToTable(queueName);
            consumers.put(clientName, consumer);
        } else {
            consumer = session.createConsumer(destination);
        }

        consumer.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage textMessage) {
                    String text = textMessage.getText();
                    System.out.println("client name, this client consumes the message sent to this queue");
                    System.out.println(clientName);

                    System.out.println("consumer responsible for consuming message");
                    System.out.println(consumer);
                    BrokerViewController.showMessageInThePanel(queueName, clientName, text, CustomColor.red);

                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });

        var executorService = createExecutorService();

        executorService.execute(() -> {
            // Keep the program running to receive messages
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void deleteQueue(String name) throws Exception {
        String operationName="removeQueue"; //operation like addQueue or removeQueue
        ObjectName activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
        if(name != null) {
            Object[] params = {name};
            String[] sig = {"java.lang.String"};
            connectionMBean.invoke(activeMQ, operationName, params, sig);
        } else {
            connectionMBean.invoke(activeMQ, operationName,null,null);
        }
    }

    public static void deleteTopic(String name) throws Exception {
        String operationName="removeTopic"; //operation like addQueue or removeQueue
        ObjectName activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
        if(name != null) {
            Object[] params = {name};
            String[] sig = {"java.lang.String"};
            connectionMBean.invoke(activeMQ, operationName, params, sig);
        }
    }

    public static void createSubscriber(String topicToSubscribe, String clientName) throws Exception {

        var topicExists = false;
        Destination destination = null;
        for (HashMap.Entry<String, Destination> entry : topics.entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase(topicToSubscribe)) {
                topicExists = true;
                destination = entry.getValue();
            }
        }

        MessageConsumer subscriber;
        if (!topicExists) {
            Destination newDestination = session.createQueue(topicToSubscribe);
            subscriber = session.createConsumer(newDestination);
            topics.put(topicToSubscribe, newDestination);
            BrokerViewController.addTopicToTable(topicToSubscribe);
            consumers.put(clientName, subscriber);
        } else {
            subscriber = session.createConsumer(destination);
        }

        subscribers.put(topicToSubscribe, subscriber);
        subscriber.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage textMessage) {
                    String text = textMessage.getText();
                    System.out.println("de dentro do subscriber");
                    BrokerViewController.showMessageInThePanel(topicToSubscribe, clientName, text, CustomColor.red);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });

        var executorService = createExecutorService();

        // Execute the subscriber in a separate thread
        executorService.execute(() -> {
            // Keep the program running to receive messages
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public static void sendMessageToTopic(String topicName, String message) throws JMSException {
        for (HashMap.Entry<String, Destination> entry : topics.entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase(topicName)) {
                MessageProducer producer = session.createProducer(entry.getValue());
                publishers.put(UUID.randomUUID().toString(), producer);
                var messageToSend = session.createTextMessage(message);
                producer.send(messageToSend);
                System.out.println("Message sent to topic: " + topicName);
            }
        }
    }

    public static void sendMessageToQueue(String queue, String message) throws JMSException {
        for (HashMap.Entry<String, Destination> entry : queues.entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase(queue)) {
                MessageProducer producer = session.createProducer(entry.getValue());
                producers.put(UUID.randomUUID().toString(), producer);
                var messageToSend = session.createTextMessage(message);
                producer.send(messageToSend);
                System.out.println("Message sent to queue: " + queue);
            }
        }
    }
}
