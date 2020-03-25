package sic.vista.swing;

import java.awt.Desktop;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.ArrayList;
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
import sic.modelo.Factura;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.NuevaFacturaVenta;
import sic.modelo.Pedido;
import sic.modelo.RenglonFactura;
import sic.modelo.TipoDeComprobante;
import sic.modelo.Transportista;
import sic.modelo.UsuarioActivo;

public class CerrarVentaGUI extends JDialog {

    private boolean exito;
    private boolean facturaAutorizada = false;
    private final NuevaFacturaVenta nuevaFacturaVenta;
    private final BigDecimal totalFactura;
    private final Pedido pedido;
    private final ModeloTabla modeloTabla;
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private int[] indicesParaDividir = null;
    private final List<Long> idsFormasDePago = new ArrayList<>();
    private final List<BigDecimal> montos = new ArrayList<>();
    private boolean dividir = false;    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    public CerrarVentaGUI(NuevaFacturaVenta nuevaFacturaVenta, Pedido pedido, BigDecimal totalFactura, ModeloTabla modeloTabla) {
        super.setModal(true);
        this.nuevaFacturaVenta = nuevaFacturaVenta;
        this.totalFactura = totalFactura;
        this.pedido = pedido;
        this.modeloTabla = modeloTabla;
        this.initComponents();
        this.setIcon();
        this.setListeners();
    }

    public boolean isExito() {
        return exito;
    }

