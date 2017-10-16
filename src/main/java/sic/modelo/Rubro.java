package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre", "empresa"})
public class Rubro implements Serializable {

    private long id_Rubro;
    private String nombre;
    private Empresa empresa;
    private boolean eliminado;

    @Override
    public String toString() {
        return nombre;
    }
}
