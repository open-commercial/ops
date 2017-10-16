package sic.util;

import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

public class RenderTabla extends JFormattedTextField implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        this.setBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : null);
        this.setBackground(isSelected ? UIManager.getColor("Table.selectionBackground") : UIManager.getColor("Table.background"));
        this.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("##,##0.00"))));
        this.setHorizontalAlignment(SwingConstants.RIGHT);
        Double valor = null;
        if (value instanceof Double) {
            valor = (Double) value;
        }
        this.setValue(valor);
        return this;
    }
}
