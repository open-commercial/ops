package sic.modelo;

public class EmpresaActiva {

    private static final EmpresaActiva INSTANCE = new EmpresaActiva();
    private Empresa empresa;

    private EmpresaActiva() {}
    
    //Singleton
    public static EmpresaActiva getInstance() {
        return INSTANCE;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }   

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }    
}
