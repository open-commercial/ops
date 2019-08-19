package sic.vista.swing;

import java.util.Arrays;
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
import sic.modelo.Sucursal;
import sic.modelo.SucursalActiva;
import sic.modelo.Rol;
import sic.modelo.Usuario;
import sic.modelo.UsuarioActivo;

public class SeleccionSucursalGUI extends JDialog {

    private List<Sucursal> sucursales;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public SeleccionSucursalGUI() {
        this.initComponents();
        this.setIcon();        
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(SeleccionSucursalGUI.class.getResource("/sic/icons/Sucursal_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarComboBoxSucursales() {        
        cmb_Sucursales.removeAllItems();
        sucursales.stream().forEach(e -> {
            cmb_Sucursales.addItem(e.getNombre());
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_Leyenda = new javax.swing.JLabel();
        cmb_Sucursales = new javax.swing.JComboBox();
        btn_Aceptar = new javax.swing.JButton();
        lbl_Icon = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sucursales");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lbl_Leyenda.setFont(new java.awt.Font("Dialog", 1, 15)); // NOI18N
        lbl_Leyenda.setText("Seleccione la Sucursal con la que desea trabajar:");

        cmb_Sucursales.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmb_SucursalesKeyPressed(evt);
            }
        });

        btn_Aceptar.setForeground(java.awt.Color.blue);
        btn_Aceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/ArrowRight_16x16.png"))); // NOI18N
        btn_Aceptar.setText("Aceptar");
        btn_Aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AceptarActionPerformed(evt);
            }
        });

        lbl_Icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Sucursal_16x16.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_Icon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmb_Sucursales, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lbl_Leyenda)
                    .addComponent(btn_Aceptar, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_Leyenda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_Sucursales, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Icon, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_Aceptar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void btn_AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AceptarActionPerformed
        if (cmb_Sucursales.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una Sucursal para poder continuar!\nEn "
                    + "caso de que no encuentre ninguna, comuníquese con un "
                    + "Administrador del sistema",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            sucursales.stream()
                    .filter(e -> (e.getNombre().equals(cmb_Sucursales.getSelectedItem())))
                    .forEachOrdered(e -> {
                        SucursalActiva.getInstance().setSucursal(e);
                        try {
                            RestClient.getRestTemplate().put("/usuarios/" + UsuarioActivo.getInstance().getUsuario().getId_Usuario() + "/sucursales/" + e.getIdSucursal(), Usuario.class);
                        } catch (RestClientResponseException ex) {
                            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            this.dispose();
                        } catch (ResourceAccessException ex) {
                            LOGGER.error(ex.getMessage());
                            JOptionPane.showMessageDialog(this,
                                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
            this.dispose();
        }
    }//GEN-LAST:event_btn_AceptarActionPerformed

    private void cmb_SucursalesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmb_SucursalesKeyPressed
        if (evt.getKeyCode() == 10) {
            btn_AceptarActionPerformed(null);
        }
    }//GEN-LAST:event_cmb_SucursalesKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            sucursales = Arrays.asList(RestClient.getRestTemplate().getForObject("/sucursales", Sucursal[].class));
            if (sucursales.isEmpty() || sucursales.size() > 1) {
                if (sucursales.isEmpty() && UsuarioActivo.getInstance().getUsuario().getRoles().contains(Rol.ADMINISTRADOR)) {
                    SucursalesGUI sucursalesGUI = new SucursalesGUI();
                    sucursalesGUI.setModal(true);
                    sucursalesGUI.setLocationRelativeTo(this);
                    sucursalesGUI.setVisible(true);
                }
                sucursales = Arrays.asList(RestClient.getRestTemplate().getForObject("/sucursales", Sucursal[].class));
                this.cargarComboBoxSucursales();
            } else {
                SucursalActiva.getInstance().setSucursal(sucursales.get(0));
                RestClient.getRestTemplate().put("/usuarios/" + UsuarioActivo.getInstance().getUsuario().getId_Usuario() + "/sucursales/" + SucursalActiva.getInstance().getSucursal().getIdSucursal(), Usuario.class);
                this.dispose();
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Aceptar;
    private javax.swing.JComboBox cmb_Sucursales;
    private javax.swing.JLabel lbl_Icon;
    private javax.swing.JLabel lbl_Leyenda;
    // End of variables declaration//GEN-END:variables
}
