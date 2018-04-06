package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotaCreditoProveedor extends NotaCredito implements Serializable {

    private Proveedor proveedor;
    
    private FacturaCompra facturaCompra;
    
    public NotaCreditoProveedor() {}

    public NotaCreditoProveedor(long idNota, long serie, FacturaCompra facturaCompra, long nroNota, boolean eliminada,
            TipoDeComprobante tipoDeComprobante, Date fecha, Empresa empresa,
            Usuario usuario, String motivo, List<RenglonNotaCredito> renglones, BigDecimal subTotalBruto, BigDecimal iva21Neto,
            BigDecimal iva105Neto, BigDecimal total, long CAE, Date vencimientoCAE,
            long numSerieAfip, long numFacturaAfip, Proveedor proveedor) {

        super(idNota, serie, nroNota, eliminada, tipoDeComprobante, fecha, empresa, usuario, motivo, renglones, 
                subTotalBruto, iva21Neto, iva105Neto, total, CAE, vencimientoCAE, numSerieAfip, numFacturaAfip);
        this.facturaCompra = facturaCompra;
        this.proveedor = proveedor;
    }
    
}
