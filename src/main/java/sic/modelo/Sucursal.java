package sic.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"idSucursal"})
public class Sucursal implements Serializable {
    
    private long idSucursal;    
    private String nombre;
    private String lema;    
    private CategoriaIVA categoriaIVA;
    private Long idFiscal;
    private Long ingresosBrutos;
    private LocalDateTime fechaInicioActividad;
    private String email;
    private String telefono;
    private Ubicacion ubicacion;
    private String detalleUbicacion;
    private String logo;
    private boolean eliminada;

    @Override
    public String toString() {
        return nombre;
    }
}
