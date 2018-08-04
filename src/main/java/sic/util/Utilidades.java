package sic.util;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JTable;

public class Utilidades {

    /**
     * Verifica si ya existe una instancia de la clase @tipo dentro de @desktop
     * 
     * @param desktop Contenedor de internal frames donde debe buscar
     * @param tipo Clase buscada
     * @return Internal frame encontrado, en caso contrario devuelve NULL
     */
    public static JInternalFrame estaEnDesktop(JDesktopPane desktop, Class tipo) {
        JInternalFrame[] frames = desktop.getAllFrames();
        for (JInternalFrame fr : frames) {
            if (tipo.isAssignableFrom(fr.getClass())) {
                return fr;
            }
        }
        return null;
    }
    
    public static void cerrarTodasVentanas(JDesktopPane desktop) {
        JInternalFrame[] frames = desktop.getAllFrames();
        for (JInternalFrame frame : frames) {
            frame.dispose();
        }
    }

    /**
     * Convierte un caracter de minusculas a mayusculas
     *
     * @param caracter Caracter para ser convertido
     * @return Devuelve el caracter ya convertido a mayusculas
     */
    public static char convertirAMayusculas(char caracter) {
        if ((caracter >= 'a' && caracter <= 'z') || caracter == 'ñ') {
            return (char) (((int) caracter) - 32);
        } else {
            return caracter;
        }
    }

    /**
     * Devuelve los indices de las filas seleccionadas luego de que el JTable
     * haya sido ordenado. Al utilizar getSelectedRows() despues de un
     * ordenamiento, devuelve mal los indices.
     *
     * @param table JTable donde debe buscar los indices correctos
     * @return indices seleccionados
     */
    public static int[] getSelectedRowsModelIndices(JTable table) {
        if (table == null) throw new NullPointerException("table == null");
        int[] selectedRowIndices = table.getSelectedRows();
        int countSelected = selectedRowIndices.length;
        for (int i = 0; i < countSelected; i++) {
            selectedRowIndices[i] = table.convertRowIndexToModel(selectedRowIndices[i]);
        }
        return selectedRowIndices;
    }

    /**
     * Devuelve el indice de la fila seleccionada luego de que el JTable haya
     * sido ordenado. Al utilizar getSelectedRow() despues de un ordenamiento,
     * devuelve mal el indice.
     *
     * @param table JTable donde debe buscar el indice correcto
     * @return indice seleccionado
     */
    public static int getSelectedRowModelIndice(JTable table) {
        if (table == null) throw new NullPointerException("table == null");
        int selectedRowIndice = table.getSelectedRow();
        selectedRowIndice = table.convertRowIndexToModel(selectedRowIndice);
        return selectedRowIndice;
    }

    /**
     * Convierte el archivo en un array de bytes.
     *
     * @param archivo Archivo a ser convertido.
     * @return Array de byte representando al archivo.
     * @throws java.io.IOException
     */
    public static byte[] convertirFileIntoByteArray(File archivo) throws IOException {
        byte[] bArchivo = new byte[(int) archivo.length()];
        FileInputStream fileInputStream = new FileInputStream(archivo);
        fileInputStream.read(bArchivo);
        fileInputStream.close();
        return bArchivo;
    }

    /**
     * Valida el tamanio del archivo, teniendo en cuenta el tamanioValido.
     *
     * @param archivo Archivo a ser validado.
     * @param tamanioValido Tamanio maximo en bytes permitido para el archivo.
     * @return Retorna true en caso de que el tamanio sea válido, false en otro caso.
     * @throws FileNotFoundException En caso de que no se encuentre el archivo.
     */
    public static boolean esTamanioValido(File archivo, long tamanioValido) throws FileNotFoundException {
        if (archivo == null) {
            throw new FileNotFoundException();
        }
        return archivo.length() <= tamanioValido;
    }

    public static void controlarEntradaSoloNumerico(KeyEvent evt) {
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9')) {
            evt.consume();
        }
    }

}
