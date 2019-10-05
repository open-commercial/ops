package sic.util;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColoresFilasRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof BigDecimal && ((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0) {
//            table.set
//            c.setForeground(Color.GREEN);
            DecimalFormat dFormat = new DecimalFormat("##,##0.00"); 
            value = dFormat.format(value);
        }
        return c;
    }
}
