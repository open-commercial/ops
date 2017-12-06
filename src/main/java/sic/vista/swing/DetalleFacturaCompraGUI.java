package sic.vista.swing;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import sic.util.RenderTabla;

public class DetalleFacturaCompraGUI extends JInternalFrame {

    private ModeloTabla modeloTablaRenglones = new ModeloTabla();
    private List<RenglonFactura> renglones = new ArrayList<>();
    private final FacturaCompra facturaParaMostrar;
    private TipoDeComprobante tipoDeComprobante;
    private final boolean operacionAlta;
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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
        cmb_Proveedor.setEnabled(false);
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
        txt_SerieFactura.setValue(new Long("0"));
        txt_NumeroFactura.setValue(new Long("0"));
        txt_SubTotal.setValue(new Double("0.0"));
        txt_Descuento_Porcentaje.setValue(new Double("0.0"));
        txt_Descuento_Neto.setValue(new Double("0.0"));
        txt_Recargo_Porcentaje.setValue(new Double("0.0"));
        txt_Recargo_Neto.setValue(new Double("0.0"));
        txt_SubTotal_Neto.setValue(new Double("0.0"));
        txt_IVA_105.setValue(new Double("0.0"));
        txt_IVA_21.setValue(new Double("0.0"));
        txt_Total.setValue(new Double("0.0"));
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
        lineaDeFactura[5] = renglon.getDescuento_porcentaje();
        lineaDeFactura[6] = renglon.getImporte();
        modeloTablaRenglones.addRow(lineaDeFactura);
        renglones.add(renglon);
        this.calcularResultados();

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

