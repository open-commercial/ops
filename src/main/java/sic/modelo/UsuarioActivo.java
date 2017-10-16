package sic.modelo;

public class UsuarioActivo {

    private static final UsuarioActivo INSTANCE = new UsuarioActivo();
    private Usuario usuario;
    private String token;
    
    private UsuarioActivo() {}

    //Singleton
    public static UsuarioActivo getInstance() {
        return INSTANCE;
    }        

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }    

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        throw new CloneNotSupportedException();
    }
}
