package sic.vista.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.EmpresaActiva;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.Movimiento;
import sic.modelo.TipoDeComprobante;
import sic.util.RenderTabla;

public class PagoMultiplesFacturasGUI extends JInternalFrame {

    private List<Factura> facturas = new ArrayList<>();
    private final Movimiento movimiento;
    private final ModeloTabla modeloTablaFacturas = new ModeloTabla();
    private double montoTotal = 0.0;
    private boolean pagosCreados;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public PagoMultiplesFacturasGUI(long[] idsFacturas, Movimiento movimiento) {                   
        this.movimiento = movimiento;                 
        this.facturas = this.getFacturasOrdenadasPorFechaAscAndTipoDeComprobante(idsFacturas);
        pagosCreados = false;
        this.initComponents();   
    }
 
    public boolean isPagosCreados() {
        return this.pagosCreados;
    }
    
    private void setColumnasTabla() {
        tbl_InformacionFacturas.setAutoCreateRowSorter(true);
        String[] encabezados = new String[5];
        encabezados[0] = "Fecha";
        encabezados[1] = "Tipo";
        encabezados[2] = "Nº Factura";
        encabezados[3] = "Total";
        encabezados[4] = "Total Adeudado";
        modeloTablaFacturas.setColumnIdentifiers(encabezados);
        tbl_InformacionFacturas.setModel(modeloTablaFacturas);
        Class[] tipos = new Class[modeloTablaFacturas.getColumnCount()];
        tipos[0] = Date.class;
        tipos[1] = TipoDeComprobante.class;
        tipos[2] = String.class;
        tipos[3] = Double.class;
        tipos[4] = Double.class;
        modeloTablaFacturas.setClaseColumnas(tipos);
        tbl_InformacionFacturas.getTableHeader().setReorderingAllowed(false);
        tbl_InformacionFacturas.getTableHeader().setResizingAllowed(true);
        tbl_InformacionFacturas.setDefaultRenderer(Double.class, new RenderTabla());
        tbl_InformacionFacturas.getColumnModel().getColumn(0).setPreferredWidth(100);
        tbl_InformacionFacturas.getColumnModel().getColumn(1).setPreferredWidth(100);
        tbl_InformacionFacturas.getColumnModel().getColumn(2).setPreferredWidth(120);
        tbl_InformacionFacturas.getColumnModel().getColumn(3).setPreferredWidth(120);
        tbl_InformacionFacturas.getColumnModel().getColumn(4).setPreferredWidth(120);
    }
   
