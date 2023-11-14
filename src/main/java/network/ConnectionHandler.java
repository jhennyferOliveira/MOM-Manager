package network;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;


import javax.jms.*;
import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Enumeration;


public class ConnectionHandler {

    public static MBeanServerConnection connectionMBean;
    public static Connection connection;
    public static Session session;

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
        consumer.close();
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
        } else {
            connectionMBean.invoke(activeMQ, operationName,null,null);
        }
    }


}
