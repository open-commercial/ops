package sic.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class ColoresNumerosTablaRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable tabla,
            Object valor, boolean isSelected, boolean hasFocus,
            int row, int column) {

        JLabel cell = (JLabel) super.getTableCellRendererComponent(tabla, valor, isSelected, hasFocus, row, column);
        this.setHorizontalAlignment(SwingConstants.RIGHT);
        if (valor instanceof BigDecimal) {
            BigDecimal numero = ((BigDecimal)valor).setScale(2, RoundingMode.HALF_UP);
            cell.setText(numero.setScale(2, RoundingMode.HALF_UP).toString());
            if (numero.compareTo(BigDecimal.ZERO) > 0) {
                cell.setBackground(Color.GREEN);
            }
            if (numero.compareTo(BigDecimal.ZERO) < 0) {
                cell.setBackground(Color.PINK);
            }
            if (numero.compareTo(BigDecimal.ZERO) == 0) {
                cell.setBackground(Color.WHITE);
            }
        } else {
            cell.setBackground(Color.WHITE);
        }
        cell.setFont(new Font("Font", Font.BOLD, 12));
        return cell;
    }

}
