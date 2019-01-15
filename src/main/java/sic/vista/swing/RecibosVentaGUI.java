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
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.modelo.Usuario;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Recibo;
import sic.modelo.Rol;
import sic.modelo.UsuarioActivo;
import sic.util.DecimalesRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class RecibosVentaGUI extends JInternalFrame {

    private ModeloTabla modeloTablaFacturas = new ModeloTabla();
    private List<Recibo> recibosTotal = new ArrayList<>();
    private List<Recibo> recibosParcial = new ArrayList<>();
    private Cliente clienteSeleccionado;
    private Usuario usuarioSeleccionado;
    private boolean tienePermisoSegunRoles;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(970, 600);
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;    

    public RecibosVentaGUI() {
        this.initComponents();
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 10;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (recibosTotal.size() >= 10) {
                    NUMERO_PAGINA += 1;
                    buscar(false);
                }
            }
        });
    }
    
    private String getUriCriteria() {
        String uriCriteria = "idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa();
        if (chk_Cliente.isSelected() && clienteSeleccionado != null) {
            uriCriteria += "&idCliente=" + clienteSeleccionado.getId_Cliente();
        }
        if (chk_Fecha.isSelected()) {
            uriCriteria += "&desde=" + dc_FechaDesde.getDate().getTime()
                    + "&hasta=" + dc_FechaHasta.getDate().getTime();
        }
        if (chk_Usuario.isSelected() && usuarioSeleccionado != null) {
            uriCriteria += "&idUsuario=" + usuarioSeleccionado.getId_Usuario();
        }
        if (chk_Concepto.isSelected()) {
            uriCriteria += "&concepto=" + txt_Concepto.getText();
        }
        if (chk_NumRecibo.isSelected()) {
            uriCriteria += "&nroSerie=" + Long.valueOf(txt_SerieRecibo.getText()) 
                    + "&nroRecibo=" +  Long.valueOf(txt_NroRecibo.getText());             
        }
        int seleccionOrden = cmbOrden.getSelectedIndex();
        switch (seleccionOrden) {
            case 0:
                uriCriteria += "&ordenarPor=fecha";
                break;
            case 1:
                uriCriteria += "&ordenarPor=concepto";
                break;
            case 2:
                uriCriteria += "&ordenarPor=monto";
                break;
        }
        int seleccionDireccion = cmbSentido.getSelectedIndex();
        switch (seleccionDireccion) {
            case 0:
                uriCriteria += "&sentido=DESC";
                break;
            case 1:
                uriCriteria += "&sentido=ASC";
                break;
        }
        uriCriteria += "&pagina=" + NUMERO_PAGINA;
        return uriCriteria;
    }

    private void setColumnas() {     
        //nombres de columnas
        String[] encabezados = new String[6];
        encabezados[0] = "Fecha Recibo";
        encabezados[1] = "Nº Recibo";
        encabezados[2] = "Cliente";
        encabezados[3] = "Usuario";
        encabezados[4] = "Concepto";
        encabezados[5] = "Monto";
        modeloTablaFacturas.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaFacturas);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaFacturas.getColumnCount()];
        tipos[0] = Date.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = String.class;
        tipos[4] = String.class;
        tipos[5] = BigDecimal.class;
        modeloTablaFacturas.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);        
        //tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(140);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(130);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(320);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(130);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(320);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(130);
        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        tbl_Resultados.getColumnModel().getColumn(0).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
    }

    private void calcularResultados(String uriCriteria) {
        txt_ResultMontoRecibos.setValue(RestClient.getRestTemplate()
                .getForObject("/recibos/venta/monto/criteria?" + uriCriteria, BigDecimal.class));
    }

    private void buscar(boolean calcularResultados) {
        this.cambiarEstadoEnabledComponentes(false);
        String uriCriteria = getUriCriteria();
        try {
            PaginaRespuestaRest<Recibo> response = RestClient.getRestTemplate()
                    .exchange("/recibos/venta/busqueda/criteria?" + uriCriteria, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Recibo>>() {
                    })
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            recibosParcial = response.getContent();
            recibosTotal.addAll(recibosParcial);
            if (calcularResultados && tienePermisoSegunRoles) {
                this.calcularResultados(getUriCriteria());
            }
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
        this.cambiarEstadoDeComponentesSegunRolUsuario();
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
        chk_Cliente.setEnabled(status);
        if (status == true && chk_Cliente.isSelected() == true) {
            btnBuscarCliente.setEnabled(true);
        } else {
            btnBuscarCliente.setEnabled(false);
        }
        chk_Usuario.setEnabled(status);
        if (status == true && chk_Usuario.isSelected() == true) {
            btnBuscarUsuarios.setEnabled(true);
        } else {
            btnBuscarUsuarios.setEnabled(false);
        }
        chk_Usuario.setEnabled(status);
        if (status == true && chk_Usuario.isSelected() == true) {
            btnBuscarUsuarios.setEnabled(true);
        } else {
            btnBuscarUsuarios.setEnabled(false);
        }
        chk_NumRecibo.setEnabled(status);
        if (status == true && chk_NumRecibo.isSelected() == true) {
            txt_SerieRecibo.setEnabled(true);
            txt_NroRecibo.setEnabled(true);
        } else {
            txt_SerieRecibo.setEnabled(false);
            txt_NroRecibo.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);        
        btn_Eliminar.setEnabled(status);
        btn_VerDetalle.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        sp_Resultados.setEnabled(status);
        tbl_Resultados.requestFocus();
    }

    private void cargarResultadosAlTable() {
        recibosParcial.stream().map(recibo -> {
            Object[] fila = new Object[6];
            fila[0] = recibo.getFecha();
            if (recibo.getNumSerie() == 0 && recibo.getNumRecibo() == 0) {
                fila[1] = "";
            } else {
                fila[1] = recibo.getNumSerie() + " - " + recibo.getNumRecibo();
            }
            fila[2] = recibo.getNombreFiscalCliente();
            fila[3] = recibo.getNombreUsuario();
            fila[4] = recibo.getConcepto();
            fila[5] = recibo.getMonto();
            return fila;
        }).forEach(fila -> {
            modeloTablaFacturas.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaFacturas);
        lbl_cantResultados.setText(totalElementosBusqueda + " recibos encontrados");
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        recibosTotal.clear();
        recibosParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }

    private void limpiarJTable() {
        modeloTablaFacturas = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaFacturas);
        this.setColumnas();
    }

    private void limpiarYBuscar(boolean calcularResultados) {
        this.resetScroll();
        this.limpiarJTable();
        this.buscar(calcularResultados);
    }

    private void lanzarReporteFactura() {
        if (Desktop.isDesktopSupported()) {
            try {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/recibos/" + recibosTotal.get(indexFilaSeleccionada).getIdRecibo() + "/reporte",
                                byte[].class);
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
    }

    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)) {
            btn_Eliminar.setEnabled(true);
        } else {
            btn_Eliminar.setEnabled(false);
        }
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR) 
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)) {
            lbl_MontosRecibos.setVisible(true);
            txt_ResultMontoRecibos.setVisible(true);
        } else {
            lbl_MontosRecibos.setVisible(false);
            txt_ResultMontoRecibos.setVisible(false);
        }
        tienePermisoSegunRoles = rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_VerDetalle = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        panelNumeros = new javax.swing.JPanel();
        lbl_MontosRecibos = new javax.swing.JLabel();
        txt_ResultMontoRecibos = new javax.swing.JFormattedTextField();
        panelFiltros = new javax.swing.JPanel();
        subPanelFiltros1 = new javax.swing.JPanel();
        chk_Fecha = new javax.swing.JCheckBox();
        chk_Cliente = new javax.swing.JCheckBox();
        lbl_Hasta = new javax.swing.JLabel();
        lbl_Desde = new javax.swing.JLabel();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        chk_Usuario = new javax.swing.JCheckBox();
        txtCliente = new javax.swing.JTextField();
        btnBuscarCliente = new javax.swing.JButton();
        txtUsuario = new javax.swing.JTextField();
        btnBuscarUsuarios = new javax.swing.JButton();
        chk_Concepto = new javax.swing.JCheckBox();
        txt_Concepto = new javax.swing.JTextField();
        subPanelFiltros2 = new javax.swing.JPanel();
        chk_NumRecibo = new javax.swing.JCheckBox();
        txt_SerieRecibo = new javax.swing.JFormattedTextField();
        separador = new javax.swing.JLabel();
        txt_NroRecibo = new javax.swing.JFormattedTextField();
        btn_Buscar = new javax.swing.JButton();
        lbl_cantResultados = new javax.swing.JLabel();
        panelOrden = new javax.swing.JPanel();
        cmbOrden = new javax.swing.JComboBox<>();
        cmbSentido = new javax.swing.JComboBox<>();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Facturas de Venta");
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
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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

        lbl_MontosRecibos.setText("Monto Total:");

        txt_ResultMontoRecibos.setEditable(false);
        txt_ResultMontoRecibos.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultMontoRecibos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout panelNumerosLayout = new javax.swing.GroupLayout(panelNumeros);
        panelNumeros.setLayout(panelNumerosLayout);
        panelNumerosLayout.setHorizontalGroup(
            panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNumerosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_MontosRecibos, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_ResultMontoRecibos, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelNumerosLayout.setVerticalGroup(
            panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNumerosLayout.createSequentialGroup()
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_MontosRecibos)
                    .addComponent(txt_ResultMontoRecibos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btn_Eliminar)
                .addGap(0, 0, 0)
                .addComponent(btn_VerDetalle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelNumeros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sp_Resultados)
                .addContainerGap())
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Eliminar, btn_VerDetalle});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_VerDetalle)
                            .addComponent(btn_Eliminar)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelNumeros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Eliminar, btn_VerDetalle});

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Fecha.setText("Fecha Recibo:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

        chk_Cliente.setText("Cliente:");
        chk_Cliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ClienteItemStateChanged(evt);
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

        chk_Usuario.setText("Usuario:");
        chk_Usuario.setToolTipText("");
        chk_Usuario.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UsuarioItemStateChanged(evt);
            }
        });

        txtCliente.setEditable(false);
        txtCliente.setEnabled(false);
        txtCliente.setOpaque(false);

        btnBuscarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarCliente.setEnabled(false);
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        txtUsuario.setEditable(false);
        txtUsuario.setEnabled(false);
        txtUsuario.setOpaque(false);

        btnBuscarUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarUsuarios.setEnabled(false);
        btnBuscarUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarUsuariosActionPerformed(evt);
            }
        });

        chk_Concepto.setText("Concepto:");
        chk_Concepto.setToolTipText("");
        chk_Concepto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ConceptoItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout subPanelFiltros1Layout = new javax.swing.GroupLayout(subPanelFiltros1);
        subPanelFiltros1.setLayout(subPanelFiltros1Layout);
        subPanelFiltros1Layout.setHorizontalGroup(
            subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chk_Concepto, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addComponent(lbl_Desde)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaDesde, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_Hasta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaHasta, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCliente)
                            .addComponent(txtUsuario))
                        .addGap(0, 0, 0)
                        .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBuscarCliente, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnBuscarUsuarios, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(txt_Concepto)))
        );
        subPanelFiltros1Layout.setVerticalGroup(
            subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Desde)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Hasta)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Fecha))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Cliente)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Usuario)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarUsuarios))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chk_Concepto)
                    .addComponent(txt_Concepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        subPanelFiltros1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarUsuarios, txtUsuario, txt_Concepto});

        subPanelFiltros1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarCliente, txtCliente});

        chk_NumRecibo.setText("Nº de Recibo:");
        chk_NumRecibo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumReciboItemStateChanged(evt);
            }
        });

        txt_SerieRecibo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_SerieRecibo.setText("0");
        txt_SerieRecibo.setEnabled(false);
        txt_SerieRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_SerieReciboActionPerformed(evt);
            }
        });
        txt_SerieRecibo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_SerieReciboKeyTyped(evt);
            }
        });

        separador.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        separador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        separador.setText("-");

        txt_NroRecibo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_NroRecibo.setText("0");
        txt_NroRecibo.setEnabled(false);
        txt_NroRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_NroReciboActionPerformed(evt);
            }
        });
        txt_NroRecibo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NroReciboKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout subPanelFiltros2Layout = new javax.swing.GroupLayout(subPanelFiltros2);
        subPanelFiltros2.setLayout(subPanelFiltros2Layout);
        subPanelFiltros2Layout.setHorizontalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, subPanelFiltros2Layout.createSequentialGroup()
                .addComponent(chk_NumRecibo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_SerieRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separador, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_NroRecibo, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addContainerGap())
        );
        subPanelFiltros2Layout.setVerticalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(chk_NumRecibo)
                .addComponent(txt_SerieRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(separador)
                .addComponent(txt_NroRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGap(6, 6, 6)
                .addComponent(subPanelFiltros1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(subPanelFiltros2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_Buscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(subPanelFiltros1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subPanelFiltros2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Buscar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelOrden.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordenar por"));

        cmbOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fecha Recibo", "Concepto", "Monto" }));
        cmbOrden.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbOrdenItemStateChanged(evt);
            }
        });

        cmbSentido.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Descendente", "Ascendente" }));
        cmbSentido.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSentidoItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelOrdenLayout = new javax.swing.GroupLayout(panelOrden);
        panelOrden.setLayout(panelOrdenLayout);
        panelOrdenLayout.setHorizontalGroup(
            panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbOrden, 0, 158, Short.MAX_VALUE)
                    .addComponent(cmbSentido, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelOrdenLayout.setVerticalGroup(
            panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenLayout.createSequentialGroup()
                .addComponent(cmbOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbSentido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 76, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void chk_ClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ClienteItemStateChanged
        if (chk_Cliente.isSelected() == true) {
            btnBuscarCliente.setEnabled(true);
            btnBuscarCliente.requestFocus();
            txtCliente.setEnabled(true);
        } else {
            btnBuscarCliente.setEnabled(false);
            txtCliente.setEnabled(false);
        }
}//GEN-LAST:event_chk_ClienteItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.limpiarYBuscar(true);
}//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_VerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerDetalleActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            this.lanzarReporteFactura();
        }
}//GEN-LAST:event_btn_VerDetalleActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int respuesta = JOptionPane.showConfirmDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_eliminar_multiples_recibo"),
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    RestClient.getRestTemplate().delete("/recibos?idRecibo="
                            + recibosTotal.get(Utilidades.getSelectedRowModelIndice(tbl_Resultados)));
                    this.limpiarYBuscar(true);
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

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setSize(sizeInternalFrame);
            this.setColumnas();
            this.setMaximum(true);
            dc_FechaDesde.setDate(new Date());
            dc_FechaHasta.setDate(new Date());
            this.cambiarEstadoDeComponentesSegunRolUsuario();
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }        
    }//GEN-LAST:event_formInternalFrameOpened

    private void chk_UsuarioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UsuarioItemStateChanged
        if (chk_Usuario.isSelected() == true) {
            btnBuscarUsuarios.setEnabled(true);
            btnBuscarUsuarios.requestFocus();
            txtUsuario.setEnabled(true);
        } else {
            btnBuscarUsuarios.setEnabled(false);
            txtUsuario.setEnabled(false);
        }
    }//GEN-LAST:event_chk_UsuarioItemStateChanged

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        BuscarClientesGUI buscarClientesGUI = new BuscarClientesGUI();
        buscarClientesGUI.setModal(true);
        buscarClientesGUI.setLocationRelativeTo(this);
        buscarClientesGUI.setVisible(true);
        if (buscarClientesGUI.getClienteSeleccionado() != null) {
            clienteSeleccionado = buscarClientesGUI.getClienteSeleccionado();
            txtCliente.setText(clienteSeleccionado.getNombreFiscal());
        }
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void btnBuscarUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarUsuariosActionPerformed
        Rol[] rolesParaFiltrar = new Rol[]{Rol.ADMINISTRADOR, Rol.ENCARGADO, Rol.VENDEDOR};
        BuscarUsuariosGUI buscarUsuariosGUI = new BuscarUsuariosGUI(rolesParaFiltrar);
        buscarUsuariosGUI.setModal(true);
        buscarUsuariosGUI.setLocationRelativeTo(this);
        buscarUsuariosGUI.setVisible(true);
        if (buscarUsuariosGUI.getUsuarioSeleccionado() != null) {
            usuarioSeleccionado = buscarUsuariosGUI.getUsuarioSeleccionado();
            txtUsuario.setText(usuarioSeleccionado.toString());
        }
    }//GEN-LAST:event_btnBuscarUsuariosActionPerformed

    private void cmbOrdenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbOrdenItemStateChanged
        this.limpiarYBuscar(true);
    }//GEN-LAST:event_cmbOrdenItemStateChanged

    private void cmbSentidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSentidoItemStateChanged
        this.limpiarYBuscar(true);
    }//GEN-LAST:event_cmbSentidoItemStateChanged

    private void chk_ConceptoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ConceptoItemStateChanged
        if (chk_Concepto.isSelected() == true) {
            txt_Concepto.requestFocus();
            txt_Concepto.setEnabled(true);
        } else {
            txt_Concepto.setEnabled(false);
        }
    }//GEN-LAST:event_chk_ConceptoItemStateChanged

    private void txt_NroReciboKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NroReciboKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_NroReciboKeyTyped

    private void txt_NroReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_NroReciboActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_NroReciboActionPerformed

    private void txt_SerieReciboKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_SerieReciboKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_SerieReciboKeyTyped

    private void txt_SerieReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_SerieReciboActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_SerieReciboActionPerformed

    private void chk_NumReciboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_NumReciboItemStateChanged
        if (chk_NumRecibo.isSelected() == true) {
            txt_NroRecibo.setEnabled(true);
            txt_SerieRecibo.setEnabled(true);
        } else {
            txt_NroRecibo.setEnabled(false);
            txt_SerieRecibo.setEnabled(false);
        }
    }//GEN-LAST:event_chk_NumReciboItemStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnBuscarUsuarios;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_VerDetalle;
    private javax.swing.JCheckBox chk_Cliente;
    private javax.swing.JCheckBox chk_Concepto;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_NumRecibo;
    private javax.swing.JCheckBox chk_Usuario;
    private javax.swing.JComboBox<String> cmbOrden;
    private javax.swing.JComboBox<String> cmbSentido;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JLabel lbl_Desde;
    private javax.swing.JLabel lbl_Hasta;
    private javax.swing.JLabel lbl_MontosRecibos;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelNumeros;
    private javax.swing.JPanel panelOrden;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JLabel separador;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JPanel subPanelFiltros1;
    private javax.swing.JPanel subPanelFiltros2;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtUsuario;
    private javax.swing.JTextField txt_Concepto;
    private javax.swing.JFormattedTextField txt_NroRecibo;
    private javax.swing.JFormattedTextField txt_ResultMontoRecibos;
    private javax.swing.JFormattedTextField txt_SerieRecibo;
    // End of variables declaration//GEN-END:variables
}
