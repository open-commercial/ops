package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "nombre")
public class Transportista implements Serializable {

    private long idTransportista;
    private String nombre;
    private Ubicacion ubicacion;
    private String web;
    private String telefono;
    private boolean eliminado;

    @Override
    public String toString() {
        return nombre;
    }
}
