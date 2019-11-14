package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "idProductoItem")
public class RenglonPedido implements Serializable {
    
    private long idRenglonPedido;
    private long idProductoItem;
    private String descripcionItem;
    private String medidaItem;
    private String urlImagenItem;
    private boolean oferta;
    private BigDecimal precioUnitario;
    private String codigoItem;
    private BigDecimal cantidad;
    private BigDecimal bonificacionPorcentaje;
    private BigDecimal bonificacionNeta;
    private BigDecimal importe;
    
}