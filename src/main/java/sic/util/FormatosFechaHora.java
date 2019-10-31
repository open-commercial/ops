package sic.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatosFechaHora {

    public static final String FORMATO_FECHA_HISPANO = "dd/MM/yyyy";
    public static final String FORMATO_FECHAHORA_HISPANO = "dd/MM/yyyy HH:mm:ss";
    
    public static String formatoFecha(LocalDateTime fecha, String formato) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(formato);
        return fecha.format(format);
    }
}
