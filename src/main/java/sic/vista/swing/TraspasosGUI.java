package sic.vista.swing;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Usuario;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.RenglonTraspaso;
import sic.modelo.Rol;
import sic.modelo.Traspaso;
import sic.modelo.criteria.BusquedaTraspasoCriteria;
import sic.util.DecimalesRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class TraspasosGUI extends JInternalFrame {

    private ModeloTabla modeloTablaTraspasos = new ModeloTabla();
    private ModeloTabla modeloTablaDetalle = new ModeloTabla();
    private List<Traspaso> traspasosTotal = new ArrayList<>();
    private List<Traspaso> traspasosParcial = new ArrayList<>();
    private Usuario usuarioSeleccionado;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(970, 600);
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;

    public TraspasosGUI() {
        this.initComponents();
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va  = scrollBar.getVisibleAmount() + 10;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (traspasosTotal.size() >= 10) {
                    NUMERO_PAGINA += 1;
                    buscar();
                }
            }
        });
    }

    private BusquedaTraspasoCriteria getCriteria() {
        BusquedaTraspasoCriteria criteria = BusquedaTraspasoCriteria.builder().build();
        if (chk_Fecha.isSelected()) {
            criteria.setFechaDesde((dc_FechaDesde.getDate() != null) ? LocalDateTime.ofInstant(dc_FechaDesde.getDate().toInstant(), ZoneId.systemDefault()) : null);
            criteria.setFechaHasta((dc_FechaHasta.getDate() != null) ? LocalDateTime.ofInstant(dc_FechaHasta.getDate().toInstant(), ZoneId.systemDefault()) : null);
        }
        if (chk_Usuario.isSelected() && usuarioSeleccionado != null) {
            criteria.setIdUsuario(usuarioSeleccionado.getIdUsuario());
        }
        if (chk_NumTraspaso.isSelected()) {
            criteria.setNroTraspaso(txt_NroTraspaso.getText());
        }
        int seleccionOrden = cmbOrden.getSelectedIndex();
        switch (seleccionOrden) {
            case 0:
                criteria.setOrdenarPor("fechaDeAlta");
                break;
            case 1:
                criteria.setOrdenarPor("nroTraspaso");
                break;
            case 2:
                criteria.setOrdenarPor("sucursalOrigen.nombre");
                break;
            case 3:
                criteria.setOrdenarPor("sucursalDestino.nombre");
                break;
        }
        int seleccionDireccion = cmbSentido.getSelectedIndex();
        switch (seleccionDireccion) {
            case 0:
                criteria.setSentido("DESC");
                break;
            case 1:
                criteria.setSentido("ASC");
                break;
        }
        criteria.setPagina(NUMERO_PAGINA);
        return criteria;
    }

    private void setColumnasTraspasos() {
        //nombres de columnas
        String[] encabezados = new String[6];
        encabezados[0] = "Fecha Traspaso";
        encabezados[1] = "Nº Traspaso";
        encabezados[2] = "Nº Pedido";
        encabezados[3] = "Sucursal Origen";
        encabezados[4] = "Sucursal Destino";
        encabezados[5] = "Usuario";
        modeloTablaTraspasos.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaTraspasos);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaTraspasos.getColumnCount()];
        tipos[0] = LocalDateTime.class;
        tipos[1] = String.class;
        tipos[2] = Long.class;
        tipos[3] = String.class;
        tipos[4] = String.class;
        tipos[5] = String.class;
        modeloTablaTraspasos.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);
        //tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(140);
        tbl_Resultados.getColumnModel().getColumn(0).setMaxWidth(140);
        tbl_Resultados.getColumnModel().getColumn(0).setMinWidth(140);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(130);
        tbl_Resultados.getColumnModel().getColumn(1).setMaxWidth(130);
        tbl_Resultados.getColumnModel().getColumn(1).setMinWidth(130);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(130);
        tbl_Resultados.getColumnModel().getColumn(2).setMaxWidth(130);
        tbl_Resultados.getColumnModel().getColumn(2).setMinWidth(130);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(220);
        tbl_Resultados.getColumnModel().getColumn(3).setMaxWidth(220);
        tbl_Resultados.getColumnModel().getColumn(3).setMinWidth(220);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(220);
        tbl_Resultados.getColumnModel().getColumn(4).setMaxWidth(220);
        tbl_Resultados.getColumnModel().getColumn(4).setMinWidth(220);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(220);
        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        tbl_Resultados.getColumnModel().getColumn(0).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
    }

    private void setColumnasDetalles() {
        //nombres de columnas
        String[] encabezados = new String[4];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripción";
        encabezados[2] = "Medida";
        encabezados[3] = "Cantidad";
        modeloTablaDetalle.setColumnIdentifiers(encabezados);
        tbl_Detalles.setModel(modeloTablaDetalle);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaDetalle.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = String.class;
        modeloTablaDetalle.setClaseColumnas(tipos);
        tbl_Detalles.getTableHeader().setReorderingAllowed(false);
        tbl_Detalles.getTableHeader().setResizingAllowed(true);
        //tamanios de columnas
        tbl_Detalles.getColumnModel().getColumn(0).setPreferredWidth(150);
        tbl_Detalles.getColumnModel().getColumn(0).setMaxWidth(150);
        tbl_Detalles.getColumnModel().getColumn(0).setMinWidth(150);
        tbl_Detalles.getColumnModel().getColumn(2).setPreferredWidth(220);
        tbl_Detalles.getColumnModel().getColumn(2).setMaxWidth(220);
        tbl_Detalles.getColumnModel().getColumn(2).setMinWidth(220);
        tbl_Detalles.getColumnModel().getColumn(3).setPreferredWidth(220);
        tbl_Detalles.getColumnModel().getColumn(3).setMaxWidth(220);
        tbl_Detalles.getColumnModel().getColumn(3).setMinWidth(220);
        //render para los tipos de datos
        tbl_Detalles.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
    }

    private void buscar() {
        this.cambiarEstadoEnabledComponentes(false);
        try {
            HttpEntity<BusquedaTraspasoCriteria> requestEntity = new HttpEntity<>(this.getCriteria());
            PaginaRespuestaRest<Traspaso> response = RestClient.getRestTemplate()
                    .exchange("/traspasos/busqueda/criteria", HttpMethod.POST, requestEntity,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Traspaso>>() {
                    })
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            traspasosParcial = response.getContent();
            traspasosTotal.addAll(traspasosParcial);
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

    private void cambiarEstadoEnabledComponentes(boolean status) {
        chk_Fecha.setEnabled(status);
        if (status == true && chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
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
        chk_NumTraspaso.setEnabled(status);
        if (status == true && chk_NumTraspaso.isSelected() == true) {
            txt_NroTraspaso.setEnabled(true);
        } else {
            txt_NroTraspaso.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        sp_Resultados.setEnabled(status);
        tbl_Resultados.requestFocus();
    }

    private void cargarResultadosAlTable() {
        traspasosParcial.stream().map(traspaso -> {
            Object[] fila = new Object[6];
            fila[0] = traspaso.getFechaDeAlta();
            fila[1] = traspaso.getNroTraspaso();
            fila[2] = traspaso.getNroPedido();
            fila[3] = traspaso.getNombreSucursalOrigen();
            fila[4] = traspaso.getNombreSucursalDestino();
            fila[5] = traspaso.getNombreUsuario();
            return fila;
        }).forEach(fila -> {
            modeloTablaTraspasos.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaTraspasos);
        lbl_cantResultados.setText(totalElementosBusqueda + " traspasos encontrados");
    }

    private void limpiarTablaDetalle() {
        modeloTablaDetalle = new ModeloTabla();
        tbl_Detalles.setModel(modeloTablaDetalle);
        this.setColumnasDetalles();
    }

    private void cargarTablaDetalle(long idTraspaso) {
        this.limpiarTablaDetalle();
        if (idTraspaso != 0) {
            //try
            List<RenglonTraspaso> renglonesTraspaso = Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/traspasos/" + idTraspaso + "/renglones", RenglonTraspaso[].class));
            Object[] renglonMovimiento = new Object[4];
            renglonesTraspaso.forEach(renglon -> {
                renglonMovimiento[0] = renglon.getCodigoProducto();
                renglonMovimiento[1] = renglon.getDescripcionProducto();
                renglonMovimiento[2] = renglon.getNombreMedidaProducto();
                renglonMovimiento[3] = renglon.getCantidadProducto();
                modeloTablaDetalle.addRow(renglonMovimiento);
            });
            tbl_Detalles.setModel(modeloTablaDetalle);
        }
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        traspasosTotal.clear();
        traspasosParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }

    private void limpiarJTableTraspasos() {
        modeloTablaTraspasos = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaTraspasos);
        this.setColumnasTraspasos();
    }

    private void limpiarJTableRenglones() {
        modeloTablaDetalle = new ModeloTabla();
        tbl_Detalles.setModel(modeloTablaDetalle);
        this.setColumnasDetalles();
    }

    private void limpiarYBuscar() {
        this.resetScroll();
        this.limpiarJTableTraspasos();
        this.limpiarJTableRenglones();
        this.buscar();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        sp_Detalle = new javax.swing.JScrollPane();
        tbl_Detalles = new javax.swing.JTable();
        lbl_movimientos = new javax.swing.JLabel();
        btnImprimirPedido = new javax.swing.JButton();
        panelFiltros = new javax.swing.JPanel();
        subPanelFiltros2 = new javax.swing.JPanel();
        chk_NumTraspaso = new javax.swing.JCheckBox();
        txt_NroTraspaso = new javax.swing.JFormattedTextField();
        chk_Fecha = new javax.swing.JCheckBox();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        chk_Usuario = new javax.swing.JCheckBox();
        txtUsuario = new javax.swing.JTextField();
        btnBuscarUsuarios = new javax.swing.JButton();
        btn_Buscar = new javax.swing.JButton();
        lbl_cantResultados = new javax.swing.JLabel();
        panelOrden = new javax.swing.JPanel();
        cmbOrden = new javax.swing.JComboBox<>();
        cmbSentido = new javax.swing.JComboBox<>();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Traspasos");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Traspaso_16x16.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        panelResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Resultados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_ResultadosMouseClicked(evt);
            }
        });
        tbl_Resultados.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_ResultadosKeyPressed(evt);
            }
        });
        sp_Resultados.setViewportView(tbl_Resultados);

        tbl_Detalles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        sp_Detalle.setViewportView(tbl_Detalles);

        lbl_movimientos.setText("Detalle Traspaso (Seleccione uno de la lista superior)");

        btnImprimirPedido.setForeground(new java.awt.Color(0, 0, 255));
        btnImprimirPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/target_16x16.png"))); // NOI18N
        btnImprimirPedido.setText("Ver Detalle");
        btnImprimirPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirPedidoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 815, Short.MAX_VALUE)
            .addComponent(sp_Detalle)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(lbl_movimientos, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnImprimirPedido)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_movimientos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Detalle, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImprimirPedido)
                .addContainerGap())
        );

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_NumTraspaso.setText("Nº Traspaso:");
        chk_NumTraspaso.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumTraspasoItemStateChanged(evt);
            }
        });

        txt_NroTraspaso.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_NroTraspaso.setText("0");
        txt_NroTraspaso.setEnabled(false);
        txt_NroTraspaso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_NroTraspasoActionPerformed(evt);
            }
        });
        txt_NroTraspaso.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NroTraspasoKeyTyped(evt);
            }
        });

        chk_Fecha.setText("Fecha Traspaso:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

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

        javax.swing.GroupLayout subPanelFiltros2Layout = new javax.swing.GroupLayout(subPanelFiltros2);
        subPanelFiltros2.setLayout(subPanelFiltros2Layout);
        subPanelFiltros2Layout.setHorizontalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(subPanelFiltros2Layout.createSequentialGroup()
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(chk_NumTraspaso, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Fecha, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Usuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, subPanelFiltros2Layout.createSequentialGroup()
                        .addComponent(txtUsuario)
                        .addGap(0, 0, 0)
                        .addComponent(btnBuscarUsuarios))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, subPanelFiltros2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_NroTraspaso, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, subPanelFiltros2Layout.createSequentialGroup()
                                .addComponent(dc_FechaDesde, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        subPanelFiltros2Layout.setVerticalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros2Layout.createSequentialGroup()
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Fecha)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_NumTraspaso)
                    .addComponent(txt_NroTraspaso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Usuario)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarUsuarios))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        subPanelFiltros2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarUsuarios, txtUsuario});

        subPanelFiltros2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {dc_FechaDesde, dc_FechaHasta});

        subPanelFiltros2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chk_Fecha, chk_NumTraspaso});

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
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(subPanelFiltros2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addComponent(subPanelFiltros2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Buscar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelOrden.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordenar por"));

        cmbOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fecha Traspaso", "Nº Traspaso", "Sucursal Origen", "Sucursal Destino" }));
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
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(117, Short.MAX_VALUE))
            .addComponent(panelResultados, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void chk_FechaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FechaItemStateChanged
        if (chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
            dc_FechaDesde.requestFocus();
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
        }
}//GEN-LAST:event_chk_FechaItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.limpiarYBuscar();
}//GEN-LAST:event_btn_BuscarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setSize(sizeInternalFrame);
            this.setColumnasTraspasos();
            this.setColumnasDetalles();
            this.setMaximum(true);
            dc_FechaDesde.setDate(new Date());
            dc_FechaHasta.setDate(new Date());
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void cmbOrdenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbOrdenItemStateChanged
        this.limpiarYBuscar();
    }//GEN-LAST:event_cmbOrdenItemStateChanged

    private void cmbSentidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSentidoItemStateChanged
        this.limpiarYBuscar();
    }//GEN-LAST:event_cmbSentidoItemStateChanged

    private void txt_NroTraspasoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NroTraspasoKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_NroTraspasoKeyTyped

    private void txt_NroTraspasoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_NroTraspasoActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_NroTraspasoActionPerformed

    private void chk_NumTraspasoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_NumTraspasoItemStateChanged
        if (chk_NumTraspaso.isSelected() == true) {
            txt_NroTraspaso.setEnabled(true);
        } else {
            txt_NroTraspaso.setEnabled(false);
        }
    }//GEN-LAST:event_chk_NumTraspasoItemStateChanged

    private void btnBuscarUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarUsuariosActionPerformed
        Rol[] rolesParaFiltrar = new Rol[]{Rol.ADMINISTRADOR, Rol.ENCARGADO, Rol.VENDEDOR, Rol.COMPRADOR};
        BuscarUsuariosGUI buscarUsuariosGUI = new BuscarUsuariosGUI(rolesParaFiltrar, "Buscar Usuario");
        buscarUsuariosGUI.setModal(true);
        buscarUsuariosGUI.setLocationRelativeTo(this);
        buscarUsuariosGUI.setVisible(true);
        if (buscarUsuariosGUI.getUsuarioSeleccionado() != null) {
            usuarioSeleccionado = buscarUsuariosGUI.getUsuarioSeleccionado();
            txtUsuario.setText(usuarioSeleccionado.toString());
        }
    }//GEN-LAST:event_btnBuscarUsuariosActionPerformed

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

    private void tbl_ResultadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ResultadosMouseClicked
        this.cargarTablaDetalle(traspasosTotal.get(Utilidades.getSelectedRowModelIndice(tbl_Resultados)).getIdTraspaso());
    }//GEN-LAST:event_tbl_ResultadosMouseClicked

    private void tbl_ResultadosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadosKeyPressed
        int row = tbl_Resultados.getSelectedRow();
        if (row != -1) {
            row = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            if ((evt.getKeyCode() == KeyEvent.VK_UP) && row > 0) {
                row--;
            }
            if ((evt.getKeyCode() == KeyEvent.VK_DOWN) && (row + 1) < tbl_Resultados.getRowCount()) {
                row++;
            }
            try {
                if (row != -1) {
                    this.cargarTablaDetalle(this.traspasosTotal.get(row).getIdTraspaso());
                } else {
                    this.limpiarTablaDetalle();
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
    }//GEN-LAST:event_tbl_ResultadosKeyPressed

    private void btnImprimirPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirPedidoActionPerformed
        if (Desktop.isDesktopSupported()) {
            try {
                byte[] reporte = RestClient.getRestTemplate().postForObject("/traspasos/reporte/criteria", this.getCriteria(), byte[].class);
                File f = new File(System.getProperty("user.home") + "/Traspasos.pdf");
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
    }//GEN-LAST:event_btnImprimirPedidoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarUsuarios;
    private javax.swing.JButton btnImprimirPedido;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_NumTraspaso;
    private javax.swing.JCheckBox chk_Usuario;
    private javax.swing.JComboBox<String> cmbOrden;
    private javax.swing.JComboBox<String> cmbSentido;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JLabel lbl_movimientos;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelOrden;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane sp_Detalle;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JPanel subPanelFiltros2;
    private javax.swing.JTable tbl_Detalles;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txtUsuario;
    private javax.swing.JFormattedTextField txt_NroTraspaso;
    // End of variables declaration//GEN-END:variables
}
