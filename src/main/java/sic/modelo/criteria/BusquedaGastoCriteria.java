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
public class BusquedaGastoCriteria {

  private LocalDateTime fechaDesde;
  private LocalDateTime fechaHasta;
  private Long idEmpresa;
  private Long idUsuario;
  private Long idFormaDePago;
  private Long nroGasto;
  private String concepto;
  private Integer pagina;
  private String ordenarPor;
  private String sentido;
}