package network;

import controller.BrokerViewController;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import view.extensions.CustomColor;

import javax.jms.*;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageHandler {

    public static MBeanServerConnection connectionMBean = ConnectionHandler.connectionMBean;
    public static Session session = ConnectionHandler.session;

    public static HashMap<String, Destination> topics = new HashMap<>();

    public static HashMap<String, MessageConsumer> subscribers = new HashMap<>();

    public static HashMap<String, MessageProducer> producers = new HashMap<>();
    public static HashMap<String, MessageConsumer> consumers = new HashMap<>();

    public static HashMap<String, MessageProducer> publishers = new HashMap<>();
    public static HashMap<String, Destination> queues = new HashMap<>();

    public static void createQueue(String name) throws JMSException {
        Destination destination = session.createQueue(name);
        // Create a consumer to see the queue in the ActiveMQ console
        MessageConsumer consumer = session.createConsumer(destination);
        queues.put(name, destination);
        consumer.close();
    }

    public static long getMessageCount(String queueName) throws MalformedObjectNameException {
        ObjectName queueViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=" + queueName);
        QueueViewMBean queueViewMBean = JMX.newMBeanProxy(connectionMBean, queueViewMBeanName, QueueViewMBean.class);
        return queueViewMBean.getQueueSize();
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
        var queueExists = queues.containsKey(queueName);
        Destination destination = null;
        if (queueExists) {
            destination = queues.get(queueName);
        }
        MessageConsumer consumer;

        if (!queueExists) {
            Destination newDestination = session.createQueue(queueName);
            consumer = session.createConsumer(newDestination);
            queues.put(queueName, newDestination);
            BrokerViewController.addQueueToTable(queueName);
            consumers.put(clientName, consumer);
        } else {
            if (!consumers.containsKey(clientName)) { // if consumer doesnt exist, we create a new consumer for the existent queue
                consumer = session.createConsumer(destination);
                consumers.put(clientName, consumer);
            } else { // if the consumer exists we just get the consumer
                consumer = consumers.get(clientName);
            }
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
        runExecutorService(createExecutorService());
    }

    private static void runExecutorService(ExecutorService executorService) {
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

    public static void deleteConsumer(String name) throws Exception {
        for (HashMap.Entry<String, MessageConsumer> entry : consumers.entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase(name)) {
                entry.getValue().close();
                System.out.println("removeu consumer: " + key);
                consumers.remove(key, entry.getValue());
            }
        }
    }

    public static void createSubscriber(String topicToSubscribe, String clientName) throws Exception {

        var topicExists = topics.containsKey(topicToSubscribe);
        Destination destination = null;
        if (topicExists) {
            destination = topics.get(topicToSubscribe);
        }

        MessageConsumer subscriber;

        if (!topicExists) {
            Destination newDestination = session.createQueue(topicToSubscribe);
            subscriber = session.createConsumer(newDestination);
            topics.put(topicToSubscribe, newDestination);
            BrokerViewController.addTopicToTable(topicToSubscribe);
            subscribers.put(clientName, subscriber);
        } else {
            if (!subscribers.containsKey(clientName)) { // if consumer doesnt exist, we create a new consumer for the existent queue
                subscriber = session.createConsumer(destination);
                subscribers.put(clientName, subscriber);
            } else { // if the consumer exists we just get the consumer
                subscriber = subscribers.get(clientName);
            }
        }

        subscriber.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage textMessage) {
                    String text = textMessage.getText();
                    BrokerViewController.showMessageInThePanel(topicToSubscribe, clientName, text, CustomColor.red);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
        runExecutorService(createExecutorService());
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
        var queueExists = queues.containsKey(queue);
        Destination destination = null;
        if (queueExists) {
            destination = queues.get(queue);
        }

        MessageProducer producer;

        if (!queueExists) {
            Destination newDestination = session.createQueue(queue);
            producer = session.createProducer(newDestination);
            queues.put(queue, newDestination);
            BrokerViewController.addQueueToTable(queue);
            producers.put(UUID.randomUUID().toString(), producer);
        } else {
            producer = session.createProducer(destination);
        }

        var messageToSend = session.createTextMessage(message);
        producer.send(messageToSend);
        System.out.println("Message sent to queue: " + queue);
    }
}
