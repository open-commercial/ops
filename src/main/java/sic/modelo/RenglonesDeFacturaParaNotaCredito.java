package sic.modelo;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RenglonesDeFacturaParaNotaCredito {

    Map<Long, BigDecimal> idsYCantidades;
    TipoDeComprobante tipoDeComprobante;
}
