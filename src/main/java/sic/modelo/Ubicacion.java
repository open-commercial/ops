package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "idUbicacion")
public class Ubicacion implements Serializable {

    private long idUbicacion;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private String calle;
    private Integer numero;
    private Integer piso;
    private String departamento;
    private Long idLocalidad;
    private String nombreLocalidad;
    private Long idProvincia;
    private String nombreProvincia;
    private String codigoPostal;

}