package view;

import controller.BrokerViewController;
import view.extensions.CustomColor;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class ClientsView {

    private final JFrame frame = new JFrame("Client window");

    private final JPanel secondaryPanel = new JPanel();

    private String clientType = "";
    private String clientName = "";

    private final JPanel chatPanel = new JPanel();
    private final JTextPane chatPane = new JTextPane();
    public  void setUpFrame(String clientType, String clientName) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        this.clientType = clientType;
        this.clientName = clientName;
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                var clientTable = BrokerMainView.clientTable;

                for (int i = 0; i < clientTable.getRowCount(); i++) {
                    if (Objects.equals(clientTable.getValueAt(i, 0).toString(), clientName)) {
                        BrokerMainView.clientModel.removeRow(i);
                        break; // Exit loop after removing the row
                    }
                }

                BrokerViewController.deleteConsumer(clientName);

                Iterator<Map.Entry<String, ClientsView>> iterator = BrokerMainView.clientsView.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, ClientsView> entry = iterator.next();
                    if (entry.getKey().equalsIgnoreCase(clientName)) {
                        iterator.remove(); // Use iterator's remove method to safely remove the entry
                        break; // Exit loop after removing the entry
                    }
                }
            }
        });
        frame.setVisible(true);
        int frameWidth = 620;
        int frameHeight = 790;
        frame.setSize(frameWidth, frameHeight);
        frame.getContentPane().setBackground(Color.WHITE);

        setUpSecondaryFrame();
        setUpSystemName(clientType, clientName);
        setUpSubscribeButton();
        setUpSendMessageButton();
        setUpLabel();
        setUpMessagePanel();

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(Color.WHITE);
        backgroundPanel.setBounds(0, 0, 583, 790);
        frame.add(backgroundPanel);
    }

    private void setUpSecondaryFrame() {
        secondaryPanel.setLayout(null);
        secondaryPanel.setBackground(CustomColor.lightGray);
        secondaryPanel.setBounds(18, 131, 583, 583);
        frame.add(secondaryPanel);
    }

    private void setUpSystemName(String clientType, String clientName) {
        JButton systemNameButton = new JButton();
        systemNameButton.setAlignmentX(0.5f); // Center horizontally
        systemNameButton.setAlignmentY(0.5f); // Center vertically
        systemNameButton.setBounds(18, 41, 320, 44);
        systemNameButton.setFocusPainted(false);
        systemNameButton.setContentAreaFilled(false);
        systemNameButton.setText(clientType + ": " + clientName);
        systemNameButton.setForeground(Color.WHITE);
        systemNameButton.setOpaque(true);
        systemNameButton.setFont(new Font("Inter", Font.BOLD, 18));
        systemNameButton.setBackground(CustomColor.darkGray);
        frame.add(systemNameButton);
    }

    private void setUpSubscribeButton() {
        JButton subscribeButton = new JButton();
        subscribeButton.setAlignmentX(0.5f); // Center horizontally
        subscribeButton.setAlignmentY(0.5f); // Center vertically
        subscribeButton.setBounds(210, 40, 162, 45);
        subscribeButton.setFocusPainted(false);
        subscribeButton.setContentAreaFilled(false);
        subscribeButton.setText("Subscribe");
        subscribeButton.setForeground(Color.black);
        subscribeButton.setOpaque(true);
        subscribeButton.setFont(new Font("Inter", Font.BOLD, 15));
        subscribeButton.setBackground(CustomColor.green);
        subscribeButton.addActionListener(e -> showSubscribeButtonForm());
        if (!Objects.equals(clientType, "Subscriber")) {
            subscribeButton.setEnabled(false);
        }
        secondaryPanel.add(subscribeButton);
    }

    private void setUpSendMessageButton() {
        JButton sendMessageButton = new JButton();
        sendMessageButton.setAlignmentX(0.5f); // Center horizontally
        sendMessageButton.setAlignmentY(0.5f); // Center vertically
        sendMessageButton.setBounds(210, 120, 162, 45);
        sendMessageButton.setFocusPainted(false);
        sendMessageButton.setContentAreaFilled(false);
        sendMessageButton.setText("Send Message");
        sendMessageButton.setForeground(Color.black);
        sendMessageButton.setOpaque(true);
        sendMessageButton.setFont(new Font("Inter", Font.BOLD, 15));
        sendMessageButton.setBackground(CustomColor.green);
        if (Objects.equals(clientType, "Publisher")) {
            sendMessageButton.addActionListener(e -> showSendMessageToTopicForm());
        } else if (Objects.equals(clientType, "Consumer/Producer")) {
            sendMessageButton.addActionListener(e -> showSendMessageToQueueForm());
        } else {
            sendMessageButton.setEnabled(false);
        }
        secondaryPanel.add(sendMessageButton);
    }

    private void setUpLabel() {
        JLabel receivedMessagesTitle = new JLabel();
        receivedMessagesTitle.setBounds(47, 240, 176, 22);
        receivedMessagesTitle.setText("Received Messages");
        receivedMessagesTitle.setFont(new Font("Inter", Font.BOLD, 15));
        secondaryPanel.add(receivedMessagesTitle);
    }



    public void setUpMessagePanel() {
        chatPanel.setOpaque(false);
        chatPanel.setBounds(47, 300, 480, 250);
        chatPanel.setLayout(new BorderLayout());

        chatPane.setEditable(false);
        chatPane.setFocusable(false);

        chatPane.setBackground(CustomColor.white);

        JScrollPane scrollPane = new JScrollPane(chatPane);
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        secondaryPanel.add(chatPanel);
    }

    public void addMessageToPane(String sender, String clientName, String message, Color color) {
        StyledDocument doc = chatPane.getStyledDocument();

        SimpleAttributeSet senderStyle = new SimpleAttributeSet();
        StyleConstants.setBold(senderStyle, true);
        StyleConstants.setForeground(senderStyle, color);

        SimpleAttributeSet messageStyle = new SimpleAttributeSet();

        try {
            doc.insertString(doc.getLength(), "  " + sender + ": ", senderStyle);
            doc.insertString(doc.getLength(), message + "\n", messageStyle);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        chatPane.setCaretPosition(doc.getLength());
    }


    public void showSubscribeButtonForm() {
        var topicName = new JTextField();

        Object[] fields = {
                "Topic name", topicName,
        };
        int option = JOptionPane.showConfirmDialog(frame, fields,"Subscribe", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String enteredName = topicName.getText();
            try {
                BrokerViewController.createSubscriber(enteredName, clientName);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void showSendMessageToTopicForm() {
        var message = new JTextField();
        var topicName = new JTextField();

        Object[] fields = {
                "Message", message,
                "Topic name", topicName
        };
        int option = JOptionPane.showConfirmDialog(frame, fields,"Send Message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String enteredTopicName = topicName.getText();
            String enteredMessage = message.getText();
            try {
                BrokerViewController.sendMessageToTopic(enteredTopicName, enteredMessage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void showSendMessageToQueueForm() {
        var message = new JTextField();
        var queueName = new JTextField();

        Object[] fields = {
                "Message", message,
                "Queue name", queueName
        };
        int option = JOptionPane.showConfirmDialog(frame, fields,"Send Message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String enteredQueueName = queueName.getText();
            String enteredMessage = message.getText();
            try {
                BrokerViewController.sendMessageToQueue(enteredQueueName, enteredMessage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
