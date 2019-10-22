package sic.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatosFechaHora {

    public static final String FORMATO_FECHAHORA_INTERNACIONAL = "yyyy/MM/dd HH:mm:ss";
    public static final String FORMATO_FECHA_INTERNACIONAL = "yyyy/MM/dd";
    public static final String FORMATO_HORA_INTERNACIONAL = "HH:mm:ss";
    public static final String FORMATO_FECHA_HISPANO = "dd/MM/yyyy";
    public static final String FORMATO_FECHAHORA_HISPANO = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMATO_FECHAHORA_LETRAS = "EEE, d MMM yyyy HH:mm";
    
    public static String formatoFecha(LocalDateTime fecha, String formato) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(formato);
        return fecha.format(format);
    }
}
