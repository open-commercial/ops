package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre", "empresa"})
public class Transportista implements Serializable {

    private long id_Transportista;
    private String nombre;
    private String direccion;
    private Long idLocalidad;
    private String nombreLocalidad;
    private String nombreProvincia;
    private String nombrePais;
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
