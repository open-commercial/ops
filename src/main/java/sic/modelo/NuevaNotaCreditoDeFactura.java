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
public class NuevaNotaCreditoDeFactura {

    private Long idFactura;
    private BigDecimal[] cantidades;
    private Long[] idsRenglonesFactura;
    private boolean modificaStock;
    private String motivo;
    private DetalleCompra detalleCompra;

}
