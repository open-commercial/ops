package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotaDebito extends Nota implements Serializable {

    private List<RenglonNotaDebito> renglonesNotaDebito;
    
    private BigDecimal montoNoGravado;

    public NotaDebito() {}

    public NotaDebito(long idNota, long serie, long nroNota, FacturaVenta facturaVenta, boolean eliminada,
            TipoDeComprobante tipoDeComprobante, Date fecha, Empresa empresa, Cliente cliente,
            Usuario usuario, Pago pago, List<Pago> pagos, String motivo, BigDecimal subTotalBruto, BigDecimal iva21Neto,
            BigDecimal iva105Neto, BigDecimal total, BigDecimal montoNoGravado, long CAE, Date vencimientoCAE,
            long numSerieAfip, long numFacturaAfip, List<RenglonNotaDebito> renglonesDebito) {
        
        super(idNota, serie, nroNota, facturaVenta, pagos, eliminada, tipoDeComprobante, fecha, empresa, cliente, usuario,
                motivo, subTotalBruto, iva21Neto, iva105Neto, total, CAE, vencimientoCAE, numSerieAfip, numFacturaAfip);
        this.montoNoGravado = montoNoGravado;
        this.renglonesNotaDebito = renglonesDebito;
    }

}
