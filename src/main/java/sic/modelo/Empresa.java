package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre"})
public class Empresa implements Serializable {
    
    private long id_Empresa;    
    private String nombre;
    private String lema;
    private String direccion;
    private CondicionIVA condicionIVA;
    private long cuip;
    private long ingresosBrutos;
    private Date fechaInicioActividad;
    private String email;
    private String telefono;
    private Localidad localidad;
    private String logo;
    private boolean eliminada;

    @Override
    public String toString() {
        return nombre;
    }
}
