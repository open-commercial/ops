package sic.modelo;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"fecha", "tipoComprobante", "serie", "nroNota", "empresa"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idNota", scope = Nota.class)
@JsonSubTypes({
  @JsonSubTypes.Type(value = NotaCredito.class),
  @JsonSubTypes.Type(value = NotaDebito.class), 
})
public class Nota implements Serializable {

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
    private Long idEmpresa;
    private String nombreEmpresa;    
    private Long idUsuario;
    private String nombreUsuario;   
    private Long idCliente;
    private String nombreFiscalCliente;
    private Long idViajante;
    private String nombreViajante;
    private Long idProveedor;
    private String razonSocialProveedor;
    private Long idFacturaVenta;
    private Long idFacturaCompra;
    private Movimiento movimiento;
    private String motivo;
    private BigDecimal subTotalBruto;
    private BigDecimal iva21Neto;      
    private BigDecimal iva105Neto;
    private BigDecimal total;
    private long CAE;
    private Date vencimientoCAE;
    private long numSerieAfip;
    private long numNotaAfip;

    public Nota() {
    }

    public Nota(long idNota, long serie, long nroNota, boolean eliminada,
            TipoDeComprobante tipoDeComprobante, Date fecha, long idEmpresa, String nombreEmpresa, 
            long idUsuario, String nombreUsuario, long idCliente, String nombreFiscalCliente,
            long idProveedor, String razonSocialProveedor, long idFacturaVenta, long idFacturaCompra, 
            String motivo, BigDecimal subTotalBruto, BigDecimal iva21Neto, BigDecimal iva105Neto, 
            BigDecimal total, long CAE, Date vencimientoCAE, long numSerieAfip, long numNotaAfip) {

        this.idNota = idNota;
        this.serie = serie;
        this.nroNota = nroNota;
        this.eliminada = eliminada;
        this.tipoComprobante = tipoDeComprobante;
        this.fecha = fecha;
        this.idEmpresa = idEmpresa;
        this.nombreEmpresa = nombreEmpresa;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idCliente = idCliente;
        this.nombreFiscalCliente = nombreFiscalCliente;
        this.idProveedor = idProveedor;
        this.razonSocialProveedor = razonSocialProveedor;
        this.idFacturaVenta = idFacturaVenta;
        this.idFacturaCompra = idFacturaCompra;
        this.motivo = motivo;
        this.subTotalBruto = subTotalBruto;
        this.iva21Neto = iva21Neto;
        this.iva105Neto = iva105Neto;
        this.total = total;
        this.CAE = CAE;
        this.vencimientoCAE = vencimientoCAE;
        this.numSerieAfip = numSerieAfip;
        this.numNotaAfip = numNotaAfip;
    }
}
