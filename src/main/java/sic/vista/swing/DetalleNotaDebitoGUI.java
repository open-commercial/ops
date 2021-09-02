package sic.vista.swing;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
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
import sic.modelo.SucursalActiva;
import sic.modelo.NotaDebito;
import sic.modelo.NuevaNotaDebitoDeRecibo;
import sic.modelo.NuevaNotaDebitoSinRecibo;
import sic.modelo.Proveedor;
import sic.modelo.Recibo;
import sic.modelo.RenglonNotaDebito;
import sic.modelo.TipoDeComprobante;
import sic.util.FormatosFechaHora;
import sic.util.FormatterNumero;

public class DetalleNotaDebitoGUI extends JDialog {

    private Cliente cliente;
    private Recibo recibo;
    private Proveedor proveedor;
    private boolean notaDebitoCreada;
    private long idNotaDebito;
    private NotaDebito notaDebito;
    //private final FormatterFechaHora formatter = new FormatterFechaHora(FormatosFechaHora.FORMATO_FECHA_HISPANO);
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleNotaDebitoGUI(NotaDebito notaDebitoCalculada) {
        this.initComponents();
        this.setIcon();
        this.notaDebitoCreada = false;
        this.notaDebito = notaDebitoCalculada;
    }

    public DetalleNotaDebitoGUI(long idNotaDebitoProveedor) {
        this.initComponents();
        this.setIcon();
        this.notaDebitoCreada = false;
        this.idNotaDebito = idNotaDebitoProveedor;
    }

