package sic.vista.swing;

import java.util.Arrays;
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
import sic.modelo.EmpresaActiva;
import sic.modelo.Factura;
import sic.modelo.FormaDePago;
import sic.modelo.NotaDebito;
import sic.modelo.Pago;

public class DetallePagoGUI extends JDialog {

    private final Factura facturaRelacionada;
    private final NotaDebito notaDebitoRelacionada;
    private boolean pagoCreado;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetallePagoGUI(Factura factura) {
        this.initComponents();
        this.setIcon();
        facturaRelacionada = factura;  
        notaDebitoRelacionada = null;
        pagoCreado = false;
    }
    
    public DetallePagoGUI(NotaDebito notaDebito) {
        this.initComponents();
        this.setIcon();
        facturaRelacionada = null;     
        notaDebitoRelacionada = notaDebito;
        pagoCreado = false;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetallePagoGUI.class.getResource("/sic/icons/Stamp_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarFormasDePago() {
        try {
            List<FormaDePago> formasDePago = Arrays.asList(RestClient.getRestTemplate().getForObject("/formas-de-pago/empresas/"
                    + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(), FormaDePago[].class));
            formasDePago.stream().forEach((formaDePago) -> {
                cmb_FormaDePago.addItem(formaDePago);
            });
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarPago() {
        try {
            Pago pago = new Pago();
            pago.setMonto(Double.parseDouble(txt_Monto.getValue().toString()));
            pago.setNota(txt_Nota.getText().trim());
            pago.setFormaDePago((FormaDePago) cmb_FormaDePago.getSelectedItem());
            if (facturaRelacionada != null) {
                pago.setEmpresa(facturaRelacionada.getEmpresa());
                RestClient.getRestTemplate().postForObject("/pagos/facturas/" + facturaRelacionada.getId_Factura(),
                        pago, Pago.class);
            } else if (notaDebitoRelacionada != null) {
                pago.setEmpresa(notaDebitoRelacionada.getEmpresa());
                RestClient.getRestTemplate().postForObject("/pagos/notas/" + notaDebitoRelacionada.getIdNota(),
                        pago, Pago.class);
            }
            pagoCreado = true;
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isPagoCreado() {
        return this.pagoCreado;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelGeneral = new javax.swing.JPanel();
        lbl_FormaDePago = new javax.swing.JLabel();
        txt_Monto = new javax.swing.JFormattedTextField();
        lbl_Monto = new javax.swing.JLabel();
        txt_Nota = new javax.swing.JTextField();
        cmb_FormaDePago = new javax.swing.JComboBox<>();
        lbl_Nota = new javax.swing.JLabel();
        btn_Guardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nuevo Pago");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_FormaDePago.setForeground(java.awt.Color.red);
        lbl_FormaDePago.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_FormaDePago.setText("* Forma de Pago:");

        txt_Monto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Monto.setText("0");
        txt_Monto.setToolTipText("");
        txt_Monto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_MontoFocusGained(evt);
            }
        });

        lbl_Monto.setForeground(java.awt.Color.red);
        lbl_Monto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Monto.setText("* Monto");

        lbl_Nota.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nota.setText("Nota");

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbl_FormaDePago, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Monto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Nota, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmb_FormaDePago, javax.swing.GroupLayout.Alignment.LEADING, 0, 269, Short.MAX_VALUE)
                    .addComponent(txt_Monto, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_Nota, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmb_FormaDePago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_FormaDePago))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Monto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Nota)
                    .addComponent(txt_Nota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_Guardar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        this.guardarPago();
    }//GEN-LAST:event_btn_GuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (facturaRelacionada != null) {
            txt_Monto.setValue(RestClient.getRestTemplate()
                    .getForObject("/pagos/facturas/" + facturaRelacionada.getId_Factura() + "/saldo",
                            double.class));
        } else if (notaDebitoRelacionada != null) {
            txt_Monto.setValue(RestClient.getRestTemplate()
                    .getForObject("/pagos/notas/" + notaDebitoRelacionada.getIdNota() + "/saldo",
                            double.class));
        }
        this.cargarFormasDePago();
    }//GEN-LAST:event_formWindowOpened

    private void txt_MontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_MontoFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_Monto.selectAll();
        });
    }//GEN-LAST:event_txt_MontoFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JComboBox<FormaDePago> cmb_FormaDePago;
    private javax.swing.JLabel lbl_FormaDePago;
    private javax.swing.JLabel lbl_Monto;
    private javax.swing.JLabel lbl_Nota;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JFormattedTextField txt_Monto;
    private javax.swing.JTextField txt_Nota;
    // End of variables declaration//GEN-END:variables
}
