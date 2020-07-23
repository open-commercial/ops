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
public class LocalidadesParaActualizarDTO {

    private long[] idLocalidad;
    private BigDecimal costoDeEnvio;
    private boolean envioGratuito;
}
