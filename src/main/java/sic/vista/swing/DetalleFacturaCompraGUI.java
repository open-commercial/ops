package sic.vista.swing;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.EmpresaActiva;
import sic.modelo.FacturaCompra;
import sic.modelo.Movimiento;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.RenglonFactura;
import sic.modelo.TipoDeComprobante;
import sic.modelo.Transportista;
import sic.modelo.UsuarioActivo;
import sic.util.DecimalesRenderer;
import sic.util.FormatosFechaHora;
import sic.util.FormatterFechaHora;

public class DetalleFacturaCompraGUI extends JInternalFrame {

    private ModeloTabla modeloTablaRenglones = new ModeloTabla();
    private List<RenglonFactura> renglones = new ArrayList<>();
    private final FacturaCompra facturaParaMostrar;
    private TipoDeComprobante tipoDeComprobante;
    private final boolean operacionAlta;
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final FormatterFechaHora formatter = new FormatterFechaHora(FormatosFechaHora.FORMATO_FECHA_HISPANO);
    private BigDecimal totalComprobante;    
    private BigDecimal iva105netoFactura;
    private BigDecimal iva21netoFactura;
    private BigDecimal subTotalBruto;
    private Proveedor proveedorSeleccionado;
    private final static BigDecimal IVA_21 = new BigDecimal("21");
    private final static BigDecimal IVA_105 = new BigDecimal("10.5");
    private final static BigDecimal CIEN = new BigDecimal("100");
    
    public DetalleFacturaCompraGUI() {
        this.initComponents();     
        facturaParaMostrar = new FacturaCompra();
        operacionAlta = true;
        this.prepararComponentes();
        this.agregarListeners();
    }

    public DetalleFacturaCompraGUI(FacturaCompra facturaCompra) {
        this.initComponents();              
        this.prepararComponentes();        
        operacionAlta = false;
        facturaParaMostrar = facturaCompra;
        txtProveedor.setText(facturaParaMostrar.getRazonSocialProveedor());
        btnBuscarProveedor.setEnabled(false);
        btn_NuevoProveedor.setEnabled(false);
        dc_FechaFactura.setEnabled(false);
        txt_SerieFactura.setEditable(false);
        txt_SerieFactura.setFocusable(false);
        txt_NumeroFactura.setEditable(false);
        txt_NumeroFactura.setFocusable(false);
        dc_FechaVencimiento.setEnabled(false);
        cmb_Transportista.setEnabled(false);
        cmb_TipoFactura.setEnabled(false);
        btn_NuevoTransportista.setEnabled(false);
        btn_BuscarProducto.setVisible(false);
        btn_NuevoProducto.setVisible(false);
        btn_QuitarDeLista.setVisible(false);
        btn_Guardar.setVisible(false);
        txta_Observaciones.setEditable(false);
        txt_Descuento_Porcentaje.setEditable(false);
        txt_Recargo_Porcentaje.setEditable(false);
        lbl_Proveedor.setForeground(Color.BLACK);
        lbl_TipoFactura.setForeground(Color.BLACK);
        lbl_Fecha.setForeground(Color.BLACK);
        lbl_Transporte.setForeground(Color.BLACK);
        lbl_TipoFactura.setText("Tipo de Factura:");
        lbl_Proveedor.setText("Proveedor:");
        lbl_Fecha.setText("Fecha Factura:");
        lbl_Transporte.setText("Transporte:");
    }

    private void prepararComponentes() {
        txt_SerieFactura.setValue(BigDecimal.ZERO);
        txt_NumeroFactura.setValue(BigDecimal.ZERO);
        txt_SubTotal.setValue(BigDecimal.ZERO);
        txt_Descuento_Porcentaje.setValue(BigDecimal.ZERO);
        txt_Descuento_Neto.setValue(BigDecimal.ZERO);
        txt_Recargo_Porcentaje.setValue(BigDecimal.ZERO);
        txt_Recargo_Neto.setValue(BigDecimal.ZERO);
        txt_SubTotal_Neto.setValue(BigDecimal.ZERO);
        txt_IVA_105.setValue(BigDecimal.ZERO);
        txt_IVA_21.setValue(BigDecimal.ZERO);
        txt_Total.setValue(BigDecimal.ZERO);
        dc_FechaFactura.setDate(new Date());
    }

