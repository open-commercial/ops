package sic.vista.swing;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import sic.modelo.ProductosParaActualizar;
import sic.modelo.Medida;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Rol;
import sic.modelo.Rubro;
import sic.modelo.UsuarioActivo;
import sic.util.CalculosPrecioProducto;

public class ModificacionMultipleProductosGUI extends JDialog {

    private final List<Producto> productosParaModificar;
    private Proveedor proveedorSeleccionado;
    private ModeloTabla modeloTablaProductos;
    private BigDecimal precioListaAnterior = BigDecimal.ZERO;
    private final List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
    private final static BigDecimal IVA_21 = new BigDecimal("21");	
    private final static BigDecimal IVA_105 = new BigDecimal("10.5");
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ModificacionMultipleProductosGUI(List<Producto> productosParaModificar) {
        this.initComponents();
        this.setIcon();        
        this.productosParaModificar = productosParaModificar;
        this.cargarResultadosAlTable(); 
        this.proveedorSeleccionado = null;
    }    

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(ModificacionMultipleProductosGUI.class.getResource("/sic/icons/Product_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarMedidas() {
        try {
            cmb_Medida.removeAllItems();
            List<Medida> medidas = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/medidas",
                            Medida[].class)));
            medidas.stream().forEach((m) -> {
                cmb_Medida.addItem(m);
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

    private void cargarRubros() {
        try {
            cmb_Rubro.removeAllItems();
            List<Rubro> rubros = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/rubros", Rubro[].class)));
            rubros.stream().forEach((r) -> {
                cmb_Rubro.addItem(r);
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

    private void prepararComponentes() {
        txtPrecioCosto.setValue(BigDecimal.ZERO);
        txtPVP.setValue(BigDecimal.ZERO);
        txtIVANeto.setValue(BigDecimal.ZERO);
        txtGananciaPorcentaje.setValue(BigDecimal.ZERO);
        txtGananciaNeto.setValue(BigDecimal.ZERO);
        txtPrecioLista.setValue(BigDecimal.ZERO);
        rbRecargo.setSelected(true);
        rbPrivado.setSelected(true);
        rbCatalogoNo.setSelected(true);
        txtDescuentoRecargoPorcentaje.setValue(BigDecimal.ZERO);
        txtPorcentajePrecioBonificado.setValue(BigDecimal.ZERO);
        txtPrecioBonificado.setValue(BigDecimal.ZERO);
        txtPorcentajeOferta.setValue(BigDecimal.ZERO);
        if (!rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)) {
            chkVentaMinima.setEnabled(false);
        } 
    }
    
    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[2];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripcion";
        modeloTablaProductos.setColumnIdentifiers(encabezados);
        tbl_ProductosAModifcar.setModel(modeloTablaProductos);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaProductos.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        modeloTablaProductos.setClaseColumnas(tipos);
        tbl_ProductosAModifcar.getTableHeader().setReorderingAllowed(false);
        tbl_ProductosAModifcar.getTableHeader().setResizingAllowed(true);
        //Tamanios de columnas
        tbl_ProductosAModifcar.getColumnModel().getColumn(0).setPreferredWidth(150);
        tbl_ProductosAModifcar.getColumnModel().getColumn(1).setPreferredWidth(400);
    }

    private void limpiarJTable() {
        modeloTablaProductos = new ModeloTabla();
        tbl_ProductosAModifcar.setModel(modeloTablaProductos);
        this.setColumnas();
    }

    private void cargarResultadosAlTable() {
        this.limpiarJTable();
        productosParaModificar.stream().map(p -> {
            Object[] fila = new Object[23];
            fila[0] = p.getCodigo();
            fila[1] = p.getDescripcion();
            return fila;
        }).forEach(f -> {
            modeloTablaProductos.addRow(f);
        });
        tbl_ProductosAModifcar.setModel(modeloTablaProductos);
    }

    private void habilitarBotonGuardar() {
        if (chk_Precios.isSelected()
                || chk_Proveedor.isSelected()
                || chk_Rubro.isSelected()
                || chk_UnidadDeMedida.isSelected()
                || chkRecargoDescuento.isSelected()
                || chkVisibilidad.isSelected()
                || chkVentaMinima.isSelected()
                || chkCatalogo.isSelected()) {
            btn_Guardar.setEnabled(true);
        } else {
            btn_Guardar.setEnabled(false);
        }
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
    
    private void calcularPrecioOferta() {
        txtPrecioOferta.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajeOferta.getValue().toString())));
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
    
    private BigDecimal calcularPrecioPorcentaje(BigDecimal monto, BigDecimal porcentaje) {
        return monto
                .multiply((new BigDecimal("100")).subtract(porcentaje)
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
    }
    
    private BigDecimal calcularPorcentaje(BigDecimal precioBonificado, BigDecimal precioDeLista) {
        if (precioDeLista.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        } else {
            return (new BigDecimal("100")).subtract(precioBonificado.multiply(new BigDecimal("100")).divide(precioDeLista, 2, RoundingMode.HALF_UP));
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgRecargoDescuento = new javax.swing.ButtonGroup();
        bgVisibilidad = new javax.swing.ButtonGroup();
        bgCatalogo = new javax.swing.ButtonGroup();
        sp_ProductosAModificar = new javax.swing.JScrollPane();
        tbl_ProductosAModifcar = new javax.swing.JTable();
        panel1 = new javax.swing.JPanel();
        cmb_Rubro = new javax.swing.JComboBox();
        btn_NuevoProveedor = new javax.swing.JButton();
        cmb_Medida = new javax.swing.JComboBox();
        chk_Rubro = new javax.swing.JCheckBox();
        chk_Proveedor = new javax.swing.JCheckBox();
        chk_UnidadDeMedida = new javax.swing.JCheckBox();
        chkVisibilidad = new javax.swing.JCheckBox();
        rbPublico = new javax.swing.JRadioButton();
        rbPrivado = new javax.swing.JRadioButton();
        txtProveedor = new javax.swing.JTextField();
        btnBuscarProveedor = new javax.swing.JButton();
        chkCatalogo = new javax.swing.JCheckBox();
        rbCatalogoSi = new javax.swing.JRadioButton();
        rbCatalogoNo = new javax.swing.JRadioButton();
        panel2 = new javax.swing.JPanel();
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
        chk_Precios = new javax.swing.JCheckBox();
        cmbIVAPorcentaje = new javax.swing.JComboBox();
        lblBonificacion = new javax.swing.JLabel();
        txtPorcentajePrecioBonificado = new javax.swing.JFormattedTextField();
        txtPrecioBonificado = new javax.swing.JFormattedTextField();
        chkOferta = new javax.swing.JCheckBox();
        txtPorcentajeOferta = new javax.swing.JFormattedTextField();
        txtPrecioOferta = new javax.swing.JFormattedTextField();
        btn_Guardar = new javax.swing.JButton();
        lbl_Indicaciones = new javax.swing.JLabel();
        panel3 = new javax.swing.JPanel();
        chkRecargoDescuento = new javax.swing.JCheckBox();
        rbRecargo = new javax.swing.JRadioButton();
        rbDescuento = new javax.swing.JRadioButton();
        txtDescuentoRecargoPorcentaje = new javax.swing.JFormattedTextField();
        panelCantidades = new javax.swing.JPanel();
        lblVentaCantidadMinima = new javax.swing.JLabel();
        txtVentaCantidadMinima = new javax.swing.JFormattedTextField();
        chkVentaMinima = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modificar multiples Productos");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tbl_ProductosAModifcar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_ProductosAModifcar.setFocusable(false);
        sp_ProductosAModificar.setViewportView(tbl_ProductosAModifcar);

        panel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cmb_Rubro.setEnabled(false);

        btn_NuevoProveedor.setForeground(java.awt.Color.blue);
        btn_NuevoProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProviderBag_16x16.png"))); // NOI18N
        btn_NuevoProveedor.setEnabled(false);
        btn_NuevoProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoProveedorActionPerformed(evt);
            }
        });

        cmb_Medida.setEnabled(false);

        chk_Rubro.setText("Rubro:");
        chk_Rubro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_RubroItemStateChanged(evt);
            }
        });

