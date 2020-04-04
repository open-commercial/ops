package sic.util;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColoresCeldaRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Number) {
            DecimalFormat dFormat = new DecimalFormat("##,##0.##");
            value = dFormat.format(value);
        }
        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(Color.LIGHT_GRAY);
        return c;
    }
}
