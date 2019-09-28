package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre", "idEmpresa"})
public class Rubro implements Serializable {

    private long id_Rubro;
    private String nombre;
    private Long idEmpresa;
    private String nombreEmpresa;
    private boolean eliminado;

    @Override
    public String toString() {
        return nombre;
    }
}
