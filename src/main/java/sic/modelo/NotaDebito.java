package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotaDebito extends Nota implements Serializable {
    
    private Long pagoId;

    private List<RenglonNotaDebito> renglonesNotaDebito;
    
    private double montoNoGravado;

    public NotaDebito() {}

    public NotaDebito(long idNota, long serie, long nroNota, FacturaVenta facturaVenta, boolean eliminada,
            TipoDeComprobante tipoDeComprobante, Date fecha, Empresa empresa, Cliente cliente,
            Usuario usuario, Pago pago, List<Pago> pagos, String motivo, double subTotalBruto, double iva21Neto,
            double iva105Neto, double total, double montoNoGravado, long CAE, Date vencimientoCAE,
            long numSerieAfip, long numFacturaAfip, List<RenglonNotaDebito> renglonesDebito) {
        
        super(idNota, serie, nroNota, facturaVenta, pagos, eliminada, tipoDeComprobante, fecha, empresa, cliente, usuario,
                motivo, subTotalBruto, iva21Neto, iva105Neto, total, CAE, vencimientoCAE, numSerieAfip, numFacturaAfip);
        this.montoNoGravado = montoNoGravado;
        this.renglonesNotaDebito = renglonesDebito;
    }

}
