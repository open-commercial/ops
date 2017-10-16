package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre"})
public class Provincia implements Serializable {

    private long id_Provincia;
    private String nombre;    
    private Pais pais;    
    private boolean eliminada = false;

    @Override
    public String toString() {
        return nombre;
    }
}
