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
public class NotaDebito extends Nota implements Serializable {

    private List<RenglonNotaDebito> renglonesNotaDebito;
    private BigDecimal montoNoGravado;
    private Long idRecibo;
    private Long numSerieRecibo;
    private Long nroRecibo;
    
    public NotaDebito() {}

    public NotaDebito(long idNota, long serie, long nroNota, boolean eliminada,
            TipoDeComprobante tipoDeComprobante, LocalDateTime fecha, long idSucursal, String nombreSucursal, long idUsuario, String nombreUsuario,
            long idCliente, String nombreFiscalCliente, long idProveedor, String razonSocialProveedor,
            String motivo, List<RenglonNotaDebito> renglones, BigDecimal subTotalBruto,
            BigDecimal iva21Neto, BigDecimal iva105Neto, BigDecimal total, BigDecimal montoNoGravado, long CAE, LocalDate vencimientoCAE,
            long numSerieAfip, long numFacturaAfip, long idRecibo) {

        super(idNota, serie, nroNota, eliminada, tipoDeComprobante, fecha, idSucursal, nombreSucursal,
                idUsuario, nombreUsuario, idCliente, nombreFiscalCliente, idProveedor, razonSocialProveedor,
                0, 0, motivo, subTotalBruto, iva21Neto, iva105Neto, total, CAE,
                vencimientoCAE, numSerieAfip, numFacturaAfip);
        this.montoNoGravado = montoNoGravado;
        this.renglonesNotaDebito = renglones;
        this.idRecibo = idRecibo;
    }

}
