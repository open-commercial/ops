package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"razonSocial", "idFiscal", "empresa"})
public class Cliente implements Serializable {

    private long id_Cliente;
    private String razonSocial;
    private String nombreFantasia;
    private String direccion;
    private CondicionIVA condicionIVA;
    private String idFiscal;
    private String email;
    private String telPrimario;
    private String telSecundario;
    private Localidad localidad;
    private String contacto;
    private Date fechaAlta;
    private Empresa empresa;
    private Usuario viajante;
    private Usuario credencial;
    private boolean eliminado;
    private boolean predeterminado;
    private Double saldoCuentaCorriente;

    @Override
    public String toString() {
        return razonSocial;
    }
}
