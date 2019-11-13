package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre", "idEmpresa"})
public class Transportista implements Serializable {

    private long idTransportista;
    private String nombre;
    private Ubicacion ubicacion;
    private String web;
    private String telefono;
    private Long idEmpresa;
    private String nombreEmpresa;
    private boolean eliminado;

    @Override
    public String toString() {
        return nombre;
    }
}
