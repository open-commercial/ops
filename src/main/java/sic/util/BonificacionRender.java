package sic.util;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class BonificacionRender extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        this.setHorizontalAlignment(SwingConstants.RIGHT);
        Component c = super.getTableCellRendererComponent(table, this.aplicarFormato(value), isSelected, hasFocus, row, column);
        if (value instanceof Number && ((Number) value).doubleValue() > 0) {
            c.setBackground(Color.GREEN);
        }
        return c;
    }

    private Object aplicarFormato(Object value) {
        if (value instanceof Number) {
            DecimalFormat dFormat = new DecimalFormat("##,##0.00");
            value = dFormat.format(value);
        }
        return value;
    }
}
