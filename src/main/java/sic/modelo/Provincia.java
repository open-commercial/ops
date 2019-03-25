package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre"})
public class Provincia implements Serializable {

    private long idProvincia;
    private String nombre;     

    @Override
    public String toString() {
        return nombre;
    }
}
