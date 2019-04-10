package sic.vista.swing;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.Movimiento;
import sic.modelo.NotaCredito;
import sic.modelo.Proveedor;
import sic.modelo.RenglonNotaCredito;
import sic.modelo.TipoDeComprobante;
import sic.modelo.UsuarioActivo;
import sic.util.DecimalesRenderer;
import sic.util.FormatosFechaHora;
import sic.util.FormatterFechaHora;

public class DetalleNotaCreditoGUI extends JDialog {

    private final ModeloTabla modeloTablaRenglones = new ModeloTabla();
    private boolean modificarStock;
    private long idFactura;
    private Factura factura;
    private Cliente cliente;
    private Proveedor proveedor;
    private long idNotaCredito;
    private NotaCredito notaCredito;
    private HashMap<Long, BigDecimal> idsRenglonesYCantidades = new HashMap<>();
    private List<RenglonNotaCredito> renglones;
    private boolean notaCreditoCreada;
    private final FormatterFechaHora formatter = new FormatterFechaHora(FormatosFechaHora.FORMATO_FECHA_HISPANO);
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private BigDecimal subTotalBruto = BigDecimal.ZERO;
    private BigDecimal iva_105_netoFactura = BigDecimal.ZERO;
    private BigDecimal iva_21_netoFactura = BigDecimal.ZERO;

    public DetalleNotaCreditoGUI(HashMap<Long, BigDecimal> idsRenglonesYCantidades, long idFactura,
            boolean modificarStock, Cliente cliente) {
        this.initComponents();
        this.setIcon();
        this.idsRenglonesYCantidades = idsRenglonesYCantidades;
        this.modificarStock = modificarStock;
        this.notaCreditoCreada = false;
        this.cliente = cliente;
        this.idFactura = idFactura;
    }

    public DetalleNotaCreditoGUI(HashMap<Long, BigDecimal> idsRenglonesYCantidades, long idFactura,
            boolean modificarStock, Proveedor proveedor) {
        this.initComponents();
        this.setIcon();
        this.idsRenglonesYCantidades = idsRenglonesYCantidades;
        this.modificarStock = modificarStock;
        this.notaCreditoCreada = false;
        this.proveedor = proveedor;
        this.idFactura = idFactura;
    }

    public DetalleNotaCreditoGUI(long idNotaCredito) {
        this.initComponents();
        this.setIcon();
        this.idNotaCredito = idNotaCredito;
    }

    public boolean isNotaCreada() {
        return notaCreditoCreada;
    }

