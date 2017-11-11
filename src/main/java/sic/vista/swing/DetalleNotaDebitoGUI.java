package sic.vista.swing;

import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
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
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.modelo.NotaDebito;
import sic.modelo.Pago;
import sic.modelo.RenglonNotaDebito;
import sic.modelo.UsuarioActivo;
import sic.util.FormatterNumero;

public class DetalleNotaDebitoGUI extends JDialog {
    private Cliente cliente;
    private Pago pago;
    private final long idCliente;
    private final long idPago;
    private boolean notaDebitoCreada;    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleNotaDebitoGUI(long idCliente, long idPago) {
        this.initComponents();
        this.setIcon();
        this.notaDebitoCreada = false;
        this.idCliente = idCliente;
        this.idPago = idPago;
    }
    
    public boolean isNotaDebitoCreada() {
        return notaDebitoCreada;
    }
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(PuntoDeVentaGUI.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }
    
    private void cargarDetalleCliente() {
        txtNombreCliente.setText(cliente.getRazonSocial());
        txtDomicilioCliente.setText(cliente.getDireccion());
        txtIDFiscalCliente.setText(cliente.getIdFiscal());
        txtCondicionIVACliente.setText(cliente.getCondicionIVA().getNombre());
    }
    
    private void cargarDetallePago() {
        lblDetallePago.setText("NroPago: " + pago.getNroPago() + " - " + pago.getNota());
        lblMontoPago.setText("$" + FormatterNumero.formatConRedondeo(pago.getMonto()));
        lblImportePago.setText("$" + FormatterNumero.formatConRedondeo(pago.getMonto()));
        txtNoGravado.setValue(pago.getMonto());
        txtTotal.setValue(pago.getMonto());
    }
    
