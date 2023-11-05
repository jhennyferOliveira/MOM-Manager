package view.extensions;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ButtonRenderer extends DefaultTableCellRenderer {
    private JButton button = new JButton("Delete");

    public ButtonRenderer() {
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setForeground(CustomColor.white);
        button.setBackground(CustomColor.red);
        button.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return button;
    }
}
