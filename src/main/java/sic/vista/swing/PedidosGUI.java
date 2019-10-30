package sic.vista.swing;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import sic.util.ColoresEstadosRenderer;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.modelo.Pedido;
import sic.modelo.Usuario;
import sic.modelo.EstadoPedido;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Producto;
import sic.modelo.Rol;
import sic.modelo.UsuarioActivo;
import sic.modelo.criteria.BusquedaClienteCriteria;
import sic.modelo.criteria.BusquedaPedidoCriteria;
import sic.util.DecimalesRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class PedidosGUI extends JInternalFrame {

    private List<Pedido> pedidosTotal = new ArrayList<>();    
    private List<Pedido> pedidosParcial = new ArrayList<>();
    private ModeloTabla modeloTablaPedidos;    
    private Cliente clienteSeleccionado;
    private Usuario usuarioSeleccionado;  
    private Usuario viajanteSeleccionado;
    private Producto productoSeleccionado;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame =  new Dimension(880, 600);
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;    

    public PedidosGUI() {
        this.initComponents();
        sp_Pedidos.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 10;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (pedidosTotal.size() >= 10) {
                    NUMERO_PAGINA += 1;
                    buscar();
                }
            }
        });
    }

    private void buscar() {
        this.cambiarEstadoEnabledComponentes(false);
        BusquedaPedidoCriteria criteria = BusquedaPedidoCriteria.builder().build();
        criteria.setIdEmpresa(EmpresaActiva.getInstance().getEmpresa().getId_Empresa());
        if (chk_Fecha.isSelected()) {
            criteria.setFechaDesde((dc_FechaDesde.getDate() != null) ? dc_FechaDesde.getDate() : null);
            criteria.setFechaHasta((dc_FechaHasta.getDate() != null) ? dc_FechaHasta.getDate() : null);
        }
        if (chk_NumeroPedido.isSelected()) {
            criteria.setNroPedido(Long.valueOf(txt_NumeroPedido.getText()));
        }
        if (chkEstado.isSelected()) {
            criteria.setEstadoPedido((EstadoPedido) cmbEstado.getSelectedItem());
        }
        if (chk_Cliente.isSelected() && clienteSeleccionado != null) {
            criteria.setIdCliente(clienteSeleccionado.getId_Cliente());
        }
        if (chk_Usuario.isSelected() && usuarioSeleccionado != null) {
            criteria.setIdUsuario(usuarioSeleccionado.getId_Usuario());
        }
        if (chk_Viajante.isSelected() && viajanteSeleccionado != null) {
            criteria.setIdViajante(viajanteSeleccionado.getId_Usuario());
        }
        if (chk_Producto.isSelected() && productoSeleccionado != null) {
            criteria.setIdProducto(productoSeleccionado.getIdProducto());
        }
        criteria.setPagina(NUMERO_PAGINA);
        try {
            HttpEntity<BusquedaPedidoCriteria> requestEntity = new HttpEntity<>(criteria);
            PaginaRespuestaRest<Pedido> response = RestClient.getRestTemplate()
                    .exchange("/pedidos/busqueda/criteria", HttpMethod.POST, requestEntity,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Pedido>>() {
                    })
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            pedidosParcial = response.getContent();
            pedidosTotal.addAll(pedidosParcial);
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

    private void verFacturasVenta() {
        if (tbl_Pedidos.getSelectedRow() != -1) {
            FacturasVentaGUI gui_facturaVenta = new FacturasVentaGUI();
            gui_facturaVenta.setLocation(getDesktopPane().getWidth() / 2 - gui_facturaVenta.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui_facturaVenta.getHeight() / 2);
            getDesktopPane().add(gui_facturaVenta);
            long numeroDePedido = (long) tbl_Pedidos.getValueAt(tbl_Pedidos.getSelectedRow(), 2);
            gui_facturaVenta.setVisible(true);
            gui_facturaVenta.buscarPorNroPedido(numeroDePedido);
            try {
                gui_facturaVenta.setSelected(true);
            } catch (PropertyVetoException ex) {
                String mensaje = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(mensaje + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            }
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
        chk_NumeroPedido.setEnabled(status);
        if (status == true && chk_NumeroPedido.isSelected() == true) {
            txt_NumeroPedido.setEnabled(true);
        } else {
            txt_NumeroPedido.setEnabled(false);
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
        btn_Buscar.setEnabled(status);        
        btnNuevoPedido.setEnabled(status);
        btnVerFacturas.setEnabled(status);
        btnFacturar.setEnabled(status);
        btnModificarPedido.setEnabled(status);
        btnEliminarPedido.setEnabled(status);
        btnImprimirPedido.setEnabled(status);
        tbl_Pedidos.setEnabled(status);
        sp_Pedidos.setEnabled(status);
        tbl_Pedidos.requestFocus();
    }
    
    private void cargarResultadosAlTable() {
        pedidosParcial.stream().map(p -> {
            Object[] fila = new Object[8];
            fila[0] = p.getEstado();
            fila[1] = p.getFecha();
            fila[2] = p.getNroPedido();
            fila[3] = p.getNombreFiscalCliente();
            fila[4] = p.getNombreUsuario();
            fila[5] = p.getNombreViajante();
            fila[6] = p.getTotalEstimado();
            fila[7] = p.getTotalActual();
            return fila;
        }).forEach(f -> {
            modeloTablaPedidos.addRow(f);
        });
        tbl_Pedidos.setModel(modeloTablaPedidos);
        tbl_Pedidos.setDefaultRenderer(EstadoPedido.class, new ColoresEstadosRenderer());        
        lbl_cantResultados.setText(totalElementosBusqueda + " pedidos encontrados");
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        pedidosTotal.clear();
        pedidosParcial.clear();
        Point p = new Point(0, 0);
        sp_Pedidos.getViewport().setViewPosition(p);
    }
    
    private void limpiarJTables() {
        modeloTablaPedidos = new ModeloTabla();
        tbl_Pedidos.setModel(modeloTablaPedidos);        
        this.setColumnas();
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[8];
        encabezados[0] = "Estado";
        encabezados[1] = "Fecha Pedido";
        encabezados[2] = "Nº Pedido";
        encabezados[3] = "Cliente";
        encabezados[4] = "Usuario";
        encabezados[5] = "Viajante";
        encabezados[6] = "Total Estimado";
        encabezados[7] = "Total Actual";
        modeloTablaPedidos.setColumnIdentifiers(encabezados);
        tbl_Pedidos.setModel(modeloTablaPedidos);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaPedidos.getColumnCount()];
        tipos[0] = EstadoPedido.class;
        tipos[1] = Date.class;
        tipos[2] = Long.class;
        tipos[3] = String.class;
        tipos[4] = String.class;
        tipos[5] = String.class;
        tipos[6] = BigDecimal.class;
        tipos[7] = BigDecimal.class;
        modeloTablaPedidos.setClaseColumnas(tipos);
        tbl_Pedidos.getTableHeader().setReorderingAllowed(false);
        tbl_Pedidos.getTableHeader().setResizingAllowed(true);
        //tamanios de columnas
        tbl_Pedidos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tbl_Pedidos.getColumnModel().getColumn(0).setMaxWidth(80);
        tbl_Pedidos.getColumnModel().getColumn(1).setPreferredWidth(140);
        tbl_Pedidos.getColumnModel().getColumn(1).setMaxWidth(140);
        tbl_Pedidos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tbl_Pedidos.getColumnModel().getColumn(2).setMaxWidth(100);
        tbl_Pedidos.getColumnModel().getColumn(3).setPreferredWidth(220);
        tbl_Pedidos.getColumnModel().getColumn(3).setMaxWidth(220);
        tbl_Pedidos.getColumnModel().getColumn(3).setMinWidth(220);
        tbl_Pedidos.getColumnModel().getColumn(4).setPreferredWidth(220);
        tbl_Pedidos.getColumnModel().getColumn(4).setMaxWidth(220);
        tbl_Pedidos.getColumnModel().getColumn(4).setMinWidth(220);
        tbl_Pedidos.getColumnModel().getColumn(5).setPreferredWidth(220);
        tbl_Pedidos.getColumnModel().getColumn(5).setMaxWidth(220);
        tbl_Pedidos.getColumnModel().getColumn(5).setMinWidth(220);
        tbl_Pedidos.getColumnModel().getColumn(6).setPreferredWidth(25);
        tbl_Pedidos.getColumnModel().getColumn(7).setPreferredWidth(25);
        //renderers
        tbl_Pedidos.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        tbl_Pedidos.getColumnModel().getColumn(1).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
    }

    private boolean existeClienteDisponible() {
        BusquedaClienteCriteria criteriaCliente = BusquedaClienteCriteria.builder()
                .idEmpresa(EmpresaActiva.getInstance().getEmpresa().getId_Empresa())
                .pagina(0)
                .build();
        HttpEntity<BusquedaClienteCriteria> requestEntity = new HttpEntity<>(criteriaCliente);
        PaginaRespuestaRest<Cliente> response = RestClient.getRestTemplate()
                .exchange("/clientes/busqueda/criteria", HttpMethod.POST, requestEntity,
                        new ParameterizedTypeReference<PaginaRespuestaRest<Cliente>>() {
                })
                .getBody();
        return !response.getContent().isEmpty();
    }

    private void lanzarReportePedido(long idPedido) {
        if (Desktop.isDesktopSupported()) {
            try {
                byte[] reporte = RestClient.getRestTemplate().getForObject("/pedidos/" + idPedido + "/reporte", byte[].class);
                File f = new File(System.getProperty("user.home") + "/Pedido.pdf");
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
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)
                || rolesDeUsuarioActivo.contains(Rol.VENDEDOR)) {
            btnEliminarPedido.setEnabled(true);
            btnFacturar.setEnabled(true);
            chk_Usuario.setEnabled(true);
        } else {
            btnEliminarPedido.setEnabled(false);
            btnFacturar.setEnabled(false);
            chk_Usuario.setEnabled(false);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFiltros = new javax.swing.JPanel();
        chk_NumeroPedido = new javax.swing.JCheckBox();
        chk_Fecha = new javax.swing.JCheckBox();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        chk_Cliente = new javax.swing.JCheckBox();
        chk_Usuario = new javax.swing.JCheckBox();
        btn_Buscar = new javax.swing.JButton();
        txt_NumeroPedido = new javax.swing.JFormattedTextField();
        lbl_cantResultados = new javax.swing.JLabel();
        chkEstado = new javax.swing.JCheckBox();
        cmbEstado = new javax.swing.JComboBox<>();
        txtCliente = new javax.swing.JTextField();
        btnBuscarCliente = new javax.swing.JButton();
        txtUsuario = new javax.swing.JTextField();
        btnBuscarUsuarios = new javax.swing.JButton();
        chk_Viajante = new javax.swing.JCheckBox();
        txtViajante = new javax.swing.JTextField();
        btnBuscarViajantes = new javax.swing.JButton();
        chk_Producto = new javax.swing.JCheckBox();
        txtProducto = new javax.swing.JTextField();
        btnBuscarProductos = new javax.swing.JButton();
        panelResultados = new javax.swing.JPanel();
        sp_Pedidos = new javax.swing.JScrollPane();
        tbl_Pedidos = new javax.swing.JTable();
        btnNuevoPedido = new javax.swing.JButton();
        btnVerFacturas = new javax.swing.JButton();
        btnFacturar = new javax.swing.JButton();
        btnImprimirPedido = new javax.swing.JButton();
        btnModificarPedido = new javax.swing.JButton();
        btnEliminarPedido = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoFacturar_16x16.png"))); // NOI18N
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

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_NumeroPedido.setText("Nº Pedido:");
        chk_NumeroPedido.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumeroPedidoItemStateChanged(evt);
            }
        });

        chk_Fecha.setText("Fecha Pedido:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

        dc_FechaDesde.setDateFormatString("dd/MM/yyyy");
        dc_FechaDesde.setEnabled(false);

        dc_FechaHasta.setDateFormatString("dd/MM/yyyy");
        dc_FechaHasta.setEnabled(false);

        chk_Cliente.setText("Cliente:");
        chk_Cliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ClienteItemStateChanged(evt);
            }
        });

        chk_Usuario.setText("Usuario:");
        chk_Usuario.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UsuarioItemStateChanged(evt);
            }
        });

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        txt_NumeroPedido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("0"))));
        txt_NumeroPedido.setText("0");
        txt_NumeroPedido.setEnabled(false);
        txt_NumeroPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_NumeroPedidoActionPerformed(evt);
            }
        });
        txt_NumeroPedido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NumeroPedidoKeyTyped(evt);
            }
        });

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        chkEstado.setText("Estado:");
        chkEstado.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkEstadoItemStateChanged(evt);
            }
        });

        cmbEstado.setEnabled(false);

        txtCliente.setEditable(false);
        txtCliente.setBackground(new java.awt.Color(69, 73, 74));
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

        chk_Viajante.setText("Viajante:");
        chk_Viajante.setToolTipText("");
        chk_Viajante.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ViajanteItemStateChanged(evt);
            }
        });

        txtViajante.setEditable(false);
        txtViajante.setEnabled(false);
        txtViajante.setOpaque(false);

        btnBuscarViajantes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarViajantes.setEnabled(false);
        btnBuscarViajantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarViajantesActionPerformed(evt);
            }
        });

        chk_Producto.setText("Producto:");
        chk_Producto.setToolTipText("");
        chk_Producto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ProductoItemStateChanged(evt);
            }
        });

        txtProducto.setEditable(false);
        txtProducto.setEnabled(false);
        txtProducto.setOpaque(false);

        btnBuscarProductos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarProductos.setEnabled(false);
        btnBuscarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProductosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFiltrosLayout.createSequentialGroup()
                                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chk_Usuario)
                                    .addComponent(chk_Cliente)
                                    .addComponent(chk_Viajante))
                                .addGap(0, 0, 0)
                                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtViajante)
                                    .addComponent(txtCliente)
                                    .addComponent(txtUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)))
                            .addGroup(panelFiltrosLayout.createSequentialGroup()
                                .addComponent(chk_Producto)
                                .addGap(0, 0, 0)
                                .addComponent(txtProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, 0)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(btnBuscarCliente)
                                .addComponent(btnBuscarUsuarios)
                                .addComponent(btnBuscarViajantes))
                            .addComponent(btnBuscarProductos))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelFiltrosLayout.createSequentialGroup()
                                .addComponent(chk_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_NumeroPedido))
                            .addGroup(panelFiltrosLayout.createSequentialGroup()
                                .addComponent(chkEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbEstado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(panelFiltrosLayout.createSequentialGroup()
                                .addComponent(chk_Fecha)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chk_Cliente, chk_Producto, chk_Usuario, chk_Viajante});

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chkEstado, chk_Fecha, chk_NumeroPedido});

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {dc_FechaDesde, dc_FechaHasta});

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCliente, txtProducto, txtUsuario, txtViajante});

        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Cliente)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Fecha)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Usuario)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarUsuarios)
                    .addComponent(chkEstado)
                    .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Producto)
                    .addComponent(txtProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarProductos)
                    .addComponent(chk_NumeroPedido)
                    .addComponent(txt_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Viajante)
                    .addComponent(txtViajante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarViajantes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Buscar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarCliente, btnBuscarUsuarios, txtCliente, txtProducto, txtUsuario, txtViajante});

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chk_Cliente, chk_Producto, chk_Usuario, chk_Viajante});

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {dc_FechaDesde, dc_FechaHasta});

        panelResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Pedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Pedidos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Pedidos.setViewportView(tbl_Pedidos);

        btnNuevoPedido.setForeground(java.awt.Color.blue);
        btnNuevoPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoNuevo_16x16.png"))); // NOI18N
        btnNuevoPedido.setText("Nuevo");
        btnNuevoPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoPedidoActionPerformed(evt);
            }
        });

        btnVerFacturas.setForeground(java.awt.Color.blue);
        btnVerFacturas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoFacturas_16x16.png"))); // NOI18N
        btnVerFacturas.setText("Ver Facturas");
        btnVerFacturas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerFacturasActionPerformed(evt);
            }
        });

        btnFacturar.setForeground(java.awt.Color.blue);
        btnFacturar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoFacturar_16x16.png"))); // NOI18N
        btnFacturar.setText("Facturar");
        btnFacturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFacturarActionPerformed(evt);
            }
        });

        btnImprimirPedido.setForeground(new java.awt.Color(0, 0, 255));
        btnImprimirPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/target_16x16.png"))); // NOI18N
        btnImprimirPedido.setText("Ver Detalle");
        btnImprimirPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirPedidoActionPerformed(evt);
            }
        });

        btnModificarPedido.setForeground(new java.awt.Color(0, 0, 225));
        btnModificarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoEditar_16x16.png"))); // NOI18N
        btnModificarPedido.setText("Modificar");
        btnModificarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarPedidoActionPerformed(evt);
            }
        });

        btnEliminarPedido.setForeground(new java.awt.Color(0, 0, 255));
        btnEliminarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Cancel_16x16.png"))); // NOI18N
        btnEliminarPedido.setText("Eliminar");
        btnEliminarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPedidoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Pedidos)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btnNuevoPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnModificarPedido)
                .addGap(0, 0, 0)
                .addComponent(btnEliminarPedido)
                .addGap(0, 0, 0)
                .addComponent(btnVerFacturas)
                .addGap(0, 0, 0)
                .addComponent(btnFacturar)
                .addGap(0, 0, 0)
                .addComponent(btnImprimirPedido)
                .addGap(0, 83, Short.MAX_VALUE))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnEliminarPedido, btnFacturar, btnImprimirPedido, btnModificarPedido, btnNuevoPedido, btnVerFacturas});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Pedidos, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoPedido)
                    .addComponent(btnVerFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFacturar)
                    .addComponent(btnModificarPedido)
                    .addComponent(btnImprimirPedido)
                    .addComponent(btnEliminarPedido)))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnEliminarPedido, btnFacturar, btnImprimirPedido, btnModificarPedido, btnNuevoPedido, btnVerFacturas});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.resetScroll();
        this.limpiarJTables();
        this.buscar();
    }//GEN-LAST:event_btn_BuscarActionPerformed

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

    private void chk_NumeroPedidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_NumeroPedidoItemStateChanged
        if (chk_NumeroPedido.isSelected() == true) {
            txt_NumeroPedido.setEnabled(true);
            txt_NumeroPedido.requestFocus();
        } else {
            txt_NumeroPedido.removeAll();
            txt_NumeroPedido.setEnabled(false);
        }
    }//GEN-LAST:event_chk_NumeroPedidoItemStateChanged

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

    private void btnNuevoPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoPedidoActionPerformed
        try {
            if (this.existeClienteDisponible()) {
                JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), DetallePedidoGUI.class);
                if (gui == null) {
                    Pedido pedido = new Pedido();
                    pedido.setObservaciones("Los precios se encuentran sujetos a modificaciones.");
                    DetallePedidoGUI nuevoPedidoGUI = new DetallePedidoGUI(pedido, false);
                    nuevoPedidoGUI.setLocation(getDesktopPane().getWidth() / 2 - nuevoPedidoGUI.getWidth() / 2,
                            getDesktopPane().getHeight() / 2 - nuevoPedidoGUI.getHeight() / 2);
                    getDesktopPane().add(nuevoPedidoGUI);
                    nuevoPedidoGUI.setMaximizable(true);
                    nuevoPedidoGUI.setClosable(true);
                    nuevoPedidoGUI.setVisible(true);
                } else {
                    //selecciona y trae al frente el internalframe
                    try {
                        gui.setSelected(true);
                    } catch (PropertyVetoException ex) {
                        String msjError = "No se pudo seleccionar la ventana requerida.";
                        LOGGER.error(msjError + " - " + ex.getMessage());
                        JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_cliente"),
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
    }//GEN-LAST:event_btnNuevoPedidoActionPerformed

    private void btnFacturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFacturarActionPerformed
        try {
            if (tbl_Pedidos.getSelectedRow() != -1) {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Pedidos);
                Pedido pedido = RestClient.getRestTemplate()
                        .getForObject("/pedidos/" + pedidosTotal.get(indexFilaSeleccionada).getId_Pedido(), Pedido.class);
                if (pedido.getEstado() == EstadoPedido.CERRADO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_pedido_facturado"), "Error", JOptionPane.ERROR_MESSAGE);
                } else if (this.existeClienteDisponible()) {
                    NuevaFacturaVentaGUI puntoDeVentaGUI = new NuevaFacturaVentaGUI();
                    puntoDeVentaGUI.setPedido(pedido);
                    puntoDeVentaGUI.setLocation(getDesktopPane().getWidth() / 2 - puntoDeVentaGUI.getWidth() / 2,
                            getDesktopPane().getHeight() / 2 - puntoDeVentaGUI.getHeight() / 2);
                    getDesktopPane().add(puntoDeVentaGUI);
                    puntoDeVentaGUI.setMaximizable(true);
                    puntoDeVentaGUI.setClosable(true);
                    puntoDeVentaGUI.setVisible(true);
                } else {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_cliente"),
                            "Error", JOptionPane.ERROR_MESSAGE);
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
    }//GEN-LAST:event_btnFacturarActionPerformed

    private void btnVerFacturasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerFacturasActionPerformed
        this.verFacturasVenta();
    }//GEN-LAST:event_btnVerFacturasActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.setTitle("Administrar Pedidos");
        this.setSize(sizeInternalFrame);
        this.limpiarJTables();
        dc_FechaDesde.setDate(new Date());
        dc_FechaHasta.setDate(new Date());
        try {
            this.setMaximum(true);
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
        this.cambiarEstadoDeComponentesSegunRolUsuario();
    }//GEN-LAST:event_formInternalFrameOpened
        
    private void btnImprimirPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirPedidoActionPerformed
        if (tbl_Pedidos.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Pedidos);
            this.lanzarReportePedido(pedidosTotal.get(indexFilaSeleccionada).getId_Pedido());
        }
    }//GEN-LAST:event_btnImprimirPedidoActionPerformed

    private void btnModificarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarPedidoActionPerformed
        try {
            if (tbl_Pedidos.getSelectedRow() != -1) {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Pedidos);
                Pedido pedido = RestClient.getRestTemplate()
                        .getForObject("/pedidos/" + pedidosTotal.get(indexFilaSeleccionada).getId_Pedido(), Pedido.class);
                if (pedido.getEstado() == EstadoPedido.CERRADO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_pedido_facturado"), "Error", JOptionPane.ERROR_MESSAGE);
                } else if (pedido.getEstado() == EstadoPedido.ACTIVO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_pedido_procesado"), "Error", JOptionPane.ERROR_MESSAGE);
                } else if (this.existeClienteDisponible()) {
                    DetallePedidoGUI nuevoPedidoGUI = new DetallePedidoGUI(pedido, true);
                    nuevoPedidoGUI.setLocation(getDesktopPane().getWidth() / 2 - nuevoPedidoGUI.getWidth() / 2,
                            getDesktopPane().getHeight() / 2 - nuevoPedidoGUI.getHeight() / 2);
                    getDesktopPane().add(nuevoPedidoGUI);
                    nuevoPedidoGUI.setMaximizable(true);
                    nuevoPedidoGUI.setClosable(true);
                    nuevoPedidoGUI.setVisible(true);
                } else {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_cliente"),
                            "Error", JOptionPane.ERROR_MESSAGE);
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
    }//GEN-LAST:event_btnModificarPedidoActionPerformed

    private void btnEliminarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPedidoActionPerformed
        try {
            if (tbl_Pedidos.getSelectedRow() != -1) {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Pedidos);
                Pedido pedido = RestClient.getRestTemplate().getForObject("/pedidos/" + pedidosTotal.get(indexFilaSeleccionada).getId_Pedido(), Pedido.class);
                if (pedido.getEstado() == EstadoPedido.CERRADO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_pedido_facturado"), "Error", JOptionPane.ERROR_MESSAGE);
                } else if (pedido.getEstado() == EstadoPedido.ACTIVO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_pedido_procesado"), "Error", JOptionPane.ERROR_MESSAGE);
                } else if (this.existeClienteDisponible()) {
                    int respuesta = JOptionPane.showConfirmDialog(this,
                            "¿Esta seguro que desea eliminar el pedido seleccionado?",
                            "Eliminar", JOptionPane.YES_NO_OPTION);
                    if (respuesta == JOptionPane.YES_OPTION) {
                        RestClient.getRestTemplate().delete("/pedidos/" + pedido.getId_Pedido());
                        this.resetScroll();
                        this.limpiarJTables();
                        this.buscar();
                    }
                } else {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_cliente"),
                            "Error", JOptionPane.ERROR_MESSAGE);
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
    }//GEN-LAST:event_btnEliminarPedidoActionPerformed
      
    private void txt_NumeroPedidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NumeroPedidoKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_NumeroPedidoKeyTyped

    private void chkEstadoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkEstadoItemStateChanged
        if (chkEstado.isSelected() == true) {
            cmbEstado.setEnabled(true);
            cmbEstado.addItem(EstadoPedido.ABIERTO);
            cmbEstado.addItem(EstadoPedido.ACTIVO);
            cmbEstado.addItem(EstadoPedido.CERRADO);
            cmbEstado.requestFocus();
        } else {
            cmbEstado.removeAllItems();
            cmbEstado.setEnabled(false);
        }
    }//GEN-LAST:event_chkEstadoItemStateChanged

    private void txt_NumeroPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_NumeroPedidoActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_NumeroPedidoActionPerformed

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
        Rol[] rolesParaFiltrar = new Rol[]{Rol.ADMINISTRADOR, Rol.ENCARGADO, Rol.VENDEDOR, Rol.VIAJANTE, Rol.COMPRADOR};
        BuscarUsuariosGUI buscarUsuariosGUI = new BuscarUsuariosGUI(rolesParaFiltrar, "Buscar Usuario");
        buscarUsuariosGUI.setModal(true);
        buscarUsuariosGUI.setLocationRelativeTo(this);
        buscarUsuariosGUI.setVisible(true);
        if (buscarUsuariosGUI.getUsuarioSeleccionado() != null) {
            usuarioSeleccionado = buscarUsuariosGUI.getUsuarioSeleccionado();
            txtUsuario.setText(usuarioSeleccionado.toString());
        }
    }//GEN-LAST:event_btnBuscarUsuariosActionPerformed

    private void chk_ViajanteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ViajanteItemStateChanged
        if (chk_Viajante.isSelected() == true) {
            btnBuscarViajantes.setEnabled(true);
            btnBuscarViajantes.requestFocus();
            txtViajante.setEnabled(true);
        } else {
            btnBuscarViajantes.setEnabled(false);
            txtViajante.setEnabled(false);
        }
    }//GEN-LAST:event_chk_ViajanteItemStateChanged

    private void btnBuscarViajantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarViajantesActionPerformed
        Rol[] rolesParaFiltrar = new Rol[]{Rol.VIAJANTE};
        BuscarUsuariosGUI buscarUsuariosGUI = new BuscarUsuariosGUI(rolesParaFiltrar, "Buscar Viajante");
        buscarUsuariosGUI.setModal(true);
        buscarUsuariosGUI.setLocationRelativeTo(this);
        buscarUsuariosGUI.setVisible(true);
        if (buscarUsuariosGUI.getUsuarioSeleccionado() != null) {
            viajanteSeleccionado = buscarUsuariosGUI.getUsuarioSeleccionado();
            txtViajante.setText(viajanteSeleccionado.toString());
        }
    }//GEN-LAST:event_btnBuscarViajantesActionPerformed

    private void chk_ProductoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ProductoItemStateChanged
        if (chk_Producto.isSelected() == true) {
            btnBuscarProductos.setEnabled(true);
            btnBuscarProductos.requestFocus();
            txtProducto.setEnabled(true);
        } else {
            btnBuscarProductos.setEnabled(false);
            txtProducto.setEnabled(false);
        }
    }//GEN-LAST:event_chk_ProductoItemStateChanged

    private void btnBuscarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProductosActionPerformed
        BuscarProductosGUI buscarProductosGUI = new BuscarProductosGUI();
        buscarProductosGUI.setModal(true);
        buscarProductosGUI.setLocationRelativeTo(this);
        buscarProductosGUI.setVisible(true);
        productoSeleccionado = buscarProductosGUI.getProductoSeleccionado();
        if (productoSeleccionado != null) {
            txtProducto.setText(productoSeleccionado.getDescripcion());
        }
    }//GEN-LAST:event_btnBuscarProductosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnBuscarProductos;
    private javax.swing.JButton btnBuscarUsuarios;
    private javax.swing.JButton btnBuscarViajantes;
    private javax.swing.JButton btnEliminarPedido;
    private javax.swing.JButton btnFacturar;
    private javax.swing.JButton btnImprimirPedido;
    private javax.swing.JButton btnModificarPedido;
    private javax.swing.JButton btnNuevoPedido;
    private javax.swing.JButton btnVerFacturas;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JCheckBox chkEstado;
    private javax.swing.JCheckBox chk_Cliente;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_NumeroPedido;
    private javax.swing.JCheckBox chk_Producto;
    private javax.swing.JCheckBox chk_Usuario;
    private javax.swing.JCheckBox chk_Viajante;
    private javax.swing.JComboBox<EstadoPedido> cmbEstado;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane sp_Pedidos;
    private javax.swing.JTable tbl_Pedidos;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtProducto;
    private javax.swing.JTextField txtUsuario;
    private javax.swing.JTextField txtViajante;
    private javax.swing.JFormattedTextField txt_NumeroPedido;
    // End of variables declaration//GEN-END:variables
}