    private void cargarDetalleComprobante() {
        txtMontoRenglon2.setValue(Double.parseDouble(txtMontoRenglon2.getText()));
        double iva = (Double) txtMontoRenglon2.getValue() * 0.21;
        lblIvaNetoRenglon2.setText("$" + FormatterNumero.formatConRedondeo(iva));
        lblImporteRenglon2.setText("$" + FormatterNumero.formatConRedondeo((Double.parseDouble(txtMontoRenglon2.getText()) + iva)));
        txtSubTotalBruto.setValue(Double.parseDouble(txtMontoRenglon2.getText()));
        txtIVA21Neto.setValue(iva);
        txtNoGravado.setValue(pago.getMonto());
        txtTotal.setValue(pago.getMonto() + ((Double) txtMontoRenglon2.getValue()) + iva); 
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelCliente = new javax.swing.JPanel();
        lblNombreCliente = new javax.swing.JLabel();
        lblDomicilioCliente = new javax.swing.JLabel();
        lblIDFiscalCliente = new javax.swing.JLabel();
        lblCondicionIVACliente = new javax.swing.JLabel();
        txtCondicionIVACliente = new javax.swing.JTextField();
        txtIDFiscalCliente = new javax.swing.JTextField();
        txtDomicilioCliente = new javax.swing.JTextField();
        txtNombreCliente = new javax.swing.JTextField();
        panelDetalle = new javax.swing.JPanel();
        lblDescripcion = new javax.swing.JLabel();
        lblIvaPorcentaje = new javax.swing.JLabel();
        lblImporteEncabezado = new javax.swing.JLabel();
        lblIvaEncabezado = new javax.swing.JLabel();
        lblIVAnetoDetallePago = new javax.swing.JLabel();
        lblImportePago = new javax.swing.JLabel();
        lbl_Monto = new javax.swing.JLabel();
        txtMontoRenglon2 = new javax.swing.JFormattedTextField();
        lblIvaNetoRenglon2 = new javax.swing.JLabel();
        lblImporteRenglon2 = new javax.swing.JLabel();
        lblGastoAdministrativo = new javax.swing.JLabel();
        lblIVA21 = new javax.swing.JLabel();
        lblDetallePago = new javax.swing.JLabel();
        lblMontoPago = new javax.swing.JLabel();
        lblIVADetallePago = new javax.swing.JLabel();
        panelResultados = new javax.swing.JPanel();
        lbl_IVA105 = new javax.swing.JLabel();
        txtIVA21Neto = new javax.swing.JFormattedTextField();
        lblTotal = new javax.swing.JLabel();
        txtTotal = new javax.swing.JFormattedTextField();
        txtSubTotalBruto = new javax.swing.JFormattedTextField();
        lblSubTotalBruto = new javax.swing.JLabel();
        lblIva21 = new javax.swing.JLabel();
        lblNoGravado = new javax.swing.JLabel();
        txtNoGravado = new javax.swing.JFormattedTextField();
        btnGuardar = new javax.swing.JButton();
        panelMotivo = new javax.swing.JPanel();
        lblMotivo = new javax.swing.JLabel();
        cmbDescripcionRenglon2 = new javax.swing.JComboBox<>();

        setLocationByPlatform(true);
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lblNombreCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNombreCliente.setText("Nombre:");

        lblDomicilioCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDomicilioCliente.setText("Domicilio:");

        lblIDFiscalCliente.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIDFiscalCliente.setText("ID Fiscal:");

        lblCondicionIVACliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCondicionIVACliente.setText("Condición IVA:");

        txtCondicionIVACliente.setEditable(false);
        txtCondicionIVACliente.setFocusable(false);

        txtIDFiscalCliente.setEditable(false);
        txtIDFiscalCliente.setFocusable(false);

        txtDomicilioCliente.setEditable(false);
        txtDomicilioCliente.setFocusable(false);

        txtNombreCliente.setEditable(false);
        txtNombreCliente.setFocusable(false);

        javax.swing.GroupLayout panelClienteLayout = new javax.swing.GroupLayout(panelCliente);
        panelCliente.setLayout(panelClienteLayout);
        panelClienteLayout.setHorizontalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDomicilioCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCondicionIVACliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDomicilioCliente, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtNombreCliente)
                    .addGroup(panelClienteLayout.createSequentialGroup()
                        .addComponent(txtCondicionIVACliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIDFiscalCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIDFiscalCliente)))
                .addContainerGap())
        );
        panelClienteLayout.setVerticalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombreCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDomicilioCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDomicilioCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCondicionIVACliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCondicionIVACliente)
                    .addComponent(txtIDFiscalCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIDFiscalCliente)))
        );

        panelDetalle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblDescripcion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDescripcion.setText("Descripcion");

        lblIvaPorcentaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIvaPorcentaje.setText("% IVA");

        lblImporteEncabezado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImporteEncabezado.setText("Importe");

        lblIvaEncabezado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIvaEncabezado.setText("IVA");

        lblIVAnetoDetallePago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIVAnetoDetallePago.setText("-");

        lblImportePago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImportePago.setText("$0");

        lbl_Monto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_Monto.setText("Monto");

        txtMontoRenglon2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtMontoRenglon2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMontoRenglon2.setText("0");
        txtMontoRenglon2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMontoRenglon2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMontoRenglon2FocusLost(evt);
            }
        });
        txtMontoRenglon2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMontoRenglon2ActionPerformed(evt);
            }
        });
        txtMontoRenglon2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtMontoRenglon2KeyTyped(evt);
            }
        });

        lblIvaNetoRenglon2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIvaNetoRenglon2.setText("$0");

        lblImporteRenglon2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImporteRenglon2.setText("$0");

        lblGastoAdministrativo.setText("Gasto Administrativo");

        lblIVA21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIVA21.setText("21%");

        lblDetallePago.setText("nroPago + nota");

        lblMontoPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMontoPago.setText("$0");

        lblIVADetallePago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIVADetallePago.setText("-");

        javax.swing.GroupLayout panelDetalleLayout = new javax.swing.GroupLayout(panelDetalle);
        panelDetalle.setLayout(panelDetalleLayout);
        panelDetalleLayout.setHorizontalGroup(
            panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescripcion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGastoAdministrativo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDetallePago, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtMontoRenglon2)
                    .addComponent(lbl_Monto, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(lblMontoPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblIvaPorcentaje, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                    .addComponent(lblIVA21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblIVADetallePago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblIvaEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(lblIVAnetoDetallePago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblIvaNetoRenglon2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblImporteRenglon2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblImporteEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                    .addComponent(lblImportePago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelDetalleLayout.setVerticalGroup(
            panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDescripcion)
                    .addComponent(lbl_Monto)
                    .addComponent(lblIvaPorcentaje)
                    .addComponent(lblIvaEncabezado)
                    .addComponent(lblImporteEncabezado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDetallePago)
                    .addComponent(lblMontoPago)
                    .addComponent(lblIVADetallePago)
                    .addComponent(lblIVAnetoDetallePago)
                    .addComponent(lblImportePago))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblGastoAdministrativo)
                    .addComponent(txtMontoRenglon2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIVA21)
                    .addComponent(lblIvaNetoRenglon2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblImporteRenglon2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDetalleLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblIVAnetoDetallePago, lblImportePago});

        panelResultados.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_IVA105.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_IVA105.setText("I.V.A.");

        txtIVA21Neto.setEditable(false);
        txtIVA21Neto.setForeground(new java.awt.Color(29, 156, 37));
        txtIVA21Neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtIVA21Neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIVA21Neto.setText("0");
        txtIVA21Neto.setFocusable(false);
        txtIVA21Neto.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N

        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotal.setText("TOTAL");

        txtTotal.setEditable(false);
        txtTotal.setForeground(new java.awt.Color(29, 156, 37));
        txtTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setText("0");
        txtTotal.setFocusable(false);
        txtTotal.setFont(new java.awt.Font("DejaVu Sans", 1, 30)); // NOI18N

        txtSubTotalBruto.setEditable(false);
        txtSubTotalBruto.setForeground(new java.awt.Color(29, 156, 37));
        txtSubTotalBruto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtSubTotalBruto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSubTotalBruto.setText("0");
        txtSubTotalBruto.setFocusable(false);
        txtSubTotalBruto.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N

        lblSubTotalBruto.setText("SubTotal Bruto");

        lblIva21.setText("21 %");

        lblNoGravado.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNoGravado.setText("No Gravado");

        txtNoGravado.setEditable(false);
        txtNoGravado.setForeground(new java.awt.Color(29, 156, 37));
        txtNoGravado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtNoGravado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNoGravado.setText("0");
        txtNoGravado.setFocusable(false);
        txtNoGravado.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSubTotalBruto)
                    .addComponent(lblSubTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtIVA21Neto)
                    .addComponent(lblIva21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_IVA105, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNoGravado)
                    .addComponent(lblNoGravado, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotal)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblIva21, lblNoGravado, lblSubTotalBruto, lbl_IVA105, txtIVA21Neto, txtNoGravado, txtSubTotalBruto});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblSubTotalBruto)
                    .addComponent(lbl_IVA105)
                    .addComponent(lblNoGravado)
                    .addComponent(lblTotal))
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelResultadosLayout.createSequentialGroup()
                                .addComponent(lblIva21, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtIVA21Neto)
                                    .addComponent(txtNoGravado)))
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(txtSubTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        btnGuardar.setForeground(java.awt.Color.blue);
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        panelMotivo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblMotivo.setText("Motivo:");

        cmbDescripcionRenglon2.setEditable(true);
        cmbDescripcionRenglon2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cheque Rechazado - Sin Fondos", "Cheque Rechazado - Cuenta Embargada", "Cheque Rechazado", "Irregularidad Cadena de Endosos" }));

        javax.swing.GroupLayout panelMotivoLayout = new javax.swing.GroupLayout(panelMotivo);
        panelMotivo.setLayout(panelMotivoLayout);
        panelMotivoLayout.setHorizontalGroup(
            panelMotivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMotivoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMotivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbDescripcionRenglon2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelMotivoLayout.setVerticalGroup(
            panelMotivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMotivoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMotivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblMotivo)
                    .addComponent(cmbDescripcionRenglon2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMotivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGuardar)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelResultados, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        NotaDebito notaDebito = new NotaDebito();
        notaDebito.setFecha(new Date());
        notaDebito.setIva21Neto((Double)txtIVA21Neto.getValue());
        notaDebito.setIva105Neto(0);
        notaDebito.setMontoNoGravado(pago.getMonto());
        notaDebito.setMotivo(cmbDescripcionRenglon2.getSelectedItem().toString());
        try {
            notaDebito.setRenglonesNotaDebito(Arrays.asList(RestClient.getRestTemplate().getForObject("/notas/renglon/debito/pago/" + pago.getId_Pago()
                    + "?monto=" + (Double) txtSubTotalBruto.getValue()
                    + "&ivaPorcentaje=21", RenglonNotaDebito[].class)));
            notaDebito.setSubTotalBruto((Double) txtSubTotalBruto.getValue());
            notaDebito.setTotal(RestClient.getRestTemplate().getForObject("/notas/debito/total"
                    + "?subTotalBruto=" + (Double) txtSubTotalBruto.getValue()
                    + "&iva21Neto=" + notaDebito.getIva21Neto()
                    + "&montoNoGravado=" + notaDebito.getMontoNoGravado(), double.class));
            notaDebito.setUsuario(UsuarioActivo.getInstance().getUsuario());
            notaDebito = RestClient.getRestTemplate().postForObject("/notas/debito/empresa/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                    + "/cliente/" + cliente.getId_Cliente()
                    + "/usuario/" + UsuarioActivo.getInstance().getUsuario().getId_Usuario()
                    + "/pago/" + pago.getId_Pago(), notaDebito, NotaDebito.class);
            if (notaDebito != null) {
                notaDebitoCreada = true;
                if (Desktop.isDesktopSupported()) {
                    try {
                        byte[] reporte = RestClient.getRestTemplate()
                                .getForObject("/notas/" + notaDebito.getIdNota() + "/reporte",
                                        byte[].class);
                        File f = new File(System.getProperty("user.home") + "/NotaDebito.pdf");
                        Files.write(f.toPath(), reporte);
                        Desktop.getDesktop().open(f);
                    } catch (IOException ex) {
                        LOGGER.error(ex.getMessage());
                        JOptionPane.showMessageDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                this.dispose();
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_btnGuardarActionPerformed
    
    private void txtMontoRenglon2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMontoRenglon2FocusGained
        SwingUtilities.invokeLater(() -> {
            txtMontoRenglon2.selectAll();
        });
    }//GEN-LAST:event_txtMontoRenglon2FocusGained

    private void txtMontoRenglon2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMontoRenglon2FocusLost
        this.cargarDetalleComprobante();
    }//GEN-LAST:event_txtMontoRenglon2FocusLost

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setTitle("Nueva Nota de Debito");
        try {
            cliente = RestClient.getRestTemplate().getForObject("/clientes/" + idCliente, Cliente.class);
            pago = RestClient.getRestTemplate().getForObject("/pagos/" + idPago, Pago.class);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        this.cargarDetalleCliente();
        this.cargarDetallePago();        
    }//GEN-LAST:event_formWindowOpened

    private void txtMontoRenglon2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMontoRenglon2KeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_txtMontoRenglon2KeyTyped

    private void txtMontoRenglon2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMontoRenglon2ActionPerformed
        this.cargarDetalleComprobante();
    }//GEN-LAST:event_txtMontoRenglon2ActionPerformed
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> cmbDescripcionRenglon2;
    private javax.swing.JLabel lblCondicionIVACliente;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblDetallePago;
    private javax.swing.JLabel lblDomicilioCliente;
    private javax.swing.JLabel lblGastoAdministrativo;
    private javax.swing.JLabel lblIDFiscalCliente;
    private javax.swing.JLabel lblIVA21;
    private javax.swing.JLabel lblIVADetallePago;
    private javax.swing.JLabel lblIVAnetoDetallePago;
    private javax.swing.JLabel lblImporteEncabezado;
    private javax.swing.JLabel lblImportePago;
    private javax.swing.JLabel lblImporteRenglon2;
    private javax.swing.JLabel lblIva21;
    private javax.swing.JLabel lblIvaEncabezado;
    private javax.swing.JLabel lblIvaNetoRenglon2;
    private javax.swing.JLabel lblIvaPorcentaje;
    private javax.swing.JLabel lblMontoPago;
    private javax.swing.JLabel lblMotivo;
    private javax.swing.JLabel lblNoGravado;
    private javax.swing.JLabel lblNombreCliente;
    private javax.swing.JLabel lblSubTotalBruto;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lbl_IVA105;
    private javax.swing.JLabel lbl_Monto;
    private javax.swing.JPanel panelCliente;
    private javax.swing.JPanel panelDetalle;
    private javax.swing.JPanel panelMotivo;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JTextField txtCondicionIVACliente;
    private javax.swing.JTextField txtDomicilioCliente;
    private javax.swing.JTextField txtIDFiscalCliente;
    private javax.swing.JFormattedTextField txtIVA21Neto;
    private javax.swing.JFormattedTextField txtMontoRenglon2;
    private javax.swing.JFormattedTextField txtNoGravado;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JFormattedTextField txtSubTotalBruto;
    private javax.swing.JFormattedTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
