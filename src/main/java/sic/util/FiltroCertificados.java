package sic.util;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FiltroCertificados extends FileFilter {

    @Override
    public boolean accept(File certificado) {
        String extension = "";
        if (certificado.getAbsolutePath().lastIndexOf(".") > 0) {
            extension = certificado.getAbsolutePath().substring(
                    certificado.getAbsolutePath().lastIndexOf(".") + 1).toLowerCase();
        }

        if (!extension.equals("")) {
            return extension.equals("p12");
        } else {
            return certificado.isDirectory();
        }
    }

    @Override
    public String getDescription() {
        return "Archivo de Certificado (*.p12)";
    }
    
}
