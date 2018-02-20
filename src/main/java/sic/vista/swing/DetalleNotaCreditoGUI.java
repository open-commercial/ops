package sic.vista.swing;

import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.modelo.FacturaVenta;
import sic.modelo.NotaCredito;
import sic.modelo.RenglonNotaCredito;
import sic.modelo.TipoDeComprobante;
import sic.modelo.UsuarioActivo;
import sic.util.RenderTabla;

public class DetalleNotaCreditoGUI extends JDialog {

    private final ModeloTabla modeloTablaRenglones = new ModeloTabla();
    private final boolean modificarStock;
    private FacturaVenta fv;
    private Cliente cliente;
    private HashMap<Long,BigDecimal> idsRenglonesYCantidades = new HashMap<>();
    private List<RenglonNotaCredito> renglones;
    private BigDecimal subTotalBruto;
    private boolean notaCreada;    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    public DetalleNotaCreditoGUI(HashMap<Long,BigDecimal> idsRenglonesYCantidades, long idFacturaVenta,
            boolean modificarStock, long idCliente) {
        this.initComponents();
        this.setIcon();
        this.idsRenglonesYCantidades = idsRenglonesYCantidades;
        this.modificarStock = modificarStock;
        this.notaCreada = false;
        this.recuperarFactura(idFacturaVenta);
        this.recuperarCliente(idCliente);
    }
    
    public boolean isNotaCreada() {
        return notaCreada;
    }
    
