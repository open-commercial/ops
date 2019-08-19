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
@EqualsAndHashCode(of = {"fecha", "tipoComprobante", "numSerie", "numFactura", "nombreSucursal"})
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
    private String nombreUsuario;
    private Date fecha;
    private TipoDeComprobante tipoComprobante;
    private long numSerie;
    private long numFactura;
    private Date fechaVencimiento;  
    private Long nroPedido;
    private long idTransportista;
    private String nombreTransportista;     
    private List<RenglonFactura> renglones;
    private BigDecimal subTotal;
    private BigDecimal recargoPorcentaje;
    private BigDecimal recargoNeto;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoNeto;
    private BigDecimal subTotalBruto;
    private BigDecimal iva105Neto;
    private BigDecimal iva21Neto;
    private BigDecimal total;    
    private String observaciones;
    private BigDecimal cantidadArticulos;
    private long idSucursal;
    private String nombreSucursal;
    private boolean eliminada;   
    private long CAE;
    private Date vencimientoCAE;
    private long numSerieAfip;
    private long numFacturaAfip;
}
