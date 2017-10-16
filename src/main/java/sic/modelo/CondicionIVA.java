package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre"})
public class CondicionIVA implements Serializable {

    private long id_CondicionIVA;    
    private String nombre;
    private boolean discriminaIVA;
    private boolean eliminada;

    @Override
    public String toString() {
        if (discriminaIVA) {
            return nombre + " (discrimina IVA)";
        } else {
            return nombre + " (no discrimina IVA)";
        }
    }
}
