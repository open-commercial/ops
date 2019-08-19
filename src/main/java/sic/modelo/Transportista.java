package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre", "idSucursal"})
public class Transportista implements Serializable {

    private long id_Transportista;
    private String nombre;
    private Ubicacion ubicacion;
    private String web;
    private String telefono;
    private Long idSucursal;
    private String nombreSucursal;
    private boolean eliminado;

    @Override
    public String toString() {
        return nombre;
    }
}
