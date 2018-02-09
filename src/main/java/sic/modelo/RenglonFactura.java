package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id_ProductoItem", "codigoItem"})
public class RenglonFactura implements Serializable {

    private long id_RenglonFactura;
    private long id_ProductoItem;
    private String codigoItem;
    private String descripcionItem;
    private String medidaItem;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento_porcentaje;
    private BigDecimal descuento_neto;
    private BigDecimal iva_porcentaje;
    private BigDecimal iva_neto;
    private BigDecimal impuesto_porcentaje;
    private BigDecimal impuesto_neto;
    private BigDecimal ganancia_porcentaje;
    private BigDecimal ganancia_neto;
    private BigDecimal importe;
}
