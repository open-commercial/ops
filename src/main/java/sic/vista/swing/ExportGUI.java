package sic.vista.swing;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.criteria.BusquedaCuentaCorrienteClienteCriteria;
import sic.modelo.criteria.BusquedaProductoCriteria;

public class ExportGUI extends JDialog {

    private BusquedaProductoCriteria criteriaProducto;
    private BusquedaCuentaCorrienteClienteCriteria criteriaCuentaCorrienteCliente;
    private boolean listaClientes; 
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    public ExportGUI(BusquedaProductoCriteria criteriaProducto) {
        this.initComponents();
        this.criteriaProducto = criteriaProducto;
    }
    
    public ExportGUI(BusquedaCuentaCorrienteClienteCriteria criteriaCuentaCorrienteCliente, boolean listaClientes) {
        this.initComponents();
        this.criteriaCuentaCorrienteCliente = criteriaCuentaCorrienteCliente;
        this.listaClientes = listaClientes;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelGeneral = new javax.swing.JPanel();
        btnExcel = new javax.swing.JButton();
        btnPDF = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Exportar");
        setResizable(false);

        panelGeneral.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Excel_64x64.png"))); // NOI18N
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });

        btnPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Pdf_64x64.png"))); // NOI18N
        btnPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPDFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelGeneralLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnExcel, btnPDF});

        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnExcel, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(btnPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        try {
            if (this.criteriaCuentaCorrienteCliente != null) {
                if (listaClientes) {
                    byte[] reporte = RestClient.getRestTemplate().postForObject("/cuentas-corriente/lista-clientes/reporte/criteria?formato=xlsx", this.criteriaCuentaCorrienteCliente, byte[].class);
                    File f = new File(System.getProperty("user.home") + "/" + "CuentaCorriente.xlsx");
                    Files.write(f.toPath(), reporte);
                    Desktop.getDesktop().open(f);
                } else {
                    byte[] reporte = RestClient.getRestTemplate().postForObject("/cuentas-corriente/clientes/reporte/criteria?formato=xlsx", this.criteriaCuentaCorrienteCliente, byte[].class);
                    File f = new File(System.getProperty("user.home") + "/" + "CuentaCorriente.xlsx");
                    Files.write(f.toPath(), reporte);
                    Desktop.getDesktop().open(f);
                }
            }
//            if (this.criteriaCuentaCorrienteCliente != null && !listaClientes) {
//                byte[] reporte = RestClient.getRestTemplate().postForObject("/cuentas-corriente/clientes/reporte/criteria?formato=xlsx", this.criteriaCuentaCorrienteCliente, byte[].class);
//                File f = new File(System.getProperty("user.home") + "/" + "CuentaCorriente.xlsx");
//                Files.write(f.toPath(), reporte);
//                Desktop.getDesktop().open(f);
//            }
//            if (this.criteriaCuentaCorrienteCliente != null && listaClientes) {
//                byte[] reporte = RestClient.getRestTemplate().postForObject("/cuentas-corriente/lista-clientes/reporte/criteria", this.criteriaCuentaCorrienteCliente, byte[].class);
//                File f = new File(System.getProperty("user.home") + "/" + "CuentaCorriente.xlsx");
//                Files.write(f.toPath(), reporte);
//                Desktop.getDesktop().open(f);
//            }
            if (this.criteriaProducto != null) {
                byte[] reporte = RestClient.getRestTemplate().postForObject("/productos/reporte/criteria?formato=xlsx", this.criteriaProducto, byte[].class);
                File f = new File(System.getProperty("user.home") + "/" + "ListaPrecios.xlsx");
                Files.write(f.toPath(), reporte);
                Desktop.getDesktop().open(f);
            }
            this.dispose();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnExcelActionPerformed

    private void btnPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPDFActionPerformed
        try {
            if (this.criteriaCuentaCorrienteCliente != null) {
                if (listaClientes) {
                    byte[] reporte = RestClient.getRestTemplate().postForObject("/cuentas-corriente/lista-clientes/reporte/criteria?formato=pdf", this.criteriaCuentaCorrienteCliente, byte[].class);
                    File f = new File(System.getProperty("user.home") + "/" + "ListaClientes.pdf");
                    Files.write(f.toPath(), reporte);
                    Desktop.getDesktop().open(f);
                } else {
                    byte[] reporte = RestClient.getRestTemplate().postForObject("/cuentas-corriente/clientes/reporte/criteria?formato=pdf", this.criteriaCuentaCorrienteCliente, byte[].class);
                    File f = new File(System.getProperty("user.home") + "/" + "CuentaCorriente.pdf");
                    Files.write(f.toPath(), reporte);
                    Desktop.getDesktop().open(f);
                }
            }
            if (this.criteriaProducto != null) {
                byte[] reporte = RestClient.getRestTemplate().postForObject("/productos/reporte/criteria?formato=pdf", this.criteriaProducto, byte[].class);
                File f = new File(System.getProperty("user.home") + "/" + "ListaPrecios.pdf");
                Files.write(f.toPath(), reporte);
                Desktop.getDesktop().open(f);
            }
            this.dispose();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnPDFActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnPDF;
    private javax.swing.JPanel panelGeneral;
    // End of variables declaration//GEN-END:variables
}
