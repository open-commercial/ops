package sic.modelo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
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
@JsonSubTypes({
  @JsonSubTypes.Type(value = NotaDebitoCliente.class), 
  @JsonSubTypes.Type(value = NotaDebitoProveedor.class)
})
public abstract class NotaDebito extends Nota implements Serializable {

    private List<RenglonNotaDebito> renglonesNotaDebito;

    private BigDecimal montoNoGravado;
    
    private Recibo recibo;
    
    public NotaDebito() {}

    public NotaDebito(long idNota, long serie, long nroNota, boolean eliminada,
            TipoDeComprobante tipoDeComprobante, Date fecha, Empresa empresa, Usuario usuario, String motivo, List<RenglonNotaDebito> renglones, 
            BigDecimal subTotalBruto, BigDecimal iva21Neto, BigDecimal iva105Neto, BigDecimal total, BigDecimal montoNoGravado, long CAE, 
            Date vencimientoCAE, long numSerieAfip, long numNotaAfip, Recibo recibo) {

        super(idNota, serie, nroNota, eliminada, tipoDeComprobante, fecha, empresa, usuario,
              motivo, subTotalBruto, iva21Neto, iva105Neto, total, CAE, vencimientoCAE, numSerieAfip, numNotaAfip);
        this.montoNoGravado = montoNoGravado;
        this.renglonesNotaDebito = renglones;
        this.recibo = recibo;
    }

}
