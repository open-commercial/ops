package sic.modelo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre"})
public class Usuario implements Serializable {

    private long id_Usuario;
    private String nombre;
    private String password;
    private String token;
    private List<Rol> roles;
    private boolean eliminado;

    @Override
    public String toString() {
        return nombre;
    }
}
