package sic.modelo;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"idTraspaso"})
public class Traspaso {

    private Long idTraspaso;
    private LocalDateTime fechaDeAlta;
    private String nroTraspaso;
    private Long idSucursalOrigen;
    private String nombreSucursalOrigen;
    private Long idSucursalDestino;
    private String nombreSucursalDestino;
    private String nombreUsuario;

}
