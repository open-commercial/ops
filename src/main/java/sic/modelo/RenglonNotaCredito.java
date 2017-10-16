package sic.modelo;

import java.io.Serializable;
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
        
    private double cantidad;
    
    private double precioUnitario;
        
    private double gananciaPorcentaje;
        
    private double gananciaNeto;
    
    private double importe; 
    
    private double descuentoPorcentaje;
    
    private double descuentoNeto; 
    
    private double importeBruto;
        
    private double ivaPorcentaje;
    
    private double ivaNeto;
    
    private double importeNeto; 

}
