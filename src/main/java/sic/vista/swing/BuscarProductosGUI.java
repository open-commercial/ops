package sic.vista.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.CantidadEnSucursal;
import sic.modelo.SucursalActiva;
import sic.modelo.Movimiento;
import sic.modelo.NuevoRenglonFactura;
import sic.modelo.NuevoRenglonPedido;
import sic.modelo.Producto;
import sic.modelo.RenglonFactura;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.ProductoFaltante;
import sic.modelo.ProductosParaVerificarStock;
import sic.modelo.TipoDeComprobante;
import sic.modelo.criteria.BusquedaProductoCriteria;
import sic.util.ColoresCeldaRenderer;
import sic.util.DecimalesRenderer;
import sic.util.Utilidades;

public class BuscarProductosGUI extends JDialog {

    private TipoDeComprobante tipoDeComprobante;
    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private final List<Producto> productosTotal = new ArrayList<>();
    private List<Producto> productosParcial = new ArrayList<>();
    private List<RenglonFactura> renglonesFactura;
    private Map<Long, BigDecimal> cantidadesNuevas;
    private Map<Long, BigDecimal> cantidadesIniciales;
    private Long idPedido;
    private Producto productoSeleccionado;
    private NuevoRenglonFactura nuevoRenglonFactura;
    private NuevoRenglonPedido nuevoRenglonPedido;
    private boolean debeCargarRenglon;    
    private final boolean busquedaParaFiltros;
    private Movimiento movimiento;
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private int NUMERO_PAGINA = 0;    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeDialog = new Dimension(1100, 400);
    
    public BuscarProductosGUI(List<RenglonFactura> renglones, TipoDeComprobante tipoDeComprobante, Movimiento tipoDeMovimiento) { 
        this.initComponents();
        this.setIcon();
        this.renglonesFactura = renglones;
        this.movimiento = tipoDeMovimiento;
        this.tipoDeComprobante = tipoDeComprobante;
        this.busquedaParaFiltros = false;
        this.setColumnas();
        this.agregarListeners();
    }
    
    public BuscarProductosGUI(Map<Long, BigDecimal> cantidadesNuevas, Map<Long, BigDecimal> cantidadesIniciales, Long idPedido) { 
        this.initComponents();
        this.setIcon();
        this.cantidadesNuevas = cantidadesNuevas;
        this.cantidadesIniciales = cantidadesIniciales;
        this.idPedido = idPedido;
        this.movimiento = Movimiento.PEDIDO;
        this.tipoDeComprobante = TipoDeComprobante.PEDIDO;
        this.busquedaParaFiltros = false;
        this.setColumnas();
        this.agregarListeners();
    }
    
    public BuscarProductosGUI() {
        this.initComponents();
        this.setIcon();
        this.busquedaParaFiltros = true;
        this.setColumnas();
        this.agregarListeners();
    }

    public boolean debeCargarRenglon() {
        return debeCargarRenglon;
    }

    public NuevoRenglonFactura getRenglonFactura() {
        return nuevoRenglonFactura;
    }
    
    public NuevoRenglonPedido getRenglonPedido() {
        return nuevoRenglonPedido;
    }
    
