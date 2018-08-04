package sic.vista.swing;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import sic.modelo.EmpresaActiva;
import sic.modelo.Medida;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Rol;
import sic.modelo.Rubro;
import sic.modelo.TipoDeOperacion;
import sic.modelo.UsuarioActivo;
import sic.util.CalculosPrecioProducto;
import sic.util.FormatosFechaHora;
import sic.util.FormatterFechaHora;

public class DetalleProductoGUI extends JDialog {

    private Producto productoParaModificar;
    private final TipoDeOperacion operacion;        
    private List<Medida> medidas;
    private List<Rubro> rubros;
    private List<Proveedor> proveedores;    
    private BigDecimal precioListaAnterior = BigDecimal.ZERO;
    private final static BigDecimal IVA_21 = new BigDecimal("21");	
    private final static BigDecimal IVA_105 = new BigDecimal("10.5");
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleProductoGUI() {
        this.initComponents();
        this.setIcon();                
        operacion = TipoDeOperacion.ALTA;
        panel6.setVisible(false);
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
        btn_Rubros = new javax.swing.JButton();
        lbl_Proveedor = new javax.swing.JLabel();
        cmb_Proveedor = new javax.swing.JComboBox();
        btn_NuevoProveedor = new javax.swing.JButton();
        btn_Medidas = new javax.swing.JButton();
        cmb_Medida = new javax.swing.JComboBox();
        lbl_Medida = new javax.swing.JLabel();
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
        panelCantidades = new javax.swing.JPanel();
        chkSinLimite = new javax.swing.JCheckBox();
        lbl_Cantidad = new javax.swing.JLabel();
        txt_Cantidad = new javax.swing.JFormattedTextField();
        txt_CantMinima = new javax.swing.JFormattedTextField();
        lbl_CantMinima = new javax.swing.JLabel();
        lbl_VentaMinima = new javax.swing.JLabel();
        txt_VentaMinima = new javax.swing.JFormattedTextField();
        lblSinLimite = new javax.swing.JLabel();
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

        btn_Rubros.setForeground(java.awt.Color.blue);
        btn_Rubros.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddBlock.png"))); // NOI18N
        btn_Rubros.setText("Nuevo");
        btn_Rubros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RubrosActionPerformed(evt);
            }
        });

        lbl_Proveedor.setForeground(java.awt.Color.red);
        lbl_Proveedor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Proveedor.setText("* Proveedor:");

        btn_NuevoProveedor.setForeground(java.awt.Color.blue);
        btn_NuevoProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProviderBag_16x16.png"))); // NOI18N
        btn_NuevoProveedor.setText("Nuevo");
        btn_NuevoProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoProveedorActionPerformed(evt);
            }
        });

        btn_Medidas.setForeground(java.awt.Color.blue);
        btn_Medidas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddRuler_16x16.png"))); // NOI18N
        btn_Medidas.setText("Nueva");
        btn_Medidas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_MedidasActionPerformed(evt);
            }
        });

        lbl_Medida.setForeground(java.awt.Color.red);
        lbl_Medida.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Medida.setText("* Unidad de Medida:");

        javax.swing.GroupLayout panelSuperiorLayout = new javax.swing.GroupLayout(panelSuperior);
        panelSuperior.setLayout(panelSuperiorLayout);
        panelSuperiorLayout.setHorizontalGroup(
            panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSuperiorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Codigo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Medida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Proveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Rubro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Descripcion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSuperiorLayout.createSequentialGroup()
                        .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_Rubro, 0, 295, Short.MAX_VALUE)
                            .addComponent(cmb_Proveedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmb_Medida, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_NuevoProveedor)
                            .addComponent(btn_Medidas)
                            .addComponent(btn_Rubros)))
                    .addComponent(txt_Codigo)
                    .addComponent(txt_Descripcion))
                .addContainerGap())
        );
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
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmb_Rubro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Rubros, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Rubro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmb_Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Proveedor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Medidas, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_Medida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Medida))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelSuperiorLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Medidas, btn_NuevoProveedor, btn_Rubros, cmb_Medida, cmb_Proveedor, cmb_Rubro});

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
        txtGananciaPorcentaje.setText("0");
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

        javax.swing.GroupLayout panelPreciosLayout = new javax.swing.GroupLayout(panelPrecios);
        panelPrecios.setLayout(panelPreciosLayout);
        panelPreciosLayout.setHorizontalGroup(
            panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreciosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_PrecioLista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_PVP, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                    .addComponent(lbl_IVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Ganancia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_PrecioCosto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPreciosLayout.createSequentialGroup()
                        .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbIVAPorcentaje, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtGananciaPorcentaje))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPreciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIVANeto, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtGananciaNeto)
                            .addComponent(txtPrecioCosto)
                            .addComponent(txtPVP, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)))
                    .addComponent(txtPrecioLista, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPreciosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtGananciaNeto, txtGananciaPorcentaje, txtIVANeto, txtPVP, txtPrecioCosto, txtPrecioLista});

        panelCantidades.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkSinLimite.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chkSinLimite.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkSinLimiteItemStateChanged(evt);
            }
        });

        lbl_Cantidad.setForeground(java.awt.Color.red);
        lbl_Cantidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Cantidad.setText("* Cantidad:");

        txt_Cantidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Cantidad.setText("0");
        txt_Cantidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_CantidadFocusGained(evt);
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
        lbl_CantMinima.setText("Cantidad Mínima:");

        lbl_VentaMinima.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_VentaMinima.setText("Venta Mínima:");

        txt_VentaMinima.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_VentaMinima.setText("0");
        txt_VentaMinima.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_VentaMinimaFocusGained(evt);
            }
        });

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
                            .addComponent(lbl_Cantidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSinLimite, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkSinLimite, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                            .addComponent(txt_Cantidad)
                            .addComponent(txt_CantMinima)))
                    .addGroup(panelCantidadesLayout.createSequentialGroup()
                        .addComponent(lbl_VentaMinima, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_VentaMinima)))
                .addContainerGap())
        );

        panelCantidadesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_CantMinima, lbl_Cantidad, lbl_VentaMinima});

        panelCantidadesLayout.setVerticalGroup(
            panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCantidadesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblSinLimite)
                    .addComponent(chkSinLimite))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Cantidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_CantMinima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_CantMinima))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_VentaMinima)
                    .addComponent(txt_VentaMinima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
                        .addComponent(panelCantidades, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPrecios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelCantidades, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
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

        javax.swing.GroupLayout panel5Layout = new javax.swing.GroupLayout(panel5);
        panel5.setLayout(panel5Layout);
        panel5Layout.setHorizontalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbl_Estante, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPublico, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Estanteria, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Ven, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                    .addComponent(lbl_Nota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dc_Vencimiento, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                    .addComponent(txt_Estante)
                    .addComponent(txt_Estanteria)
                    .addGroup(panel5Layout.createSequentialGroup()
                        .addComponent(rbPublico)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbPrivado)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panel5Layout.setVerticalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelPropiedadesLayout = new javax.swing.GroupLayout(panelPropiedades);
        panelPropiedades.setLayout(panelPropiedadesLayout);
        panelPropiedadesLayout.setHorizontalGroup(
            panelPropiedadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPropiedadesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPropiedadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        panelPropiedadesLayout.setVerticalGroup(
            panelPropiedadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPropiedadesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        tpTabs.addTab("Propiedades", panelPropiedades);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnGuardar))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tpTabs)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpTabs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardar)
                .addContainerGap())
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
        txt_Cantidad.setValue(productoParaModificar.getCantidad());
        txt_CantMinima.setValue(productoParaModificar.getCantMinima());
        txt_VentaMinima.setValue(productoParaModificar.getVentaMinima());
        cmb_Rubro.setSelectedItem(productoParaModificar.getNombreRubro());
        cmb_Proveedor.setSelectedItem(productoParaModificar.getRazonSocialProveedor());
        FormatterFechaHora formateador = new FormatterFechaHora(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO);
        lbl_FechaUltimaModificacion.setText(formateador.format(productoParaModificar.getFechaUltimaModificacion()));
        lbl_FechaAlta.setText(formateador.format(productoParaModificar.getFechaAlta()));
        dc_Vencimiento.setDate(productoParaModificar.getFechaVencimiento());
        txt_Estanteria.setText(productoParaModificar.getEstanteria());
        txt_Estante.setText(productoParaModificar.getEstante());        
        txtPrecioCosto.setValue(productoParaModificar.getPrecioCosto());        
        txtGananciaPorcentaje.setValue(productoParaModificar.getGanancia_porcentaje());        
        txtGananciaNeto.setValue(productoParaModificar.getGanancia_neto());        
        txtPVP.setValue(productoParaModificar.getPrecioVentaPublico());        
        cmbIVAPorcentaje.setSelectedItem(productoParaModificar.getIva_porcentaje().stripTrailingZeros());        
        txtIVANeto.setValue(productoParaModificar.getIva_neto());        
        txtPrecioLista.setValue(productoParaModificar.getPrecioLista());
    }

    private void prepararComponentes() {
        txt_Cantidad.setValue(BigDecimal.ZERO);
        txt_CantMinima.setValue(BigDecimal.ZERO);
        txt_VentaMinima.setValue(BigDecimal.ONE);
        txtPrecioCosto.setValue(BigDecimal.ZERO);
        txtPVP.setValue(BigDecimal.ZERO);
        txtIVANeto.setValue(BigDecimal.ZERO);
        txtGananciaPorcentaje.setValue(BigDecimal.ZERO);
        txtGananciaNeto.setValue(BigDecimal.ZERO);
        txtPrecioLista.setValue(BigDecimal.ZERO);
        rbPrivado.setSelected(true);
    }
    
    private void cargarMedidas() {
        cmb_Medida.removeAllItems();
        try {
            medidas = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/medidas/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
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
                .getForObject("/rubros/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
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

    private void cargarProveedores() {
        cmb_Proveedor.removeAllItems();
        try {
            proveedores = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/proveedores/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                Proveedor[].class)));
            proveedores.stream().forEach(p -> cmb_Proveedor.addItem(p.getRazonSocial()));
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
        txt_Cantidad.setValue(BigDecimal.ZERO);
        txt_CantMinima.setValue(BigDecimal.ZERO);
        txt_VentaMinima.setValue(BigDecimal.ZERO);
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
                BigDecimal.ZERO, new BigDecimal(txtPVP.getValue().toString()), BigDecimal.ZERO, BigDecimal.ZERO,
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
                new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()), BigDecimal.ZERO);
        txtPrecioLista.setValue(precioDeLista);
    }
    
    private void calcularGananciaPorcentajeSegunPrecioDeLista() {        
        BigDecimal gananciaPorcentaje = CalculosPrecioProducto.calcularGananciaPorcentaje(
                new BigDecimal(txtPrecioLista.getValue().toString()), precioListaAnterior,
                new BigDecimal(txtPVP.getValue().toString()), 
                new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()), BigDecimal.ZERO,
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
                idMedida = m.getId_Medida();
            }
        }
        for (Rubro r : rubros) {
            if (r.getNombre().equals(cmb_Rubro.getSelectedItem())) {
                idRubro = r.getId_Rubro();
            }
        }
        for (Proveedor p : proveedores) {
            if (p.getRazonSocial().equals(cmb_Proveedor.getSelectedItem())) {
                idProveedor = p.getId_Proveedor();
            }
        }
        if (idMedida == null) {
            mensajeError = mensajeError.concat(ResourceBundle.getBundle("Mensajes").getString("mensaje_producto_vacio_medida") + "\n");
            ejecutarOperacion = false;
        }
        if (idRubro == null) {
            mensajeError = mensajeError.concat(ResourceBundle.getBundle("Mensajes").getString("mensaje_producto_vacio_rubro") + "\n");
            ejecutarOperacion = false;
        }
        if (idProveedor == null) {
            mensajeError = mensajeError.concat(ResourceBundle.getBundle("Mensajes").getString("mensaje_producto_vacio_proveedor") + "\n");
            ejecutarOperacion = false;
        }
        if (ejecutarOperacion) {
            try {
                if (operacion == TipoDeOperacion.ALTA) {
                    Producto producto = new Producto();
                    producto.setCodigo(txt_Codigo.getText());
                    producto.setDescripcion(txt_Descripcion.getText().trim());
                    producto.setCantidad(new BigDecimal(txt_Cantidad.getValue().toString()));
                    producto.setCantMinima(new BigDecimal(txt_CantMinima.getValue().toString()));
                    producto.setVentaMinima(new BigDecimal(txt_VentaMinima.getValue().toString()));
                    producto.setPrecioCosto(new BigDecimal(txtPrecioCosto.getValue().toString()));
                    producto.setGanancia_porcentaje(new BigDecimal(txtGananciaPorcentaje.getValue().toString()));
                    producto.setGanancia_neto(new BigDecimal(txtGananciaNeto.getValue().toString()));
                    producto.setPrecioVentaPublico(new BigDecimal(txtPVP.getValue().toString()));
                    producto.setIva_porcentaje(new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()));
                    producto.setIva_neto(new BigDecimal(txtIVANeto.getValue().toString()));
                    producto.setImpuestoInterno_porcentaje(BigDecimal.ZERO);
                    producto.setImpuestoInterno_neto(BigDecimal.ZERO);
                    producto.setPrecioLista(new BigDecimal(txtPrecioLista.getValue().toString()));
                    producto.setIlimitado(chkSinLimite.isSelected());   
                    producto.setPublico(rbPublico.isSelected());
                    producto.setEstanteria(txt_Estanteria.getText().trim());
                    producto.setEstante(txt_Estante.getText().trim());
                    producto.setNota(txt_Nota.getText().trim());                    
                    producto.setFechaVencimiento(dc_Vencimiento.getDate());
                    RestClient.getRestTemplate().postForObject("/productos?idMedida=" + idMedida + "&idRubro=" + idRubro
                            + "&idProveedor=" + idProveedor + "&idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                            producto, Producto.class);
                    LOGGER.warn("El producto " + producto + " se guardó correctamente");
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
                    productoParaModificar.setCantidad(new BigDecimal(txt_Cantidad.getValue().toString()));
                    productoParaModificar.setCantMinima(new BigDecimal(txt_CantMinima.getValue().toString()));
                    productoParaModificar.setCantidad(new BigDecimal(txt_Cantidad.getValue().toString()));
                    productoParaModificar.setCantMinima(new BigDecimal(txt_CantMinima.getValue().toString()));
                    productoParaModificar.setVentaMinima(new BigDecimal(txt_VentaMinima.getValue().toString()));
                    productoParaModificar.setPrecioCosto(new BigDecimal(txtPrecioCosto.getValue().toString()));
                    productoParaModificar.setGanancia_porcentaje(new BigDecimal(txtGananciaPorcentaje.getValue().toString()));
                    productoParaModificar.setGanancia_neto(new BigDecimal(txtGananciaNeto.getValue().toString()));
                    productoParaModificar.setPrecioVentaPublico(new BigDecimal(txtPVP.getValue().toString()));
                    productoParaModificar.setIva_porcentaje(new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()));
                    productoParaModificar.setIva_neto(new BigDecimal(txtIVANeto.getValue().toString()));
                    productoParaModificar.setImpuestoInterno_porcentaje(BigDecimal.ZERO);
                    productoParaModificar.setImpuestoInterno_neto(BigDecimal.ZERO);
                    productoParaModificar.setPrecioLista(new BigDecimal(txtPrecioLista.getValue().toString()));
                    productoParaModificar.setIlimitado(chkSinLimite.isSelected());   
                    productoParaModificar.setPublico(rbPublico.isSelected());
                    productoParaModificar.setEstanteria(txt_Estanteria.getText().trim());
                    productoParaModificar.setEstante(txt_Estante.getText().trim());
                    productoParaModificar.setNota(txt_Nota.getText().trim());
                    productoParaModificar.setFechaVencimiento(dc_Vencimiento.getDate());
                    RestClient.getRestTemplate().put("/productos?idMedida=" + idMedida + "&idRubro=" + idRubro
                            + "&idProveedor=" + idProveedor + "&idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
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
        this.cargarProveedores();
        if (operacion == TipoDeOperacion.ALTA) {
            this.setTitle("Nuevo Producto");
        } else if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.setTitle("Modificar Producto");
            this.cargarProductoParaModificar();
        }
        if (!UsuarioActivo.getInstance().getUsuario().getRoles().contains(Rol.ADMINISTRADOR) 
                && operacion == TipoDeOperacion.ACTUALIZACION) {
            chkSinLimite.setEnabled(false);
            lbl_Cantidad.setEnabled(false);
            txt_Cantidad.setEnabled(false);
            lbl_CantMinima.setEnabled(false);
            txt_CantMinima.setEnabled(false);
            lbl_VentaMinima.setEnabled(false);
            txt_VentaMinima.setEnabled(false);
        }
    }//GEN-LAST:event_formWindowOpened

    private void txt_VentaMinimaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_VentaMinimaFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_VentaMinima.selectAll();
        });
    }//GEN-LAST:event_txt_VentaMinimaFocusGained

    private void txt_CantMinimaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_CantMinimaFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_CantMinima.selectAll();
        });
    }//GEN-LAST:event_txt_CantMinimaFocusGained

    private void txt_CantidadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_CantidadFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_Cantidad.selectAll();
        });
    }//GEN-LAST:event_txt_CantidadFocusGained

    private void chkSinLimiteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkSinLimiteItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            txt_Cantidad.setEnabled(false);
            lbl_Cantidad.setForeground(Color.LIGHT_GRAY);
            txt_CantMinima.setEnabled(false);
            lbl_CantMinima.setForeground(Color.LIGHT_GRAY);
            txt_VentaMinima.setEnabled(false);
            lbl_VentaMinima.setForeground(Color.LIGHT_GRAY);
        } else {
            txt_Cantidad.setEnabled(true);
            lbl_Cantidad.setForeground(Color.RED);
            txt_CantMinima.setEnabled(true);
            lbl_CantMinima.setForeground(Color.BLACK);
            txt_VentaMinima.setEnabled(true);
            lbl_VentaMinima.setForeground(Color.BLACK);
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
        } catch (ParseException ex) {}
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

    private void btn_MedidasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_MedidasActionPerformed
        DetalleMedidaGUI gui_DetalleMedida = new DetalleMedidaGUI();
        gui_DetalleMedida.setModal(true);
        gui_DetalleMedida.setLocationRelativeTo(this);
        gui_DetalleMedida.setVisible(true);
        this.cargarMedidas();
    }//GEN-LAST:event_btn_MedidasActionPerformed

    private void btn_NuevoProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoProveedorActionPerformed
        DetalleProveedorGUI gui_DetalleProveedor = new DetalleProveedorGUI();
        gui_DetalleProveedor.setModal(true);
        gui_DetalleProveedor.setLocationRelativeTo(this);
        gui_DetalleProveedor.setVisible(true);
        this.cargarProveedores();
    }//GEN-LAST:event_btn_NuevoProveedorActionPerformed

    private void btn_RubrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RubrosActionPerformed
        DetalleRubroGUI gui_DetalleRubro = new DetalleRubroGUI();
        gui_DetalleRubro.setModal(true);
        gui_DetalleRubro.setLocationRelativeTo(this);
        gui_DetalleRubro.setVisible(true);
        this.cargarRubros();
    }//GEN-LAST:event_btn_RubrosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgVisibilidad;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btn_Medidas;
    private javax.swing.JButton btn_NuevoProveedor;
    private javax.swing.JButton btn_Rubros;
    private javax.swing.JCheckBox chkSinLimite;
    private javax.swing.JComboBox cmbIVAPorcentaje;
    private javax.swing.JComboBox cmb_Medida;
    private javax.swing.JComboBox cmb_Proveedor;
    private javax.swing.JComboBox cmb_Rubro;
    private com.toedter.calendar.JDateChooser dc_Vencimiento;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPublico;
    private javax.swing.JLabel lblSinLimite;
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
    private javax.swing.JLabel lbl_VentaMinima;
    private javax.swing.JPanel panel5;
    private javax.swing.JPanel panel6;
    private javax.swing.JPanel panelCantidades;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelPrecios;
    private javax.swing.JPanel panelPropiedades;
    private javax.swing.JPanel panelSuperior;
    private javax.swing.JRadioButton rbPrivado;
    private javax.swing.JRadioButton rbPublico;
    private javax.swing.JTabbedPane tpTabs;
    private javax.swing.JFormattedTextField txtGananciaNeto;
    private javax.swing.JFormattedTextField txtGananciaPorcentaje;
    private javax.swing.JFormattedTextField txtIVANeto;
    private javax.swing.JFormattedTextField txtPVP;
    private javax.swing.JFormattedTextField txtPrecioCosto;
    private javax.swing.JFormattedTextField txtPrecioLista;
    private javax.swing.JFormattedTextField txt_CantMinima;
    private javax.swing.JFormattedTextField txt_Cantidad;
    private javax.swing.JTextField txt_Codigo;
    private javax.swing.JTextField txt_Descripcion;
    private javax.swing.JTextField txt_Estante;
    private javax.swing.JTextField txt_Estanteria;
    private javax.swing.JTextArea txt_Nota;
    private javax.swing.JFormattedTextField txt_VentaMinima;
    // End of variables declaration//GEN-END:variables
 
}
