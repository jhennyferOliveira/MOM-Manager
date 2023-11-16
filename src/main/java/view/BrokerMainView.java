package view;

import controller.BrokerViewController;
import network.ConnectionHandler;
import view.extensions.ButtonEditor;
import view.extensions.ButtonRenderer;
import view.extensions.CustomColor;

import javax.jms.JMSException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

public class BrokerMainView {

    public enum TableType {
        Client("Client"), Topic("Topic"), Queue("Queue");

        public final String type;
        TableType(String level) {
            this.type = level;
        }
    }

    private JFrame frame = new JFrame("Broker Management");
    private JPanel secondaryPanel = new JPanel();
    public static DefaultTableModel clientModel = new DefaultTableModel();
    public static JTable clientTable;
    public static DefaultTableModel topicModel = new DefaultTableModel();
    public static JTable topicTable;
    public static DefaultTableModel queueModel = new DefaultTableModel();
    public static JTable queueTable;

    public static HashMap<String, ClientsView> clientsView = new HashMap<>();

    public void setUpFrame() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        int frameWidth = 1270;
        int frameHeight = 790;
        frame.setSize(frameWidth, frameHeight);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setUpSystemName();
        setUpSecondaryFrame();
        setQueueComponent();
        setTopicComponent();
        setClientComponent();

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(Color.WHITE);
        backgroundPanel.setBounds(0, 0, 1270, 790);
        frame.add(backgroundPanel);
    }

    private void setUpSecondaryFrame() {
        secondaryPanel.setLayout(null);
        secondaryPanel.setBackground(CustomColor.lightGray);
        secondaryPanel.setBounds(18, 131, 1230, 583);
        frame.add(secondaryPanel);
    }

    void setUpSystemName() {
        JButton systemNameButton = new JButton();
        systemNameButton.setAlignmentX(0.5f); // Center horizontally
        systemNameButton.setAlignmentY(0.5f); // Center vertically
        systemNameButton.setBounds(18, 41, 231, 44);
        systemNameButton.setFocusPainted(false);
        systemNameButton.setContentAreaFilled(false);
        systemNameButton.setText("Broker Management");
        systemNameButton.setForeground(Color.WHITE);
        systemNameButton.setOpaque(true);
        systemNameButton.setFont(new Font("Inter", Font.BOLD, 18));
        systemNameButton.setBackground(CustomColor.darkGray);
        frame.add(systemNameButton);
    }

    private void setQueueComponent() {
        JLabel queueTitle = new JLabel();
        queueTitle.setBounds(182, 35, 84, 27);
        queueTitle.setText("Queues");
        queueTitle.setFont(new Font("Inter", Font.BOLD, 18));
        secondaryPanel.add(queueTitle);

        // Create the button
        JButton queueButton = new JButton();
        queueButton.setAlignmentX(0.5f); // Center horizontally
        queueButton.setAlignmentY(0.5f); // Center vertically
        queueButton.setBounds(137, 100, 162, 45);
        queueButton.setFocusPainted(false);
        queueButton.setContentAreaFilled(false);
        queueButton.setText("Create Queue");
        queueButton.setForeground(CustomColor.black);
        queueButton.setOpaque(true);
        queueButton.setFont(new Font("Inter", Font.BOLD, 15));
        queueButton.setBackground(CustomColor.green);
        queueButton.addActionListener(e -> {
            showCreateForm("Create Queue", "Queue name", queueModel, queueTable, TableType.Queue);
        });
        secondaryPanel.add(queueButton);

        // Create the table
        queueModel.addColumn("Queues");
        queueModel.addColumn("Messages");
        queueModel.addColumn("Action 1");
        queueModel.addColumn("Action 2");

        queueTable = new JTable(queueModel);
        queueTable.setRowSelectionAllowed(false);
        queueTable.setBounds(43, 179, 354, 72);

        JScrollPane scrollPane = new JScrollPane(queueTable);
        scrollPane.setBounds(43, 179, 354, 372);
        secondaryPanel.add(scrollPane);

    }

    private void setTopicComponent() {
        JLabel topicTitle = new JLabel();
        topicTitle.setBounds(647, 35, 70, 27);
        topicTitle.setText("Topics");
        topicTitle.setFont(new Font("Inter", Font.BOLD, 18));
        secondaryPanel.add(topicTitle);

        // Create the button
        JButton topicButton = new JButton();
        topicButton.setAlignmentX(0.5f); // Center horizontally
        topicButton.setAlignmentY(0.5f); // Center vertically
        topicButton.setBounds(593, 100, 162, 45);
        topicButton.setFocusPainted(false);
        topicButton.setContentAreaFilled(false);
        topicButton.setText("Create Topic");
        topicButton.setForeground(CustomColor.black);
        topicButton.setOpaque(true);
        topicButton.setFont(new Font("Inter", Font.BOLD, 15));
        topicButton.setBackground(CustomColor.green);
        topicButton.addActionListener(e -> {
            showCreateForm("Create Topic", "Topic name", topicModel, topicTable, TableType.Topic);
        });
        secondaryPanel.add(topicButton);

        // Create the table
        topicModel.addColumn("Topics");
        topicModel.addColumn("Action");

        topicTable = new JTable(topicModel);
        topicTable.setBounds(498, 179, 354, 72);
        JScrollPane scrollPane = new JScrollPane(topicTable);
        scrollPane.setBounds(498, 179, 354, 372);
        secondaryPanel.add(scrollPane);
    }

    private void setClientComponent() {
        JLabel clientTitle = new JLabel();
        clientTitle.setBounds(1045, 35, 75, 27);
        clientTitle.setText("Clients");
        clientTitle.setFont(new Font("Inter", Font.BOLD, 18));
        secondaryPanel.add(clientTitle);

        // Create the button
        JButton clientButton = new JButton();
        clientButton.setAlignmentX(0.5f); // Center horizontally
        clientButton.setAlignmentY(0.5f); // Center vertically
        clientButton.setBounds(998, 100, 162, 45);
        clientButton.setFocusPainted(false);
        clientButton.setContentAreaFilled(false);
        clientButton.setText("Create Client");
        clientButton.setForeground(CustomColor.black);
        clientButton.setOpaque(true);
        clientButton.setFont(new Font("Inter", Font.BOLD, 15));
        clientButton.setBackground(CustomColor.green);
        clientButton.addActionListener(e -> {
            showCreateClientForm( clientModel, clientTable);
        });
        secondaryPanel.add(clientButton);

        // Configure table
        clientModel.addColumn("Clients");
        clientModel.addColumn("Type");

        clientTable = new JTable(clientModel);
        clientTable.setBounds(955, 179, 239, 72);

        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setBounds(955, 179, 239, 372);
        secondaryPanel.add(scrollPane);
    }

    public static void addRow(DefaultTableModel model, JTable table, Object[] rowData, TableType tableType) {
        model.addRow(rowData);
        switch (tableType) {
            case Queue -> {
                table.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer(false));
                table.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), table, false));
                table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer(true));
                table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), table,true));
            }
            case Topic -> {
                table.getColumnModel().getColumn(1).setCellRenderer(new ButtonRenderer(true));
                table.getColumnModel().getColumn(1).setCellEditor(new ButtonEditor(new JCheckBox(), table, true));}
        }
    }

    public void showCreateForm(String title, String parameterName, DefaultTableModel model, JTable table, TableType tableType) {
        var parameterNameTextField = new JTextField();
        Object[] fields = {
                parameterName, parameterNameTextField
        };
        int option = JOptionPane.showConfirmDialog(frame, fields,title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            var enteredName = parameterNameTextField.getText();
            if (tableType.type.equalsIgnoreCase(TableType.Queue.type)) {
                BrokerViewController.createQueue(enteredName);
            } else {
                BrokerViewController.createTopic(enteredName);
            }
            addRow(model, table, new Object[]{enteredName, 0}, tableType);
        }
    }

    public static void updatePendingMessages(String queueName) {
        var pendingCount = BrokerViewController.updatePendingMessages(queueName);
        var row = 0;
        var column = 0; // queue name column
        for (int i = queueTable.getRowCount() - 1; i >= 0; --i) {
            if (queueTable.getValueAt(i, column).equals(queueName)) {
                row = i;
                System.out.println("achou a queue");
            }
        }
        int finalRow = row;
        SwingUtilities.invokeLater(() -> {
            queueTable.setValueAt(pendingCount, finalRow, 1);
        });
    }



    public void showCreateClientForm(DefaultTableModel model, JTable table) {
        var clientName = new JTextField();
        var clientQueue = new JTextField();
        String[] options = {"Consumer/Producer", "Subscriber", "Publisher"};
        JComboBox<String> comboBox = new JComboBox<>(options);

        Object[] fields = {
                "Client name", clientName,
                "Client type", comboBox,
                "Client queue", clientQueue
        };
        int option = JOptionPane.showConfirmDialog(frame, fields,"Create Client", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String enteredName = clientName.getText();
            String enteredClientQueue = clientQueue.getText();
            String enteredType = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
            addRow(model, table, new Object[]{enteredName, enteredType}, TableType.Client);

            SwingUtilities.invokeLater(() -> {
                try {
                    var client = new ClientsView();
                    client.setUpFrame(enteredType , enteredName);
                    clientsView.put(enteredName, client);
                    if (enteredType.equalsIgnoreCase("Consumer/Producer")) {
                        ConnectionHandler.createConsumerClient(enteredClientQueue, enteredName);
                    }
                } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                         IllegalAccessException | JMSException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }
}





