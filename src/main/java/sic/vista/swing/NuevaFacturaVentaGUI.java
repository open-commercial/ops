package sic.vista.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
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
import sic.modelo.SucursalActiva;
import sic.modelo.RenglonFactura;
import sic.modelo.UsuarioActivo;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.Movimiento;
import sic.modelo.Pedido;
import sic.modelo.Producto;
import sic.modelo.ProductosParaVerificarStock;
import sic.modelo.Rol;
import sic.modelo.TipoDeComprobante;
import sic.modelo.Transportista;
import sic.util.DecimalesRenderer;
import sic.util.Utilidades;

public class NuevaFacturaVentaGUI extends JInternalFrame {

    private TipoDeComprobante tipoDeComprobante;
    private Cliente cliente;
    private final Pedido pedido;
    private List<RenglonFactura> renglonesFactura = new ArrayList<>();
    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(1200, 700);    
    private int cantidadMaximaRenglones = 0;
    private BigDecimal subTotalBruto;
    private BigDecimal iva_105_netoFactura;
    private BigDecimal iva_21_netoFactura;
    private final List<Rol> rolesDeUsuario = UsuarioActivo.getInstance().getUsuario().getRoles();
    private final static BigDecimal IVA_21 = new BigDecimal("21");
    private final static BigDecimal IVA_105 = new BigDecimal("10.5");
    private final static BigDecimal CIEN = new BigDecimal("100");

    public NuevaFacturaVentaGUI() {
        this.initComponents();
        this.pedido = null;        
        ImageIcon iconoNoMarcado = new ImageIcon(getClass().getResource("/sic/icons/chkNoMarcado_16x16.png"));
        this.tbtn_marcarDesmarcar.setIcon(iconoNoMarcado);        
        dc_fechaVencimiento.setDate(new Date());
        this.setListeners();
    }   
    
    public NuevaFacturaVentaGUI(Pedido pedido) {
        this.initComponents();        
        this.pedido = pedido;
        ImageIcon iconoNoMarcado = new ImageIcon(getClass().getResource("/sic/icons/chkNoMarcado_16x16.png"));
        this.tbtn_marcarDesmarcar.setIcon(iconoNoMarcado);        
        dc_fechaVencimiento.setDate(new Date());
        this.setListeners();
    }
    
