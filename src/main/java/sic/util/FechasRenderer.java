package sic.util;

import java.awt.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class FechasRenderer extends DefaultTableCellRenderer {
    
    private final String formato;
    
    public FechasRenderer(String formato) {
        this.formato = formato;
    }

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel cell = (JLabel) super.getTableCellRendererComponent(tabla, valor, isSelected, hasFocus, row, column);
        if (valor instanceof LocalDateTime) {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern(this.formato);
            LocalDateTime fecha = (LocalDateTime) valor;
            cell.setText(fecha.format(formato));
        }
        return cell;
    }

}
