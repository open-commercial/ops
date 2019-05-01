package sic.modelo;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RenglonesDeFacturaParaNotaCredito {

    private Long[] idsRenglonesFactura;
    private BigDecimal[] cantidades;
    TipoDeComprobante tipoDeComprobante;
}
