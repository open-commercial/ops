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
public class NuevoRemito {

  private long idFacturaVenta;
  private long idTransportista;
  private TipoBulto[] tiposDeBulto;
  private BigDecimal[] cantidadPorBulto;
  private BigDecimal costoDeEnvio;
  private BigDecimal pesoTotalEnKg;
  private BigDecimal volumenTotalEnM3;
  private String observaciones;
}

