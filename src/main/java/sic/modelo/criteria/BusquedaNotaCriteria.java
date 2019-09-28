package sic.modelo.criteria;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sic.modelo.Movimiento;
import sic.modelo.TipoDeComprobante;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusquedaNotaCriteria {

    private Date fechaDesde;
    private Date fechaHasta;
    private Long idEmpresa;
    private int cantidadDeRegistros;
    private Long numSerie;
    private Long numNota;
    private TipoDeComprobante tipoComprobante;
    private Movimiento movimiento;
    private Long idUsuario;
    private Long idProveedor;
    private Long idCliente;
    private Long idViajante;
    private Integer pagina;
    private String ordenarPor;
    private String sentido;
}
