package view.extensions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ButtonEditor extends DefaultCellEditor {
    private JButton button;

    public ButtonEditor(JCheckBox checkBox, JTable table) {
        super(checkBox);
        button = new JButton("Delete");
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setForeground(CustomColor.white);
        button.setBackground(CustomColor.red);
        button.setOpaque(true);
        button.addActionListener(e -> {
            int editingRow = table.getEditingRow();
            if (editingRow >= 0) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.removeRow(editingRow);
            }
        });
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
