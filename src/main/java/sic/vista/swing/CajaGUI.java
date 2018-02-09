package sic.vista.swing;

import sic.modelo.TipoMovimiento;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Caja;
import sic.modelo.EmpresaActiva;
import sic.modelo.FormaDePago;
import sic.modelo.Gasto;
import sic.modelo.UsuarioActivo;
import sic.modelo.EstadoCaja;
import sic.modelo.Recibo;
import sic.util.ColoresNumerosTablaRenderer;
import sic.util.FormatoFechasEnTablasRenderer;
import sic.util.FormatterFechaHora;
import sic.util.Utilidades;

public class CajaGUI extends JInternalFrame {

    private final FormatterFechaHora formatter = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_HISPANO);
    private ModeloTabla modeloTablaBalance = new ModeloTabla();
    private ModeloTabla modeloTablaResumen = new ModeloTabla();
    private List<Movimiento> movimientos = new ArrayList<>();
    private Map<Long, List<Movimiento>> mapMovimientos = new HashMap<>();
    private Caja caja;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(880, 600);    

    @Data
    class Movimiento implements Comparable<Movimiento> {

        private long idMovimiento;
        private TipoMovimiento tipoMovimientoCaja;
        private String concepto;
        private Date fecha;
        private BigDecimal monto;
        
        public Movimiento(Recibo recibo) {
            this.idMovimiento = recibo.getIdRecibo();
            this.tipoMovimientoCaja = TipoMovimiento.RECIBO;
            String razonSocial = ((recibo.getRazonSocialCliente().isEmpty()) ? recibo.getRazonSocialProveedor() : recibo.getRazonSocialCliente());
            this.concepto = "Recibo Nº " + recibo.getNumSerie() + " - " + recibo.getNumRecibo() 
                    + " del " + ((recibo.getRazonSocialCliente().isEmpty()) ? "Proveedor: " : "Cliente: ")
                    + razonSocial;
            this.fecha = recibo.getFecha();
            this.monto = recibo.getMonto();
        }

        public Movimiento(Gasto gasto) {
            this.idMovimiento = gasto.getId_Gasto();
            this.tipoMovimientoCaja = TipoMovimiento.GASTO;
            this.concepto = this.tipoMovimientoCaja + " por: " + gasto.getConcepto();
            this.fecha = gasto.getFecha();
            this.monto = gasto.getMonto().negate();
        }

        @Override
        public int compareTo(Movimiento o) {
            return o.getFecha().compareTo(this.fecha);
        }

    }

    public CajaGUI(Caja caja) {
        this.initComponents();
        this.caja = caja;        
    }  
  
    private void cargarMovimientosDeFormaDePago(KeyEvent evt) {
        int row = tbl_Resumen.getSelectedRow();
        if (row != -1) {
            row = Utilidades.getSelectedRowModelIndice(tbl_Resumen);
            if (evt != null) {
                if ((evt.getKeyCode() == KeyEvent.VK_UP) && row > 0) {
                    row--;
                }
                if ((evt.getKeyCode() == KeyEvent.VK_DOWN) && (row + 1) < tbl_Resumen.getRowCount()) {
                    row++;
                }
            }
            try {
                if (row != 0) {
                    long idFormaDePago = (long) tbl_Resumen.getModel().getValueAt(row, 0);
                    this.cargarTablaMovimientos(idFormaDePago);                  
                } else {
                    this.limpiarTablaMovimientos();
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
    
    private void limpiarTablaResumen() {
        modeloTablaResumen.setRowCount(0);
        tbl_Resumen.setModel(modeloTablaResumen);
        this.setColumnasTablaResumen();
    }
    
    private void limpiarTablaMovimientos() {
        modeloTablaBalance.setRowCount(0);
        tbl_Movimientos.setModel(modeloTablaBalance);
        this.setColumnasTablaMovimientos();
    }

    private void setColumnasTablaMovimientos() {
        //sorting
        tbl_Movimientos.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[3];
        encabezados[0] = "Concepto";
        encabezados[1] = "Fecha";
        encabezados[2] = "Monto";
        modeloTablaBalance.setColumnIdentifiers(encabezados);
        tbl_Movimientos.setModel(modeloTablaBalance);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaBalance.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = Date.class;
        tipos[2] = Double.class;
        modeloTablaBalance.setClaseColumnas(tipos);
        tbl_Movimientos.getTableHeader().setReorderingAllowed(false);
        tbl_Movimientos.getTableHeader().setResizingAllowed(true);

        //Tamanios de columnas
        tbl_Movimientos.getColumnModel().getColumn(0).setPreferredWidth(200);
        tbl_Movimientos.getColumnModel().getColumn(1).setPreferredWidth(5);
        tbl_Movimientos.getColumnModel().getColumn(2).setCellRenderer(new ColoresNumerosTablaRenderer());
        tbl_Movimientos.getColumnModel().getColumn(1).setCellRenderer(new FormatoFechasEnTablasRenderer());
    }

    private void setColumnasTablaResumen() {
        //sorting
        tbl_Resumen.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[4];
        encabezados[0] = "idFormaDePago";
        encabezados[1] = "Forma de Pago";
        encabezados[2] = "Afecta la Caja";
        encabezados[3] = "Total";
        modeloTablaResumen.setColumnIdentifiers(encabezados);
        tbl_Resumen.setModel(modeloTablaResumen);
        
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResumen.getColumnCount()];
        tipos[0] = Long.class;
        tipos[1] = String.class;
        tipos[2] = Boolean.class;
        tipos[3] = Double.class;
        modeloTablaResumen.setClaseColumnas(tipos);
        tbl_Resumen.getTableHeader().setReorderingAllowed(false);
        tbl_Resumen.getTableHeader().setResizingAllowed(true);

        //Tamanios de columnas
        tbl_Resumen.getColumnModel().getColumn(0).setPreferredWidth(200);
        tbl_Resumen.getColumnModel().getColumn(1).setPreferredWidth(5);
    }

    private void cargarTablaResumen() {
        this.caja = RestClient.getRestTemplate().getForObject("/cajas/" + this.caja.getId_Caja(), Caja.class);
        Object[] renglonSaldoApertura = new Object[4];
        renglonSaldoApertura[0] = 0L;
        renglonSaldoApertura[1] = "Saldo Apertura";
        renglonSaldoApertura[2] = true;
        renglonSaldoApertura[3] = caja.getSaldoInicial();
        modeloTablaResumen.addRow(renglonSaldoApertura);
        List<Recibo> recibos;
        List<Gasto> gastos;
        try {
            for (long idFormaDePago : caja.getTotalesPorFomaDePago().keySet()) {
                movimientos.clear();
                FormaDePago fdp = RestClient.getRestTemplate().getForObject("/formas-de-pago/" + idFormaDePago, FormaDePago.class);
                Object[] fila = new Object[4];
                fila[0] = fdp.getId_FormaDePago();
                fila[1] = fdp.getNombre();
                fila[2] = fdp.isAfectaCaja();
                fila[3] = caja.getTotalesPorFomaDePago().get(idFormaDePago);
                modeloTablaResumen.addRow(fila);
                recibos = this.getRecibosPorFormaDePago(idFormaDePago);
                recibos.stream().forEach(r -> {
                    movimientos.add(new Movimiento(r));
                });
                gastos = this.getGastosPorFormaDePago(idFormaDePago);
                gastos.stream().forEach(g -> {
                    movimientos.add(new Movimiento(g));
                });
                Collections.sort(movimientos);
                mapMovimientos.put(idFormaDePago, new ArrayList<>(movimientos));
            }
            this.cargarResultados();
            tbl_Resumen.setModel(modeloTablaResumen);
            tbl_Resumen.removeColumn(tbl_Resumen.getColumnModel().getColumn(0));
            tbl_Resumen.setDefaultRenderer(Double.class, new ColoresNumerosTablaRenderer());
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }
      
    private void cargarTablaMovimientos(Long idFormaDePago) {
        this.limpiarTablaMovimientos();
        if (idFormaDePago != 0) {
            Object[] renglonMovimiento = new Object[3];
            movimientos = mapMovimientos.get(idFormaDePago);
            movimientos.forEach(m -> {
                renglonMovimiento[0] = m.getConcepto();
                renglonMovimiento[1] = m.getFecha();
                renglonMovimiento[2] = m.getMonto();
                modeloTablaBalance.addRow(renglonMovimiento);
            });
            tbl_Movimientos.setModel(modeloTablaBalance);
        }
    }
  
    private void cargarResultados() {   
        caja.setTotalAfectaCaja(RestClient.getRestTemplate()
                .getForObject("/cajas/" + this.caja.getId_Caja() + "/total?soloAfectaCaja=true", BigDecimal.class));
        ftxt_TotalAfectaCaja.setValue(caja.getTotalAfectaCaja());
        caja.setTotalGeneral(RestClient.getRestTemplate()
                .getForObject("/cajas/" + this.caja.getId_Caja() + "/total", BigDecimal.class));
        ftxt_TotalGeneral.setValue(caja.getTotalGeneral());              
        if (caja.getTotalAfectaCaja().compareTo(BigDecimal.ZERO) > 0) {
            ftxt_TotalAfectaCaja.setBackground(Color.GREEN);
        }
        if (caja.getTotalAfectaCaja().compareTo(BigDecimal.ZERO) < 0) {
            ftxt_TotalAfectaCaja.setBackground(Color.PINK);
        }
        if (caja.getTotalGeneral().compareTo(BigDecimal.ZERO) < 0) {
            ftxt_TotalGeneral.setBackground(Color.PINK);
        }
        if (caja.getTotalGeneral().compareTo(BigDecimal.ZERO) > 0) {
            ftxt_TotalGeneral.setBackground(Color.GREEN);
        }
    }

    private void limpiarYCargarTablas() {
        this.limpiarTablaResumen();
        this.cargarTablaResumen();
        this.limpiarTablaMovimientos();
    }

    private void lanzarReporteRecibo(long idRecibo) {
        if (Desktop.isDesktopSupported()) {
            try {
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/recibos/" + idRecibo + "/reporte", 
                                byte[].class);
                File f = new File(System.getProperty("user.home") + "/Factura.pdf");
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
    
    private List<Recibo> getRecibosPorFormaDePago(long idFormaDePago) {
        String criteriaRecibos = "/recibos/busqueda?"
                + "idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                + "&idFormaDePago=" + idFormaDePago
                + "&desde=" + caja.getFechaApertura().getTime()
                + "&hasta=" + this.getFechaHastaCaja(caja);
        return new ArrayList(Arrays.asList(RestClient.getRestTemplate().getForObject(criteriaRecibos, Recibo[].class)));
    }

    private List<Gasto> getGastosPorFormaDePago(long idFormaDePago) {
        String criteriaGastos = "/gastos/busqueda?"
                + "idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                + "&idFormaDePago=" + idFormaDePago
                + "&desde=" + caja.getFechaApertura().getTime()
                + "&hasta=" + this.getFechaHastaCaja(caja);
        return new ArrayList(Arrays.asList(RestClient.getRestTemplate().getForObject(criteriaGastos, Gasto[].class)));
    }
        
    private long getFechaHastaCaja(Caja caja) {
        LocalDateTime hasta = caja.getFechaApertura().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        hasta = hasta.withHour(23);
        hasta = hasta.withMinute(59);
        hasta = hasta.withSecond(59);
        return hasta.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Resumen = new javax.swing.JPanel();
        sp_TablaResumen = new javax.swing.JScrollPane();
        tbl_Resumen = new javax.swing.JTable();
        sp_Tabla = new javax.swing.JScrollPane();
        tbl_Movimientos = new javax.swing.JTable();
        lbl_movimientos = new javax.swing.JLabel();
        btn_VerDetalle = new javax.swing.JButton();
        btn_EliminarGasto = new javax.swing.JButton();
        lbl_estadoEstatico = new javax.swing.JLabel();
        lbl_estadoDinamico = new javax.swing.JLabel();
        btn_CerrarCaja = new javax.swing.JButton();
        btn_AgregarGasto = new javax.swing.JButton();
        lbl_Total = new javax.swing.JLabel();
        lbl_totalCaja = new javax.swing.JLabel();
        ftxt_TotalAfectaCaja = new javax.swing.JFormattedTextField();
        ftxt_TotalGeneral = new javax.swing.JFormattedTextField();
        btn_Refresh = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Caja_16x16.png"))); // NOI18N
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

        pnl_Resumen.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumen General"));

        tbl_Resumen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resumen.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Resumen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_ResumenMouseClicked(evt);
            }
        });
        tbl_Resumen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_ResumenKeyPressed(evt);
            }
        });
        sp_TablaResumen.setViewportView(tbl_Resumen);

        tbl_Movimientos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Tabla.setViewportView(tbl_Movimientos);

        lbl_movimientos.setText("Movimientos por Forma de Pago (Seleccione una de la lista superior)");

        btn_VerDetalle.setForeground(java.awt.Color.blue);
        btn_VerDetalle.setText("Ver Detalle");
        btn_VerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerDetalleActionPerformed(evt);
            }
        });

        btn_EliminarGasto.setForeground(java.awt.Color.blue);
        btn_EliminarGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CoinsDel_16x16.png"))); // NOI18N
        btn_EliminarGasto.setText("Eliminar Gasto");
        btn_EliminarGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarGastoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_ResumenLayout = new javax.swing.GroupLayout(pnl_Resumen);
        pnl_Resumen.setLayout(pnl_ResumenLayout);
        pnl_ResumenLayout.setHorizontalGroup(
            pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_TablaResumen, javax.swing.GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
            .addComponent(sp_Tabla, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(pnl_ResumenLayout.createSequentialGroup()
                .addGroup(pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_movimientos)
                    .addGroup(pnl_ResumenLayout.createSequentialGroup()
                        .addComponent(btn_VerDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_EliminarGasto)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnl_ResumenLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_EliminarGasto, btn_VerDetalle});

        pnl_ResumenLayout.setVerticalGroup(
            pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ResumenLayout.createSequentialGroup()
                .addComponent(sp_TablaResumen, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_movimientos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Tabla, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_VerDetalle)
                    .addComponent(btn_EliminarGasto)))
        );

        pnl_ResumenLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_EliminarGasto, btn_VerDetalle});

        lbl_estadoEstatico.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_estadoEstatico.setText("Estado:");

        lbl_estadoDinamico.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btn_CerrarCaja.setForeground(java.awt.Color.blue);
        btn_CerrarCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CerrarCaja_16x16.png"))); // NOI18N
        btn_CerrarCaja.setText("Cerrar Caja");
        btn_CerrarCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CerrarCajaActionPerformed(evt);
            }
        });

        btn_AgregarGasto.setForeground(java.awt.Color.blue);
        btn_AgregarGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CoinsAdd_16x16.png"))); // NOI18N
        btn_AgregarGasto.setText("Nuevo Gasto");
        btn_AgregarGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarGastoActionPerformed(evt);
            }
        });

        lbl_Total.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_Total.setText("Total General:");

        lbl_totalCaja.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_totalCaja.setText("Total que afecta Caja:");

        ftxt_TotalAfectaCaja.setEditable(false);
        ftxt_TotalAfectaCaja.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        ftxt_TotalAfectaCaja.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxt_TotalAfectaCaja.setText("0");
        ftxt_TotalAfectaCaja.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        ftxt_TotalGeneral.setEditable(false);
        ftxt_TotalGeneral.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        ftxt_TotalGeneral.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxt_TotalGeneral.setText("0");
        ftxt_TotalGeneral.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        btn_Refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Refresh_16x16.png"))); // NOI18N
        btn_Refresh.setFocusable(false);
        btn_Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_AgregarGasto)
                        .addGap(0, 0, 0)
                        .addComponent(btn_CerrarCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbl_Total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftxt_TotalGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbl_totalCaja)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftxt_TotalAfectaCaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(pnl_Resumen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_estadoEstatico)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_estadoDinamico, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_Refresh)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftxt_TotalAfectaCaja, ftxt_TotalGeneral});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_AgregarGasto, btn_CerrarCaja});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_estadoDinamico, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_estadoEstatico, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Refresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Resumen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_totalCaja, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ftxt_TotalAfectaCaja, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_AgregarGasto)
                    .addComponent(btn_CerrarCaja)
                    .addComponent(lbl_Total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ftxt_TotalGeneral))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftxt_TotalAfectaCaja, ftxt_TotalGeneral, lbl_Total, lbl_totalCaja});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_CerrarCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CerrarCajaActionPerformed
        if (caja != null) {
            if (caja.getEstado() == EstadoCaja.ABIERTA) {
                try {
                    String monto = JOptionPane.showInputDialog(this,
                            "Saldo del Sistema: " + new DecimalFormat("#.##").format(caja.getSaldoFinal())
                            + "\nSaldo Real:", "Cerrar Caja", JOptionPane.QUESTION_MESSAGE);
                    if (monto != null) {
                        RestClient.getRestTemplate().put("/cajas/" + caja.getId_Caja() + "/cierre?"
                                + "monto=" + Double.parseDouble(monto)
                                + "&idUsuarioCierre=" + UsuarioActivo.getInstance().getUsuario().getId_Usuario(),
                                Caja.class);
                        this.dispose();
                    }
                } catch (NumberFormatException e) {
                    String msjError = "Monto inválido";
                    LOGGER.error(msjError + " - " + e.getMessage());
                    JOptionPane.showMessageDialog(this, msjError, "Error", JOptionPane.INFORMATION_MESSAGE);
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_caja_cerrada"),
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_CerrarCajaActionPerformed

    private void btn_VerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerDetalleActionPerformed
        if (tbl_Movimientos.getSelectedRow() != -1) {
            long id = this.movimientos.get(Utilidades.getSelectedRowModelIndice(tbl_Movimientos)).getIdMovimiento();
            TipoMovimiento tipoMovimientoCaja = this.movimientos.get(Utilidades.getSelectedRowModelIndice(tbl_Movimientos)).getTipoMovimientoCaja();
            try {
                if (tipoMovimientoCaja.equals(TipoMovimiento.RECIBO)) {
                    this.lanzarReporteRecibo(id);
                }
                if (tipoMovimientoCaja.equals(TipoMovimiento.GASTO)) {
                    Gasto gasto = RestClient.getRestTemplate().getForObject("/gastos/" + id, Gasto.class);
                    String mensaje = "En Concepto de: " + gasto.getConcepto()
                            + "\nMonto: " + gasto.getMonto() + "\nUsuario: " + gasto.getUsuario().getNombre();
                    JOptionPane.showMessageDialog(this, mensaje, "Resumen de Gasto", JOptionPane.INFORMATION_MESSAGE);
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
    }//GEN-LAST:event_btn_VerDetalleActionPerformed

    private void btn_AgregarGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarGastoActionPerformed
        try {
            if (this.caja.getEstado().equals(EstadoCaja.ABIERTA)) {
                List<FormaDePago> formasDePago = Arrays.asList(RestClient.getRestTemplate().getForObject("/formas-de-pago/empresas/"
                        + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(), FormaDePago[].class));
                AgregarGastoGUI agregarGasto = new AgregarGastoGUI(formasDePago);
                agregarGasto.setLocationRelativeTo(null);
                agregarGasto.setEnabled(true);
                agregarGasto.setVisible(true);
                this.limpiarYCargarTablas();
            } else if (this.caja.getEstado().equals(EstadoCaja.CERRADA)) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_caja_cerrada"),
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_AgregarGastoActionPerformed

    private void btn_EliminarGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarGastoActionPerformed
        if (tbl_Movimientos.getSelectedRow() != -1) {
            long idMovimientoTable = this.movimientos.get(Utilidades.getSelectedRowModelIndice(tbl_Movimientos)).getIdMovimiento();
            TipoMovimiento tipoMovimientoCaja = this.movimientos.get(Utilidades.getSelectedRowModelIndice(tbl_Movimientos)).getTipoMovimientoCaja();
            if (tipoMovimientoCaja.equals(TipoMovimiento.GASTO) && this.caja.getEstado().equals(EstadoCaja.ABIERTA)) {
                int confirmacionEliminacion = JOptionPane.showConfirmDialog(this,
                        "¿Esta seguro que desea eliminar el gasto seleccionado?",
                        "Eliminar", JOptionPane.YES_NO_OPTION);
                if (confirmacionEliminacion == JOptionPane.YES_OPTION) {
                    try {
                        RestClient.getRestTemplate().delete("/gastos/" + idMovimientoTable);
                        this.limpiarYCargarTablas();
                    } catch (RestClientResponseException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (ResourceAccessException ex) {
                        LOGGER.error(ex.getMessage());
                        JOptionPane.showMessageDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (this.caja.getEstado().equals(EstadoCaja.CERRADA)) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_caja_cerrada"),
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_EliminarGastoActionPerformed

    private void tbl_ResumenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ResumenMouseClicked
        this.cargarTablaMovimientos((long) tbl_Resumen.getModel().getValueAt(Utilidades.getSelectedRowModelIndice(tbl_Resumen),0));
    }//GEN-LAST:event_tbl_ResumenMouseClicked

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.setSize(sizeInternalFrame);        
        this.setTitle("Arqueo de Caja - Apertura: " + formatter.format(this.caja.getFechaApertura()));
        switch (caja.getEstado()) {
            case ABIERTA: {
                lbl_estadoDinamico.setText("Abierta");
                lbl_estadoDinamico.setForeground(Color.GREEN);
                break;
            }
            case CERRADA: {
                lbl_estadoDinamico.setText("Cerrada");
                lbl_estadoDinamico.setForeground(Color.RED);
                break;
            }
        }
        this.limpiarYCargarTablas();
        try {
            this.setMaximum(true);
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void tbl_ResumenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResumenKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            this.cargarMovimientosDeFormaDePago(evt);
        }
    }//GEN-LAST:event_tbl_ResumenKeyPressed

    private void btn_RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RefreshActionPerformed
        if (caja.getEstado().equals(EstadoCaja.CERRADA)) {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_cerrada"), "Aviso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            this.limpiarYCargarTablas();
        }
    }//GEN-LAST:event_btn_RefreshActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AgregarGasto;
    private javax.swing.JButton btn_CerrarCaja;
    private javax.swing.JButton btn_EliminarGasto;
    private javax.swing.JButton btn_Refresh;
    private javax.swing.JButton btn_VerDetalle;
    private javax.swing.JFormattedTextField ftxt_TotalAfectaCaja;
    private javax.swing.JFormattedTextField ftxt_TotalGeneral;
    private javax.swing.JLabel lbl_Total;
    private javax.swing.JLabel lbl_estadoDinamico;
    private javax.swing.JLabel lbl_estadoEstatico;
    private javax.swing.JLabel lbl_movimientos;
    private javax.swing.JLabel lbl_totalCaja;
    private javax.swing.JPanel pnl_Resumen;
    private javax.swing.JScrollPane sp_Tabla;
    private javax.swing.JScrollPane sp_TablaResumen;
    javax.swing.JTable tbl_Movimientos;
    private javax.swing.JTable tbl_Resumen;
    // End of variables declaration//GEN-END:variables

}
