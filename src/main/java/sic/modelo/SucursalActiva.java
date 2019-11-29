package sic.modelo;

public class SucursalActiva {

    private static final SucursalActiva INSTANCE = new SucursalActiva();
    private Sucursal sucursal;

    private SucursalActiva() {}
    
    //Singleton
    public static SucursalActiva getInstance() {
        return INSTANCE;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }   

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }    
}
