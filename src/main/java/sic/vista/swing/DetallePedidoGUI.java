package sic.vista.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
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
import sic.modelo.Cliente;
import sic.modelo.CuentaCorrienteCliente;
import sic.modelo.PedidoDTO;
import sic.modelo.RenglonPedido;
import sic.modelo.UsuarioActivo;
import sic.modelo.EstadoPedido;
import sic.modelo.FormaDePago;
import sic.modelo.NuevoRenglonPedido;
import sic.modelo.Pedido;
import sic.modelo.Producto;
import sic.modelo.ProductoFaltante;
import sic.modelo.ProductosParaVerificarStock;
import sic.modelo.Rol;
import sic.modelo.SucursalActiva;
import sic.modelo.Transportista;
import sic.util.DecimalesRenderer;
import sic.util.Utilidades;

public class DetallePedidoGUI extends JInternalFrame {

    private Cliente cliente;
    private List<RenglonPedido> renglones = new ArrayList<>();
    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(1200, 700);
    private Pedido pedido;
    private PedidoDTO nuevoPedido;
    private final boolean modificandoPedido;
    private int cantidadMaximaRenglones = 0;
    private BigDecimal subTotalBruto;
    private final List<Rol> rolesDeUsuario = UsuarioActivo.getInstance().getUsuario().getRoles();
    private final static BigDecimal CIEN = new BigDecimal("100");

    public DetallePedidoGUI(Pedido pedido, boolean modificandoPedido) {
        this.initComponents();
        this.pedido = pedido;
        this.modificandoPedido = modificandoPedido;
        //listeners        
        btn_NuevoCliente.addKeyListener(keyHandler);
        btn_BuscarCliente.addKeyListener(keyHandler);
        btn_BuscarProductos.addKeyListener(keyHandler);
        btn_QuitarProducto.addKeyListener(keyHandler);
        tbl_Resultado.addKeyListener(keyHandler);
        txt_CodigoProducto.addKeyListener(keyHandler);
        btn_BuscarPorCodigoProducto.addKeyListener(keyHandler);
        txt_Descuento_porcentaje.addKeyListener(keyHandler);
        txt_Recargo_porcentaje.addKeyListener(keyHandler);
        btn_Continuar.addKeyListener(keyHandler);
        btnModificarCliente.addKeyListener(keyHandler);
    }

    public DetallePedidoGUI(Cliente cliente, List<RenglonPedido> renglones) {
        this.initComponents();
        this.pedido = new Pedido();
        this.cliente = cliente;
        this.renglones = renglones;
        this.modificandoPedido = false;
        //listeners        
        btn_NuevoCliente.addKeyListener(keyHandler);
        btn_BuscarCliente.addKeyListener(keyHandler);
        btn_BuscarProductos.addKeyListener(keyHandler);
        btn_QuitarProducto.addKeyListener(keyHandler);
        tbl_Resultado.addKeyListener(keyHandler);
        txt_CodigoProducto.addKeyListener(keyHandler);
        btn_BuscarPorCodigoProducto.addKeyListener(keyHandler);
        txt_Descuento_porcentaje.addKeyListener(keyHandler);
        txt_Recargo_porcentaje.addKeyListener(keyHandler);
        btn_Continuar.addKeyListener(keyHandler);
        btnModificarCliente.addKeyListener(keyHandler);
    }

    private boolean existeClientePredeterminado() {
        return RestClient.getRestTemplate().getForObject("/clientes/existe-predeterminado", boolean.class);
    }

    private boolean existeFormaDePagoPredeterminada() {
        FormaDePago formaDePago = RestClient.getRestTemplate()
                .getForObject("/formas-de-pago/predeterminada",
                        FormaDePago.class);
        return (formaDePago != null);
    }

