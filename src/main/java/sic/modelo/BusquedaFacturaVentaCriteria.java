package sic.modelo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusquedaFacturaVentaCriteria {

    private Date fechaDesde;
    private Date fechaHasta;
    private Long idCliente;
    private TipoDeComprobante tipoComprobante;
    private Long idUsuario;
    private Long idViajante;
    private long numSerie;
    private long numFactura;
    private long nroPedido;
    private Long idProducto;
    private Long idSucursal;
    private Integer pagina;
    private String ordenarPor;
    private String sentido;
}
