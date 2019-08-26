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
public class NuevaNotaDebitoDeRecibo {

    private long idRecibo;
    private BigDecimal gastoAdministrativo;
    private String motivo;
    private Long serie;
    private Long nroNota;
    private Long CAE;
    private TipoDeComprobante tipoDeComprobante;
}