    private void cargarFacturas() {
        try {
            facturas.stream().forEach((factura) -> {
                Object[] fila = new Object[5];
                fila[0] = factura.getFecha();
                fila[1] = factura.getTipoComprobante();
                fila[2] = factura.getNumSerie() + " - " + factura.getNumFactura();
                fila[3] = factura.getTotal();        
                double saldoAPagar = RestClient.getRestTemplate()
                        .getForObject("/pagos/facturas/" + factura.getId_Factura() + "/saldo",
                        double.class);                  
                montoTotal += saldoAPagar;
                fila[4] = saldoAPagar;
                modeloTablaFacturas.addRow(fila);
            });
            ftxt_Monto.setValue(montoTotal);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarFormasDePago() {
         List<FormaDePago> formasDePago = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/formas-de-pago/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                FormaDePago[].class)));
        formasDePago.stream().forEach((formaDePago) -> {
            cmb_FormaDePago.addItem(formaDePago);
        });
    }
    
    private List<Factura> getFacturasOrdenadasPorFechaAscAndTipoDeComprobante(long idsFacturas[]) {
        for (int i = 0; i < idsFacturas.length; i++) {
            facturas.add(RestClient.getRestTemplate().getForObject("/facturas/" + idsFacturas[i], Factura.class));
        }
        Collections.sort(facturas, (Factura f1, Factura f2) -> {
            if (f1.getTipoComprobante() == f2.getTipoComprobante()) {
                return f1.getFecha().compareTo(f2.getFecha());
            } else {
                return f2.getTipoComprobante().compareTo(f1.getTipoComprobante());
            }
        });
        return facturas;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Parametros = new javax.swing.JPanel();
        lbl_FormaDePago = new javax.swing.JLabel();
        lbl_Monto = new javax.swing.JLabel();
        cmb_FormaDePago = new javax.swing.JComboBox<>();
        ftxt_Monto = new javax.swing.JFormattedTextField();
        lblObservaciones = new javax.swing.JLabel();
        ftxt_Nota = new javax.swing.JTextField();
        pnl_Botones = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_InformacionFacturas = new javax.swing.JTable();
        lbl_leyenda = new javax.swing.JLabel();
        lbl_Aceptar = new javax.swing.JButton();

        setClosable(true);
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Stamp_16x16.png"))); // NOI18N
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

        pnl_Parametros.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_FormaDePago.setForeground(java.awt.Color.red);
        lbl_FormaDePago.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_FormaDePago.setText("* Forma de Pago:");

        lbl_Monto.setForeground(java.awt.Color.red);
        lbl_Monto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Monto.setText("* Monto:");

        ftxt_Monto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxt_Monto.setText("0");
        ftxt_Monto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxt_MontoFocusGained(evt);
            }
        });

        lblObservaciones.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblObservaciones.setText("Observaciones:");

        javax.swing.GroupLayout pnl_ParametrosLayout = new javax.swing.GroupLayout(pnl_Parametros);
        pnl_Parametros.setLayout(pnl_ParametrosLayout);
        pnl_ParametrosLayout.setHorizontalGroup(
            pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_ParametrosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                        .addComponent(lblObservaciones)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxt_Nota, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                        .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_Monto)
                            .addComponent(lbl_FormaDePago))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_FormaDePago, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblObservaciones, lbl_FormaDePago, lbl_Monto});

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmb_FormaDePago, ftxt_Monto, ftxt_Nota});

        pnl_ParametrosLayout.setVerticalGroup(
            pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_FormaDePago)
                    .addComponent(cmb_FormaDePago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Monto)
                    .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblObservaciones)
                    .addComponent(ftxt_Nota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblObservaciones, lbl_FormaDePago, lbl_Monto});

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmb_FormaDePago, ftxt_Monto, ftxt_Nota});

        javax.swing.GroupLayout pnl_BotonesLayout = new javax.swing.GroupLayout(pnl_Botones);
        pnl_Botones.setLayout(pnl_BotonesLayout);
        pnl_BotonesLayout.setHorizontalGroup(
            pnl_BotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 409, Short.MAX_VALUE)
        );
        pnl_BotonesLayout.setVerticalGroup(
            pnl_BotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );

        tbl_InformacionFacturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbl_InformacionFacturas);

        lbl_leyenda.setText("Facturas a pagar:");

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_leyenda))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(pnl_Botones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_Aceptar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_leyenda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Aceptar)
                    .addComponent(pnl_Botones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lbl_AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lbl_AceptarActionPerformed
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Esta seguro que desea realizar esta operacion?",
                "Pago", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                if (ftxt_Monto.getValue() == null) {
                    ftxt_Monto.setText("0");
                }
                double montoDelPago = Double.parseDouble(ftxt_Monto.getValue().toString());
                int indice = 0;
                long[] idsFacturas = new long[facturas.size()];
                for (Factura factura : facturas) {
                    idsFacturas[indice] = factura.getId_Factura();
                    indice++;
                }
                RestClient.getRestTemplate().put("/pagos/multiples-facturas-compras?"
                        + "idFactura=" + Arrays.toString(idsFacturas).substring(1, Arrays.toString(idsFacturas).length() - 1)
                        + "&monto=" + montoDelPago
                        + "&idFormaDePago=" + ((FormaDePago) cmb_FormaDePago.getSelectedItem()).getId_FormaDePago()
                        + "&nota=" + ftxt_Nota.getText(),
                        null);
                pagosCreados = true;
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
    }//GEN-LAST:event_lbl_AceptarActionPerformed

    private void ftxt_MontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxt_MontoFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxt_Monto.selectAll();
        });
    }//GEN-LAST:event_ftxt_MontoFocusGained

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            tbl_InformacionFacturas.setModel(modeloTablaFacturas);
            if (movimiento == Movimiento.VENTA) {
                this.setTitle("Pago Multiple - Cliente: " + ((FacturaVenta) facturas.get(0)).getRazonSocialCliente());
            }
            if (movimiento == Movimiento.COMPRA) {
                this.setTitle("Pago Multiple - Proveedor: " + ((FacturaCompra) facturas.get(0)).getRazonSocialProveedor());
            }
            this.setColumnasTabla();
            this.cargarFormasDePago();
            this.cargarFacturas();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formInternalFrameOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<FormaDePago> cmb_FormaDePago;
    private javax.swing.JFormattedTextField ftxt_Monto;
    private javax.swing.JTextField ftxt_Nota;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JButton lbl_Aceptar;
    private javax.swing.JLabel lbl_FormaDePago;
    private javax.swing.JLabel lbl_Monto;
    private javax.swing.JLabel lbl_leyenda;
    private javax.swing.JPanel pnl_Botones;
    private javax.swing.JPanel pnl_Parametros;
    private javax.swing.JTable tbl_InformacionFacturas;
    // End of variables declaration//GEN-END:variables
  
}
