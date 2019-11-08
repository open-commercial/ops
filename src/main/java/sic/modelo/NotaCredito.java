package sic.modelo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class NotaCredito extends Nota implements Serializable {
    
    private boolean modificaStock;
    private List<RenglonNotaCredito> renglonesNotaCredito;    
    private BigDecimal subTotal;
    private BigDecimal recargoPorcentaje;
    private BigDecimal recargoNeto;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoNeto;

    public NotaCredito() {}

    public NotaCredito(long idNota, long serie, long nroNota, boolean eliminada,
            TipoDeComprobante tipoDeComprobante, LocalDateTime fecha, long idSucursal, String nombreSucursal, long idUsuario, String nombreUsuario,
            long idCliente, String nombreFiscalCliente, long idProveedor, String razonSocialProveedor, long idFacturaVenta, long idFacturaCompra,
            String motivo, List<RenglonNotaCredito> renglones, BigDecimal subTotalBruto,
            BigDecimal iva21Neto, BigDecimal iva105Neto, BigDecimal total, long cae, LocalDate vencimientoCAE,
            long numSerieAfip, long numFacturaAfip) {

        super(idNota, serie, nroNota, eliminada, tipoDeComprobante, fecha, idSucursal, nombreSucursal,
                idUsuario, nombreUsuario, idCliente, nombreFiscalCliente, idProveedor, razonSocialProveedor,
                idFacturaVenta, idFacturaCompra, motivo, subTotalBruto, iva21Neto, iva105Neto, total, cae,
                vencimientoCAE, numSerieAfip, numFacturaAfip);
        this.renglonesNotaCredito = renglones;
    }

}
