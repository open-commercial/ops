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
import sic.modelo.Empresa;
import sic.modelo.EmpresaActiva;

public class EmpresasGUI extends JDialog {

    private Empresa empresaSeleccionada;
    private List<Empresa> empresas;
    private final DefaultListModel modeloListEmpresas = new DefaultListModel();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public EmpresasGUI() {
        this.initComponents();
        ImageIcon iconoVentana = new ImageIcon(EmpresasGUI.class.getResource("/sic/icons/Empresa_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarListEmpresas() {
        modeloListEmpresas.removeAllElements();                
        try {
            empresas = Arrays.asList(RestClient.getRestTemplate().getForObject("/empresas", Empresa[].class));
            empresas.stream().forEach((e) -> {
                modeloListEmpresas.addElement(e);
            });
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        lst_Empresas.setModel(modeloListEmpresas);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        sp_Empresas = new javax.swing.JScrollPane();
        lst_Empresas = new javax.swing.JList();
        btn_NuevaEmpresa = new javax.swing.JButton();
        btn_ModificarEmpresa = new javax.swing.JButton();
        btn_EliminarEmpresa = new javax.swing.JButton();
        lbl_MisEmpresas = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administrar Empresas");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lst_Empresas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_Empresas.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lst_EmpresasValueChanged(evt);
            }
        });
        sp_Empresas.setViewportView(lst_Empresas);

        btn_NuevaEmpresa.setForeground(java.awt.Color.blue);
        btn_NuevaEmpresa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddEmpresa_16x16.png"))); // NOI18N
        btn_NuevaEmpresa.setText("Nueva");
        btn_NuevaEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaEmpresaActionPerformed(evt);
            }
        });

        btn_ModificarEmpresa.setForeground(java.awt.Color.blue);
        btn_ModificarEmpresa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditEmpresa_16x16.png"))); // NOI18N
        btn_ModificarEmpresa.setText("Modificar");
        btn_ModificarEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ModificarEmpresaActionPerformed(evt);
            }
        });

        btn_EliminarEmpresa.setForeground(java.awt.Color.blue);
        btn_EliminarEmpresa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/RemoveEmpresa_16x16.png"))); // NOI18N
        btn_EliminarEmpresa.setText("Eliminar");
        btn_EliminarEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarEmpresaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(sp_Empresas)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(btn_NuevaEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_ModificarEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_EliminarEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sp_Empresas, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_NuevaEmpresa)
                    .addComponent(btn_ModificarEmpresa)
                    .addComponent(btn_EliminarEmpresa))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_EliminarEmpresa, btn_ModificarEmpresa, btn_NuevaEmpresa});

        lbl_MisEmpresas.setText("Empresas:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_MisEmpresas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_MisEmpresas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ModificarEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ModificarEmpresaActionPerformed
        if (empresaSeleccionada != null) {
            DetalleEmpresaGUI gui_DetalleEmpresa = new DetalleEmpresaGUI(empresaSeleccionada);
            gui_DetalleEmpresa.setModal(true);
            gui_DetalleEmpresa.setLocationRelativeTo(this);
            gui_DetalleEmpresa.setVisible(true);
            this.cargarListEmpresas();
        }
}//GEN-LAST:event_btn_ModificarEmpresaActionPerformed

    private void lst_EmpresasValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lst_EmpresasValueChanged
        empresaSeleccionada = (Empresa) lst_Empresas.getSelectedValue();
    }//GEN-LAST:event_lst_EmpresasValueChanged

    private void btn_NuevaEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaEmpresaActionPerformed
            DetalleEmpresaGUI gui_DetalleEmpresa = new DetalleEmpresaGUI();
            gui_DetalleEmpresa.setModal(true);
            gui_DetalleEmpresa.setLocationRelativeTo(this);
            gui_DetalleEmpresa.setVisible(true);
            this.cargarListEmpresas();
    }//GEN-LAST:event_btn_NuevaEmpresaActionPerformed

    private void btn_EliminarEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarEmpresaActionPerformed
        if (empresaSeleccionada != null) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar la empresa: "
                    + empresaSeleccionada.getNombre() + "?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    RestClient.getRestTemplate().delete("/empresas/" + empresaSeleccionada.getId_Empresa());                    
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                LOGGER.warn("Empresa " + empresaSeleccionada.getNombre() + " eliminada.");
                //actualiza la empresa en caso de que sea la seleccionada
                if (empresaSeleccionada.equals(EmpresaActiva.getInstance().getEmpresa())) {                    
                    EmpresaActiva.getInstance().setEmpresa(null);
                }
                this.cargarListEmpresas();
                empresaSeleccionada = null;
            }
        }
    }//GEN-LAST:event_btn_EliminarEmpresaActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.cargarListEmpresas();
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_EliminarEmpresa;
    private javax.swing.JButton btn_ModificarEmpresa;
    private javax.swing.JButton btn_NuevaEmpresa;
    private javax.swing.JLabel lbl_MisEmpresas;
    private javax.swing.JList lst_Empresas;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JScrollPane sp_Empresas;
    // End of variables declaration//GEN-END:variables
}
