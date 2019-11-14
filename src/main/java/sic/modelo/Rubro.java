package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "nombre")
public class Rubro implements Serializable {

    private long idRubro;
    private String nombre;
    private boolean eliminado;

    @Override
    public String toString() {
        return nombre;
    }
}
