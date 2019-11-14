package sic.modelo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"username", "email"})
public class Usuario implements Serializable {
    
    private long idUsuario;    
    private String username;    
    private String password;    
    private String nombre;   
    private String apellido;   
    private String email;       
    private long idSucursalPredeterminada;    
    private List<Rol> roles;
    private boolean habilitado;        

    @Override
    public String toString() {
        return nombre + " " + apellido + " (" + username + ")";
    }
}