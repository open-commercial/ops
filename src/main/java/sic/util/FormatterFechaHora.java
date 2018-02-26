package sic.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FormatterFechaHora extends SimpleDateFormat {

    public FormatterFechaHora(String formato) {
        super.applyPattern(formato);        
    }

    public boolean esFechaHoraValida(String cadenaFecha) {
        try {
            this.setLenient(false);
            this.parse(cadenaFecha);

        } catch (ParseException ex) {
            return false;
        }
        return true;
    }

}
