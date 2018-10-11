package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id_ProductoItem", "codigoItem"})
public class RenglonFactura implements Serializable {

    private long id_RenglonFactura;
    private long idProductoItem;
    private String codigoItem;
    private String descripcionItem;
    private String medidaItem;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoNeto;
    private BigDecimal ivaPorcentaje;
    private BigDecimal ivaNeto;
    private BigDecimal impuestoPorcentaje;
    private BigDecimal impuestoNeto;
    private BigDecimal gananciaPorcentaje;
    private BigDecimal gananciaNeto;
    private BigDecimal importe;
}
