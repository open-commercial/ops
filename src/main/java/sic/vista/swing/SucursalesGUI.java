package sic.vista.swing;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultListModel;
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

public class SucursalesGUI extends JDialog {

    private Sucursal sucursalSeleccionada;
    private List<Sucursal> sucursales;
    private final DefaultListModel modeloListSucursales = new DefaultListModel();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public SucursalesGUI() {
        this.initComponents();
        this.setIcon();
    }
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(SeleccionSucursalGUI.class.getResource("/sic/icons/Sucursal_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarListaSucursales() {
        modeloListSucursales.removeAllElements();                
        try {
            sucursales = Arrays.asList(RestClient.getRestTemplate().getForObject("/sucursales", Sucursal[].class));
            sucursales.stream().forEach(e -> {
                modeloListSucursales.addElement(e);
            });
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
        lst_Sucursales.setModel(modeloListSucursales);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        sp_Sucursales = new javax.swing.JScrollPane();
        lst_Sucursales = new javax.swing.JList();
        btn_NuevaSucursal = new javax.swing.JButton();
        btn_ModificarSucursal = new javax.swing.JButton();
        btn_EliminarSucursal = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administrar Sucursales");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lst_Sucursales.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_Sucursales.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lst_SucursalesValueChanged(evt);
            }
        });
        sp_Sucursales.setViewportView(lst_Sucursales);

        btn_NuevaSucursal.setForeground(java.awt.Color.blue);
        btn_NuevaSucursal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddSucursal_16x16.png"))); // NOI18N
        btn_NuevaSucursal.setText("Nueva");
        btn_NuevaSucursal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaSucursalActionPerformed(evt);
            }
        });

        btn_ModificarSucursal.setForeground(java.awt.Color.blue);
        btn_ModificarSucursal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditSucursal_16x16.png"))); // NOI18N
        btn_ModificarSucursal.setText("Modificar");
        btn_ModificarSucursal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ModificarSucursalActionPerformed(evt);
            }
        });

        btn_EliminarSucursal.setForeground(java.awt.Color.blue);
        btn_EliminarSucursal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/RemoveSucursal_16x16.png"))); // NOI18N
        btn_EliminarSucursal.setText("Eliminar");
        btn_EliminarSucursal.setEnabled(false);
        btn_EliminarSucursal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarSucursalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(sp_Sucursales)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(btn_NuevaSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_ModificarSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_EliminarSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sp_Sucursales, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_NuevaSucursal)
                    .addComponent(btn_ModificarSucursal)
                    .addComponent(btn_EliminarSucursal))
                .addContainerGap())
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_EliminarSucursal, btn_ModificarSucursal, btn_NuevaSucursal});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ModificarSucursalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ModificarSucursalActionPerformed
        if (sucursalSeleccionada != null) {
            DetalleSucursalGUI gui_DetalleSucursal = new DetalleSucursalGUI(sucursalSeleccionada);
            gui_DetalleSucursal.setModal(true);
            gui_DetalleSucursal.setLocationRelativeTo(this);
            gui_DetalleSucursal.setVisible(true);
            this.cargarListaSucursales();
        }
}//GEN-LAST:event_btn_ModificarSucursalActionPerformed

    private void lst_SucursalesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lst_SucursalesValueChanged
        sucursalSeleccionada = (Sucursal) lst_Sucursales.getSelectedValue();
    }//GEN-LAST:event_lst_SucursalesValueChanged

    private void btn_NuevaSucursalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaSucursalActionPerformed
            DetalleSucursalGUI gui_DetalleSucursal = new DetalleSucursalGUI();
            gui_DetalleSucursal.setModal(true);
            gui_DetalleSucursal.setLocationRelativeTo(this);
            gui_DetalleSucursal.setVisible(true);
            this.cargarListaSucursales();
    }//GEN-LAST:event_btn_NuevaSucursalActionPerformed

    private void btn_EliminarSucursalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarSucursalActionPerformed
        if (sucursalSeleccionada != null) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar la sucursal: "
                    + sucursalSeleccionada.getNombre() + "?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    RestClient.getRestTemplate().delete("/sucursales/" + sucursalSeleccionada.getIdSucursal());
                    LOGGER.warn("Sucursal " + sucursalSeleccionada.getNombre() + " eliminada.");   
                    if (sucursalSeleccionada.equals(SucursalActiva.getInstance().getSucursal())) {
                        SucursalActiva.getInstance().setSucursal(null);
                    }
                    this.cargarListaSucursales();
                    sucursalSeleccionada = null;
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }               
            }
        }
    }//GEN-LAST:event_btn_EliminarSucursalActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.cargarListaSucursales();
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_EliminarSucursal;
    private javax.swing.JButton btn_ModificarSucursal;
    private javax.swing.JButton btn_NuevaSucursal;
    private javax.swing.JList lst_Sucursales;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JScrollPane sp_Sucursales;
    // End of variables declaration//GEN-END:variables
}
