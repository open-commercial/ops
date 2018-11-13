package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "producto")
public class RenglonPedido implements Serializable {
    
    private long id_RenglonPedido;
    private long idProductoItem;
    private String descripcionItem;
    private String medidaItem;
    private BigDecimal precioUnitario;
    private String codigoItem;
    private BigDecimal cantidad;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoNeto;
    private BigDecimal importe;
    
}