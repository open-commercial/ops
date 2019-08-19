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
import sic.modelo.SucursalActiva;
import sic.modelo.FormaDePago;

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
        formasDePago = new ArrayList(Arrays.asList(RestClient.getRestTemplate().getForObject("/formas-de-pago/sucursales/"
                + SucursalActiva.getInstance().getSucursal().getIdSucursal(), FormaDePago[].class)));
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sp_FormasDePago = new javax.swing.JScrollPane();
        tbl_FormasDePago = new javax.swing.JTable();
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

        tbl_FormasDePago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        sp_FormasDePago.setViewportView(tbl_FormasDePago);

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
                    .addComponent(sp_FormasDePago, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_SetPredeterminado)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sp_FormasDePago, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_SetPredeterminado)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setSize(sizeInternalFrame);
            this.setColumnas();            
            this.cargarFormasDePago();
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

    private void btn_SetPredeterminadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SetPredeterminadoActionPerformed
        this.setPredeterminado();
    }//GEN-LAST:event_btn_SetPredeterminadoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_SetPredeterminado;
    private javax.swing.JScrollPane sp_FormasDePago;
    private javax.swing.JTable tbl_FormasDePago;
    // End of variables declaration//GEN-END:variables
}
