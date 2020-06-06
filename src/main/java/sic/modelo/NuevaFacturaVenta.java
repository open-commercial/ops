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
public class NuevaFacturaVenta {

    private Long idSucursal;
    private Long idPedido;
    private Long idCliente;
    private Long idTransportista;
    private TipoDeComprobante tipoDeComprobante;
    private String observaciones;
    private boolean[] renglonMarcado;
    private Long[] idsFormaDePago;
    private BigDecimal[] montos;
    private int[] indices;
    private BigDecimal recargoPorcentaje;
    private BigDecimal descuentoPorcentaje;
}
