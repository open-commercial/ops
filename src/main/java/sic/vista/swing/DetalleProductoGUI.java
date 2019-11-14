package sic.vista.swing;

import java.awt.Image;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.CantidadEnSucursal;
import sic.modelo.SucursalActiva;
import sic.modelo.Medida;
import sic.modelo.NuevoProducto;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Rol;
import sic.modelo.Rubro;
import sic.modelo.Sucursal;
import sic.modelo.TipoDeOperacion;
import sic.modelo.UsuarioActivo;
import sic.util.CalculosPrecioProducto;
import sic.util.FiltroImagenes;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class DetalleProductoGUI extends JDialog {

    private byte[] imagenProducto = null;
    private boolean cambioImagen = false;
    private final int anchoImagenContainer = 416;
    private final int altoImagenContainer = 312; 
    private Producto productoParaModificar;
    private final TipoDeOperacion operacion;        
    private List<Medida> medidas;
    private List<Rubro> rubros;  
    private List<Sucursal> sucursales;
    private Map<Long,BigDecimal> cantidadEnSucursal;
    private Proveedor proveedorSeleccionado;
    private BigDecimal precioListaAnterior = BigDecimal.ZERO;
    private final static BigDecimal IVA_21 = new BigDecimal("21");	
    private final static BigDecimal IVA_105 = new BigDecimal("10.5");
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleProductoGUI() {
        this.initComponents();
        this.setIcon();                
        operacion = TipoDeOperacion.ALTA;
    }       

    public DetalleProductoGUI(Producto producto) {
        this.initComponents();
        this.setIcon();                
        operacion = TipoDeOperacion.ACTUALIZACION;
        productoParaModificar = producto;                
    }            
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleProductoGUI.class.getResource("/sic/icons/Product_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }
    
    private void calcularPrecioBonificado(BigDecimal precioDeLista) {
        txtPrecioBonificado.setValue(precioDeLista
                .multiply((new BigDecimal("100")).subtract(new BigDecimal(txtPorcentajeOferta.getValue().toString()))
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)));
    }
    
    private void calcularPorcentajeOferta(BigDecimal precioBonificado, BigDecimal precioDeLista) {
        txtPorcentajeOferta.setValue((new BigDecimal("100")).subtract(precioBonificado.multiply(new BigDecimal("100")).divide(precioDeLista, 2, RoundingMode.HALF_UP)));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgVisibilidad = new javax.swing.ButtonGroup();
        btnGuardar = new javax.swing.JButton();
        tpTabs = new javax.swing.JTabbedPane();
        panelGeneral = new javax.swing.JPanel();
        panelSuperior = new javax.swing.JPanel();
        lbl_Codigo = new javax.swing.JLabel();
        lbl_Descripcion = new javax.swing.JLabel();
        txt_Descripcion = new javax.swing.JTextField();
        txt_Codigo = new javax.swing.JTextField();
        lbl_Rubro = new javax.swing.JLabel();
        cmb_Rubro = new javax.swing.JComboBox();
        lbl_Proveedor = new javax.swing.JLabel();
        btn_NuevoProveedor = new javax.swing.JButton();
        cmb_Medida = new javax.swing.JComboBox();
        lbl_Medida = new javax.swing.JLabel();
        btnBuscarProveedor = new javax.swing.JButton();
        txtProveedor = new javax.swing.JTextField();
        panelPrecios = new javax.swing.JPanel();
        lbl_PrecioCosto = new javax.swing.JLabel();
        lbl_Ganancia = new javax.swing.JLabel();
        lbl_PrecioLista = new javax.swing.JLabel();
        txtPrecioCosto = new javax.swing.JFormattedTextField();
        txtGananciaPorcentaje = new javax.swing.JFormattedTextField();
        txtPrecioLista = new javax.swing.JFormattedTextField();
        lbl_IVA = new javax.swing.JLabel();
        txtIVANeto = new javax.swing.JFormattedTextField();
        txtGananciaNeto = new javax.swing.JFormattedTextField();
        lbl_PVP = new javax.swing.JLabel();
        txtPVP = new javax.swing.JFormattedTextField();
        cmbIVAPorcentaje = new javax.swing.JComboBox();
        chkOferta = new javax.swing.JCheckBox();
        txtPorcentajeOferta = new javax.swing.JFormattedTextField();
        txtPrecioBonificado = new javax.swing.JFormattedTextField();
        panelCantidades = new javax.swing.JPanel();
        chkSinLimite = new javax.swing.JCheckBox();
        txt_CantMinima = new javax.swing.JFormattedTextField();
        lbl_CantMinima = new javax.swing.JLabel();
        lbl_Bulto = new javax.swing.JLabel();
        txt_Bulto = new javax.swing.JFormattedTextField();
        lblSinLimite = new javax.swing.JLabel();
        pnlCantidadSucursales = new javax.swing.JPanel();
        lblSucursal = new javax.swing.JLabel();
        cmbSucursales = new javax.swing.JComboBox<>();
        txtCantidad = new javax.swing.JFormattedTextField();
        lbl_Cantidad = new javax.swing.JLabel();
        panelPropiedades = new javax.swing.JPanel();
        panel5 = new javax.swing.JPanel();
        lbl_Ven = new javax.swing.JLabel();
        lbl_Estanteria = new javax.swing.JLabel();
        txt_Estanteria = new javax.swing.JTextField();
        lbl_Estante = new javax.swing.JLabel();
        txt_Estante = new javax.swing.JTextField();
        dc_Vencimiento = new com.toedter.calendar.JDateChooser();
        lbl_Nota = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_Nota = new javax.swing.JTextArea();
        lblPublico = new javax.swing.JLabel();
        rbPublico = new javax.swing.JRadioButton();
        rbPrivado = new javax.swing.JRadioButton();
        panel6 = new javax.swing.JPanel();
        lbl_FUM = new javax.swing.JLabel();
        lbl_FechaUltimaModificacion = new javax.swing.JLabel();
        lbl_FA = new javax.swing.JLabel();
        lbl_FechaAlta = new javax.swing.JLabel();
        panelImagen = new javax.swing.JPanel();
        lbl_imagen = new javax.swing.JLabel();
        btn_EliminarImagen = new javax.swing.JButton();
        btn_ExaminarArchivos = new javax.swing.JButton();
        lblAspectRatio = new javax.swing.JLabel();
        lblTamanioMax = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btnGuardar.setForeground(java.awt.Color.blue);
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        panelSuperior.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_Codigo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Codigo.setText("Código:");

        lbl_Descripcion.setForeground(java.awt.Color.red);
        lbl_Descripcion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Descripcion.setText("* Descripción:");

        lbl_Rubro.setForeground(java.awt.Color.red);
        lbl_Rubro.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Rubro.setText("* Rubro:");

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

        lbl_Medida.setForeground(java.awt.Color.red);
        lbl_Medida.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Medida.setText("* Medida:");

        btnBuscarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProveedorActionPerformed(evt);
            }
        });

        txtProveedor.setEditable(false);
        txtProveedor.setOpaque(false);

        javax.swing.GroupLayout panelSuperiorLayout = new javax.swing.GroupLayout(panelSuperior);
        panelSuperior.setLayout(panelSuperiorLayout);
        panelSuperiorLayout.setHorizontalGroup(
            panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSuperiorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Codigo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Proveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Descripcion, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                    .addComponent(lbl_Medida, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Rubro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_Codigo)
                    .addComponent(txt_Descripcion)
                    .addGroup(panelSuperiorLayout.createSequentialGroup()
                        .addComponent(txtProveedor)
                        .addGap(0, 0, 0)
                        .addComponent(btnBuscarProveedor)
                        .addGap(0, 0, 0)
                        .addComponent(btn_NuevoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cmb_Medida, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_Rubro, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelSuperiorLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnBuscarProveedor, btn_NuevoProveedor});

        panelSuperiorLayout.setVerticalGroup(
            panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSuperiorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Codigo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Descripcion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Proveedor)
                    .addComponent(btn_NuevoProveedor)
                    .addComponent(btnBuscarProveedor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmb_Medida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Medida))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmb_Rubro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Rubro))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelSuperiorLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarProveedor, btn_NuevoProveedor, cmb_Medida, cmb_Rubro, txtProveedor});

        panelSuperiorLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txt_Codigo, txt_Descripcion});

        panelPrecios.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_PrecioCosto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PrecioCosto.setText("Precio de Costo:");

        lbl_Ganancia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Ganancia.setText("Ganancia (%):");

        lbl_PrecioLista.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PrecioLista.setText("Precio de Lista:");

        txtPrecioCosto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPrecioCosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioCosto.setText("0");
        txtPrecioCosto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPrecioCostoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPrecioCostoFocusLost(evt);
            }
        });
        txtPrecioCosto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioCostoActionPerformed(evt);
            }
        });

        txtGananciaPorcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtGananciaPorcentaje.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGananciaPorcentaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtGananciaPorcentajeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtGananciaPorcentajeFocusLost(evt);
            }
        });
        txtGananciaPorcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGananciaPorcentajeActionPerformed(evt);
            }
        });

        txtPrecioLista.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPrecioLista.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioLista.setText("0");
        txtPrecioLista.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPrecioListaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPrecioListaFocusLost(evt);
            }
        });
        txtPrecioLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioListaActionPerformed(evt);
            }
        });

        lbl_IVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_IVA.setText("I.V.A. (%):");

        txtIVANeto.setEditable(false);
        txtIVANeto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtIVANeto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIVANeto.setText("0");
        txtIVANeto.setFocusable(false);

        txtGananciaNeto.setEditable(false);
        txtGananciaNeto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtGananciaNeto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGananciaNeto.setText("0");
        txtGananciaNeto.setFocusable(false);

        lbl_PVP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PVP.setText("Precio Venta Público:");

        txtPVP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPVP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPVP.setText("0");
        txtPVP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPVPFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPVPFocusLost(evt);
            }
        });
        txtPVP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPVPActionPerformed(evt);
            }
        });

        cmbIVAPorcentaje.setModel(new javax.swing.DefaultComboBoxModel(new BigDecimal[] { BigDecimal.ZERO, IVA_105, IVA_21 }));
        cmbIVAPorcentaje.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbIVAPorcentajeItemStateChanged(evt);
            }
        });

        chkOferta.setText("Oferta (%):");
        chkOferta.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        chkOferta.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        chkOferta.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkOfertaItemStateChanged(evt);
            }
        });
        chkOferta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOfertaActionPerformed(evt);
            }
        });

        txtPorcentajeOferta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPorcentajeOferta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPorcentajeOferta.setText("0");
        txtPorcentajeOferta.setEnabled(false);
        txtPorcentajeOferta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPorcentajeOfertaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPorcentajeOfertaFocusLost(evt);
            }
        });
        txtPorcentajeOferta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPorcentajeOfertaActionPerformed(evt);
            }
        });

        txtPrecioBonificado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPrecioBonificado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioBonificado.setText("0");
        txtPrecioBonificado.setEnabled(false);
        txtPrecioBonificado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPrecioBonificadoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPrecioBonificadoFocusLost(evt);
            }
        });
        txtPrecioBonificado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioBonificadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPreciosLayout = new javax.swing.GroupLayout(panelPrecios);
        panelPrecios.setLayout(panelPreciosLayout);
        panelPreciosLayout.setHorizontalGroup(
            panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreciosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_PrecioCosto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Ganancia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_PVP, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_IVA, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_PrecioLista, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkOferta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtGananciaPorcentaje)
                    .addComponent(cmbIVAPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPorcentajeOferta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPrecioCosto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .addComponent(txtGananciaNeto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                    .addComponent(txtPVP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                    .addComponent(txtIVANeto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                    .addComponent(txtPrecioLista, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(txtPrecioBonificado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelPreciosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtGananciaNeto, txtIVANeto, txtPVP, txtPrecioCosto, txtPrecioLista});

        panelPreciosLayout.setVerticalGroup(
            panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreciosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PrecioCosto)
                    .addComponent(txtPrecioCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Ganancia)
                    .addComponent(txtGananciaPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGananciaNeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PVP)
                    .addComponent(txtPVP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtIVANeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbIVAPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_IVA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PrecioLista)
                    .addComponent(txtPrecioLista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkOferta, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPorcentajeOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioBonificado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        panelPreciosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtGananciaNeto, txtGananciaPorcentaje, txtIVANeto, txtPVP, txtPrecioCosto, txtPrecioLista});

        panelPreciosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chkOferta, lbl_Ganancia, lbl_IVA, lbl_PVP, lbl_PrecioCosto, lbl_PrecioLista});

        panelCantidades.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkSinLimite.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chkSinLimite.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkSinLimiteItemStateChanged(evt);
            }
        });

        txt_CantMinima.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_CantMinima.setText("0");
        txt_CantMinima.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_CantMinimaFocusGained(evt);
            }
        });

        lbl_CantMinima.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_CantMinima.setText("Cant. Mínima:");

        lbl_Bulto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Bulto.setText("Cant. x Bulto:");

        txt_Bulto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat(""))));
        txt_Bulto.setText("0");
        txt_Bulto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_BultoFocusGained(evt);
            }
        });

        lblSinLimite.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSinLimite.setText("Sin Límite:");

        javax.swing.GroupLayout panelCantidadesLayout = new javax.swing.GroupLayout(panelCantidades);
        panelCantidades.setLayout(panelCantidadesLayout);
        panelCantidadesLayout.setHorizontalGroup(
            panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCantidadesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCantidadesLayout.createSequentialGroup()
                        .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_CantMinima, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSinLimite, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkSinLimite, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                            .addComponent(txt_CantMinima)))
                    .addGroup(panelCantidadesLayout.createSequentialGroup()
                        .addComponent(lbl_Bulto, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_Bulto)))
                .addGap(12, 12, 12))
        );
        panelCantidadesLayout.setVerticalGroup(
            panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCantidadesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblSinLimite)
                    .addComponent(chkSinLimite))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_CantMinima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_CantMinima))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Bulto)
                    .addComponent(txt_Bulto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCantidadSucursales.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblSucursal.setText("Sucursal:");

        cmbSucursales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSucursalesActionPerformed(evt);
            }
        });

        txtCantidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtCantidad.setText("0");
        txtCantidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCantidadFocusGained(evt);
            }
        });
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCantidadKeyReleased(evt);
            }
        });

        lbl_Cantidad.setForeground(java.awt.Color.red);
        lbl_Cantidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Cantidad.setText("Cantidad:");

        javax.swing.GroupLayout pnlCantidadSucursalesLayout = new javax.swing.GroupLayout(pnlCantidadSucursales);
        pnlCantidadSucursales.setLayout(pnlCantidadSucursalesLayout);
        pnlCantidadSucursalesLayout.setHorizontalGroup(
            pnlCantidadSucursalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCantidadSucursalesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCantidadSucursalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Cantidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCantidadSucursalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCantidad)
                    .addComponent(cmbSucursales, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlCantidadSucursalesLayout.setVerticalGroup(
            pnlCantidadSucursalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCantidadSucursalesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCantidadSucursalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSucursal)
                    .addComponent(cmbSucursales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCantidadSucursalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Cantidad))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCantidadSucursalesLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblSucursal, lbl_Cantidad});

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addComponent(panelPrecios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelCantidades, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlCantidadSucursales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSuperior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addComponent(pnlCantidadSucursales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelCantidades, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panelPrecios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        tpTabs.addTab("General", panelGeneral);

        panel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_Ven.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Ven.setText("Vencimiento:");

        lbl_Estanteria.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Estanteria.setText("Estantería:");

        lbl_Estante.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Estante.setText("Estante:");

        dc_Vencimiento.setDateFormatString("dd/MM/yyyy");

        lbl_Nota.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nota.setText("Nota:");

        txt_Nota.setColumns(20);
        txt_Nota.setLineWrap(true);
        txt_Nota.setRows(5);
        jScrollPane1.setViewportView(txt_Nota);

        lblPublico.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPublico.setText("Visibilidad:");

        bgVisibilidad.add(rbPublico);
        rbPublico.setText("Público");

        bgVisibilidad.add(rbPrivado);
        rbPrivado.setText("Privado");
        rbPrivado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPrivadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel5Layout = new javax.swing.GroupLayout(panel5);
        panel5.setLayout(panel5Layout);
        panel5Layout.setHorizontalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Estante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPublico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Estanteria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Ven, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Nota, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_Estanteria)
                    .addComponent(dc_Vencimiento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_Estante)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                    .addGroup(panel5Layout.createSequentialGroup()
                        .addComponent(rbPublico)
                        .addGap(8, 8, 8)
                        .addComponent(rbPrivado)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panel5Layout.setVerticalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPublico)
                    .addComponent(rbPublico)
                    .addComponent(rbPrivado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Ven)
                    .addComponent(dc_Vencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Estanteria)
                    .addComponent(txt_Estanteria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Estante)
                    .addComponent(txt_Estante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Nota)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {rbPrivado, rbPublico});

        panel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_FUM.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_FUM.setText("Última Modificación:");

        lbl_FechaUltimaModificacion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        lbl_FA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_FA.setText("Fecha de Alta:");

        lbl_FechaAlta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout panel6Layout = new javax.swing.GroupLayout(panel6);
        panel6.setLayout(panel6Layout);
        panel6Layout.setHorizontalGroup(
            panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_FA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_FUM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_FechaUltimaModificacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_FechaAlta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(7, 7, 7))
        );
        panel6Layout.setVerticalGroup(
            panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_FUM)
                    .addComponent(lbl_FechaUltimaModificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_FA)
                    .addComponent(lbl_FechaAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelPropiedadesLayout = new javax.swing.GroupLayout(panelPropiedades);
        panelPropiedades.setLayout(panelPropiedadesLayout);
        panelPropiedadesLayout.setHorizontalGroup(
            panelPropiedadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPropiedadesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPropiedadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPropiedadesLayout.setVerticalGroup(
            panelPropiedadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPropiedadesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(326, 326, 326))
        );

        tpTabs.addTab("Propiedades", panelPropiedades);

        lbl_imagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_imagen.setText("SIN IMAGEN");
        lbl_imagen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbl_imagen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btn_EliminarImagen.setForeground(java.awt.Color.blue);
        btn_EliminarImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/RemovePicture_16x16.png"))); // NOI18N
        btn_EliminarImagen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarImagenActionPerformed(evt);
            }
        });

        btn_ExaminarArchivos.setForeground(java.awt.Color.blue);
        btn_ExaminarArchivos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddPicture_16x16.png"))); // NOI18N
        btn_ExaminarArchivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ExaminarArchivosActionPerformed(evt);
            }
        });

        lblAspectRatio.setForeground(java.awt.Color.gray);
        lblAspectRatio.setText("Relacion de aspecto 4:3");

        lblTamanioMax.setForeground(java.awt.Color.gray);
        lblTamanioMax.setText("Maximo 1MB");

        javax.swing.GroupLayout panelImagenLayout = new javax.swing.GroupLayout(panelImagen);
        panelImagen.setLayout(panelImagenLayout);
        panelImagenLayout.setHorizontalGroup(
            panelImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImagenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_imagen, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btn_ExaminarArchivos, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btn_EliminarImagen, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(lblAspectRatio)
                    .addComponent(lblTamanioMax))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelImagenLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_EliminarImagen, btn_ExaminarArchivos});

        panelImagenLayout.setVerticalGroup(
            panelImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImagenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_imagen, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelImagenLayout.createSequentialGroup()
                        .addComponent(btn_ExaminarArchivos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_EliminarImagen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTamanioMax)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAspectRatio)))
                .addGap(27, 27, 27))
        );

        tpTabs.addTab("Imagen", panelImagen);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGuardar)
                .addContainerGap())
            .addComponent(tpTabs)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tpTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cargarProductoParaModificar() {
        txt_Codigo.setText(productoParaModificar.getCodigo());
        txt_Descripcion.setText(productoParaModificar.getDescripcion());
        txt_Nota.setText(productoParaModificar.getNota());
        cmb_Medida.setSelectedItem(productoParaModificar.getNombreMedida());
        chkSinLimite.setSelected(productoParaModificar.isIlimitado());
        rbPublico.setSelected(productoParaModificar.isPublico());
        rbPrivado.setSelected(!productoParaModificar.isPublico());
        cmbSucursales.setSelectedItem(SucursalActiva.getInstance().getSucursal());
        productoParaModificar.getCantidadEnSucursales().forEach(cantidadesEnSucursal -> {
            cantidadEnSucursal.put(cantidadesEnSucursal.idSucursal, cantidadesEnSucursal.getCantidad());
            if (cantidadesEnSucursal.getIdSucursal().equals(SucursalActiva.getInstance().getSucursal().getIdSucursal())) {
                txtCantidad.setValue(cantidadesEnSucursal.getCantidad());
            }
        });
        txt_CantMinima.setValue(productoParaModificar.getCantMinima());
        txt_Bulto.setValue(productoParaModificar.getBulto());
        cmb_Rubro.setSelectedItem(productoParaModificar.getNombreRubro());
        txtProveedor.setText(productoParaModificar.getRazonSocialProveedor());     
        lbl_FechaUltimaModificacion.setText(FormatosFechaHora.formatoFecha(productoParaModificar.getFechaUltimaModificacion(), FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
        lbl_FechaAlta.setText(FormatosFechaHora.formatoFecha(productoParaModificar.getFechaAlta(), FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
        if (productoParaModificar.getFechaVencimiento() != null) {
            Date fVencimiento = Date.from(productoParaModificar.getFechaVencimiento().atStartOfDay(ZoneId.systemDefault()).toInstant());
            dc_Vencimiento.setDate(fVencimiento);
        }
        txt_Estanteria.setText(productoParaModificar.getEstanteria());
        txt_Estante.setText(productoParaModificar.getEstante());        
        txtPrecioCosto.setValue(productoParaModificar.getPrecioCosto());        
        txtGananciaPorcentaje.setValue(productoParaModificar.getGananciaPorcentaje());        
        txtGananciaNeto.setValue(productoParaModificar.getGananciaNeto());        
        txtPVP.setValue(productoParaModificar.getPrecioVentaPublico());        
        cmbIVAPorcentaje.setSelectedItem(productoParaModificar.getIvaPorcentaje().stripTrailingZeros());        
        txtIVANeto.setValue(productoParaModificar.getIvaNeto());        
        txtPrecioLista.setValue(productoParaModificar.getPrecioLista());
        if (productoParaModificar.isOferta()) {
            chkOferta.setSelected(true);
            txtPorcentajeOferta.setValue(productoParaModificar.getPorcentajeBonificacionOferta());
            txtPrecioBonificado.setValue(productoParaModificar.getPrecioListaBonificado() != null 
                    ? productoParaModificar.getPrecioListaBonificado() : BigDecimal.ZERO);
            this.calcularPrecioBonificado(productoParaModificar.getPrecioLista());
        } else {
            txtPorcentajeOferta.setValue(BigDecimal.ZERO);
            txtPrecioBonificado.setEnabled(false);
            txtPrecioBonificado.setValue(BigDecimal.ZERO);
        }         
        if (productoParaModificar.getUrlImagen() == null || "".equals(productoParaModificar.getUrlImagen())) {
            lbl_imagen.setText("SIN IMAGEN");
            imagenProducto = null;
        } else {
            lbl_imagen.setText("");
            Image image = null;
            try {
                URL url = new URL(productoParaModificar.getUrlImagen());
                image = ImageIO.read(url);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_error_al_recuperar_imagen"),
                        "Error", JOptionPane.ERROR_MESSAGE);
                this.dispose();
            }
            ImageIcon imagenLogo = new ImageIcon(image);
            ImageIcon logoRedimensionado = new ImageIcon(imagenLogo.getImage()
                    .getScaledInstance(anchoImagenContainer, altoImagenContainer, Image.SCALE_SMOOTH));
            lbl_imagen.setIcon(logoRedimensionado);
        }
    }

    private void prepararComponentes() {
        txtCantidad.setValue(BigDecimal.ZERO);
        txt_CantMinima.setValue(BigDecimal.ZERO);
        txt_Bulto.setValue(BigDecimal.ONE);
        txtPrecioCosto.setValue(BigDecimal.ZERO);
        txtPVP.setValue(BigDecimal.ZERO);
        txtIVANeto.setValue(BigDecimal.ZERO);
        txtGananciaPorcentaje.setValue(BigDecimal.ZERO);
        txtGananciaNeto.setValue(BigDecimal.ZERO);
        txtPrecioLista.setValue(BigDecimal.ZERO);
        rbPrivado.setSelected(true);
        txtPorcentajeOferta.setValue(BigDecimal.ZERO);
        txtPrecioBonificado.setEnabled(false);
    }
    
    private void cargarMedidas() {
        cmb_Medida.removeAllItems();
        try {
            medidas = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/medidas",
                            Medida[].class)));
            medidas.stream().forEach(m -> cmb_Medida.addItem(m.getNombre()));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarRubros() {
        cmb_Rubro.removeAllItems();
        try {
            rubros = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/rubros",
                            Rubro[].class)));
            rubros.stream().forEach(r -> cmb_Rubro.addItem(r.getNombre()));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarSucursales() {
        this.cantidadEnSucursal = new HashMap<>();
        cmbSucursales.removeAllItems();
        try {
            sucursales = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/sucursales",
                            Sucursal[].class)));
            sucursales.stream().forEach(s -> cmbSucursales.addItem(s));
            cmbSucursales.setSelectedItem(SucursalActiva.getInstance().getSucursal());
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarYRecargarComponentes() {
        txt_Codigo.setText("");
        txt_Descripcion.setText("");
        txtCantidad.setValue(BigDecimal.ZERO);
        txt_CantMinima.setValue(BigDecimal.ZERO);
        txt_Bulto.setValue(BigDecimal.ZERO);
        chkSinLimite.setSelected(false);
        txtPrecioCosto.setValue(BigDecimal.ZERO);
        txtPVP.setValue(BigDecimal.ZERO);
        txtIVANeto.setValue(BigDecimal.ZERO);
        txtGananciaPorcentaje.setValue(BigDecimal.ZERO);
        txtGananciaNeto.setValue(BigDecimal.ZERO);
        txtPrecioLista.setValue(BigDecimal.ZERO);
        txt_Estanteria.setText("");
        txt_Estante.setText("");
        txt_Nota.setText("");
        dc_Vencimiento.setDate(null);
    }
    
    private void calcularGananciaPorcentaje() {
        BigDecimal gananciaPorcentaje = CalculosPrecioProducto.calcularGananciaPorcentaje(BigDecimal.ZERO,
                BigDecimal.ZERO, new BigDecimal(txtPVP.getValue().toString()), BigDecimal.ZERO,
                new BigDecimal(txtPrecioCosto.getValue().toString()), false);
        txtGananciaPorcentaje.setValue(gananciaPorcentaje);
    }

    private void calcularGananciaNeto() {        
        BigDecimal gananciaNeto = CalculosPrecioProducto.calcularGananciaNeto(new BigDecimal(txtPrecioCosto.getValue().toString()),
                new BigDecimal(txtGananciaPorcentaje.getValue().toString()));
        txtGananciaNeto.setValue(gananciaNeto);
    }
    
    private void calcularPVP() {                
        BigDecimal pvp = CalculosPrecioProducto.calcularPVP(new BigDecimal(txtPrecioCosto.getValue().toString()),
                new BigDecimal(txtGananciaPorcentaje.getValue().toString()));
        txtPVP.setValue(pvp);
    }
    
    private void calcularIVANeto() {
        BigDecimal IVANeto = CalculosPrecioProducto.calcularIVANeto(new BigDecimal(txtPVP.getValue().toString()),
                new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()));
        txtIVANeto.setValue(IVANeto);
    }

    private void calcularPrecioLista() {
        BigDecimal precioDeLista = CalculosPrecioProducto.calcularPrecioLista(new BigDecimal(txtPVP.getValue().toString()),
                new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()));
        txtPrecioLista.setValue(precioDeLista);
    }

    private void calcularGananciaPorcentajeSegunPrecioDeLista() {
        BigDecimal gananciaPorcentaje = CalculosPrecioProducto.calcularGananciaPorcentaje(
                new BigDecimal(txtPrecioLista.getValue().toString()), precioListaAnterior,
                new BigDecimal(txtPVP.getValue().toString()),
                new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()),
                new BigDecimal(txtPrecioCosto.getValue().toString()), true);
        txtGananciaPorcentaje.setValue(gananciaPorcentaje);
        precioListaAnterior = new BigDecimal(txtPrecioLista.getValue().toString());
    }

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        Long idMedida = null;
        Long idRubro = null;
        Long idProveedor = null;
        boolean ejecutarOperacion = true;
        String mensajeError = "";
        for (Medida m : medidas) {
            if (m.getNombre().equals(cmb_Medida.getSelectedItem())) {
                idMedida = m.getIdMedida();
            }
        }
        for (Rubro r : rubros) {
            if (r.getNombre().equals(cmb_Rubro.getSelectedItem())) {
                idRubro = r.getIdRubro();
            }
        }
        if (idMedida == null) {
            mensajeError = mensajeError.concat(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_medida") + "\n");
            ejecutarOperacion = false;
        }
        if (idRubro == null) {
            mensajeError = mensajeError.concat(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_rubro") + "\n");
            ejecutarOperacion = false;
        }
        if (operacion == TipoDeOperacion.ALTA) {
            if (proveedorSeleccionado == null) {
                mensajeError = mensajeError.concat(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_producto_vacio_proveedor") + "\n");
                ejecutarOperacion = false;
            } else {
                idProveedor = proveedorSeleccionado.getIdProveedor();
            }
        } else if (operacion == TipoDeOperacion.ACTUALIZACION && proveedorSeleccionado != null) {
            idProveedor = proveedorSeleccionado.getIdProveedor();
        }
        if (ejecutarOperacion) {
            try {
                if (operacion == TipoDeOperacion.ALTA) {
                    NuevoProducto producto = new NuevoProducto();
                    producto.setCodigo(txt_Codigo.getText());
                    producto.setDescripcion(txt_Descripcion.getText().trim());
                    producto.setCantidadEnSucursal(cantidadEnSucursal);
                    producto.setCantMinima(new BigDecimal(txt_CantMinima.getValue().toString()));
                    producto.setBulto(new BigDecimal(txt_Bulto.getValue().toString()));
                    producto.setPrecioCosto(new BigDecimal(txtPrecioCosto.getValue().toString()));
                    producto.setGananciaPorcentaje(new BigDecimal(txtGananciaPorcentaje.getValue().toString()));
                    producto.setGananciaNeto(new BigDecimal(txtGananciaNeto.getValue().toString()));
                    producto.setPrecioVentaPublico(new BigDecimal(txtPVP.getValue().toString()));
                    producto.setIvaPorcentaje(new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()));
                    producto.setIvaNeto(new BigDecimal(txtIVANeto.getValue().toString()));
                    producto.setPrecioLista(new BigDecimal(txtPrecioLista.getValue().toString()));
                    producto.setIlimitado(chkSinLimite.isSelected());
                    producto.setPublico(rbPublico.isSelected());
                    producto.setOferta(chkOferta.isSelected());
                    producto.setPorcentajeBonificacionOferta(new BigDecimal(txtPorcentajeOferta.getValue().toString()));
                    producto.setEstanteria(txt_Estanteria.getText().trim());
                    producto.setEstante(txt_Estante.getText().trim());
                    producto.setNota(txt_Nota.getText().trim());
                    if (dc_Vencimiento.getDate() != null) {
                        producto.setFechaVencimiento(dc_Vencimiento.getDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate());
                    }
                    Producto productoRecuperado = RestClient.getRestTemplate().postForObject("/productos?idMedida=" + idMedida
                            + "&idRubro=" + idRubro
                            + "&idProveedor=" + ((idProveedor != null) ? idProveedor : "")
                            + "&idSucursal=" + SucursalActiva.getInstance().getSucursal().getIdSucursal(),
                            producto, Producto.class);
                    LOGGER.warn("El producto " + productoRecuperado + " se guardó correctamente");
                    if (imagenProducto != null) {
                        RestClient.getRestTemplate()
                                .postForObject("/productos/" + productoRecuperado.getIdProducto() + "/imagenes", imagenProducto, String.class);
                    }
                    int respuesta = JOptionPane.showConfirmDialog(this,
                            "El producto se guardó correctamente.\n¿Desea dar de alta otro producto?",
                            "Aviso", JOptionPane.YES_NO_OPTION);
                    this.limpiarYRecargarComponentes();
                    if (respuesta == JOptionPane.NO_OPTION) {
                        this.dispose();
                    }
                }
                if (operacion == TipoDeOperacion.ACTUALIZACION) {
                    productoParaModificar.setCodigo(txt_Codigo.getText());
                    productoParaModificar.setDescripcion(txt_Descripcion.getText().trim());
                    productoParaModificar.setCantMinima(new BigDecimal(txt_CantMinima.getValue().toString()));
                    List<CantidadEnSucursal> cantidadesNuevas = new ArrayList<>();
                    cantidadEnSucursal.keySet().forEach(idSucursal -> {
                        boolean crearNuevaCantidadEnSucursal = true;
                        for (CantidadEnSucursal cant : productoParaModificar.getCantidadEnSucursales()) {
                            if (cant.getIdSucursal().equals(idSucursal)) {
                                cant.setCantidad(cantidadEnSucursal.get(idSucursal));
                                crearNuevaCantidadEnSucursal = false;
                            }
                        }
                        if (crearNuevaCantidadEnSucursal) {
                            CantidadEnSucursal cantidadNuevaEnSucursal = new CantidadEnSucursal();
                            cantidadNuevaEnSucursal.setCantidad(cantidadEnSucursal.get(idSucursal));
                            cantidadNuevaEnSucursal.setEstante(txt_Estante.getText().trim());
                            cantidadNuevaEnSucursal.setEstanteria(txt_Estanteria.getText().trim());
                            cantidadNuevaEnSucursal.setIdSucursal(idSucursal);
                            cantidadesNuevas.add(cantidadNuevaEnSucursal);
                        }
                    });
                    productoParaModificar.getCantidadEnSucursales().addAll(cantidadesNuevas);
                    productoParaModificar.setCantMinima(new BigDecimal(txt_CantMinima.getValue().toString()));
                    productoParaModificar.setBulto(new BigDecimal(txt_Bulto.getValue().toString()));
                    productoParaModificar.setPrecioCosto(new BigDecimal(txtPrecioCosto.getValue().toString()));
                    productoParaModificar.setGananciaPorcentaje(new BigDecimal(txtGananciaPorcentaje.getValue().toString()));
                    productoParaModificar.setGananciaNeto(new BigDecimal(txtGananciaNeto.getValue().toString()));
                    productoParaModificar.setPrecioVentaPublico(new BigDecimal(txtPVP.getValue().toString()));
                    productoParaModificar.setIvaPorcentaje(new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()));
                    productoParaModificar.setIvaNeto(new BigDecimal(txtIVANeto.getValue().toString()));
                    productoParaModificar.setPrecioLista(new BigDecimal(txtPrecioLista.getValue().toString()));
                    productoParaModificar.setIlimitado(chkSinLimite.isSelected());
                    productoParaModificar.setPublico(rbPublico.isSelected());
                    productoParaModificar.setEstanteria(txt_Estanteria.getText().trim());
                    productoParaModificar.setEstante(txt_Estante.getText().trim());
                    productoParaModificar.setNota(txt_Nota.getText().trim());
                    productoParaModificar.setOferta(chkOferta.isSelected());
                    if (chkOferta.isSelected()) {
                        productoParaModificar.setPorcentajeBonificacionOferta(new BigDecimal(txtPorcentajeOferta.getValue().toString()));
                    } else {
                        productoParaModificar.setPorcentajeBonificacionOferta(null);
                    }
                    if (dc_Vencimiento.getDate() != null) {
                        productoParaModificar.setFechaVencimiento(dc_Vencimiento.getDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate());
                    }
                    if (cambioImagen && imagenProducto != null) {
                        productoParaModificar.setUrlImagen(RestClient.getRestTemplate()
                                .postForObject("/productos/" + productoParaModificar.getIdProducto() + "/imagenes", imagenProducto, String.class));
                    } else if (cambioImagen && imagenProducto == null) {
                        productoParaModificar.setUrlImagen(null);
                    }
                    RestClient.getRestTemplate().put("/productos?idMedida=" + idMedida
                            + "&idRubro=" + idRubro
                            + "&idProveedor=" + ((idProveedor != null) ? idProveedor : "")
                            + "&idSucursal=" + SucursalActiva.getInstance().getSucursal().getIdSucursal(),
                            productoParaModificar);
                    LOGGER.warn("El producto " + productoParaModificar + " se modificó correctamente");
                    JOptionPane.showMessageDialog(this, "El producto se modificó correctamente.",
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                }
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnGuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.prepararComponentes();
        this.cargarMedidas();
        this.cargarRubros();
        this.cargarSucursales();
        if (operacion == TipoDeOperacion.ALTA) {
            this.setTitle("Nuevo Producto");
            lbl_FUM.setEnabled(false);
            lbl_FA.setEnabled(false);
            lbl_FechaUltimaModificacion.setVisible(false);
            lbl_FechaAlta.setVisible(false);
        } else if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.setTitle("Modificar Producto");
            this.cargarProductoParaModificar();
        }
        if (!UsuarioActivo.getInstance().getUsuario().getRoles().contains(Rol.ADMINISTRADOR) 
                && operacion == TipoDeOperacion.ACTUALIZACION) {
            chkSinLimite.setEnabled(false);
            lbl_Cantidad.setEnabled(false);
            txtCantidad.setEnabled(false);
            lbl_CantMinima.setEnabled(false);
            txt_CantMinima.setEnabled(false);
            lbl_Bulto.setEnabled(false);
            txt_Bulto.setEnabled(false);
        }
    }//GEN-LAST:event_formWindowOpened

    private void txt_BultoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_BultoFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_Bulto.selectAll();
        });
    }//GEN-LAST:event_txt_BultoFocusGained

    private void txt_CantMinimaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_CantMinimaFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_CantMinima.selectAll();
        });
    }//GEN-LAST:event_txt_CantMinimaFocusGained

    private void txtCantidadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCantidadFocusGained
        SwingUtilities.invokeLater(() -> {
            txtCantidad.selectAll();
        });
    }//GEN-LAST:event_txtCantidadFocusGained

    private void chkSinLimiteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkSinLimiteItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            txtCantidad.setEnabled(false);
            lbl_Cantidad.setEnabled(false);
            lbl_CantMinima.setEnabled(false);
            txt_CantMinima.setEnabled(false);
            lbl_Bulto.setEnabled(false);
            txt_Bulto.setEnabled(false);
            lblSucursal.setEnabled(false);
            cmbSucursales.setEnabled(false);
        } else {
            txtCantidad.setEnabled(true);
            lbl_Cantidad.setEnabled(true);
            lbl_CantMinima.setEnabled(true);
            txt_CantMinima.setEnabled(true);
            lbl_Bulto.setEnabled(true);
            txt_Bulto.setEnabled(true);
            lblSucursal.setEnabled(true);
            cmbSucursales.setEnabled(true);
        }
    }//GEN-LAST:event_chkSinLimiteItemStateChanged

    private void cmbIVAPorcentajeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbIVAPorcentajeItemStateChanged
        this.calcularIVANeto();
        this.calcularPrecioLista();
    }//GEN-LAST:event_cmbIVAPorcentajeItemStateChanged

    private void txtPVPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPVPActionPerformed
        this.calcularGananciaPorcentaje();
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularIVANeto();
        this.calcularPrecioLista();
    }//GEN-LAST:event_txtPVPActionPerformed

    private void txtPVPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPVPFocusLost
        try {
            txtPVP.commitEdit();
        } catch (ParseException ex) {}
        this.calcularGananciaPorcentaje();
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularIVANeto();
        this.calcularPrecioLista();
    }//GEN-LAST:event_txtPVPFocusLost

    private void txtPVPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPVPFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPVP.selectAll();
        });
    }//GEN-LAST:event_txtPVPFocusGained

    private void txtPrecioListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioListaActionPerformed
        this.calcularGananciaPorcentajeSegunPrecioDeLista();
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularIVANeto();
        this.calcularPrecioLista();
        this.calcularPrecioBonificado(new BigDecimal(txtPrecioLista.getValue().toString()));
    }//GEN-LAST:event_txtPrecioListaActionPerformed

    private void txtPrecioListaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioListaFocusLost
        try {
            txtPrecioLista.commitEdit();
        } catch (ParseException ex) {}
        this.calcularGananciaPorcentajeSegunPrecioDeLista();
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularIVANeto();
        this.calcularPrecioLista();
        this.calcularPrecioBonificado(new BigDecimal(txtPrecioLista.getValue().toString()));
    }//GEN-LAST:event_txtPrecioListaFocusLost

    private void txtPrecioListaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioListaFocusGained
        precioListaAnterior = new BigDecimal(txtPrecioLista.getValue().toString());
        SwingUtilities.invokeLater(() -> {
            txtPrecioLista.selectAll();
        });
    }//GEN-LAST:event_txtPrecioListaFocusGained

    private void txtGananciaPorcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGananciaPorcentajeActionPerformed
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularGananciaPorcentaje();
        this.calcularIVANeto();
        this.calcularPrecioLista();
    }//GEN-LAST:event_txtGananciaPorcentajeActionPerformed

    private void txtGananciaPorcentajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGananciaPorcentajeFocusLost
        try {
            txtGananciaPorcentaje.commitEdit();
        } catch (ParseException ex) {
            
        }
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularGananciaPorcentaje();
        this.calcularIVANeto();
        this.calcularPrecioLista();
    }//GEN-LAST:event_txtGananciaPorcentajeFocusLost

    private void txtGananciaPorcentajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGananciaPorcentajeFocusGained
        SwingUtilities.invokeLater(() -> {
            txtGananciaPorcentaje.selectAll();
        });
    }//GEN-LAST:event_txtGananciaPorcentajeFocusGained

    private void txtPrecioCostoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioCostoActionPerformed
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularIVANeto();
        this.calcularPrecioLista();
    }//GEN-LAST:event_txtPrecioCostoActionPerformed

    private void txtPrecioCostoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioCostoFocusLost
        try {
            txtPrecioCosto.commitEdit();
        } catch (ParseException ex) {}
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularIVANeto();
        this.calcularPrecioLista();
    }//GEN-LAST:event_txtPrecioCostoFocusLost

    private void txtPrecioCostoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioCostoFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPrecioCosto.selectAll();
        });
    }//GEN-LAST:event_txtPrecioCostoFocusGained

    private void btn_NuevoProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoProveedorActionPerformed
        DetalleProveedorGUI gui_DetalleProveedor = new DetalleProveedorGUI();
        gui_DetalleProveedor.setModal(true);
        gui_DetalleProveedor.setLocationRelativeTo(this);
        gui_DetalleProveedor.setVisible(true);
        if (gui_DetalleProveedor.getProveedorCreado() != null) {
            txtProveedor.setText(gui_DetalleProveedor.getProveedorCreado().getRazonSocial());
            proveedorSeleccionado = gui_DetalleProveedor.getProveedorCreado();
        }
    }//GEN-LAST:event_btn_NuevoProveedorActionPerformed

    private void btn_EliminarImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarImagenActionPerformed
        lbl_imagen.setIcon(null);
        lbl_imagen.setText("SIN IMAGEN");
        imagenProducto = null;
        cambioImagen = true;
    }//GEN-LAST:event_btn_EliminarImagenActionPerformed

    private void btn_ExaminarArchivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ExaminarArchivosActionPerformed
        JFileChooser menuElegirLogo = new JFileChooser();
        menuElegirLogo.setAcceptAllFileFilterUsed(false);
        menuElegirLogo.addChoosableFileFilter(new FiltroImagenes());
        try {
            if (menuElegirLogo.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (Utilidades.esTamanioValido(menuElegirLogo.getSelectedFile(), 1024000)) {
                    File file = menuElegirLogo.getSelectedFile();
                    imagenProducto = Utilidades.convertirFileIntoByteArray(file);
                    ImageIcon logoProvisional = new ImageIcon(menuElegirLogo.getSelectedFile().getAbsolutePath());
                    ImageIcon logoRedimensionado = new ImageIcon(logoProvisional.getImage()
                            .getScaledInstance(anchoImagenContainer, altoImagenContainer, Image.SCALE_SMOOTH));
                    lbl_imagen.setIcon(logoRedimensionado);
                    lbl_imagen.setText("");
                    cambioImagen = true;
                } else {
                    JOptionPane.showMessageDialog(this, "El tamaño del archivo seleccionado supera el límite de 1MB.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    imagenProducto = null;
                }
            } else {
                imagenProducto = null;
            }
        } catch (IOException ex) {
            String mensaje = ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException");
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_ExaminarArchivosActionPerformed

    private void btnBuscarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProveedorActionPerformed
        BuscarProveedoresGUI buscarProveedoresGUI = new BuscarProveedoresGUI();
        buscarProveedoresGUI.setModal(true);
        buscarProveedoresGUI.setLocationRelativeTo(this);
        buscarProveedoresGUI.setVisible(true);
        if (buscarProveedoresGUI.getProveedorSeleccionado() != null) {
            proveedorSeleccionado = buscarProveedoresGUI.getProveedorSeleccionado();
            txtProveedor.setText(proveedorSeleccionado.getRazonSocial());
        }
    }//GEN-LAST:event_btnBuscarProveedorActionPerformed

    private void rbPrivadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPrivadoActionPerformed
        if (rbPrivado.isSelected() && chkOferta.isSelected()) {
            rbPublico.setSelected(true);
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_producto_privado_no_en_oferta"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_rbPrivadoActionPerformed

    private void cmbSucursalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSucursalesActionPerformed
        BigDecimal cantidad = cantidadEnSucursal.get(((Sucursal) cmbSucursales.getSelectedItem()).getIdSucursal());
        if (cantidad != null) {
            txtCantidad.setValue(cantidad);
        } else {
            txtCantidad.setValue(BigDecimal.ZERO);
        }
    }//GEN-LAST:event_cmbSucursalesActionPerformed

    private void txtCantidadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyReleased
        if (!txtCantidad.getText().isEmpty() && (new BigDecimal(txtCantidad.getText())).compareTo(new BigDecimal(txtCantidad.getValue().toString())) != 0) {
            cantidadEnSucursal.put(((Sucursal) cmbSucursales.getSelectedItem()).getIdSucursal(), new BigDecimal(txtCantidad.getText()));
        }
    }//GEN-LAST:event_txtCantidadKeyReleased

    private void chkOfertaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkOfertaItemStateChanged
        if (chkOferta.isSelected()) {
            txtPorcentajeOferta.setEnabled(true);
            txtPrecioBonificado.setEnabled(true);
            this.calcularPrecioBonificado(txtPrecioLista.getValue() != null ? new BigDecimal(txtPrecioLista.getValue().toString()) : BigDecimal.ZERO);
        } else {
            txtPorcentajeOferta.setEnabled(false);
            txtPorcentajeOferta.setValue(BigDecimal.ZERO);
            txtPrecioBonificado.setEnabled(false);
            txtPrecioBonificado.setValue(BigDecimal.ZERO);
        }
    }//GEN-LAST:event_chkOfertaItemStateChanged

    private void txtPorcentajeOfertaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajeOfertaFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPorcentajeOferta.selectAll();
        });
    }//GEN-LAST:event_txtPorcentajeOfertaFocusGained

    private void txtPorcentajeOfertaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPorcentajeOfertaActionPerformed
        this.calcularPrecioBonificado(new BigDecimal(txtPrecioLista.getValue().toString()));
    }//GEN-LAST:event_txtPorcentajeOfertaActionPerformed

    private void txtPorcentajeOfertaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajeOfertaFocusLost
        try {
            txtPorcentajeOferta.commitEdit();
        } catch (ParseException ex) {
        }
        this.calcularPrecioBonificado(new BigDecimal(txtPrecioLista.getValue().toString()));
    }//GEN-LAST:event_txtPorcentajeOfertaFocusLost

    private void chkOfertaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOfertaActionPerformed
        if (rbPrivado.isSelected() && chkOferta.isSelected()) {
            chkOferta.setSelected(false);
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_producto_privado_no_en_oferta"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_chkOfertaActionPerformed

    private void txtPrecioBonificadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioBonificadoActionPerformed
        try {
            txtPrecioBonificado.commitEdit();
        } catch (ParseException ex) {
        }
        this.calcularPorcentajeOferta(new BigDecimal(txtPrecioBonificado.getValue().toString()), new BigDecimal(txtPrecioLista.getValue().toString()));
    }//GEN-LAST:event_txtPrecioBonificadoActionPerformed

    private void txtPrecioBonificadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioBonificadoFocusLost
        try {
            txtPrecioBonificado.commitEdit();
        } catch (ParseException ex) {
        }
        this.calcularPorcentajeOferta(new BigDecimal(txtPrecioBonificado.getValue().toString()), new BigDecimal(txtPrecioLista.getValue().toString()));
    }//GEN-LAST:event_txtPrecioBonificadoFocusLost

    private void txtPrecioBonificadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioBonificadoFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPrecioBonificado.selectAll();
        });
    }//GEN-LAST:event_txtPrecioBonificadoFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgVisibilidad;
    private javax.swing.JButton btnBuscarProveedor;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btn_EliminarImagen;
    private javax.swing.JButton btn_ExaminarArchivos;
    private javax.swing.JButton btn_NuevoProveedor;
    private javax.swing.JCheckBox chkOferta;
    private javax.swing.JCheckBox chkSinLimite;
    private javax.swing.JComboBox cmbIVAPorcentaje;
    private javax.swing.JComboBox<Sucursal> cmbSucursales;
    private javax.swing.JComboBox cmb_Medida;
    private javax.swing.JComboBox cmb_Rubro;
    private com.toedter.calendar.JDateChooser dc_Vencimiento;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAspectRatio;
    private javax.swing.JLabel lblPublico;
    private javax.swing.JLabel lblSinLimite;
    private javax.swing.JLabel lblSucursal;
    private javax.swing.JLabel lblTamanioMax;
    private javax.swing.JLabel lbl_Bulto;
    private javax.swing.JLabel lbl_CantMinima;
    private javax.swing.JLabel lbl_Cantidad;
    private javax.swing.JLabel lbl_Codigo;
    private javax.swing.JLabel lbl_Descripcion;
    private javax.swing.JLabel lbl_Estante;
    private javax.swing.JLabel lbl_Estanteria;
    private javax.swing.JLabel lbl_FA;
    private javax.swing.JLabel lbl_FUM;
    private javax.swing.JLabel lbl_FechaAlta;
    private javax.swing.JLabel lbl_FechaUltimaModificacion;
    private javax.swing.JLabel lbl_Ganancia;
    private javax.swing.JLabel lbl_IVA;
    private javax.swing.JLabel lbl_Medida;
    private javax.swing.JLabel lbl_Nota;
    private javax.swing.JLabel lbl_PVP;
    private javax.swing.JLabel lbl_PrecioCosto;
    private javax.swing.JLabel lbl_PrecioLista;
    private javax.swing.JLabel lbl_Proveedor;
    private javax.swing.JLabel lbl_Rubro;
    private javax.swing.JLabel lbl_Ven;
    private javax.swing.JLabel lbl_imagen;
    private javax.swing.JPanel panel5;
    private javax.swing.JPanel panel6;
    private javax.swing.JPanel panelCantidades;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelImagen;
    private javax.swing.JPanel panelPrecios;
    private javax.swing.JPanel panelPropiedades;
    private javax.swing.JPanel panelSuperior;
    private javax.swing.JPanel pnlCantidadSucursales;
    private javax.swing.JRadioButton rbPrivado;
    private javax.swing.JRadioButton rbPublico;
    private javax.swing.JTabbedPane tpTabs;
    private javax.swing.JFormattedTextField txtCantidad;
    private javax.swing.JFormattedTextField txtGananciaNeto;
    private javax.swing.JFormattedTextField txtGananciaPorcentaje;
    private javax.swing.JFormattedTextField txtIVANeto;
    private javax.swing.JFormattedTextField txtPVP;
    private javax.swing.JFormattedTextField txtPorcentajeOferta;
    private javax.swing.JFormattedTextField txtPrecioBonificado;
    private javax.swing.JFormattedTextField txtPrecioCosto;
    private javax.swing.JFormattedTextField txtPrecioLista;
    private javax.swing.JTextField txtProveedor;
    private javax.swing.JFormattedTextField txt_Bulto;
    private javax.swing.JFormattedTextField txt_CantMinima;
    private javax.swing.JTextField txt_Codigo;
    private javax.swing.JTextField txt_Descripcion;
    private javax.swing.JTextField txt_Estante;
    private javax.swing.JTextField txt_Estanteria;
    private javax.swing.JTextArea txt_Nota;
    // End of variables declaration//GEN-END:variables
 
}
