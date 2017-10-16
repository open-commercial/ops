package sic.util;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import sic.modelo.EstadoCaja;
import sic.modelo.EstadoPedido;

public class ColoresEstadosRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable tabla,
            Object valor, boolean isSelected, boolean hasFocus,
            int row, int column) {

        JLabel cell = (JLabel) super.getTableCellRendererComponent(tabla, valor, isSelected, hasFocus, row, column);
        if (valor instanceof EstadoPedido) {
            EstadoPedido estado = (EstadoPedido) valor;
            if (estado == EstadoPedido.ABIERTO) {
                cell.setBackground(Color.GREEN);
            }
            if (estado == EstadoPedido.ACTIVO) {
                cell.setBackground(Color.YELLOW);
            }
            if (estado == EstadoPedido.CERRADO) {
                cell.setBackground(Color.PINK);
            }
        }
        if (valor instanceof EstadoCaja) {
            EstadoCaja estado = (EstadoCaja) valor;
            if (estado == EstadoCaja.ABIERTA) {
                cell.setBackground(Color.GREEN);
            }
            if (estado == EstadoCaja.CERRADA) {
                cell.setBackground(Color.PINK);
            }
        }
        return cell;
    }
}
