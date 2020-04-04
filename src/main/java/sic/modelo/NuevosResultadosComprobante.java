package sic.modelo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NuevosResultadosComprobante {

    private BigDecimal[] importe;
    private BigDecimal[] ivaPorcentajes;
    private BigDecimal[] ivaNetos;
    private BigDecimal[] cantidades;
    private TipoDeComprobante tipoDeComprobante;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal recargoPorcentaje;

}