    private void cargarProveedores() {
        cmb_Proveedor.removeAllItems();      
        try {
            List<Proveedor> proveedores = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/proveedores/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                    Proveedor[].class)));
            proveedores.stream().forEach((p) -> {
                cmb_Proveedor.addItem(p);
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

    private void cargarTransportistas() {        
        cmb_Transportista.removeAllItems();
        try {
            List<Transportista> transportistas = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/transportistas/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                    Transportista[].class)));
            transportistas.stream().forEach((t) -> {
                cmb_Transportista.addItem(t);
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

    private boolean guardarFactura() {
        FacturaCompra facturaCompra = new FacturaCompra();
        facturaCompra.setFecha(dc_FechaFactura.getDate());
        facturaCompra.setTipoComprobante(tipoDeComprobante);
        facturaCompra.setNumSerie(Long.parseLong(txt_SerieFactura.getValue().toString()));
        facturaCompra.setNumFactura(Long.parseLong(txt_NumeroFactura.getValue().toString()));
        facturaCompra.setFechaVencimiento(dc_FechaVencimiento.getDate());
        facturaCompra.setRenglones(new ArrayList<>(renglones));
        facturaCompra.setSubTotal(Double.parseDouble(txt_SubTotal.getValue().toString()));
        facturaCompra.setRecargo_porcentaje(0);
        facturaCompra.setRecargo_neto(0);
        facturaCompra.setDescuento_porcentaje(Double.parseDouble(txt_Descuento_Porcentaje.getValue().toString()));
        facturaCompra.setDescuento_neto(Double.parseDouble(txt_Descuento_Neto.getValue().toString()));
        facturaCompra.setRecargo_porcentaje(Double.parseDouble(txt_Recargo_Porcentaje.getValue().toString()));
        facturaCompra.setRecargo_neto(Double.parseDouble(txt_Recargo_Neto.getValue().toString()));
        facturaCompra.setSubTotal_bruto(Double.parseDouble(txt_SubTotal_Neto.getValue().toString()));
        facturaCompra.setIva_105_neto(Double.parseDouble(txt_IVA_105.getValue().toString()));
        facturaCompra.setIva_21_neto(Double.parseDouble(txt_IVA_21.getValue().toString()));
        facturaCompra.setTotal(Double.parseDouble(txt_Total.getValue().toString()));
        facturaCompra.setObservaciones(txta_Observaciones.getText().trim());
        facturaCompra.setPagada(false);
        facturaCompra.setEliminada(false);
        try {
            RestClient.getRestTemplate().postForObject("/facturas/compra"
                    + "?idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                    + "&idProveedor=" + ((Proveedor) cmb_Proveedor.getSelectedItem()).getId_Proveedor()
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
        txt_SubTotal.setValue(0.0);
        txt_Descuento_Porcentaje.setValue(0.0);
        txt_Descuento_Neto.setValue(0.0);
        txt_Recargo_Porcentaje.setValue(new Double("0.0"));
        txt_Recargo_Neto.setValue(new Double("0.0"));
        txt_SubTotal_Neto.setValue(0.0);
        txt_IVA_105.setValue(0.0);
        txt_IVA_21.setValue(0.0);
        txt_Total.setValue(0.0);
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
        double subTotal = 0;
        double descuento_porcentaje;
        double descuento_neto;
        double recargo_porcentaje;
        double recargo_neto;
        double subTotalBruto;
        double iva105_netoFactura;
        double iva21_netoFactura;        
        double total;
        this.validarComponentesDeResultados();
        //subtotal        
         double[] cantidades = new double[renglones.size()];
        double[] ivaPorcentajeRenglones = new double[renglones.size()];
        double[] ivaNetoRenglones = new double[renglones.size()]; 
        int indice = 0;
        for (RenglonFactura renglon : renglones) {
            subTotal += renglon.getImporte();
            cantidades[indice] = renglon.getCantidad();
            ivaPorcentajeRenglones[indice] = renglon.getIva_porcentaje();
            ivaNetoRenglones[indice] = renglon.getIva_neto();            
            indice++;
        }
        try {
            txt_SubTotal.setValue(subTotal);
            //Descuento
            descuento_porcentaje = Double.parseDouble(txt_Descuento_Porcentaje.getValue().toString());
            descuento_neto = (subTotal * descuento_porcentaje) / 100;
            txt_Descuento_Neto.setValue(descuento_neto);            
            //Regargo
            recargo_porcentaje = Double.parseDouble(txt_Recargo_Porcentaje.getValue().toString());
            recargo_neto = (subTotal * recargo_porcentaje) / 100;
            txt_Recargo_Neto.setValue(recargo_neto);            
            //iva 10,5% neto - IVA 21% neto
            iva105_netoFactura = 0;
            iva21_netoFactura = 0;
            indice = cantidades.length;
            if (tipoDeComprobante == TipoDeComprobante.FACTURA_B || tipoDeComprobante == TipoDeComprobante.FACTURA_A || tipoDeComprobante == TipoDeComprobante.PRESUPUESTO) {
                for (int i = 0; i < indice; i++) {
                    if (ivaPorcentajeRenglones[i] == 10.5) {
                        iva105_netoFactura += cantidades[i] * (ivaNetoRenglones[i]
                                - (ivaNetoRenglones[i] * (descuento_porcentaje / 100))
                                + (ivaNetoRenglones[i] * (recargo_porcentaje / 100)));
                    } else if (ivaPorcentajeRenglones[i] == 21) {
                        iva21_netoFactura += cantidades[i] * (ivaNetoRenglones[i]
                                - (ivaNetoRenglones[i] * (descuento_porcentaje / 100))
                                + (ivaNetoRenglones[i] * (recargo_porcentaje / 100)));
                    }
                }
            } else {
                for (int i = 0; i < indice; i++) {
                    if (ivaPorcentajeRenglones[i] == 10.5) {
                    iva105_netoFactura += cantidades[i] * ivaNetoRenglones[i];} else if (ivaPorcentajeRenglones[i] == 21){
                    iva21_netoFactura += cantidades[i] * ivaNetoRenglones[i];}
                }
            }
            if (tipoDeComprobante == TipoDeComprobante.FACTURA_B || tipoDeComprobante == TipoDeComprobante.PRESUPUESTO) {
                txt_IVA_105.setValue(0);
                txt_IVA_21.setValue(0);
            } else {
                txt_IVA_105.setValue(iva105_netoFactura);
                txt_IVA_21.setValue(iva21_netoFactura);
            }
            //subtotal bruto
            subTotalBruto = subTotal + recargo_neto - descuento_neto;
            if (tipoDeComprobante == TipoDeComprobante.FACTURA_B || tipoDeComprobante == TipoDeComprobante.PRESUPUESTO) {
                subTotalBruto = subTotalBruto - (iva105_netoFactura + iva21_netoFactura);
            }
            total = subTotalBruto + iva105_netoFactura + iva21_netoFactura;
            txt_Total.setValue(total);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        tipos[3] = Double.class;
        tipos[4] = Double.class;
        tipos[5] = Double.class;
        tipos[6] = Double.class;
        modeloTablaRenglones.setClaseColumnas(tipos);
        tbl_Renglones.getTableHeader().setReorderingAllowed(false);
        tbl_Renglones.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Renglones.setDefaultRenderer(Double.class, new RenderTabla());

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
        return renglones.stream().anyMatch((renglon) -> (renglon.getDescripcionItem().equals(producto.getDescripcion())));
    }

    private void cargarFactura() {
        if (facturaParaMostrar.getNumSerie() == 0 && facturaParaMostrar.getNumFactura() == 0) {
            txt_SerieFactura.setText("");
            txt_NumeroFactura.setText("");
        } else {
            txt_SerieFactura.setText(String.valueOf(facturaParaMostrar.getNumSerie()));
            txt_NumeroFactura.setText(String.valueOf(facturaParaMostrar.getNumFactura()));
        }
        cmb_Proveedor.setSelectedItem(facturaParaMostrar.getRazonSocialProveedor());
        cmb_TipoFactura.removeAllItems();
        try {
            cmb_TipoFactura.addItem(facturaParaMostrar.getTipoComprobante());
//            cmb_TipoFactura.addItem(RestClient.getRestTemplate()
//                    .getForObject("/facturas/" + facturaParaMostrar.getId_Factura() + "/tipo",
//                    TipoDeComprobante.class));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        cmb_Transportista.setSelectedItem(facturaParaMostrar.getNombreTransportista());
        dc_FechaFactura.setDate(facturaParaMostrar.getFecha());
        dc_FechaVencimiento.setDate(facturaParaMostrar.getFechaVencimiento());
        txta_Observaciones.setText(facturaParaMostrar.getObservaciones());
        txt_SubTotal.setValue(facturaParaMostrar.getSubTotal());
        txt_Descuento_Porcentaje.setValue(facturaParaMostrar.getDescuento_porcentaje());
        txt_Descuento_Neto.setValue(facturaParaMostrar.getDescuento_neto());
        txt_Recargo_Porcentaje.setValue(facturaParaMostrar.getRecargo_porcentaje());
        txt_Recargo_Neto.setValue(facturaParaMostrar.getRecargo_neto());
        txt_SubTotal_Neto.setValue(facturaParaMostrar.getSubTotal_bruto());
        txt_IVA_105.setValue(facturaParaMostrar.getIva_105_neto());
        txt_IVA_21.setValue(facturaParaMostrar.getIva_21_neto());
        txt_Total.setValue(facturaParaMostrar.getTotal());
        try {
            facturaParaMostrar.setRenglones(new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/facturas/" + facturaParaMostrar.getId_Factura() + "/renglones",
                    RenglonFactura[].class))));
            facturaParaMostrar.getRenglones().stream().forEach((r) -> {
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
                    + "/proveedores/" + ((Proveedor) cmb_Proveedor.getSelectedItem()).getId_Proveedor(),
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
                        .getForObject("/productos/" + rf.getId_ProductoItem(), Producto.class);
                RenglonFactura nuevoRenglon = RestClient.getRestTemplate().getForObject("/facturas/renglon?"
                        + "idProducto=" + producto.getId_Producto()
                        + "&tipoDeComprobante=" + tipoDeComprobante.name()
                        + "&movimiento=" + Movimiento.COMPRA
                        + "&cantidad=" + rf.getCantidad()
                        + "&descuentoPorcentaje=" + rf.getDescuento_porcentaje(),                        
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
        cmb_Proveedor.addKeyListener(keyHandler);
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
        cmb_Proveedor = new javax.swing.JComboBox();
        btn_NuevoProveedor = new javax.swing.JButton();
        lbl_TipoFactura = new javax.swing.JLabel();
        cmb_TipoFactura = new javax.swing.JComboBox();
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

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Nueva Factura de Compra");
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

        cmb_Proveedor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_ProveedorItemStateChanged(evt);
            }
        });

        btn_NuevoProveedor.setForeground(java.awt.Color.blue);
        btn_NuevoProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProviderBag_16x16.png"))); // NOI18N
        btn_NuevoProveedor.setText("Nuevo");
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
                .addGroup(panelDatosComprobanteDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_TipoFactura, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_Transportista, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_Proveedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addGroup(panelDatosComprobanteDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_NuevoTransportista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_NuevoProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelDatosComprobanteDerechoLayout.setVerticalGroup(
            panelDatosComprobanteDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosComprobanteDerechoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosComprobanteDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Proveedor)
                    .addComponent(cmb_Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevoProveedor))
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

        panelDatosComprobanteDerechoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevoProveedor, cmb_Proveedor});

        panelDatosComprobanteDerechoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevoTransportista, cmb_Transportista});

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
                .addComponent(sp_Renglones, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panelMisc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelResultados, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_Guardar)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelDatosComprobanteDerecho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelDatosComprobanteIzquierdo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
        this.cargarProveedores();
    }//GEN-LAST:event_btn_NuevoProveedorActionPerformed

    private void btn_NuevoProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoProductoActionPerformed
        DetalleProductoGUI gui_DetalleProducto = new DetalleProductoGUI();
        gui_DetalleProducto.setModal(true);
        gui_DetalleProducto.setLocationRelativeTo(this);
        gui_DetalleProducto.setVisible(true);
    }//GEN-LAST:event_btn_NuevoProductoActionPerformed

    private void btn_BuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarProductoActionPerformed
        BuscarProductosGUI gui_buscarProducto = new BuscarProductosGUI(renglones,
                (TipoDeComprobante)cmb_TipoFactura.getSelectedItem(), Movimiento.COMPRA);
        gui_buscarProducto.setModal(true);
        gui_buscarProducto.setLocationRelativeTo(this);
        gui_buscarProducto.setVisible(true);
        this.cargarRenglonFactura(gui_buscarProducto);
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

    private void cmb_ProveedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_ProveedorItemStateChanged
        //para evitar que pase null cuando esta recargando el comboBox
        if (cmb_Proveedor.getSelectedItem() != null) {
            this.cargarTiposDeFacturaDisponibles();
        }
    }//GEN-LAST:event_cmb_ProveedorItemStateChanged

    private void cmb_TipoFacturaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_TipoFacturaItemStateChanged
        //para evitar que pase null cuando esta recargando el comboBox
        if (cmb_TipoFactura.getSelectedItem() != null) {
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
        this.cargarProveedores();
        this.cargarTransportistas();
        this.cargarTiposDeFacturaDisponibles();
        this.setColumnas();
        this.setTitle("Factura Compra");
        if (operacionAlta == false) {
            this.cargarFactura();
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_BuscarProducto;
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JButton btn_NuevoProducto;
    private javax.swing.JButton btn_NuevoProveedor;
    private javax.swing.JButton btn_NuevoTransportista;
    private javax.swing.JButton btn_QuitarDeLista;
    private javax.swing.JComboBox cmb_Proveedor;
    private javax.swing.JComboBox cmb_TipoFactura;
    private javax.swing.JComboBox cmb_Transportista;
    private com.toedter.calendar.JDateChooser dc_FechaFactura;
    private com.toedter.calendar.JDateChooser dc_FechaVencimiento;
    private javax.swing.JScrollPane jScrollPane1;
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
