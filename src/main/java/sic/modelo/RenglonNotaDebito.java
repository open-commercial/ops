package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenglonNotaDebito implements Serializable {
    
    private long idRenglonNotaCredito;
          
    private String descripcion;
    
    private BigDecimal monto; 

    private BigDecimal importeBruto; 

    private BigDecimal ivaPorcentaje;

    private BigDecimal ivaNeto;

    private BigDecimal importeNeto;
   
}
