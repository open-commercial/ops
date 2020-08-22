package sic.modelo.criteria;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import sic.modelo.Movimiento;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusquedaReciboCriteria {

  private LocalDateTime fechaDesde;
  private LocalDateTime fechaHasta;
  private long idSucursal;
  private Long numSerie;
  private Long numRecibo;
  private String concepto;
  private Long idCliente;
  private Long idProveedor;
  private Long idUsuario;
  private Long idViajante;
  private Long idFormaDePago;
  private Movimiento movimiento;
  private Integer pagina;
  private String ordenarPor;
  private String sentido;
}
