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
public class NuevaNotaCreditoSinFactura {

    private Long idCliente;
    private Long idProveedor;
    private long idSucursal;
    private BigDecimal monto;
    private TipoDeComprobante tipo;
    private String detalle;
    private String motivo;

}
