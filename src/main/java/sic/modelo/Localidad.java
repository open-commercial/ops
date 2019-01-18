package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre"})
public class Localidad implements Serializable {

    private long id_Localidad;
    private String nombre;
    private String codigoPostal;
    private Long idProvincia;
    private String nombreProvincia;
    private String nombrePais;
    private boolean eliminada = false;

    @Override
    public String toString() {
        return nombre;
    }
}
