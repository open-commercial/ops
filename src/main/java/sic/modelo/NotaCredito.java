package sic.modelo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class NotaCredito extends Nota implements Serializable {
    
    private List<RenglonNotaCredito> renglonesNotaCredito;
    
    private BigDecimal subTotal;

    private BigDecimal recargoPorcentaje;

    private BigDecimal recargoNeto;

    private BigDecimal descuentoPorcentaje;

    private BigDecimal descuentoNeto;

    public NotaCredito() {
    }

    public NotaCredito(long idNota, long serie, long nroNota, boolean eliminada,
            TipoDeComprobante tipoDeComprobante, Date fecha, long idEmpresa, String nombreEmpresa, long idUsuario, String nombreUsuario,
            long idCliente, String razonSocialCliente, long idProveedor, String razonSocialProveedor, long idFacturaVenta, long idFacturaCompra,
            String motivo, List<RenglonNotaCredito> renglones, BigDecimal subTotalBruto,
            BigDecimal iva21Neto, BigDecimal iva105Neto, BigDecimal total, long CAE, Date vencimientoCAE,
            long numSerieAfip, long numFacturaAfip) {

        super(idNota, serie, nroNota, eliminada, tipoDeComprobante, fecha, idEmpresa, nombreEmpresa,
                idUsuario, nombreUsuario, idCliente, razonSocialCliente, idProveedor, razonSocialProveedor,
                idFacturaVenta, idFacturaCompra, motivo, subTotalBruto, iva21Neto, iva105Neto, total, CAE,
                vencimientoCAE, numSerieAfip, numFacturaAfip);
        this.renglonesNotaCredito = renglones;
    }

}