    private void setListeners() {
        chk_FormaDePago1.addKeyListener(keyHandler);
        chk_FormaDePago2.addKeyListener(keyHandler);
        chk_FormaDePago3.addKeyListener(keyHandler);
        cmb_FormaDePago1.addKeyListener(keyHandler);
        cmb_FormaDePago2.addKeyListener(keyHandler);
        cmb_FormaDePago3.addKeyListener(keyHandler);
        txt_MontoPago1.addKeyListener(keyHandler);
        txt_MontoPago2.addKeyListener(keyHandler);
        txt_MontoPago3.addKeyListener(keyHandler);
        cmb_Transporte.addKeyListener(keyHandler);
        btn_Finalizar.addKeyListener(keyHandler);
    }
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(CerrarVentaGUI.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void lanzarReporteFactura(Factura factura, String nombreReporte) {
        if (Desktop.isDesktopSupported()) {
            try {
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/facturas/ventas/" + factura.getIdFactura() + "/reporte", byte[].class);
                File f = new File(System.getProperty("user.home") + "/" + nombreReporte + ".pdf");
                Files.write(f.toPath(), reporte);
                Desktop.getDesktop().open(f);
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
        } else {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarFormasDePago() {
        try {
            cmb_FormaDePago1.removeAllItems();
            cmb_FormaDePago2.removeAllItems();
            cmb_FormaDePago3.removeAllItems();
            List<FormaDePago> formasDePago = Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/formas-de-pago", FormaDePago[].class));
            formasDePago.stream().map(fdp -> {
                cmb_FormaDePago1.addItem(fdp);
                return fdp;
            }).map(fdp -> {
                cmb_FormaDePago2.addItem(fdp);
                return fdp;
            }).forEach(fdp -> {
                cmb_FormaDePago3.addItem(fdp);
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

    private void cargarTransportistas() {
        try {
            cmb_Transporte.removeAllItems();
            cmb_Transporte.addItem(null);
            List<Transportista> transportes = Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/transportistas",
                            Transportista[].class));
            transportes.stream().forEach(t -> {
                cmb_Transporte.addItem(t);
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

    private void setEstadoFormasDePago() {
        try {
            FormaDePago formaDePagoPredeterminada = RestClient.getRestTemplate()
                    .getForObject("/formas-de-pago/predeterminada", FormaDePago.class);
            cmb_FormaDePago1.setSelectedItem(formaDePagoPredeterminada);
            cmb_FormaDePago1.setEnabled(false);
            txt_MontoPago1.setEnabled(false);
            cmb_FormaDePago2.setSelectedItem(formaDePagoPredeterminada);
            txt_MontoPago2.setEnabled(false);
            cmb_FormaDePago2.setEnabled(false);
            cmb_FormaDePago3.setSelectedItem(formaDePagoPredeterminada);
            cmb_FormaDePago3.setEnabled(false);
            txt_MontoPago3.setEnabled(false);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finalizarVenta() {
        if (cmb_Transporte.getSelectedItem() != null) {
            this.nuevaFacturaVenta.setIdTransportista(((Transportista) cmb_Transporte.getSelectedItem()).getIdTransportista());
        }
        this.armarMontosConFormasDePago();
        List<TipoDeComprobante> tiposAutorizables
                = Arrays.asList(
                        TipoDeComprobante.FACTURA_A, TipoDeComprobante.FACTURA_B, TipoDeComprobante.FACTURA_C);
        try {
            if (idsFormasDePago.isEmpty() == false) {
                Long[] formasDePago = new Long[idsFormasDePago.size()];
                formasDePago = idsFormasDePago.toArray(formasDePago);
                nuevaFacturaVenta.setIdsFormaDePago(formasDePago);
                BigDecimal[] montosPagos = new BigDecimal[montos.size()];
                montosPagos = montos.toArray(montosPagos);
                nuevaFacturaVenta.setMontos(montosPagos);
            }
            if (this.pedido != null && this.pedido.getIdPedido() != 0) {
                nuevaFacturaVenta.setIdPedido(this.pedido.getIdPedido());
            }
            if (dividir) {
                nuevaFacturaVenta.setIndices(indicesParaDividir);
                List<FacturaVenta> facturasDivididas = Arrays.asList(RestClient.getRestTemplate()
                        .postForObject("/facturas/ventas", nuevaFacturaVenta, FacturaVenta[].class));
                facturasDivididas.forEach(fv -> {
                    fv.setRenglones(Arrays.asList(RestClient.getRestTemplate()
                            .getForObject("/facturas/" + fv.getIdFactura() + "/renglones",
                                    RenglonFactura[].class)));
                });
                exito = true;
                facturasDivididas.stream().filter(facturaVenta -> tiposAutorizables.contains(facturaVenta.getTipoComprobante()))
                        .forEach(facturaVenta -> {
                            if (facturaVenta.getCae() != 0L) {
                                facturaAutorizada = true;
                            }
                        });
                if (facturaAutorizada) {
                    int reply = JOptionPane.showConfirmDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_reporte"),
                            "Aviso", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        int indice = facturasDivididas.size();
                        for (int i = 0; i < indice; i++) {
                            if (facturasDivididas.size() == 2 && !facturasDivididas.get(i).getRenglones().isEmpty()) {
                                if (i == 0) {
                                    this.lanzarReporteFactura(facturasDivididas.get(i), "ComprobanteX");
                                } else {
                                    this.lanzarReporteFactura(facturasDivididas.get(i), "Factura");
                                }
                            } else if (facturasDivididas.size() == 1 && !facturasDivididas.get(i).getRenglones().isEmpty()) {
                                this.lanzarReporteFactura(facturasDivididas.get(i), "Factura");
                            }
                        }
                    }
                }
            } else {
                FacturaVenta facturaGuardada = Arrays.asList(RestClient.getRestTemplate().postForObject("/facturas/ventas", nuevaFacturaVenta, FacturaVenta[].class)).get(0);
                exito = true;
                if (tiposAutorizables.contains(facturaGuardada.getTipoComprobante()) && facturaGuardada.getCae() != 0L) {
                    facturaAutorizada = true;
                }
                if (facturaAutorizada
                        || facturaGuardada.getTipoComprobante() == TipoDeComprobante.FACTURA_X
                        || facturaGuardada.getTipoComprobante() == TipoDeComprobante.FACTURA_Y
                        || facturaGuardada.getTipoComprobante() == TipoDeComprobante.PRESUPUESTO) {
                    int reply = JOptionPane.showConfirmDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_reporte"),
                            "Aviso", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        this.lanzarReporteFactura(facturaGuardada, "Factura");
                    }
                }
            }
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            exito = true;
            this.dispose();
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void armarMontosConFormasDePago() {
        montos.clear();
        idsFormasDePago.clear();
        if (chk_FormaDePago1.isSelected() && chk_FormaDePago1.isEnabled()) {            
            montos.add(new BigDecimal(txt_MontoPago1.getValue().toString()));            
            idsFormasDePago.add(((FormaDePago) cmb_FormaDePago1.getSelectedItem()).getIdFormaDePago());            
        }
        if (chk_FormaDePago2.isSelected() && chk_FormaDePago2.isEnabled()) {            
            montos.add(new BigDecimal(txt_MontoPago2.getValue().toString()));            
            idsFormasDePago.add(((FormaDePago) cmb_FormaDePago2.getSelectedItem()).getIdFormaDePago());            
        }
        if (chk_FormaDePago3.isSelected() && chk_FormaDePago3.isEnabled()) {            
            montos.add(new BigDecimal(txt_MontoPago3.getValue().toString()));                        
            idsFormasDePago.add(((FormaDePago) cmb_FormaDePago3.getSelectedItem()).getIdFormaDePago());
        }
    }

    /**
     * Clase interna para manejar las hotkeys
     */
    class HotKeysHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }

            if (evt.getKeyCode() == KeyEvent.VK_ENTER && evt.getSource() == btn_Finalizar) {
                btn_FinalizarActionPerformed(null);
            }
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_Finalizar = new javax.swing.JButton();
        panel = new javax.swing.JPanel();
        lbl_Vendor = new javax.swing.JLabel();
        lbl_Vendedor = new javax.swing.JLabel();
        lbl_Transporte = new javax.swing.JLabel();
        separador1 = new javax.swing.JSeparator();
        chk_FormaDePago1 = new javax.swing.JCheckBox();
        cmb_FormaDePago1 = new javax.swing.JComboBox();
        txt_MontoPago1 = new javax.swing.JFormattedTextField();
        chk_FormaDePago2 = new javax.swing.JCheckBox();
        cmb_FormaDePago2 = new javax.swing.JComboBox();
        txt_MontoPago2 = new javax.swing.JFormattedTextField();
        chk_FormaDePago3 = new javax.swing.JCheckBox();
        cmb_FormaDePago3 = new javax.swing.JComboBox();
        txt_MontoPago3 = new javax.swing.JFormattedTextField();
        separador2 = new javax.swing.JSeparator();
        lbl_Dividido = new javax.swing.JLabel();
        cmb_Transporte = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cerrar Venta");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btn_Finalizar.setForeground(java.awt.Color.blue);
        btn_Finalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Finalizar.setText("Finalizar");
        btn_Finalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_FinalizarActionPerformed(evt);
            }
        });

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Vendor.setText("Vendedor:");

        lbl_Vendedor.setForeground(new java.awt.Color(29, 156, 37));

        lbl_Transporte.setText("Transporte:");

        chk_FormaDePago1.setText("Forma de Pago #1:");
        chk_FormaDePago1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FormaDePago1ItemStateChanged(evt);
            }
        });

        txt_MontoPago1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_MontoPago1.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txt_MontoPago1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_MontoPago1FocusGained(evt);
            }
        });

        chk_FormaDePago2.setText("Forma de Pago #2:");
        chk_FormaDePago2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FormaDePago2ItemStateChanged(evt);
            }
        });

        txt_MontoPago2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_MontoPago2.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txt_MontoPago2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_MontoPago2FocusGained(evt);
            }
        });

        chk_FormaDePago3.setText("Forma de Pago #3:");
        chk_FormaDePago3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FormaDePago3ItemStateChanged(evt);
            }
        });

        txt_MontoPago3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_MontoPago3.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txt_MontoPago3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_MontoPago3FocusGained(evt);
            }
        });

        lbl_Dividido.setForeground(new java.awt.Color(29, 156, 37));
        lbl_Dividido.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_Dividido.setText("ATENCION: Este comprobante será dividido");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(chk_FormaDePago2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_FormaDePago1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_FormaDePago3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmb_FormaDePago3, 0, 250, Short.MAX_VALUE)
                            .addComponent(cmb_FormaDePago2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmb_FormaDePago1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_MontoPago3)
                            .addComponent(txt_MontoPago1)
                            .addComponent(txt_MontoPago2)))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_Transporte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_Vendor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_Vendedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmb_Transporte, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(separador1)
                    .addComponent(separador2)
                    .addComponent(lbl_Dividido, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Vendedor)
                    .addComponent(lbl_Vendor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Transporte)
                    .addComponent(cmb_Transporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separador1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_MontoPago1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_FormaDePago1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_FormaDePago1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_FormaDePago2)
                    .addComponent(cmb_FormaDePago2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_MontoPago2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_MontoPago3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_FormaDePago3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_FormaDePago3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separador2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Dividido, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txt_MontoPago1, txt_MontoPago3});

        panelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbl_Vendedor, lbl_Vendor});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Finalizar)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Finalizar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.cargarFormasDePago();
        this.cargarTransportistas();
        this.setEstadoFormasDePago();
        cmb_Transporte.setSelectedIndex(0);
        lbl_Vendedor.setText(UsuarioActivo.getInstance().getUsuario().toString());
        txt_MontoPago1.setValue(totalFactura);
        txt_MontoPago2.setValue(0);
        txt_MontoPago3.setValue(0);
        indicesParaDividir = new int[this.modeloTabla.getRowCount()];
        if ((indicesParaDividir.length > 0) 
                && (nuevaFacturaVenta.getTipoDeComprobante() == TipoDeComprobante.FACTURA_A
                || nuevaFacturaVenta.getTipoDeComprobante() == TipoDeComprobante.FACTURA_B
                || nuevaFacturaVenta.getTipoDeComprobante() == TipoDeComprobante.FACTURA_C)) {
            int j = 0;
            boolean tieneRenglonesMarcados = false;
            for (int i = 0; i < this.modeloTabla.getRowCount(); i++) {
                if ((boolean) this.modeloTabla.getValueAt(i, 0)) {
                    indicesParaDividir[j] = i;
                    j++;
                    tieneRenglonesMarcados = true;
                }
            }
            if (indicesParaDividir.length != 0 && tieneRenglonesMarcados) {
                dividir = true;
            }
        }
        if (dividir) {
            chk_FormaDePago1.setEnabled(false);
            chk_FormaDePago2.setEnabled(false);
            chk_FormaDePago3.setEnabled(false);
            cmb_FormaDePago1.setEnabled(false);
            cmb_FormaDePago2.setEnabled(false);
            cmb_FormaDePago3.setEnabled(false);
            txt_MontoPago1.setEnabled(false);
            txt_MontoPago2.setEnabled(false);
            txt_MontoPago3.setEnabled(false);
        } else {            
            lbl_Dividido.setText("");
        }
    }//GEN-LAST:event_formWindowOpened

    private void btn_FinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_FinalizarActionPerformed
        if (dividir) {
            this.finalizarVenta();
        } else {
            BigDecimal totalPagos = BigDecimal.ZERO;
            if (chk_FormaDePago1.isSelected() && chk_FormaDePago1.isEnabled()) {
                totalPagos = totalPagos.add(new BigDecimal(txt_MontoPago1.getValue().toString()));
            }
            if (chk_FormaDePago2.isSelected() && chk_FormaDePago2.isEnabled()) {                
                totalPagos = totalPagos.add(new BigDecimal(txt_MontoPago2.getValue().toString()));
            }
            if (chk_FormaDePago3.isSelected() && chk_FormaDePago3.isEnabled()) {
                totalPagos = totalPagos.add(new BigDecimal(txt_MontoPago3.getValue().toString()));
            }
            BigDecimal totalAPagar = totalFactura.setScale(2, RoundingMode.FLOOR);
            if (totalPagos.compareTo(totalAPagar) < 0) {
                int reply = JOptionPane.showConfirmDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_montos_insuficientes"),
                        "Aviso", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    this.finalizarVenta();
                }
            } else if (totalPagos.compareTo(totalAPagar) > 0) {
                int reply = JOptionPane.showConfirmDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_montos_superiores_al_total_factura"),
                        "Aviso", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    this.finalizarVenta();
                }
            } else {
                this.finalizarVenta();
            }
        }
    }//GEN-LAST:event_btn_FinalizarActionPerformed

    private void txt_MontoPago1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_MontoPago1FocusGained
        SwingUtilities.invokeLater(() -> {
            txt_MontoPago1.selectAll();
        });
    }//GEN-LAST:event_txt_MontoPago1FocusGained

    private void txt_MontoPago3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_MontoPago3FocusGained
        SwingUtilities.invokeLater(() -> {
            txt_MontoPago3.selectAll();
        });
    }//GEN-LAST:event_txt_MontoPago3FocusGained

    private void chk_FormaDePago1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FormaDePago1ItemStateChanged
        cmb_FormaDePago1.setEnabled(chk_FormaDePago1.isSelected());
        txt_MontoPago1.setEnabled(chk_FormaDePago1.isSelected());
    }//GEN-LAST:event_chk_FormaDePago1ItemStateChanged

    private void chk_FormaDePago2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FormaDePago2ItemStateChanged
        cmb_FormaDePago2.setEnabled(chk_FormaDePago2.isSelected());
        txt_MontoPago2.setEnabled(chk_FormaDePago2.isSelected());
    }//GEN-LAST:event_chk_FormaDePago2ItemStateChanged

    private void chk_FormaDePago3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FormaDePago3ItemStateChanged
        cmb_FormaDePago3.setEnabled(chk_FormaDePago3.isSelected());
        txt_MontoPago3.setEnabled(chk_FormaDePago3.isSelected());
    }//GEN-LAST:event_chk_FormaDePago3ItemStateChanged

    private void txt_MontoPago2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_MontoPago2FocusGained
        SwingUtilities.invokeLater(() -> {
            txt_MontoPago2.selectAll();
        });
    }//GEN-LAST:event_txt_MontoPago2FocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Finalizar;
    private javax.swing.JCheckBox chk_FormaDePago1;
    private javax.swing.JCheckBox chk_FormaDePago2;
    private javax.swing.JCheckBox chk_FormaDePago3;
    private javax.swing.JComboBox cmb_FormaDePago1;
    private javax.swing.JComboBox cmb_FormaDePago2;
    private javax.swing.JComboBox cmb_FormaDePago3;
    private javax.swing.JComboBox cmb_Transporte;
    private javax.swing.JLabel lbl_Dividido;
    private javax.swing.JLabel lbl_Transporte;
    private javax.swing.JLabel lbl_Vendedor;
    private javax.swing.JLabel lbl_Vendor;
    private javax.swing.JPanel panel;
    private javax.swing.JSeparator separador1;
    private javax.swing.JSeparator separador2;
    private javax.swing.JFormattedTextField txt_MontoPago1;
    private javax.swing.JFormattedTextField txt_MontoPago2;
    private javax.swing.JFormattedTextField txt_MontoPago3;
    // End of variables declaration//GEN-END:variables
}
