package sic.util;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FiltroImagenes extends FileFilter {

    @Override
    public boolean accept(File fotoElegida) {
        String extension = "";
        if (fotoElegida.getAbsolutePath().lastIndexOf(".") > 0) {
            extension = fotoElegida.getAbsolutePath().substring(
                    fotoElegida.getAbsolutePath().lastIndexOf(".") + 1).toLowerCase();
        }

        if (!extension.equals("")) {
            return extension.equals("jpg") | extension.equals("png");
        } else {
            return fotoElegida.isDirectory();
        }
    }

    @Override
    public String getDescription() {
        return "Archivos de Imagenes (*.jpg y *.png)";
    }
}
