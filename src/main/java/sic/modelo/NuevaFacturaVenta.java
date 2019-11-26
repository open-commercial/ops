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

  private FacturaVenta facturaVenta;
  private Long[] idsFormaDePago;
  private BigDecimal[] montos;
  private int[] indices;
  private Long idPedido;
}