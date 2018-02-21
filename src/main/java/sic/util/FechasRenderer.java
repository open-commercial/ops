package sic.util;

import java.awt.Component;
import java.util.Date;
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
        if (valor instanceof Date) {
            Date fecha = (Date) valor;
            cell.setText((new FormatterFechaHora(this.formato)).format(fecha));
        }
        return cell;
    }

}
