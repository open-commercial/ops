package sic.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"idMovimiento", "tipoComprobante", "fecha"})
public class MovimientoCaja {

    private long idMovimiento;
    private TipoDeComprobante tipoComprobante;
    private String concepto;
    private LocalDateTime fecha;
    private BigDecimal monto;

}