    private boolean existeTransportistaCargado() {
        if (Arrays.asList(RestClient.getRestTemplate().
                getForObject("/transportistas",
                        Transportista[].class)).isEmpty()) {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_transportista_ninguno_cargado"), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    private void cargarCliente(CuentaCorrienteCliente cuentaCorrienteCliente) {
        this.cliente = cuentaCorrienteCliente.getCliente();
        txtNombreCliente.setText(cliente.getNombreFiscal() + " (" + cliente.getNroCliente() + ")");
        ftxtSaldoFinal.setValue(cuentaCorrienteCliente.getSaldo().setScale(2, RoundingMode.HALF_UP));
        if (cuentaCorrienteCliente.getSaldo().setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) < 0) {
            ftxtSaldoFinal.setBackground(Color.PINK);
        } else if (cuentaCorrienteCliente.getSaldo().setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) >= 0) {
            ftxtSaldoFinal.setBackground(Color.GREEN);
        }
        ftxtCompraMinima.setValue(cliente.getMontoCompraMinima().setScale(2, RoundingMode.HALF_UP));
        txtUbicacionCliente.setText(cliente.getUbicacionFacturacion() != null ? cliente.getUbicacionFacturacion().toString() : "");
        txt_CondicionIVACliente.setText(cliente.getCategoriaIVA().toString());
        txtIdFiscalCliente.setText(cliente.getIdFiscal() != null ? cliente.getIdFiscal().toString() : "");
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[7];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripcion";
        encabezados[2] = "Unidad";
        encabezados[3] = "Cantidad";
        encabezados[4] = "P. Unitario";
        encabezados[5] = "% Descuento";
        encabezados[6] = "Importe";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultado.setModel(modeloTablaResultados);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = BigDecimal.class;
        tipos[4] = BigDecimal.class;
        tipos[5] = BigDecimal.class;
        tipos[6] = BigDecimal.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultado.getTableHeader().setReorderingAllowed(false);
        tbl_Resultado.getTableHeader().setResizingAllowed(true);
        //render para los tipos de datos
        tbl_Resultado.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        //tamanios de columnas
        tbl_Resultado.getColumnModel().getColumn(0).setPreferredWidth(170);
        tbl_Resultado.getColumnModel().getColumn(1).setPreferredWidth(580);
        tbl_Resultado.getColumnModel().getColumn(2).setPreferredWidth(120);
        tbl_Resultado.getColumnModel().getColumn(3).setPreferredWidth(120);
        tbl_Resultado.getColumnModel().getColumn(4).setPreferredWidth(120);
        tbl_Resultado.getColumnModel().getColumn(5).setPreferredWidth(120);
        tbl_Resultado.getColumnModel().getColumn(6).setPreferredWidth(120);
    }

    private void agregarRenglon(NuevoRenglonPedido nuevoRenglonPedido) {
        try {
            boolean agregado = false;
            List<NuevoRenglonPedido> nuevosRenglonesPedido = new ArrayList();
            this.renglones.forEach(r -> nuevosRenglonesPedido.add(
                    new NuevoRenglonPedido(r.getIdProductoItem(), r.getCantidad())));
            for (int i = 0; i < nuevosRenglonesPedido.size(); i++) {
                if (nuevosRenglonesPedido.get(i).getIdProductoItem() == nuevoRenglonPedido.getIdProductoItem()) {
                    nuevosRenglonesPedido.get(i).setCantidad(nuevosRenglonesPedido.get(i).getCantidad().add(nuevoRenglonPedido.getCantidad()));
                    agregado = true;
                }
            }
            if (!agregado) {
                nuevosRenglonesPedido.add(nuevoRenglonPedido);
            }
            renglones.clear();
            Collections.addAll(renglones, RestClient.getRestTemplate().postForObject("/pedidos/renglones/clientes/" + this.cliente.getIdCliente(),
                    nuevosRenglonesPedido, RenglonPedido[].class));
            //para que baje solo el scroll vertical
            Point p = new Point(0, tbl_Resultado.getHeight());
            sp_Resultado.getViewport().setViewPosition(p);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarRenglonesAlTable() {
        modeloTablaResultados = new ModeloTabla();
        this.setColumnas();
        renglones.stream().map(renglon -> {
            Object[] fila = new Object[7];
            fila[0] = renglon.getCodigoItem();
            fila[1] = renglon.getDescripcionItem();
            fila[2] = renglon.getMedidaItem();
            fila[3] = renglon.getCantidad();
            fila[4] = renglon.getPrecioUnitario();
            fila[5] = renglon.getBonificacionPorcentaje();
            fila[6] = renglon.getImporte();
            return fila;
        }).forEachOrdered(fila -> {
            modeloTablaResultados.addRow(fila);
        });
        tbl_Resultado.setModel(modeloTablaResultados);
    }

    private void buscarProductoConVentanaAuxiliar() {
        if (cantidadMaximaRenglones > renglones.size()) {
            List<RenglonPedido> renglonesDelPedido = new ArrayList<>();
            Long idPedido = null;
            try {
                if (pedido.getIdPedido() != 0L) {
                    renglonesDelPedido = Arrays.asList(RestClient.getRestTemplate()
                            .getForObject("/pedidos/" + pedido.getIdPedido() + "/renglones", RenglonPedido[].class));
                    idPedido = pedido.getIdPedido();
                }
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_error_conexion"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            Map<Long, BigDecimal> idsAndCantidadesNuevas = new HashMap<>();
            renglones.forEach(renglon -> idsAndCantidadesNuevas.put(renglon.getIdProductoItem(), renglon.getCantidad()));
            BuscarProductosGUI buscarProductosGUI = new BuscarProductosGUI(idsAndCantidadesNuevas, renglonesDelPedido, idPedido);
            buscarProductosGUI.setModal(true);
            buscarProductosGUI.setLocationRelativeTo(this);
            buscarProductosGUI.setVisible(true);
            if (buscarProductosGUI.debeCargarRenglon()) {
                this.agregarRenglon(buscarProductosGUI.getRenglonPedido());
                this.cargarRenglonesAlTable();
                this.calcularResultados();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_maxima_cantidad_de_renglones"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarProductoPorCodigo() {
        try {
            Producto producto = RestClient.getRestTemplate().getForObject("/productos/busqueda?"
                    + "&codigo=" + txt_CodigoProducto.getText().trim(), Producto.class);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_producto_no_encontrado"), "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                long[] idsProductos = {producto.getIdProducto()};
                ProductosParaVerificarStock productosParaVerificarStock = ProductosParaVerificarStock.builder()
                        .cantidad(new BigDecimal[]{BigDecimal.ONE})
                        .idProducto(idsProductos)
                        .build();
                HttpEntity<ProductosParaVerificarStock> requestEntity = new HttpEntity<>(productosParaVerificarStock);
                boolean existeStockSuficiente;
                existeStockSuficiente = RestClient.getRestTemplate()
                        .exchange("/productos/disponibilidad-stock", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<ProductoFaltante>>() {
                        })
                        .getBody().isEmpty();
                if (!existeStockSuficiente) {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_producto_sin_stock_suficiente"), "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    NuevoRenglonPedido nuevoRenglonPedido = new NuevoRenglonPedido();
                    nuevoRenglonPedido.setIdProductoItem(producto.getIdProducto());
                    nuevoRenglonPedido.setCantidad(BigDecimal.ONE);
                    this.agregarRenglon(nuevoRenglonPedido);
                    this.cargarRenglonesAlTable();
                    this.calcularResultados();
                    txt_CodigoProducto.setText("");
                }
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_error_conexion"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validarComponentesDeResultados() {
        if (txt_Descuento_porcentaje.isEditValid()) {
            try {
                txt_Descuento_porcentaje.commitEdit();
            } catch (ParseException ex) {
                String mensaje = "Se produjo un error analizando los campos.";
                LOGGER.error(mensaje + " - " + ex.getMessage());
            }
        }
        if (txt_Recargo_porcentaje.isEditValid()) {
            try {
                txt_Recargo_porcentaje.commitEdit();
            } catch (ParseException ex) {
                String mensaje = "Se produjo un error analizando los campos.";
                LOGGER.error(mensaje + " - " + ex.getMessage());
            }
        }
    }

    private void calcularResultados() {
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal descuentoPorcentaje;
        BigDecimal descuentoNeto;
        BigDecimal recargoPorcentaje;
        BigDecimal recargoNeto;
        this.validarComponentesDeResultados();
        for (RenglonPedido renglon : renglones) {
            subTotal = subTotal.add(renglon.getImporte());
        }
        txt_Subtotal.setValue(subTotal);
        descuentoPorcentaje = new BigDecimal(txt_Descuento_porcentaje.getValue().toString());
        descuentoNeto = subTotal.multiply(descuentoPorcentaje).divide(CIEN, 15, RoundingMode.HALF_UP);
        txt_Descuento_neto.setValue(descuentoNeto);
        recargoPorcentaje = new BigDecimal(txt_Recargo_porcentaje.getValue().toString());
        recargoNeto = subTotal.multiply(recargoPorcentaje).divide(CIEN, 15, RoundingMode.HALF_UP);
        txt_Recargo_neto.setValue(recargoNeto);
        subTotalBruto = subTotal.add(recargoNeto).subtract(descuentoNeto);
        txt_Total.setValue(subTotalBruto);
    }

    private void construirPedido() {
        nuevoPedido = new PedidoDTO();
        nuevoPedido.setRecargoPorcentaje(new BigDecimal(txt_Recargo_porcentaje.getValue().toString()));
        nuevoPedido.setDescuentoPorcentaje(new BigDecimal(txt_Descuento_porcentaje.getValue().toString()));
        nuevoPedido.setObservaciones(txt_Observaciones.getText());
        nuevoPedido.setRenglones(this.calcularNuevosRenglonesPedido());
    }

    private void finalizarPedido() {
        if (nuevoPedido != null) {
            System.out.print(cliente.getNroCliente());
            CerrarOperacionGUI cerrarOperacionGUI = new CerrarOperacionGUI(nuevoPedido, subTotalBruto, cliente);
            cerrarOperacionGUI.setModal(true);
            cerrarOperacionGUI.setLocationRelativeTo(this);
            cerrarOperacionGUI.setVisible(true);
            if (cerrarOperacionGUI.isExito()) {
                if (modificandoPedido) {
                    this.dispose();
                } else {
                    int respuesta = JOptionPane.showConfirmDialog(this,
                            "El pedido se guardó correctamente.\n¿Desea dar de alta otro pedido?",
                            "Aviso", JOptionPane.YES_NO_OPTION);
                    if (respuesta == JOptionPane.NO_OPTION) {
                        this.dispose();
                    }
                    this.cliente = null;
                    this.cargarComponentesIniciales();
                    this.renglones = new ArrayList<>();
                    this.cargarRenglonesAlTable();
                    this.calcularResultados();
                }
            }
        } else if ((pedido.getEstado() == EstadoPedido.ABIERTO || pedido.getEstado() == null) && modificandoPedido == true) {
            this.actualizarPedido(pedido);
        }
    }

    private void actualizarPedido(Pedido pedido) {
        pedido = RestClient.getRestTemplate().getForObject("/pedidos/" + pedido.getIdPedido(), Pedido.class);
        pedido.setRenglones(this.renglones);
        pedido.setSubTotal(new BigDecimal(txt_Subtotal.getValue().toString()));
        pedido.setRecargoNeto(new BigDecimal(txt_Recargo_neto.getValue().toString()));
        pedido.setRecargoPorcentaje(new BigDecimal(txt_Recargo_porcentaje.getValue().toString()));
        pedido.setDescuentoNeto(new BigDecimal(txt_Descuento_neto.getValue().toString()));
        pedido.setDescuentoPorcentaje(new BigDecimal(txt_Descuento_porcentaje.getValue().toString()));
        pedido.setTotal(new BigDecimal(txt_Total.getValue().toString()));
        pedido.setObservaciones(txt_Observaciones.getText());
        CerrarOperacionGUI cerrarOperacionGUI = new CerrarOperacionGUI(pedido, cliente);
        cerrarOperacionGUI.setModal(true);
        cerrarOperacionGUI.setLocationRelativeTo(this);
        cerrarOperacionGUI.setVisible(true);
        if (cerrarOperacionGUI.isExito()) {
            this.dispose();
        }
    }

    private List<NuevoRenglonPedido> calcularNuevosRenglonesPedido() {
        List<NuevoRenglonPedido> nuevosRenglonesPedido = new ArrayList();
        this.renglones.forEach(r -> nuevosRenglonesPedido.add(
                new NuevoRenglonPedido(r.getIdProductoItem(), r.getCantidad())));
        return nuevosRenglonesPedido;
    }

    // Clase interna para manejar las hotkeys del TPV     
    class HotKeysHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_F2) {
                btn_BuscarClienteActionPerformed(null);
            }

            if (evt.getKeyCode() == KeyEvent.VK_F4) {
                btn_BuscarProductosActionPerformed(null);
            }

            if (evt.getKeyCode() == KeyEvent.VK_F5) {
                btn_NuevoClienteActionPerformed(null);
            }

            if (evt.getKeyCode() == KeyEvent.VK_F6) {
                btnModificarClienteActionPerformed(null);
            }

            if (evt.getKeyCode() == KeyEvent.VK_F9) {
                btn_ContinuarActionPerformed(null);
            }

            if (evt.getSource() == tbl_Resultado && evt.getKeyCode() == 127) {
                btn_QuitarProductoActionPerformed(null);
            }

            if (evt.getSource() == tbl_Resultado && evt.getKeyCode() == KeyEvent.VK_TAB) {
                txt_Descuento_porcentaje.requestFocus();
            }
        }
    };

    private void cargarComponentesIniciales() {
        cantidadMaximaRenglones = RestClient.getRestTemplate().getForObject("/configuraciones-sucursal/"
                + SucursalActiva.getInstance().getSucursal().getIdSucursal()
                + "/cantidad-renglones", Integer.class);
        if (rolesDeUsuario.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuario.contains(Rol.ENCARGADO)
                || rolesDeUsuario.contains(Rol.VENDEDOR)) {
            if (this.existeClientePredeterminado() && this.cliente == null) {
                CuentaCorrienteCliente cuentaCorrienteClientePredeterminado
                        = RestClient.getRestTemplate().getForObject("/cuentas-corriente/clientes/predeterminado", CuentaCorrienteCliente.class);
                this.cargarCliente(cuentaCorrienteClientePredeterminado);
                this.btnModificarCliente.setEnabled(true);
            }
        }
        if (!this.existeFormaDePagoPredeterminada() || !this.existeTransportistaCargado()) {
            this.dispose();
        }
        this.setTitle(this.modificandoPedido ? "Modificar Pedido" : "Nuevo Pedido");
        txt_Observaciones.setText(this.pedido.getObservaciones());
    }

    private BigDecimal[] getArrayCantidades() {
        BigDecimal[] cantidades = new BigDecimal[renglones.size()];
        if (renglones != null && !renglones.isEmpty()) {
            int i = 0;
            for (RenglonPedido r : renglones) {
                cantidades[i] = r.getCantidad();
                i++;
            }
        }
        return cantidades;
    }

    private long[] getArrayIds() {
        long[] ids = new long[renglones.size()];
        if (renglones != null && !renglones.isEmpty()) {
            int i = 0;
            for (RenglonPedido r : renglones) {
                ids[i] = r.getIdProductoItem();
                i++;
            }
        }
        return ids;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelGeneral = new javax.swing.JPanel();
        panelCliente = new javax.swing.JPanel();
        lblNombreCliente = new javax.swing.JLabel();
        lblUbicacionCliente = new javax.swing.JLabel();
        lbl_IdFiscalCliente = new javax.swing.JLabel();
        lbl_CondicionIVACliente = new javax.swing.JLabel();
        txt_CondicionIVACliente = new javax.swing.JTextField();
        txtUbicacionCliente = new javax.swing.JTextField();
        txtNombreCliente = new javax.swing.JTextField();
        lblMontoCompraMinima = new javax.swing.JLabel();
        txtIdFiscalCliente = new javax.swing.JTextField();
        lblSaldoCC = new javax.swing.JLabel();
        ftxtSaldoFinal = new javax.swing.JFormattedTextField();
        ftxtCompraMinima = new javax.swing.JFormattedTextField();
        panelRenglones = new javax.swing.JPanel();
        sp_Resultado = new javax.swing.JScrollPane();
        tbl_Resultado = new javax.swing.JTable();
        btn_BuscarProductos = new javax.swing.JButton();
        btn_QuitarProducto = new javax.swing.JButton();
        txt_CodigoProducto = new javax.swing.JTextField();
        btn_BuscarPorCodigoProducto = new javax.swing.JButton();
        panelObservaciones = new javax.swing.JPanel();
        lbl_Observaciones = new javax.swing.JLabel();
        btn_AddComment = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_Observaciones = new javax.swing.JTextArea();
        panelResultados = new javax.swing.JPanel();
        lbl_SubTotal = new javax.swing.JLabel();
        txt_Subtotal = new javax.swing.JFormattedTextField();
        lbl_Total = new javax.swing.JLabel();
        txt_Total = new javax.swing.JFormattedTextField();
        txt_Descuento_porcentaje = new javax.swing.JFormattedTextField();
        txt_Descuento_neto = new javax.swing.JFormattedTextField();
        lbl_DescuentoRecargo = new javax.swing.JLabel();
        lbl_recargoPorcentaje = new javax.swing.JLabel();
        txt_Recargo_neto = new javax.swing.JFormattedTextField();
        txt_Recargo_porcentaje = new javax.swing.JFormattedTextField();
        btn_Continuar = new javax.swing.JButton();
        panelEncabezado = new javax.swing.JPanel();
        btn_NuevoCliente = new javax.swing.JButton();
        btn_BuscarCliente = new javax.swing.JButton();
        lblSeparadorDerecho = new javax.swing.JLabel();
        btnModificarCliente = new javax.swing.JButton();

        setResizable(true);
        setToolTipText("");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoNuevo_16x16.png"))); // NOI18N
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

        panelGeneral.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblNombreCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNombreCliente.setText("Nombre:");

        lblUbicacionCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblUbicacionCliente.setText("Ubicación:");

        lbl_IdFiscalCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_IdFiscalCliente.setText("CUIT o DNI:");

        lbl_CondicionIVACliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_CondicionIVACliente.setText("Categoria IVA:");

        txt_CondicionIVACliente.setEditable(false);
        txt_CondicionIVACliente.setFocusable(false);

        txtUbicacionCliente.setEditable(false);
        txtUbicacionCliente.setFocusable(false);

        txtNombreCliente.setEditable(false);
        txtNombreCliente.setFocusable(false);

        lblMontoCompraMinima.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMontoCompraMinima.setText("Compra Mín. $:");

        txtIdFiscalCliente.setEditable(false);
        txtIdFiscalCliente.setFocusable(false);

        lblSaldoCC.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblSaldoCC.setText("Saldo CC $:");

        ftxtSaldoFinal.setEditable(false);
        ftxtSaldoFinal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtSaldoFinal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxtSaldoFinal.setFocusable(false);
        ftxtSaldoFinal.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        ftxtCompraMinima.setEditable(false);
        ftxtCompraMinima.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtCompraMinima.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxtCompraMinima.setFocusable(false);
        ftxtCompraMinima.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        javax.swing.GroupLayout panelClienteLayout = new javax.swing.GroupLayout(panelCliente);
        panelCliente.setLayout(panelClienteLayout);
        panelClienteLayout.setHorizontalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUbicacionCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_CondicionIVACliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtUbicacionCliente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
                    .addComponent(txt_CondicionIVACliente)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblMontoCompraMinima, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSaldoCC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_IdFiscalCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtIdFiscalCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                    .addComponent(ftxtSaldoFinal)
                    .addComponent(ftxtCompraMinima))
                .addContainerGap())
        );
        panelClienteLayout.setVerticalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNombreCliente)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_IdFiscalCliente)
                    .addComponent(txtIdFiscalCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblUbicacionCliente)
                    .addComponent(txtUbicacionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSaldoCC)
                    .addComponent(ftxtSaldoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lbl_CondicionIVACliente)
                        .addComponent(txt_CondicionIVACliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblMontoCompraMinima))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelClienteLayout.createSequentialGroup()
                        .addComponent(ftxtCompraMinima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        panelClienteLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtIdFiscalCliente, txtNombreCliente, txtUbicacionCliente, txt_CondicionIVACliente});

        panelClienteLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblMontoCompraMinima, lbl_IdFiscalCliente});

        tbl_Resultado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultado.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbl_Resultado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbl_ResultadoFocusGained(evt);
            }
        });
        tbl_Resultado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_ResultadoMouseClicked(evt);
            }
        });
        sp_Resultado.setViewportView(tbl_Resultado);

        btn_BuscarProductos.setForeground(java.awt.Color.blue);
        btn_BuscarProductos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Product_16x16.png"))); // NOI18N
        btn_BuscarProductos.setText("Buscar Producto (F4)");
        btn_BuscarProductos.setFocusable(false);
        btn_BuscarProductos.setPreferredSize(new java.awt.Dimension(200, 30));
        btn_BuscarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarProductosActionPerformed(evt);
            }
        });

        btn_QuitarProducto.setForeground(java.awt.Color.blue);
        btn_QuitarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteProduct_16x16.png"))); // NOI18N
        btn_QuitarProducto.setText("Quitar Producto (DEL)");
        btn_QuitarProducto.setFocusable(false);
        btn_QuitarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_QuitarProductoActionPerformed(evt);
            }
        });

        txt_CodigoProducto.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        txt_CodigoProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_CodigoProductoActionPerformed(evt);
            }
        });

        btn_BuscarPorCodigoProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/16x16.png"))); // NOI18N
        btn_BuscarPorCodigoProducto.setFocusable(false);
        btn_BuscarPorCodigoProducto.setPreferredSize(new java.awt.Dimension(34, 28));
        btn_BuscarPorCodigoProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarPorCodigoProductoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRenglonesLayout = new javax.swing.GroupLayout(panelRenglones);
        panelRenglones.setLayout(panelRenglonesLayout);
        panelRenglonesLayout.setHorizontalGroup(
            panelRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultado)
            .addGroup(panelRenglonesLayout.createSequentialGroup()
                .addComponent(txt_CodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btn_BuscarPorCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_BuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btn_QuitarProducto)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        panelRenglonesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_BuscarProductos, btn_QuitarProducto});

        panelRenglonesLayout.setVerticalGroup(
            panelRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRenglonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_CodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_BuscarPorCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_BuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_QuitarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultado, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
        );

        panelRenglonesLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_BuscarPorCodigoProducto, txt_CodigoProducto});

        lbl_Observaciones.setText("Observaciones:");

        btn_AddComment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Comment_16x16.png"))); // NOI18N
        btn_AddComment.setFocusable(false);
        btn_AddComment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AddCommentActionPerformed(evt);
            }
        });

        txt_Observaciones.setEditable(false);
        txt_Observaciones.setBackground(new java.awt.Color(220, 215, 215));
        txt_Observaciones.setColumns(20);
        txt_Observaciones.setRows(5);
        txt_Observaciones.setFocusable(false);
        jScrollPane1.setViewportView(txt_Observaciones);

        javax.swing.GroupLayout panelObservacionesLayout = new javax.swing.GroupLayout(panelObservaciones);
        panelObservaciones.setLayout(panelObservacionesLayout);
        panelObservacionesLayout.setHorizontalGroup(
            panelObservacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelObservacionesLayout.createSequentialGroup()
                .addGroup(panelObservacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 478, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_AddComment)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelObservacionesLayout.setVerticalGroup(
            panelObservacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelObservacionesLayout.createSequentialGroup()
                .addComponent(lbl_Observaciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelObservacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelObservacionesLayout.createSequentialGroup()
                        .addComponent(btn_AddComment)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                .addContainerGap())
        );

        lbl_SubTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_SubTotal.setText("SubTotal:");

        txt_Subtotal.setEditable(false);
        txt_Subtotal.setForeground(new java.awt.Color(29, 156, 37));
        txt_Subtotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Subtotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Subtotal.setText("0");
        txt_Subtotal.setFocusable(false);
        txt_Subtotal.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        lbl_Total.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_Total.setText("Total:");

        txt_Total.setEditable(false);
        txt_Total.setForeground(new java.awt.Color(29, 156, 37));
        txt_Total.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Total.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Total.setText("0");
        txt_Total.setFocusable(false);
        txt_Total.setFont(new java.awt.Font("DejaVu Sans", 1, 24)); // NOI18N

        txt_Descuento_porcentaje.setForeground(new java.awt.Color(29, 156, 37));
        txt_Descuento_porcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Descuento_porcentaje.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Descuento_porcentaje.setText("0");
        txt_Descuento_porcentaje.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_Descuento_porcentaje.setNextFocusableComponent(txt_Recargo_porcentaje);
        txt_Descuento_porcentaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Descuento_porcentajeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Descuento_porcentajeFocusLost(evt);
            }
        });
        txt_Descuento_porcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_Descuento_porcentajeActionPerformed(evt);
            }
        });
        txt_Descuento_porcentaje.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_Descuento_porcentajeKeyTyped(evt);
            }
        });

        txt_Descuento_neto.setEditable(false);
        txt_Descuento_neto.setForeground(new java.awt.Color(29, 156, 37));
        txt_Descuento_neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Descuento_neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Descuento_neto.setText("0");
        txt_Descuento_neto.setFocusable(false);
        txt_Descuento_neto.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        lbl_DescuentoRecargo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_DescuentoRecargo.setText("Descuento (%):");

        lbl_recargoPorcentaje.setText("Recargo (%):");

        txt_Recargo_neto.setEditable(false);
        txt_Recargo_neto.setForeground(new java.awt.Color(29, 156, 37));
        txt_Recargo_neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Recargo_neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Recargo_neto.setText("0");
        txt_Recargo_neto.setFocusable(false);
        txt_Recargo_neto.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        txt_Recargo_porcentaje.setForeground(new java.awt.Color(29, 156, 37));
        txt_Recargo_porcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Recargo_porcentaje.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Recargo_porcentaje.setText("0");
        txt_Recargo_porcentaje.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_Recargo_porcentaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Recargo_porcentajeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Recargo_porcentajeFocusLost(evt);
            }
        });
        txt_Recargo_porcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_Recargo_porcentajeActionPerformed(evt);
            }
        });
        txt_Recargo_porcentaje.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_Recargo_porcentajeKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_DescuentoRecargo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_SubTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_Subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelResultadosLayout.createSequentialGroup()
                                .addComponent(txt_Descuento_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_Descuento_neto, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addComponent(lbl_Total, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_Total))
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addComponent(lbl_recargoPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_Recargo_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_Recargo_neto, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelResultadosLayout.createSequentialGroup()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_SubTotal)
                    .addComponent(txt_Subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_DescuentoRecargo)
                    .addComponent(txt_Descuento_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Descuento_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_Recargo_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Recargo_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_recargoPorcentaje))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Total)
                    .addComponent(txt_Total, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txt_Descuento_neto, txt_Descuento_porcentaje, txt_Recargo_neto, txt_Recargo_porcentaje, txt_Subtotal});

        btn_Continuar.setForeground(java.awt.Color.blue);
        btn_Continuar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_FlechaGO.png"))); // NOI18N
        btn_Continuar.setText("Continuar (F9)");
        btn_Continuar.setFocusable(false);
        btn_Continuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ContinuarActionPerformed(evt);
            }
        });

        btn_NuevoCliente.setForeground(java.awt.Color.blue);
        btn_NuevoCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddClient_16x16.png"))); // NOI18N
        btn_NuevoCliente.setText("Nuevo Cliente (F5)");
        btn_NuevoCliente.setFocusable(false);
        btn_NuevoCliente.setPreferredSize(new java.awt.Dimension(200, 30));
        btn_NuevoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoClienteActionPerformed(evt);
            }
        });

        btn_BuscarCliente.setForeground(java.awt.Color.blue);
        btn_BuscarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Client_16x16.png"))); // NOI18N
        btn_BuscarCliente.setText("Buscar Cliente (F2)");
        btn_BuscarCliente.setFocusable(false);
        btn_BuscarCliente.setPreferredSize(new java.awt.Dimension(200, 30));
        btn_BuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarClienteActionPerformed(evt);
            }
        });

        lblSeparadorDerecho.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        btnModificarCliente.setForeground(java.awt.Color.blue);
        btnModificarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditClient_16x16.png"))); // NOI18N
        btnModificarCliente.setText("Modificar Cliente (F6)");
        btnModificarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelEncabezadoLayout = new javax.swing.GroupLayout(panelEncabezado);
        panelEncabezado.setLayout(panelEncabezadoLayout);
        panelEncabezadoLayout.setHorizontalGroup(
            panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezadoLayout.createSequentialGroup()
                .addComponent(btn_NuevoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btn_BuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnModificarCliente)
                .addGap(99, 99, 99)
                .addComponent(lblSeparadorDerecho, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                .addGap(305, 305, 305))
        );

        panelEncabezadoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnModificarCliente, btn_BuscarCliente, btn_NuevoCliente});

        panelEncabezadoLayout.setVerticalGroup(
            panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezadoLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSeparadorDerecho, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_BuscarCliente, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevoCliente, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnModificarCliente, javax.swing.GroupLayout.Alignment.CENTER))
                .addContainerGap())
        );

        panelEncabezadoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnModificarCliente, btn_BuscarCliente, btn_NuevoCliente});

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelRenglones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelEncabezado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGeneralLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btn_Continuar)))
                        .addContainerGap())
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addComponent(panelObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addComponent(panelEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRenglones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Continuar)
                .addContainerGap())
        );

        panelGeneralLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {panelObservaciones, panelResultados});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_BuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarClienteActionPerformed
        BuscarClientesGUI buscarClientesGUI = new BuscarClientesGUI();
        buscarClientesGUI.setModal(true);
        buscarClientesGUI.setLocationRelativeTo(this);
        buscarClientesGUI.setVisible(true);
        if (buscarClientesGUI.getClienteSeleccionado() != null) {
            try {
                CuentaCorrienteCliente cuentaCorrienteCliente
                        = RestClient.getRestTemplate().getForObject("/cuentas-corriente/clientes/" + buscarClientesGUI.getClienteSeleccionado().getIdCliente(), CuentaCorrienteCliente.class);
                this.cargarCliente(cuentaCorrienteCliente);
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_BuscarClienteActionPerformed

    private void btn_NuevoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoClienteActionPerformed
        DetalleClienteGUI gui_DetalleCliente = new DetalleClienteGUI();
        gui_DetalleCliente.setModal(true);
        gui_DetalleCliente.setLocationRelativeTo(this);
        gui_DetalleCliente.setVisible(true);
        if (gui_DetalleCliente.getClienteDadoDeAlta() != null) {
            try {
                CuentaCorrienteCliente cuentaCorrienteCliente
                        = RestClient.getRestTemplate().getForObject("/cuentas-corriente/clientes/" + gui_DetalleCliente.getClienteDadoDeAlta().getIdCliente(), CuentaCorrienteCliente.class);
                this.cargarCliente(cuentaCorrienteCliente);
                btnModificarCliente.setEnabled(true);
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_NuevoClienteActionPerformed

    private void btn_BuscarPorCodigoProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarPorCodigoProductoActionPerformed
        this.buscarProductoPorCodigo();
    }//GEN-LAST:event_btn_BuscarPorCodigoProductoActionPerformed

    private void txt_CodigoProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_CodigoProductoActionPerformed
        this.buscarProductoPorCodigo();
    }//GEN-LAST:event_txt_CodigoProductoActionPerformed

    private void btn_AddCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AddCommentActionPerformed
        ObservacionesGUI observacionesGUI = new ObservacionesGUI(txt_Observaciones.getText());
        observacionesGUI.setModal(true);
        observacionesGUI.setLocationRelativeTo(this);
        observacionesGUI.setVisible(true);
        txt_Observaciones.setText(observacionesGUI.getTxta_Observaciones().getText());
    }//GEN-LAST:event_btn_AddCommentActionPerformed

    private void txt_Descuento_porcentajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Descuento_porcentajeFocusLost
        this.calcularResultados();
    }//GEN-LAST:event_txt_Descuento_porcentajeFocusLost

    private void txt_Descuento_porcentajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Descuento_porcentajeFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_Descuento_porcentaje.selectAll();
        });
    }//GEN-LAST:event_txt_Descuento_porcentajeFocusGained

    private void txt_Descuento_porcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_Descuento_porcentajeActionPerformed
        this.calcularResultados();
    }//GEN-LAST:event_txt_Descuento_porcentajeActionPerformed

    private void btn_ContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ContinuarActionPerformed
        if (cliente != null) {
            if (txt_Total.getValue() != null && (new BigDecimal(String.valueOf(txt_Total.getValue())).compareTo(cliente.getMontoCompraMinima())) < 0) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_pedido_no_supera_compra_minima"), "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (renglones.isEmpty()) {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_factura_sin_renglones"), "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (new BigDecimal(txt_Descuento_porcentaje.getValue().toString()).compareTo(CIEN) > 0) {
                        JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_factura_descuento_mayor_cien"), "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        this.calcularResultados();
                        try {
                            List<ProductoFaltante> faltantes;
                            BigDecimal[] cantidades = this.getArrayCantidades();
                            long[] idsProductos = this.getArrayIds();
                            ProductosParaVerificarStock productosParaVerificarStock = ProductosParaVerificarStock.builder()
                                    .cantidad(cantidades)
                                    .idProducto(idsProductos)
                                    .build();
                            if (pedido != null) {
                                productosParaVerificarStock.setIdPedido(pedido.getIdPedido());
                            }
                            HttpEntity<ProductosParaVerificarStock> requestEntity = new HttpEntity<>(productosParaVerificarStock);
                            faltantes = RestClient.getRestTemplate()
                                    .exchange("/productos/disponibilidad-stock", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<ProductoFaltante>>() {
                                    })
                                    .getBody();
                            if (faltantes != null && faltantes.isEmpty()) {
                                // Es null cuando, se genera un pedido desde el punto de venta entrando por el menu sistemas.
                                // El Id es 0 cuando, se genera un pedido desde el punto de venta entrando por el botón nuevo de administrar pedidos.
                                if (pedido == null || pedido.getIdPedido() == 0) {
                                    this.construirPedido();
                                }
                                this.finalizarPedido();
                            } else {
                                ProductosFaltantesGUI productosFaltantesGUI = new ProductosFaltantesGUI(faltantes);
                                productosFaltantesGUI.setLocationRelativeTo(this);
                                productosFaltantesGUI.setVisible(true);
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
            }
        } else {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_seleccionar_cliente"),
                    "Aviso", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_ContinuarActionPerformed

    private void btn_QuitarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_QuitarProductoActionPerformed
        int[] indicesParaEliminar = Utilidades.getSelectedRowsModelIndices(tbl_Resultado);
        List<RenglonPedido> renglonesParaBorrar = new ArrayList<>();
        for (int i = 0; i < indicesParaEliminar.length; i++) {
            renglonesParaBorrar.add(renglones.get(indicesParaEliminar[i]));
        }
        renglones.removeAll(renglonesParaBorrar);
        this.cargarRenglonesAlTable();
        this.calcularResultados();
    }//GEN-LAST:event_btn_QuitarProductoActionPerformed

    private void btn_BuscarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarProductosActionPerformed
        if (cliente != null) {
            this.buscarProductoConVentanaAuxiliar();
        } else {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_seleccionar_cliente"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_BuscarProductosActionPerformed

    private void tbl_ResultadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbl_ResultadoFocusGained
        //Si no hay nada seleccionado y NO esta vacio el table, selecciona la primer fila
        if ((tbl_Resultado.getSelectedRow() == -1) && (tbl_Resultado.getRowCount() != 0)) {
            tbl_Resultado.setRowSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_tbl_ResultadoFocusGained

    private void txt_Descuento_porcentajeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_Descuento_porcentajeKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_Descuento_porcentajeKeyTyped

    private void tbl_ResultadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ResultadoMouseClicked
        int fila = tbl_Resultado.getSelectedRow();
        int columna = tbl_Resultado.getSelectedColumn();
        if (columna == 0) {
            tbl_Resultado.setValueAt(!(boolean) tbl_Resultado.getValueAt(fila, columna), fila, columna);
        }
    }//GEN-LAST:event_tbl_ResultadoMouseClicked

    private void txt_Recargo_porcentajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Recargo_porcentajeFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_Recargo_porcentaje.selectAll();
        });
    }//GEN-LAST:event_txt_Recargo_porcentajeFocusGained

    private void txt_Recargo_porcentajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Recargo_porcentajeFocusLost
        this.calcularResultados();
    }//GEN-LAST:event_txt_Recargo_porcentajeFocusLost

    private void txt_Recargo_porcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_Recargo_porcentajeActionPerformed
        this.calcularResultados();
    }//GEN-LAST:event_txt_Recargo_porcentajeActionPerformed

    private void txt_Recargo_porcentajeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_Recargo_porcentajeKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_Recargo_porcentajeKeyTyped

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setSize(sizeInternalFrame);
            this.setColumnas();
            this.setMaximum(true);
            this.cargarComponentesIniciales();
            if (cliente != null && renglones != null && !renglones.isEmpty()) {
                this.cargarCliente(RestClient.getRestTemplate().getForObject("/cuentas-corriente/clientes/" + cliente.getIdCliente(), CuentaCorrienteCliente.class));
                this.cargarRenglonesAlTable();
                this.calcularResultados();
            }
            if (this.pedido != null && this.pedido.getIdPedido() != 0) {
                btn_NuevoCliente.setEnabled(false);
                btn_BuscarCliente.setEnabled(false);
                this.cargarCliente(RestClient.getRestTemplate().getForObject("/cuentas-corriente/clientes/" + pedido.getCliente().getIdCliente(), CuentaCorrienteCliente.class));
                this.renglones.addAll(Arrays.asList(RestClient.getRestTemplate()
                        .getForObject("/pedidos/" + this.pedido.getIdPedido() + "/renglones", RenglonPedido[].class)));
                this.cargarRenglonesAlTable();
                this.calcularResultados();
            }
        } catch (PropertyVetoException ex) {
            String msjError = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnModificarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarClienteActionPerformed
        if (cliente != null) {
            DetalleClienteGUI gui_DetalleCliente = new DetalleClienteGUI(cliente);
            gui_DetalleCliente.setModal(true);
            gui_DetalleCliente.setLocationRelativeTo(this);
            gui_DetalleCliente.setVisible(true);
            try {
                this.cargarCliente(RestClient.getRestTemplate().getForObject("/cuentas-corriente/clientes/" + this.cliente.getIdCliente(), CuentaCorrienteCliente.class));
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                this.dispose();
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
                this.dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_seleccionar_cliente"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnModificarClienteActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnModificarCliente;
    private javax.swing.JButton btn_AddComment;
    private javax.swing.JButton btn_BuscarCliente;
    private javax.swing.JButton btn_BuscarPorCodigoProducto;
    private javax.swing.JButton btn_BuscarProductos;
    private javax.swing.JButton btn_Continuar;
    private javax.swing.JButton btn_NuevoCliente;
    private javax.swing.JButton btn_QuitarProducto;
    private javax.swing.JFormattedTextField ftxtCompraMinima;
    private javax.swing.JFormattedTextField ftxtSaldoFinal;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMontoCompraMinima;
    private javax.swing.JLabel lblNombreCliente;
    private javax.swing.JLabel lblSaldoCC;
    private javax.swing.JLabel lblSeparadorDerecho;
    private javax.swing.JLabel lblUbicacionCliente;
    private javax.swing.JLabel lbl_CondicionIVACliente;
    private javax.swing.JLabel lbl_DescuentoRecargo;
    private javax.swing.JLabel lbl_IdFiscalCliente;
    private javax.swing.JLabel lbl_Observaciones;
    private javax.swing.JLabel lbl_SubTotal;
    private javax.swing.JLabel lbl_Total;
    private javax.swing.JLabel lbl_recargoPorcentaje;
    private javax.swing.JPanel panelCliente;
    private javax.swing.JPanel panelEncabezado;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelObservaciones;
    private javax.swing.JPanel panelRenglones;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane sp_Resultado;
    private javax.swing.JTable tbl_Resultado;
    private javax.swing.JTextField txtIdFiscalCliente;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JTextField txtUbicacionCliente;
    private javax.swing.JTextField txt_CodigoProducto;
    private javax.swing.JTextField txt_CondicionIVACliente;
    private javax.swing.JFormattedTextField txt_Descuento_neto;
    private javax.swing.JFormattedTextField txt_Descuento_porcentaje;
    private javax.swing.JTextArea txt_Observaciones;
    private javax.swing.JFormattedTextField txt_Recargo_neto;
    private javax.swing.JFormattedTextField txt_Recargo_porcentaje;
    private javax.swing.JFormattedTextField txt_Subtotal;
    private javax.swing.JFormattedTextField txt_Total;
    // End of variables declaration//GEN-END:variables
}
