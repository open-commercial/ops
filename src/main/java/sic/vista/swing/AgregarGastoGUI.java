package sic.vista.swing;

import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Empresa;
import sic.modelo.EmpresaActiva;
import sic.modelo.FormaDePago;
import sic.modelo.Gasto;
import sic.modelo.UsuarioActivo;

public class AgregarGastoGUI extends JDialog {
    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final List<FormaDePago> formasDePago;
    
    public AgregarGastoGUI(List<FormaDePago> formasDePago) {
        this.setModal(true);
        this.formasDePago = formasDePago;
        this.initComponents();
        this.setIcon();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(PuntoDeVentaGUI.class.getResource("/sic/icons/CoinsAdd_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }
    
    public Gasto construirGasto(String concepto, double monto, FormaDePago formaDePago) {
        Empresa empresa = EmpresaActiva.getInstance().getEmpresa();        
        Gasto gasto = new Gasto();
        gasto.setConcepto(concepto);
        gasto.setEliminado(false);
        gasto.setEmpresa(empresa);
        gasto.setFecha(new Date());
        gasto.setFormaDePago(formaDePago);
        gasto.setMonto(monto);
        gasto.setUsuario(UsuarioActivo.getInstance().getUsuario());
        return gasto;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Parametros = new javax.swing.JPanel();
        lbl_FormaDePago = new javax.swing.JLabel();
        cmb_FormaDePago = new javax.swing.JComboBox<>();
        lbl_Concepto = new javax.swing.JLabel();
        ftxt_Concepto = new javax.swing.JTextField();
        lbl_Monto = new javax.swing.JLabel();
        ftxt_Monto = new javax.swing.JFormattedTextField();
        lbl_Aceptar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nuevo Gasto");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        pnl_Parametros.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_FormaDePago.setForeground(java.awt.Color.red);
        lbl_FormaDePago.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_FormaDePago.setText("* Forma de Pago:");

        lbl_Concepto.setForeground(java.awt.Color.red);
        lbl_Concepto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Concepto.setText("* Concepto:");

        lbl_Monto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Monto.setText("Monto:");

        ftxt_Monto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxt_Monto.setText("0");
        ftxt_Monto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxt_MontoFocusGained(evt);
            }
        });
        ftxt_Monto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxt_MontoKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout pnl_ParametrosLayout = new javax.swing.GroupLayout(pnl_Parametros);
        pnl_Parametros.setLayout(pnl_ParametrosLayout);
        pnl_ParametrosLayout.setHorizontalGroup(
            pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Monto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Concepto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_FormaDePago, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_FormaDePago, 0, 249, Short.MAX_VALUE)
                    .addComponent(ftxt_Concepto)
                    .addComponent(ftxt_Monto))
                .addContainerGap())
        );
        pnl_ParametrosLayout.setVerticalGroup(
            pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_FormaDePago)
                    .addComponent(cmb_FormaDePago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Concepto)
                    .addComponent(ftxt_Concepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Monto)
                    .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbl_Aceptar.setForeground(java.awt.Color.blue);
        lbl_Aceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        lbl_Aceptar.setText("Aceptar");
        lbl_Aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lbl_AceptarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_Aceptar)
                    .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Aceptar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setModal(true);
        formasDePago.forEach((formaDePago) -> {
            cmb_FormaDePago.addItem(formaDePago);
        });
    }//GEN-LAST:event_formWindowOpened

    private void lbl_AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lbl_AceptarActionPerformed
        if (ftxt_Monto.getValue() == null) {
            ftxt_Monto.setValue(0.00);
        }
        if (ftxt_Concepto.getText() == null) {
            ftxt_Concepto.setText("");
        }
        Gasto gasto = null;
        try {
            gasto = RestClient.getRestTemplate().postForObject("/gastos", this.construirGasto(ftxt_Concepto.getText(),
                    Double.parseDouble(ftxt_Monto.getValue().toString()),
                    (FormaDePago) cmb_FormaDePago.getSelectedItem()), Gasto.class);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (gasto != null) {
            this.dispose();
        }
    }//GEN-LAST:event_lbl_AceptarActionPerformed

    private void ftxt_MontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxt_MontoFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxt_Monto.selectAll();
        });
    }//GEN-LAST:event_ftxt_MontoFocusGained

    private void ftxt_MontoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxt_MontoKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxt_MontoKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<FormaDePago> cmb_FormaDePago;
    private javax.swing.JTextField ftxt_Concepto;
    private javax.swing.JFormattedTextField ftxt_Monto;
    private javax.swing.JButton lbl_Aceptar;
    private javax.swing.JLabel lbl_Concepto;
    private javax.swing.JLabel lbl_FormaDePago;
    private javax.swing.JLabel lbl_Monto;
    private javax.swing.JPanel pnl_Parametros;
    // End of variables declaration//GEN-END:variables
}
