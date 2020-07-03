package sic.modelo.criteria;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusquedaTraspasoCriteria {

    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
    private String nroTraspaso;
    private Long idSucursalOrigen;
    private Long idSucursalDestino;
    private Long idUsuario;
    private Integer pagina;
    private String ordenarPor;
    private String sentido;
}
