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
public class NuevaNotaDebitoSinRecibo {

    private Long idCliente;
    private Long idProveedor;
    private long idSucursal;
    private String motivo;
    private BigDecimal gastoAdministrativo;
    private TipoDeComprobante tipoDeComprobante;
    private DetalleCompra detalleCompra;
    
}
