package sic.vista.swing;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.EmpresaActiva;
import sic.modelo.FormaDePago;
import sic.modelo.Rol;
import sic.modelo.UsuarioActivo;

public class FormasDePagoGUI extends JInternalFrame {

    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private List<FormaDePago> formasDePago;
    private final Dimension sizeInternalFrame =  new Dimension(600, 400);
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public FormasDePagoGUI() {
        this.initComponents();
    }

    private void setColumnas() {        
        String[] encabezados = new String[3];
        encabezados[0] = "Predeterminado";
        encabezados[1] = "Nombre";
        encabezados[2] = "Afecta la Caja";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_FormasDePago.setModel(modeloTablaResultados);        
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = Boolean.class;
        tipos[1] = String.class;
        tipos[2] = Boolean.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_FormasDePago.getTableHeader().setReorderingAllowed(false);
        tbl_FormasDePago.getTableHeader().setResizingAllowed(true);
        tbl_FormasDePago.getColumnModel().getColumn(0).setPreferredWidth(130);
        tbl_FormasDePago.getColumnModel().getColumn(1).setPreferredWidth(340);
        tbl_FormasDePago.getColumnModel().getColumn(2).setPreferredWidth(130);
    }

    private void cargarFormasDePago() {
        formasDePago = new ArrayList(Arrays.asList(RestClient.getRestTemplate().getForObject("/formas-de-pago/empresas/"
                + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(), FormaDePago[].class)));
        this.limpiarJTable();
        formasDePago.stream().map(fdp -> {
            Object[] fila = new Object[3];
            fila[0] = fdp.isPredeterminado();
            fila[1] = fdp.getNombre();
            fila[2] = fdp.isAfectaCaja();
            return fila;
        }).forEach(fila -> {
            modeloTablaResultados.addRow(fila);
        });
        tbl_FormasDePago.setModel(modeloTablaResultados);
    }

    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_FormasDePago.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void agregarFormaDePago() {
        try {
            FormaDePago formaDePago = new FormaDePago();
            formaDePago.setNombre(txt_Nombre.getText().trim());
            formaDePago.setAfectaCaja(chk_AfectaCaja.isSelected());
            RestClient.getRestTemplate().postForObject("/formas-de-pago?idEmpresa="
                    + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                    formaDePago,
                    FormaDePago.class);
            txt_Nombre.setText("");
            chk_AfectaCaja.setSelected(false);
            this.cargarFormasDePago();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarFormaDePago() {
        if (tbl_FormasDePago.getSelectedRow() != -1) {
            int filaSeleccionada = tbl_FormasDePago.getSelectedRow();
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar la forma de pago seleccionada?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    RestClient.getRestTemplate().delete("/formas-de-pago/" + formasDePago.get(filaSeleccionada).getId_FormaDePago());
                    formasDePago.remove(formasDePago.get(filaSeleccionada));                    
                    this.cargarFormasDePago();
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
    }

    private void setPredeterminado() {
        if (tbl_FormasDePago.getSelectedRow() != -1) {
            int filaSeleccionada = tbl_FormasDePago.getSelectedRow();
            try {
                if (RestClient.getRestTemplate().getForObject("/formas-de-pago/"
                        + formasDePago.get(filaSeleccionada).getId_FormaDePago(),
                        FormaDePago.class) != null) {
                    RestClient.getRestTemplate()
                            .put("/formas-de-pago/predeterminada/" + formasDePago.get(filaSeleccionada).getId_FormaDePago(),
                                    formasDePago.get(filaSeleccionada).getId_FormaDePago());                    
                    this.cargarFormasDePago();
                } else {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_forma_de_pago_seleccionada_no_existente"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
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

    private void verificarExistenciaPredeterminado() {
        try {
            RestClient.getRestTemplate().getForObject("/formas-de-pago/predeterminada/empresas/"
                    + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(), FormaDePago.class);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new javax.swing.JPanel();
        lbl_Nombre = new javax.swing.JLabel();
        txt_Nombre = new javax.swing.JTextField();
        lbl_AfectaCaja = new javax.swing.JLabel();
        chk_AfectaCaja = new javax.swing.JCheckBox();
        sp_FormasDePago = new javax.swing.JScrollPane();
        tbl_FormasDePago = new javax.swing.JTable();
        btn_Agregar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        btn_SetPredeterminado = new javax.swing.JButton();

        setClosable(true);
        setTitle("Administrar Formas de Pago");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Wallet_16x16.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Nombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nombre.setText("Nombre:");

        lbl_AfectaCaja.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_AfectaCaja.setText("Afecta la Caja:");

        chk_AfectaCaja.setText(" (se utilizará para el conteo del cierre de Caja)");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_AfectaCaja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Nombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_Nombre)
                    .addComponent(chk_AfectaCaja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Nombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_AfectaCaja)
                    .addComponent(chk_AfectaCaja))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbl_FormasDePago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        sp_FormasDePago.setViewportView(tbl_FormasDePago);

        btn_Agregar.setForeground(java.awt.Color.blue);
        btn_Agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddWallet_16x16.png"))); // NOI18N
        btn_Agregar.setText("Agregar");
        btn_Agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteWallet_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        btn_SetPredeterminado.setForeground(java.awt.Color.blue);
        btn_SetPredeterminado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/WalletArrow_16x16.png"))); // NOI18N
        btn_SetPredeterminado.setText("Marcar Predeterminado");
        btn_SetPredeterminado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SetPredeterminadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sp_FormasDePago)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_Agregar)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Eliminar)
                        .addGap(0, 0, 0)
                        .addComponent(btn_SetPredeterminado)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Agregar, btn_Eliminar, btn_SetPredeterminado});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sp_FormasDePago, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Agregar)
                    .addComponent(btn_Eliminar)
                    .addComponent(btn_SetPredeterminado))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Agregar, btn_Eliminar, btn_SetPredeterminado});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_AgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarActionPerformed
        this.agregarFormaDePago();
    }//GEN-LAST:event_btn_AgregarActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        this.eliminarFormaDePago();
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void btn_SetPredeterminadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SetPredeterminadoActionPerformed
        this.setPredeterminado();
    }//GEN-LAST:event_btn_SetPredeterminadoActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setSize(sizeInternalFrame);
            this.setColumnas();            
            this.cargarFormasDePago();
            this.verificarExistenciaPredeterminado();
            if (!UsuarioActivo.getInstance().getUsuario().getRoles().contains(Rol.ADMINISTRADOR)) {
                btn_Eliminar.setEnabled(false);
            }
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
    }//GEN-LAST:event_formInternalFrameOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Agregar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_SetPredeterminado;
    private javax.swing.JCheckBox chk_AfectaCaja;
    private javax.swing.JLabel lbl_AfectaCaja;
    private javax.swing.JLabel lbl_Nombre;
    private javax.swing.JPanel panel1;
    private javax.swing.JScrollPane sp_FormasDePago;
    private javax.swing.JTable tbl_FormasDePago;
    private javax.swing.JTextField txt_Nombre;
    // End of variables declaration//GEN-END:variables
}
