package sic.vista.swing;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
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
import sic.modelo.EmpresaActiva;
import sic.modelo.FacturaVenta;
import sic.modelo.Movimiento;
import sic.modelo.NotaDebito;
import sic.modelo.NotaDebitoCliente;
import sic.modelo.NotaDebitoProveedor;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Rol;
import sic.modelo.TipoDeComprobante;
import sic.modelo.UsuarioActivo;
import sic.util.DecimalesRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class NotasDebitoGUI extends JInternalFrame {

    private ModeloTabla modeloTablaNotas = new ModeloTabla();
    private List<NotaDebitoCliente> notasClienteTotal = new ArrayList<>();
    private List<NotaDebitoCliente> notasClienteParcial = new ArrayList<>();
    private List<NotaDebitoProveedor> notasProveedorTotal = new ArrayList<>();
    private List<NotaDebitoProveedor> notasProveedorParcial = new ArrayList<>();
    private Movimiento movimiento;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(970, 600);
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;
    private static final int TAMANIO_PAGINA = 50;

    public NotasDebitoGUI(Movimiento movimiento) {
        this.initComponents();
        this.movimiento = movimiento;
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 50;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (movimiento.equals(movimiento.NOTA_CLIENTE)) {
                    if (notasClienteTotal.size() >= TAMANIO_PAGINA) {
                        NUMERO_PAGINA += 1;
                        buscar(false);
                    }
                } else if (movimiento.equals(movimiento.NOTA_PROVEEDOR)) {
                    if (notasProveedorTotal.size() >= TAMANIO_PAGINA) {
                        NUMERO_PAGINA += 1;
                        buscar(false);
                    }
                }
            }
        });
    }

    private String getUriCriteria() {
        String uriCriteria = "idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa();
        if (chk_Fecha.isSelected()) {
            uriCriteria += "&desde=" + dc_FechaDesde.getDate().getTime()
                    + "&hasta=" + dc_FechaHasta.getDate().getTime();
        }
        if (chk_NumNota.isSelected()) {
            uriCriteria += "&nroNota=" + Long.valueOf(txt_NroNota.getText())
                    + "&nroSerie=" + Long.valueOf(txt_SerieNota.getText());
        }
        if (chk_TipoNota.isSelected()) {
            uriCriteria += "&tipoDeComprobante=" + ((TipoDeComprobante) cmb_TipoNota.getSelectedItem()).name();
        }
        uriCriteria += "&pagina=" + NUMERO_PAGINA + "&tamanio=" + TAMANIO_PAGINA;
        return uriCriteria;
    }

    private void setColumnas() {
        // Momentaneamente desactivado hasta terminar la paginacion.        
        //sorting
        // tbl_Resultados.setAutoCreateRowSorter(true);        
        //nombres de columnas
        String[] encabezados = new String[8];
        encabezados[0] = "CAE";
        encabezados[1] = "Fecha Nota";
        encabezados[2] = "Tipo";
        encabezados[3] = "Nº Nota";
        encabezados[4] = (movimiento.equals(Movimiento.NOTA_CLIENTE) ? "Cliente" : "Proveedor");
        encabezados[5] = "Total";
        encabezados[6] = "Nº Nota Afip";
        encabezados[7] = "Vencimiento CAE";
        modeloTablaNotas.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaNotas);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaNotas.getColumnCount()];
        tipos[0] = Object.class;
        tipos[1] = Date.class;
        tipos[2] = TipoDeComprobante.class;
        tipos[3] = String.class;
        tipos[4] = String.class;
        tipos[5] = BigDecimal.class;
        tipos[6] = String.class;
        tipos[7] = Date.class;
        modeloTablaNotas.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);        
        //tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setMinWidth(120);
        tbl_Resultados.getColumnModel().getColumn(0).setMaxWidth(120);
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(1).setMinWidth(140);
        tbl_Resultados.getColumnModel().getColumn(1).setMaxWidth(140);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(140);
        tbl_Resultados.getColumnModel().getColumn(2).setMinWidth(140);
        tbl_Resultados.getColumnModel().getColumn(2).setMaxWidth(140);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(140);
        tbl_Resultados.getColumnModel().getColumn(3).setMinWidth(100);
        tbl_Resultados.getColumnModel().getColumn(3).setMaxWidth(100);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(4).setMinWidth(380);
        tbl_Resultados.getColumnModel().getColumn(4).setMaxWidth(380);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(380);
        tbl_Resultados.getColumnModel().getColumn(5).setMinWidth(120);
        tbl_Resultados.getColumnModel().getColumn(5).setMaxWidth(120);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(6).setMinWidth(100);
        tbl_Resultados.getColumnModel().getColumn(6).setMaxWidth(100);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(7).setMinWidth(140);
        tbl_Resultados.getColumnModel().getColumn(7).setMaxWidth(140);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(140);
        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        tbl_Resultados.getColumnModel().getColumn(1).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
        tbl_Resultados.getColumnModel().getColumn(7).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHA_HISPANO));
    }

    private void buscar(boolean calcularResultados) {
        this.cambiarEstadoEnabledComponentes(false);
        String uriCriteria = getUriCriteria();
        try {
            if (movimiento.equals(Movimiento.NOTA_CLIENTE)) {
                PaginaRespuestaRest<NotaDebitoCliente> response = RestClient.getRestTemplate()
                        .exchange("/notas/debito/clientes/busqueda/criteria?" + uriCriteria, HttpMethod.GET, null,
                                new ParameterizedTypeReference<PaginaRespuestaRest<NotaDebitoCliente>>() {
                        })
                        .getBody();
                totalElementosBusqueda = response.getTotalElements();
                notasClienteParcial = response.getContent();
                notasClienteTotal.addAll(notasClienteParcial);
            } else if (movimiento.equals(Movimiento.NOTA_PROVEEDOR)) {
                PaginaRespuestaRest<NotaDebitoProveedor> response = RestClient.getRestTemplate()
                        .exchange("/notas/debito/proveedores/busqueda/criteria?" + uriCriteria, HttpMethod.GET, null,
                                new ParameterizedTypeReference<PaginaRespuestaRest<NotaDebitoProveedor>>() {
                        })
                        .getBody();
                totalElementosBusqueda = response.getTotalElements();
                notasProveedorParcial = response.getContent();
                notasProveedorTotal.addAll(notasProveedorParcial);
            }
            this.cargarResultadosAlTable();
            if (calcularResultados) {
                this.calcularResultados(uriCriteria);
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        this.cambiarEstadoEnabledComponentes(true);
        this.cambiarEstadoDeComponentesSegunRolUsuario();
    }
    
    private void calcularResultados(String uriCriteria) {
        if (movimiento.equals(Movimiento.NOTA_CLIENTE)) {
            txt_ResultTotalIVANotaDebito.setValue(RestClient.getRestTemplate()
                    .getForObject("/notas/debito/clientes/total-iva/criteria?" + uriCriteria, BigDecimal.class));
            txt_ResultTotalNotasDebito.setValue(RestClient.getRestTemplate()
                    .getForObject("/notas/debito/clientes/total/criteria?" + uriCriteria, BigDecimal.class));
        } else if (movimiento.equals(Movimiento.NOTA_PROVEEDOR)) {
            txt_ResultTotalIVANotaDebito.setValue(RestClient.getRestTemplate()
                    .getForObject("/notas/debito/proveedores/total-iva/criteria?" + uriCriteria, BigDecimal.class));
            txt_ResultTotalNotasDebito.setValue(RestClient.getRestTemplate()
                    .getForObject("/notas/debito/proveedores/total/criteria?" + uriCriteria, BigDecimal.class));
        }
    }

    private void cambiarEstadoEnabledComponentes(boolean status) {
        chk_Fecha.setEnabled(status);
        if (status == true && chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
        }
        chk_NumNota.setEnabled(status);
        if (status == true && chk_NumNota.isSelected() == true) {
            txt_SerieNota.setEnabled(true);
            txt_NroNota.setEnabled(true);
        } else {
            txt_SerieNota.setEnabled(false);
            txt_NroNota.setEnabled(false);
        }
        chk_TipoNota.setEnabled(status);
        if (status == true && chk_TipoNota.isSelected() == true) {
            cmb_TipoNota.setEnabled(true);
        } else {
            cmb_TipoNota.setEnabled(false);
        }
        chk_NumNota.setEnabled(status);
        if (status == true && chk_NumNota.isSelected() == true) {
            txt_NroNota.setEnabled(true);
        } else {
            txt_NroNota.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);        
        btn_Eliminar.setEnabled(status);
        btn_VerDetalle.setEnabled(status);
        btn_Autorizar.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        sp_Resultados.setEnabled(status);
        tbl_Resultados.requestFocus();
    }

    private void cargarResultadosAlTable() {
        if (movimiento.equals(Movimiento.NOTA_CLIENTE)) {
            notasClienteParcial.stream().map(notaCliente -> {
                Object[] fila = new Object[9];
                fila[0] = notaCliente.getCAE() == 0 ? "" : notaCliente.getCAE();
                fila[1] = notaCliente.getFecha();
                fila[2] = notaCliente.getTipoComprobante();
                fila[3] = notaCliente.getSerie() + " - " + notaCliente.getNroNota();
                fila[4] = notaCliente.getCliente().getRazonSocial();
                fila[5] = notaCliente.getTotal();
                fila[6] = notaCliente.getNumSerieAfip() + "-" + notaCliente.getNumNotaAfip();
                fila[7] = notaCliente.getVencimientoCAE();
                if (notaCliente.getNumSerieAfip() == 0 && notaCliente.getNumNotaAfip() == 0) {
                    fila[8] = "";
                } else {
                    fila[8] = notaCliente.getNumSerieAfip() + " - " + notaCliente.getNumNotaAfip();
                }
                return fila;
            }).forEach(fila -> {
                modeloTablaNotas.addRow(fila);
            });
        } else if (movimiento.equals(Movimiento.NOTA_PROVEEDOR)) {
            notasProveedorParcial.stream().map(notaproveedor -> {
                Object[] fila = new Object[9];
                fila[0] = notaproveedor.getCAE() == 0 ? "" : notaproveedor.getCAE();
                fila[1] = notaproveedor.getFecha();
                fila[2] = notaproveedor.getTipoComprobante();
                fila[3] = notaproveedor.getSerie() + " - " + notaproveedor.getNroNota();
                fila[4] = notaproveedor.getProveedor().getRazonSocial();
                fila[5] = notaproveedor.getTotal();
                fila[6] = notaproveedor.getNumSerieAfip() + "-" + notaproveedor.getNumNotaAfip();
                fila[7] = notaproveedor.getVencimientoCAE();
                if (notaproveedor.getNumSerieAfip() == 0 && notaproveedor.getNumNotaAfip() == 0) {
                    fila[8] = "";
                } else {
                    fila[8] = notaproveedor.getNumSerieAfip() + " - " + notaproveedor.getNumNotaAfip();
                }
                return fila;
            }).forEach(fila -> {
                modeloTablaNotas.addRow(fila);
            });
        }        
        tbl_Resultados.setModel(modeloTablaNotas);
        lbl_cantResultados.setText(totalElementosBusqueda + " notas encontradas");
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        notasClienteTotal.clear();
        notasClienteParcial.clear();
        notasProveedorTotal.clear();
        notasProveedorParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }

    private void limpiarJTable() {
        modeloTablaNotas = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaNotas);
        this.setColumnas();
    }

    private void cargarTiposDeNota() {
        try {
            TipoDeComprobante[] tiposDeComprobantes = RestClient.getRestTemplate()
                    .getForObject("/notas/debito/tipos/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                            TipoDeComprobante[].class);
            for (int i = 0; tiposDeComprobantes.length > i; i++) {
                cmb_TipoNota.addItem(tiposDeComprobantes[i]);
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

    private void lanzarReporteNota() {
        if (Desktop.isDesktopSupported()) {
            try {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                if (Desktop.isDesktopSupported()) {
                    byte[] reporte = RestClient.getRestTemplate() 
                            .getForObject("/notas/"
                                    + ((notasClienteTotal.size() > 0)
                                    ? notasClienteTotal.get(indexFilaSeleccionada).getIdNota() : notasProveedorTotal.get(indexFilaSeleccionada).getIdNota())
                                    + "/reporte", byte[].class);
                    File f = new File(System.getProperty("user.home") + "/NotaCredito.pdf");
                    Files.write(f.toPath(), reporte);
                    Desktop.getDesktop().open(f);
                } else {
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
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

    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (!rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)) {
            btn_Eliminar.setEnabled(false);
            if ((!rolesDeUsuarioActivo.contains(Rol.ENCARGADO) && !rolesDeUsuarioActivo.contains(Rol.VENDEDOR))
                    || movimiento.equals(Movimiento.NOTA_PROVEEDOR)) {
                btn_Autorizar.setEnabled(false);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_VerDetalle = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        btn_Autorizar = new javax.swing.JButton();
        lbl_TotalIVANotasDebito = new javax.swing.JLabel();
        txt_ResultTotalIVANotaDebito = new javax.swing.JFormattedTextField();
        lbl_TotalNotasDebito = new javax.swing.JLabel();
        txt_ResultTotalNotasDebito = new javax.swing.JFormattedTextField();
        panelFiltros = new javax.swing.JPanel();
        subPanelFiltros1 = new javax.swing.JPanel();
        chk_Fecha = new javax.swing.JCheckBox();
        lbl_Hasta = new javax.swing.JLabel();
        lbl_Desde = new javax.swing.JLabel();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        chk_NumNota = new javax.swing.JCheckBox();
        txt_SerieNota = new javax.swing.JFormattedTextField();
        separador = new javax.swing.JLabel();
        txt_NroNota = new javax.swing.JFormattedTextField();
        subPanelFiltros2 = new javax.swing.JPanel();
        chk_TipoNota = new javax.swing.JCheckBox();
        cmb_TipoNota = new javax.swing.JComboBox();
        btn_Buscar = new javax.swing.JButton();
        lbl_cantResultados = new javax.swing.JLabel();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/SIC_16_square.png"))); // NOI18N
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

        panelResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sp_Resultados.setViewportView(tbl_Resultados);

        btn_VerDetalle.setForeground(java.awt.Color.blue);
        btn_VerDetalle.setText("Ver Detalle");
        btn_VerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerDetalleActionPerformed(evt);
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

        btn_Autorizar.setForeground(java.awt.Color.blue);
        btn_Autorizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Certificate_16x16.png"))); // NOI18N
        btn_Autorizar.setText("Autorizar");
        btn_Autorizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AutorizarActionPerformed(evt);
            }
        });

        lbl_TotalIVANotasDebito.setText("Total IVA Debito:");

        txt_ResultTotalIVANotaDebito.setEditable(false);
        txt_ResultTotalIVANotaDebito.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultTotalIVANotaDebito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        lbl_TotalNotasDebito.setText("Total Acumulado:");

        txt_ResultTotalNotasDebito.setEditable(false);
        txt_ResultTotalNotasDebito.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultTotalNotasDebito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btn_Eliminar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Autorizar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_VerDetalle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                        .addComponent(lbl_TotalIVANotasDebito)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_ResultTotalIVANotaDebito, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                        .addComponent(lbl_TotalNotasDebito)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_ResultTotalNotasDebito, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 938, Short.MAX_VALUE)
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Autorizar, btn_Eliminar, btn_VerDetalle});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_VerDetalle)
                        .addComponent(btn_Autorizar)
                        .addComponent(btn_Eliminar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_TotalIVANotasDebito)
                            .addComponent(txt_ResultTotalIVANotaDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_TotalNotasDebito)
                            .addComponent(txt_ResultTotalNotasDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Autorizar, btn_Eliminar, btn_VerDetalle});

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Fecha.setText("Fecha Nota:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

        lbl_Hasta.setText("Hasta:");
        lbl_Hasta.setEnabled(false);

        lbl_Desde.setText("Desde:");
        lbl_Desde.setEnabled(false);

        dc_FechaDesde.setDateFormatString("dd/MM/yyyy");
        dc_FechaDesde.setEnabled(false);

        dc_FechaHasta.setDateFormatString("dd/MM/yyyy");
        dc_FechaHasta.setEnabled(false);

        chk_NumNota.setText("Nº de Nota:");
        chk_NumNota.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumNotaItemStateChanged(evt);
            }
        });

        txt_SerieNota.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_SerieNota.setText("0");
        txt_SerieNota.setEnabled(false);
        txt_SerieNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_SerieNotaActionPerformed(evt);
            }
        });
        txt_SerieNota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_SerieNotaKeyTyped(evt);
            }
        });

        separador.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        separador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        separador.setText("-");

        txt_NroNota.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_NroNota.setText("0");
        txt_NroNota.setEnabled(false);
        txt_NroNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_NroNotaActionPerformed(evt);
            }
        });
        txt_NroNota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NroNotaKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout subPanelFiltros1Layout = new javax.swing.GroupLayout(subPanelFiltros1);
        subPanelFiltros1.setLayout(subPanelFiltros1Layout);
        subPanelFiltros1Layout.setHorizontalGroup(
            subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chk_NumNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Fecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addComponent(txt_SerieNota)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(separador, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_NroNota, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addComponent(lbl_Desde)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaDesde, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_Hasta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaHasta, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))))
        );
        subPanelFiltros1Layout.setVerticalGroup(
            subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Hasta)
                    .addComponent(lbl_Desde)
                    .addComponent(chk_Fecha))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chk_NumNota)
                    .addComponent(txt_SerieNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(separador)
                    .addComponent(txt_NroNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        chk_TipoNota.setText("Tipo de Nota:");
        chk_TipoNota.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_TipoNotaItemStateChanged(evt);
            }
        });

        cmb_TipoNota.setEnabled(false);

        javax.swing.GroupLayout subPanelFiltros2Layout = new javax.swing.GroupLayout(subPanelFiltros2);
        subPanelFiltros2.setLayout(subPanelFiltros2Layout);
        subPanelFiltros2Layout.setHorizontalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros2Layout.createSequentialGroup()
                .addComponent(chk_TipoNota)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmb_TipoNota, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        subPanelFiltros2Layout.setVerticalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros2Layout.createSequentialGroup()
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_TipoNota)
                    .addComponent(cmb_TipoNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelFiltrosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(subPanelFiltros1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(subPanelFiltros2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(subPanelFiltros1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(subPanelFiltros2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_Buscar)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 73, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void chk_FechaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FechaItemStateChanged
        if (chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
            lbl_Desde.setEnabled(true);
            lbl_Hasta.setEnabled(true);
            dc_FechaDesde.requestFocus();
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
            lbl_Desde.setEnabled(false);
            lbl_Hasta.setEnabled(false);
        }
}//GEN-LAST:event_chk_FechaItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar(true);
}//GEN-LAST:event_btn_BuscarActionPerformed

    private void chk_NumNotaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_NumNotaItemStateChanged
        if (chk_NumNota.isSelected() == true) {
            txt_NroNota.setEnabled(true);
            txt_SerieNota.setEnabled(true);
            txt_SerieNota.requestFocus();
        } else {
            txt_NroNota.setEnabled(false);
            txt_SerieNota.setEnabled(false);
        }
    }//GEN-LAST:event_chk_NumNotaItemStateChanged

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setSize(sizeInternalFrame);
            this.setColumnas();
            this.setMaximum(true);
            dc_FechaDesde.setDate(new Date());
            dc_FechaHasta.setDate(new Date());
            this.cambiarEstadoDeComponentesSegunRolUsuario();
            if (movimiento.equals(Movimiento.NOTA_CLIENTE)) {
                this.setTitle("Administrar Notas de Debito Cliente");
            } else if (movimiento.equals(Movimiento.NOTA_PROVEEDOR)) {
                this.setTitle("Administrar Notas de Debito Proveedor");
            }
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void chk_TipoNotaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_TipoNotaItemStateChanged
        if (chk_TipoNota.isSelected() == true) {
            cmb_TipoNota.setEnabled(true);
            this.cargarTiposDeNota();
            cmb_TipoNota.requestFocus();
        } else {
            cmb_TipoNota.setEnabled(false);
            cmb_TipoNota.removeAllItems();
        }
    }//GEN-LAST:event_chk_TipoNotaItemStateChanged

    private void txt_SerieNotaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_SerieNotaKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_SerieNotaKeyTyped

    private void txt_NroNotaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NroNotaKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_NroNotaKeyTyped

    private void txt_SerieNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_SerieNotaActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_SerieNotaActionPerformed

    private void txt_NroNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_NroNotaActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_NroNotaActionPerformed

    private void btn_AutorizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AutorizarActionPerformed
        try {
            boolean FEHabilitada = RestClient.getRestTemplate().getForObject("/configuraciones-del-sistema/empresas/"
                    + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                    + "/factura-electronica-habilitada", Boolean.class);
            if (FEHabilitada) {
                if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
                    int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados); 
                    long idNota = ((notasClienteTotal.size() > 0) ? notasClienteTotal.get(indexFilaSeleccionada).getIdNota() : notasProveedorTotal.get(indexFilaSeleccionada).getIdNota());
                    RestClient.getRestTemplate().postForObject("/notas/" + idNota + "/autorizacion",
                            null, FacturaVenta.class);
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_nota_autorizada"),
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    this.resetScroll();
                    this.limpiarJTable();
                    this.buscar(true);
                }
            } else {
                JOptionPane.showInternalMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_cds_fe_habilitada"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_AutorizarActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int respuesta = JOptionPane.showConfirmDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_eliminar_multiples_notas"),
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                int[] indexFilasSeleccionadas = Utilidades.getSelectedRowsModelIndices(tbl_Resultados);
                long[] idsNotas = new long[indexFilasSeleccionadas.length];
                int i = 0;
                for (int indice : indexFilasSeleccionadas) {
                    idsNotas[i] = ((notasClienteTotal.size() > 0)
                            ? notasClienteTotal.get(indice).getIdNota() : notasProveedorTotal.get(indice).getIdNota());
                    i++;
                }
                try {
                    RestClient.getRestTemplate().delete("/notas?idsNota="
                            + Arrays.toString(idsNotas).substring(1, Arrays.toString(idsNotas).length() - 1));
                    this.resetScroll();
                    this.limpiarJTable();
                    this.buscar(true);
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

    private void btn_VerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerDetalleActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            if (movimiento.equals(Movimiento.NOTA_CLIENTE)) {
                this.lanzarReporteNota();
            } else if (movimiento.equals(Movimiento.NOTA_PROVEEDOR)) {
                if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
                    int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                    long idNota = ((notasClienteTotal.size() > 0) ? notasClienteTotal.get(indexFilaSeleccionada).getIdNota() : notasProveedorTotal.get(indexFilaSeleccionada).getIdNota());
                    DetalleNotaDebitoGUI detalleNotaDebitoGUI = new DetalleNotaDebitoGUI(idNota);
                    detalleNotaDebitoGUI.setLocationRelativeTo(this);
                    detalleNotaDebitoGUI.setVisible(true);
                }
            }
        }
    }//GEN-LAST:event_btn_VerDetalleActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Autorizar;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_VerDetalle;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_NumNota;
    private javax.swing.JCheckBox chk_TipoNota;
    private javax.swing.JComboBox cmb_TipoNota;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JLabel lbl_Desde;
    private javax.swing.JLabel lbl_Hasta;
    private javax.swing.JLabel lbl_TotalIVANotasDebito;
    private javax.swing.JLabel lbl_TotalNotasDebito;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JLabel separador;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JPanel subPanelFiltros1;
    private javax.swing.JPanel subPanelFiltros2;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JFormattedTextField txt_NroNota;
    private javax.swing.JFormattedTextField txt_ResultTotalIVANotaDebito;
    private javax.swing.JFormattedTextField txt_ResultTotalNotasDebito;
    private javax.swing.JFormattedTextField txt_SerieNota;
    // End of variables declaration//GEN-END:variables
}
