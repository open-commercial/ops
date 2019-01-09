package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre", "empresa"})
public class Medida implements Serializable {

    private long id_Medida;
    private String nombre;
    private Long idEmpresa;
    private String nombreEmpresa;
    private boolean eliminada;

    @Override
    public String toString() {
        return nombre;
    }
}
