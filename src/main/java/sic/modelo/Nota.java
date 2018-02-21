package sic.modelo;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"fecha", "tipoComprobante", "serie", "nroNota", "empresa", "cliente"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idNota", scope = Nota.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = NotaCredito.class), 
  @JsonSubTypes.Type(value = NotaDebito.class) 
})
public abstract class Nota implements Serializable {

    @JsonGetter(value = "type")
    public String getType() {
        return this.getClass().getSimpleName();
    }
    
    private long idNota;
    private long serie;
    private long nroNota;
    private boolean eliminada;
    private TipoDeComprobante tipoComprobante;
    private Date fecha; 
    private Empresa empresa;
    private Cliente cliente;   
    private Usuario usuario;    
    private FacturaVenta facturaVenta;
    private String motivo;
    private BigDecimal subTotalBruto;
    private BigDecimal iva21Neto;      
    private BigDecimal iva105Neto;
    private BigDecimal total;
    private long CAE;
    private Date vencimientoCAE;
    private long numSerieAfip;
    private long numFacturaAfip;
    
    public Nota() {}

    public Nota(long idNota, long serie, long nroNota, FacturaVenta facturaVenta, boolean eliminada,
            TipoDeComprobante tipoDeComprobante, Date fecha, Empresa empresa, Cliente cliente, Usuario usuario,
            String motivo, BigDecimal subTotalBruto, BigDecimal iva21Neto, BigDecimal iva105Neto,
            BigDecimal total, long CAE, Date vencimientoCAE, long numSerieAfip, long numFacturaAfip) {

        this.idNota = idNota;
        this.serie = serie;
        this.nroNota = nroNota;
        this.eliminada = eliminada;
        this.tipoComprobante = tipoDeComprobante;
        this.fecha = fecha;
        this.empresa = empresa;
        this.cliente = cliente;
        this.usuario = usuario;
        this.facturaVenta = facturaVenta;
        this.motivo = motivo;
        this.subTotalBruto = subTotalBruto;
        this.iva21Neto = iva21Neto;
        this.iva105Neto = iva105Neto;
        this.total = total;
        this.CAE = CAE;
        this.vencimientoCAE = vencimientoCAE;
        this.numSerieAfip = numSerieAfip;
        this.numFacturaAfip = numFacturaAfip;
    }
}