    private void recuperarFactura(long idFactura) {
        try {
            fv = RestClient.getRestTemplate().getForObject("/facturas/" + idFactura, FacturaVenta.class);            
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void recuperarCliente(long idCliente) {
        try {
            cliente = RestClient.getRestTemplate().getForObject("/clientes/" + idCliente, Cliente.class);            
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
        //SubTotal  
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
        try{
        txt_Subtotal.setValue(RestClient.getRestTemplate().getForObject("/notas/credito/sub-total?importe="
                + Arrays.toString(importes).substring(1, Arrays.toString(importes).length() - 1), BigDecimal.class));
        txt_Decuento_porcentaje.setValue(fv.getDescuento_porcentaje());
        txt_Decuento_neto.setValue(RestClient.getRestTemplate().getForObject("/notas/credito/descuento-neto?subTotal="
                + txt_Subtotal.getValue().toString()
                + "&descuentoPorcentaje=" + fv.getDescuento_porcentaje(), BigDecimal.class));
        txt_Recargo_porcentaje.setValue(fv.getRecargo_porcentaje());
        txt_Recargo_neto.setValue(RestClient.getRestTemplate().getForObject("/notas/credito/recargo-neto?subTotal="
                + txt_Subtotal.getValue().toString()
                + "&recargoPorcentaje=" + fv.getRecargo_porcentaje(), BigDecimal.class));
        if (fv.getTipoComprobante() == TipoDeComprobante.FACTURA_X) {
                txt_IVA105_neto.setValue(0.0);
                txt_IVA21_neto.setValue(0.0);
            } else {
        txt_IVA105_neto.setValue(RestClient.getRestTemplate().getForObject("/notas/credito/iva-neto?"
                + "tipoDeComprobante=" + fv.getTipoComprobante().name()
                + "&cantidades=" + Arrays.toString(cantidades).substring(1, Arrays.toString(cantidades).length() - 1)
                + "&ivaPorcentajeRenglones="
                + Arrays.toString(ivaPorcentajeRenglones).substring(1, Arrays.toString(ivaPorcentajeRenglones).length() - 1)
                + "&ivaNetoRenglones="
                + Arrays.toString(ivaNetoRenglones).substring(1, Arrays.toString(ivaNetoRenglones).length() - 1)
                + "&ivaPorcentaje=10.5" 
                + "&descuentoPorcentaje=" + fv.getDescuento_porcentaje()
                + "&recargoPorcentaje=" + fv.getRecargo_porcentaje()
                , BigDecimal.class));
        txt_IVA21_neto.setValue(RestClient.getRestTemplate().getForObject("/notas/credito/iva-neto?"
                + "tipoDeComprobante=" + fv.getTipoComprobante().name()
                + "&cantidades=" + Arrays.toString(cantidades).substring(1, Arrays.toString(cantidades).length() - 1)
                + "&ivaPorcentajeRenglones="
                + Arrays.toString(ivaPorcentajeRenglones).substring(1, Arrays.toString(ivaPorcentajeRenglones).length() - 1)
                + "&ivaNetoRenglones="
                + Arrays.toString(ivaNetoRenglones).substring(1, Arrays.toString(ivaNetoRenglones).length() - 1)
                + "&ivaPorcentaje=21"
                + "&descuentoPorcentaje=" + fv.getDescuento_porcentaje()
                + "&recargoPorcentaje=" + fv.getRecargo_porcentaje()
                , BigDecimal.class));
        }
        subTotalBruto = RestClient.getRestTemplate().getForObject("/notas/credito/sub-total-bruto?"
                + "tipoDeComprobante=" + fv.getTipoComprobante().name()
                + "&subTotal=" + txt_Subtotal.getValue().toString()
                + "&recargoNeto=" + txt_Recargo_neto.getValue().toString()
                + "&descuentoNeto=" + txt_Decuento_neto.getValue().toString()
                + "&iva105Neto=" + txt_IVA105_neto.getValue().toString()
                + "&iva21Neto=" + txt_IVA21_neto.getValue().toString(),
                BigDecimal.class);
        txt_Total.setValue(RestClient.getRestTemplate().getForObject("/notas/credito/total"
                + "?subTotalBruto=" + subTotalBruto
                + "&iva105Neto=" + txt_IVA105_neto.getValue().toString()
                + "&iva21Neto=" + txt_IVA21_neto.getValue().toString(), 
                BigDecimal.class));
        if (fv.getTipoComprobante() == TipoDeComprobante.FACTURA_B || fv.getTipoComprobante() == TipoDeComprobante.PRESUPUESTO) {
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
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDetalleCliente() {
        txtNombreCliente.setText(fv.getRazonSocialCliente());
        txtDomicilioCliente.setText(cliente.getDireccion()
                + " " + cliente.getLocalidad().getNombre()
                + " " + cliente.getLocalidad().getProvincia().getNombre()
                + " " + cliente.getLocalidad().getProvincia().getPais());
        txtIDFiscalCliente.setText(cliente.getIdFiscal());
        txtCondicionIVACliente.setText(cliente.getCondicionIVA().getNombre());
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
        tblResultados.setDefaultRenderer(BigDecimal.class, new RenderTabla());

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
        String uri = "/notas/renglon/credito/producto?"
                 + "tipoDeComprobante=" + fv.getTipoComprobante().name()
                 + "&cantidad="  
                 + idsRenglonesYCantidades.values().toString().substring(1, idsRenglonesYCantidades.values().toString().length() - 1)
                 + "&idRenglonFactura=" 
                 + idsRenglonesYCantidades.keySet().toString().substring(1, idsRenglonesYCantidades.keySet().toString().length() - 1);
        try {
            renglones = Arrays.asList(RestClient.getRestTemplate().getForObject(uri, RenglonNotaCredito[].class));
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
            fila[6] = r.getImporte();
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
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelCliente = new javax.swing.JPanel();
        lblNombreCliente = new javax.swing.JLabel();
        lblDomicilioCliente = new javax.swing.JLabel();
        lblIDFiscalCliente = new javax.swing.JLabel();
        lblCondicionIVACliente = new javax.swing.JLabel();
        txtCondicionIVACliente = new javax.swing.JTextField();
        txtIDFiscalCliente = new javax.swing.JTextField();
        txtDomicilioCliente = new javax.swing.JTextField();
        txtNombreCliente = new javax.swing.JTextField();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lblNombreCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNombreCliente.setText("Nombre:");

        lblDomicilioCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDomicilioCliente.setText("Domicilio:");

        lblIDFiscalCliente.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIDFiscalCliente.setText("ID Fiscal:");

        lblCondicionIVACliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCondicionIVACliente.setText("Condición IVA:");

        txtCondicionIVACliente.setEditable(false);
        txtCondicionIVACliente.setFocusable(false);

        txtIDFiscalCliente.setEditable(false);
        txtIDFiscalCliente.setFocusable(false);

        txtDomicilioCliente.setEditable(false);
        txtDomicilioCliente.setFocusable(false);

        txtNombreCliente.setEditable(false);
        txtNombreCliente.setFocusable(false);

        javax.swing.GroupLayout panelClienteLayout = new javax.swing.GroupLayout(panelCliente);
        panelCliente.setLayout(panelClienteLayout);
        panelClienteLayout.setHorizontalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDomicilioCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCondicionIVACliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDomicilioCliente, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtNombreCliente)
                    .addGroup(panelClienteLayout.createSequentialGroup()
                        .addComponent(txtCondicionIVACliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIDFiscalCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIDFiscalCliente)))
                .addContainerGap())
        );
        panelClienteLayout.setVerticalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombreCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDomicilioCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDomicilioCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCondicionIVACliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCondicionIVACliente)
                    .addComponent(txtIDFiscalCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIDFiscalCliente)))
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
            .addComponent(spResultados)
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
        txt_Decuento_porcentaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Decuento_porcentajeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Decuento_porcentajeFocusLost(evt);
            }
        });
        txt_Decuento_porcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_Decuento_porcentajeActionPerformed(evt);
            }
        });
        txt_Decuento_porcentaje.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_Decuento_porcentajeKeyTyped(evt);
            }
        });

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(panelMotivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGuardar)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(panelMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        NotaCredito notaCredito = new NotaCredito();
        notaCredito.setFecha(new Date());
        notaCredito.setMotivo(cmbMotivo.getSelectedItem().toString());
        notaCredito.setFacturaVenta(fv);
        notaCredito.setSubTotal(new BigDecimal(txt_Subtotal.getValue().toString()));
        notaCredito.setDescuentoPorcentaje(new BigDecimal(txt_Decuento_porcentaje.getValue().toString()));
        notaCredito.setDescuentoNeto(new BigDecimal(txt_Decuento_neto.getValue().toString()));
        notaCredito.setRecargoPorcentaje(new BigDecimal(txt_Recargo_porcentaje.getValue().toString()));
        notaCredito.setRecargoNeto(new BigDecimal(txt_Recargo_neto.getValue().toString()));
        notaCredito.setSubTotalBruto(subTotalBruto);
        notaCredito.setIva21Neto(new BigDecimal(txt_IVA21_neto.getValue().toString()));
        notaCredito.setIva105Neto(new BigDecimal(txt_IVA105_neto.getValue().toString()));
        notaCredito.setTotal(new BigDecimal(txt_Total.getValue().toString()));
        notaCredito.setRenglonesNotaCredito(renglones);
        try {
            NotaCredito nc = RestClient.getRestTemplate().postForObject("/notas/credito/empresa/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                    + "/cliente/" + cliente.getId_Cliente()
                    + "/usuario/" + UsuarioActivo.getInstance().getUsuario().getId_Usuario()
                    + "/factura/" + fv.getId_Factura()
                    + "?modificarStock=" + modificarStock,
                     notaCredito, NotaCredito.class);
            if (nc != null) {
                int reply = JOptionPane.showConfirmDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_reporte"),
                        "Aviso", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    if (Desktop.isDesktopSupported()) {
                        byte[] reporte = RestClient.getRestTemplate()
                                .getForObject("/notas/" + nc.getIdNota() + "/reporte",
                                        byte[].class);
                        File f = new File(System.getProperty("user.home") + "/NotaCredito.pdf");
                        Files.write(f.toPath(), reporte);
                        Desktop.getDesktop().open(f);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            notaCreada = true;
            this.dispose();
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
    }//GEN-LAST:event_btnGuardarActionPerformed
      
    private void txt_Decuento_porcentajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Decuento_porcentajeFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_Decuento_porcentaje.selectAll();
        });
    }//GEN-LAST:event_txt_Decuento_porcentajeFocusGained

    private void txt_Decuento_porcentajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Decuento_porcentajeFocusLost
        
    }//GEN-LAST:event_txt_Decuento_porcentajeFocusLost

    private void txt_Decuento_porcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_Decuento_porcentajeActionPerformed
        
    }//GEN-LAST:event_txt_Decuento_porcentajeActionPerformed

    private void txt_Decuento_porcentajeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_Decuento_porcentajeKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_Decuento_porcentajeKeyTyped

    private void txt_Recargo_porcentajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Recargo_porcentajeFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_Recargo_porcentaje.selectAll();
        });
    }//GEN-LAST:event_txt_Recargo_porcentajeFocusGained

    private void txt_Recargo_porcentajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Recargo_porcentajeFocusLost
        
    }//GEN-LAST:event_txt_Recargo_porcentajeFocusLost

    private void txt_Recargo_porcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_Recargo_porcentajeActionPerformed
        
    }//GEN-LAST:event_txt_Recargo_porcentajeActionPerformed

    private void txt_Recargo_porcentajeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_Recargo_porcentajeKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_Recargo_porcentajeKeyTyped

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.cargarDetalleCliente();
        this.setColumnas();
        this.cargarRenglonesAlTable();
        this.cargarResultados();
        this.setTitle("Nueva Nota de Credito");
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> cmbMotivo;
    private javax.swing.JLabel lblCondicionIVACliente;
    private javax.swing.JLabel lblDomicilioCliente;
    private javax.swing.JLabel lblIDFiscalCliente;
    private javax.swing.JLabel lblMotivo;
    private javax.swing.JLabel lblNombreCliente;
    private javax.swing.JLabel lbl_105;
    private javax.swing.JLabel lbl_22;
    private javax.swing.JLabel lbl_DescuentoRecargo;
    private javax.swing.JLabel lbl_IVA105;
    private javax.swing.JLabel lbl_IVA22;
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
    private javax.swing.JTextField txtCondicionIVACliente;
    private javax.swing.JTextField txtDomicilioCliente;
    private javax.swing.JTextField txtIDFiscalCliente;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JFormattedTextField txt_Decuento_neto;
    private javax.swing.JFormattedTextField txt_Decuento_porcentaje;
    private javax.swing.JFormattedTextField txt_IVA105_neto;
    private javax.swing.JFormattedTextField txt_IVA21_neto;
    private javax.swing.JFormattedTextField txt_Recargo_neto;
    private javax.swing.JFormattedTextField txt_Recargo_porcentaje;
    private javax.swing.JFormattedTextField txt_SubTotalBruto;
    private javax.swing.JFormattedTextField txt_Subtotal;
    private javax.swing.JFormattedTextField txt_Total;
    // End of variables declaration//GEN-END:variables
}
