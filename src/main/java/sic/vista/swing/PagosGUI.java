package sic.vista.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.Nota;
import sic.modelo.NotaDebito;
import sic.modelo.Pago;
import sic.modelo.Recibo;
import sic.util.FormatoFechasEnTablasRenderer;
import sic.util.FormatterFechaHora;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class PagosGUI extends JInternalFrame {

    private List<Pago> pagos;
    private Factura facturaRelacionada = null;
    private NotaDebito notaDebitoRelacionada = null;
    private Recibo reciboRelacionado = null;
    private boolean mostrarDetalleComprobanteRelacionado = false;
    private final ModeloTabla modeloTablaResultados = new ModeloTabla();
    private final FormatterFechaHora formateador = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHA_HISPANO);
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public PagosGUI(Factura factura) {
        this.initComponents();
        facturaRelacionada = factura;        
        this.setColumnas();
    }
    
    public PagosGUI(NotaDebito notaDebito) {
        this.initComponents();                       
        this.notaDebitoRelacionada = notaDebito;        
        this.setColumnas();
    }
    
    public PagosGUI(Recibo recibo) {
        this.initComponents();                       
        reciboRelacionado = recibo;
        mostrarDetalleComprobanteRelacionado = true;
        this.setColumnas();
    }

    private void getPagos() {
        try {
            if (facturaRelacionada != null) {
                pagos = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                        .getForObject("/pagos/facturas/" + facturaRelacionada.getId_Factura(), Pago[].class)));
            } else if (notaDebitoRelacionada != null) {
                pagos = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                        .getForObject("/pagos/notas/" + notaDebitoRelacionada.getIdNota(), Pago[].class)));
            } else if (reciboRelacionado != null) {
                pagos = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                        .getForObject("/pagos/recibos/" + reciboRelacionado.getIdRecibo(), Pago[].class)));
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

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados;
        if (mostrarDetalleComprobanteRelacionado == true) {
            encabezados = new String[7];
            encabezados[0] = "Nº Pago";
            encabezados[1] = "Fecha Pago";
            encabezados[2] = "Forma de Pago";
            encabezados[3] = "Monto";
            encabezados[4] = "Observaciones";            
            encabezados[5] = "Fecha Comprobante";
            encabezados[6] = "Comprobante";
        } else {
            encabezados = new String[5];
            encabezados[0] = "Nº Pago";
            encabezados[1] = "Fecha Pago";
            encabezados[2] = "Forma de Pago";
            encabezados[3] = "Monto";
            encabezados[4] = "Observaciones";
        }        
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = Integer.class;
        tipos[1] = Date.class;
        tipos[2] = String.class;
        tipos[3] = Double.class;
        tipos[4] = String.class;
        if (mostrarDetalleComprobanteRelacionado == true) {
            tipos[5] = Date.class;
            tipos[6] = String.class;
        }
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);
        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(Double.class, new RenderTabla());
        //size de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setMinWidth(80);
        tbl_Resultados.getColumnModel().getColumn(0).setMaxWidth(80);
        tbl_Resultados.getColumnModel().getColumn(1).setMinWidth(120);
        tbl_Resultados.getColumnModel().getColumn(1).setMaxWidth(120);        
        tbl_Resultados.getColumnModel().getColumn(3).setMinWidth(120);
        tbl_Resultados.getColumnModel().getColumn(3).setMaxWidth(120);        
    }

    private void cargarResultadosAlTable() {
        try {
            modeloTablaResultados.setRowCount(0);
            tbl_Resultados.setModel(modeloTablaResultados);
            this.setColumnas();
            pagos.stream().map(p -> {
                Object[] fila;
                if (mostrarDetalleComprobanteRelacionado == true) {
                    fila = new Object[7];
                    fila[0] = p.getNroPago();
                    fila[1] = p.getFecha();
                    fila[2] = p.getNombreFormaDePago();
                    fila[3] = p.getMonto();
                    fila[4] = p.getNota();                    
                    Factura f = RestClient.getRestTemplate().getForObject("/facturas/pagos/" + p.getId_Pago(), Factura.class);
                    if (f != null) {
                        fila[5] = f.getFecha();
                        fila[6] = f.getTipoComprobante() + " " + f.getNumSerie() + " - " + f.getNumFactura();
                    }
                    Nota n = RestClient.getRestTemplate().getForObject("/notas/pagos/" + p.getId_Pago(), Nota.class);
                    if (n != null) {
                        fila[5] = n.getFecha();
                        fila[6] = n.getTipoComprobante() + " " + n.getSerie() + " - " + n.getNroNota();
                    }
                } else {
                    fila = new Object[5];
                    fila[0] = p.getNroPago();
                    fila[1] = p.getFecha();
                    fila[2] = p.getNombreFormaDePago();
                    fila[3] = p.getMonto();
                    fila[4] = p.getNota();
                }
                return fila;
            }).forEach(fila -> {
                modeloTablaResultados.addRow(fila);
            });
            tbl_Resultados.getColumnModel().getColumn(0).setCellRenderer(new FormatoFechasEnTablasRenderer());
            tbl_Resultados.setModel(modeloTablaResultados);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarSaldos() {
        try {
            if (facturaRelacionada != null) {
                txt_TotalAdeudado.setValue(facturaRelacionada.getTotal());
                txt_TotalPagado.setValue(RestClient.getRestTemplate()
                        .getForObject("/pagos/facturas/" + facturaRelacionada.getId_Factura() + "/total-pagado", double.class));
                txt_SaldoAPagar.setValue(RestClient.getRestTemplate()
                        .getForObject("/pagos/facturas/" + facturaRelacionada.getId_Factura() + "/saldo", double.class));
            } else if (notaDebitoRelacionada != null) {
                txt_TotalAdeudado.setValue(notaDebitoRelacionada.getTotal());
                txt_TotalPagado.setValue(RestClient.getRestTemplate()
                        .getForObject("/pagos/notas/" + notaDebitoRelacionada.getIdNota() + "/total-pagado", double.class));
                txt_SaldoAPagar.setValue(RestClient.getRestTemplate()
                        .getForObject("/pagos/notas/" + notaDebitoRelacionada.getIdNota() + "/saldo", double.class));
            } else if (reciboRelacionado != null) {
                txt_TotalAdeudado.setValue(0);
                txt_TotalPagado.setValue(reciboRelacionado.getMonto());
                lbl_Saldo.setText("Saldo Sobrante:");
                txt_SaldoAPagar.setValue(reciboRelacionado.getSaldoSobrante());
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sp_Resultado = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        panelSaldos = new javax.swing.JPanel();
        lbl_TA = new javax.swing.JLabel();
        lbl_TP = new javax.swing.JLabel();
        lbl_Saldo = new javax.swing.JLabel();
        txt_TotalAdeudado = new javax.swing.JFormattedTextField();
        txt_TotalPagado = new javax.swing.JFormattedTextField();
        txt_SaldoAPagar = new javax.swing.JFormattedTextField();
        lbl_AvisoPagado = new javax.swing.JLabel();
        btn_nuevoPago = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();

        setClosable(true);
        setResizable(true);
        setTitle("Pagos");
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

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Resultado.setViewportView(tbl_Resultados);

        panelSaldos.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_TA.setForeground(java.awt.Color.red);
        lbl_TA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TA.setText("Total Adeudado:");

        lbl_TP.setForeground(java.awt.Color.green);
        lbl_TP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TP.setText("Total Pagado:");

        lbl_Saldo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Saldo.setText("Saldo a Pagar:");

        txt_TotalAdeudado.setEditable(false);
        txt_TotalAdeudado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_TotalAdeudado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_TotalAdeudado.setFocusable(false);

        txt_TotalPagado.setEditable(false);
        txt_TotalPagado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_TotalPagado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_TotalPagado.setFocusable(false);

        txt_SaldoAPagar.setEditable(false);
        txt_SaldoAPagar.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_SaldoAPagar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_SaldoAPagar.setFocusable(false);

        javax.swing.GroupLayout panelSaldosLayout = new javax.swing.GroupLayout(panelSaldos);
        panelSaldos.setLayout(panelSaldosLayout);
        panelSaldosLayout.setHorizontalGroup(
            panelSaldosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaldosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_TA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_TotalAdeudado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_TP)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_TotalPagado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Saldo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_SaldoAPagar)
                .addContainerGap())
        );
        panelSaldosLayout.setVerticalGroup(
            panelSaldosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaldosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaldosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_TA)
                    .addComponent(txt_TotalAdeudado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Saldo)
                    .addComponent(txt_SaldoAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_TP)
                    .addComponent(txt_TotalPagado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbl_AvisoPagado.setText("NOTA: Cuando el total pagado cumpla con el valor del comprobante, se marcará automaticamente como pagado.");

        btn_nuevoPago.setForeground(java.awt.Color.blue);
        btn_nuevoPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/StampArrow_16x16.png"))); // NOI18N
        btn_nuevoPago.setText("Nuevo");
        btn_nuevoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_nuevoPagoActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Cancel_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar ");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelSaldos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(sp_Resultado))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbl_AvisoPagado)
                        .addGap(0, 90, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_nuevoPago)
                .addGap(0, 0, 0)
                .addComponent(btn_Eliminar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Eliminar, btn_nuevoPago});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_AvisoPagado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultado, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_nuevoPago, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_Eliminar, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelSaldos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Eliminar, btn_nuevoPago});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        txt_TotalAdeudado.setValue(0.00);
        txt_TotalPagado.setValue(0.00);
        txt_SaldoAPagar.setValue(0.00);
        if (facturaRelacionada != null) {
            if (facturaRelacionada.getNumSerie() == 0 && facturaRelacionada.getNumFactura() == 0) {
                this.setTitle("Pagos de " + facturaRelacionada.getTipoComprobante() 
                        + " (sin numero) con Fecha: " + formateador.format(facturaRelacionada.getFecha()));
            } else {
                this.setTitle("Pagos de " + facturaRelacionada.getTipoComprobante()
                        + " Nro: " + facturaRelacionada.getNumSerie() + " - " + facturaRelacionada.getNumFactura()
                        + " con Fecha: " + formateador.format(facturaRelacionada.getFecha()));
            }
        } else if (notaDebitoRelacionada != null) {
            if (notaDebitoRelacionada.getSerie() == 0 && notaDebitoRelacionada.getNroNota() == 0) {
                this.setTitle("Pagos de " + notaDebitoRelacionada.getTipoComprobante() 
                        + " (sin numero) con Fecha: " + formateador.format(notaDebitoRelacionada.getFecha()));
            } else {
                this.setTitle("Pagos de " + notaDebitoRelacionada.getTipoComprobante() + 
                        " Nro: " + notaDebitoRelacionada.getSerie() + " - " + notaDebitoRelacionada.getNroNota()
                        + " con Fecha: " + formateador.format(notaDebitoRelacionada.getFecha()));
            }
        } else if (reciboRelacionado != null) {
            this.setTitle("Pagos del Recibo " + reciboRelacionado.getNumSerie()+ " - " + reciboRelacionado.getNumRecibo()
                    + " con Fecha: " + formateador.format(reciboRelacionado.getFecha()));
        }
        if (mostrarDetalleComprobanteRelacionado == true) {
            btn_nuevoPago.setVisible(false);
            btn_Eliminar.setVisible(false);
            lbl_AvisoPagado.setVisible(false);
            lbl_TA.setVisible(false);
            txt_TotalAdeudado.setVisible(false);
        }
        if (facturaRelacionada instanceof FacturaVenta || notaDebitoRelacionada != null) {
            btn_nuevoPago.setVisible(false);
            btn_Eliminar.setVisible(false);
        }
        this.getPagos();
        this.actualizarSaldos();
        this.cargarResultadosAlTable();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btn_nuevoPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_nuevoPagoActionPerformed
        if (facturaRelacionada != null && facturaRelacionada instanceof FacturaCompra) {
            DetalleComprobanteGUI gui_DetalleComprobanteGUI = new DetalleComprobanteGUI((FacturaCompra) facturaRelacionada);
            gui_DetalleComprobanteGUI.setModal(true);
            gui_DetalleComprobanteGUI.setLocationRelativeTo(this);
            gui_DetalleComprobanteGUI.setVisible(true);
            this.getPagos();
            this.actualizarSaldos();
            this.cargarResultadosAlTable();
        }
    }//GEN-LAST:event_btn_nuevoPagoActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Esta seguro que desea eliminar el pago seleccionado?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    RestClient.getRestTemplate().delete("/pagos/" + pagos.get(indexFilaSeleccionada).getId_Pago());
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                LOGGER.warn("El Pago: " + pagos.get(indexFilaSeleccionada).toString() + " se eliminó correctamente.");
                pagos.remove(indexFilaSeleccionada);
                this.getPagos();
                this.actualizarSaldos();
                this.cargarResultadosAlTable();
            }
        }
    }//GEN-LAST:event_btn_EliminarActionPerformed
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_nuevoPago;
    private javax.swing.JLabel lbl_AvisoPagado;
    private javax.swing.JLabel lbl_Saldo;
    private javax.swing.JLabel lbl_TA;
    private javax.swing.JLabel lbl_TP;
    private javax.swing.JPanel panelSaldos;
    private javax.swing.JScrollPane sp_Resultado;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JFormattedTextField txt_SaldoAPagar;
    private javax.swing.JFormattedTextField txt_TotalAdeudado;
    private javax.swing.JFormattedTextField txt_TotalPagado;
    // End of variables declaration//GEN-END:variables
}
