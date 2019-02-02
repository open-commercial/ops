package sic.vista.swing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Rol;
import sic.modelo.Usuario;
import sic.modelo.TipoDeOperacion;

public class DetalleUsuarioGUI extends JDialog {
    
    private Usuario usuarioParaModificar;
    private Usuario usuarioCreado;
    private Rol rolDeUsuarioACrear;
    private final TipoDeOperacion operacion;    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleUsuarioGUI() {
        this.initComponents();
        operacion = TipoDeOperacion.ALTA;
        this.setIcon();
        lblAvisoSeguridad.setText("");
    }

    public DetalleUsuarioGUI(Rol rolDeUsuarioACrear) {
        this.initComponents();
        operacion = TipoDeOperacion.ALTA;
        this.rolDeUsuarioACrear = rolDeUsuarioACrear;
        this.setIcon();
        lblAvisoSeguridad.setText("");
    }

    public DetalleUsuarioGUI(Usuario usuario) {
        this.initComponents();
        this.usuarioParaModificar = usuario;
        operacion = TipoDeOperacion.ACTUALIZACION;        
        this.setIcon();
        lbl_Contrasenia.setForeground(Color.BLACK);
        lbl_RepetirContrasenia.setForeground(Color.BLACK);
    }    

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleUsuarioGUI.class.getResource("/sic/icons/Group_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }
    
    private void cargarUsuarioParaModificar() {
        txtNombre.setText(usuarioParaModificar.getNombre());
        txtApellido.setText(usuarioParaModificar.getApellido());
        txtEmail.setText(usuarioParaModificar.getEmail());
        txtUsername.setText(usuarioParaModificar.getUsername());
        chkHabilitado.setSelected(usuarioParaModificar.isHabilitado());
        usuarioParaModificar.getRoles().forEach(rol -> {
            if (Rol.ADMINISTRADOR.equals(rol)) {
                chk_Administrador.setSelected(true);
            }
            if (Rol.ENCARGADO.equals(rol)) {
                chk_Encargado.setSelected(true);
            }
            if (Rol.VENDEDOR.equals(rol)) {
                chk_Vendedor.setSelected(true);
            }
            if (Rol.VIAJANTE.equals(rol)) {
                chk_Viajante.setSelected(true);
            }
            if (Rol.COMPRADOR.equals(rol)) {
                chk_Comprador.setSelected(true);
            }
        });
    }

    public Usuario getUsuarioCreado() {
        return usuarioCreado;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_Guardar = new javax.swing.JButton();
        panelPrincipal = new javax.swing.JPanel();
        lbl_Username = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        lblApellido = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        chkHabilitado = new javax.swing.JCheckBox();
        lblHabilitado = new javax.swing.JLabel();
        separador1 = new javax.swing.JSeparator();
        lblAvisoSeguridad = new javax.swing.JLabel();
        lbl_Contrasenia = new javax.swing.JLabel();
        txt_Contrasenia = new javax.swing.JPasswordField();
        lbl_RepetirContrasenia = new javax.swing.JLabel();
        txt_RepetirContrasenia = new javax.swing.JPasswordField();
        separador2 = new javax.swing.JSeparator();
        chk_Administrador = new javax.swing.JCheckBox();
        chk_Encargado = new javax.swing.JCheckBox();
        chk_Vendedor = new javax.swing.JCheckBox();
        chk_Viajante = new javax.swing.JCheckBox();
        chk_Comprador = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        panelPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_Username.setForeground(java.awt.Color.red);
        lbl_Username.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Username.setText("* Usuario:");

        lblNombre.setForeground(java.awt.Color.red);
        lblNombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNombre.setText("* Nombre:");

        lblApellido.setForeground(java.awt.Color.red);
        lblApellido.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblApellido.setText("* Apellido:");

        lblEmail.setForeground(java.awt.Color.red);
        lblEmail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEmail.setText("* Email:");

        chkHabilitado.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        lblHabilitado.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblHabilitado.setText("Habilitado:");

        lblAvisoSeguridad.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAvisoSeguridad.setText("(Dejar en blanco para mantener la actual)");

        lbl_Contrasenia.setForeground(java.awt.Color.red);
        lbl_Contrasenia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Contrasenia.setText("* Contraseña:");

        txt_Contrasenia.setPreferredSize(new java.awt.Dimension(125, 20));

        lbl_RepetirContrasenia.setForeground(java.awt.Color.red);
        lbl_RepetirContrasenia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_RepetirContrasenia.setText("* Repetir:");

        txt_RepetirContrasenia.setPreferredSize(new java.awt.Dimension(125, 20));

        chk_Administrador.setText("Administrador");

        chk_Encargado.setText("Encargado");

        chk_Vendedor.setText("Vendedor");

        chk_Viajante.setText("Viajante");

        chk_Comprador.setText("Comprador");

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lbl_Username, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblApellido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblHabilitado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtApellido, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                            .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                            .addComponent(txtUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                            .addComponent(chkHabilitado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(separador1)
                    .addComponent(lblAvisoSeguridad, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lbl_RepetirContrasenia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_Contrasenia))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_Contrasenia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_RepetirContrasenia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(separador2)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(chk_Comprador, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_Administrador, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_Vendedor, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chk_Viajante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_Encargado))))
                .addContainerGap())
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblApellido, lblEmail, lblHabilitado, lblNombre, lbl_Contrasenia, lbl_RepetirContrasenia, lbl_Username});

        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblHabilitado)
                    .addComponent(chkHabilitado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Username)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblApellido)
                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(separador1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblAvisoSeguridad)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Contrasenia)
                    .addComponent(txt_Contrasenia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_RepetirContrasenia)
                    .addComponent(txt_RepetirContrasenia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addComponent(separador2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Administrador)
                    .addComponent(chk_Encargado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Vendedor)
                    .addComponent(chk_Viajante))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chk_Comprador)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_Guardar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        try {
            if (operacion == TipoDeOperacion.ALTA) {
                if (new String(txt_Contrasenia.getPassword()).equals(new String(txt_RepetirContrasenia.getPassword()))) {
                    Usuario usuario = new Usuario();
                    usuario.setNombre(txtNombre.getText().trim());
                    usuario.setApellido(txtApellido.getText().trim());
                    usuario.setEmail(txtEmail.getText().trim());
                    usuario.setUsername(txtUsername.getText().trim());
                    usuario.setPassword(new String(txt_Contrasenia.getPassword()));
                    usuario.setHabilitado(chkHabilitado.isSelected());
                    List<Rol> roles = new ArrayList<>();
                    if (chk_Administrador.isSelected()) roles.add(Rol.ADMINISTRADOR);
                    if (chk_Encargado.isSelected()) roles.add(Rol.ENCARGADO);
                    if (chk_Vendedor.isSelected()) roles.add(Rol.VENDEDOR);
                    if (chk_Viajante.isSelected()) roles.add(Rol.VIAJANTE);
                    if (chk_Comprador.isSelected()) roles.add(Rol.COMPRADOR);
                    usuario.setRoles(roles);
                    usuarioCreado = RestClient.getRestTemplate().postForObject("/usuarios", usuario, Usuario.class);                                     
                    this.dispose();
                } else {                    
                    JOptionPane.showMessageDialog(this, 
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_password_diferentes"),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (operacion == TipoDeOperacion.ACTUALIZACION) {
                if (new String(txt_Contrasenia.getPassword()).equals(new String(txt_RepetirContrasenia.getPassword()))) {                    
                    usuarioParaModificar.setNombre(txtNombre.getText().trim());
                    usuarioParaModificar.setApellido(txtApellido.getText().trim());
                    usuarioParaModificar.setEmail(txtEmail.getText().trim());
                    usuarioParaModificar.setUsername(txtUsername.getText().trim());
                    usuarioParaModificar.setHabilitado(chkHabilitado.isSelected());
                    usuarioParaModificar.setPassword(new String(txt_Contrasenia.getPassword()));                    
                    List<Rol> roles = new ArrayList<>();
                    if (chk_Administrador.isSelected()) roles.add(Rol.ADMINISTRADOR);                  
                    if (chk_Encargado.isSelected()) roles.add(Rol.ENCARGADO);
                    if (chk_Vendedor.isSelected()) roles.add(Rol.VENDEDOR);
                    if (chk_Viajante.isSelected()) roles.add(Rol.VIAJANTE);
                    if (chk_Comprador.isSelected()) roles.add(Rol.COMPRADOR);                   
                    boolean debeActualizar = false;
                    if (usuarioParaModificar.getRoles().contains(Rol.COMPRADOR) && !chk_Comprador.isSelected()) {
                        int reply = JOptionPane.showConfirmDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_quitar_rol_comprador"),
                                "Aviso", JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            roles.remove(Rol.COMPRADOR);
                            debeActualizar = true;
                        } else chk_Comprador.setSelected(true);
                    } else debeActualizar = true; 
                    if (usuarioParaModificar.getRoles().contains(Rol.VIAJANTE) && !chk_Viajante.isSelected()) {
                        int reply = JOptionPane.showConfirmDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_quitar_rol_viajante"),
                                "Aviso", JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            roles.remove(Rol.VIAJANTE);
                            debeActualizar = true;
                        } else {
                            chk_Viajante.setSelected(true);
                            debeActualizar = false;
                        }
                    }
                    if (debeActualizar) {
                        usuarioParaModificar.setRoles(roles);
                        RestClient.getRestTemplate().put("/usuarios", usuarioParaModificar);                        
                        JOptionPane.showMessageDialog(this, "El Usuario se modificó correctamente!",
                                "Aviso", JOptionPane.INFORMATION_MESSAGE);
                        this.dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_password_diferentes"),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }                
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_GuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.setTitle("Modificar Usuario");
            this.cargarUsuarioParaModificar();
        } else if (operacion == TipoDeOperacion.ALTA) {
            this.setTitle("Nuevo Usuario");
            if (rolDeUsuarioACrear != null) {
                if (rolDeUsuarioACrear.equals(Rol.COMPRADOR)) {
                    lblHabilitado.setEnabled(false);
                    chkHabilitado.setSelected(true);
                    chkHabilitado.setEnabled(false);
                    chk_Comprador.setSelected(true);
                    chk_Comprador.setEnabled(false);
                    chk_Administrador.setEnabled(false);
                    chk_Encargado.setEnabled(false);
                    chk_Vendedor.setEnabled(false);
                    chk_Viajante.setEnabled(false);
                }
                if (rolDeUsuarioACrear.equals(Rol.VIAJANTE)) {
                    lblHabilitado.setEnabled(false);
                    chkHabilitado.setSelected(true);
                    chkHabilitado.setEnabled(false);
                    chk_Viajante.setSelected(true);
                    chk_Viajante.setEnabled(false);
                    chk_Administrador.setEnabled(false);
                    chk_Encargado.setEnabled(false);
                    chk_Comprador.setEnabled(false);
                    chk_Vendedor.setEnabled(false);
                }
            }
        }
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JCheckBox chkHabilitado;
    private javax.swing.JCheckBox chk_Administrador;
    private javax.swing.JCheckBox chk_Comprador;
    private javax.swing.JCheckBox chk_Encargado;
    private javax.swing.JCheckBox chk_Vendedor;
    private javax.swing.JCheckBox chk_Viajante;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblAvisoSeguridad;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblHabilitado;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lbl_Contrasenia;
    private javax.swing.JLabel lbl_RepetirContrasenia;
    private javax.swing.JLabel lbl_Username;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JSeparator separador1;
    private javax.swing.JSeparator separador2;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JPasswordField txt_Contrasenia;
    private javax.swing.JPasswordField txt_RepetirContrasenia;
    // End of variables declaration//GEN-END:variables
}
