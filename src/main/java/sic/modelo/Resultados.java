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
public class Resultados {

    private BigDecimal subTotal;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoNeto;
    private BigDecimal recargoPorcentaje;
    private BigDecimal recargoNeto;
    private BigDecimal subTotalBruto;
    private BigDecimal iva105Neto;
    private BigDecimal iva21Neto;
    private BigDecimal total;

}
