package sic.vista.swing;

import sic.modelo.TipoMovimiento;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.CuentaCorriente;
import sic.modelo.CuentaCorrienteCliente;
import sic.modelo.CuentaCorrienteProveedor;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.Nota;
import sic.modelo.NotaDebito;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Proveedor;
import sic.modelo.Recibo;
import sic.modelo.RenglonCuentaCorriente;
import sic.modelo.TipoDeComprobante;
import sic.util.ColoresNumerosTablaRenderer;
import sic.util.FormatterNumero;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class CuentaCorrienteGUI extends JInternalFrame {

    private final Cliente cliente;
    private final Proveedor proveedor;
    private CuentaCorriente cuentaCorriente;
    private final ModeloTabla modeloTablaResultados = new ModeloTabla();
    private static int NUMERO_PAGINA = 0;
    private static final int TAMANIO_PAGINA = 50;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final List<RenglonCuentaCorriente> movimientosTotal = new ArrayList<>();
    private List<RenglonCuentaCorriente> movimientosParcial = new ArrayList<>();
    private final Dimension sizeInternalFrame = new Dimension(880, 600);

    public CuentaCorrienteGUI(Cliente cliente) {
        this.initComponents();
        this.cliente = cliente;
        this.proveedor = null;
        this.setListeners();
    }
    
    public CuentaCorrienteGUI(Proveedor proveedor) {
        this.initComponents();
        this.cliente = null;
        this.proveedor = proveedor;
        this.setListeners();
    }
    
    private void setListeners() {
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 50;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (movimientosTotal.size() >= TAMANIO_PAGINA) {
                    NUMERO_PAGINA += 1;
                    buscar();
                }
            }
        });
        ftf_saldoFinal.addPropertyChangeListener("value", (PropertyChangeEvent e) -> {
            BigDecimal saldoFinal = new BigDecimal(ftf_saldoFinal.getValue().toString());
            if (Utilidades.truncarDecimal(saldoFinal, 2).compareTo(BigDecimal.ZERO) < 0) {
                ftf_saldoFinal.setBackground(Color.PINK);
            } else if (Utilidades.truncarDecimal(saldoFinal, 2).compareTo(BigDecimal.ZERO) > 0) {
                ftf_saldoFinal.setBackground(Color.GREEN);
            } else {
                ftf_saldoFinal.setBackground(Color.WHITE);
            }
        });
    }
    
    private void cambiarEstadoEnabledComponentes(boolean status) {
        if (cliente != null) {
            btnCrearNotaCredito.setEnabled(status);
            btnCrearNotaDebito.setEnabled(status);
            btnAutorizarNota.setEnabled(status);
        }
        btnVerDetalle.setEnabled(status);
        tbl_Resultados.requestFocus();
        sp_Resultados.setEnabled(status);
        btn_Eliminar.setEnabled(status);
        btn_VerPagos.setEnabled(status);
        btnRefresh.setEnabled(status);
    }

    private void buscar() {
        this.cambiarEstadoEnabledComponentes(false);
        try {
            PaginaRespuestaRest<RenglonCuentaCorriente> response = RestClient.getRestTemplate()
                    .exchange("/cuentas-corrientes/" + cuentaCorriente.getIdCuentaCorriente() + "/renglones"
                            + "?pagina=" + NUMERO_PAGINA + "&tamanio=" + TAMANIO_PAGINA,
                            HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<RenglonCuentaCorriente>>() {
                    })
                    .getBody();
            movimientosParcial = response.getContent();
            movimientosTotal.addAll(movimientosParcial);
            this.cargarSaldoAlInicioYAlFinal();
            this.cargarResultadosAlTable();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        this.cambiarEstadoEnabledComponentes(true);
    }

    private void cargarResultadosAlTable() {
        movimientosParcial.stream().map(r -> {
            Object[] renglonTabla = new Object[7];
            renglonTabla[0] = r.getFecha();
            renglonTabla[1] = r.getTipoComprobante() + " Nº " + r.getSerie() + " - " + r.getNumero();
            renglonTabla[2] = r.getFechaVencimiento();
            renglonTabla[3] = r.getCAE() == 0 ? "" : r.getCAE();
            renglonTabla[4] = r.getDescripcion();
            renglonTabla[5] = r.getMonto();
            renglonTabla[6] = r.getSaldo();
            return renglonTabla;
        }).forEachOrdered(renglonTabla -> {
            modeloTablaResultados.addRow(renglonTabla);
        });
        tbl_Resultados.setModel(modeloTablaResultados);
    }
    
    private void limpiarJTable() {
        modeloTablaResultados.setRowCount(0);
        tbl_Resultados.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[7];
        encabezados[0] = "Fecha";
        encabezados[1] = "Comprobante";
        encabezados[2] = "Vencimiento";
        encabezados[3] = "CAE";
        encabezados[4] = "Detalle";
        encabezados[5] = "Monto";
        encabezados[6] = "Saldo";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = Date.class;
        tipos[1] = String.class;
        tipos[2] = Date.class;
        tipos[3] = Object.class;
        tipos[4] = String.class;
        tipos[5] = BigDecimal.class;
        tipos[6] = BigDecimal.class;
        modeloTablaResultados.setClaseColumnas(tipos);        
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);

        //Tamanios de columnas
        tbl_Resultados.setDefaultRenderer(BigDecimal.class, new RenderTabla());
        tbl_Resultados.getColumnModel().getColumn(6).setCellRenderer(new ColoresNumerosTablaRenderer());
        tbl_Resultados.getColumnModel().getColumn(0).setMinWidth(100);
        tbl_Resultados.getColumnModel().getColumn(0).setMaxWidth(100);
        tbl_Resultados.getColumnModel().getColumn(1).setMinWidth(200);
        tbl_Resultados.getColumnModel().getColumn(1).setMaxWidth(200);
        tbl_Resultados.getColumnModel().getColumn(2).setMinWidth(100);
        tbl_Resultados.getColumnModel().getColumn(2).setMaxWidth(100);
        tbl_Resultados.getColumnModel().getColumn(3).setMinWidth(120);
        tbl_Resultados.getColumnModel().getColumn(3).setMaxWidth(120);
        tbl_Resultados.getColumnModel().getColumn(5).setMinWidth(100);
        tbl_Resultados.getColumnModel().getColumn(5).setMaxWidth(100);
        tbl_Resultados.getColumnModel().getColumn(6).setMinWidth(100);
        tbl_Resultados.getColumnModel().getColumn(6).setMaxWidth(100);
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        movimientosTotal.clear();
        movimientosParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }
    
    private void refrescarVista() {
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }

    private void cargarSaldoAlInicioYAlFinal() {
        try {
            if (cliente != null) {
                ftf_saldoFinal.setValue(RestClient.getRestTemplate()
                        .getForObject("/cuentas-corrientes/clientes/" + cliente.getId_Cliente() + "/saldo",
                                BigDecimal.class));
                ftf_saldoFinal.setText(FormatterNumero.formatConRedondeo((Number) ftf_saldoFinal.getValue()));
            } else if (proveedor != null) {
                ftf_saldoFinal.setValue(RestClient.getRestTemplate()
                        .getForObject("/cuentas-corrientes/proveedores/" + proveedor.getId_Proveedor()+ "/saldo",
                                BigDecimal.class));
                ftf_saldoFinal.setText(FormatterNumero.formatConRedondeo((Number) ftf_saldoFinal.getValue()));
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
    
    private void cargarDetalleCliente() {
        this.setTitle("Cuenta Corriente del Cliente: " + cliente.getRazonSocial());
        txtNombreCliente.setText(cliente.getRazonSocial());
        txtDomicilioCliente.setText(cliente.getDireccion()
                + " " + cliente.getLocalidad().getNombre()
                + " " + cliente.getLocalidad().getProvincia().getNombre()
                + " " + cliente.getLocalidad().getProvincia().getPais());
        txtIDFiscalCliente.setText(cliente.getIdFiscal());
        txtCondicionIVACliente.setText(cliente.getCondicionIVA().getNombre());
        try {
            cuentaCorriente = RestClient.getRestTemplate()
                    .getForObject("/cuentas-corrientes/clientes/" + cliente.getId_Cliente(), CuentaCorrienteCliente.class);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarDetalleProveedor() {
        this.setTitle("Cuenta Corriente del Proveedor: " + proveedor.getRazonSocial());
        txtNombreCliente.setText(proveedor.getRazonSocial());
        txtDomicilioCliente.setText(proveedor.getDireccion() 
                + " " + proveedor.getLocalidad().getNombre() 
                + " " + proveedor.getLocalidad().getProvincia().getNombre() 
                + " " + proveedor.getLocalidad().getProvincia().getPais());
        txtIDFiscalCliente.setText(proveedor.getIdFiscal());
        txtCondicionIVACliente.setText(proveedor.getCondicionIVA().getNombre());
        try {
            cuentaCorriente = RestClient.getRestTemplate()
                    .getForObject("/cuentas-corrientes/proveedores/" + proveedor.getId_Proveedor(), CuentaCorrienteProveedor.class);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verDetalleCliente(RenglonCuentaCorriente renglonCC) {
        try {
            if (renglonCC.getTipoComprobante() == null) {
                JOptionPane.showInternalMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                switch (renglonCC.getTipoComprobante()) {
                    case NOTA_DEBITO_A:
                    case NOTA_DEBITO_B:
                    case NOTA_DEBITO_PRESUPUESTO:
                    case NOTA_DEBITO_X:
                    case NOTA_DEBITO_Y:
                        if (Desktop.isDesktopSupported()) {
                            byte[] reporte = RestClient.getRestTemplate()
                                    .getForObject("/notas/" + renglonCC.getIdMovimiento() + "/reporte", byte[].class);
                            File f = new File(System.getProperty("user.home") + "/NotaDebito.pdf");
                            Files.write(f.toPath(), reporte);
                            Desktop.getDesktop().open(f);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case NOTA_CREDITO_A:
                    case NOTA_CREDITO_B:
                    case NOTA_CREDITO_PRESUPUESTO:
                    case NOTA_CREDITO_X:
                    case NOTA_CREDITO_Y:
                        if (Desktop.isDesktopSupported()) {
                            byte[] reporte = RestClient.getRestTemplate()
                                    .getForObject("/notas/" + renglonCC.getIdMovimiento() + "/reporte", byte[].class);
                            File f = new File(System.getProperty("user.home") + "/NotaCredito.pdf");
                            Files.write(f.toPath(), reporte);
                            Desktop.getDesktop().open(f);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case FACTURA_A:
                    case FACTURA_B:
                    case FACTURA_C:
                    case FACTURA_X:
                    case FACTURA_Y:
                    case PRESUPUESTO:
                        if (Desktop.isDesktopSupported()) {
                            byte[] reporte = RestClient.getRestTemplate()
                                    .getForObject("/facturas/" + renglonCC.getIdMovimiento() + "/reporte", byte[].class);
                            File f = new File(System.getProperty("user.home") + "/Factura.pdf");
                            Files.write(f.toPath(), reporte);
                            Desktop.getDesktop().open(f);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case RECIBO:
                        if (Desktop.isDesktopSupported()) {
                            try {
                                byte[] reporte = RestClient.getRestTemplate()
                                        .getForObject("/recibos/" + renglonCC.getIdMovimiento() + "/reporte", byte[].class);
                                File f = new File(System.getProperty("user.home") + "/Recibo.pdf");
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
                        break;
                    default:
                        JOptionPane.showInternalMessageDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verDetalleProveedor(RenglonCuentaCorriente renglonCC) {
        if (renglonCC.getTipoComprobante() == null) {
            JOptionPane.showInternalMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            switch (renglonCC.getTipoComprobante()) {
                case FACTURA_A:
                case FACTURA_B:
                case FACTURA_C:
                case FACTURA_X:
                case FACTURA_Y:
                    if (Desktop.isDesktopSupported()) {
                        JInternalFrame gui = new DetalleFacturaCompraGUI(RestClient.getRestTemplate().getForObject("/facturas/" + renglonCC.getIdMovimiento(), FacturaCompra.class));
                        gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                                getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
                        getDesktopPane().add(gui);
                        gui.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                default:
                    JOptionPane.showInternalMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btnCrearNotaCredito = new javax.swing.JButton();
        btnVerDetalle = new javax.swing.JButton();
        btnAutorizarNota = new javax.swing.JButton();
        lbl_saldoFinal = new javax.swing.JLabel();
        ftf_saldoFinal = new javax.swing.JFormattedTextField();
        btnCrearNotaDebito = new javax.swing.JButton();
        btn_VerPagos = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        btnCrearRecibo = new javax.swing.JButton();
        txtCondicionIVACliente = new javax.swing.JTextField();
        lblCondicionIVACliente = new javax.swing.JLabel();
        txtIDFiscalCliente = new javax.swing.JTextField();
        lblIDFiscalCliente = new javax.swing.JLabel();
        lblDomicilioCliente = new javax.swing.JLabel();
        lblNombreCliente = new javax.swing.JLabel();
        txtNombreCliente = new javax.swing.JTextField();
        txtDomicilioCliente = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CC_16x16.png"))); // NOI18N
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

        pnlResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Movimientos"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Resultados.setViewportView(tbl_Resultados);

        btnCrearNotaCredito.setForeground(java.awt.Color.blue);
        btnCrearNotaCredito.setText("Nueva Nota Credito");
        btnCrearNotaCredito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearNotaCreditoActionPerformed(evt);
            }
        });

        btnVerDetalle.setForeground(java.awt.Color.blue);
        btnVerDetalle.setText("Ver Detalle");
        btnVerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDetalleActionPerformed(evt);
            }
        });

        btnAutorizarNota.setForeground(java.awt.Color.blue);
        btnAutorizarNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Certificate_16x16.png"))); // NOI18N
        btnAutorizarNota.setText("Autorizar Nota");
        btnAutorizarNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutorizarNotaActionPerformed(evt);
            }
        });

        lbl_saldoFinal.setText("Saldo:");

        ftf_saldoFinal.setEditable(false);
        ftf_saldoFinal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        ftf_saldoFinal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftf_saldoFinal.setFocusable(false);
        ftf_saldoFinal.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        btnCrearNotaDebito.setForeground(java.awt.Color.blue);
        btnCrearNotaDebito.setText("Nueva Nota Debito");
        btnCrearNotaDebito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearNotaDebitoActionPerformed(evt);
            }
        });

        btn_VerPagos.setForeground(java.awt.Color.blue);
        btn_VerPagos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/StampArrow_16x16.png"))); // NOI18N
        btn_VerPagos.setText("Ver Pagos");
        btn_VerPagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerPagosActionPerformed(evt);
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

        btnCrearRecibo.setForeground(java.awt.Color.blue);
        btnCrearRecibo.setText("Nuevo Recibo");
        btnCrearRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearReciboActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlResultadosLayout = new javax.swing.GroupLayout(pnlResultados);
        pnlResultados.setLayout(pnlResultadosLayout);
        pnlResultadosLayout.setHorizontalGroup(
            pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultados, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(pnlResultadosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_saldoFinal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ftf_saldoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(pnlResultadosLayout.createSequentialGroup()
                .addGroup(pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlResultadosLayout.createSequentialGroup()
                        .addComponent(btnAutorizarNota)
                        .addGap(0, 0, 0)
                        .addComponent(btnVerDetalle)
                        .addGap(0, 0, 0)
                        .addComponent(btn_VerPagos))
                    .addGroup(pnlResultadosLayout.createSequentialGroup()
                        .addComponent(btnCrearNotaCredito)
                        .addGap(0, 0, 0)
                        .addComponent(btnCrearNotaDebito)
                        .addGap(0, 0, 0)
                        .addComponent(btnCrearRecibo)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Eliminar)))
                .addContainerGap(99, Short.MAX_VALUE))
        );

        pnlResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAutorizarNota, btnCrearNotaCredito, btnCrearNotaDebito, btnCrearRecibo, btnVerDetalle, btn_Eliminar, btn_VerPagos});

        pnlResultadosLayout.setVerticalGroup(
            pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlResultadosLayout.createSequentialGroup()
                .addGroup(pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ftf_saldoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_saldoFinal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAutorizarNota, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerDetalle)
                    .addComponent(btn_VerPagos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnCrearNotaCredito)
                    .addComponent(btnCrearNotaDebito)
                    .addComponent(btn_Eliminar)
                    .addComponent(btnCrearRecibo)))
        );

        pnlResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAutorizarNota, btnCrearNotaCredito, btnCrearNotaDebito, btnCrearRecibo, btnVerDetalle, btn_Eliminar, btn_VerPagos});

        txtCondicionIVACliente.setEditable(false);
        txtCondicionIVACliente.setFocusable(false);

        lblCondicionIVACliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCondicionIVACliente.setText("Condición IVA:");

        txtIDFiscalCliente.setEditable(false);
        txtIDFiscalCliente.setFocusable(false);

        lblIDFiscalCliente.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIDFiscalCliente.setText("ID Fiscal:");

        lblDomicilioCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDomicilioCliente.setText("Domicilio:");

        lblNombreCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNombreCliente.setText("Nombre:");

        txtNombreCliente.setEditable(false);
        txtNombreCliente.setFocusable(false);

        txtDomicilioCliente.setEditable(false);
        txtDomicilioCliente.setFocusable(false);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Refresh_16x16.png"))); // NOI18N
        btnRefresh.setFocusable(false);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnRefresh))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblCondicionIVACliente, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblDomicilioCliente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtCondicionIVACliente)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblIDFiscalCliente)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIDFiscalCliente))
                            .addComponent(txtDomicilioCliente)
                            .addComponent(txtNombreCliente, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
            .addComponent(pnlResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNombreCliente)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDomicilioCliente)
                    .addComponent(txtDomicilioCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCondicionIVACliente)
                    .addComponent(txtCondicionIVACliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIDFiscalCliente)
                    .addComponent(txtIDFiscalCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        if (cliente != null) {
            this.cargarDetalleCliente();
        } else if (proveedor != null) {
            this.cargarDetalleProveedor();
            this.btnAutorizarNota.setVisible(false);
            this.btnCrearNotaCredito.setVisible(false);
            this.btnCrearNotaDebito.setVisible(false);
        }
        this.setColumnas();
        this.setSize(sizeInternalFrame);
        try {
            this.setMaximum(true);
            this.refrescarVista();
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnCrearNotaCreditoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearNotaCreditoActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            RenglonCuentaCorriente renglonCC = movimientosTotal.get(indexFilaSeleccionada);
            if (renglonCC.getTipoComprobante() == TipoDeComprobante.FACTURA_A || renglonCC.getTipoComprobante() == TipoDeComprobante.FACTURA_B 
                    || renglonCC.getTipoComprobante() == TipoDeComprobante.FACTURA_C || renglonCC.getTipoComprobante() == TipoDeComprobante.FACTURA_X
                    || renglonCC.getTipoComprobante() == TipoDeComprobante.FACTURA_Y || renglonCC.getTipoComprobante() == TipoDeComprobante.PRESUPUESTO) {
                SeleccionDeProductosGUI seleccionDeProductosGUI = new SeleccionDeProductosGUI(renglonCC.getIdMovimiento(), TipoMovimiento.CREDITO);
                seleccionDeProductosGUI.setModal(true);
                seleccionDeProductosGUI.setLocationRelativeTo(this);
                seleccionDeProductosGUI.setVisible(true);
                if (!seleccionDeProductosGUI.getRenglonesConCantidadNueva().isEmpty()) {
                    DetalleNotaCreditoGUI detalleNotaCredito = new DetalleNotaCreditoGUI(
                            seleccionDeProductosGUI.getRenglonesConCantidadNueva(),
                            seleccionDeProductosGUI.getIdFactura(), seleccionDeProductosGUI.modificarStock(),
                            this.cliente.getId_Cliente());
                    detalleNotaCredito.setModal(true);
                    detalleNotaCredito.setLocationRelativeTo(this);
                    detalleNotaCredito.setVisible(true);
                    if (detalleNotaCredito.isNotaCreada()) this.refrescarVista();                    
                }
            } else {
                JOptionPane.showInternalMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnCrearNotaCreditoActionPerformed

    private void btnCrearNotaDebitoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearNotaDebitoActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            RenglonCuentaCorriente renglonCC = movimientosTotal.get(indexFilaSeleccionada);
            if (renglonCC.getTipoComprobante() == TipoDeComprobante.RECIBO) {
                if (RestClient.getRestTemplate().getForObject("/notas/debito/recibo/" + renglonCC.getIdMovimiento() + "/existe", boolean.class)) {
                    JOptionPane.showInternalMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_recibo_con_nota_debito"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    DetalleNotaDebitoGUI detalleNotaDebitoGUI = new DetalleNotaDebitoGUI(cliente.getId_Cliente(), renglonCC.getIdMovimiento());
                    detalleNotaDebitoGUI.setLocationRelativeTo(this);
                    detalleNotaDebitoGUI.setVisible(true);
                    if (detalleNotaDebitoGUI.isNotaDebitoCreada()) this.refrescarVista();
                }
            } else {
                JOptionPane.showInternalMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnCrearNotaDebitoActionPerformed

    private void btnVerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetalleActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            RenglonCuentaCorriente renglonCC = movimientosTotal.get(indexFilaSeleccionada);
            if (cuentaCorriente instanceof CuentaCorrienteCliente) {
                this.verDetalleCliente(renglonCC);
            } else if (cuentaCorriente instanceof CuentaCorrienteProveedor) {
                this.verDetalleProveedor(renglonCC);
            }
        }
    }//GEN-LAST:event_btnVerDetalleActionPerformed
    
    private void btnAutorizarNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutorizarNotaActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            RenglonCuentaCorriente renglonCC = movimientosTotal.get(indexFilaSeleccionada);
            if (renglonCC.getTipoComprobante() == TipoDeComprobante.NOTA_CREDITO_A || renglonCC.getTipoComprobante() == TipoDeComprobante.NOTA_CREDITO_B
                    || renglonCC.getTipoComprobante() == TipoDeComprobante.NOTA_CREDITO_PRESUPUESTO || renglonCC.getTipoComprobante() == TipoDeComprobante.NOTA_CREDITO_X
                    || renglonCC.getTipoComprobante() == TipoDeComprobante.NOTA_CREDITO_Y || renglonCC.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_A
                    || renglonCC.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_B || renglonCC.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_PRESUPUESTO
                    || renglonCC.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_X || renglonCC.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_Y) {
                try {
                    RestClient.getRestTemplate().postForObject("/notas/" + renglonCC.getIdMovimiento() + "/autorizacion",
                            null, Nota.class);
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_nota_autorizada"),
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    this.refrescarVista();
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showInternalMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnAutorizarNotaActionPerformed

    private void btn_VerPagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerPagosActionPerformed
        try {
            if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
                boolean refrescar = false;
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                RenglonCuentaCorriente renglonCC = movimientosTotal.get(indexFilaSeleccionada);
                if (null == renglonCC.getTipoComprobante()) {
                    JOptionPane.showInternalMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    switch (renglonCC.getTipoComprobante()) {
                        case FACTURA_A:
                        case FACTURA_B:
                        case FACTURA_C:
                        case FACTURA_X:
                        case FACTURA_Y:
                        case PRESUPUESTO: {
                            JInternalFrame gui = new PagosGUI(RestClient.getRestTemplate().getForObject("/facturas/" + renglonCC.getIdMovimiento(), Factura.class));
                            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
                            getDesktopPane().add(gui);
                            gui.setVisible(true);
                            break;
                        }
                        case NOTA_DEBITO_A:
                        case NOTA_DEBITO_B:
                        case NOTA_DEBITO_X:
                        case NOTA_DEBITO_Y:
                        case NOTA_DEBITO_PRESUPUESTO: {
                            JInternalFrame gui = new PagosGUI(RestClient.getRestTemplate().getForObject("/notas/" + renglonCC.getIdMovimiento(), NotaDebito.class));
                            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
                            getDesktopPane().add(gui);
                            gui.setVisible(true);
                            break;
                        }
                        case RECIBO: {
                            JInternalFrame gui = new PagosGUI(RestClient.getRestTemplate().getForObject("/recibos/" + renglonCC.getIdMovimiento(), Recibo.class));
                            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
                            getDesktopPane().add(gui);
                            gui.setVisible(true);
                            break;
                        }
                        default:
                            JOptionPane.showInternalMessageDialog(this,
                                    ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                    }
                }
                this.refrescarVista();
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_VerPagosActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int respuesta = JOptionPane.showConfirmDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_eliminar_movimientos"),
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                RenglonCuentaCorriente renglonCC = movimientosTotal.get(indexFilaSeleccionada);
                boolean refrescar = false;
                try {
                    switch (renglonCC.getTipoComprobante()) {
                        case FACTURA_A:
                        case FACTURA_B:
                        case FACTURA_C:
                        case FACTURA_X:
                        case FACTURA_Y:
                        case PRESUPUESTO: {
                            RestClient.getRestTemplate().delete("/facturas?idFactura=" + renglonCC.getIdMovimiento());
                            refrescar = true;
                        }
                        break;
                        case RECIBO: {
                            RestClient.getRestTemplate().delete("/recibos/" + renglonCC.getIdMovimiento());
                            refrescar = true;
                        }
                        break;
                        case NOTA_CREDITO_A:
                        case NOTA_CREDITO_B:
                        case NOTA_CREDITO_PRESUPUESTO:
                        case NOTA_CREDITO_X:
                        case NOTA_CREDITO_Y:
                        case NOTA_DEBITO_A:
                        case NOTA_DEBITO_B:
                        case NOTA_DEBITO_PRESUPUESTO:
                        case NOTA_DEBITO_X:
                        case NOTA_DEBITO_Y: {
                            RestClient.getRestTemplate().delete("/notas?idsNota=" + renglonCC.getIdMovimiento());
                            refrescar = true;
                        }
                        break;
                        default:
                            JOptionPane.showInternalMessageDialog(this,
                                    ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            break;

                    }
                    if (refrescar) this.refrescarVista();                    
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
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        this.refrescarVista();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnCrearReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearReciboActionPerformed
        DetalleReciboGUI detalleComprobante;
        if (cliente != null) {
            detalleComprobante = new DetalleReciboGUI(cliente);
        } else {
            detalleComprobante = new DetalleReciboGUI(proveedor);
        }        
        detalleComprobante.setModal(true);
        detalleComprobante.setLocationRelativeTo(this);
        detalleComprobante.setVisible(true);
        this.refrescarVista();
    }//GEN-LAST:event_btnCrearReciboActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAutorizarNota;
    private javax.swing.JButton btnCrearNotaCredito;
    private javax.swing.JButton btnCrearNotaDebito;
    private javax.swing.JButton btnCrearRecibo;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnVerDetalle;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_VerPagos;
    private javax.swing.JFormattedTextField ftf_saldoFinal;
    private javax.swing.JLabel lblCondicionIVACliente;
    private javax.swing.JLabel lblDomicilioCliente;
    private javax.swing.JLabel lblIDFiscalCliente;
    private javax.swing.JLabel lblNombreCliente;
    private javax.swing.JLabel lbl_saldoFinal;
    private javax.swing.JPanel pnlResultados;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txtCondicionIVACliente;
    private javax.swing.JTextField txtDomicilioCliente;
    private javax.swing.JTextField txtIDFiscalCliente;
    private javax.swing.JTextField txtNombreCliente;
    // End of variables declaration//GEN-END:variables

}
