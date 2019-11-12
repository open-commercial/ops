package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre", "idEmpresa"})
public class Medida implements Serializable {

    private long idMedida;
    private String nombre;
    private Long idEmpresa;
    private String nombreEmpresa;
    private boolean eliminada;

    @Override
    public String toString() {
        return nombre;
    }
}
