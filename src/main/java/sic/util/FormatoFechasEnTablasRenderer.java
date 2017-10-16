package sic.util;

import java.awt.Component;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class FormatoFechasEnTablasRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable tabla,
            Object valor, boolean isSelected, boolean hasFocus,
            int row, int column) {

        JLabel cell = (JLabel) super.getTableCellRendererComponent(tabla, valor, isSelected, hasFocus, row, column);
        if (valor instanceof Date) {
            Date fecha = (Date) valor;
            cell.setText((new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_HISPANO)).format(fecha));
        }
        return cell;
    }

}