    public boolean isNotaCreada() {
        return notaDebitoCreada;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleNotaDebitoGUI.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarDetalleCliente() {
        txtNombre.setText(cliente.getNombreFiscal() + " (" + cliente.getNroCliente() + ")");
        txtUbicacion.setText(cliente.getUbicacionFacturacion().toString());
        if (cliente.getIdFiscal() != null) {
            txtIdFiscal.setText(cliente.getIdFiscal().toString());
        }
        txtCondicionIVA.setText(cliente.getCategoriaIVA().toString());
        this.setVisibleFalseComponentesDeCompra();
    }
    
    private void desactivarComponentesRecibo() {
        lblDetallePago.setVisible(false);
        lblMontoPago.setVisible(false);
        lblIVADetallePago.setVisible(false);
        lblIVAnetoDetallePago.setVisible(false);
        lblImportePago.setVisible(false);
    }

    private void setVisibleFalseComponentesDeCompra() {
        lblFecha.setVisible(false);
        dcFechaNota.setVisible(false);
        lbl_NumComprobante.setVisible(false);
        txt_Serie.setVisible(false);
        lbl_separador.setVisible(false);
        txt_Numero.setVisible(false);
        lbl_NumCAE.setVisible(false);
        txt_CAE.setVisible(false);
    }

    private void cargarDetalleProveedor() {
        txtNombre.setText(proveedor.getRazonSocial());
        if (proveedor.getUbicacion() != null) {
            txtUbicacion.setText(proveedor.getUbicacion().toString());
        }
        if (proveedor.getIdFiscal() != null) {
            txtIdFiscal.setText(proveedor.getIdFiscal().toString());
        }
        txtCondicionIVA.setText(proveedor.getCategoriaIVA().toString());
    }

    private void cargarDetalleRecibo() {
        lblDetallePago.setText("Nº Recibo " + recibo.getNumSerie() + "-" + recibo.getNumRecibo() + ": " + recibo.getConcepto());
        lblMontoPago.setText("$" + FormatterNumero.formatConRedondeo(recibo.getMonto()));
        lblImportePago.setText("$" + FormatterNumero.formatConRedondeo(recibo.getMonto()));
    }

    private void cargarNotaDeDebitoCalculada() {
        this.setTitle("Nueva " + notaDebito.getTipoComprobante());
        lblMontoRenglon2.setText("$" + String.valueOf(FormatterNumero
                .formatConRedondeo(notaDebito.getRenglonesNotaDebito()
                        .get(notaDebito.getRenglonesNotaDebito().size() - 1).getImporteBruto())));
        if (notaDebito.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_C
                || notaDebito.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_X) {
            lblIVA21.setText("-");
            lblIvaNetoRenglon2.setText("-");
        } else {
            lblIvaNetoRenglon2.setText("$" + String.valueOf(FormatterNumero.formatConRedondeo(notaDebito.getRenglonesNotaDebito()
                    .get(notaDebito.getRenglonesNotaDebito().size() - 1).getIvaNeto())));
        }
        lblImporteRenglon2.setText("$" + String.valueOf(FormatterNumero.formatConRedondeo(notaDebito.getRenglonesNotaDebito()
                .get(notaDebito.getRenglonesNotaDebito().size() - 1).getImporteNeto())));
        txtSubTotalBruto.setValue(notaDebito.getSubTotalBruto());
        txtIVA21Neto.setValue(notaDebito.getIva21Neto());
        txtNoGravado.setValue(notaDebito.getMontoNoGravado());
        txtTotal.setValue(notaDebito.getTotal());
        if (notaDebito.getIdCliente() != null) {
            this.setVisibleFalseComponentesDeCompra();
            cliente = RestClient.getRestTemplate().getForObject("/clientes/" + notaDebito.getIdCliente(), Cliente.class);
            this.cargarDetalleCliente();
        } else if (notaDebito.getIdProveedor() != null) {
            proveedor = RestClient.getRestTemplate().getForObject("/proveedores/" + notaDebito.getIdProveedor(), Proveedor.class);
            this.cargarDetalleProveedor();
            dcFechaNota.setDate(new Date());
        }
    }

    private void guardar() throws IOException {                   
        NotaDebito nd;
            if (recibo == null) {
                NuevaNotaDebitoSinRecibo nuevaNotaCreditoSinRecibo = NuevaNotaDebitoSinRecibo
                        .builder()
                        .idCliente(cliente != null ? cliente.getIdCliente() : null)
                        .idProveedor(proveedor != null ? proveedor.getIdProveedor() : null)
                        .gastoAdministrativo(notaDebito.getRenglonesNotaDebito().get(1).getImporteNeto())
                        .tipoDeComprobante(notaDebito.getTipoComprobante())
                        .idSucursal(SucursalActiva.getInstance().getSucursal().getIdSucursal())
                        .motivo(cmbDescripcionRenglon2.getSelectedItem().toString())
                        .build();
                nd = RestClient.getRestTemplate().postForObject("/notas/debito/sin-recibo", nuevaNotaCreditoSinRecibo, NotaDebito.class);
            } else {
                NuevaNotaDebitoDeRecibo nuevaNotaDebitoDeRecibo = NuevaNotaDebitoDeRecibo
                        .builder()
                        .idRecibo(recibo.getIdRecibo())
                        .gastoAdministrativo(notaDebito.getRenglonesNotaDebito().get(1).getImporteNeto())
                        .tipoDeComprobante(notaDebito.getTipoComprobante())
                        .motivo(cmbDescripcionRenglon2.getSelectedItem().toString())
                        .build();
                nd = RestClient.getRestTemplate().postForObject("/notas/debito", nuevaNotaDebitoDeRecibo, NotaDebito.class);
            } 
        if (nd != null) {
            notaDebitoCreada = true;
            boolean FEHabilitada = RestClient.getRestTemplate().getForObject("/configuraciones-sucursal/"
                    + SucursalActiva.getInstance().getSucursal().getIdSucursal()
                    + "/factura-electronica-habilitada", Boolean.class);
            if (cliente != null && FEHabilitada) {
                if (this.autorizarNotaDebito(nd)) {
                    this.lanzarReporteNotaCredito(nd.getIdNota());
                }
            } else if (cliente != null) {
                this.lanzarReporteNotaCredito(nd.getIdNota());
            } else {
                JOptionPane.showMessageDialog(this, "La Nota se guardó correctamente!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
            this.dispose();
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

    private boolean autorizarNotaDebito(NotaDebito notaDebito) {
        if (notaDebito.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_A
                || notaDebito.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_B
                || notaDebito.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_C) {
            notaDebito = RestClient.getRestTemplate()
                    .postForObject("/notas/" + notaDebito.getIdNota() + "/autorizacion",
                            null, NotaDebito.class);
            return notaDebito.getCae() != 0L;
        }
        return false;
    }

    private void cargarDetalleNotaDebitoProveedor() {
        notaDebito = RestClient.getRestTemplate().getForObject("/notas/" + idNotaDebito, NotaDebito.class);
        Proveedor proveedorDeNota = RestClient.getRestTemplate()
                .getForObject("/proveedores/" + notaDebito.getIdProveedor(), Proveedor.class);
        this.setTitle(notaDebito.getTipoComprobante() + " Nº " + notaDebito.getSerie() + " - " + notaDebito.getNroNota()
                + " con fecha " + FormatosFechaHora.formatoFecha(notaDebito.getFecha(), FormatosFechaHora.FORMATO_FECHAHORA_HISPANO) + " del Proveedor: " + proveedorDeNota.getRazonSocial());
        dcFechaNota.setEnabled(false);
        txt_Serie.setEnabled(false);
        txt_Numero.setEnabled(false);
        txt_CAE.setEnabled(false);
        ZonedDateTime zdt = notaDebito.getFecha().atZone(ZoneId.systemDefault());
        dcFechaNota.setDate(Date.from(zdt.toInstant()));
        txt_Serie.setText(String.valueOf(notaDebito.getSerie()));
        txt_Numero.setText(String.valueOf(notaDebito.getNroNota()));
        txt_CAE.setText(String.valueOf(notaDebito.getCae()));
        if (notaDebito.getCae() == 0L) {
            txt_CAE.setText("");
        } else {
            txt_CAE.setText(String.valueOf(notaDebito.getCae()));
        }
        txtNombre.setText(proveedorDeNota.getRazonSocial());
        cmbDescripcionRenglon2.removeAllItems();
        cmbDescripcionRenglon2.addItem(notaDebito.getMotivo());
        if (proveedorDeNota.getUbicacion() != null) {
            txtUbicacion.setText(proveedorDeNota.getUbicacion().toString());
        }
        txtCondicionIVA.setText(proveedorDeNota.getCategoriaIVA().toString());
        if (proveedorDeNota.getIdFiscal() != null) {
            txtIdFiscal.setText(proveedorDeNota.getIdFiscal().toString());
        }
        if (notaDebito.getIdRecibo() != null) {
            Recibo reciboNotaDebitoProveedor = RestClient.getRestTemplate().getForObject("/recibos/" + notaDebito.getIdRecibo(), Recibo.class);
            lblDetallePago.setText("Nº Recibo: " + reciboNotaDebitoProveedor.getNumSerie() + " - " + reciboNotaDebitoProveedor.getNumRecibo()
                    + " - " + reciboNotaDebitoProveedor.getConcepto());
            lblMontoPago.setText("$" + FormatterNumero.formatConRedondeo(reciboNotaDebitoProveedor.getMonto()));
            lblImportePago.setText("$" + FormatterNumero.formatConRedondeo(reciboNotaDebitoProveedor.getMonto()));
        } else {
            this.desactivarComponentesRecibo();
        }
        List<RenglonNotaDebito> renglonesNotaDebito = Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/notas/renglones/debito/" + idNotaDebito, RenglonNotaDebito[].class));
        lblMontoRenglon2.setText("$"
                + String.valueOf(FormatterNumero.formatConRedondeo(renglonesNotaDebito.get(renglonesNotaDebito.size() - 1)
                        .getImporteBruto())));
        lblIvaNetoRenglon2.setText("$" + String.valueOf(FormatterNumero.formatConRedondeo(renglonesNotaDebito.get(renglonesNotaDebito.size() - 1).getIvaNeto())));
        lblImporteRenglon2.setText("$" + String.valueOf(FormatterNumero.formatConRedondeo(renglonesNotaDebito.get(renglonesNotaDebito.size() - 1).getImporteNeto())));
        txtSubTotalBruto.setValue(notaDebito.getSubTotalBruto());
        txtIVA21Neto.setValue(notaDebito.getIva21Neto());
        txtNoGravado.setValue(notaDebito.getMontoNoGravado());
        txtTotal.setValue(notaDebito.getTotal());
        cmbDescripcionRenglon2.setEnabled(false);
        btnGuardar.setVisible(false);
        if (notaDebito.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_C
                || notaDebito.getTipoComprobante() == TipoDeComprobante.NOTA_DEBITO_X) {
            lblIVA21.setText("-");
            lblIvaNetoRenglon2.setText("-");
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
        lblDescripcion = new javax.swing.JLabel();
        lblIvaPorcentaje = new javax.swing.JLabel();
        lblImporteEncabezado = new javax.swing.JLabel();
        lblIvaEncabezado = new javax.swing.JLabel();
        lblIVAnetoDetallePago = new javax.swing.JLabel();
        lblImportePago = new javax.swing.JLabel();
        lbl_Monto = new javax.swing.JLabel();
        lblIvaNetoRenglon2 = new javax.swing.JLabel();
        lblImporteRenglon2 = new javax.swing.JLabel();
        lblGastoAdministrativo = new javax.swing.JLabel();
        lblIVA21 = new javax.swing.JLabel();
        lblDetallePago = new javax.swing.JLabel();
        lblMontoPago = new javax.swing.JLabel();
        lblIVADetallePago = new javax.swing.JLabel();
        lblMontoRenglon2 = new javax.swing.JLabel();
        panelResultados = new javax.swing.JPanel();
        lbl_IVA105 = new javax.swing.JLabel();
        txtIVA21Neto = new javax.swing.JFormattedTextField();
        lblTotal = new javax.swing.JLabel();
        txtTotal = new javax.swing.JFormattedTextField();
        txtSubTotalBruto = new javax.swing.JFormattedTextField();
        lblSubTotalBruto = new javax.swing.JLabel();
        lblIva21 = new javax.swing.JLabel();
        lblNoGravado = new javax.swing.JLabel();
        txtNoGravado = new javax.swing.JFormattedTextField();
        btnGuardar = new javax.swing.JButton();
        panelMotivo = new javax.swing.JPanel();
        lblMotivo = new javax.swing.JLabel();
        cmbDescripcionRenglon2 = new javax.swing.JComboBox<>();
        panelEncabezado = new javax.swing.JPanel();
        lblFecha = new javax.swing.JLabel();
        dcFechaNota = new com.toedter.calendar.JDateChooser();
        lbl_NumComprobante = new javax.swing.JLabel();
        txt_Serie = new javax.swing.JFormattedTextField();
        lbl_separador = new javax.swing.JLabel();
        txt_Numero = new javax.swing.JFormattedTextField();
        lbl_NumCAE = new javax.swing.JLabel();
        txt_CAE = new javax.swing.JFormattedTextField();

        setModal(true);
        setResizable(false);
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelClienteLayout.createSequentialGroup()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUbicacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCondicionIVACliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelClienteLayout.createSequentialGroup()
                        .addComponent(txtCondicionIVA, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIDFiscalCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtUbicacion)
                    .addComponent(txtNombre))
                .addContainerGap())
        );
        panelClienteLayout.setVerticalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addContainerGap()
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

        lblDescripcion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDescripcion.setText("Descripcion");

        lblIvaPorcentaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIvaPorcentaje.setText("% IVA");

        lblImporteEncabezado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImporteEncabezado.setText("Importe");

        lblIvaEncabezado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIvaEncabezado.setText("IVA");

        lblIVAnetoDetallePago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIVAnetoDetallePago.setText("-");

        lblImportePago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImportePago.setText("$0");

        lbl_Monto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_Monto.setText("Monto");

        lblIvaNetoRenglon2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIvaNetoRenglon2.setText("$0");

        lblImporteRenglon2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImporteRenglon2.setText("$0");

        lblGastoAdministrativo.setText("Gasto Administrativo");

        lblIVA21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIVA21.setText("21%");

        lblDetallePago.setText("nroRecibo + nota");

        lblMontoPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMontoPago.setText("$0");

        lblIVADetallePago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIVADetallePago.setText("-");

        lblMontoRenglon2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMontoRenglon2.setText("$0");

        javax.swing.GroupLayout panelDetalleLayout = new javax.swing.GroupLayout(panelDetalle);
        panelDetalle.setLayout(panelDetalleLayout);
        panelDetalleLayout.setHorizontalGroup(
            panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescripcion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGastoAdministrativo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDetallePago, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Monto, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(lblMontoPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMontoRenglon2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblIvaPorcentaje, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                    .addComponent(lblIVA21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblIVADetallePago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblIvaEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(lblIVAnetoDetallePago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblIvaNetoRenglon2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblImporteRenglon2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblImporteEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                    .addComponent(lblImportePago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelDetalleLayout.setVerticalGroup(
            panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDescripcion)
                    .addComponent(lbl_Monto)
                    .addComponent(lblIvaPorcentaje)
                    .addComponent(lblIvaEncabezado)
                    .addComponent(lblImporteEncabezado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDetallePago)
                    .addComponent(lblMontoPago)
                    .addComponent(lblIVADetallePago)
                    .addComponent(lblIVAnetoDetallePago)
                    .addComponent(lblImportePago))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblMontoRenglon2)
                    .addComponent(lblGastoAdministrativo)
                    .addComponent(lblIVA21)
                    .addComponent(lblIvaNetoRenglon2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblImporteRenglon2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDetalleLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblIVAnetoDetallePago, lblImportePago});

        panelResultados.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_IVA105.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_IVA105.setText("I.V.A.");

        txtIVA21Neto.setEditable(false);
        txtIVA21Neto.setForeground(new java.awt.Color(29, 156, 37));
        txtIVA21Neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtIVA21Neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIVA21Neto.setFocusable(false);
        txtIVA21Neto.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        txtIVA21Neto.setValue(0);

        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotal.setText("TOTAL");

        txtTotal.setEditable(false);
        txtTotal.setForeground(new java.awt.Color(29, 156, 37));
        txtTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setFocusable(false);
        txtTotal.setFont(new java.awt.Font("DejaVu Sans", 1, 30)); // NOI18N
        txtTotal.setValue(0);

        txtSubTotalBruto.setEditable(false);
        txtSubTotalBruto.setForeground(new java.awt.Color(29, 156, 37));
        txtSubTotalBruto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtSubTotalBruto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSubTotalBruto.setFocusable(false);
        txtSubTotalBruto.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        txtSubTotalBruto.setValue(0);

        lblSubTotalBruto.setText("SubTotal Bruto");

        lblIva21.setText("21 %");

        lblNoGravado.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNoGravado.setText("No Gravado");

        txtNoGravado.setEditable(false);
        txtNoGravado.setForeground(new java.awt.Color(29, 156, 37));
        txtNoGravado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtNoGravado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNoGravado.setFocusable(false);
        txtNoGravado.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        txtNoGravado.setValue(0);

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSubTotalBruto)
                    .addComponent(lblSubTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtIVA21Neto)
                    .addComponent(lblIva21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_IVA105, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNoGravado)
                    .addComponent(lblNoGravado, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotal)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblIva21, lblNoGravado, lblSubTotalBruto, lbl_IVA105, txtIVA21Neto, txtNoGravado, txtSubTotalBruto});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblSubTotalBruto)
                    .addComponent(lbl_IVA105)
                    .addComponent(lblNoGravado)
                    .addComponent(lblTotal))
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelResultadosLayout.createSequentialGroup()
                                .addComponent(lblIva21, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtIVA21Neto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNoGravado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(txtSubTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        btnGuardar.setForeground(java.awt.Color.blue);
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        panelMotivo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblMotivo.setText("Motivo:");

        cmbDescripcionRenglon2.setEditable(true);
        cmbDescripcionRenglon2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cheque Rechazado - Sin Fondos", "Cheque Rechazado - Cuenta Embargada", "Cheque Rechazado", "Irregularidad Cadena de Endosos" }));

        javax.swing.GroupLayout panelMotivoLayout = new javax.swing.GroupLayout(panelMotivo);
        panelMotivo.setLayout(panelMotivoLayout);
        panelMotivoLayout.setHorizontalGroup(
            panelMotivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMotivoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMotivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbDescripcionRenglon2, javax.swing.GroupLayout.PREFERRED_SIZE, 746, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelMotivoLayout.setVerticalGroup(
            panelMotivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMotivoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMotivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblMotivo)
                    .addComponent(cmbDescripcionRenglon2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblFecha.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFecha.setText("Fecha:");

        dcFechaNota.setDateFormatString("dd/MM/yyyy");

        lbl_NumComprobante.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_NumComprobante.setText("Nº de Nota:");

        txt_Serie.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_Serie.setValue(0);

        lbl_separador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_separador.setText("-");

        txt_Numero.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_Numero.setValue(0);

        lbl_NumCAE.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_NumCAE.setText("CAE:");

        txt_CAE.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_CAE.setToolTipText("");
        txt_CAE.setValue(0);

        javax.swing.GroupLayout panelEncabezadoLayout = new javax.swing.GroupLayout(panelEncabezado);
        panelEncabezado.setLayout(panelEncabezadoLayout);
        panelEncabezadoLayout.setHorizontalGroup(
            panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_NumCAE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_NumComprobante, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dcFechaNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEncabezadoLayout.createSequentialGroup()
                        .addComponent(txt_Serie)
                        .addGap(0, 0, 0)
                        .addComponent(lbl_separador, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(txt_Numero, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_CAE, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelEncabezadoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txt_Numero, txt_Serie});

        panelEncabezadoLayout.setVerticalGroup(
            panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezadoLayout.createSequentialGroup()
                .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFecha)
                    .addComponent(dcFechaNota, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_NumComprobante)
                    .addComponent(txt_Serie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_separador)
                    .addComponent(txt_Numero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_NumCAE)
                    .addComponent(txt_CAE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        panelEncabezadoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {dcFechaNota, txt_CAE, txt_Numero, txt_Serie});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGuardar))
                    .addComponent(panelMotivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelEncabezado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(panelCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnGuardar, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        try {
            this.guardar();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
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
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            if (notaDebito != null) {
                this.cargarNotaDeDebitoCalculada();
                if (notaDebito.getIdRecibo() != null) {
                    recibo = RestClient.getRestTemplate().getForObject("/recibos/" + notaDebito.getIdRecibo(), Recibo.class);
                    this.cargarDetalleRecibo();
                } else {
                    this.desactivarComponentesRecibo();
                }
            } else {
                this.cargarDetalleNotaDebitoProveedor();
                this.setTitle(notaDebito.getTipoComprobante() + " Nº " + notaDebito.getSerie() + " - " + notaDebito.getNroNota()
                        + " con fecha " + FormatosFechaHora.formatoFecha(notaDebito.getFecha(), FormatosFechaHora.FORMATO_FECHAHORA_HISPANO) + " del Proveedor: " + notaDebito.getRazonSocialProveedor());
            }
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
    private javax.swing.JComboBox<String> cmbDescripcionRenglon2;
    private com.toedter.calendar.JDateChooser dcFechaNota;
    private javax.swing.JLabel lblCondicionIVACliente;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblDetallePago;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblGastoAdministrativo;
    private javax.swing.JLabel lblIDFiscalCliente;
    private javax.swing.JLabel lblIVA21;
    private javax.swing.JLabel lblIVADetallePago;
    private javax.swing.JLabel lblIVAnetoDetallePago;
    private javax.swing.JLabel lblImporteEncabezado;
    private javax.swing.JLabel lblImportePago;
    private javax.swing.JLabel lblImporteRenglon2;
    private javax.swing.JLabel lblIva21;
    private javax.swing.JLabel lblIvaEncabezado;
    private javax.swing.JLabel lblIvaNetoRenglon2;
    private javax.swing.JLabel lblIvaPorcentaje;
    private javax.swing.JLabel lblMontoPago;
    private javax.swing.JLabel lblMontoRenglon2;
    private javax.swing.JLabel lblMotivo;
    private javax.swing.JLabel lblNoGravado;
    private javax.swing.JLabel lblNombreCliente;
    private javax.swing.JLabel lblSubTotalBruto;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblUbicacion;
    private javax.swing.JLabel lbl_IVA105;
    private javax.swing.JLabel lbl_Monto;
    private javax.swing.JLabel lbl_NumCAE;
    private javax.swing.JLabel lbl_NumComprobante;
    private javax.swing.JLabel lbl_separador;
    private javax.swing.JPanel panelCliente;
    private javax.swing.JPanel panelDetalle;
    private javax.swing.JPanel panelEncabezado;
    private javax.swing.JPanel panelMotivo;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JTextField txtCondicionIVA;
    private javax.swing.JFormattedTextField txtIVA21Neto;
    private javax.swing.JTextField txtIdFiscal;
    private javax.swing.JFormattedTextField txtNoGravado;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JFormattedTextField txtSubTotalBruto;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JTextField txtUbicacion;
    private javax.swing.JFormattedTextField txt_CAE;
    private javax.swing.JFormattedTextField txt_Numero;
    private javax.swing.JFormattedTextField txt_Serie;
    // End of variables declaration//GEN-END:variables
}
