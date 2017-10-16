package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre"})
public class Pais implements Serializable {

    private long id_Pais;
    private String nombre;    
    private boolean eliminado = false;

    @Override
    public String toString() {
        return nombre;
    }
}
