package view;

import view.extensions.ButtonEditor;
import view.extensions.ButtonRenderer;
import view.extensions.CustomColor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class BrokerMainView {

    public JFrame frame = new JFrame("Broker Management");
    JPanel secondaryPanel = new JPanel();
    DefaultTableModel clientModel = new DefaultTableModel();
    JTable clientTable;
    DefaultTableModel topicModel = new DefaultTableModel();
    JTable topicTable;
    DefaultTableModel queueModel = new DefaultTableModel();
    JTable queueTable;
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
            showCreateForm("Create Queue", "Queue name", queueModel, queueTable);
        });
        secondaryPanel.add(queueButton);

        // Create the table
        queueModel.addColumn("Queues");
        queueModel.addColumn("Messages");
        queueModel.addColumn("Actions");

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
            showCreateForm("Create Topic", "Topic name", topicModel, topicTable);
        });
        secondaryPanel.add(topicButton);


        // Create the table
        topicModel.addColumn("Topics");
        topicModel.addColumn("Messages");
        topicModel.addColumn("Actions");

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


    private void addRow(DefaultTableModel model, JTable table, Object[] rowData, Boolean isClientTable) {
        model.addRow(rowData);
        if (!isClientTable) {
            table.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
            table.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), table));
        }
    }

    public void showCreateForm(String title, String parameterName, DefaultTableModel model, JTable table) {
        var parameterNameTextField = new JTextField();
        Object[] fields = {
                parameterName, parameterNameTextField
        };
        int option = JOptionPane.showConfirmDialog(frame, fields,title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String enteredName = parameterNameTextField.getText();
            addRow(model, table, new Object[]{enteredName}, false);
        }
    }

    public void showCreateClientForm(DefaultTableModel model, JTable table) {
        var clientName = new JTextField();
        String[] options = {"Consumer", "Productor", "Subscriber", "Publisher"};
        JComboBox<String> comboBox = new JComboBox<>(options);

        Object[] fields = {
                "Client name", clientName,
                "Client type", comboBox
        };
        int option = JOptionPane.showConfirmDialog(frame, fields,"Create Client", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String enteredName = clientName.getText();
            String enteredType = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
            addRow(model, table, new Object[]{enteredName, enteredType}, true);
        }
    }
}





