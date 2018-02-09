package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"idProductoItem", "codigoItem"})
public class RenglonNotaCredito implements Serializable {

    private long idRenglonNotaCredito;
    
    private long idProductoItem;

    private String codigoItem;

    private String descripcionItem;

    private String medidaItem;
        
    private BigDecimal cantidad;
    
    private BigDecimal precioUnitario;
        
    private BigDecimal gananciaPorcentaje;
        
    private BigDecimal gananciaNeto;
    
    private BigDecimal importe; 
    
    private BigDecimal descuentoPorcentaje;
    
    private BigDecimal descuentoNeto; 
    
    private BigDecimal importeBruto;
        
    private BigDecimal ivaPorcentaje;
    
    private BigDecimal ivaNeto;
    
    private BigDecimal importeNeto; 

}
