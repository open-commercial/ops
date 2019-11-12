package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre", "idEmpresa"})
public class FormaDePago implements Serializable {

    private long idFormaDePago;
    private String nombre;
    private boolean afectaCaja;
    private boolean predeterminado;
    private Long idEmpresa;
    private String nombreEmpresa;
    private boolean eliminada;

    @Override
    public String toString() {
        return nombre;
    }
}