    private void cargarRenglonFactura(BuscarProductosGUI gui_buscarProducto) {
        Producto productoSeleccionado = gui_buscarProducto.getProductoSeleccionado();
        if (productoSeleccionado != null) {
            if (this.existeProductoCargado(productoSeleccionado)) {
                JOptionPane.showMessageDialog(this,
                        "Ya esta cargado el producto \"" + gui_buscarProducto.getProductoSeleccionado().getDescripcion()
                        + "\" en los renglones de la factura.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (gui_buscarProducto.debeCargarRenglon()) {
                this.agregarRenglon(gui_buscarProducto.getRenglon());
            }
        }
    }

    private void agregarRenglon(RenglonFactura renglon) {
        Object[] lineaDeFactura = new Object[7];
        lineaDeFactura[0] = renglon.getCodigoItem();
        lineaDeFactura[1] = renglon.getDescripcionItem();
        lineaDeFactura[2] = renglon.getMedidaItem();
        lineaDeFactura[3] = renglon.getCantidad();
        lineaDeFactura[4] = renglon.getPrecioUnitario();
        lineaDeFactura[5] = renglon.getDescuentoPorcentaje();
        lineaDeFactura[6] = renglon.getImporte();
        modeloTablaRenglones.addRow(lineaDeFactura);
        renglones.add(renglon);
        if (operacionAlta) this.calcularResultados();
        //para que baje solo el scroll vertical
        Point p = new Point(0, tbl_Renglones.getHeight());
        sp_Renglones.getViewport().setViewPosition(p);
    }

    private void quitarRenglonFactura() {
        if (tbl_Renglones.getSelectedRow() != -1) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar el renglon de factura seleccionado?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                int fila = tbl_Renglones.getSelectedRow();
                modeloTablaRenglones.removeRow(fila);
                renglones.remove(fila);
                this.calcularResultados();
            }
        }
    }

    private void cargarTransportistas() {        
        cmb_Transportista.removeAllItems();
        try {
            List<Transportista> transportistas = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/transportistas/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                    Transportista[].class)));
            transportistas.stream().forEach(t -> cmb_Transportista.addItem(t));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean guardarFactura() {
        FacturaCompra facturaCompra = new FacturaCompra();
        facturaCompra.setFecha(dc_FechaFactura.getDate());
        facturaCompra.setTipoComprobante(tipoDeComprobante);
        facturaCompra.setNumSerie(Long.parseLong(txt_SerieFactura.getValue().toString()));
        facturaCompra.setNumFactura(Long.parseLong(txt_NumeroFactura.getValue().toString()));
        facturaCompra.setFechaVencimiento(dc_FechaVencimiento.getDate());
        facturaCompra.setRenglones(new ArrayList<>(renglones));
        facturaCompra.setSubTotal(new BigDecimal(txt_SubTotal.getValue().toString()));
        facturaCompra.setRecargoPorcentaje(BigDecimal.ZERO);
        facturaCompra.setRecargoNeto(BigDecimal.ZERO);
        facturaCompra.setDescuentoPorcentaje(new BigDecimal(txt_Descuento_Porcentaje.getValue().toString()));
        facturaCompra.setDescuentoNeto(new BigDecimal(txt_Descuento_Neto.getValue().toString()));
        facturaCompra.setRecargoPorcentaje(new BigDecimal(txt_Recargo_Porcentaje.getValue().toString()));
        facturaCompra.setRecargoNeto(new BigDecimal(txt_Recargo_Neto.getValue().toString()));
        facturaCompra.setSubTotalBruto(subTotalBruto);
        facturaCompra.setIva105Neto(iva105netoFactura);
        facturaCompra.setIva21Neto(iva21netoFactura);
        facturaCompra.setTotal(totalComprobante);
        facturaCompra.setObservaciones(txta_Observaciones.getText().trim());
        facturaCompra.setEliminada(false);
        try {
            RestClient.getRestTemplate().postForObject("/facturas/compra"
                    + "?idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                    + "&idProveedor=" + proveedorSeleccionado.getId_Proveedor()
                    + "&idUsuario=" + UsuarioActivo.getInstance().getUsuario().getId_Usuario()
                    + "&idTransportista=" + ((Transportista) cmb_Transportista.getSelectedItem()).getId_Transportista(),
                    facturaCompra, FacturaCompra[].class);
            return true;
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void limpiarYRecargarComponentes() {
        renglones = new ArrayList<>();
        modeloTablaRenglones = new ModeloTabla();
        this.setColumnas();
        dc_FechaFactura.setDate(new Date());
        dc_FechaVencimiento.setDate(null);
        txta_Observaciones.setText("");
        txt_SerieFactura.setValue(0);
        txt_NumeroFactura.setValue(0);
        txt_SubTotal.setValue(BigDecimal.ZERO);
        txt_Descuento_Porcentaje.setValue(BigDecimal.ZERO);
        txt_Descuento_Neto.setValue(BigDecimal.ZERO);
        txt_Recargo_Porcentaje.setValue(BigDecimal.ZERO);
        txt_Recargo_Neto.setValue(BigDecimal.ZERO);
        txt_SubTotal_Neto.setValue(BigDecimal.ZERO);
        txt_IVA_105.setValue(BigDecimal.ZERO);
        txt_IVA_21.setValue(BigDecimal.ZERO);
        txt_Total.setValue(BigDecimal.ZERO);
    }

    private void validarComponentesDeResultados() {
        try {
            txt_SubTotal.commitEdit();
            txt_Descuento_Porcentaje.commitEdit();
            txt_Descuento_Neto.commitEdit();
            txt_Recargo_Porcentaje.commitEdit();
            txt_Recargo_Neto.commitEdit();
            txt_SubTotal_Neto.commitEdit();
            txt_IVA_105.commitEdit();
            txt_IVA_21.commitEdit();
            txt_Total.commitEdit();

        } catch (ParseException ex) {
            String msjError = "Se produjo un error analizando los campos.";
            LOGGER.error(msjError + " - " + ex.getMessage());
        }
    }

    private void calcularResultados() {
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal descuentoPorcentaje;
        BigDecimal descuentoNeto;
        BigDecimal recargoPorcentaje;
        BigDecimal recargoNeto;
        this.validarComponentesDeResultados();
        BigDecimal[] cantidades = new BigDecimal[renglones.size()];
        BigDecimal[] ivaPorcentajeRenglones = new BigDecimal[renglones.size()];
        BigDecimal[] ivaNetoRenglones = new BigDecimal[renglones.size()];
        int indice = 0;
        for (RenglonFactura renglon : renglones) {
            subTotal = subTotal.add(renglon.getImporte());
            cantidades[indice] = renglon.getCantidad();
            ivaPorcentajeRenglones[indice] = renglon.getIvaPorcentaje();
            ivaNetoRenglones[indice] = renglon.getIvaNeto();
            indice++;
        }
        txt_SubTotal.setValue(subTotal);
        descuentoPorcentaje = new BigDecimal(txt_Descuento_Porcentaje.getValue().toString());
        descuentoNeto = subTotal.multiply(descuentoPorcentaje).divide(CIEN, 15, RoundingMode.HALF_UP);
        txt_Descuento_Neto.setValue(descuentoNeto);
        recargoPorcentaje = new BigDecimal(txt_Recargo_Porcentaje.getValue().toString());
        recargoNeto = subTotal.multiply(recargoPorcentaje).divide(CIEN, 15, RoundingMode.HALF_UP);
        txt_Recargo_Neto.setValue(recargoNeto);
        iva105netoFactura = BigDecimal.ZERO;
        iva21netoFactura = BigDecimal.ZERO;
        indice = cantidades.length;
        if (tipoDeComprobante == TipoDeComprobante.FACTURA_B || tipoDeComprobante == TipoDeComprobante.FACTURA_A || tipoDeComprobante == TipoDeComprobante.PRESUPUESTO) {
            for (int i = 0; i < indice; i++) {
                if (ivaPorcentajeRenglones[i].compareTo(IVA_105) == 0) {
                    iva105netoFactura = iva105netoFactura.add(cantidades[i].multiply(ivaNetoRenglones[i]
                    .subtract(ivaNetoRenglones[i].multiply(descuentoPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP)))
                    .add(ivaNetoRenglones[i].multiply(recargoPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP)))));
                } else if (ivaPorcentajeRenglones[i].compareTo(IVA_21) == 0) {
                    iva21netoFactura = iva21netoFactura.add(cantidades[i].multiply(ivaNetoRenglones[i]
                    .subtract(ivaNetoRenglones[i].multiply(descuentoPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP)))
                    .add(ivaNetoRenglones[i].multiply(recargoPorcentaje.divide(CIEN, 15, RoundingMode.HALF_UP)))));
                }
            }
        } else {
            for (int i = 0; i < indice; i++) {
                if (ivaPorcentajeRenglones[i].compareTo(IVA_105) == 0) {
                    iva105netoFactura = iva105netoFactura.add(cantidades[i].multiply(ivaNetoRenglones[i]));
                } else if (ivaPorcentajeRenglones[i].compareTo(IVA_21) == 0) {
                    iva21netoFactura = iva21netoFactura.add(cantidades[i].multiply(ivaNetoRenglones[i]));
                }
            }
        }
        if (tipoDeComprobante == TipoDeComprobante.FACTURA_B || tipoDeComprobante == TipoDeComprobante.PRESUPUESTO) {
            txt_IVA_105.setValue(0);
            txt_IVA_21.setValue(0);
        } else {
            txt_IVA_105.setValue(iva105netoFactura);
            txt_IVA_21.setValue(iva21netoFactura);
        }
        subTotalBruto = subTotal.add(recargoNeto).subtract(descuentoNeto);
        if (tipoDeComprobante == TipoDeComprobante.FACTURA_B || tipoDeComprobante == TipoDeComprobante.PRESUPUESTO) {
            subTotalBruto = subTotalBruto.subtract(iva105netoFactura.add(iva21netoFactura));
        }
        totalComprobante = subTotalBruto.add(iva105netoFactura).add(iva21netoFactura);
        if (tipoDeComprobante == TipoDeComprobante.FACTURA_B || tipoDeComprobante == TipoDeComprobante.PRESUPUESTO) {
            txt_SubTotal_Neto.setValue(totalComprobante);
        } else {
            txt_SubTotal_Neto.setValue(subTotalBruto);
        }
        txt_Total.setValue(totalComprobante);
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
        modeloTablaRenglones.setColumnIdentifiers(encabezados);
        tbl_Renglones.setModel(modeloTablaRenglones);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaRenglones.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = BigDecimal.class;
        tipos[4] = BigDecimal.class;
        tipos[5] = BigDecimal.class;
        tipos[6] = BigDecimal.class;
        modeloTablaRenglones.setClaseColumnas(tipos);
        tbl_Renglones.getTableHeader().setReorderingAllowed(false);
        tbl_Renglones.getTableHeader().setResizingAllowed(true);
        //render para los tipos de datos
        tbl_Renglones.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        //Tamanios de columnas
        tbl_Renglones.getColumnModel().getColumn(0).setPreferredWidth(200);
        tbl_Renglones.getColumnModel().getColumn(1).setPreferredWidth(400);
        tbl_Renglones.getColumnModel().getColumn(2).setPreferredWidth(200);
        tbl_Renglones.getColumnModel().getColumn(3).setPreferredWidth(150);
        tbl_Renglones.getColumnModel().getColumn(4).setPreferredWidth(150);
        tbl_Renglones.getColumnModel().getColumn(5).setPreferredWidth(180);
        tbl_Renglones.getColumnModel().getColumn(6).setPreferredWidth(120);
    }

    private boolean existeProductoCargado(Producto producto) {
        return renglones.stream()
                        .anyMatch(r -> r.getDescripcionItem().equals(producto.getDescripcion()));
    }

    private void cargarFactura() {
        if (facturaParaMostrar.getNumSerie() == 0 && facturaParaMostrar.getNumFactura() == 0) {
            txt_SerieFactura.setText("");
            txt_NumeroFactura.setText("");
        } else {
            txt_SerieFactura.setText(String.valueOf(facturaParaMostrar.getNumSerie()));
            txt_NumeroFactura.setText(String.valueOf(facturaParaMostrar.getNumFactura()));
        }
        cmb_TipoFactura.removeAllItems();
        cmb_TipoFactura.addItem(facturaParaMostrar.getTipoComprobante());
        txtProveedor.setText(facturaParaMostrar.getRazonSocialProveedor());        
        cmb_Transportista.addItem(facturaParaMostrar.getNombreTransportista());
        dc_FechaFactura.setDate(facturaParaMostrar.getFecha());
        dc_FechaVencimiento.setDate(facturaParaMostrar.getFechaVencimiento());
        txta_Observaciones.setText(facturaParaMostrar.getObservaciones());
        txt_SubTotal.setValue(facturaParaMostrar.getSubTotal());
        txt_Descuento_Porcentaje.setValue(facturaParaMostrar.getDescuentoPorcentaje());
        txt_Descuento_Neto.setValue(facturaParaMostrar.getDescuentoNeto());
        txt_Recargo_Porcentaje.setValue(facturaParaMostrar.getRecargoPorcentaje());
        txt_Recargo_Neto.setValue(facturaParaMostrar.getRecargoNeto());
        txt_SubTotal_Neto.setValue(facturaParaMostrar.getSubTotalBruto());
        txt_IVA_105.setValue(facturaParaMostrar.getIva105Neto());
        txt_IVA_21.setValue(facturaParaMostrar.getIva21Neto());
        txt_Total.setValue(facturaParaMostrar.getTotal());
        try {
            facturaParaMostrar.setRenglones(new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/facturas/" + facturaParaMostrar.getId_Factura() + "/renglones",
                    RenglonFactura[].class))));
            facturaParaMostrar.getRenglones().stream().forEach(r -> {
                this.agregarRenglon(r);
            });
            tbl_Renglones.setModel(modeloTablaRenglones);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTiposDeFacturaDisponibles() {
        try {
            TipoDeComprobante[] tiposFactura = RestClient.getRestTemplate()
                    .getForObject("/facturas/compra/tipos/empresas/"
                    + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                    + "/proveedores/" + proveedorSeleccionado.getId_Proveedor(),
                    TipoDeComprobante[].class);
            cmb_TipoFactura.removeAllItems();
            for (int i = 0; tiposFactura.length > i; i++) {
                cmb_TipoFactura.addItem(tiposFactura[i]);
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

    private void recargarRenglonesSegunTipoDeFactura() {
        List<RenglonFactura> resguardoRenglones = renglones;
        renglones = new ArrayList<>();
        modeloTablaRenglones = new ModeloTabla();
        this.setColumnas();
        try {
            resguardoRenglones.stream().map(rf -> {
                Producto producto = RestClient.getRestTemplate()
                        .getForObject("/productos/" + rf.getIdProductoItem(), Producto.class);
                RenglonFactura nuevoRenglon = RestClient.getRestTemplate().getForObject("/facturas/renglon?"
                        + "idProducto=" + producto.getIdProducto()
                        + "&tipoDeComprobante=" + tipoDeComprobante.name()
                        + "&movimiento=" + Movimiento.COMPRA
                        + "&cantidad=" + rf.getCantidad()
                        + "&descuentoPorcentaje=" + rf.getDescuentoPorcentaje(),                        
                        RenglonFactura.class);
                return nuevoRenglon;
            }).forEachOrdered(nuevoRenglon -> {
                this.agregarRenglon(nuevoRenglon);
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

    private void agregarListeners() {
        //listeners        
        btn_Guardar.addKeyListener(keyHandler);
        btn_NuevoProducto.addKeyListener(keyHandler);
        btn_NuevoProveedor.addKeyListener(keyHandler);
        btn_NuevoTransportista.addKeyListener(keyHandler);
        btn_BuscarProducto.addKeyListener(keyHandler);
        btn_QuitarDeLista.addKeyListener(keyHandler);
        btnBuscarProveedor.addKeyListener(keyHandler);
        cmb_TipoFactura.addKeyListener(keyHandler);
        cmb_Transportista.addKeyListener(keyHandler);
        dc_FechaFactura.addKeyListener(keyHandler);
        dc_FechaVencimiento.addKeyListener(keyHandler);
        jScrollPane1.addKeyListener(keyHandler);
        sp_Renglones.addKeyListener(keyHandler);
        tbl_Renglones.addKeyListener(keyHandler);
        txt_Descuento_Porcentaje.addKeyListener(keyHandler);
        txt_Descuento_Neto.addKeyListener(keyHandler);
        txt_Recargo_Porcentaje.addKeyListener(keyHandler);
        txt_Recargo_Neto.addKeyListener(keyHandler);
        txt_IVA_105.addKeyListener(keyHandler);
        txt_IVA_21.addKeyListener(keyHandler);
        txt_NumeroFactura.addKeyListener(keyHandler);
        txt_SerieFactura.addKeyListener(keyHandler);
        txt_SubTotal.addKeyListener(keyHandler);
        txt_SubTotal_Neto.addKeyListener(keyHandler);
        txt_Total.addKeyListener(keyHandler);
        txta_Observaciones.addKeyListener(keyHandler);
    }

    /**
     * Clase interna para manejar las hotkeys
     */
    class HotKeysHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {

            if (evt.getKeyCode() == KeyEvent.VK_F4) {
                btn_BuscarProductoActionPerformed(null);
            }
            
            if (evt.getKeyCode() == KeyEvent.VK_F7) {
                btn_NuevoProductoActionPerformed(null);
            }

            if (evt.getSource() == tbl_Renglones && evt.getKeyCode() == 127) {
                btn_QuitarDeListaActionPerformed(null);
            }

            if (evt.getSource() == tbl_Renglones && evt.getKeyCode() == KeyEvent.VK_TAB) {
                txt_Descuento_Porcentaje.requestFocus();
            }
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelDatosComprobanteDerecho = new javax.swing.JPanel();
        lbl_Transporte = new javax.swing.JLabel();
        cmb_Transportista = new javax.swing.JComboBox();
        btn_NuevoTransportista = new javax.swing.JButton();
        lbl_Proveedor = new javax.swing.JLabel();
        btn_NuevoProveedor = new javax.swing.JButton();
        lbl_TipoFactura = new javax.swing.JLabel();
        cmb_TipoFactura = new javax.swing.JComboBox();
        txtProveedor = new javax.swing.JTextField();
        btnBuscarProveedor = new javax.swing.JButton();
        panelRenglones = new javax.swing.JPanel();
        sp_Renglones = new javax.swing.JScrollPane();
        tbl_Renglones = new javax.swing.JTable();
        btn_BuscarProducto = new javax.swing.JButton();
        btn_NuevoProducto = new javax.swing.JButton();
        btn_QuitarDeLista = new javax.swing.JButton();
        panelResultados = new javax.swing.JPanel();
        lbl_SubTotal = new javax.swing.JLabel();
        lbl_Total = new javax.swing.JLabel();
        txt_SubTotal = new javax.swing.JFormattedTextField();
        txt_Total = new javax.swing.JFormattedTextField();
        lbl_IVA_105 = new javax.swing.JLabel();
        txt_IVA_105 = new javax.swing.JFormattedTextField();
        lbl_Descuento = new javax.swing.JLabel();
        txt_Descuento_Porcentaje = new javax.swing.JFormattedTextField();
        txt_Descuento_Neto = new javax.swing.JFormattedTextField();
        lbl_SubTotalBruto = new javax.swing.JLabel();
        txt_SubTotal_Neto = new javax.swing.JFormattedTextField();
        lbl_105 = new javax.swing.JLabel();
        lbl_IVA_21 = new javax.swing.JLabel();
        lbl_21 = new javax.swing.JLabel();
        txt_IVA_21 = new javax.swing.JFormattedTextField();
        lbl_Recargo = new javax.swing.JLabel();
        txt_Recargo_Porcentaje = new javax.swing.JFormattedTextField();
        txt_Recargo_Neto = new javax.swing.JFormattedTextField();
        panelMisc = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txta_Observaciones = new javax.swing.JTextArea();
        btn_Guardar = new javax.swing.JButton();
        panelDatosComprobanteIzquierdo = new javax.swing.JPanel();
        lbl_Fecha = new javax.swing.JLabel();
        dc_FechaFactura = new com.toedter.calendar.JDateChooser();
        lbl_NumComprobante = new javax.swing.JLabel();
        lbl_FechaVto = new javax.swing.JLabel();
        dc_FechaVencimiento = new com.toedter.calendar.JDateChooser();
        txt_SerieFactura = new javax.swing.JFormattedTextField();
        txt_NumeroFactura = new javax.swing.JFormattedTextField();
        lbl_separador = new javax.swing.JLabel();
        lblCantidadDeArticulos = new javax.swing.JLabel();

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

        panelDatosComprobanteDerecho.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Transporte.setForeground(java.awt.Color.red);
        lbl_Transporte.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Transporte.setText("* Transporte:");

        btn_NuevoTransportista.setForeground(java.awt.Color.blue);
        btn_NuevoTransportista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddTruck_16x16.png"))); // NOI18N
        btn_NuevoTransportista.setText("Nuevo");
        btn_NuevoTransportista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoTransportistaActionPerformed(evt);
            }
        });

        lbl_Proveedor.setForeground(java.awt.Color.red);
        lbl_Proveedor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Proveedor.setText("* Proveedor:");

        btn_NuevoProveedor.setForeground(java.awt.Color.blue);
        btn_NuevoProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProviderBag_16x16.png"))); // NOI18N
        btn_NuevoProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoProveedorActionPerformed(evt);
            }
        });

        lbl_TipoFactura.setForeground(java.awt.Color.red);
        lbl_TipoFactura.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TipoFactura.setText("* Tipo de Factura:");

        cmb_TipoFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_TipoFacturaItemStateChanged(evt);
            }
        });

        txtProveedor.setEditable(false);
        txtProveedor.setOpaque(false);

        btnBuscarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProveedorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDatosComprobanteDerechoLayout = new javax.swing.GroupLayout(panelDatosComprobanteDerecho);
        panelDatosComprobanteDerecho.setLayout(panelDatosComprobanteDerechoLayout);
        panelDatosComprobanteDerechoLayout.setHorizontalGroup(
            panelDatosComprobanteDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosComprobanteDerechoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosComprobanteDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbl_Proveedor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_TipoFactura, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Transporte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosComprobanteDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelDatosComprobanteDerechoLayout.createSequentialGroup()
                        .addGroup(panelDatosComprobanteDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_TipoFactura, 0, 260, Short.MAX_VALUE)
                            .addComponent(cmb_Transportista, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(btn_NuevoTransportista))
                    .addGroup(panelDatosComprobanteDerechoLayout.createSequentialGroup()
                        .addComponent(txtProveedor)
                        .addGap(0, 0, 0)
                        .addComponent(btnBuscarProveedor)
                        .addGap(0, 0, 0)
                        .addComponent(btn_NuevoProveedor)))
                .addContainerGap())
        );
        panelDatosComprobanteDerechoLayout.setVerticalGroup(
            panelDatosComprobanteDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosComprobanteDerechoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosComprobanteDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Proveedor)
                    .addComponent(btn_NuevoProveedor)
                    .addComponent(txtProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarProveedor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosComprobanteDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Transporte)
                    .addComponent(cmb_Transportista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevoTransportista))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosComprobanteDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_TipoFactura)
                    .addComponent(cmb_TipoFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDatosComprobanteDerechoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevoTransportista, cmb_TipoFactura, cmb_Transportista, txtProveedor});

        panelRenglones.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        tbl_Renglones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Renglones.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Renglones.getTableHeader().setReorderingAllowed(false);
        sp_Renglones.setViewportView(tbl_Renglones);

        btn_BuscarProducto.setForeground(java.awt.Color.blue);
        btn_BuscarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Product_16x16.png"))); // NOI18N
        btn_BuscarProducto.setText("Buscar Producto (F4)");
        btn_BuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarProductoActionPerformed(evt);
            }
        });

        btn_NuevoProducto.setForeground(java.awt.Color.blue);
        btn_NuevoProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProduct_16x16.png"))); // NOI18N
        btn_NuevoProducto.setText("Nuevo Producto (F7)");
        btn_NuevoProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoProductoActionPerformed(evt);
            }
        });

        btn_QuitarDeLista.setForeground(java.awt.Color.blue);
        btn_QuitarDeLista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteProduct_16x16.png"))); // NOI18N
        btn_QuitarDeLista.setText("Quitar Producto (DEL)");
        btn_QuitarDeLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_QuitarDeListaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRenglonesLayout = new javax.swing.GroupLayout(panelRenglones);
        panelRenglones.setLayout(panelRenglonesLayout);
        panelRenglonesLayout.setHorizontalGroup(
            panelRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Renglones, javax.swing.GroupLayout.DEFAULT_SIZE, 877, Short.MAX_VALUE)
            .addGroup(panelRenglonesLayout.createSequentialGroup()
                .addComponent(btn_BuscarProducto)
                .addGap(0, 0, 0)
                .addComponent(btn_QuitarDeLista)
                .addGap(0, 0, 0)
                .addComponent(btn_NuevoProducto)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        panelRenglonesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_BuscarProducto, btn_NuevoProducto, btn_QuitarDeLista});

        panelRenglonesLayout.setVerticalGroup(
            panelRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRenglonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_BuscarProducto)
                    .addComponent(btn_NuevoProducto)
                    .addComponent(btn_QuitarDeLista))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Renglones, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelRenglonesLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_BuscarProducto, btn_NuevoProducto, btn_QuitarDeLista});

        panelResultados.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lbl_SubTotal.setText("SubTotal");

        lbl_Total.setText("TOTAL");

        txt_SubTotal.setEditable(false);
        txt_SubTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_SubTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_SubTotal.setText("0");
        txt_SubTotal.setFocusable(false);
        txt_SubTotal.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N

        txt_Total.setEditable(false);
        txt_Total.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Total.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Total.setText("0");
        txt_Total.setFocusable(false);
        txt_Total.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N

        lbl_IVA_105.setText("I.V.A.");

        txt_IVA_105.setEditable(false);
        txt_IVA_105.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_IVA_105.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_IVA_105.setText("0");
        txt_IVA_105.setFocusable(false);
        txt_IVA_105.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N

        lbl_Descuento.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_Descuento.setText("Descuento (%)");

        txt_Descuento_Porcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Descuento_Porcentaje.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Descuento_Porcentaje.setText("0");
        txt_Descuento_Porcentaje.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        txt_Descuento_Porcentaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Descuento_PorcentajeFocusLost(evt);
            }
        });
        txt_Descuento_Porcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_Descuento_PorcentajeActionPerformed(evt);
            }
        });

        txt_Descuento_Neto.setEditable(false);
        txt_Descuento_Neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Descuento_Neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Descuento_Neto.setText("0");
        txt_Descuento_Neto.setFocusable(false);
        txt_Descuento_Neto.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N

        lbl_SubTotalBruto.setText("SubTotal Bruto");

        txt_SubTotal_Neto.setEditable(false);
        txt_SubTotal_Neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_SubTotal_Neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_SubTotal_Neto.setText("0");
        txt_SubTotal_Neto.setFocusable(false);
        txt_SubTotal_Neto.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N

        lbl_105.setText("10.5 %");

        lbl_IVA_21.setText("I.V.A.");

        lbl_21.setText("21 %");

        txt_IVA_21.setEditable(false);
        txt_IVA_21.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_IVA_21.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_IVA_21.setText("0");
        txt_IVA_21.setFocusable(false);
        txt_IVA_21.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N

        lbl_Recargo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_Recargo.setText("Recargo (%)");

        txt_Recargo_Porcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Recargo_Porcentaje.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Recargo_Porcentaje.setText("0");
        txt_Recargo_Porcentaje.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        txt_Recargo_Porcentaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Recargo_PorcentajeFocusLost(evt);
            }
        });
        txt_Recargo_Porcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_Recargo_PorcentajeActionPerformed(evt);
            }
        });

        txt_Recargo_Neto.setEditable(false);
        txt_Recargo_Neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Recargo_Neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Recargo_Neto.setText("0");
        txt_Recargo_Neto.setFocusable(false);
        txt_Recargo_Neto.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_SubTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                    .addComponent(txt_SubTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Descuento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_Descuento_Neto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Descuento_Porcentaje))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Recargo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_Recargo_Neto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Recargo_Porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_SubTotal_Neto)
                    .addComponent(lbl_SubTotalBruto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_105, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_IVA_105, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_IVA_105, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_IVA_21)
                    .addComponent(lbl_IVA_21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_21, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_Total)
                    .addComponent(lbl_Total, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_SubTotalBruto)
                    .addComponent(lbl_Recargo)
                    .addComponent(lbl_Descuento)
                    .addComponent(lbl_SubTotal)
                    .addComponent(lbl_IVA_105)
                    .addComponent(lbl_IVA_21)
                    .addComponent(lbl_Total))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_Descuento_Porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Recargo_Porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_105)
                    .addComponent(lbl_21))
                .addGap(5, 5, 5)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_SubTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Descuento_Neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Recargo_Neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_SubTotal_Neto, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_IVA_105, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_IVA_21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txt_Descuento_Neto, txt_IVA_105, txt_IVA_21, txt_SubTotal, txt_SubTotal_Neto, txt_Total});

        panelMisc.setBorder(javax.swing.BorderFactory.createTitledBorder("Observaciones"));

        txta_Observaciones.setColumns(20);
        txta_Observaciones.setLineWrap(true);
        txta_Observaciones.setRows(5);
        jScrollPane1.setViewportView(txta_Observaciones);

        javax.swing.GroupLayout panelMiscLayout = new javax.swing.GroupLayout(panelMisc);
        panelMisc.setLayout(panelMiscLayout);
        panelMiscLayout.setHorizontalGroup(
            panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        panelMiscLayout.setVerticalGroup(
            panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMiscLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.setEnabled(false);
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        panelDatosComprobanteIzquierdo.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Fecha.setForeground(java.awt.Color.red);
        lbl_Fecha.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Fecha.setText("* Fecha de Factura:");

        dc_FechaFactura.setDateFormatString("dd/MM/yyyy");

        lbl_NumComprobante.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_NumComprobante.setText("Nº de Factura:");

        lbl_FechaVto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_FechaVto.setText("Fecha Vencimiento:");

        dc_FechaVencimiento.setDateFormatString("dd/MM/yyyy");

        txt_SerieFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        txt_NumeroFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        lbl_separador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_separador.setText("-");

        javax.swing.GroupLayout panelDatosComprobanteIzquierdoLayout = new javax.swing.GroupLayout(panelDatosComprobanteIzquierdo);
        panelDatosComprobanteIzquierdo.setLayout(panelDatosComprobanteIzquierdoLayout);
        panelDatosComprobanteIzquierdoLayout.setHorizontalGroup(
            panelDatosComprobanteIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosComprobanteIzquierdoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosComprobanteIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_FechaVto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Fecha, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_NumComprobante, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosComprobanteIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelDatosComprobanteIzquierdoLayout.createSequentialGroup()
                        .addComponent(txt_SerieFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_separador, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txt_NumeroFactura))
                    .addComponent(dc_FechaFactura, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(dc_FechaVencimiento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDatosComprobanteIzquierdoLayout.setVerticalGroup(
            panelDatosComprobanteIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosComprobanteIzquierdoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosComprobanteIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_NumComprobante)
                    .addComponent(txt_SerieFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_separador)
                    .addComponent(txt_NumeroFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosComprobanteIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Fecha)
                    .addComponent(dc_FechaFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosComprobanteIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_FechaVto)
                    .addComponent(dc_FechaVencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelRenglones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelDatosComprobanteDerecho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelDatosComprobanteIzquierdo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panelMisc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelResultados, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_Guardar)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblCantidadDeArticulos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelDatosComprobanteIzquierdo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelDatosComprobanteDerecho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRenglones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCantidadDeArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Guardar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_NuevoTransportistaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoTransportistaActionPerformed
        DetalleTransportistaGUI gui_DetalleTransportista = new DetalleTransportistaGUI();
        gui_DetalleTransportista.setModal(true);
        gui_DetalleTransportista.setLocationRelativeTo(this);
        gui_DetalleTransportista.setVisible(true);
        this.cargarTransportistas();
}//GEN-LAST:event_btn_NuevoTransportistaActionPerformed

    private void btn_NuevoProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoProveedorActionPerformed
        DetalleProveedorGUI gui_DetalleProveedor = new DetalleProveedorGUI();
        gui_DetalleProveedor.setModal(true);
        gui_DetalleProveedor.setLocationRelativeTo(this);
        gui_DetalleProveedor.setVisible(true);
        if (gui_DetalleProveedor.getProveedorCreado() != null) {
            txtProveedor.setText(gui_DetalleProveedor.getProveedorCreado().getRazonSocial());
            proveedorSeleccionado = gui_DetalleProveedor.getProveedorCreado();
            this.cargarTiposDeFacturaDisponibles();
            this.btn_Guardar.setEnabled(true);
        }
    }//GEN-LAST:event_btn_NuevoProveedorActionPerformed

    private void btn_NuevoProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoProductoActionPerformed
        if (proveedorSeleccionado != null) {
            DetalleProductoGUI gui_DetalleProducto = new DetalleProductoGUI();
            gui_DetalleProducto.setModal(true);
            gui_DetalleProducto.setLocationRelativeTo(this);
            gui_DetalleProducto.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_seleccionar_proveedor"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_NuevoProductoActionPerformed

    private void btn_BuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarProductoActionPerformed
        if (proveedorSeleccionado != null) {
            BuscarProductosGUI gui_buscarProducto = new BuscarProductosGUI(renglones,
                    (TipoDeComprobante) cmb_TipoFactura.getSelectedItem(), Movimiento.COMPRA);
            gui_buscarProducto.setModal(true);
            gui_buscarProducto.setLocationRelativeTo(this);
            gui_buscarProducto.setVisible(true);
            this.cargarRenglonFactura(gui_buscarProducto);
        } else {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_seleccionar_proveedor"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_BuscarProductoActionPerformed

    private void btn_QuitarDeListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_QuitarDeListaActionPerformed
        this.quitarRenglonFactura();
    }//GEN-LAST:event_btn_QuitarDeListaActionPerformed

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        if (this.guardarFactura()) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "La Factura se guardó correctamente!\n¿Desea dar de alta otra Factura?",
                    "Aviso", JOptionPane.YES_NO_OPTION);
            this.limpiarYRecargarComponentes();
            if (respuesta == JOptionPane.NO_OPTION) {
                this.dispose();
            }
        }
    }//GEN-LAST:event_btn_GuardarActionPerformed

    private void txt_Descuento_PorcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_Descuento_PorcentajeActionPerformed
        this.calcularResultados();
    }//GEN-LAST:event_txt_Descuento_PorcentajeActionPerformed

    private void txt_Descuento_PorcentajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Descuento_PorcentajeFocusLost
        this.calcularResultados();
    }//GEN-LAST:event_txt_Descuento_PorcentajeFocusLost

    private void cmb_TipoFacturaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_TipoFacturaItemStateChanged
        //para evitar que pase null cuando esta recargando el comboBox
        if (cmb_TipoFactura.getSelectedItem() != null && operacionAlta) {
            this.tipoDeComprobante = (TipoDeComprobante)cmb_TipoFactura.getSelectedItem();
            this.recargarRenglonesSegunTipoDeFactura();
        }
    }//GEN-LAST:event_cmb_TipoFacturaItemStateChanged

    private void txt_Recargo_PorcentajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Recargo_PorcentajeFocusLost
        this.calcularResultados();
    }//GEN-LAST:event_txt_Recargo_PorcentajeFocusLost

    private void txt_Recargo_PorcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_Recargo_PorcentajeActionPerformed
        this.calcularResultados();
    }//GEN-LAST:event_txt_Recargo_PorcentajeActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.setColumnas();        
        if (operacionAlta == false) {
            this.setTitle(facturaParaMostrar.getTipoComprobante() + " Nº " + facturaParaMostrar.getNumSerie() + " - " + facturaParaMostrar.getNumFactura()
                    + " con fecha " + formatter.format(facturaParaMostrar.getFecha()) + " del Proveedor: " + facturaParaMostrar.getRazonSocialProveedor());
            this.cargarFactura();
            DecimalFormat df = new DecimalFormat("#.##");
            lblCantidadDeArticulos.setText("Cantidad de Articulos: " + df.format(this.facturaParaMostrar.getCantidadArticulos()));
        } else {
            this.setTitle("Nueva Factura Compra");
            this.cargarTransportistas();
            lblCantidadDeArticulos.setVisible(false);
        }
        try {
            this.setMaximum(true);    
        } catch (PropertyVetoException ex) {
            String msjError = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnBuscarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProveedorActionPerformed
        BuscarProveedoresGUI buscarProveedoresGUI = new BuscarProveedoresGUI();
        buscarProveedoresGUI.setModal(true);
        buscarProveedoresGUI.setLocationRelativeTo(this);
        buscarProveedoresGUI.setVisible(true);
        if (buscarProveedoresGUI.getProveedorSeleccionado() != null) {
            proveedorSeleccionado = buscarProveedoresGUI.getProveedorSeleccionado();
            txtProveedor.setText(proveedorSeleccionado.getRazonSocial());
            this.cargarTiposDeFacturaDisponibles();
            this.btn_Guardar.setEnabled(true);
        }
    }//GEN-LAST:event_btnBuscarProveedorActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarProveedor;
    private javax.swing.JButton btn_BuscarProducto;
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JButton btn_NuevoProducto;
    private javax.swing.JButton btn_NuevoProveedor;
    private javax.swing.JButton btn_NuevoTransportista;
    private javax.swing.JButton btn_QuitarDeLista;
    private javax.swing.JComboBox cmb_TipoFactura;
    private javax.swing.JComboBox cmb_Transportista;
    private com.toedter.calendar.JDateChooser dc_FechaFactura;
    private com.toedter.calendar.JDateChooser dc_FechaVencimiento;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCantidadDeArticulos;
    private javax.swing.JLabel lbl_105;
    private javax.swing.JLabel lbl_21;
    private javax.swing.JLabel lbl_Descuento;
    private javax.swing.JLabel lbl_Fecha;
    private javax.swing.JLabel lbl_FechaVto;
    private javax.swing.JLabel lbl_IVA_105;
    private javax.swing.JLabel lbl_IVA_21;
    private javax.swing.JLabel lbl_NumComprobante;
    private javax.swing.JLabel lbl_Proveedor;
    private javax.swing.JLabel lbl_Recargo;
    private javax.swing.JLabel lbl_SubTotal;
    private javax.swing.JLabel lbl_SubTotalBruto;
    private javax.swing.JLabel lbl_TipoFactura;
    private javax.swing.JLabel lbl_Total;
    private javax.swing.JLabel lbl_Transporte;
    private javax.swing.JLabel lbl_separador;
    private javax.swing.JPanel panelDatosComprobanteDerecho;
    private javax.swing.JPanel panelDatosComprobanteIzquierdo;
    private javax.swing.JPanel panelMisc;
    private javax.swing.JPanel panelRenglones;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane sp_Renglones;
    private javax.swing.JTable tbl_Renglones;
    private javax.swing.JTextField txtProveedor;
    private javax.swing.JFormattedTextField txt_Descuento_Neto;
    private javax.swing.JFormattedTextField txt_Descuento_Porcentaje;
    private javax.swing.JFormattedTextField txt_IVA_105;
    private javax.swing.JFormattedTextField txt_IVA_21;
    private javax.swing.JFormattedTextField txt_NumeroFactura;
    private javax.swing.JFormattedTextField txt_Recargo_Neto;
    private javax.swing.JFormattedTextField txt_Recargo_Porcentaje;
    private javax.swing.JFormattedTextField txt_SerieFactura;
    private javax.swing.JFormattedTextField txt_SubTotal;
    private javax.swing.JFormattedTextField txt_SubTotal_Neto;
    private javax.swing.JFormattedTextField txt_Total;
    private javax.swing.JTextArea txta_Observaciones;
    // End of variables declaration//GEN-END:variables
}
