package sic.util;

import java.util.HashSet;
import java.util.Set;

public class Validator {

    public static boolean esNumericoPositivo(String cadena) {
        for (int i = 0; i < cadena.length(); i++) {
            if (cadena.charAt(i) < '0' | cadena.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    public static boolean esVacio(String campo) {
        if (campo == null) {
            return true;
        }

        if (campo.equals("")) {
            return true;
        }

        return false;
    }

    public static boolean esLongitudCaracteresValida(String cadena, int cantCaracteresValidos) {
        if (cadena == null) {
            return true;
        }

        if (cadena.length() > cantCaracteresValidos) {
            return false;
        } else {
            return true;
        }
    }    
    
    public static boolean tieneDuplicados(long[] array) {
        Set<Long> set = new HashSet<>();
        for (long i : array) {
            if (set.contains(i)) {
                return true;
            }
            set.add(i);
        }
        return false;
    }
}
