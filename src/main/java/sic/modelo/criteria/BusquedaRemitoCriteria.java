package sic.modelo.criteria;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusquedaRemitoCriteria {

    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
    private Long serieRemito;
    private Long nroRemito;
    private Long idCliente;
    private Long idSucursal;
    private Long idUsuario;
    private Long idTransportista;
    private Long serieFacturaVenta;
    private Long nroFacturaVenta;
    private Integer pagina;
    private String ordenarPor;
    private String sentido;
}
