package sic.vista.swing;

import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Credencial;
import sic.modelo.Rol;
import sic.modelo.Usuario;
import sic.modelo.UsuarioActivo;

public class LoginGUI extends JFrame {    
    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    public LoginGUI() {
        this.initComponents();                
        ImageIcon iconoVentana = new ImageIcon(LoginGUI.class.getResource("/sic/icons/SIC_24_square.png"));        
        this.setIconImage(iconoVentana.getImage());
    }
    
    private void validarUsuario() {
        if (!txt_Usuario.getText().trim().equals("") || txt_Contrasenia.getPassword().length != 0) {
            try {
                Credencial credencial = new Credencial(txt_Usuario.getText().trim(), new String(txt_Contrasenia.getPassword()));
                UsuarioActivo.getInstance().setToken(RestClient.getRestTemplate().postForObject("/login", credencial, String.class));
                Usuario usuario = RestClient.getRestTemplate().getForObject("/usuarios/busqueda?nombre=" + credencial.getUsername(),
                        Usuario.class);
                UsuarioActivo.getInstance().setUsuario(usuario);
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_login_usuario_contrasenia_vacios"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ingresar() {
        if (UsuarioActivo.getInstance().getUsuario() != null) {
            if (UsuarioActivo.getInstance().getUsuario().getRoles().contains(Rol.ADMINISTRADOR)) {
                new PrincipalGUI().setVisible(true);
                this.dispose();
            } else {
                PuntoDeVentaGUI gui_puntoDeVenta = new PuntoDeVentaGUI();
                gui_puntoDeVenta.setModal(true);
                gui_puntoDeVenta.setVisible(true);
                this.limpiarCredenciales();
                txt_Usuario.requestFocus();
            }
        }
    }

    private void limpiarCredenciales() {        
        UsuarioActivo.getInstance().setUsuario(null);
        this.txt_Usuario.setText("");
        this.txt_Contrasenia.setText("");
    }

    private void capturaTeclaEnter(KeyEvent evt) {
        //tecla ENTER
        if (evt.getKeyCode() == 10) {
            btn_IngresarActionPerformed(null);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblUsuario = new javax.swing.JLabel();
        lblContrasenia = new javax.swing.JLabel();
        txt_Contrasenia = new javax.swing.JPasswordField();
        txt_Usuario = new javax.swing.JTextField();
        pb_Conectando = new javax.swing.JProgressBar();
        btn_Ingresar = new javax.swing.JButton();
        btn_Salir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("S.I.C.");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lblUsuario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUsuario.setText("Usuario:");

        lblContrasenia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblContrasenia.setText("Contraseña:");

        txt_Contrasenia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_ContraseniaKeyPressed(evt);
            }
        });

        txt_Usuario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_UsuarioKeyPressed(evt);
            }
        });

        pb_Conectando.setString("");
        pb_Conectando.setStringPainted(true);

        btn_Ingresar.setForeground(java.awt.Color.blue);
        btn_Ingresar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/LogIn_16x16.png"))); // NOI18N
        btn_Ingresar.setText("Ingresar");
        btn_Ingresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_IngresarActionPerformed(evt);
            }
        });

        btn_Salir.setForeground(java.awt.Color.blue);
        btn_Salir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/LogOut_16x16.png"))); // NOI18N
        btn_Salir.setText("Salir");
        btn_Salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pb_Conectando, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblContrasenia))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_Usuario)
                            .addComponent(txt_Contrasenia)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_Ingresar, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Salir, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(lblUsuario)
                    .addComponent(txt_Usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(lblContrasenia)
                    .addComponent(txt_Contrasenia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pb_Conectando, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_Ingresar)
                    .addComponent(btn_Salir))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_SalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btn_SalirActionPerformed

    private void btn_IngresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_IngresarActionPerformed
        Thread hiloLogIn = new Thread(() -> {
            pb_Conectando.setIndeterminate(true);
            pb_Conectando.setString("Conectando...");
            btn_Ingresar.setEnabled(false);
            btn_Salir.setEnabled(false);
            txt_Usuario.setEnabled(false);
            txt_Contrasenia.setEnabled(false);
            pb_Conectando.requestFocus();
            validarUsuario();
            pb_Conectando.setIndeterminate(false);
            pb_Conectando.setString("");
            btn_Ingresar.setEnabled(true);
            btn_Salir.setEnabled(true);
            txt_Usuario.setEnabled(true);
            txt_Contrasenia.setEnabled(true);
            
            SwingUtilities.invokeLater(() -> {
                ingresar();
            });
        });
        hiloLogIn.start();
    }//GEN-LAST:event_btn_IngresarActionPerformed

    private void txt_ContraseniaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ContraseniaKeyPressed
        this.capturaTeclaEnter(evt);
    }//GEN-LAST:event_txt_ContraseniaKeyPressed

    private void txt_UsuarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_UsuarioKeyPressed
        this.capturaTeclaEnter(evt);
    }//GEN-LAST:event_txt_UsuarioKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setTitle("S.I.C. " + ResourceBundle.getBundle("Mensajes").getString("version"));
        this.setLocationRelativeTo(null);        
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Ingresar;
    private javax.swing.JButton btn_Salir;
    private javax.swing.JLabel lblContrasenia;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JProgressBar pb_Conectando;
    private javax.swing.JPasswordField txt_Contrasenia;
    private javax.swing.JTextField txt_Usuario;
    // End of variables declaration//GEN-END:variables
}
