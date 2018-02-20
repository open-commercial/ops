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
@EqualsAndHashCode(of = {"fecha", "tipoComprobante", "numSerie", "numFactura", "nombreEmpresa"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id_Factura", scope = Factura.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = FacturaCompra.class), 
  @JsonSubTypes.Type(value = FacturaVenta.class) 
})
public abstract class Factura implements Serializable {

    // bug: https://jira.spring.io/browse/DATAREST-304
    @JsonGetter(value = "type")
    public String getType() {
        return this.getClass().getSimpleName();
    }

    private long id_Factura;
    private Date fecha;
    private TipoDeComprobante tipoComprobante;
    private long numSerie;
    private long numFactura;
    private Date fechaVencimiento;  
    private String nombreTransportista;     
    private List<RenglonFactura> renglones;
    private List<Pago> pagos;
    private BigDecimal subTotal;
    private BigDecimal recargo_porcentaje;
    private BigDecimal recargo_neto;
    private BigDecimal descuento_porcentaje;
    private BigDecimal descuento_neto;
    private BigDecimal subTotal_bruto;
    private BigDecimal iva_105_neto;
    private BigDecimal iva_21_neto;
    private BigDecimal impuestoInterno_neto;
    private BigDecimal total;    
    private String observaciones;
    private boolean pagada;    
    private String nombreEmpresa;
    private boolean eliminada;   
    private long CAE;
    private Date vencimientoCAE;
    private long numSerieAfip;
    private long numFacturaAfip;
}
