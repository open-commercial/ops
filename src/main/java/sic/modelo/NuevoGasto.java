package sic.modelo;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"idGasto", "idSucursal", "idFormaDePago"})
public class NuevoGasto {

    private long idGasto;
    private String concepto;
    private Long idSucursal;
    private Long idFormaDePago;
    private BigDecimal monto;
}
