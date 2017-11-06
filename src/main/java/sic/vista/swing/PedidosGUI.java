package sic.vista.swing;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import sic.util.ColoresEstadosRenderer;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
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
import sic.modelo.Pedido;
import sic.modelo.RenglonPedido;
import sic.modelo.Usuario;
import sic.modelo.EstadoPedido;
import sic.modelo.PaginaRespuestaRest;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class PedidosGUI extends JInternalFrame {

    private List<Pedido> pedidosTotal = new ArrayList<>();    
    private List<Pedido> pedidosParcial = new ArrayList<>();
    private ModeloTabla modeloTablaPedidos;
    private ModeloTabla modeloTablaRenglones;    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame =  new Dimension(880, 600);
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;
    private static final int TAMANIO_PAGINA = 50;

    public PedidosGUI() {
        this.initComponents();
        sp_Pedidos.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 50;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (pedidosTotal.size() >= TAMANIO_PAGINA) {
                    NUMERO_PAGINA += 1;
                    buscar();
                }
            }
        });
    }

    private void buscar() {
        this.cambiarEstadoEnabledComponentes(false);
        String criteria = "/pedidos/busqueda/criteria?idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa();
        if (chk_Fecha.isSelected()) {
            criteria += "&desde=" + dc_FechaDesde.getDate().getTime();
            criteria += "&hasta=" + dc_FechaHasta.getDate().getTime();
        }
        if (chk_NumeroPedido.isSelected()) {
            criteria += "&nroPedido=" + Long.valueOf(txt_NumeroPedido.getText());
        }
        if (chk_Cliente.isSelected()) {
            criteria += "&idCliente=" + ((Cliente) cmb_Cliente.getSelectedItem()).getId_Cliente();
        }
        if (chk_Vendedor.isSelected()) {
            criteria += "&idUsuario=" + ((Usuario) cmb_Vendedor.getSelectedItem()).getId_Usuario();
        }
        criteria += "&pagina=" + NUMERO_PAGINA + "&tamanio=" + TAMANIO_PAGINA;
        try {
            PaginaRespuestaRest<Pedido> response = RestClient.getRestTemplate()
                    .exchange(criteria, HttpMethod.GET, null,
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
            cmb_Cliente.setEnabled(true);
        } else {
            cmb_Cliente.setEnabled(false);
        }        
        chk_Vendedor.setEnabled(status);
        if (status == true && chk_Vendedor.isSelected() == true) {
            cmb_Vendedor.setEnabled(true);
        } else {
            cmb_Vendedor.setEnabled(false);
        }        
        btn_Buscar.setEnabled(status);        
        btnNuevoPedido.setEnabled(status);
        btnVerFacturas.setEnabled(status);
        btnFacturar.setEnabled(status);
        btnModificarPedido.setEnabled(status);
        btnEliminarPedido.setEnabled(status);
        btnImprimirPedido.setEnabled(status);
        tbl_RenglonesPedido.setEnabled(status);
        tbl_Pedidos.setEnabled(status);
        sp_RenglonesPedido.setEnabled(status);
        sp_Pedidos.setEnabled(status);
        tbl_Pedidos.requestFocus();
    }
    
    private void cargarResultadosAlTable() {
        pedidosParcial.stream().map(p -> {
            Object[] fila = new Object[7];
            fila[0] = p.getEstado();
            fila[1] = p.getFecha();
            fila[2] = p.getNroPedido();
            fila[3] = p.getCliente().getRazonSocial();
            fila[4] = p.getUsuario().getNombre();
            fila[5] = p.getTotalEstimado();
            fila[6] = p.getTotalActual();
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
        modeloTablaRenglones = new ModeloTabla();
        tbl_RenglonesPedido.setModel(modeloTablaRenglones);
        this.setColumnasPedido();
        this.setColumnasRenglonesPedido();
    }

    private void limpiarTablaRenglones() {
        modeloTablaRenglones = new ModeloTabla();
        tbl_RenglonesPedido.setModel(modeloTablaRenglones);
        this.setColumnasRenglonesPedido();
    }

    private void setColumnasRenglonesPedido() {
        //sorting
        tbl_RenglonesPedido.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezadoRenglones = new String[6];
        encabezadoRenglones[0] = "Codigo";
        encabezadoRenglones[1] = "Descripcion";
        encabezadoRenglones[2] = "Cantidad";
        encabezadoRenglones[3] = "Precio Lista";
        encabezadoRenglones[4] = "% Descuento";
        encabezadoRenglones[5] = "SubTotal";
        modeloTablaRenglones.setColumnIdentifiers(encabezadoRenglones);
        tbl_RenglonesPedido.setModel(modeloTablaRenglones);

        //tipo de dato columnas
        Class[] tiposRenglones = new Class[modeloTablaRenglones.getColumnCount()];
        tiposRenglones[0] = String.class;
        tiposRenglones[1] = String.class;
        tiposRenglones[2] = Integer.class;
        tiposRenglones[3] = Double.class;
        tiposRenglones[4] = Double.class;
        tiposRenglones[5] = Double.class;
        modeloTablaRenglones.setClaseColumnas(tiposRenglones);
        tbl_RenglonesPedido.getTableHeader().setReorderingAllowed(false);
        tbl_RenglonesPedido.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_RenglonesPedido.setDefaultRenderer(Double.class, new RenderTabla());

        //Tamanios de columnas
        tbl_RenglonesPedido.getColumnModel().getColumn(0).setPreferredWidth(25);
        tbl_RenglonesPedido.getColumnModel().getColumn(1).setPreferredWidth(250);
        tbl_RenglonesPedido.getColumnModel().getColumn(2).setPreferredWidth(25);
        tbl_RenglonesPedido.getColumnModel().getColumn(3).setPreferredWidth(25);
        tbl_RenglonesPedido.getColumnModel().getColumn(4).setPreferredWidth(25);
        tbl_RenglonesPedido.getColumnModel().getColumn(5).setPreferredWidth(25);
    }

    private void setColumnasPedido() {
        //sorting
        //tbl_Pedidos.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[7];
        encabezados[0] = "Estado";
        encabezados[1] = "Fecha Pedido";
        encabezados[2] = "Nº Pedido";
        encabezados[3] = "Cliente";
        encabezados[4] = "Usuario";
        encabezados[5] = "Total Estimado";
        encabezados[6] = "Total Actual";
        modeloTablaPedidos.setColumnIdentifiers(encabezados);
        tbl_Pedidos.setModel(modeloTablaPedidos);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaPedidos.getColumnCount()];
        tipos[0] = EstadoPedido.class;
        tipos[1] = Date.class;
        tipos[2] = Long.class;
        tipos[3] = String.class;
        tipos[4] = String.class;
        tipos[5] = Double.class;
        tipos[6] = Double.class;
        modeloTablaPedidos.setClaseColumnas(tipos);
        tbl_Pedidos.getTableHeader().setReorderingAllowed(false);
        tbl_Pedidos.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Pedidos.setDefaultRenderer(Double.class, new RenderTabla());

        //Tamanios de columnas
        tbl_Pedidos.getColumnModel().getColumn(0).setPreferredWidth(25);
        tbl_Pedidos.getColumnModel().getColumn(1).setPreferredWidth(25);
        tbl_Pedidos.getColumnModel().getColumn(2).setPreferredWidth(25);
        tbl_Pedidos.getColumnModel().getColumn(3).setPreferredWidth(150);
        tbl_Pedidos.getColumnModel().getColumn(4).setPreferredWidth(100);
        tbl_Pedidos.getColumnModel().getColumn(5).setPreferredWidth(25);
        tbl_Pedidos.getColumnModel().getColumn(6).setPreferredWidth(25);
    }

    private void cargarClientes() {
        try {
            cmb_Cliente.removeAllItems();
            List<Cliente> clientes = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/clientes/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(), Cliente[].class)));
            clientes.stream().forEach(c -> {
                cmb_Cliente.addItem(c);
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

    private void cargarUsuarios() {
        try {
            cmb_Vendedor.removeAllItems();
            List<Usuario> usuarios = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/usuarios", Usuario[].class)));
            usuarios.stream().forEach(u -> {
                cmb_Vendedor.addItem(u);
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

    private boolean existeClienteDisponible() {
        return !Arrays.asList(RestClient.getRestTemplate().getForObject("/usuarios", Usuario[].class)).isEmpty();
    }

    private void cargarRenglonesDelPedidoSeleccionadoEnTabla(KeyEvent evt) {
        int row = Utilidades.getSelectedRowModelIndice(tbl_Pedidos);
        if (evt != null) {
            if ((evt.getKeyCode() == KeyEvent.VK_UP) && row > 0) {
                row--;
            }
            if ((evt.getKeyCode() == KeyEvent.VK_DOWN) && (row + 1) < tbl_Pedidos.getRowCount()) {
                row++;
            }
        }
        this.limpiarTablaRenglones();
        this.setColumnasRenglonesPedido();
        try {
            List<RenglonPedido> renglones = Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/pedidos/" + pedidosTotal.get(row).getId_Pedido() + "/renglones", RenglonPedido[].class));
            renglones.stream().map(r -> {
                Object[] fila = new Object[6];
                fila[0] = r.getProducto().getCodigo();
                fila[1] = r.getProducto().getDescripcion();
                fila[2] = r.getCantidad();
                fila[3] = r.getProducto().getPrecioLista();
                fila[4] = r.getDescuento_porcentaje();
                fila[5] = r.getSubTotal();
                return fila;
            }).forEach(fila -> {
                modeloTablaRenglones.addRow(fila);
            });
            tbl_RenglonesPedido.setModel(modeloTablaRenglones);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_Filtros = new javax.swing.JPanel();
        chk_NumeroPedido = new javax.swing.JCheckBox();
        chk_Fecha = new javax.swing.JCheckBox();
        lbl_Desde = new javax.swing.JLabel();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        lbl_Hasta = new javax.swing.JLabel();
        chk_Cliente = new javax.swing.JCheckBox();
        cmb_Cliente = new javax.swing.JComboBox();
        cmb_Vendedor = new javax.swing.JComboBox();
        chk_Vendedor = new javax.swing.JCheckBox();
        btn_Buscar = new javax.swing.JButton();
        txt_NumeroPedido = new javax.swing.JFormattedTextField();
        lbl_cantResultados = new javax.swing.JLabel();
        panel_resultados = new javax.swing.JPanel();
        sp_RenglonesPedido = new javax.swing.JScrollPane();
        tbl_RenglonesPedido = new javax.swing.JTable();
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

        panel_Filtros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

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

        lbl_Desde.setText("Desde:");

        dc_FechaDesde.setDateFormatString("dd/MM/yyyy");
        dc_FechaDesde.setEnabled(false);

        dc_FechaHasta.setDateFormatString("dd/MM/yyyy");
        dc_FechaHasta.setEnabled(false);

        lbl_Hasta.setText("Hasta:");

        chk_Cliente.setText("Cliente:");
        chk_Cliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ClienteItemStateChanged(evt);
            }
        });

        cmb_Cliente.setToolTipText("");
        cmb_Cliente.setEnabled(false);

        cmb_Vendedor.setEnabled(false);

        chk_Vendedor.setText("Usuario:");
        chk_Vendedor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_VendedorItemStateChanged(evt);
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
        txt_NumeroPedido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NumeroPedidoKeyTyped(evt);
            }
        });

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout panel_FiltrosLayout = new javax.swing.GroupLayout(panel_Filtros);
        panel_Filtros.setLayout(panel_FiltrosLayout);
        panel_FiltrosLayout.setHorizontalGroup(
            panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_FiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_FiltrosLayout.createSequentialGroup()
                        .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chk_Vendedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_Cliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_NumeroPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_Fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cmb_Cliente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_NumeroPedido, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel_FiltrosLayout.createSequentialGroup()
                                .addComponent(lbl_Desde)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dc_FechaDesde, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbl_Hasta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dc_FechaHasta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(cmb_Vendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel_FiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panel_FiltrosLayout.setVerticalGroup(
            panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_FiltrosLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Fecha)
                    .addComponent(lbl_Desde)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Hasta)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_NumeroPedido)
                    .addComponent(txt_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Cliente)
                    .addComponent(cmb_Cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Vendedor)
                    .addComponent(cmb_Vendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Buscar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_FiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmb_Cliente, cmb_Vendedor, txt_NumeroPedido});

        panel_resultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_RenglonesPedido.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        sp_RenglonesPedido.setViewportView(tbl_RenglonesPedido);

        tbl_Pedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Pedidos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Pedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_PedidosMouseClicked(evt);
            }
        });
        tbl_Pedidos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_PedidosKeyPressed(evt);
            }
        });
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
        btnImprimirPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Printer_16x16.png"))); // NOI18N
        btnImprimirPedido.setText("Imprimir");
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

        javax.swing.GroupLayout panel_resultadosLayout = new javax.swing.GroupLayout(panel_resultados);
        panel_resultados.setLayout(panel_resultadosLayout);
        panel_resultadosLayout.setHorizontalGroup(
            panel_resultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Pedidos, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
            .addComponent(sp_RenglonesPedido)
            .addGroup(panel_resultadosLayout.createSequentialGroup()
                .addComponent(btnNuevoPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnVerFacturas)
                .addGap(0, 0, 0)
                .addComponent(btnFacturar)
                .addGap(0, 0, 0)
                .addComponent(btnModificarPedido)
                .addGap(0, 0, 0)
                .addComponent(btnEliminarPedido)
                .addGap(0, 0, 0)
                .addComponent(btnImprimirPedido)
                .addGap(0, 236, Short.MAX_VALUE))
        );
        panel_resultadosLayout.setVerticalGroup(
            panel_resultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_resultadosLayout.createSequentialGroup()
                .addComponent(sp_Pedidos, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_RenglonesPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_resultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoPedido)
                    .addComponent(btnVerFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFacturar)
                    .addComponent(btnModificarPedido)
                    .addComponent(btnImprimirPedido)
                    .addComponent(btnEliminarPedido)))
        );

        panel_resultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFacturar, btnImprimirPedido, btnModificarPedido, btnNuevoPedido, btnVerFacturas});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_resultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_Filtros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panel_Filtros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_resultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            cmb_Cliente.setEnabled(true);
            this.cargarClientes();
            cmb_Cliente.requestFocus();
        } else {
            cmb_Cliente.removeAllItems();
            cmb_Cliente.setEnabled(false);
        }
    }//GEN-LAST:event_chk_ClienteItemStateChanged

    private void chk_VendedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_VendedorItemStateChanged
        if (chk_Vendedor.isSelected() == true) {
            cmb_Vendedor.setEnabled(true);
            this.cargarUsuarios();
            cmb_Vendedor.requestFocus();
        } else {
            cmb_Vendedor.removeAllItems();
            cmb_Vendedor.setEnabled(false);
        }
    }//GEN-LAST:event_chk_VendedorItemStateChanged

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
                PuntoDeVentaGUI gui_puntoDeVenta = new PuntoDeVentaGUI();
                Pedido pedido = new Pedido();
                pedido.setObservaciones("Los precios se encuentran sujetos a modificaciones.");
                gui_puntoDeVenta.setPedido(pedido);
                gui_puntoDeVenta.setModal(true);
                gui_puntoDeVenta.setLocationRelativeTo(this);
                gui_puntoDeVenta.setVisible(true);
                this.resetScroll();
                this.buscar();
                this.limpiarJTables();
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
                Pedido pedido = RestClient.getRestTemplate().getForObject("/pedidos/" + pedidosTotal.get(indexFilaSeleccionada).getId_Pedido(), Pedido.class);
                if (pedido.getEstado() == EstadoPedido.CERRADO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_pedido_facturado"), "Error", JOptionPane.ERROR_MESSAGE);
                } else if (this.existeClienteDisponible()) {
                    PuntoDeVentaGUI gui_puntoDeVenta = new PuntoDeVentaGUI();
                    gui_puntoDeVenta.setPedido(pedido);
                    gui_puntoDeVenta.setModal(true);
                    gui_puntoDeVenta.setLocationRelativeTo(this);
                    gui_puntoDeVenta.setVisible(true);
                    this.resetScroll();
                    this.buscar();
                    this.limpiarJTables();
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

    private void tbl_PedidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_PedidosMouseClicked
        this.cargarRenglonesDelPedidoSeleccionadoEnTabla(null);
    }//GEN-LAST:event_tbl_PedidosMouseClicked

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
    }//GEN-LAST:event_formInternalFrameOpened

    private void tbl_PedidosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_PedidosKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            this.cargarRenglonesDelPedidoSeleccionadoEnTabla(evt);
        }
    }//GEN-LAST:event_tbl_PedidosKeyPressed

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
                Pedido pedido = RestClient.getRestTemplate().getForObject("/pedidos/" + pedidosTotal.get(indexFilaSeleccionada).getId_Pedido(), Pedido.class);
                if (pedido.getEstado() == EstadoPedido.CERRADO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_pedido_facturado"), "Error", JOptionPane.ERROR_MESSAGE);
                } else if (pedido.getEstado() == EstadoPedido.ACTIVO) {
                    JOptionPane.showInternalMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_pedido_procesado"), "Error", JOptionPane.ERROR_MESSAGE);
                } else if (this.existeClienteDisponible()) {
                    PuntoDeVentaGUI gui_puntoDeVenta = new PuntoDeVentaGUI();
                    gui_puntoDeVenta.setPedido(pedido);
                    gui_puntoDeVenta.setModificarPedido(true);
                    gui_puntoDeVenta.setModal(true);
                    gui_puntoDeVenta.setLocationRelativeTo(this);
                    gui_puntoDeVenta.setVisible(true);
                    this.resetScroll();
                    this.buscar();
                    this.limpiarJTables();
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
                        this.buscar();
                        this.limpiarJTables();
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminarPedido;
    private javax.swing.JButton btnFacturar;
    private javax.swing.JButton btnImprimirPedido;
    private javax.swing.JButton btnModificarPedido;
    private javax.swing.JButton btnNuevoPedido;
    private javax.swing.JButton btnVerFacturas;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JCheckBox chk_Cliente;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_NumeroPedido;
    private javax.swing.JCheckBox chk_Vendedor;
    private javax.swing.JComboBox cmb_Cliente;
    private javax.swing.JComboBox cmb_Vendedor;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JLabel lbl_Desde;
    private javax.swing.JLabel lbl_Hasta;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panel_Filtros;
    private javax.swing.JPanel panel_resultados;
    private javax.swing.JScrollPane sp_Pedidos;
    private javax.swing.JScrollPane sp_RenglonesPedido;
    private javax.swing.JTable tbl_Pedidos;
    private javax.swing.JTable tbl_RenglonesPedido;
    private javax.swing.JFormattedTextField txt_NumeroPedido;
    // End of variables declaration//GEN-END:variables
}