    public Producto getProductoSeleccionado(){
        return this.productoSeleccionado;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(BuscarProductosGUI.class.getResource("/sic/icons/Product_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }
    
    private void prepararComponentes() {
        txtCantidad.setValue(1.00);
        txtBonificacion.setValue(0.00);
        if ((renglonesFactura == null || cantidadesNuevas == null) && movimiento == null && tipoDeComprobante == null) {
            lbl_Cantidad.setVisible(false);
            txtCantidad.setVisible(false);
            lblBonificacion.setVisible(false);
            txtBonificacion.setVisible(false);
        }
    }

    private void buscar() {
        try {
            if (txtCriteriaBusqueda.getText().equals("")) {
                this.resetScroll();
                this.limpiarJTable();
            } else {
                BusquedaProductoCriteria criteria = BusquedaProductoCriteria.builder().build();
                criteria.setDescripcion(txtCriteriaBusqueda.getText().trim());
                criteria.setCodigo(txtCriteriaBusqueda.getText().trim());
                criteria.setPagina(NUMERO_PAGINA);
                HttpEntity<BusquedaProductoCriteria> requestEntity = new HttpEntity<>(criteria);
                PaginaRespuestaRest<Producto> response = RestClient.getRestTemplate()
                        .exchange("/productos/busqueda/criteria/sucursales/" + SucursalActiva.getInstance().getSucursal().getIdSucursal(), HttpMethod.POST, requestEntity,
                                new ParameterizedTypeReference<PaginaRespuestaRest<Producto>>() {
                        })
                        .getBody();
                productosParcial = response.getContent();
                productosTotal.addAll(productosParcial);
                productoSeleccionado = null;
                if (renglonesFactura != null || cantidadesNuevas != null) {
                    this.restarCantidadesSegunProductosYaCargados();
                }
                this.cargarResultadosAlTable();
                if (response.isLast()) {
                    btnVerMas.setEnabled(false);
                } else {
                    btnVerMas.setEnabled(true);
                }
            }
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
    
    private void aceptarProducto() {
        boolean esValido = true;
        this.actualizarEstadoSeleccion();
        if (productoSeleccionado == null || (renglonesFactura == null && movimiento == null && tipoDeComprobante == null)) {
            debeCargarRenglon = false;
            this.dispose();
        } else {
            try {
                if (movimiento == Movimiento.PEDIDO) {
                    BigDecimal[] cantidades = {this.sumarCantidadesSegunProductosYaCargados()};
                    long[] idsProductos = {productoSeleccionado.getIdProducto()};
                    ProductosParaVerificarStock productosParaVerificarStock = ProductosParaVerificarStock.builder()
                            .cantidad(cantidades)
                            .idProducto(idsProductos)
                            .idSucursal(SucursalActiva.getInstance().getSucursal().getIdSucursal())
                            .build();
                    if (this.idPedido != null) {
                        productosParaVerificarStock.setIdPedido(this.idPedido);
                    }
                    HttpEntity<ProductosParaVerificarStock> requestEntity = new HttpEntity<>(productosParaVerificarStock);
                    boolean existeStockSuficiente;
                    existeStockSuficiente = RestClient.getRestTemplate()
                            .exchange("/productos/disponibilidad-stock", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<ProductoFaltante>>() {
                            })
                            .getBody().isEmpty();
                    if (!existeStockSuficiente) {
                        JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_producto_sin_stock_suficiente"), "Error", JOptionPane.ERROR_MESSAGE);
                        esValido = false;
                    }
                }
                if (esValido) {
                    switch (movimiento) {
                        case PEDIDO:
                            this.nuevoRenglonPedido = new NuevoRenglonPedido();
                            this.nuevoRenglonPedido.setCantidad(new BigDecimal(txtCantidad.getValue().toString()));
                            this.nuevoRenglonPedido.setIdProductoItem(productoSeleccionado.getIdProducto());
                            break;
                        case COMPRA:
                            nuevoRenglonFactura = NuevoRenglonFactura.builder()
                                    .idProducto(productoSeleccionado.getIdProducto())
                                    .cantidad(new BigDecimal(txtCantidad.getValue().toString()))
                                    .bonificacion(new BigDecimal(txtBonificacion.getValue().toString()))
                                    .build();
                            break;
                    }
                    debeCargarRenglon = true;
                    this.dispose();
                }
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private BigDecimal sumarCantidadesSegunProductosYaCargados() {
        BigDecimal cantidad = new BigDecimal(txtCantidad.getValue().toString());
        if (renglonesFactura != null && !renglonesFactura.isEmpty()) {
            for (RenglonFactura r : renglonesFactura) {
                if (r.getIdProductoItem() == productoSeleccionado.getIdProducto()) {
                    cantidad = cantidad.add(r.getCantidad());
                }
            }
        }
        if (cantidadesNuevas != null && !cantidadesNuevas.isEmpty()) {
            for (Long id : cantidadesNuevas.keySet()) {
                if (id == productoSeleccionado.getIdProducto()) {
                    cantidad = cantidad.add(cantidadesNuevas.get(id));
                }
            }
        }
        return cantidad;
    }

    private void restarCantidadesSegunProductosYaCargados() {
        if (movimiento == Movimiento.PEDIDO) {
            Map<Long, BigDecimal> cantidadesNuevasAux = new HashMap<>();
            if (cantidadesIniciales == null || cantidadesIniciales.isEmpty()) {// Pedido nuevo
                if (cantidadesNuevas != null && !cantidadesNuevas.isEmpty()) {
                    cantidadesNuevas.forEach((id, cantidad) -> { // se restan las cantidades nuevas a los resultados
                        productosTotal.stream().filter(p -> (id == p.getIdProducto() && p.isIlimitado() == false))
                                .forEachOrdered(p -> {
                                    p.getCantidadEnSucursalesDisponible().forEach(cantidadEnSucursal -> {
                                        BigDecimal resultado;
                                        if (cantidadEnSucursal.getIdSucursal().equals(SucursalActiva.getInstance().getSucursal().getIdSucursal())) {
                                            resultado = cantidadEnSucursal.getCantidad().subtract(cantidad);
                                            if (resultado.compareTo(BigDecimal.ZERO) >= 0) {
                                                cantidadEnSucursal.setCantidad(resultado);
                                            } else {
                                                cantidadEnSucursal.setCantidad(BigDecimal.ZERO);
                                            }
                                            p.setCantidadTotalEnSucursales(p.getCantidadTotalEnSucursales().subtract(cantidad));
                                        } else {
                                            resultado = p.getCantidadTotalEnSucursales().subtract(cantidad);
                                            if (resultado.compareTo(BigDecimal.ZERO) >= 0) {
                                                cantidadEnSucursal.setCantidad(resultado);
                                            } else {
                                                cantidadEnSucursal.setCantidad(BigDecimal.ZERO);
                                            }
                                        }
                                    });
                                });
                    });
                }
            } else { // actualización (las cantidades iniciales son de un pedido guardado
                cantidadesNuevas.forEach((id, cantidadNueva) -> { //Realizar la diferencia entre la cantidad nueva y la cantidad inicial
                    BigDecimal cantidadInicial = cantidadesIniciales.get(id);
                    if (cantidadInicial != null) {
                        BigDecimal diferencia = cantidadNueva.subtract(cantidadInicial);
                        if (diferencia.compareTo(BigDecimal.ZERO) != 0) {
                            cantidadesNuevasAux.put(id, diferencia);
                        } else {
                            cantidadesNuevasAux.put(id, BigDecimal.ZERO);
                        }
                    }
                });
                cantidadesNuevasAux.forEach((id, cantidad) -> {// restar el resultado de la operación anterior
                    if (cantidad.compareTo(BigDecimal.ZERO) != 0) {
                        productosTotal.stream().filter(producto -> (id == producto.getIdProducto() && producto.isIlimitado() == false))
                                .forEachOrdered(p -> {
                                    p.getCantidadEnSucursalesDisponible().forEach(cantidadEnSucursal -> {
                                        if (cantidadEnSucursal.getIdSucursal().equals(SucursalActiva.getInstance().getSucursal().getIdSucursal())) {
                                            BigDecimal resultado = cantidadEnSucursal.getCantidad().subtract(cantidad);
                                            if (resultado.compareTo(BigDecimal.ZERO) >= 0) {
                                                cantidadEnSucursal.setCantidad(resultado);
                                            } else {
                                                cantidadEnSucursal.setCantidad(BigDecimal.ZERO);
                                            }
                                            p.setCantidadTotalEnSucursales(p.getCantidadTotalEnSucursales().subtract(cantidad));
                                        }
                                    });
                                });
                    }
                });
                cantidadesNuevas.keySet().forEach(id -> {
                    cantidadesIniciales.remove(id);
                });
                cantidadesIniciales.forEach((id, cantidad) -> { // sumar los renglones iniciales faltantes en los nuevos renglones
                    productosTotal.stream().filter(p -> (id == p.getIdProducto() && p.isIlimitado() == false))
                            .forEachOrdered(p -> {
                                p.getCantidadEnSucursalesDisponible().forEach(cantidadEnSucursal -> {
                                    if (cantidadEnSucursal.getIdSucursal().equals(SucursalActiva.getInstance().getSucursal().getIdSucursal())) {
                                        cantidadEnSucursal.setCantidad(cantidadEnSucursal.getCantidad().add(cantidad));
                                        p.setCantidadTotalEnSucursales(p.getCantidadTotalEnSucursales().add(cantidad));
                                    }
                                });
                            });
                });
            }
        }
    }

    private void actualizarEstadoSeleccion() {
        try {
            if (txtCantidad.isEditValid()) {
                txtCantidad.commitEdit();
            }
            if (txtBonificacion.isEditValid()) {
                txtBonificacion.commitEdit();
            }
        } catch (ParseException ex) {
            String msjError = "Se produjo un error analizando los campos.";
            LOGGER.error(msjError + " - " + ex.getMessage());
        }
    }

    private void seleccionarProductoEnTable() {
        int fila = tbl_Resultados.getSelectedRow();
        if (fila != -1) {
            productoSeleccionado = productosTotal.get(fila);                                                
            txtaNotaProducto.setText(productoSeleccionado.getNota());
            this.actualizarEstadoSeleccion();
        }
    }

    private void cargarResultadosAlTable() {
        productosParcial.stream().map(p -> {
            Object[] fila = new Object[busquedaParaFiltros ? 2 : 11];
            fila[0] = p.getCodigo();
            fila[1] = p.getDescripcion();
            if (!busquedaParaFiltros) {
                if (movimiento == Movimiento.COMPRA) {
                    p.getCantidadEnSucursales().forEach(cantidadesEnSucursal -> {
                        if (cantidadesEnSucursal.getIdSucursal().equals(SucursalActiva.getInstance().getSucursal().getIdSucursal())) {
                            fila[2] = cantidadesEnSucursal.getCantidad();
                        }
                    });
                    p.getCantidadEnSucursales().forEach(cantidadesEnSucursal -> {
                        if (cantidadesEnSucursal.getIdSucursal().equals(SucursalActiva.getInstance().getSucursal().getIdSucursal())) {
                            fila[3] = p.getCantidadTotalEnSucursales().subtract(cantidadesEnSucursal.getCantidad());
                        }
                    });
                } else {
                    p.getCantidadEnSucursalesDisponible().forEach(cantidadesEnSucursal -> {
                        if (cantidadesEnSucursal.getIdSucursal().equals(SucursalActiva.getInstance().getSucursal().getIdSucursal())) {
                            fila[2] = cantidadesEnSucursal.getCantidad();
                        }
                    });
                    BigDecimal cantidadEnOtrasSucursales
                            = p.getCantidadEnSucursalesDisponible().stream()
                                    .filter(cantidadDisponible
                                            -> !cantidadDisponible.getIdSucursal().equals(SucursalActiva.getInstance().getSucursal().getIdSucursal()))
                                    .map(CantidadEnSucursal::getCantidad).reduce(BigDecimal.ZERO, BigDecimal::add);
                    fila[3] = cantidadEnOtrasSucursales;                
                }
                fila[4] = p.getCantidadReservada();
                fila[5] = p.getBulto();
                fila[6] = p.getNombreMedida();
                BigDecimal precio = (movimiento == Movimiento.PEDIDO) ? p.getPrecioLista()
                        : (movimiento == Movimiento.COMPRA) ? p.getPrecioCosto() : BigDecimal.ZERO;
                fila[7] = precio;
                fila[8] = p.getPorcentajeBonificacionOferta();
                fila[9] = p.getPorcentajeBonificacionPrecio();
                fila[10] = p.getPrecioBonificado();
            }
            return fila;
        }).forEach(fila -> {
            modeloTablaResultados.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaResultados);
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        productosTotal.clear();
        productosParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }
    
    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void setColumnas() {
        String[] encabezados = new String[busquedaParaFiltros ? 2 : 11];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripción";
        if (!busquedaParaFiltros) {
            encabezados[2] = "Stock";
            encabezados[3] = "Otras Sucursales";
            encabezados[4] = "Reservado";
            encabezados[5] = "Venta x Cant.";
            encabezados[6] = "Medida";
            String encabezadoPrecio = (movimiento == Movimiento.PEDIDO) ? "P. Lista"
                            : (movimiento == Movimiento.COMPRA) ? "P.Costo" : "";
            encabezados[7] = encabezadoPrecio;
            encabezados[8] = "% Oferta";
            encabezados[9] = "% Bonif.";
            encabezados[10] = "P. Bonif.";
        }
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        if (!busquedaParaFiltros) {
            tipos[2] = BigDecimal.class;
            tipos[3] = BigDecimal.class;
            tipos[4] = BigDecimal.class;
            tipos[5] = BigDecimal.class;
            tipos[6] = String.class;
            tipos[7] = BigDecimal.class;
            tipos[8] = BigDecimal.class;
            tipos[9] = BigDecimal.class;
            tipos[10] = BigDecimal.class;
        }
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);
        tbl_Resultados.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(0).setMaxWidth(120);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(380);
        if (!busquedaParaFiltros) {
            tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(60);
            tbl_Resultados.getColumnModel().getColumn(2).setMaxWidth(60);
            tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(140);
            tbl_Resultados.getColumnModel().getColumn(3).setMaxWidth(140);
            tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(80);
            tbl_Resultados.getColumnModel().getColumn(4).setMaxWidth(80);
            tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(100);
            tbl_Resultados.getColumnModel().getColumn(5).setMaxWidth(100);
            tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(70);
            tbl_Resultados.getColumnModel().getColumn(6).setMaxWidth(70);
            tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(70);
            tbl_Resultados.getColumnModel().getColumn(7).setMaxWidth(70);
            tbl_Resultados.getColumnModel().getColumn(7).setCellRenderer(new ColoresCeldaRenderer());
            tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(70);
            tbl_Resultados.getColumnModel().getColumn(8).setMaxWidth(70);
            tbl_Resultados.getColumnModel().getColumn(9).setPreferredWidth(70);
            tbl_Resultados.getColumnModel().getColumn(9).setMaxWidth(70);
            tbl_Resultados.getColumnModel().getColumn(10).setPreferredWidth(70);
            tbl_Resultados.getColumnModel().getColumn(10).setMaxWidth(70);
        }
    }

    private void agregarListeners() {
        txtCriteriaBusqueda.addKeyListener(keyHandler);
        btnBuscar.addKeyListener(keyHandler);
        tbl_Resultados.addKeyListener(keyHandler);
        txtaNotaProducto.addKeyListener(keyHandler);
        txtCantidad.addKeyListener(keyHandler);
        btnAceptar.addKeyListener(keyHandler);
        txtBonificacion.addKeyListener(keyHandler);
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
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFondo = new javax.swing.JPanel();
        txtCriteriaBusqueda = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btnAceptar = new javax.swing.JButton();
        lbl_Cantidad = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JFormattedTextField();
        spNotaProducto = new javax.swing.JScrollPane();
        txtaNotaProducto = new javax.swing.JTextArea();
        lblBonificacion = new javax.swing.JLabel();
        txtBonificacion = new javax.swing.JFormattedTextField();
        btnVerMas = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        txtCriteriaBusqueda.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txtCriteriaBusqueda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCriteriaBusquedaActionPerformed(evt);
            }
        });
        txtCriteriaBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCriteriaBusquedaKeyTyped(evt);
            }
        });

        btnBuscar.setForeground(java.awt.Color.blue);
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Refresh_16x16.png"))); // NOI18N
        btnBuscar.setFocusable(false);
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbl_Resultados.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Resultados.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbl_ResultadosFocusGained(evt);
            }
        });
        tbl_Resultados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_ResultadosMouseClicked(evt);
            }
        });
        tbl_Resultados.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_ResultadosKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbl_ResultadosKeyReleased(evt);
            }
        });
        sp_Resultados.setViewportView(tbl_Resultados);

        btnAceptar.setForeground(java.awt.Color.blue);
        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_FlechaGO.png"))); // NOI18N
        btnAceptar.setFocusable(false);
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        lbl_Cantidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Cantidad.setText("Cantidad:");

        txtCantidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtCantidad.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        txtCantidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCantidadFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCantidadFocusLost(evt);
            }
        });
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCantidadKeyPressed(evt);
            }
        });

        txtaNotaProducto.setEditable(false);
        txtaNotaProducto.setColumns(20);
        txtaNotaProducto.setLineWrap(true);
        txtaNotaProducto.setRows(5);
        txtaNotaProducto.setFocusable(false);
        spNotaProducto.setViewportView(txtaNotaProducto);

        lblBonificacion.setText("Bonificacion (%):");

        txtBonificacion.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtBonificacion.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        txtBonificacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBonificacionFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBonificacionFocusLost(evt);
            }
        });
        txtBonificacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBonificacionKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBonificacionKeyReleased(evt);
            }
        });

        btnVerMas.setForeground(java.awt.Color.blue);
        btnVerMas.setText("Ver más resultados");
        btnVerMas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerMasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFondoLayout = new javax.swing.GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp_Resultados)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addComponent(txtCriteriaBusqueda)
                        .addGap(0, 0, 0)
                        .addComponent(btnBuscar))
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addComponent(spNotaProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFondoLayout.createSequentialGroup()
                                .addComponent(lbl_Cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCantidad, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
                            .addGroup(panelFondoLayout.createSequentialGroup()
                                .addComponent(lblBonificacion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBonificacion)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnVerMas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelFondoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblBonificacion, lbl_Cantidad});

        panelFondoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtBonificacion, txtCantidad});

        panelFondoLayout.setVerticalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBuscar)
                    .addComponent(txtCriteriaBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(btnVerMas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_Cantidad))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblBonificacion)
                            .addComponent(txtBonificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spNotaProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscar, txtCriteriaBusqueda});

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblBonificacion, lbl_Cantidad});

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtBonificacion, txtCantidad});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        productoSeleccionado = null;
        NUMERO_PAGINA = 0;
    }//GEN-LAST:event_formWindowClosing

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setSize(sizeDialog);
        this.setTitle("Buscar Producto");
        this.prepararComponentes();
        lblBonificacion.setEnabled(false);
        txtBonificacion.setEnabled(false);
        txtBonificacion.setText("");
        if (this.movimiento != null && this.movimiento.equals(Movimiento.COMPRA)) {
            txtBonificacion.setValue(BigDecimal.ZERO);
            lblBonificacion.setEnabled(true);
            txtBonificacion.setEnabled(true);
        }
    }//GEN-LAST:event_formWindowOpened

    private void txtCantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyPressed
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        } else {
            this.actualizarEstadoSeleccion();
        }
    }//GEN-LAST:event_txtCantidadKeyPressed

    private void txtCantidadFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCantidadFocusLost
        this.actualizarEstadoSeleccion();
    }//GEN-LAST:event_txtCantidadFocusLost

    private void txtCantidadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCantidadFocusGained
        this.seleccionarProductoEnTable();
        SwingUtilities.invokeLater(() -> {
            txtCantidad.selectAll();
        });
    }//GEN-LAST:event_txtCantidadFocusGained

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        this.aceptarProducto();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void tbl_ResultadosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadosKeyReleased
        this.seleccionarProductoEnTable();
    }//GEN-LAST:event_tbl_ResultadosKeyReleased

    private void tbl_ResultadosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadosKeyPressed
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        }
        if (evt.getKeyCode() == 9) {
            txtCantidad.requestFocus();
        }
    }//GEN-LAST:event_tbl_ResultadosKeyPressed

    private void tbl_ResultadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ResultadosMouseClicked
        this.seleccionarProductoEnTable();
    }//GEN-LAST:event_tbl_ResultadosMouseClicked

    private void tbl_ResultadosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbl_ResultadosFocusGained
        if ((tbl_Resultados.getSelectedRow() == -1) && (tbl_Resultados.getRowCount() != 0)) {
            tbl_Resultados.setRowSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_tbl_ResultadosFocusGained

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void txtCriteriaBusquedaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCriteriaBusquedaKeyTyped
        evt.setKeyChar(Utilidades.convertirAMayusculas(evt.getKeyChar()));
    }//GEN-LAST:event_txtCriteriaBusquedaKeyTyped

    private void txtCriteriaBusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCriteriaBusquedaActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }//GEN-LAST:event_txtCriteriaBusquedaActionPerformed

    private void txtBonificacionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBonificacionKeyReleased
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        } else {
            this.actualizarEstadoSeleccion();
        }
    }//GEN-LAST:event_txtBonificacionKeyReleased

    private void txtBonificacionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBonificacionKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_txtBonificacionKeyTyped

    private void txtBonificacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBonificacionFocusLost
        this.actualizarEstadoSeleccion();
    }//GEN-LAST:event_txtBonificacionFocusLost

    private void txtBonificacionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBonificacionFocusGained
        SwingUtilities.invokeLater(() -> {
            txtBonificacion.selectAll();
        });
    }//GEN-LAST:event_txtBonificacionFocusGained

    private void btnVerMasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerMasActionPerformed
        if (productosTotal.size() >= 10) {
            NUMERO_PAGINA += 1;
            buscar();
        }
    }//GEN-LAST:event_btnVerMasActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnVerMas;
    private javax.swing.JLabel lblBonificacion;
    private javax.swing.JLabel lbl_Cantidad;
    private javax.swing.JPanel panelFondo;
    private javax.swing.JScrollPane spNotaProducto;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JFormattedTextField txtBonificacion;
    private javax.swing.JFormattedTextField txtCantidad;
    private javax.swing.JTextField txtCriteriaBusqueda;
    private javax.swing.JTextArea txtaNotaProducto;
    // End of variables declaration//GEN-END:variables
}