        chk_Proveedor.setText("Proveedor:");
        chk_Proveedor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ProveedorItemStateChanged(evt);
            }
        });

        chk_UnidadDeMedida.setText("U. de Medida:");
        chk_UnidadDeMedida.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UnidadDeMedidaItemStateChanged(evt);
            }
        });

        chkVisibilidad.setText("Visibilidad:");
        chkVisibilidad.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkVisibilidadItemStateChanged(evt);
            }
        });

        bgVisibilidad.add(rbPublico);
        rbPublico.setText("Público");
        rbPublico.setEnabled(false);

        bgVisibilidad.add(rbPrivado);
        rbPrivado.setText("Privado");
        rbPrivado.setEnabled(false);

        txtProveedor.setEditable(false);
        txtProveedor.setEnabled(false);
        txtProveedor.setOpaque(false);

        btnBuscarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarProveedor.setEnabled(false);
        btnBuscarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProveedorActionPerformed(evt);
            }
        });

        chkCatalogo.setText("Catalogo:");
        chkCatalogo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkCatalogoItemStateChanged(evt);
            }
        });

        bgCatalogo.add(rbCatalogoSi);
        rbCatalogoSi.setText("Si");
        rbCatalogoSi.setEnabled(false);

        bgCatalogo.add(rbCatalogoNo);
        rbCatalogoNo.setText("No");
        rbCatalogoNo.setEnabled(false);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(chk_UnidadDeMedida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(chk_Rubro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(chk_Proveedor))
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(txtProveedor)
                                .addGap(0, 0, 0)
                                .addComponent(btnBuscarProveedor)
                                .addGap(0, 0, 0)
                                .addComponent(btn_NuevoProveedor)
                                .addGap(12, 12, 12))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cmb_Medida, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cmb_Rubro, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chkVisibilidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chkCatalogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbPublico)
                            .addComponent(rbCatalogoSi))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbCatalogoNo)
                            .addComponent(rbPrivado))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        panel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnBuscarProveedor, btn_NuevoProveedor});

        panel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chkVisibilidad, chk_Proveedor, chk_Rubro, chk_UnidadDeMedida});

        panel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {rbPrivado, rbPublico});

        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtProveedor)
                    .addComponent(chk_Proveedor)
                    .addComponent(btnBuscarProveedor)
                    .addComponent(btn_NuevoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmb_Rubro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Rubro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_UnidadDeMedida)
                    .addComponent(cmb_Medida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkVisibilidad)
                    .addComponent(rbPublico)
                    .addComponent(rbPrivado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkCatalogo)
                    .addComponent(rbCatalogoSi)
                    .addComponent(rbCatalogoNo))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarProveedor, btn_NuevoProveedor, cmb_Medida, cmb_Rubro, txtProveedor});

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chk_Proveedor, chk_Rubro, chk_UnidadDeMedida});

        panel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_PrecioCosto.setForeground(new java.awt.Color(192, 192, 192));
        lbl_PrecioCosto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PrecioCosto.setText("Precio de Costo:");

        lbl_Ganancia.setForeground(new java.awt.Color(192, 192, 192));
        lbl_Ganancia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Ganancia.setText("Ganancia (%):");

        lbl_PrecioLista.setForeground(new java.awt.Color(192, 192, 192));
        lbl_PrecioLista.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PrecioLista.setText("Precio de Lista:");

        txtPrecioCosto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPrecioCosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioCosto.setText("0");
        txtPrecioCosto.setEnabled(false);
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
        txtGananciaPorcentaje.setEnabled(false);
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
        txtPrecioLista.setEnabled(false);
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

        lbl_IVA.setForeground(new java.awt.Color(192, 192, 192));
        lbl_IVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_IVA.setText("I.V.A. (%):");

        txtIVANeto.setEditable(false);
        txtIVANeto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtIVANeto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIVANeto.setText("0");
        txtIVANeto.setEnabled(false);
        txtIVANeto.setFocusable(false);

        txtGananciaNeto.setEditable(false);
        txtGananciaNeto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtGananciaNeto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGananciaNeto.setText("0");
        txtGananciaNeto.setEnabled(false);
        txtGananciaNeto.setFocusable(false);

        lbl_PVP.setForeground(new java.awt.Color(192, 192, 192));
        lbl_PVP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PVP.setText("Precio Venta Público:");

        txtPVP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPVP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPVP.setText("0");
        txtPVP.setEnabled(false);
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

        chk_Precios.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_PreciosItemStateChanged(evt);
            }
        });

        cmbIVAPorcentaje.setModel(new javax.swing.DefaultComboBoxModel(new BigDecimal[] { BigDecimal.ZERO, IVA_105, IVA_21 }));
        cmbIVAPorcentaje.setEnabled(false);
        cmbIVAPorcentaje.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbIVAPorcentajeItemStateChanged(evt);
            }
        });

        lblBonificacion.setForeground(new java.awt.Color(192, 192, 192));
        lblBonificacion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBonificacion.setText("Precio Bonif.(%):");
        lblBonificacion.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txtPorcentajePrecioBonificado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPorcentajePrecioBonificado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPorcentajePrecioBonificado.setText("0");
        txtPorcentajePrecioBonificado.setEnabled(false);
        txtPorcentajePrecioBonificado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPorcentajePrecioBonificadoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPorcentajePrecioBonificadoFocusLost(evt);
            }
        });
        txtPorcentajePrecioBonificado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPorcentajePrecioBonificadoActionPerformed(evt);
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

        chkOferta.setText("Oferta (%):");
        chkOferta.setEnabled(false);
        chkOferta.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        chkOferta.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        chkOferta.setPreferredSize(new java.awt.Dimension(105, 15));
        chkOferta.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkOfertaItemStateChanged(evt);
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

        txtPrecioOferta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPrecioOferta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioOferta.setText("0");
        txtPrecioOferta.setEnabled(false);
        txtPrecioOferta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPrecioOfertaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPrecioOfertaFocusLost(evt);
            }
        });
        txtPrecioOferta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioOfertaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblBonificacion, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                    .addComponent(lbl_PrecioLista, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                    .addComponent(lbl_IVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_PVP, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                    .addComponent(lbl_Ganancia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_PrecioCosto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkOferta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtPorcentajeOferta, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPorcentajePrecioBonificado)
                    .addComponent(cmbIVAPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGananciaPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPrecioBonificado)
                    .addComponent(txtPrecioLista, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                    .addComponent(txtIVANeto, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                    .addComponent(txtPVP)
                    .addComponent(txtGananciaNeto)
                    .addComponent(txtPrecioCosto)
                    .addComponent(txtPrecioOferta))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(chk_Precios)
                .addGap(331, 331, 331))
        );

        panel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtGananciaNeto, txtIVANeto, txtPVP, txtPrecioCosto, txtPrecioLista});

        panel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmbIVAPorcentaje, txtGananciaPorcentaje, txtPorcentajeOferta, txtPorcentajePrecioBonificado});

        panel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblBonificacion, lbl_Ganancia, lbl_IVA, lbl_PVP, lbl_PrecioCosto, lbl_PrecioLista});

        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chk_Precios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PrecioCosto)
                    .addComponent(txtPrecioCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Ganancia)
                    .addComponent(txtGananciaPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGananciaNeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PVP)
                    .addComponent(txtPVP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_IVA)
                    .addComponent(cmbIVAPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIVANeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrecioLista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_PrecioLista))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPorcentajePrecioBonificado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioBonificado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBonificacion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPorcentajeOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtGananciaNeto, txtIVANeto, txtPVP, txtPrecioCosto, txtPrecioLista});

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.setEnabled(false);
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        lbl_Indicaciones.setText("Productos que se van a modificar:");

        panel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkRecargoDescuento.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkRecargoDescuentoItemStateChanged(evt);
            }
        });

        bgRecargoDescuento.add(rbRecargo);
        rbRecargo.setText("% Recargo");
        rbRecargo.setEnabled(false);

        bgRecargoDescuento.add(rbDescuento);
        rbDescuento.setText("% Descuento");
        rbDescuento.setEnabled(false);

        txtDescuentoRecargoPorcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtDescuentoRecargoPorcentaje.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDescuentoRecargoPorcentaje.setText("0");
        txtDescuentoRecargoPorcentaje.setEnabled(false);
        txtDescuentoRecargoPorcentaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDescuentoRecargoPorcentajeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDescuentoRecargoPorcentajeFocusLost(evt);
            }
        });

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkRecargoDescuento)
                    .addComponent(rbDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDescuentoRecargoPorcentaje)
                    .addComponent(rbRecargo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkRecargoDescuento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbRecargo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbDescuento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescuentoRecargoPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelCantidades.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblVentaCantidadMinima.setForeground(java.awt.Color.lightGray);
        lblVentaCantidadMinima.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblVentaCantidadMinima.setText("Venta x Cant.");

        txtVentaCantidadMinima.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat(""))));
        txtVentaCantidadMinima.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVentaCantidadMinima.setText("0");
        txtVentaCantidadMinima.setEnabled(false);
        txtVentaCantidadMinima.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVentaCantidadMinimaFocusGained(evt);
            }
        });

        chkVentaMinima.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkVentaMinimaItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelCantidadesLayout = new javax.swing.GroupLayout(panelCantidades);
        panelCantidades.setLayout(panelCantidadesLayout);
        panelCantidadesLayout.setHorizontalGroup(
            panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCantidadesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVentaCantidadMinima)
                    .addGroup(panelCantidadesLayout.createSequentialGroup()
                        .addGroup(panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkVentaMinima)
                            .addComponent(lblVentaCantidadMinima, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 29, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelCantidadesLayout.setVerticalGroup(
            panelCantidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCantidadesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkVentaMinima)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblVentaCantidadMinima)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtVentaCantidadMinima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp_ProductosAModificar)
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_Indicaciones)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btn_Guardar))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(panelCantidades, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_Indicaciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_ProductosAModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelCantidades, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Guardar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        String mensajeError = "";
        ProductosParaActualizar productosParaActualizar = ProductosParaActualizar.builder().build();
        if (chk_UnidadDeMedida.isSelected()) {
            if (cmb_Medida.getSelectedItem() != null) {
                productosParaActualizar.setIdMedida(((Medida) cmb_Medida.getSelectedItem()).getIdMedida());
            } else {
                mensajeError = mensajeError.concat(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_producto_vacio_medida") + "\n");
            }
        }
        if (chk_Rubro.isSelected()) {
            if (cmb_Rubro.getSelectedItem() != null) {
                productosParaActualizar.setIdRubro(((Rubro) cmb_Rubro.getSelectedItem()).getIdRubro());
            } else {
                mensajeError = mensajeError.concat(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_producto_vacio_rubro") + "\n");
            }
        }
        if (chk_Proveedor.isSelected() && proveedorSeleccionado == null) {
            mensajeError = mensajeError.concat(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_proveedor") + "\n");
        }
        if (!mensajeError.isEmpty()) {
            JOptionPane.showMessageDialog(this, mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            if (chk_Precios.isSelected() == true) {
                productosParaActualizar.setPrecioCosto(new BigDecimal(txtPrecioCosto.getValue().toString()));
                productosParaActualizar.setGananciaPorcentaje(new BigDecimal(txtGananciaPorcentaje.getValue().toString()));
                productosParaActualizar.setIvaPorcentaje(new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()));
                productosParaActualizar.setPorcentajeBonificacionPrecio(new BigDecimal(txtPorcentajePrecioBonificado.getValue().toString()));
            }
            if (chkVisibilidad.isSelected() == true) {
                productosParaActualizar.setPublico(rbPublico.isSelected());
            }
            if (chkCatalogo.isSelected() == true) {
                productosParaActualizar.setParaCatalogo(rbCatalogoSi.isSelected());
            }
            if (chkRecargoDescuento.isSelected() == true) {
                if (rbRecargo.isSelected()) {
                    productosParaActualizar.setDescuentoRecargoPorcentaje(new BigDecimal(txtDescuentoRecargoPorcentaje.getValue().toString()));
                } else if (rbDescuento.isSelected()) {
                    productosParaActualizar.setDescuentoRecargoPorcentaje(new BigDecimal(txtDescuentoRecargoPorcentaje.getValue().toString())
                            .multiply(new BigDecimal(-1L)));
                }
            }
            if (chkVentaMinima.isSelected() == true) {
                productosParaActualizar.setCantidadVentaMinima(new BigDecimal(txtVentaCantidadMinima.getValue().toString()));
            }
            if (chkOferta.isSelected() == true) {
                productosParaActualizar.setPorcentajeBonificacionOferta(new BigDecimal(txtPorcentajeOferta.getValue().toString()));
            }
            try {
                long[] idsProductos = new long[productosParaModificar.size()];
                int i = 0;
                for (Producto producto : productosParaModificar) {
                    idsProductos[i] = producto.getIdProducto();
                    i++;
                }
                productosParaActualizar.setIdProducto(idsProductos);
                if (proveedorSeleccionado != null) {
                    productosParaActualizar.setIdProveedor(proveedorSeleccionado.getIdProveedor());
                }
                RestClient.getRestTemplate().put("/productos/multiples?", productosParaActualizar);
                JOptionPane.showMessageDialog(this, "Los productos se modificaron correctamente.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_GuardarActionPerformed

    private void chk_RubroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_RubroItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            cmb_Rubro.setEnabled(true);
        } else {
            cmb_Rubro.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chk_RubroItemStateChanged

    private void chk_ProveedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ProveedorItemStateChanged
        if (chk_Proveedor.isSelected() == true) {
            btnBuscarProveedor.setEnabled(true);
            btnBuscarProveedor.requestFocus();
            txtProveedor.setEnabled(true);
            btn_NuevoProveedor.setEnabled(true);
        } else {
            btnBuscarProveedor.setEnabled(false);
            txtProveedor.setEnabled(false);
            btn_NuevoProveedor.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chk_ProveedorItemStateChanged

    private void chk_UnidadDeMedidaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UnidadDeMedidaItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            cmb_Medida.setEnabled(true);
        } else {
            cmb_Medida.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chk_UnidadDeMedidaItemStateChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.prepararComponentes();
        this.cargarMedidas();   
        this.cargarRubros();
    }//GEN-LAST:event_formWindowOpened

    private void cmbIVAPorcentajeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbIVAPorcentajeItemStateChanged
        this.calcularIVANeto();
        this.calcularPrecioLista();
        this.calcularPrecioOferta();
        txtPrecioBonificado.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajePrecioBonificado.getValue().toString())));
    }//GEN-LAST:event_cmbIVAPorcentajeItemStateChanged

    private void chk_PreciosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_PreciosItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            lbl_PrecioCosto.setForeground(Color.BLACK);
            txtPrecioCosto.setEnabled(true);
            lbl_Ganancia.setForeground(Color.BLACK);
            txtGananciaPorcentaje.setEnabled(true);
            txtGananciaNeto.setEnabled(true);
            lbl_PVP.setForeground(Color.BLACK);
            txtPVP.setEnabled(true);
            lbl_IVA.setForeground(Color.BLACK);
            cmbIVAPorcentaje.setEnabled(true);
            txtIVANeto.setEnabled(true);
            lbl_PrecioLista.setForeground(Color.BLACK);
            txtPrecioLista.setEnabled(true);
            rbRecargo.setEnabled(false);
            rbDescuento.setEnabled(false);
            txtDescuentoRecargoPorcentaje.setEnabled(false);
            chkRecargoDescuento.setSelected(false);
            lblBonificacion.setForeground(Color.BLACK);
            txtPorcentajePrecioBonificado.setEnabled(true);
            txtPrecioBonificado.setEnabled(true);
            chkOferta.setEnabled(true);
            if (chkOferta.isSelected()) {
                txtPorcentajeOferta.setEnabled(true);
                txtPrecioOferta.setEnabled(true);
            }
        } else {
            lbl_PrecioCosto.setForeground(Color.LIGHT_GRAY);
            txtPrecioCosto.setEnabled(false);
            lbl_Ganancia.setForeground(Color.LIGHT_GRAY);
            txtGananciaPorcentaje.setEnabled(false);
            txtGananciaNeto.setEnabled(false);
            lbl_PVP.setForeground(Color.LIGHT_GRAY);
            txtPVP.setEnabled(false);
            lbl_IVA.setForeground(Color.LIGHT_GRAY);
            cmbIVAPorcentaje.setEnabled(false);
            txtIVANeto.setEnabled(false);
            lbl_PrecioLista.setForeground(Color.LIGHT_GRAY);
            txtPrecioLista.setEnabled(false);
            lblBonificacion.setForeground(Color.LIGHT_GRAY);
            txtPorcentajePrecioBonificado.setEnabled(false);
            txtPrecioBonificado.setEnabled(false);
            chkOferta.setEnabled(false);
            txtPorcentajeOferta.setEnabled(false);
            txtPrecioOferta.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chk_PreciosItemStateChanged

    private void txtPVPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPVPFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPVP.selectAll();
        });
    }//GEN-LAST:event_txtPVPFocusGained

    private void txtPVPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPVPActionPerformed
        this.calcularGananciaPorcentaje();
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularIVANeto();
        this.calcularPrecioLista();
        this.calcularPrecioOferta();
        txtPrecioBonificado.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajePrecioBonificado.getValue().toString())));
    }//GEN-LAST:event_txtPVPActionPerformed

    private void txtGananciaPorcentajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGananciaPorcentajeFocusGained
        SwingUtilities.invokeLater(() -> {
            txtGananciaPorcentaje.selectAll();
        });
    }//GEN-LAST:event_txtGananciaPorcentajeFocusGained

    private void txtGananciaPorcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGananciaPorcentajeActionPerformed
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularGananciaPorcentaje();
        this.calcularIVANeto();
        this.calcularPrecioLista();
        this.calcularPrecioOferta();
        txtPrecioBonificado.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajePrecioBonificado.getValue().toString())));
    }//GEN-LAST:event_txtGananciaPorcentajeActionPerformed

    private void txtPrecioCostoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioCostoFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPrecioCosto.selectAll();
        });
    }//GEN-LAST:event_txtPrecioCostoFocusGained

    private void txtPrecioCostoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioCostoActionPerformed
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularIVANeto();
        this.calcularPrecioLista();  
        this.calcularPrecioOferta();
        txtPrecioBonificado.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajePrecioBonificado.getValue().toString())));
    }//GEN-LAST:event_txtPrecioCostoActionPerformed

    private void txtPrecioListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioListaActionPerformed
        this.calcularGananciaPorcentajeSegunPrecioDeLista();
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularIVANeto();
        this.calcularPrecioLista();
        this.calcularPrecioOferta();
        txtPrecioBonificado.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajePrecioBonificado.getValue().toString())));
    }//GEN-LAST:event_txtPrecioListaActionPerformed

    private void txtPrecioListaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioListaFocusGained
        precioListaAnterior = new BigDecimal(txtPrecioLista.getValue().toString());
        SwingUtilities.invokeLater(() -> {
            txtPrecioLista.selectAll();
        });
    }//GEN-LAST:event_txtPrecioListaFocusGained

    private void txtPrecioCostoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioCostoFocusLost
        try {
            txtPrecioCosto.commitEdit();
        } catch (ParseException ex) {}                
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularIVANeto();
        this.calcularPrecioLista();
        this.calcularPrecioOferta();
        txtPrecioBonificado.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajePrecioBonificado.getValue().toString())));
    }//GEN-LAST:event_txtPrecioCostoFocusLost

    private void txtPVPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPVPFocusLost
        try {
            txtPVP.commitEdit();
        } catch (ParseException ex) {}                
        this.calcularGananciaPorcentaje();
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularIVANeto();
        this.calcularPrecioLista();
        this.calcularPrecioOferta();
        txtPrecioBonificado.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajePrecioBonificado.getValue().toString())));
    }//GEN-LAST:event_txtPVPFocusLost

    private void txtGananciaPorcentajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGananciaPorcentajeFocusLost
        try {
            txtGananciaPorcentaje.commitEdit();
        } catch (ParseException ex) {}                
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularGananciaPorcentaje();
        this.calcularIVANeto();
        this.calcularPrecioLista();
        this.calcularPrecioOferta();
        txtPrecioBonificado.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajePrecioBonificado.getValue().toString())));
    }//GEN-LAST:event_txtGananciaPorcentajeFocusLost

    private void txtPrecioListaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioListaFocusLost
        try {
            txtPrecioLista.commitEdit();
        } catch (ParseException ex) {}        
        this.calcularGananciaPorcentajeSegunPrecioDeLista();
        this.calcularGananciaNeto();
        this.calcularPVP();
        this.calcularIVANeto();
        this.calcularPrecioLista();
        this.calcularPrecioOferta();
        txtPrecioBonificado.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajePrecioBonificado.getValue().toString())));
    }//GEN-LAST:event_txtPrecioListaFocusLost

    private void chkRecargoDescuentoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkRecargoDescuentoItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            lbl_PrecioCosto.setForeground(Color.LIGHT_GRAY);
            txtPrecioCosto.setEnabled(false);
            lbl_Ganancia.setForeground(Color.LIGHT_GRAY);
            txtGananciaPorcentaje.setEnabled(false);
            txtGananciaNeto.setEnabled(false);
            lbl_PVP.setForeground(Color.LIGHT_GRAY);
            txtPVP.setEnabled(false);
            lbl_IVA.setForeground(Color.LIGHT_GRAY);
            cmbIVAPorcentaje.setEnabled(false);
            txtIVANeto.setEnabled(false);
            lbl_PrecioLista.setForeground(Color.LIGHT_GRAY);
            txtPrecioLista.setEnabled(false);
            rbRecargo.setEnabled(true);
            rbDescuento.setEnabled(true);
            txtDescuentoRecargoPorcentaje.setEnabled(true);
            chk_Precios.setSelected(false);
            txtPorcentajeOferta.setEnabled(false);
            txtPrecioOferta.setEnabled(false);
        } else {
            rbRecargo.setEnabled(false);
            rbDescuento.setEnabled(false);
            txtDescuentoRecargoPorcentaje.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chkRecargoDescuentoItemStateChanged

    private void txtDescuentoRecargoPorcentajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDescuentoRecargoPorcentajeFocusGained
        SwingUtilities.invokeLater(() -> {
            txtDescuentoRecargoPorcentaje.selectAll();
        });
    }//GEN-LAST:event_txtDescuentoRecargoPorcentajeFocusGained

    private void txtDescuentoRecargoPorcentajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDescuentoRecargoPorcentajeFocusLost
        try {
            txtDescuentoRecargoPorcentaje.commitEdit();
        } catch (ParseException ex) {}
    }//GEN-LAST:event_txtDescuentoRecargoPorcentajeFocusLost

    private void chkVisibilidadItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkVisibilidadItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            rbPublico.setEnabled(true);
            rbPrivado.setEnabled(true);
        } else {
            rbPublico.setEnabled(false);
            rbPrivado.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chkVisibilidadItemStateChanged

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

    private void txtPorcentajePrecioBonificadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajePrecioBonificadoFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPorcentajePrecioBonificado.selectAll();
        });
    }//GEN-LAST:event_txtPorcentajePrecioBonificadoFocusGained

    private void txtPorcentajePrecioBonificadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajePrecioBonificadoFocusLost
        try {
            txtPorcentajePrecioBonificado.commitEdit();
        } catch (ParseException ex) {
        }
        txtPrecioBonificado.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajePrecioBonificado.getValue().toString())));
    }//GEN-LAST:event_txtPorcentajePrecioBonificadoFocusLost

    private void txtPorcentajePrecioBonificadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPorcentajePrecioBonificadoActionPerformed
        txtPrecioBonificado.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajePrecioBonificado.getValue().toString())));
    }//GEN-LAST:event_txtPorcentajePrecioBonificadoActionPerformed

    private void txtPrecioBonificadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioBonificadoFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPrecioBonificado.selectAll();
        });
    }//GEN-LAST:event_txtPrecioBonificadoFocusGained

    private void txtPrecioBonificadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioBonificadoFocusLost
        try {
            txtPrecioBonificado.commitEdit();
        } catch (ParseException ex) {
        }
        txtPorcentajePrecioBonificado.setValue(this.calcularPorcentaje(new BigDecimal(txtPrecioBonificado.getValue().toString()), new BigDecimal(txtPrecioLista.getValue().toString())));
    }//GEN-LAST:event_txtPrecioBonificadoFocusLost

    private void txtPrecioBonificadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioBonificadoActionPerformed
        try {
            txtPrecioBonificado.commitEdit();
        } catch (ParseException ex) {
        }
        txtPorcentajePrecioBonificado.setValue(this.calcularPorcentaje(new BigDecimal(txtPrecioBonificado.getValue().toString()), new BigDecimal(txtPrecioLista.getValue().toString())));
    }//GEN-LAST:event_txtPrecioBonificadoActionPerformed

    private void txtVentaCantidadMinimaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVentaCantidadMinimaFocusGained
        SwingUtilities.invokeLater(() -> {
            txtVentaCantidadMinima.selectAll();
        });
    }//GEN-LAST:event_txtVentaCantidadMinimaFocusGained

    private void chkVentaMinimaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkVentaMinimaItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            lblVentaCantidadMinima.setForeground(Color.BLACK);
            txtVentaCantidadMinima.setEnabled(true);
        } else {
            lblVentaCantidadMinima.setForeground(Color.LIGHT_GRAY);
            txtVentaCantidadMinima.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chkVentaMinimaItemStateChanged

    private void chkOfertaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkOfertaItemStateChanged
        if (chkOferta.isSelected()) {
            txtPorcentajeOferta.setEnabled(true);
            txtPrecioOferta.setEnabled(true);
            txtPrecioOferta.setValue(this.calcularPrecioPorcentaje(txtPrecioLista.getValue() != null
                ? new BigDecimal(txtPrecioLista.getValue().toString()) 
                    : BigDecimal.ZERO, new BigDecimal(txtPorcentajeOferta.getValue().toString())));
        } else {
            txtPorcentajeOferta.setEnabled(false);
            txtPrecioOferta.setEnabled(false);
        }
    }//GEN-LAST:event_chkOfertaItemStateChanged

    private void txtPorcentajeOfertaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajeOfertaFocusGained
        SwingUtilities.invokeLater(() -> txtPorcentajeOferta.selectAll());
    }//GEN-LAST:event_txtPorcentajeOfertaFocusGained

    private void txtPorcentajeOfertaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajeOfertaFocusLost
        try {
            txtPorcentajeOferta.commitEdit();
        } catch (ParseException ex) {
        }
        txtPrecioOferta.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajeOferta.getValue().toString())));
    }//GEN-LAST:event_txtPorcentajeOfertaFocusLost

    private void txtPorcentajeOfertaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPorcentajeOfertaActionPerformed
        txtPrecioOferta.setValue(this.calcularPrecioPorcentaje(new BigDecimal(txtPrecioLista.getValue().toString()), new BigDecimal(txtPorcentajeOferta.getValue().toString())));
    }//GEN-LAST:event_txtPorcentajeOfertaActionPerformed

    private void txtPrecioOfertaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioOfertaFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPrecioOferta.selectAll();
        });
    }//GEN-LAST:event_txtPrecioOfertaFocusGained

    private void txtPrecioOfertaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioOfertaFocusLost
        try {
            txtPrecioOferta.commitEdit();
        } catch (ParseException ex) {
        }
        txtPorcentajeOferta.setValue(this.calcularPorcentaje(new BigDecimal(txtPrecioOferta.getValue().toString()), new BigDecimal(txtPrecioLista.getValue().toString())));
    }//GEN-LAST:event_txtPrecioOfertaFocusLost

    private void txtPrecioOfertaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioOfertaActionPerformed
        try {
            txtPrecioOferta.commitEdit();
        } catch (ParseException ex) {
        }
        txtPorcentajeOferta.setValue(this.calcularPorcentaje(new BigDecimal(txtPrecioOferta.getValue().toString()), new BigDecimal(txtPrecioLista.getValue().toString())));
    }//GEN-LAST:event_txtPrecioOfertaActionPerformed

    private void chkCatalogoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkCatalogoItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            rbCatalogoSi.setEnabled(true);
            rbCatalogoNo.setEnabled(true);
        } else {
            rbCatalogoSi.setEnabled(false);
            rbCatalogoNo.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chkCatalogoItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgCatalogo;
    private javax.swing.ButtonGroup bgRecargoDescuento;
    private javax.swing.ButtonGroup bgVisibilidad;
    private javax.swing.JButton btnBuscarProveedor;
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JButton btn_NuevoProveedor;
    private javax.swing.JCheckBox chkCatalogo;
    private javax.swing.JCheckBox chkOferta;
    private javax.swing.JCheckBox chkRecargoDescuento;
    private javax.swing.JCheckBox chkVentaMinima;
    private javax.swing.JCheckBox chkVisibilidad;
    private javax.swing.JCheckBox chk_Precios;
    private javax.swing.JCheckBox chk_Proveedor;
    private javax.swing.JCheckBox chk_Rubro;
    private javax.swing.JCheckBox chk_UnidadDeMedida;
    private javax.swing.JComboBox cmbIVAPorcentaje;
    private javax.swing.JComboBox cmb_Medida;
    private javax.swing.JComboBox cmb_Rubro;
    private javax.swing.JLabel lblBonificacion;
    private javax.swing.JLabel lblVentaCantidadMinima;
    private javax.swing.JLabel lbl_Ganancia;
    private javax.swing.JLabel lbl_IVA;
    private javax.swing.JLabel lbl_Indicaciones;
    private javax.swing.JLabel lbl_PVP;
    private javax.swing.JLabel lbl_PrecioCosto;
    private javax.swing.JLabel lbl_PrecioLista;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JPanel panel3;
    private javax.swing.JPanel panelCantidades;
    private javax.swing.JRadioButton rbCatalogoNo;
    private javax.swing.JRadioButton rbCatalogoSi;
    private javax.swing.JRadioButton rbDescuento;
    private javax.swing.JRadioButton rbPrivado;
    private javax.swing.JRadioButton rbPublico;
    private javax.swing.JRadioButton rbRecargo;
    private javax.swing.JScrollPane sp_ProductosAModificar;
    private javax.swing.JTable tbl_ProductosAModifcar;
    private javax.swing.JFormattedTextField txtDescuentoRecargoPorcentaje;
    private javax.swing.JFormattedTextField txtGananciaNeto;
    private javax.swing.JFormattedTextField txtGananciaPorcentaje;
    private javax.swing.JFormattedTextField txtIVANeto;
    private javax.swing.JFormattedTextField txtPVP;
    private javax.swing.JFormattedTextField txtPorcentajeOferta;
    private javax.swing.JFormattedTextField txtPorcentajePrecioBonificado;
    private javax.swing.JFormattedTextField txtPrecioBonificado;
    private javax.swing.JFormattedTextField txtPrecioCosto;
    private javax.swing.JFormattedTextField txtPrecioLista;
    private javax.swing.JFormattedTextField txtPrecioOferta;
    private javax.swing.JTextField txtProveedor;
    private javax.swing.JFormattedTextField txtVentaCantidadMinima;
    // End of variables declaration//GEN-END:variables
}
