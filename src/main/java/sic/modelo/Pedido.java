package sic.modelo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroPedido", "nombreSucursal"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idPedido", scope = Pedido.class)
public class Pedido implements Serializable {
    
    private long idPedido;
    private long nroPedido;    
    private LocalDateTime fecha;    
    private LocalDateTime fechaVencimiento;    
    private String observaciones;  
    private String nombreSucursal;
    private Long idSucursal;
    private boolean eliminado;    
    private Cliente cliente; 
    private String nombreUsuario;
    private Long idViajante;
    private String nombreViajante;
    private List<Factura> facturas;        
    private List<RenglonPedido> renglones;
    private BigDecimal subTotal;
    private BigDecimal recargoPorcentaje;
    private BigDecimal recargoNeto;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoNeto;
    private BigDecimal totalEstimado;
    private BigDecimal totalActual;
    private EstadoPedido estado;
}
