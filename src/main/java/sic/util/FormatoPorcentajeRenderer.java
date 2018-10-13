package sic.util;

import java.awt.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class FormatoPorcentajeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel cell = (JLabel) super.getTableCellRendererComponent(tabla, valor, isSelected, hasFocus, row, column);
        this.setHorizontalAlignment(SwingConstants.LEFT);
        if (valor instanceof Number) {
            BigDecimal numero = ((BigDecimal) valor).setScale(2, RoundingMode.HALF_UP);
            DecimalFormat dFormat;
            if (numero.compareTo(BigDecimal.ZERO) == 0) {
                dFormat = new DecimalFormat("##0");
            } else {
                dFormat = new DecimalFormat("##0.00");
            }
            cell.setText(dFormat.format(numero) + "%");
        }
        return cell;
    }

}
