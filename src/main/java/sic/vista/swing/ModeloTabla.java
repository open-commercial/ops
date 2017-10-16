package sic.vista.swing;

import javax.swing.table.DefaultTableModel;

public class ModeloTabla extends DefaultTableModel {

    Class[] tipos = null;
    Boolean[] editables = null;

    public void setClaseColumnas(Class[] clases) {
        tipos = clases;
    }

    public void setEditables(Boolean[] columsEditables) {
        editables = columsEditables;
    }

    @Override
    public Class getColumnClass(int columna) {
        //si no esta en uso
        if (tipos == null) {
            return Object.class;
        }

        for (int i = 0; i < tipos.length; i++) {
            if (columna == i) {
                return tipos[i];
            }
        }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        //si no esta en uso
        if (editables == null) {
            return false;
        }

        for (int i = 0; i < editables.length; i++) {
            if (column == i && editables[i] == true) {
                return true;
            }
        }
        return false;
    }
}
