package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "producto")
public class RenglonPedido implements Serializable {

    private long id_RenglonPedido;   
    private Producto producto;
    private BigDecimal cantidad;
    private BigDecimal descuento_porcentaje;
    private BigDecimal descuento_neto;
    private BigDecimal subTotal;
    private long idProducto;
    private String codigoProducto;
    private String descripcionProducto;
    private BigDecimal precioDeListaProducto; 
    
}