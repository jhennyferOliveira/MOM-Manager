package view.extensions;

import controller.BrokerViewController;
import view.BrokerMainView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ButtonEditor extends DefaultCellEditor {
    private JButton button = new JButton();

    public ButtonEditor(JCheckBox checkBox, JTable table, Boolean isDeleteButton) {
        super(checkBox);
        if (isDeleteButton) {
            button.setText("Delete");
            button.setFont(new Font("Inter", Font.BOLD, 12));
            button.setForeground(CustomColor.white);
            button.setBackground(CustomColor.red);
            button.setOpaque(true);
        } else {
            button.setText("Update");
            button.setFont(new Font("Inter", Font.BOLD, 12));
            button.setForeground(CustomColor.black);
            button.setBackground(CustomColor.yellow);
            button.setOpaque(true);
        }
        button.addActionListener(e -> {
            int editingRow = table.getEditingRow();
            var isDeletingQueue = table.getEditingColumn() == 3 && table.getColumnName(0).equalsIgnoreCase("Queues");
            var isDeletingTopic = table.getEditingColumn() == 1 && table.getColumnName(0).equalsIgnoreCase("Topics");
            if (isDeletingTopic || isDeletingQueue ) { //delete action
                if (editingRow >= 0) {
                    deleteAction(table, editingRow);
                }
            } else {
                if (editingRow >= 0) {
                    updatePendingMessages(table);
                }
            }
        });
    }

    private void updatePendingMessages(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if (table.getColumnName(0).equalsIgnoreCase("Queues")) {
            BrokerMainView.updatePendingMessages(model.getValueAt(table.getEditingRow(), 0).toString());
        } else {
            BrokerViewController.deleteTopic(model.getValueAt(table.getEditingRow(), 0).toString());
        }
    }

    private void deleteAction(JTable table, int row) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if (table.getColumnName(0).equalsIgnoreCase("Queues")) {
            BrokerViewController.deleteQueue(model.getValueAt(table.getEditingRow(), 0).toString());
        } else {
            BrokerViewController.deleteTopic(model.getValueAt(table.getEditingRow(), 0).toString());
        }
        model.removeRow(row);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "Delete";
    }
}