    private FacturaVenta construirFactura() {
        FacturaVenta factura = new FacturaVenta();        
        factura.setTipoComprobante(this.tipoDeComprobante);
        if (this.dc_fechaVencimiento.getDate() != null) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(this.dc_fechaVencimiento.getDate());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 58);
            factura.setFechaVencimiento(cal.getTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
        }
        factura.setRenglones(this.renglonesFactura);
        factura.setObservaciones(this.txt_Observaciones.getText().trim());      
        factura.setSubTotal(new BigDecimal(txt_Subtotal.getValue().toString()));  
        factura.setDescuentoPorcentaje(new BigDecimal(txt_Descuento_porcentaje.getValue().toString()));
        factura.setDescuentoNeto(new BigDecimal(txt_Descuento_neto.getValue().toString()));
        factura.setRecargoPorcentaje(new BigDecimal(txt_Recargo_porcentaje.getValue().toString()));
        factura.setRecargoNeto(new BigDecimal(txt_Recargo_neto.getValue().toString()));
        factura.setSubTotalBruto(subTotalBruto);
        factura.setIva105Neto(iva_105_netoFactura);
        factura.setIva21Neto(iva_21_netoFactura);
        factura.setTotal(new BigDecimal(txt_Total.getValue().toString()));                      
        factura.setIdCliente(this.cliente.getIdCliente());
        factura.setIdSucursal(SucursalActiva.getInstance().getSucursal().getIdSucursal());
        return factura;
    }   
    
    private void setListeners() {
        cmb_TipoComprobante.addKeyListener(keyHandler);
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
        tbtn_marcarDesmarcar.addKeyListener(keyHandler);        
        dc_fechaVencimiento.addKeyListener(keyHandler);     
        btnModificarCliente.addKeyListener(keyHandler); 
    }
    
    private void cargarPedidoParaFacturar() {
        try {
            this.cargarCliente(RestClient.getRestTemplate()
                    .getForObject("/clientes/pedidos/" + pedido.getIdPedido(), Cliente.class));
            this.cargarTiposDeComprobantesDisponibles();            
            this.tipoDeComprobante = (TipoDeComprobante) cmb_TipoComprobante.getSelectedItem();
            this.renglonesFactura = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/facturas/renglones/pedidos/" + pedido.getIdPedido()
                            + "?tipoDeComprobante=" + this.tipoDeComprobante.name(),
                            RenglonFactura[].class)));
            EstadoRenglon[] marcaDeRenglonesDelPedido = new EstadoRenglon[renglonesFactura.size()];
            for (int i = 0; i < renglonesFactura.size(); i++) {
                marcaDeRenglonesDelPedido[i] = EstadoRenglon.DESMARCADO;
            }
            this.cargarRenglonesAlTable(marcaDeRenglonesDelPedido);
            txt_Descuento_porcentaje.setValue(pedido.getDescuentoPorcentaje());
            txt_Recargo_porcentaje.setValue(pedido.getRecargoPorcentaje());
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarEstadoDeLosChkEnTabla(JTable tbl_Resultado, EstadoRenglon[] estadosDeLosRenglones) {
        for (int i = 0; i < tbl_Resultado.getRowCount(); i++) {
            if ((boolean) tbl_Resultado.getValueAt(i, 0)) {
                estadosDeLosRenglones[i] = EstadoRenglon.MARCADO;
            } else {
                estadosDeLosRenglones[i] = EstadoRenglon.DESMARCADO;
            }
        }
    }

    private boolean existeClientePredeterminado() {
        return RestClient.getRestTemplate().getForObject("/clientes/existe-predeterminado", boolean.class);
    }

    private boolean existeFormaDePagoPredeterminada() {
        FormaDePago formaDePago = RestClient.getRestTemplate()
                .getForObject("/formas-de-pago/predeterminada", FormaDePago.class);
        return (formaDePago != null);
    }

    private boolean existeTransportistaCargado() {
        if (Arrays.asList(RestClient.getRestTemplate().
                getForObject("/transportistas/sucursales/" + SucursalActiva.getInstance().getSucursal().getIdSucursal(),
                        Transportista[].class)).isEmpty()) {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_transportista_ninguno_cargado"), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    private void cargarCliente(Cliente cliente) {
        this.cliente = cliente;
        txtNombreCliente.setText(cliente.getNombreFiscal() + " (" + cliente.getNroCliente() + ")");
        txtBonificacion.setText(cliente.getBonificacion().setScale(2, RoundingMode.HALF_UP) + " %");
        txtUbicacionCliente.setText(cliente.getUbicacionFacturacion() != null ? cliente.getUbicacionFacturacion().toString() : "");
        txt_CondicionIVACliente.setText(cliente.getCategoriaIVA().toString());
        txtIdFiscalCliente.setText(cliente.getIdFiscal() != null ? cliente.getIdFiscal().toString() : "");
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[8];
        encabezados[0] = " ";
        encabezados[1] = "Codigo";
        encabezados[2] = "Descripcion";
        encabezados[3] = "Unidad";
        encabezados[4] = "Cantidad";
        encabezados[5] = "P. Unitario";
        encabezados[6] = "% Bonificacion";
        encabezados[7] = "Importe";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultado.setModel(modeloTablaResultados);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = Boolean.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = String.class;
        tipos[4] = BigDecimal.class;
        tipos[5] = BigDecimal.class;
        tipos[6] = BigDecimal.class;
        tipos[7] = BigDecimal.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultado.getTableHeader().setReorderingAllowed(false);
        tbl_Resultado.getTableHeader().setResizingAllowed(true);
        //render para los tipos de datos
        tbl_Resultado.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        //tamanios de columnas
        tbl_Resultado.getColumnModel().getColumn(0).setPreferredWidth(25);
        tbl_Resultado.getColumnModel().getColumn(1).setPreferredWidth(170);
        tbl_Resultado.getColumnModel().getColumn(2).setPreferredWidth(580);
        tbl_Resultado.getColumnModel().getColumn(3).setPreferredWidth(120);
        tbl_Resultado.getColumnModel().getColumn(4).setPreferredWidth(120);
        tbl_Resultado.getColumnModel().getColumn(5).setPreferredWidth(120);
        tbl_Resultado.getColumnModel().getColumn(6).setPreferredWidth(120);
        tbl_Resultado.getColumnModel().getColumn(7).setPreferredWidth(120);
    }

    private boolean agregarRenglon(RenglonFactura renglon) {
        boolean agregado = false;
        try {
            //busca entre los renglones al producto, aumenta la cantidad y recalcula el descuento        
            for (int i = 0; i < renglonesFactura.size(); i++) {
                RenglonFactura rf;
                if (renglonesFactura.get(i).getIdProductoItem() == renglon.getIdProductoItem()) {
                    rf = RestClient.getRestTemplate().getForObject("/facturas/renglon-venta?"
                            + "idProducto=" + renglonesFactura.get(i).getIdProductoItem()
                            + "&tipoDeComprobante=" + this.tipoDeComprobante.name()
                            + "&movimiento=" + Movimiento.VENTA
                            + "&cantidad=" + renglonesFactura.get(i).getCantidad().add(renglon.getCantidad())
                            + "&idCliente=" + this.cliente.getIdCliente(),
                            RenglonFactura.class);
                    renglonesFactura.set(i, rf);
                    agregado = true;
                }
            }
            //si no encuentra el producto entre los renglones, carga un nuevo renglon        
            if (agregado == false) {
                renglonesFactura.add(renglon);
            }
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
        return agregado;
    }

    private void cargarRenglonesAlTable(EstadoRenglon[] estadosDeLosRenglones) {
        modeloTablaResultados = new ModeloTabla();
        this.setColumnas();
        int i = 0;
        boolean corte;
        for (RenglonFactura renglon : renglonesFactura) {
            Object[] fila = new Object[8];
            corte = false;
            /*Dentro de este While, el case según el valor leido en el array de la enumeración,
             (modelo tabla) asigna el valor correspondiente al checkbox del renglon.*/
            while (corte == false) {
                switch (estadosDeLosRenglones[i]) {
                    case MARCADO: {
                        fila[0] = true;
                        corte = true;
                        break;
                    }
                    case DESMARCADO: {
                        fila[0] = false;
                        corte = true;
                        break;
                    }
                    /* En caso de que no sea un marcado o desmarcado, se considera que fue de un
                     renglon eliminado, entonces la estructura while continua iterando.*/
                    case ELIMINADO: {
                        i++;
                        break;
                    }
                    /* El caso por defecto, se da cuando el método es ejecutado
                     desde otras partes que no sea eliminar, ya que la colección
                     contendrá valores vacíos ''.*/
                    default: {
                        fila[0] = false;
                        corte = true;
                    }
                }
            }
            i++;
            fila[1] = renglon.getCodigoItem();
            fila[2] = renglon.getDescripcionItem();
            fila[3] = renglon.getMedidaItem();
            fila[4] = renglon.getCantidad();
            fila[5] = renglon.getPrecioUnitario();
            fila[6] = renglon.getBonificacionPorcentaje();
            fila[7] = renglon.getImporte();
            modeloTablaResultados.addRow(fila);
        }
        tbl_Resultado.setModel(modeloTablaResultados);
    }

    private void buscarProductoConVentanaAuxiliar() {
        if (cantidadMaximaRenglones > renglonesFactura.size()) {
            Movimiento movimiento = this.tipoDeComprobante.equals(TipoDeComprobante.PEDIDO) ? Movimiento.PEDIDO : Movimiento.VENTA;
            // revisar esto, es necesario para el movimiento como String y a su vez el movimiento?
            BuscarProductosGUI buscarProductosGUI = new BuscarProductosGUI(renglonesFactura, this.tipoDeComprobante, this.cliente);
            buscarProductosGUI.setModal(true);
            buscarProductosGUI.setLocationRelativeTo(this);
            buscarProductosGUI.setVisible(true);
            if (buscarProductosGUI.debeCargarRenglon()) {
                boolean renglonCargado = false;
                for (RenglonFactura renglon : renglonesFactura) {
                    if (renglon.getIdProductoItem() == buscarProductosGUI.getRenglonFactura().getIdProductoItem()) {
                        renglonCargado = true;
                    }
                }
                this.agregarRenglon(buscarProductosGUI.getRenglonFactura());
                /*Si la tabla no contiene renglones, despues de agregar el renglon
                 a la coleccion, carga el arreglo con los estados con un solo elemento, 
                 cuyo valor es "Desmarcado" para evitar un nulo.*/
                EstadoRenglon[] estadosRenglones = new EstadoRenglon[renglonesFactura.size()];
                if (tbl_Resultado.getRowCount() == 0) {
                    estadosRenglones[0] = EstadoRenglon.DESMARCADO;
                } else {
                    this.cargarEstadoDeLosChkEnTabla(tbl_Resultado, estadosRenglones);
                    //Se ejecuta o no segun si el renglon ya existe. Si ya existe, no se ejecuta
                    if (!renglonCargado) {
                        estadosRenglones[tbl_Resultado.getRowCount()] = EstadoRenglon.DESMARCADO;
                    }
                }
                this.cargarRenglonesAlTable(estadosRenglones);
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
                    + "codigo=" + txt_CodigoProducto.getText().trim(), Producto.class);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_producto_no_encontrado"), "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                RenglonFactura renglon = RestClient.getRestTemplate().getForObject("/facturas/renglon-venta?"
                        + "idProducto=" + producto.getIdProducto()
                        + "&tipoDeComprobante=" + this.tipoDeComprobante.name()
                        + "&movimiento=" + Movimiento.VENTA
                        + "&cantidad=1" 
                        + "&idCliente=" + this.cliente.getIdCliente(),
                        RenglonFactura.class);
                boolean esValido = true;
                Map<Long, BigDecimal> faltantes;
                if (cmb_TipoComprobante.getSelectedItem() != TipoDeComprobante.PEDIDO) {
                    faltantes = this.getProductosSinStockDisponible(Arrays.asList(renglon));
                    if (!faltantes.isEmpty()) {
                        esValido = false;
                        JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_producto_sin_stock_suficiente"), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                if (esValido) {
                    boolean renglonCargado = this.agregarRenglon(renglon);
                    EstadoRenglon[] estadosRenglones = new EstadoRenglon[renglonesFactura.size()];
                    if (tbl_Resultado.getRowCount() == 0) {
                        estadosRenglones[0] = EstadoRenglon.DESMARCADO;
                    } else {
                        this.cargarEstadoDeLosChkEnTabla(tbl_Resultado, estadosRenglones);
                        if (!renglonCargado) {
                            estadosRenglones[tbl_Resultado.getRowCount()] = EstadoRenglon.DESMARCADO;
                        }
                    }
                    this.cargarRenglonesAlTable(estadosRenglones);
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
        BigDecimal total;
        this.validarComponentesDeResultados();
        BigDecimal[] cantidades = new BigDecimal[renglonesFactura.size()];
        BigDecimal[] ivaPorcentajeRenglones = new BigDecimal[renglonesFactura.size()];
        BigDecimal[] ivaNetoRenglones = new BigDecimal[renglonesFactura.size()];
        int indice = 0;
        for (RenglonFactura renglon : renglonesFactura) {
            subTotal = subTotal.add(renglon.getImporte());
            cantidades[indice] = renglon.getCantidad();
            ivaPorcentajeRenglones[indice] = renglon.getIvaPorcentaje();
            ivaNetoRenglones[indice] = renglon.getIvaNeto();
            indice++;
        }
        txt_Subtotal.setValue(subTotal);
        descuentoPorcentaje = new BigDecimal(txt_Descuento_porcentaje.getValue().toString());
        descuentoNeto = subTotal.multiply(descuentoPorcentaje).divide(CIEN, 15, RoundingMode.HALF_UP);
        txt_Descuento_neto.setValue(descuentoNeto);
        recargoPorcentaje = new BigDecimal(txt_Recargo_porcentaje.getValue().toString());
        recargoNeto = subTotal.multiply(recargoPorcentaje).divide(CIEN, 15, RoundingMode.HALF_UP);
        txt_Recargo_neto.setValue(recargoNeto);
        iva_105_netoFactura = BigDecimal.ZERO;
        iva_21_netoFactura = BigDecimal.ZERO;
        indice = cantidades.length;
        if (tipoDeComprobante == TipoDeComprobante.FACTURA_B || tipoDeComprobante == TipoDeComprobante.FACTURA_A || tipoDeComprobante == TipoDeComprobante.PRESUPUESTO) {
            for (int i = 0; i < indice; i++) {
                if (ivaPorcentajeRenglones[i].compareTo(IVA_105) == 0) {
                    iva_105_netoFactura = iva_105_netoFactura.add(cantidades[i].multiply(ivaNetoRenglones[i]
                    .subtract(ivaNetoRenglones[i].multiply(descuentoPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP)))
                    .add(ivaNetoRenglones[i].multiply(recargoPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP)))));
                } else if (ivaPorcentajeRenglones[i].compareTo(IVA_21) == 0) {
                    iva_21_netoFactura = iva_21_netoFactura.add(cantidades[i].multiply(ivaNetoRenglones[i]
                    .subtract(ivaNetoRenglones[i].multiply(descuentoPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP)))
                    .add(ivaNetoRenglones[i].multiply(recargoPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP)))));
                }
            }
        } else {
            for (int i = 0; i < indice; i++) {
                if (ivaPorcentajeRenglones[i].compareTo(IVA_105) == 0) {
                    iva_105_netoFactura = iva_105_netoFactura.add(cantidades[i].multiply(ivaNetoRenglones[i]));
                } else if (ivaPorcentajeRenglones[i].compareTo(IVA_21) == 0) {
                    iva_21_netoFactura = iva_21_netoFactura.add(cantidades[i].multiply(ivaNetoRenglones[i]));
                }
            }
        }
        if (tipoDeComprobante == TipoDeComprobante.FACTURA_B || tipoDeComprobante == TipoDeComprobante.PRESUPUESTO) {
            txt_IVA105_neto.setValue(0);
            txt_IVA21_neto.setValue(0);
        } else {
            txt_IVA105_neto.setValue(iva_105_netoFactura);
            txt_IVA21_neto.setValue(iva_21_netoFactura);
        }
        subTotalBruto = subTotal.add(recargoNeto).subtract(descuentoNeto);
        if (tipoDeComprobante == TipoDeComprobante.FACTURA_B || tipoDeComprobante == TipoDeComprobante.PRESUPUESTO) {
            subTotalBruto = subTotalBruto.subtract(iva_105_netoFactura.add(iva_21_netoFactura));
        }
        total = subTotalBruto.add(iva_105_netoFactura).add(iva_21_netoFactura);
        txt_Total.setValue(total);
        if (tipoDeComprobante == TipoDeComprobante.FACTURA_B || tipoDeComprobante == TipoDeComprobante.PRESUPUESTO) {
            txt_SubTotalBruto.setValue(total);
        } else {
            txt_SubTotalBruto.setValue(subTotalBruto);
        }
    }

    private void cargarTiposDeComprobantesDisponibles() {
        cmb_TipoComprobante.removeAllItems();
        TipoDeComprobante[] tiposDeComprobante = new TipoDeComprobante[0];
        if (cliente != null) {
            if (rolesDeUsuario.contains(Rol.ADMINISTRADOR)
                    || rolesDeUsuario.contains(Rol.ENCARGADO)
                    || rolesDeUsuario.contains(Rol.VENDEDOR)) {
                try {
                    cmb_TipoComprobante.removeAllItems();
                    tiposDeComprobante = RestClient.getRestTemplate()
                            .getForObject("/facturas/venta/tipos/sucursales/" + SucursalActiva.getInstance().getSucursal().getIdSucursal()
                                    + "/clientes/" + cliente.getIdCliente(), TipoDeComprobante[].class);
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                for (int i = 0; tiposDeComprobante.length > i; i++) {
                    cmb_TipoComprobante.addItem(tiposDeComprobante[i]);                    
                }
            }
        }
    }

    private void recargarRenglones() {
        try {            
            List<RenglonFactura> resguardoRenglones = renglonesFactura;
            renglonesFactura = new ArrayList<>();
            resguardoRenglones.stream().map(renglonFactura -> {
                Producto producto = RestClient.getRestTemplate()
                        .getForObject("/productos/" + renglonFactura.getIdProductoItem(), Producto.class);
                RenglonFactura renglon = RestClient.getRestTemplate().getForObject("/facturas/renglon-venta?"
                        + "idProducto=" + producto.getIdProducto()
                        + "&tipoDeComprobante=" + this.tipoDeComprobante.name()
                        + "&movimiento=" + Movimiento.VENTA
                        + "&cantidad=" + renglonFactura.getCantidad()
                        + "&idCliente=" + this.cliente.getIdCliente(),
                        RenglonFactura.class);
                return renglon;
            }).forEachOrdered(renglon -> this.agregarRenglon(renglon));
            EstadoRenglon[] estadosRenglones = new EstadoRenglon[renglonesFactura.size()];
            if (!renglonesFactura.isEmpty()) {
                if (tbl_Resultado.getRowCount() == 0) {
                    estadosRenglones[0] = EstadoRenglon.DESMARCADO;
                } else {
                    this.cargarEstadoDeLosChkEnTabla(tbl_Resultado, estadosRenglones);
                    if (tbl_Resultado.getRowCount() > renglonesFactura.size()) {
                        estadosRenglones[tbl_Resultado.getRowCount()] = EstadoRenglon.DESMARCADO;
                    }
                }
            }
            this.cargarRenglonesAlTable(estadosRenglones);
            this.calcularResultados();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Map<Long, BigDecimal> getProductosSinStockDisponible(List<RenglonFactura> renglonesFactura) {
        long[] idsProductos = new long[renglonesFactura.size()];
        BigDecimal[] cantidades = new BigDecimal[renglonesFactura.size()];
        for (int i = 0; i < idsProductos.length; i++) {
            idsProductos[i] = renglonesFactura.get(i).getIdProductoItem();
            cantidades[i] = renglonesFactura.get(i).getCantidad();
        }
        ProductosParaVerificarStock productosParaVerificarStock = ProductosParaVerificarStock.builder()
                .idSucursal(SucursalActiva.getInstance().getSucursal().getIdSucursal())
                .cantidad(cantidades)
                .idProducto(idsProductos)
                .build();
        HttpEntity<ProductosParaVerificarStock> requestEntity = new HttpEntity<>(productosParaVerificarStock);
        return RestClient.getRestTemplate()
                .exchange("/productos/disponibilidad-stock", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Map<Long, BigDecimal>>() {
                }).getBody();
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelGeneral = new javax.swing.JPanel();
        panelCliente = new javax.swing.JPanel();
        lblNombreCliente = new javax.swing.JLabel();
        lblUbicacionCliente = new javax.swing.JLabel();
        lbl_IDFiscalCliente = new javax.swing.JLabel();
        lbl_CondicionIVACliente = new javax.swing.JLabel();
        txt_CondicionIVACliente = new javax.swing.JTextField();
        txtUbicacionCliente = new javax.swing.JTextField();
        txtNombreCliente = new javax.swing.JTextField();
        lblBonificacion = new javax.swing.JLabel();
        txtIdFiscalCliente = new javax.swing.JTextField();
        txtBonificacion = new javax.swing.JTextField();
        panelRenglones = new javax.swing.JPanel();
        sp_Resultado = new javax.swing.JScrollPane();
        tbl_Resultado = new javax.swing.JTable();
        btn_BuscarProductos = new javax.swing.JButton();
        btn_QuitarProducto = new javax.swing.JButton();
        txt_CodigoProducto = new javax.swing.JTextField();
        btn_BuscarPorCodigoProducto = new javax.swing.JButton();
        tbtn_marcarDesmarcar = new javax.swing.JToggleButton();
        panelObservaciones = new javax.swing.JPanel();
        lbl_Observaciones = new javax.swing.JLabel();
        btn_AddComment = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_Observaciones = new javax.swing.JTextArea();
        panelResultados = new javax.swing.JPanel();
        lbl_SubTotal = new javax.swing.JLabel();
        txt_Subtotal = new javax.swing.JFormattedTextField();
        lbl_IVA21 = new javax.swing.JLabel();
        txt_IVA21_neto = new javax.swing.JFormattedTextField();
        lbl_Total = new javax.swing.JLabel();
        txt_Total = new javax.swing.JFormattedTextField();
        txt_Descuento_porcentaje = new javax.swing.JFormattedTextField();
        txt_Descuento_neto = new javax.swing.JFormattedTextField();
        lbl_DescuentoRecargo = new javax.swing.JLabel();
        txt_SubTotalBruto = new javax.swing.JFormattedTextField();
        lbl_SubTotalBruto = new javax.swing.JLabel();
        txt_IVA105_neto = new javax.swing.JFormattedTextField();
        lbl_IVA105 = new javax.swing.JLabel();
        lbl_105 = new javax.swing.JLabel();
        lbl_21 = new javax.swing.JLabel();
        lbl_recargoPorcentaje = new javax.swing.JLabel();
        txt_Recargo_neto = new javax.swing.JFormattedTextField();
        txt_Recargo_porcentaje = new javax.swing.JFormattedTextField();
        btn_Continuar = new javax.swing.JButton();
        panelEncabezado = new javax.swing.JPanel();
        lbl_fechaDeVencimiento = new javax.swing.JLabel();
        dc_fechaVencimiento = new com.toedter.calendar.JDateChooser();
        lbl_TipoDeComprobante = new javax.swing.JLabel();
        cmb_TipoComprobante = new javax.swing.JComboBox();
        btn_NuevoCliente = new javax.swing.JButton();
        btn_BuscarCliente = new javax.swing.JButton();
        lblSeparadorIzquierdo = new javax.swing.JLabel();
        lblSeparadorDerecho = new javax.swing.JLabel();
        btnModificarCliente = new javax.swing.JButton();

        setResizable(true);
        setTitle("Nueva Factura de Venta");
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

        panelGeneral.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblNombreCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNombreCliente.setText("Nombre:");

        lblUbicacionCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblUbicacionCliente.setText("Ubicación:");

        lbl_IDFiscalCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_IDFiscalCliente.setText("CUIT o DNI:");

        lbl_CondicionIVACliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_CondicionIVACliente.setText("Categoria IVA:");

        txt_CondicionIVACliente.setEditable(false);
        txt_CondicionIVACliente.setFocusable(false);

        txtUbicacionCliente.setEditable(false);
        txtUbicacionCliente.setFocusable(false);

        txtNombreCliente.setEditable(false);
        txtNombreCliente.setFocusable(false);

        lblBonificacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBonificacion.setText("Bonificación:");

        txtIdFiscalCliente.setEditable(false);
        txtIdFiscalCliente.setFocusable(false);

        txtBonificacion.setEditable(false);

        javax.swing.GroupLayout panelClienteLayout = new javax.swing.GroupLayout(panelCliente);
        panelCliente.setLayout(panelClienteLayout);
        panelClienteLayout.setHorizontalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUbicacionCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_CondicionIVACliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelClienteLayout.createSequentialGroup()
                        .addComponent(txt_CondicionIVACliente, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_IDFiscalCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdFiscalCliente))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelClienteLayout.createSequentialGroup()
                        .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 635, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblBonificacion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBonificacion))
                    .addComponent(txtUbicacionCliente, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(0, 0, 0))
        );

        panelClienteLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtNombreCliente, txt_CondicionIVACliente});

        panelClienteLayout.setVerticalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblBonificacion)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombreCliente)
                    .addComponent(txtBonificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUbicacionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUbicacionCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtIdFiscalCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_CondicionIVACliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_IDFiscalCliente)
                    .addComponent(lbl_CondicionIVACliente)))
        );

        panelClienteLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtIdFiscalCliente, txtNombreCliente, txtUbicacionCliente, txt_CondicionIVACliente});

        panelClienteLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblBonificacion, lbl_IDFiscalCliente});

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

        tbtn_marcarDesmarcar.setFocusable(false);
        tbtn_marcarDesmarcar.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tbtn_marcarDesmarcarStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelRenglonesLayout = new javax.swing.GroupLayout(panelRenglones);
        panelRenglones.setLayout(panelRenglonesLayout);
        panelRenglonesLayout.setHorizontalGroup(
            panelRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultado)
            .addGroup(panelRenglonesLayout.createSequentialGroup()
                .addComponent(tbtn_marcarDesmarcar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                    .addComponent(tbtn_marcarDesmarcar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_QuitarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_BuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultado, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))
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
                    .addGroup(panelObservacionesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbl_Observaciones))
                    .addGroup(panelObservacionesLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(btn_AddComment)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbl_SubTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_SubTotal.setText("SubTotal");

        txt_Subtotal.setEditable(false);
        txt_Subtotal.setForeground(new java.awt.Color(29, 156, 37));
        txt_Subtotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Subtotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Subtotal.setText("0");
        txt_Subtotal.setFocusable(false);
        txt_Subtotal.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        lbl_IVA21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_IVA21.setText("I.V.A.");

        txt_IVA21_neto.setEditable(false);
        txt_IVA21_neto.setForeground(new java.awt.Color(29, 156, 37));
        txt_IVA21_neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_IVA21_neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_IVA21_neto.setText("0");
        txt_IVA21_neto.setFocusable(false);
        txt_IVA21_neto.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        lbl_Total.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_Total.setText("TOTAL");

        txt_Total.setEditable(false);
        txt_Total.setForeground(new java.awt.Color(29, 156, 37));
        txt_Total.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Total.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Total.setText("0");
        txt_Total.setFocusable(false);
        txt_Total.setFont(new java.awt.Font("DejaVu Sans", 1, 36)); // NOI18N

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
        lbl_DescuentoRecargo.setText("Descuento (%)");

        txt_SubTotalBruto.setEditable(false);
        txt_SubTotalBruto.setForeground(new java.awt.Color(29, 156, 37));
        txt_SubTotalBruto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_SubTotalBruto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_SubTotalBruto.setText("0");
        txt_SubTotalBruto.setFocusable(false);
        txt_SubTotalBruto.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        lbl_SubTotalBruto.setText("SubTotal Bruto");

        txt_IVA105_neto.setEditable(false);
        txt_IVA105_neto.setForeground(new java.awt.Color(29, 156, 37));
        txt_IVA105_neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_IVA105_neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_IVA105_neto.setText("0");
        txt_IVA105_neto.setFocusable(false);
        txt_IVA105_neto.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        lbl_IVA105.setText("I.V.A.");

        lbl_105.setText("10.5 %");

        lbl_21.setText("21 %");

        lbl_recargoPorcentaje.setText("Recargo (%)");

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
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_Subtotal)
                    .addComponent(lbl_SubTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_Descuento_porcentaje, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(txt_Descuento_neto)
                    .addComponent(lbl_DescuentoRecargo, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_Recargo_neto)
                    .addComponent(txt_Recargo_porcentaje)
                    .addComponent(lbl_recargoPorcentaje, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_SubTotalBruto)
                    .addComponent(lbl_SubTotalBruto, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_IVA105_neto)
                    .addComponent(lbl_105, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(lbl_IVA105, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_IVA21_neto)
                    .addComponent(lbl_21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_IVA21, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_Total)
                    .addComponent(lbl_Total, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_SubTotal)
                    .addComponent(lbl_DescuentoRecargo)
                    .addComponent(lbl_recargoPorcentaje)
                    .addComponent(lbl_SubTotalBruto)
                    .addComponent(lbl_IVA105)
                    .addComponent(lbl_IVA21)
                    .addComponent(lbl_Total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_Descuento_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Recargo_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_105, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_21, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_Subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Descuento_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Recargo_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_SubTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_IVA105_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_IVA21_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txt_Total, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbl_105, lbl_21, txt_Descuento_neto, txt_Descuento_porcentaje, txt_IVA105_neto, txt_IVA21_neto, txt_Recargo_neto, txt_Recargo_porcentaje, txt_SubTotalBruto, txt_Subtotal});

        btn_Continuar.setForeground(java.awt.Color.blue);
        btn_Continuar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_FlechaGO.png"))); // NOI18N
        btn_Continuar.setText("Continuar (F9)");
        btn_Continuar.setFocusable(false);
        btn_Continuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ContinuarActionPerformed(evt);
            }
        });

        lbl_fechaDeVencimiento.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_fechaDeVencimiento.setText("Fecha de Vencimiento:");

        dc_fechaVencimiento.setFocusable(false);

        lbl_TipoDeComprobante.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TipoDeComprobante.setText("Tipo de Comprobante:");

        cmb_TipoComprobante.setNextFocusableComponent(txt_CodigoProducto);
        cmb_TipoComprobante.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_TipoComprobanteItemStateChanged(evt);
            }
        });
        cmb_TipoComprobante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_TipoComprobanteActionPerformed(evt);
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
                .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEncabezadoLayout.createSequentialGroup()
                        .addComponent(btn_NuevoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_BuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnModificarCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_fechaDeVencimiento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_fechaVencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelEncabezadoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblSeparadorIzquierdo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_TipoDeComprobante)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmb_TipoComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSeparadorDerecho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        panelEncabezadoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnModificarCliente, btn_BuscarCliente, btn_NuevoCliente});

        panelEncabezadoLayout.setVerticalGroup(
            panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, panelEncabezadoLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(lbl_fechaDeVencimiento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(6, 6, 6))
            .addGroup(panelEncabezadoLayout.createSequentialGroup()
                .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEncabezadoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbl_TipoDeComprobante)
                            .addComponent(cmb_TipoComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(btn_BuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnModificarCliente)
                            .addComponent(btn_NuevoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelEncabezadoLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSeparadorDerecho, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSeparadorIzquierdo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.CENTER, panelEncabezadoLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(dc_fechaVencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelEncabezadoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnModificarCliente, btn_BuscarCliente, btn_NuevoCliente});

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelRenglones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelEncabezado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addComponent(panelObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_Continuar)))
                .addContainerGap())
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
                    .addComponent(panelObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Continuar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

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
            this.cargarCliente(buscarClientesGUI.getClienteSeleccionado());
            this.cargarTiposDeComprobantesDisponibles();
            cmb_TipoComprobante.setSelectedIndex(0);
        }
    }//GEN-LAST:event_btn_BuscarClienteActionPerformed

    private void btn_NuevoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoClienteActionPerformed
        DetalleClienteGUI gui_DetalleCliente = new DetalleClienteGUI();
        gui_DetalleCliente.setModal(true);
        gui_DetalleCliente.setLocationRelativeTo(this);
        gui_DetalleCliente.setVisible(true);
        if (gui_DetalleCliente.getClienteDadoDeAlta() != null) {
            this.cargarCliente(gui_DetalleCliente.getClienteDadoDeAlta());
            this.cargarTiposDeComprobantesDisponibles();
            btnModificarCliente.setEnabled(true);
        }
    }//GEN-LAST:event_btn_NuevoClienteActionPerformed

    private void cmb_TipoComprobanteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_TipoComprobanteItemStateChanged
        //para evitar que pase null cuando esta recargando el comboBox
        if (cmb_TipoComprobante.getSelectedItem() != null) {            
            this.tipoDeComprobante = (TipoDeComprobante) cmb_TipoComprobante.getSelectedItem();
            this.recargarRenglones();
        }
    }//GEN-LAST:event_cmb_TipoComprobanteItemStateChanged

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
            if (renglonesFactura.isEmpty()) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_factura_sin_renglones"), "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (new BigDecimal(txt_Descuento_porcentaje.getValue().toString()).compareTo(CIEN) > 0) {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_factura_descuento_mayor_cien"), "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    this.calcularResultados();
                    try {
                        cliente = RestClient.getRestTemplate().getForObject("/clientes/" + this.cliente.getIdCliente(), Cliente.class);
                        Map<Long, BigDecimal> faltantes;
                        faltantes = this.getProductosSinStockDisponible(renglonesFactura);
                        if (faltantes.isEmpty()) {
                            CerrarVentaGUI cerrarVentaGUI = new CerrarVentaGUI(this.construirFactura(), this.pedido, this.modeloTablaResultados);
                            cerrarVentaGUI.setLocationRelativeTo(this);
                            cerrarVentaGUI.setVisible(true);
                            if (cerrarVentaGUI.isExito()) {
                                this.dispose();
                            }
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
        } else {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_seleccionar_cliente"),
                    "Aviso", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_ContinuarActionPerformed

    private void btn_QuitarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_QuitarProductoActionPerformed
        int[] indicesParaEliminar = Utilidades.getSelectedRowsModelIndices(tbl_Resultado);
        List<RenglonFactura> renglonesParaBorrar = new ArrayList<>();
        for (int i = 0; i < indicesParaEliminar.length; i++) {
            renglonesParaBorrar.add(renglonesFactura.get(indicesParaEliminar[i]));
        }
        EstadoRenglon[] estadoDeRenglones = new EstadoRenglon[renglonesFactura.size()];
        for (int i = 0; i < tbl_Resultado.getRowCount(); i++) {
            if (((boolean) tbl_Resultado.getValueAt(i, 0)) == true) {
                estadoDeRenglones[i] = EstadoRenglon.MARCADO;
            } else {
                estadoDeRenglones[i] = EstadoRenglon.DESMARCADO;
            }
        }
        for (int i = 0; i < indicesParaEliminar.length; i++) {
            estadoDeRenglones[indicesParaEliminar[i]] = EstadoRenglon.ELIMINADO;
        }
        renglonesParaBorrar.forEach(renglon -> renglonesFactura.remove(renglon));
        this.cargarRenglonesAlTable(estadoDeRenglones);
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

    private void cmb_TipoComprobanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_TipoComprobanteActionPerformed
        for (int i = 0; i < tbl_Resultado.getRowCount(); i++) {
            tbl_Resultado.setValueAt((boolean) tbl_Resultado.getValueAt(i, 0), i, 0);
        }
    }//GEN-LAST:event_cmb_TipoComprobanteActionPerformed

    private void tbl_ResultadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ResultadoMouseClicked
        int fila = tbl_Resultado.getSelectedRow();
        int columna = tbl_Resultado.getSelectedColumn();
        if (columna == 0) {
            tbl_Resultado.setValueAt(!(boolean) tbl_Resultado.getValueAt(fila, columna), fila, columna);
        }
    }//GEN-LAST:event_tbl_ResultadoMouseClicked

    private void tbtn_marcarDesmarcarStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tbtn_marcarDesmarcarStateChanged
        int cantidadDeFilas = tbl_Resultado.getRowCount();
        if (this.tbtn_marcarDesmarcar.isSelected()) {
            ImageIcon iconoMarcado = new ImageIcon(getClass().getResource("/sic/icons/chkMarca_16x16.png"));
            this.tbtn_marcarDesmarcar.setIcon(iconoMarcado);
            for (int i = 0; i < cantidadDeFilas; i++) {
                tbl_Resultado.setValueAt(true, i, 0);
            }
        } else {
            ImageIcon iconoNoMarcado = new ImageIcon(getClass().getResource("/sic/icons/chkNoMarcado_16x16.png"));
            this.tbtn_marcarDesmarcar.setIcon(iconoNoMarcado);
            for (int i = 0; i < cantidadDeFilas; i++) {
                tbl_Resultado.setValueAt(false, i, 0);
            }
        }
    }//GEN-LAST:event_tbtn_marcarDesmarcarStateChanged

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
            cantidadMaximaRenglones = RestClient.getRestTemplate().getForObject("/configuraciones-sucursal/"
                    + SucursalActiva.getInstance().getSucursal().getIdSucursal()
                    + "/cantidad-renglones", Integer.class);
            if (rolesDeUsuario.contains(Rol.ADMINISTRADOR)
                    || rolesDeUsuario.contains(Rol.ENCARGADO)
                    || rolesDeUsuario.contains(Rol.VENDEDOR)) {
                if (this.existeClientePredeterminado()) {
                    CuentaCorrienteCliente cuentaCorrienteClientePredeterminado
                            = RestClient.getRestTemplate().getForObject("/cuentas-corriente/clientes/predeterminado", CuentaCorrienteCliente.class);
                    this.cargarCliente(cuentaCorrienteClientePredeterminado.getCliente());
                    this.btnModificarCliente.setEnabled(true);
                }
            }
            if (!this.existeFormaDePagoPredeterminada() || !this.existeTransportistaCargado()) {
                this.dispose();
            }
            this.cargarTiposDeComprobantesDisponibles();
            if (this.pedido != null && this.pedido.getIdPedido() != 0) {
                this.cargarPedidoParaFacturar();
                btn_NuevoCliente.setEnabled(false);
                btn_BuscarCliente.setEnabled(false);
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
                this.cargarCliente(RestClient.getRestTemplate().getForObject("/clientes/" + this.cliente.getIdCliente(), Cliente.class));
                this.recargarRenglones();
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
    private javax.swing.JComboBox cmb_TipoComprobante;
    private com.toedter.calendar.JDateChooser dc_fechaVencimiento;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBonificacion;
    private javax.swing.JLabel lblNombreCliente;
    private javax.swing.JLabel lblSeparadorDerecho;
    private javax.swing.JLabel lblSeparadorIzquierdo;
    private javax.swing.JLabel lblUbicacionCliente;
    private javax.swing.JLabel lbl_105;
    private javax.swing.JLabel lbl_21;
    private javax.swing.JLabel lbl_CondicionIVACliente;
    private javax.swing.JLabel lbl_DescuentoRecargo;
    private javax.swing.JLabel lbl_IDFiscalCliente;
    private javax.swing.JLabel lbl_IVA105;
    private javax.swing.JLabel lbl_IVA21;
    private javax.swing.JLabel lbl_Observaciones;
    private javax.swing.JLabel lbl_SubTotal;
    private javax.swing.JLabel lbl_SubTotalBruto;
    private javax.swing.JLabel lbl_TipoDeComprobante;
    private javax.swing.JLabel lbl_Total;
    private javax.swing.JLabel lbl_fechaDeVencimiento;
    private javax.swing.JLabel lbl_recargoPorcentaje;
    private javax.swing.JPanel panelCliente;
    private javax.swing.JPanel panelEncabezado;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelObservaciones;
    private javax.swing.JPanel panelRenglones;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane sp_Resultado;
    private javax.swing.JTable tbl_Resultado;
    private javax.swing.JToggleButton tbtn_marcarDesmarcar;
    private javax.swing.JTextField txtBonificacion;
    private javax.swing.JTextField txtIdFiscalCliente;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JTextField txtUbicacionCliente;
    private javax.swing.JTextField txt_CodigoProducto;
    private javax.swing.JTextField txt_CondicionIVACliente;
    private javax.swing.JFormattedTextField txt_Descuento_neto;
    private javax.swing.JFormattedTextField txt_Descuento_porcentaje;
    private javax.swing.JFormattedTextField txt_IVA105_neto;
    private javax.swing.JFormattedTextField txt_IVA21_neto;
    private javax.swing.JTextArea txt_Observaciones;
    private javax.swing.JFormattedTextField txt_Recargo_neto;
    private javax.swing.JFormattedTextField txt_Recargo_porcentaje;
    private javax.swing.JFormattedTextField txt_SubTotalBruto;
    private javax.swing.JFormattedTextField txt_Subtotal;
    private javax.swing.JFormattedTextField txt_Total;
    // End of variables declaration//GEN-END:variables
}
