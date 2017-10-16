package sic.vista.swing;

import java.awt.Color;
import java.awt.event.ItemEvent;
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
import sic.modelo.Rubro;

public class ModificacionProductosBulkGUI extends JDialog {

    private final List<Producto> productosParaModificar;
    private ModeloTabla modeloTablaProductos;
    private double precioDeCosto;
    private double gananciaPorcentaje;
    private double gananciaNeto;
    private double pvp;
    private double IVANeto;
    private double precioDeLista;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ModificacionProductosBulkGUI(List<Producto> productosParaModificar) {
        this.initComponents();
        this.setIcon();        
        this.productosParaModificar = productosParaModificar;
        this.cargarResultadosAlTable();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(ModificacionProductosBulkGUI.class.getResource("/sic/icons/Product_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarMedidas() {
        try {
            cmb_Medida.removeAllItems();
            List<Medida> medidas = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/medidas/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
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
                .getForObject("/rubros/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                Rubro[].class)));
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

    private void cargarProveedores() {
        try {
            cmb_Proveedor.removeAllItems();
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

    private void cargarComboBoxIVA() {
        cmb_IVA_Porcentaje.removeAllItems();
        cmb_IVA_Porcentaje.addItem((double) 0);
        cmb_IVA_Porcentaje.addItem(10.5);
        cmb_IVA_Porcentaje.addItem((double) 21);
    }

    private void prepararComponentes() {
        txt_PrecioCosto.setValue(0.0);
        txt_PVP.setValue(0.0);
        txt_IVA_Neto.setValue(0.0);
        txt_Ganancia_Porcentaje.setValue(0.0);
        txt_Ganancia_Neto.setValue(0.0);
        txt_PrecioLista.setValue(0.0);
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
        productosParaModificar.stream().map((producto) -> {
            Object[] fila = new Object[23];
            fila[0] = producto.getCodigo();
            fila[1] = producto.getDescripcion();
            return fila;
        }).forEach((fila) -> {
            modeloTablaProductos.addRow(fila);
        });
        tbl_ProductosAModifcar.setModel(modeloTablaProductos);
    }

    private void habilitarBotonGuardar() {
        if (chk_Precios.isSelected() == true
                || chk_Proveedor.isSelected() == true
                || chk_Rubro.isSelected() == true
                || chk_UnidadDeMedida.isSelected() == true) {
            btn_Guardar.setEnabled(true);
        } else {
            btn_Guardar.setEnabled(false);
        }
    }

    private void validarComponentesDePrecios() {
        try {
            txt_PrecioCosto.commitEdit();
            txt_PVP.commitEdit();
            txt_IVA_Neto.commitEdit();
            txt_Ganancia_Porcentaje.commitEdit();
            txt_Ganancia_Neto.commitEdit();
            txt_PrecioLista.commitEdit();
        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage());
        }
    }
    
    private void calcularGananciaPorcentaje() {
        pvp = Double.parseDouble(txt_PVP.getValue().toString());
        gananciaPorcentaje = RestClient.getRestTemplate()
                .getForObject("/productos/ganancia-porcentaje?"
                        + "precioCosto=" + Double.parseDouble(txt_PrecioCosto.getValue().toString())
                        + "&pvp=" + pvp,
                        double.class);
        txt_Ganancia_Porcentaje.setValue(gananciaPorcentaje);
    }
    
    private void calcularGananciaNeto() {
        gananciaNeto = RestClient.getRestTemplate()
                .getForObject("/productos/ganancia-neto?"
                        + "precioCosto=" + Double.parseDouble(txt_PrecioCosto.getValue().toString())
                        + "&gananciaPorcentaje=" + gananciaPorcentaje,
                        double.class);
        txt_Ganancia_Neto.setValue(gananciaNeto);
    }
    
    private void calcularPVP() {
        pvp = RestClient.getRestTemplate()
                .getForObject("/productos/pvp?"
                        + "precioCosto=" + Double.parseDouble(txt_PrecioCosto.getValue().toString())
                        + "&gananciaPorcentaje=" + gananciaPorcentaje,
                        double.class);
        txt_PVP.setValue(pvp);
    }
    
    private void calcularIVANeto() {
        IVANeto = RestClient.getRestTemplate()
                .getForObject("/productos/iva-neto?"
                        + "pvp=" + pvp
                        + "&ivaPorcentaje=" + Double.parseDouble(cmb_IVA_Porcentaje.getSelectedItem().toString()),
                        double.class);
        txt_IVA_Neto.setValue(IVANeto);
    }
    
    private void calcularPrecioLista() {
        precioDeLista = RestClient.getRestTemplate()
                .getForObject("/productos/precio-lista?"
                        + "pvp=" + pvp
                        + "&ivaPorcentaje=" + Double.parseDouble(cmb_IVA_Porcentaje.getSelectedItem().toString()),
                        double.class);
        txt_PrecioLista.setValue(precioDeLista);
    }
    
    private void calcularGananciaSegunPrecioDeLista() {      
        gananciaPorcentaje = RestClient.getRestTemplate()
                .getForObject("/productos/ganancia-porcentaje?ascendente=true"
                        + "&precioDeLista=" + Double.parseDouble(txt_PrecioLista.getValue().toString())
                        + "&precioDeListaAnterior=" + precioDeLista
                        + "&pvp=" + pvp
                        + "&ivaPorcentaje=" + Double.parseDouble(cmb_IVA_Porcentaje.getSelectedItem().toString())
                        + "&precioCosto=" + precioDeCosto,
                        double.class);
        txt_Ganancia_Porcentaje.setValue(gananciaPorcentaje);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sp_ProductosAModificar = new javax.swing.JScrollPane();
        tbl_ProductosAModifcar = new javax.swing.JTable();
        panel1 = new javax.swing.JPanel();
        cmb_Rubro = new javax.swing.JComboBox();
        btn_Rubros = new javax.swing.JButton();
        cmb_Proveedor = new javax.swing.JComboBox();
        btn_NuevoProveedor = new javax.swing.JButton();
        btn_Medidas = new javax.swing.JButton();
        cmb_Medida = new javax.swing.JComboBox();
        chk_Rubro = new javax.swing.JCheckBox();
        chk_Proveedor = new javax.swing.JCheckBox();
        chk_UnidadDeMedida = new javax.swing.JCheckBox();
        panel2 = new javax.swing.JPanel();
        lbl_PrecioCosto = new javax.swing.JLabel();
        lbl_Ganancia = new javax.swing.JLabel();
        lbl_PrecioLista = new javax.swing.JLabel();
        txt_PrecioCosto = new javax.swing.JFormattedTextField();
        txt_Ganancia_Porcentaje = new javax.swing.JFormattedTextField();
        txt_PrecioLista = new javax.swing.JFormattedTextField();
        lbl_IVA = new javax.swing.JLabel();
        txt_IVA_Neto = new javax.swing.JFormattedTextField();
        txt_Ganancia_Neto = new javax.swing.JFormattedTextField();
        lbl_PVP = new javax.swing.JLabel();
        txt_PVP = new javax.swing.JFormattedTextField();
        chk_Precios = new javax.swing.JCheckBox();
        cmb_IVA_Porcentaje = new javax.swing.JComboBox();
        btn_Guardar = new javax.swing.JButton();
        lbl_Indicaciones = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modificar varios Productos");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tbl_ProductosAModifcar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_ProductosAModifcar.setFocusable(false);
        sp_ProductosAModificar.setViewportView(tbl_ProductosAModifcar);

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        cmb_Rubro.setEnabled(false);

        btn_Rubros.setForeground(java.awt.Color.blue);
        btn_Rubros.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddBlock.png"))); // NOI18N
        btn_Rubros.setText("Nuevo");
        btn_Rubros.setEnabled(false);
        btn_Rubros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RubrosActionPerformed(evt);
            }
        });

        cmb_Proveedor.setEnabled(false);

        btn_NuevoProveedor.setForeground(java.awt.Color.blue);
        btn_NuevoProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProviderBag_16x16.png"))); // NOI18N
        btn_NuevoProveedor.setText("Nuevo");
        btn_NuevoProveedor.setEnabled(false);
        btn_NuevoProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoProveedorActionPerformed(evt);
            }
        });

        btn_Medidas.setForeground(java.awt.Color.blue);
        btn_Medidas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddRuler_16x16.png"))); // NOI18N
        btn_Medidas.setText("Nueva");
        btn_Medidas.setEnabled(false);
        btn_Medidas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_MedidasActionPerformed(evt);
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

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chk_UnidadDeMedida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Proveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Rubro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_Medida, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_Proveedor, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_Rubro, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_NuevoProveedor)
                    .addComponent(btn_Medidas)
                    .addComponent(btn_Rubros))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Rubro)
                    .addComponent(cmb_Rubro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Rubros, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Proveedor)
                    .addComponent(cmb_Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_UnidadDeMedida)
                    .addComponent(cmb_Medida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Medidas, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Rubros, cmb_Rubro});

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevoProveedor, cmb_Proveedor});

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Medidas, cmb_Medida});

        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_PrecioCosto.setForeground(new java.awt.Color(192, 192, 192));
        lbl_PrecioCosto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PrecioCosto.setText("Precio de Costo:");

        lbl_Ganancia.setForeground(new java.awt.Color(192, 192, 192));
        lbl_Ganancia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Ganancia.setText("Ganancia (%):");

        lbl_PrecioLista.setForeground(new java.awt.Color(192, 192, 192));
        lbl_PrecioLista.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PrecioLista.setText("Precio de Lista:");

        txt_PrecioCosto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_PrecioCosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_PrecioCosto.setText("0");
        txt_PrecioCosto.setEnabled(false);
        txt_PrecioCosto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_PrecioCostoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_PrecioCostoFocusLost(evt);
            }
        });
        txt_PrecioCosto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_PrecioCostoActionPerformed(evt);
            }
        });

        txt_Ganancia_Porcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Ganancia_Porcentaje.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Ganancia_Porcentaje.setText("0");
        txt_Ganancia_Porcentaje.setEnabled(false);
        txt_Ganancia_Porcentaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Ganancia_PorcentajeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Ganancia_PorcentajeFocusLost(evt);
            }
        });
        txt_Ganancia_Porcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_Ganancia_PorcentajeActionPerformed(evt);
            }
        });

        txt_PrecioLista.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_PrecioLista.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_PrecioLista.setText("0");
        txt_PrecioLista.setEnabled(false);
        txt_PrecioLista.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_PrecioListaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_PrecioListaFocusLost(evt);
            }
        });
        txt_PrecioLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_PrecioListaActionPerformed(evt);
            }
        });

        lbl_IVA.setForeground(new java.awt.Color(192, 192, 192));
        lbl_IVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_IVA.setText("I.V.A. (%):");

        txt_IVA_Neto.setEditable(false);
        txt_IVA_Neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_IVA_Neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_IVA_Neto.setText("0");
        txt_IVA_Neto.setEnabled(false);
        txt_IVA_Neto.setFocusable(false);

        txt_Ganancia_Neto.setEditable(false);
        txt_Ganancia_Neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Ganancia_Neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Ganancia_Neto.setText("0");
        txt_Ganancia_Neto.setEnabled(false);
        txt_Ganancia_Neto.setFocusable(false);

        lbl_PVP.setForeground(new java.awt.Color(192, 192, 192));
        lbl_PVP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PVP.setText("Precio Venta Público:");

        txt_PVP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_PVP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_PVP.setText("0");
        txt_PVP.setEnabled(false);
        txt_PVP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_PVPFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_PVPFocusLost(evt);
            }
        });
        txt_PVP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_PVPActionPerformed(evt);
            }
        });

        chk_Precios.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_PreciosItemStateChanged(evt);
            }
        });

        cmb_IVA_Porcentaje.setEnabled(false);
        cmb_IVA_Porcentaje.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_IVA_PorcentajeItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(panel2Layout.createSequentialGroup()
                            .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panel2Layout.createSequentialGroup()
                                    .addComponent(chk_Precios)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                    .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(lbl_PrecioCosto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lbl_Ganancia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGap(12, 12, 12)))
                            .addComponent(txt_Ganancia_Porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panel2Layout.createSequentialGroup()
                            .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lbl_PVP, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                                .addComponent(lbl_IVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmb_IVA_Porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lbl_PrecioLista, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_PrecioLista, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                    .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txt_IVA_Neto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addComponent(txt_PVP, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txt_Ganancia_Neto, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txt_PrecioCosto, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txt_Ganancia_Neto, txt_IVA_Neto, txt_PVP, txt_PrecioCosto, txt_PrecioLista});

        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chk_Precios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PrecioCosto)
                    .addComponent(txt_PrecioCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Ganancia)
                    .addComponent(txt_Ganancia_Porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Ganancia_Neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PVP)
                    .addComponent(txt_PVP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_IVA)
                    .addComponent(cmb_IVA_Porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_IVA_Neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_PrecioLista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_PrecioLista))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txt_Ganancia_Neto, txt_IVA_Neto, txt_PVP, txt_PrecioCosto, txt_PrecioLista});

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp_ProductosAModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_Guardar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_Indicaciones)
                            .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_Indicaciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_ProductosAModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(btn_Guardar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_RubrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RubrosActionPerformed
        DetalleRubroGUI gui_DetalleRubro = new DetalleRubroGUI();
        gui_DetalleRubro.setModal(true);
        gui_DetalleRubro.setLocationRelativeTo(this);
        gui_DetalleRubro.setVisible(true);
        this.cargarRubros();
    }//GEN-LAST:event_btn_RubrosActionPerformed

    private void btn_NuevoProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoProveedorActionPerformed
        DetalleProveedorGUI gui_DetalleProveedor = new DetalleProveedorGUI();
        gui_DetalleProveedor.setModal(true);
        gui_DetalleProveedor.setLocationRelativeTo(this);
        gui_DetalleProveedor.setVisible(true);
        this.cargarProveedores();
    }//GEN-LAST:event_btn_NuevoProveedorActionPerformed

    private void btn_MedidasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_MedidasActionPerformed
        DetalleMedidaGUI gui_DetalleMedida = new DetalleMedidaGUI();
        gui_DetalleMedida.setModal(true);
        gui_DetalleMedida.setLocationRelativeTo(this);
        gui_DetalleMedida.setVisible(true);
        this.cargarMedidas();
    }//GEN-LAST:event_btn_MedidasActionPerformed

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        boolean checkPrecios = false;
        boolean checkMedida = false;
        boolean checkRubro = false;
        boolean checkProveedor = false;
        Medida medida = new Medida();
        Rubro rubro = new Rubro();
        Proveedor proveedor = new Proveedor();
        String preciosProducto = "";
        if (chk_Precios.isSelected() == true) {
            checkPrecios = true;
            preciosProducto = "&precioCosto=" + Double.parseDouble(txt_PrecioCosto.getValue().toString())
                    + "&gananciaPorcentaje=" + Double.parseDouble(txt_Ganancia_Porcentaje.getValue().toString())
                    + "&gananciaNeto=" + Double.parseDouble(txt_Ganancia_Neto.getValue().toString())
                    + "&precioVentaPublico=" + Double.parseDouble(txt_PVP.getValue().toString())
                    + "&IVAPorcentaje=" + Double.parseDouble(cmb_IVA_Porcentaje.getSelectedItem().toString())
                    + "&IVANeto=" + Double.parseDouble(txt_IVA_Neto.getValue().toString())
                    + "&precioLista=" + Double.parseDouble(txt_PrecioLista.getValue().toString());
        }
        if (chk_UnidadDeMedida.isSelected() == true) {
            checkMedida = true;
            medida = (Medida) cmb_Medida.getSelectedItem();
        }
        if (chk_Rubro.isSelected() == true) {
            checkRubro = true;
            rubro = (Rubro) cmb_Rubro.getSelectedItem();
        }
        if (chk_Proveedor.isSelected() == true) {
            checkProveedor = true;
            proveedor = (Proveedor) cmb_Proveedor.getSelectedItem();
        }
        try {
            long[] idsProductos = new long[productosParaModificar.size()];
            int i = 0;
            for (Producto producto : productosParaModificar) {
                idsProductos[i] = producto.getId_Producto();
                i++;
            }
            String uri = "/productos/multiples?idProducto=" 
                    + Arrays.toString(idsProductos).substring(1, Arrays.toString(idsProductos).length() - 1);            
            if (checkMedida) {
                uri += "&idMedida=" + medida.getId_Medida();
            }
            if (checkRubro) {
                uri += "&idRubro=" + rubro.getId_Rubro();
            }
            if (checkProveedor) {
                uri += "&idProveedor=" + proveedor.getId_Proveedor();
            }         
            if (checkPrecios) {
                uri = uri.concat(preciosProducto);
            }
            RestClient.getRestTemplate().put(uri , null);
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
    }//GEN-LAST:event_btn_GuardarActionPerformed

    private void chk_RubroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_RubroItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            cmb_Rubro.setEnabled(true);
            btn_Rubros.setEnabled(true);
        } else {
            cmb_Rubro.setEnabled(false);
            btn_Rubros.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chk_RubroItemStateChanged

    private void chk_ProveedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ProveedorItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            cmb_Proveedor.setEnabled(true);
            btn_NuevoProveedor.setEnabled(true);
        } else {
            cmb_Proveedor.setEnabled(false);
            btn_NuevoProveedor.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chk_ProveedorItemStateChanged

    private void chk_UnidadDeMedidaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UnidadDeMedidaItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            cmb_Medida.setEnabled(true);
            btn_Medidas.setEnabled(true);
        } else {
            cmb_Medida.setEnabled(false);
            btn_Medidas.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chk_UnidadDeMedidaItemStateChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.prepararComponentes();
        this.cargarRubros();
        this.cargarProveedores();
        this.cargarMedidas();
        this.cargarComboBoxIVA();
    }//GEN-LAST:event_formWindowOpened

    private void cmb_IVA_PorcentajeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_IVA_PorcentajeItemStateChanged
        this.validarComponentesDePrecios();
        try {
            this.calcularIVANeto();            
            this.calcularPrecioLista();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_cmb_IVA_PorcentajeItemStateChanged

    private void chk_PreciosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_PreciosItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            lbl_PrecioCosto.setForeground(Color.BLACK);
            txt_PrecioCosto.setEnabled(true);
            lbl_Ganancia.setForeground(Color.BLACK);
            txt_Ganancia_Porcentaje.setEnabled(true);
            txt_Ganancia_Neto.setEnabled(true);
            lbl_PVP.setForeground(Color.BLACK);
            txt_PVP.setEnabled(true);
            lbl_IVA.setForeground(Color.BLACK);
            cmb_IVA_Porcentaje.setEnabled(true);
            txt_IVA_Neto.setEnabled(true);
            lbl_PrecioLista.setForeground(Color.BLACK);
            txt_PrecioLista.setEnabled(true);
        } else {
            lbl_PrecioCosto.setForeground(Color.LIGHT_GRAY);
            txt_PrecioCosto.setEnabled(false);
            lbl_Ganancia.setForeground(Color.LIGHT_GRAY);
            txt_Ganancia_Porcentaje.setEnabled(false);
            txt_Ganancia_Neto.setEnabled(false);
            lbl_PVP.setForeground(Color.LIGHT_GRAY);
            txt_PVP.setEnabled(false);
            lbl_IVA.setForeground(Color.LIGHT_GRAY);
            cmb_IVA_Porcentaje.setEnabled(false);
            txt_IVA_Neto.setEnabled(false);
            lbl_PrecioLista.setForeground(Color.LIGHT_GRAY);
            txt_PrecioLista.setEnabled(false);
        }
        this.habilitarBotonGuardar();
    }//GEN-LAST:event_chk_PreciosItemStateChanged

    private void txt_PVPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PVPFocusLost
        this.txt_PVPActionPerformed(null);
    }//GEN-LAST:event_txt_PVPFocusLost

    private void txt_PVPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PVPFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_PVP.selectAll();
        });
    }//GEN-LAST:event_txt_PVPFocusGained

    private void txt_PVPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_PVPActionPerformed
        this.validarComponentesDePrecios();
        try {
            this.calcularGananciaPorcentaje();
            this.calcularGananciaNeto();
            this.calcularIVANeto();
            this.calcularPrecioLista();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txt_PVPActionPerformed

    private void txt_Ganancia_PorcentajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Ganancia_PorcentajeFocusLost
        this.txt_Ganancia_PorcentajeActionPerformed(null);
    }//GEN-LAST:event_txt_Ganancia_PorcentajeFocusLost

    private void txt_Ganancia_PorcentajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Ganancia_PorcentajeFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_Ganancia_Porcentaje.selectAll();
        });
    }//GEN-LAST:event_txt_Ganancia_PorcentajeFocusGained

    private void txt_Ganancia_PorcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_Ganancia_PorcentajeActionPerformed
        this.validarComponentesDePrecios();
        try {
            gananciaPorcentaje = Double.parseDouble(txt_Ganancia_Porcentaje.getValue().toString());
            this.calcularGananciaNeto();
            this.calcularPVP();
            this.calcularIVANeto();
            this.calcularPrecioLista();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txt_Ganancia_PorcentajeActionPerformed

    private void txt_PrecioCostoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PrecioCostoFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_PrecioCosto.selectAll();
        });
    }//GEN-LAST:event_txt_PrecioCostoFocusGained

    private void txt_PrecioCostoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_PrecioCostoActionPerformed
        this.validarComponentesDePrecios();
        try {
            precioDeCosto = Double.parseDouble(txt_PrecioCosto.getValue().toString());
            this.calcularGananciaNeto();
            this.calcularPVP();
            this.calcularIVANeto();
            this.calcularPrecioLista();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txt_PrecioCostoActionPerformed

    private void txt_PrecioCostoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PrecioCostoFocusLost
        txt_PrecioCostoActionPerformed(null);
    }//GEN-LAST:event_txt_PrecioCostoFocusLost

    private void txt_PrecioListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_PrecioListaActionPerformed
        this.validarComponentesDePrecios();
        try {
            this.calcularGananciaSegunPrecioDeLista();
            this.calcularGananciaNeto();
            this.calcularPVP();
            this.calcularIVANeto();
            this.calcularPrecioLista(); 
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txt_PrecioListaActionPerformed

    private void txt_PrecioListaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PrecioListaFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_PrecioLista.selectAll();
        });
    }//GEN-LAST:event_txt_PrecioListaFocusGained

    private void txt_PrecioListaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PrecioListaFocusLost
        this.txt_PrecioListaActionPerformed(null);
    }//GEN-LAST:event_txt_PrecioListaFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JButton btn_Medidas;
    private javax.swing.JButton btn_NuevoProveedor;
    private javax.swing.JButton btn_Rubros;
    private javax.swing.JCheckBox chk_Precios;
    private javax.swing.JCheckBox chk_Proveedor;
    private javax.swing.JCheckBox chk_Rubro;
    private javax.swing.JCheckBox chk_UnidadDeMedida;
    private javax.swing.JComboBox cmb_IVA_Porcentaje;
    private javax.swing.JComboBox cmb_Medida;
    private javax.swing.JComboBox cmb_Proveedor;
    private javax.swing.JComboBox cmb_Rubro;
    private javax.swing.JLabel lbl_Ganancia;
    private javax.swing.JLabel lbl_IVA;
    private javax.swing.JLabel lbl_Indicaciones;
    private javax.swing.JLabel lbl_PVP;
    private javax.swing.JLabel lbl_PrecioCosto;
    private javax.swing.JLabel lbl_PrecioLista;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JScrollPane sp_ProductosAModificar;
    private javax.swing.JTable tbl_ProductosAModifcar;
    private javax.swing.JFormattedTextField txt_Ganancia_Neto;
    private javax.swing.JFormattedTextField txt_Ganancia_Porcentaje;
    private javax.swing.JFormattedTextField txt_IVA_Neto;
    private javax.swing.JFormattedTextField txt_PVP;
    private javax.swing.JFormattedTextField txt_PrecioCosto;
    private javax.swing.JFormattedTextField txt_PrecioLista;
    // End of variables declaration//GEN-END:variables
}