    private void recuperarNotaCreditoProveedor(long idNotaCreditoProvedor) {
        try {
            notaCredito = RestClient.getRestTemplate().getForObject("/notas/" + idNotaCreditoProvedor, NotaCredito.class);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarResultados() {
        if (notaCredito == null) {
            BigDecimal[] importes = new BigDecimal[renglones.size()];
            BigDecimal[] cantidades = new BigDecimal[renglones.size()];
            BigDecimal[] ivaPorcentajeRenglones = new BigDecimal[renglones.size()];
            BigDecimal[] ivaNetoRenglones = new BigDecimal[renglones.size()];
            int indice = 0;
            for (RenglonNotaCredito renglon : renglones) {
                importes[indice] = renglon.getImporteBruto();
                cantidades[indice] = renglon.getCantidad();
                ivaPorcentajeRenglones[indice] = renglon.getIvaPorcentaje();
                ivaNetoRenglones[indice] = renglon.getIvaNeto();
                indice++;
            }
            try {
                txt_Subtotal.setValue(RestClient.getRestTemplate().getForObject("/notas/credito/sub-total?importe="
                        + Arrays.toString(importes).substring(1, Arrays.toString(importes).length() - 1), BigDecimal.class));
                txt_Decuento_porcentaje.setValue(factura.getDescuentoPorcentaje());
                txt_Decuento_neto.setValue(RestClient.getRestTemplate().getForObject("/notas/credito/descuento-neto?subTotal="
                        + txt_Subtotal.getValue().toString()
                        + "&descuentoPorcentaje=" + factura.getDescuentoPorcentaje(), BigDecimal.class));
                txt_Recargo_porcentaje.setValue(factura.getRecargoPorcentaje());
                txt_Recargo_neto.setValue(RestClient.getRestTemplate().getForObject("/notas/credito/recargo-neto?subTotal="
                        + txt_Subtotal.getValue().toString()
                        + "&recargoPorcentaje=" + factura.getRecargoPorcentaje(), BigDecimal.class));
                iva_105_netoFactura = RestClient.getRestTemplate().getForObject("/notas/credito/iva-neto?"
                        + "tipoDeComprobante=" + factura.getTipoComprobante().name()
                        + "&cantidades=" + Arrays.toString(cantidades).substring(1, Arrays.toString(cantidades).length() - 1)
                        + "&ivaPorcentajeRenglones=" + Arrays.toString(ivaPorcentajeRenglones).substring(1, Arrays.toString(ivaPorcentajeRenglones).length() - 1)
                        + "&ivaNetoRenglones=" + Arrays.toString(ivaNetoRenglones).substring(1, Arrays.toString(ivaNetoRenglones).length() - 1)
                        + "&ivaPorcentaje=10.5"
                        + "&descuentoPorcentaje=" + factura.getDescuentoPorcentaje()
                        + "&recargoPorcentaje=" + factura.getRecargoPorcentaje(), BigDecimal.class);
                iva_21_netoFactura = RestClient.getRestTemplate().getForObject("/notas/credito/iva-neto?"
                        + "tipoDeComprobante=" + factura.getTipoComprobante().name()
                        + "&cantidades=" + Arrays.toString(cantidades).substring(1, Arrays.toString(cantidades).length() - 1)
                        + "&ivaPorcentajeRenglones=" + Arrays.toString(ivaPorcentajeRenglones).substring(1, Arrays.toString(ivaPorcentajeRenglones).length() - 1)
                        + "&ivaNetoRenglones=" + Arrays.toString(ivaNetoRenglones).substring(1, Arrays.toString(ivaNetoRenglones).length() - 1)
                        + "&ivaPorcentaje=21"
                        + "&descuentoPorcentaje=" + factura.getDescuentoPorcentaje()
                        + "&recargoPorcentaje=" + factura.getRecargoPorcentaje(), BigDecimal.class);
                txt_IVA105_neto.setValue(iva_105_netoFactura);
                txt_IVA21_neto.setValue(iva_21_netoFactura);
                subTotalBruto = RestClient.getRestTemplate().getForObject("/notas/credito/sub-total-bruto?"
                        + "tipoDeComprobante=" + factura.getTipoComprobante().name()
                        + "&subTotal=" + txt_Subtotal.getValue().toString()
                        + "&recargoNeto=" + txt_Recargo_neto.getValue().toString()
                        + "&descuentoNeto=" + txt_Decuento_neto.getValue().toString()
                        + "&iva105Neto=" + iva_105_netoFactura
                        + "&iva21Neto=" + iva_21_netoFactura, BigDecimal.class);
                txt_Total.setValue(RestClient.getRestTemplate().getForObject("/notas/credito/total?"
                        + "subTotalBruto=" + subTotalBruto
                        + "&iva105Neto=" + iva_105_netoFactura
                        + "&iva21Neto=" + iva_21_netoFactura, BigDecimal.class));
                if (factura.getTipoComprobante() == TipoDeComprobante.FACTURA_B || factura.getTipoComprobante() == TipoDeComprobante.PRESUPUESTO) {
                    txt_IVA105_neto.setValue(BigDecimal.ZERO);
                    txt_IVA21_neto.setValue(BigDecimal.ZERO);
                    txt_SubTotalBruto.setValue(txt_Total.getValue());
                } else {
                    txt_SubTotalBruto.setValue(subTotalBruto);
                }
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            txt_Subtotal.setValue(notaCredito.getSubTotal());
            txt_Decuento_porcentaje.setValue(notaCredito.getDescuentoPorcentaje());
            txt_Decuento_neto.setValue(notaCredito.getDescuentoNeto());
            txt_Recargo_porcentaje.setValue(notaCredito.getRecargoPorcentaje());
            txt_Recargo_neto.setValue(notaCredito.getRecargoNeto());
            txt_IVA105_neto.setValue(notaCredito.getIva105Neto());
            txt_IVA21_neto.setValue(notaCredito.getIva21Neto());
            txt_Total.setValue(notaCredito.getTotal());
            if (notaCredito.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_B || notaCredito.getTipoComprobante() == TipoDeComprobante.NOTA_CREDITO_PRESUPUESTO) {
                txt_IVA105_neto.setValue(BigDecimal.ZERO);
                txt_IVA21_neto.setValue(BigDecimal.ZERO);
                txt_SubTotalBruto.setValue(txt_Total.getValue());
            } else {
                txt_SubTotalBruto.setValue(notaCredito.getSubTotalBruto());
            }
        }
    }

    private void cargarDetalleCliente() {
        txtNombre.setText(((FacturaVenta) factura).getNombreFiscalCliente() + " (" + cliente.getNroCliente() + ")");
        txtUbicacion.setText(cliente.getUbicacionFacturacion().toString());
        if (cliente.getIdFiscal() != null) {
            txtIdFiscal.setText(cliente.getIdFiscal().toString());
        }
        txtCondicionIVA.setText(cliente.getCategoriaIVA().toString());
        lbl_NumComprobante.setVisible(false);
        txt_Serie.setVisible(false);
        txt_Numero.setVisible(false);
        lbl_NumCAE.setVisible(false);
        lblSeparador.setVisible(false);
        txt_CAE.setVisible(false);
        lblFecha.setVisible(false);
        dc_FechaNota.setVisible(false);
    }

    private void cargarDetalleProveedor() {
        txtNombre.setText(((FacturaCompra) factura).getRazonSocialProveedor());
        if (proveedor.getIdUbicacion()!= null) {
            txtUbicacion.setText(proveedor.getDetalleUbicacion());
        }
        if (proveedor.getIdFiscal() != null) {
            txtIdFiscal.setText(proveedor.getIdFiscal().toString());
        }
        txtCondicionIVA.setText(proveedor.getCategoriaIVA().toString());
    }

    private void cargarDetalleNotaCreditoProveedor() {
        Proveedor proveedorDeNota = RestClient.getRestTemplate()
                .getForObject("/proveedores/" + notaCredito.getIdProveedor(), Proveedor.class);
        txtNombre.setText(proveedorDeNota.getRazonSocial());
        if (proveedorDeNota.getIdUbicacion() != null) {
            txtUbicacion.setText(proveedorDeNota.getDetalleUbicacion());
        }
        if (proveedorDeNota.getIdFiscal() != null) {
            txtIdFiscal.setText(proveedorDeNota.getIdFiscal().toString());
        }
        txtCondicionIVA.setText(proveedorDeNota.getCategoriaIVA().toString());
        txt_Serie.setEnabled(false);
        txt_Numero.setEnabled(false);
        cmbMotivo.setEnabled(false);
        txt_Serie.setText(String.valueOf(notaCredito.getSerie()));
        txt_Numero.setText(String.valueOf(notaCredito.getNroNota()));
        txt_CAE.setEnabled(false);
        if (notaCredito.getCAE() == 0L) {
            txt_CAE.setText("");
        } else {
            txt_CAE.setText(String.valueOf(notaCredito.getCAE()));
        }
        cmbMotivo.removeAllItems();
        cmbMotivo.addItem(notaCredito.getMotivo());
        dc_FechaNota.setEnabled(false);
        dc_FechaNota.setDate(notaCredito.getFecha());
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
        tblResultados.setModel(modeloTablaRenglones);
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
        tblResultados.getTableHeader().setReorderingAllowed(false);
        tblResultados.getTableHeader().setResizingAllowed(true);
        //render para los tipos de datos
        tblResultados.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        //Tamanios de columnas
        tblResultados.getColumnModel().getColumn(0).setPreferredWidth(200);
        tblResultados.getColumnModel().getColumn(1).setPreferredWidth(400);
        tblResultados.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblResultados.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblResultados.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblResultados.getColumnModel().getColumn(5).setPreferredWidth(180);
        tblResultados.getColumnModel().getColumn(6).setPreferredWidth(120);
    }

    private void cargarRenglonesAlTable() {
        try {
            if (notaCredito == null) {
                String uri = "/notas/renglon/credito/producto?"
                        + "tipoDeComprobante=" + factura.getTipoComprobante().name()
                        + "&cantidad=" + idsRenglonesYCantidades.values().toString().substring(1, idsRenglonesYCantidades.values().toString().length() - 1)
                        + "&idRenglonFactura=" + idsRenglonesYCantidades.keySet().toString().substring(1, idsRenglonesYCantidades.keySet().toString().length() - 1);
                renglones = Arrays.asList(RestClient.getRestTemplate().getForObject(uri, RenglonNotaCredito[].class));
            } else {
                renglones = Arrays.asList(RestClient.getRestTemplate().getForObject("/notas/renglones/credito/" + notaCredito.getIdNota(), RenglonNotaCredito[].class));
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        renglones.stream().map(r -> {
            Object[] fila = new Object[7];
            fila[0] = r.getCodigoItem();
            fila[1] = r.getDescripcionItem();
            fila[2] = r.getMedidaItem();
            fila[3] = r.getCantidad();
            fila[4] = r.getPrecioUnitario();
            fila[5] = r.getDescuentoPorcentaje();
            fila[6] = r.getImporteBruto();
            return fila;
        }).forEach(fila -> {
            modeloTablaRenglones.addRow(fila);
        });
        tblResultados.setModel(modeloTablaRenglones);
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleNotaCreditoGUI.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void guardar() throws IOException {
        NotaCredito notaCreditoNueva = new NotaCredito();
        notaCreditoNueva.setMotivo(cmbMotivo.getSelectedItem().toString());
        notaCreditoNueva.setSubTotal(new BigDecimal(txt_Subtotal.getValue().toString()));
        notaCreditoNueva.setDescuentoPorcentaje(new BigDecimal(txt_Decuento_porcentaje.getValue().toString()));
        notaCreditoNueva.setDescuentoNeto(new BigDecimal(txt_Decuento_neto.getValue().toString()));
        notaCreditoNueva.setRecargoPorcentaje(new BigDecimal(txt_Recargo_porcentaje.getValue().toString()));
        notaCreditoNueva.setRecargoNeto(new BigDecimal(txt_Recargo_neto.getValue().toString()));
        notaCreditoNueva.setSubTotalBruto(subTotalBruto);
        notaCreditoNueva.setIva105Neto(iva_105_netoFactura);
        notaCreditoNueva.setIva21Neto(iva_21_netoFactura);
        notaCreditoNueva.setTotal(new BigDecimal(txt_Total.getValue().toString()));
        notaCreditoNueva.setRenglonesNotaCredito(renglones);
        String uri = "/notas/credito/empresa/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                + "/usuario/" + UsuarioActivo.getInstance().getUsuario().getId_Usuario()
                + "/factura/" + factura.getId_Factura()
                + "?modificarStock=" + modificarStock;
        if (proveedor != null) {
            Calendar fechaActual = Calendar.getInstance();
            Calendar fechaNotaCredito = Calendar.getInstance();
            fechaNotaCredito.setTime(dc_FechaNota.getDate());
            fechaNotaCredito.set(Calendar.HOUR_OF_DAY, fechaActual.get(Calendar.HOUR_OF_DAY));
            fechaNotaCredito.set(Calendar.MINUTE, fechaActual.get(Calendar.MINUTE));
            fechaNotaCredito.set(Calendar.SECOND, fechaActual.get(Calendar.SECOND));
            notaCreditoNueva.setFecha(fechaNotaCredito.getTime());
            notaCreditoNueva.setSerie(Long.parseLong(txt_Serie.getValue().toString()));
            notaCreditoNueva.setNroNota(Long.parseLong(txt_Numero.getValue().toString()));
            notaCreditoNueva.setCAE(Long.parseLong(txt_CAE.getValue().toString()));
            uri += "&movimiento=" + Movimiento.COMPRA
                    + "&idProveedor=" + proveedor.getId_Proveedor();
        } else if (cliente != null) {
            notaCreditoNueva.setFecha(new Date());
            uri += "&movimiento=" + Movimiento.VENTA
                    + "&idCliente=" + cliente.getId_Cliente();
        }
        NotaCredito nc = RestClient.getRestTemplate()
                .postForObject(uri, notaCreditoNueva, NotaCredito.class);
        if (nc != null) {
            notaCreditoCreada = true;
            boolean FEHabilitada = RestClient.getRestTemplate()
                    .getForObject("/configuraciones-del-sistema/empresas/"
                            + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                            + "/factura-electronica-habilitada", Boolean.class);
            if (cliente != null && FEHabilitada) {
                this.autorizarNotaCredito(nc);
                this.lanzarReporteNotaCredito(nc.getIdNota());
            } else if (cliente != null) {
                this.lanzarReporteNotaCredito(nc.getIdNota());
            } else {
                JOptionPane.showMessageDialog(this, "La Nota se guardó correctamente!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void lanzarReporteNotaCredito(long idNota) throws IOException {
        int reply = JOptionPane.showConfirmDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_reporte"),
                "Aviso", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            if (Desktop.isDesktopSupported()) {
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/notas/" + idNota + "/reporte", byte[].class);
                File f = new File(System.getProperty("user.home") + "/NotaCredito.pdf");
                Files.write(f.toPath(), reporte);
                Desktop.getDesktop().open(f);
            } else {
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_error_plataforma_no_soportada"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void autorizarNotaCredito(NotaCredito notaCredito) {
        if (notaCredito.getTipoComprobante() == TipoDeComprobante.NOTA_CREDITO_A
                || notaCredito.getTipoComprobante() == TipoDeComprobante.NOTA_CREDITO_B) {
            RestClient.getRestTemplate()
                    .postForObject("/notas/" + notaCredito.getIdNota() + "/autorizacion",
                            null, NotaCredito.class);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelCliente = new javax.swing.JPanel();
        lblNombreCliente = new javax.swing.JLabel();
        lblUbicacion = new javax.swing.JLabel();
        lblIDFiscalCliente = new javax.swing.JLabel();
        lblCondicionIVACliente = new javax.swing.JLabel();
        txtCondicionIVA = new javax.swing.JTextField();
        txtIdFiscal = new javax.swing.JTextField();
        txtUbicacion = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        panelDetalle = new javax.swing.JPanel();
        spResultados = new javax.swing.JScrollPane();
        tblResultados = new javax.swing.JTable();
        panelMotivo = new javax.swing.JPanel();
        lblMotivo = new javax.swing.JLabel();
        cmbMotivo = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        panelResultados = new javax.swing.JPanel();
        lbl_SubTotal = new javax.swing.JLabel();
        txt_Subtotal = new javax.swing.JFormattedTextField();
        lbl_IVA22 = new javax.swing.JLabel();
        txt_IVA21_neto = new javax.swing.JFormattedTextField();
        lbl_Total = new javax.swing.JLabel();
        txt_Total = new javax.swing.JFormattedTextField();
        txt_Decuento_porcentaje = new javax.swing.JFormattedTextField();
        txt_Decuento_neto = new javax.swing.JFormattedTextField();
        lbl_DescuentoRecargo = new javax.swing.JLabel();
        txt_SubTotalBruto = new javax.swing.JFormattedTextField();
        lbl_SubTotalBruto = new javax.swing.JLabel();
        txt_IVA105_neto = new javax.swing.JFormattedTextField();
        lbl_IVA105 = new javax.swing.JLabel();
        lbl_105 = new javax.swing.JLabel();
        lbl_22 = new javax.swing.JLabel();
        lbl_recargoPorcentaje = new javax.swing.JLabel();
        txt_Recargo_neto = new javax.swing.JFormattedTextField();
        txt_Recargo_porcentaje = new javax.swing.JFormattedTextField();
        txt_Serie = new javax.swing.JFormattedTextField();
        lbl_NumComprobante = new javax.swing.JLabel();
        txt_Numero = new javax.swing.JFormattedTextField();
        lblSeparador = new javax.swing.JLabel();
        txt_CAE = new javax.swing.JFormattedTextField();
        lbl_NumCAE = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        dc_FechaNota = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lblNombreCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNombreCliente.setText("Nombre:");

        lblUbicacion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblUbicacion.setText("Ubicación:");

        lblIDFiscalCliente.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIDFiscalCliente.setText("CUIT o DNI:");

        lblCondicionIVACliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCondicionIVACliente.setText("Categoria IVA:");

        txtCondicionIVA.setEditable(false);
        txtCondicionIVA.setFocusable(false);

        txtIdFiscal.setEditable(false);
        txtIdFiscal.setFocusable(false);

        txtUbicacion.setEditable(false);
        txtUbicacion.setFocusable(false);

        txtNombre.setEditable(false);
        txtNombre.setFocusable(false);

        javax.swing.GroupLayout panelClienteLayout = new javax.swing.GroupLayout(panelCliente);
        panelCliente.setLayout(panelClienteLayout);
        panelClienteLayout.setHorizontalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUbicacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCondicionIVACliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtUbicacion, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtNombre)
                    .addGroup(panelClienteLayout.createSequentialGroup()
                        .addComponent(txtCondicionIVA)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIDFiscalCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdFiscal)))
                .addContainerGap())
        );
        panelClienteLayout.setVerticalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombreCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUbicacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUbicacion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCondicionIVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCondicionIVACliente)
                    .addComponent(txtIdFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIDFiscalCliente))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDetalle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        spResultados.setViewportView(tblResultados);

        javax.swing.GroupLayout panelDetalleLayout = new javax.swing.GroupLayout(panelDetalle);
        panelDetalle.setLayout(panelDetalleLayout);
        panelDetalleLayout.setHorizontalGroup(
            panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spResultados)
        );
        panelDetalleLayout.setVerticalGroup(
            panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDetalleLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(spResultados, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
        );

        panelMotivo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblMotivo.setText("Motivo:");

        cmbMotivo.setEditable(true);
        cmbMotivo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Devolucion" }));

        javax.swing.GroupLayout panelMotivoLayout = new javax.swing.GroupLayout(panelMotivo);
        panelMotivo.setLayout(panelMotivoLayout);
        panelMotivoLayout.setHorizontalGroup(
            panelMotivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMotivoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMotivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbMotivo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelMotivoLayout.setVerticalGroup(
            panelMotivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMotivoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMotivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblMotivo)
                    .addComponent(cmbMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnGuardar.setForeground(java.awt.Color.blue);
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        lbl_SubTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_SubTotal.setText("SubTotal");

        txt_Subtotal.setEditable(false);
        txt_Subtotal.setForeground(new java.awt.Color(29, 156, 37));
        txt_Subtotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Subtotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Subtotal.setText("0");
        txt_Subtotal.setFocusable(false);
        txt_Subtotal.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        lbl_IVA22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_IVA22.setText("I.V.A.");

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

        txt_Decuento_porcentaje.setEditable(false);
        txt_Decuento_porcentaje.setForeground(new java.awt.Color(29, 156, 37));
        txt_Decuento_porcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Decuento_porcentaje.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Decuento_porcentaje.setText("0");
        txt_Decuento_porcentaje.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        txt_Decuento_neto.setEditable(false);
        txt_Decuento_neto.setForeground(new java.awt.Color(29, 156, 37));
        txt_Decuento_neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Decuento_neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Decuento_neto.setText("0");
        txt_Decuento_neto.setFocusable(false);
        txt_Decuento_neto.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

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

        lbl_22.setText("21 %");

        lbl_recargoPorcentaje.setText("Recargo (%)");

        txt_Recargo_neto.setEditable(false);
        txt_Recargo_neto.setForeground(new java.awt.Color(29, 156, 37));
        txt_Recargo_neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Recargo_neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Recargo_neto.setText("0");
        txt_Recargo_neto.setFocusable(false);
        txt_Recargo_neto.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        txt_Recargo_porcentaje.setEditable(false);
        txt_Recargo_porcentaje.setForeground(new java.awt.Color(29, 156, 37));
        txt_Recargo_porcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Recargo_porcentaje.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Recargo_porcentaje.setText("0");
        txt_Recargo_porcentaje.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

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
                    .addComponent(txt_Decuento_porcentaje, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(txt_Decuento_neto)
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
                    .addComponent(lbl_22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_IVA22, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_Total)
                    .addComponent(lbl_Total, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_SubTotal)
                    .addComponent(lbl_DescuentoRecargo)
                    .addComponent(lbl_recargoPorcentaje)
                    .addComponent(lbl_SubTotalBruto)
                    .addComponent(lbl_IVA105)
                    .addComponent(lbl_IVA22)
                    .addComponent(lbl_Total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_Decuento_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Recargo_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_105, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_22, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_Subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Decuento_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Recargo_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_SubTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_IVA105_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_IVA21_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txt_Total, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        txt_Serie.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_Serie.setValue(0);

        lbl_NumComprobante.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_NumComprobante.setText("Nº de Nota:");

        txt_Numero.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_Numero.setValue(0);

        lblSeparador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSeparador.setText("-");

        txt_CAE.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_CAE.setToolTipText("");
        txt_CAE.setValue(0);

        lbl_NumCAE.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_NumCAE.setText("CAE:");

        lblFecha.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFecha.setText("Fecha:");

        dc_FechaNota.setDateFormatString("dd/MM/yyyy");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(panelMotivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGuardar)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panelCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbl_NumCAE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lbl_NumComprobante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txt_Serie)
                                .addGap(0, 0, 0)
                                .addComponent(lblSeparador, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(txt_Numero, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_CAE)
                            .addComponent(dc_FechaNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txt_Numero, txt_Serie});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFecha)
                    .addComponent(dc_FechaNota, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_NumComprobante)
                    .addComponent(txt_Serie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSeparador)
                    .addComponent(txt_Numero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_NumCAE)
                    .addComponent(txt_CAE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(panelMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {dc_FechaNota, txt_CAE, txt_Numero, txt_Serie});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        try {
            this.guardar();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (notaCreditoCreada) {
            this.dispose();
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            if (idNotaCredito != 0L) {
                this.recuperarNotaCreditoProveedor(idNotaCredito);
                btnGuardar.setVisible(false);
                this.cargarDetalleNotaCreditoProveedor();
                this.setTitle(notaCredito.getTipoComprobante() + " Nº " + notaCredito.getSerie() + " - " + notaCredito.getNroNota()
                        + " con fecha " + formatter.format(notaCredito.getFecha()) + " del Proveedor: " + notaCredito.getRazonSocialProveedor());
            } else {
                factura = RestClient.getRestTemplate().getForObject("/facturas/" + idFactura, Factura.class);
                if (cliente != null) {
                    this.cargarDetalleCliente();
                } else if (proveedor != null) {
                    this.cargarDetalleProveedor();
                    dc_FechaNota.setDate(new Date());
                }
                this.setTitle("Nueva Nota de Credito");
            }
            this.setColumnas();
            this.cargarRenglonesAlTable();
            this.cargarResultados();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> cmbMotivo;
    private com.toedter.calendar.JDateChooser dc_FechaNota;
    private javax.swing.JLabel lblCondicionIVACliente;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblIDFiscalCliente;
    private javax.swing.JLabel lblMotivo;
    private javax.swing.JLabel lblNombreCliente;
    private javax.swing.JLabel lblSeparador;
    private javax.swing.JLabel lblUbicacion;
    private javax.swing.JLabel lbl_105;
    private javax.swing.JLabel lbl_22;
    private javax.swing.JLabel lbl_DescuentoRecargo;
    private javax.swing.JLabel lbl_IVA105;
    private javax.swing.JLabel lbl_IVA22;
    private javax.swing.JLabel lbl_NumCAE;
    private javax.swing.JLabel lbl_NumComprobante;
    private javax.swing.JLabel lbl_SubTotal;
    private javax.swing.JLabel lbl_SubTotalBruto;
    private javax.swing.JLabel lbl_Total;
    private javax.swing.JLabel lbl_recargoPorcentaje;
    private javax.swing.JPanel panelCliente;
    private javax.swing.JPanel panelDetalle;
    private javax.swing.JPanel panelMotivo;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane spResultados;
    private javax.swing.JTable tblResultados;
    private javax.swing.JTextField txtCondicionIVA;
    private javax.swing.JTextField txtIdFiscal;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtUbicacion;
    private javax.swing.JFormattedTextField txt_CAE;
    private javax.swing.JFormattedTextField txt_Decuento_neto;
    private javax.swing.JFormattedTextField txt_Decuento_porcentaje;
    private javax.swing.JFormattedTextField txt_IVA105_neto;
    private javax.swing.JFormattedTextField txt_IVA21_neto;
    private javax.swing.JFormattedTextField txt_Numero;
    private javax.swing.JFormattedTextField txt_Recargo_neto;
    private javax.swing.JFormattedTextField txt_Recargo_porcentaje;
    private javax.swing.JFormattedTextField txt_Serie;
    private javax.swing.JFormattedTextField txt_SubTotalBruto;
    private javax.swing.JFormattedTextField txt_Subtotal;
    private javax.swing.JFormattedTextField txt_Total;
    // End of variables declaration//GEN-END:variables
}
