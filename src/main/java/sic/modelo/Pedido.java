package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroPedido", "empresa"})
public class Pedido implements Serializable {
    
    private long id_Pedido;
    private long nroPedido;    
    private Date fecha;    
    private Date fechaVencimiento;    
    private String observaciones;    
    private Empresa empresa;
    private boolean eliminado;    
    private Cliente cliente;    
    private Usuario usuario;
    private List<Factura> facturas;        
    private List<RenglonPedido> renglones;
    private double totalEstimado;
    private double totalActual;
    private EstadoPedido estado;
}
