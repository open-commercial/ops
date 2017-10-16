package sic.modelo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenglonNotaDebito implements Serializable {
    
    private long idRenglonNotaCredito;
          
    private String descripcion;
    
    private double monto; 

    private double importeBruto; 

    private double ivaPorcentaje;

    private double ivaNeto;

    private double importeNeto;
   
}
