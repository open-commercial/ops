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
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.CuentaCorriente;
import sic.modelo.Factura;
import sic.modelo.Movimiento;
import sic.modelo.Nota;
import sic.modelo.NotaDebito;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.RenglonCuentaCorriente;
import sic.util.ColoresNumerosTablaRenderer;
import sic.util.FormatterNumero;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class CuentaCorrienteGUI extends JInternalFrame {

    private final Cliente cliente;
    private CuentaCorriente cuentaCorriente;
    private final ModeloTabla modeloTablaResultados = new ModeloTabla();
    private static int NUMERO_PAGINA = 0;
    private static final int TAMANIO_PAGINA = 100;
    private static int totalElementosBusqueda;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private List<RenglonCuentaCorriente> movimientosTotal = new ArrayList<>();
    private List<RenglonCuentaCorriente> movimientosParcial = new ArrayList<>();
    private final Dimension sizeInternalFrame = new Dimension(880, 600);

    public CuentaCorrienteGUI(Cliente cliente) {
        this.initComponents();
        this.cliente = cliente;
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
        ftf_saldoInicial.addPropertyChangeListener("value", (PropertyChangeEvent e) -> {
            if (Utilidades.truncarDecimal((Double) ftf_saldoInicial.getValue(), 2) < 0) {
                ftf_saldoInicial.setBackground(Color.PINK);
            } else if (Utilidades.truncarDecimal((Double) ftf_saldoInicial.getValue(), 2) > 0) {
                ftf_saldoInicial.setBackground(Color.GREEN);
            } else {
                ftf_saldoInicial.setBackground(Color.WHITE);
            }
        });
        ftf_saldoFinal.addPropertyChangeListener("value", (PropertyChangeEvent e) -> {
            if (Utilidades.truncarDecimal((Double) ftf_saldoFinal.getValue(), 2) < 0) {
                ftf_saldoFinal.setBackground(Color.PINK);
            } else if (Utilidades.truncarDecimal((Double) ftf_saldoFinal.getValue(), 2) > 0) {
                ftf_saldoFinal.setBackground(Color.GREEN);
            } else {
                ftf_saldoFinal.setBackground(Color.WHITE);
            }
        });
    }
    
    private void cambiarEstadoEnabledComponentes(boolean status) {
        dcFechaDesde.setEnabled(status);        
        dcFechaHasta.setEnabled(status);
        btnBuscar.setEnabled(status);
        btnCrearNotaCredito.setEnabled(status);
        btnCrearNotaDebito.setEnabled(status);
        btnVerDetalle.setEnabled(status);
        btnAutorizarNota.setEnabled(status);
        tbl_Resultados.requestFocus();
        sp_Resultados.setEnabled(status);
        btn_Eliminar.setEnabled(status);
        btn_VerPagos.setEnabled(status);
    }

    private void buscar() {
        cambiarEstadoEnabledComponentes(false);
        pb_Filtro.setIndeterminate(true);
        SwingWorker<List<RenglonCuentaCorriente>, Void> worker = new SwingWorker<List<RenglonCuentaCorriente>, Void>() {
            
            @Override
            protected List<RenglonCuentaCorriente> doInBackground() throws Exception {
                PaginaRespuestaRest<RenglonCuentaCorriente> response = RestClient.getRestTemplate()
                    .exchange("/cuentas-corrientes/" + cuentaCorriente.getIdCuentaCorriente() + "/renglones"
                            + "?desde=" + dcFechaDesde.getDate().getTime()
                            + "&hasta=" + dcFechaHasta.getDate().getTime()
                            + "&pagina=" + NUMERO_PAGINA
                            + "&tamanio=" + TAMANIO_PAGINA,
                            HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<RenglonCuentaCorriente>>() {
                    })
                    .getBody();
                totalElementosBusqueda = response.getTotalElements();
                return response.getContent();
            }
            
            @Override
            protected void done() {
                pb_Filtro.setIndeterminate(false);
                try {
                    movimientosParcial = get();
                    movimientosTotal.addAll(movimientosParcial);
                    lblResultados.setText(totalElementosBusqueda + " movimientos encontrados");                   
                    cargarSaldoAlInicioYAlFinal();
                    cargarResultadosAlTable();                                        
                } catch (InterruptedException ex) {
                    String msjError = "La tarea que se estaba realizando fue interrumpida. Intente nuevamente.";
                    LOGGER.error(msjError + " - " + ex.getMessage());
                    JOptionPane.showInternalMessageDialog(getParent(), msjError, "Error", JOptionPane.ERROR_MESSAGE);                    
                } catch (ExecutionException ex) {
                    if (ex.getCause() instanceof RestClientResponseException) {
                        JOptionPane.showMessageDialog(getParent(), ex.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (ex.getCause() instanceof ResourceAccessException) {
                        LOGGER.error(ex.getMessage());
                        JOptionPane.showMessageDialog(getParent(),
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        String msjError = "Se produjo un error en la ejecución de la tarea solicitada. Intente nuevamente.";
                        LOGGER.error(msjError + " - " + ex.getMessage());
                        JOptionPane.showInternalMessageDialog(getParent(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
                    }                    
                }
                cambiarEstadoEnabledComponentes(true);
            }
        };                
        worker.execute();
    }

    private void cargarResultadosAlTable() {
        movimientosParcial.stream().map(r -> {
            Object[] renglonTabla = new Object[7];
            renglonTabla[0] = r.getFecha();
            renglonTabla[1] = r.getComprobante();
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
        tipos[5] = Double.class;
        tipos[6] = Double.class;
        modeloTablaResultados.setClaseColumnas(tipos);        
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);

        //Tamanios de columnas
        tbl_Resultados.setDefaultRenderer(Double.class, new RenderTabla());
        tbl_Resultados.getColumnModel().getColumn(6).setCellRenderer(new ColoresNumerosTablaRenderer());
        tbl_Resultados.getColumnModel().getColumn(0).setMinWidth(100);
        tbl_Resultados.getColumnModel().getColumn(0).setMaxWidth(100);
        tbl_Resultados.getColumnModel().getColumn(1).setMinWidth(180);
        tbl_Resultados.getColumnModel().getColumn(1).setMaxWidth(180);
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
    
    private void refrescarVista(boolean refrescar) {
        if (refrescar) {
            this.resetScroll();
            this.limpiarJTable();
            this.buscar();
        }
    }

    private void cargarSaldoAlInicioYAlFinal() {
        try {
            ftf_saldoInicial.setValue(RestClient.getRestTemplate()
                    .getForObject("/cuentas-corrientes/clientes/" + cliente.getId_Cliente()
                            + "/saldo?hasta=" + dcFechaDesde.getDate().getTime() + "&limiteDerecho=false",
                            double.class));
            ftf_saldoInicial.setText(FormatterNumero.formatConRedondeo((Number) ftf_saldoInicial.getValue()));
            ftf_saldoFinal.setValue(RestClient.getRestTemplate()
                    .getForObject("/cuentas-corrientes/clientes/" + cliente.getId_Cliente()
                            + "/saldo?hasta=" + dcFechaHasta.getDate().getTime(),
                            double.class));
            ftf_saldoFinal.setText(FormatterNumero.formatConRedondeo((Number) ftf_saldoFinal.getValue()));
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

        pnl_cabecera = new javax.swing.JPanel();
        dcFechaDesde = new com.toedter.calendar.JDateChooser();
        lbl_Desde = new javax.swing.JLabel();
        lbl_Hasta = new javax.swing.JLabel();
        dcFechaHasta = new com.toedter.calendar.JDateChooser();
        btnBuscar = new javax.swing.JButton();
        pb_Filtro = new javax.swing.JProgressBar();
        lblResultados = new javax.swing.JLabel();
        pnlResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btnCrearNotaCredito = new javax.swing.JButton();
        btnVerDetalle = new javax.swing.JButton();
        btnAutorizarNota = new javax.swing.JButton();
        lbl_saldoFinal = new javax.swing.JLabel();
        ftf_saldoFinal = new javax.swing.JFormattedTextField();
        lbl_saldoInicial = new javax.swing.JLabel();
        ftf_saldoInicial = new javax.swing.JFormattedTextField();
        btnCrearNotaDebito = new javax.swing.JButton();
        btn_VerPagos = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();

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

        pnl_cabecera.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        dcFechaDesde.setDateFormatString("dd/MM/yyyy");

        lbl_Desde.setText("Desde:");

        lbl_Hasta.setText("Hasta:");

        dcFechaHasta.setDateFormatString("dd/MM/yyyy");

        btnBuscar.setForeground(java.awt.Color.blue);
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        lblResultados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout pnl_cabeceraLayout = new javax.swing.GroupLayout(pnl_cabecera);
        pnl_cabecera.setLayout(pnl_cabeceraLayout);
        pnl_cabeceraLayout.setHorizontalGroup(
            pnl_cabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_cabeceraLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_cabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_cabeceraLayout.createSequentialGroup()
                        .addComponent(lbl_Desde)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dcFechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_Hasta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dcFechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnl_cabeceraLayout.createSequentialGroup()
                        .addComponent(btnBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pb_Filtro, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pnl_cabeceraLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {dcFechaDesde, dcFechaHasta});

        pnl_cabeceraLayout.setVerticalGroup(
            pnl_cabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_cabeceraLayout.createSequentialGroup()
                .addGroup(pnl_cabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Desde)
                    .addComponent(dcFechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Hasta)
                    .addComponent(dcFechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_cabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnBuscar)
                    .addComponent(lblResultados, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pb_Filtro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        pnl_cabeceraLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {dcFechaDesde, dcFechaHasta});

        pnlResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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

        lbl_saldoFinal.setText("Saldo al Final:");

        ftf_saldoFinal.setEditable(false);
        ftf_saldoFinal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        ftf_saldoFinal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftf_saldoFinal.setFocusable(false);
        ftf_saldoFinal.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        lbl_saldoInicial.setText("Saldo al Inicio:");

        ftf_saldoInicial.setEditable(false);
        ftf_saldoInicial.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        ftf_saldoInicial.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftf_saldoInicial.setFocusable(false);
        ftf_saldoInicial.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        btnCrearNotaDebito.setForeground(java.awt.Color.blue);
        btnCrearNotaDebito.setText("Nueva Nota Debito");
        btnCrearNotaDebito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearNotaDebitoActionPerformed(evt);
            }
        });

        btn_VerPagos.setForeground(java.awt.Color.blue);
        btn_VerPagos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/StampArrow_16x16.png"))); // NOI18N
        btn_VerPagos.setText("Pagos");
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

        javax.swing.GroupLayout pnlResultadosLayout = new javax.swing.GroupLayout(pnlResultados);
        pnlResultados.setLayout(pnlResultadosLayout);
        pnlResultadosLayout.setHorizontalGroup(
            pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlResultadosLayout.createSequentialGroup()
                .addComponent(btnCrearNotaCredito)
                .addGap(0, 0, 0)
                .addComponent(btnCrearNotaDebito)
                .addGap(0, 0, 0)
                .addComponent(btn_Eliminar)
                .addGap(0, 0, 0)
                .addComponent(btn_VerPagos)
                .addGap(0, 222, Short.MAX_VALUE))
            .addComponent(sp_Resultados)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlResultadosLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lbl_saldoInicial)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ftf_saldoInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlResultadosLayout.createSequentialGroup()
                .addComponent(btnAutorizarNota)
                .addGap(0, 0, 0)
                .addComponent(btnVerDetalle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_saldoFinal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ftf_saldoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pnlResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAutorizarNota, btnCrearNotaCredito, btnCrearNotaDebito, btnVerDetalle, btn_Eliminar, btn_VerPagos});

        pnlResultadosLayout.setVerticalGroup(
            pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlResultadosLayout.createSequentialGroup()
                .addGroup(pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ftf_saldoInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_saldoInicial))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_saldoFinal)
                    .addComponent(ftf_saldoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAutorizarNota, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerDetalle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnCrearNotaCredito)
                    .addComponent(btnCrearNotaDebito)
                    .addComponent(btn_VerPagos)
                    .addComponent(btn_Eliminar)))
        );

        pnlResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAutorizarNota, btnCrearNotaCredito, btnCrearNotaDebito, btnVerDetalle, btn_Eliminar, btn_VerPagos});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_cabecera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_cabecera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.setTitle("Cuenta Corriente del Cliente: " + cliente.getRazonSocial());
        this.setColumnas();
        this.setSize(sizeInternalFrame);
        LocalDateTime hasta = LocalDateTime.now();
        LocalDateTime desde = hasta.minusDays(30);
        dcFechaDesde.setDate(Date.from(desde.atZone(ZoneId.systemDefault()).toInstant()));
        dcFechaHasta.setDate(new Date());        
        try {
            cuentaCorriente = RestClient.getRestTemplate()
                    .getForObject("/cuentas-corrientes/cliente/" + cliente.getId_Cliente(), CuentaCorriente.class);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }        
        try {
            this.setMaximum(true);
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        refrescarVista(true);
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnCrearNotaCreditoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearNotaCreditoActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            RenglonCuentaCorriente renglonCC = movimientosTotal.get(indexFilaSeleccionada);
            if (renglonCC.getTipoMovimiento() == TipoMovimiento.VENTA) {
                SeleccionDeProductosGUI seleccionDeProductosGUI = new SeleccionDeProductosGUI(renglonCC.getIdMovimiento(), TipoMovimiento.CREDITO);
                seleccionDeProductosGUI.setModal(true);
                seleccionDeProductosGUI.setLocationRelativeTo(this);
                seleccionDeProductosGUI.setVisible(true);
                if (!seleccionDeProductosGUI.getRenglonesConCantidadNueva().isEmpty()) {
                    DetalleNotaCreditoGUI detalleNotaCredito = new DetalleNotaCreditoGUI(
                            seleccionDeProductosGUI.getRenglonesConCantidadNueva(),
                            seleccionDeProductosGUI.getIdFactura(), seleccionDeProductosGUI.modificarStock());
                    detalleNotaCredito.setModal(true);
                    detalleNotaCredito.setLocationRelativeTo(this);
                    detalleNotaCredito.setVisible(true);
                    refrescarVista(detalleNotaCredito.isNotaCreada());
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
            if (renglonCC.getTipoMovimiento() == TipoMovimiento.PAGO) {
                if (RestClient.getRestTemplate().getForObject("/notas/debito/" + renglonCC.getIdMovimiento(), NotaDebito.class) != null) {
                    JOptionPane.showInternalMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_pago_con_nota_debito"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    DetalleNotaDebitoGUI detalleNotaDebitoGUI = new DetalleNotaDebitoGUI(cliente.getId_Cliente(), renglonCC.getIdMovimiento());
                    detalleNotaDebitoGUI.setLocationRelativeTo(this);
                    detalleNotaDebitoGUI.setVisible(true);
                    refrescarVista(detalleNotaDebitoGUI.isNotaDebitoCreada());
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
            try {
                if (renglonCC.getTipoMovimiento() == null) {
                    JOptionPane.showInternalMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    switch (renglonCC.getTipoMovimiento()) {
                        case DEBITO:
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
                        case CREDITO:
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
                        case VENTA:
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
    }//GEN-LAST:event_btnVerDetalleActionPerformed

    private void btnAutorizarNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutorizarNotaActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            RenglonCuentaCorriente renglonCC = movimientosTotal.get(indexFilaSeleccionada);
            if (renglonCC.getTipoMovimiento() == TipoMovimiento.CREDITO || renglonCC.getTipoMovimiento() == TipoMovimiento.DEBITO) {
                try {
                    RestClient.getRestTemplate().postForObject("/notas/" + renglonCC.getIdMovimiento() + "/autorizacion",
                            null, Nota.class);
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_nota_autorizada"),
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    refrescarVista(true);
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
            if (tbl_Resultados.getSelectedRow() != -1) {
                if (tbl_Resultados.getSelectedRowCount() == 1) {
                                    boolean refrescar = false;
                    int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                    RenglonCuentaCorriente renglonCC = movimientosTotal.get(indexFilaSeleccionada);
                    if (null == renglonCC.getTipoMovimiento()) {
                        JOptionPane.showInternalMessageDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else switch (renglonCC.getTipoMovimiento()) {
                        case VENTA:
                            {
                                PagosGUI gui_Pagos = new PagosGUI(RestClient.getRestTemplate().getForObject("/facturas/" + renglonCC.getIdMovimiento(), Factura.class));
                                gui_Pagos.setModal(true);
                                gui_Pagos.setLocationRelativeTo(this);
                                gui_Pagos.setVisible(true);
                                refrescar = gui_Pagos.isPagosCreados();
                                break;
                            }
                        case DEBITO:
                            {
                                PagosGUI gui_Pagos = new PagosGUI(RestClient.getRestTemplate().getForObject("/notas/" + renglonCC.getIdMovimiento(), NotaDebito.class));
                                gui_Pagos.setModal(true);
                                gui_Pagos.setLocationRelativeTo(this);
                                gui_Pagos.setVisible(true);
                                refrescar = gui_Pagos.isPagosCreados();
                                break;
                            }
                        default:
                            JOptionPane.showInternalMessageDialog(this,
                                    ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                    }
                    refrescarVista(refrescar);
                }
                if (tbl_Resultados.getSelectedRowCount() > 1) {
                    int[] indicesTabla = Utilidades.getSelectedRowsModelIndices(tbl_Resultados);
                    long[] idsFacturas = new long[indicesTabla.length];
                    boolean todosMovimientosDeVenta = true;
                    for (int i = 0; i < indicesTabla.length; i++) {
                        if(movimientosTotal.get(indicesTabla[i]).getTipoMovimiento() != TipoMovimiento.VENTA) {
                            todosMovimientosDeVenta = false;
                            break;
                        }
                        idsFacturas[i] = movimientosTotal.get(indicesTabla[i]).getIdMovimiento();
                    }
                    if (todosMovimientosDeVenta) {
                        String uri = "/facturas/validaciones-pago-multiple?"
                                + "idFactura=" + Arrays.toString(idsFacturas).substring(1, Arrays.toString(idsFacturas).length() - 1)
                                + "&movimiento=" + Movimiento.VENTA;
                        boolean esValido = RestClient.getRestTemplate().getForObject(uri, boolean.class);
                        if (esValido) {
                            PagoMultiplesFacturasGUI gui_PagoMultiples = new PagoMultiplesFacturasGUI(this, idsFacturas, Movimiento.VENTA);
                            gui_PagoMultiples.setModal(true);
                            gui_PagoMultiples.setLocationRelativeTo(this);
                            gui_PagoMultiples.setVisible(true);
                            refrescarVista(gui_PagoMultiples.isPagosCreados());
                        }
                    } else {
                        JOptionPane.showInternalMessageDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
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
                    if (renglonCC.getTipoMovimiento() == TipoMovimiento.VENTA) {
                        RestClient.getRestTemplate().delete("/facturas?idFactura=" + renglonCC.getIdMovimiento());
                        refrescar = true;
                    }
                    if (renglonCC.getTipoMovimiento() == TipoMovimiento.PAGO) {
                        RestClient.getRestTemplate().delete("/pagos/" + renglonCC.getIdMovimiento());
                        refrescar = true;
                    }
                    if (renglonCC.getTipoMovimiento() == TipoMovimiento.CREDITO || renglonCC.getTipoMovimiento() == TipoMovimiento.DEBITO) {
                        RestClient.getRestTemplate().delete("/notas?idsNota=" + renglonCC.getIdMovimiento());
                        refrescar = true;
                    }
                    refrescarVista(refrescar);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAutorizarNota;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCrearNotaCredito;
    private javax.swing.JButton btnCrearNotaDebito;
    private javax.swing.JButton btnVerDetalle;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_VerPagos;
    private com.toedter.calendar.JDateChooser dcFechaDesde;
    private com.toedter.calendar.JDateChooser dcFechaHasta;
    private javax.swing.JFormattedTextField ftf_saldoFinal;
    private javax.swing.JFormattedTextField ftf_saldoInicial;
    private javax.swing.JLabel lblResultados;
    private javax.swing.JLabel lbl_Desde;
    private javax.swing.JLabel lbl_Hasta;
    private javax.swing.JLabel lbl_saldoFinal;
    private javax.swing.JLabel lbl_saldoInicial;
    private javax.swing.JProgressBar pb_Filtro;
    private javax.swing.JPanel pnlResultados;
    private javax.swing.JPanel pnl_cabecera;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    // End of variables declaration//GEN-END:variables

}
