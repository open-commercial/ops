package sic.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculosPrecioProducto {

    private final static BigDecimal CIEN = new BigDecimal("100");

    public static BigDecimal calcularGananciaPorcentaje(BigDecimal precioDeListaNuevo,
            BigDecimal precioDeListaAnterior, BigDecimal pvp, BigDecimal ivaPorcentaje,
            BigDecimal impInternoPorcentaje, BigDecimal precioCosto, boolean ascendente) {
        //evita la division por cero
        if (precioCosto.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        BigDecimal resultado;
        if (!ascendente) {
            resultado = pvp.subtract(precioCosto).divide(precioCosto, 15, RoundingMode.HALF_UP).multiply(CIEN);
        } else if (precioDeListaAnterior.compareTo(BigDecimal.ZERO) == 0 || precioCosto.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else {
            resultado = precioDeListaNuevo;
            BigDecimal porcentajeIncremento = precioDeListaNuevo.divide(precioDeListaAnterior, 15, RoundingMode.HALF_UP);
            resultado = resultado.subtract(porcentajeIncremento.multiply(impInternoPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP).multiply(pvp)));
            resultado = resultado.subtract(porcentajeIncremento.multiply(ivaPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP).multiply(pvp)));
            resultado = resultado.subtract(precioCosto).multiply(CIEN).divide(precioCosto, 15, RoundingMode.HALF_UP);
        }
        return resultado;
    }

    public static BigDecimal calcularGananciaNeto(BigDecimal precioCosto, BigDecimal gananciaPorcentaje) {
        return precioCosto.multiply(gananciaPorcentaje).divide(CIEN, 15, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularPVP(BigDecimal precioCosto, BigDecimal gananciaPorcentaje) {
        return precioCosto.add(precioCosto.multiply(gananciaPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP)));
    }

    public static BigDecimal calcularIVANeto(BigDecimal pvp, BigDecimal ivaPorcentaje) {
        return pvp.multiply(ivaPorcentaje).divide(CIEN, 15, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularImpInternoNeto(BigDecimal pvp, BigDecimal impInternoPorcentaje) {
        return pvp.multiply(impInternoPorcentaje).divide(CIEN, 15, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularPrecioLista(BigDecimal PVP, BigDecimal ivaPorcentaje, BigDecimal impInternoPorcentaje) {
        BigDecimal resulIVA = PVP.multiply(ivaPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP));
        BigDecimal resultImpInterno = PVP.multiply(impInternoPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP));
        return PVP.add(resulIVA).add(resultImpInterno);
    }

}
