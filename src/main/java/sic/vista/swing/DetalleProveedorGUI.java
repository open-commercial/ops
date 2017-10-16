package sic.vista.swing;

import java.util.ArrayList;
import java.util.Arrays;
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
import sic.modelo.CondicionIVA;
import sic.modelo.EmpresaActiva;
import sic.modelo.Localidad;
import sic.modelo.Pais;
import sic.modelo.Proveedor;
import sic.modelo.Provincia;
import sic.modelo.TipoDeOperacion;

public class DetalleProveedorGUI extends JDialog {

    private Proveedor proveedorModificar;
    private final TipoDeOperacion operacion;  
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleProveedorGUI() {
        this.initComponents();
        this.setIcon();
        this.setTitle("Nuevo Proveedor");
        operacion = TipoDeOperacion.ALTA;
    }

    public DetalleProveedorGUI(Proveedor prov) {
        this.initComponents();
        this.setIcon();
        this.setTitle("Modificar Proveedor");
        operacion = TipoDeOperacion.ACTUALIZACION;
        proveedorModificar = prov;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleProveedorGUI.class.getResource("/sic/icons/ProviderBag_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarProveedorParaModificar() {
        txt_Codigo.setText(proveedorModificar.getCodigo());
        txt_RazonSocial.setText(proveedorModificar.getRazonSocial());
        txt_Id_Fiscal.setText(String.valueOf(proveedorModificar.getIdFiscal()));
        cmb_CondicionIVA.setSelectedItem(proveedorModificar.getCondicionIVA());
        txt_Direccion.setText(proveedorModificar.getDireccion());
        cmb_Pais.setSelectedItem(proveedorModificar.getLocalidad().getProvincia().getPais());
        cmb_Provincia.setSelectedItem(proveedorModificar.getLocalidad().getProvincia());
        cmb_Localidad.setSelectedItem(proveedorModificar.getLocalidad());
        txt_TelPrimario.setText(proveedorModificar.getTelPrimario());
        txt_TelSecundario.setText(proveedorModificar.getTelSecundario());
        txt_Contacto.setText(proveedorModificar.getContacto());
        txt_Email.setText(proveedorModificar.getEmail());
        txt_Web.setText(proveedorModificar.getWeb());
    }

    private void limpiarYRecargarComponentes() {
        txt_Codigo.setText("");
        txt_RazonSocial.setText("");
        txt_Id_Fiscal.setText("");
        txt_Direccion.setText("");
        txt_TelPrimario.setText("");
        txt_TelSecundario.setText("");
        txt_Contacto.setText("");
        txt_Email.setText("");
        txt_Web.setText("");
        this.cargarComboBoxCondicionesIVA();
        this.cargarComboBoxPaises();
    }

    public void cargarComboBoxCondicionesIVA() {
        cmb_CondicionIVA.removeAllItems();
        try {
            List<CondicionIVA> condicionesIVA = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/condiciones-iva", CondicionIVA[].class)));
            condicionesIVA.stream().forEach((c) -> {
                cmb_CondicionIVA.addItem(c);
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

    public void cargarComboBoxPaises() {
        cmb_Pais.removeAllItems();
        try {
            List<Pais> paises = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/paises", Pais[].class)));
            paises.stream().forEach((pais) -> {
                cmb_Pais.addItem(pais);
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

    public void cargarComboBoxProvinciasDelPais(Pais paisSeleccionado) {
        cmb_Provincia.removeAllItems();
        try {
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/provincias/paises/" + paisSeleccionado.getId_Pais(),
                    Provincia[].class)));
            provincias.stream().forEach((p) -> {
                cmb_Provincia.addItem(p);
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

    public void cargarComboBoxLocalidadesDeLaProvincia(Provincia provSeleccionada) {
        cmb_Localidad.removeAllItems();
        try {
            List<Localidad> Localidades = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/localidades/provincias/" + provSeleccionada.getId_Provincia(),
                    Localidad[].class)));
            Localidades.stream().forEach((l) -> {
                cmb_Localidad.addItem(l);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel2 = new javax.swing.JPanel();
        lbl_Direccion = new javax.swing.JLabel();
        txt_Direccion = new javax.swing.JTextField();
        lbl_Provincia = new javax.swing.JLabel();
        cmb_Provincia = new javax.swing.JComboBox();
        lbl_Localidad = new javax.swing.JLabel();
        cmb_Pais = new javax.swing.JComboBox();
        lbl_Pais = new javax.swing.JLabel();
        btn_NuevoPais = new javax.swing.JButton();
        btn_NuevaProvincia = new javax.swing.JButton();
        btn_NuevaLocalidad = new javax.swing.JButton();
        cmb_Localidad = new javax.swing.JComboBox();
        panel3 = new javax.swing.JPanel();
        lbl_Email = new javax.swing.JLabel();
        txt_Email = new javax.swing.JTextField();
        lbl_Web = new javax.swing.JLabel();
        txt_Web = new javax.swing.JTextField();
        lbl_TelPrimario = new javax.swing.JLabel();
        txt_TelPrimario = new javax.swing.JTextField();
        lbl_TelSecundario = new javax.swing.JLabel();
        txt_TelSecundario = new javax.swing.JTextField();
        txt_Contacto = new javax.swing.JTextField();
        lbl_Contacto = new javax.swing.JLabel();
        panel1 = new javax.swing.JPanel();
        lbl_CondicionIVA = new javax.swing.JLabel();
        cmb_CondicionIVA = new javax.swing.JComboBox();
        lbl_Id_Fiscal = new javax.swing.JLabel();
        txt_Id_Fiscal = new javax.swing.JTextField();
        lbl_RazonSocial = new javax.swing.JLabel();
        txt_RazonSocial = new javax.swing.JTextField();
        btn_NuevaCondicionIVA = new javax.swing.JButton();
        txt_Codigo = new javax.swing.JTextField();
        lbl_Codigo = new javax.swing.JLabel();
        btn_Guardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nuevo Proveedor");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Direccion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Direccion.setText("Dirección:");

        lbl_Provincia.setForeground(java.awt.Color.red);
        lbl_Provincia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Provincia.setText("* Provincia:");

        cmb_Provincia.setMaximumRowCount(5);
        cmb_Provincia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_ProvinciaItemStateChanged(evt);
            }
        });

        lbl_Localidad.setForeground(java.awt.Color.red);
        lbl_Localidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Localidad.setText("* Localidad:");

        cmb_Pais.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_PaisItemStateChanged(evt);
            }
        });

        lbl_Pais.setForeground(java.awt.Color.red);
        lbl_Pais.setText("* Pais:");

        btn_NuevoPais.setForeground(java.awt.Color.blue);
        btn_NuevoPais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_NuevoPais.setText("Nuevo");
        btn_NuevoPais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoPaisActionPerformed(evt);
            }
        });

        btn_NuevaProvincia.setForeground(java.awt.Color.blue);
        btn_NuevaProvincia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_NuevaProvincia.setText("Nueva");
        btn_NuevaProvincia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaProvinciaActionPerformed(evt);
            }
        });

        btn_NuevaLocalidad.setForeground(java.awt.Color.blue);
        btn_NuevaLocalidad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_NuevaLocalidad.setText("Nueva");
        btn_NuevaLocalidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaLocalidadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_Provincia)
                    .addComponent(lbl_Direccion)
                    .addComponent(lbl_Localidad)
                    .addComponent(lbl_Pais))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                        .addComponent(cmb_Localidad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_NuevaLocalidad))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cmb_Provincia, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmb_Pais, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_NuevoPais, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btn_NuevaProvincia, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(txt_Direccion))
                .addContainerGap())
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Direccion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Pais)
                    .addComponent(cmb_Pais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevoPais))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Provincia)
                    .addComponent(cmb_Provincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevaProvincia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Localidad)
                    .addComponent(cmb_Localidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevaLocalidad))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevoPais, cmb_Pais});

        panel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaProvincia, cmb_Provincia});

        panel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaLocalidad, cmb_Localidad});

        panel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Email.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Email.setText("Correo Electrónico:");

        lbl_Web.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Web.setText("Página Web:");

        lbl_TelPrimario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TelPrimario.setText("Teléfono #1:");

        lbl_TelSecundario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TelSecundario.setText("Teléfono #2:");

        lbl_Contacto.setText("Contacto:");

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbl_Contacto)
                    .addComponent(lbl_Email)
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbl_Web, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lbl_TelSecundario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_TelPrimario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_TelPrimario, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(txt_Web, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(txt_Contacto, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(txt_Email, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(txt_TelSecundario, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_TelPrimario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_TelPrimario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_TelSecundario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_TelSecundario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Contacto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Contacto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Email))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Web, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Web))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_CondicionIVA.setForeground(java.awt.Color.red);
        lbl_CondicionIVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_CondicionIVA.setText("* Condición IVA:");

        cmb_CondicionIVA.setMaximumRowCount(5);

        lbl_Id_Fiscal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Id_Fiscal.setText("ID Fiscal:");

        lbl_RazonSocial.setForeground(java.awt.Color.red);
        lbl_RazonSocial.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_RazonSocial.setText("* Razón Social:");

        btn_NuevaCondicionIVA.setForeground(java.awt.Color.blue);
        btn_NuevaCondicionIVA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMoney_16x16.png"))); // NOI18N
        btn_NuevaCondicionIVA.setText("Nueva");
        btn_NuevaCondicionIVA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaCondicionIVAActionPerformed(evt);
            }
        });

        lbl_Codigo.setText("Código:");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_RazonSocial)
                    .addComponent(lbl_Codigo)
                    .addComponent(lbl_Id_Fiscal)
                    .addComponent(lbl_CondicionIVA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(cmb_CondicionIVA, 0, 357, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_NuevaCondicionIVA))
                    .addComponent(txt_Id_Fiscal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                    .addComponent(txt_RazonSocial, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                    .addComponent(txt_Codigo, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Codigo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_RazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_RazonSocial))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Id_Fiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Id_Fiscal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmb_CondicionIVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_CondicionIVA)
                    .addComponent(btn_NuevaCondicionIVA))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaCondicionIVA, cmb_CondicionIVA});

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_Guardar)
                    .addComponent(panel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_NuevaCondicionIVAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaCondicionIVAActionPerformed
        DetalleCondicionIvaGUI gui_DetalleCondicionIVA = new DetalleCondicionIvaGUI();
        gui_DetalleCondicionIVA.setModal(true);
        gui_DetalleCondicionIVA.setLocationRelativeTo(this);
        gui_DetalleCondicionIVA.setVisible(true);
        this.cargarComboBoxCondicionesIVA();
    }//GEN-LAST:event_btn_NuevaCondicionIVAActionPerformed

    private void btn_NuevoPaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoPaisActionPerformed
        DetallePaisGUI gui_DetallePais = new DetallePaisGUI();
        gui_DetallePais.setModal(true);
        gui_DetallePais.setLocationRelativeTo(this);
        gui_DetallePais.setVisible(true);
        this.cargarComboBoxPaises();
    }//GEN-LAST:event_btn_NuevoPaisActionPerformed

    private void btn_NuevaProvinciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaProvinciaActionPerformed
        DetalleProvinciaGUI gui_DetalleProvincia = new DetalleProvinciaGUI();
        gui_DetalleProvincia.setModal(true);
        gui_DetalleProvincia.setLocationRelativeTo(this);
        gui_DetalleProvincia.setVisible(true);
        this.cargarComboBoxProvinciasDelPais((Pais) cmb_Pais.getSelectedItem());
    }//GEN-LAST:event_btn_NuevaProvinciaActionPerformed

    private void cmb_PaisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_PaisItemStateChanged
        if (cmb_Pais.getItemCount() > 0) {
            this.cargarComboBoxProvinciasDelPais((Pais) cmb_Pais.getSelectedItem());
        } else {
            cmb_Provincia.removeAllItems();
        }
    }//GEN-LAST:event_cmb_PaisItemStateChanged

    private void btn_NuevaLocalidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaLocalidadActionPerformed
        DetalleLocalidadGUI gui_DetalleLocalidad = new DetalleLocalidadGUI();
        gui_DetalleLocalidad.setModal(true);
        gui_DetalleLocalidad.setLocationRelativeTo(this);
        gui_DetalleLocalidad.setVisible(true);
        this.cargarComboBoxLocalidadesDeLaProvincia((Provincia) cmb_Provincia.getSelectedItem());
    }//GEN-LAST:event_btn_NuevaLocalidadActionPerformed

    private void cmb_ProvinciaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_ProvinciaItemStateChanged
        if (cmb_Provincia.getItemCount() > 0) {
            this.cargarComboBoxLocalidadesDeLaProvincia((Provincia) cmb_Provincia.getSelectedItem());
        } else {
            cmb_Localidad.removeAllItems();
        }
    }//GEN-LAST:event_cmb_ProvinciaItemStateChanged

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        try {
            if (operacion == TipoDeOperacion.ALTA) {
                Proveedor proveedor = new Proveedor();
                proveedor.setCodigo(txt_Codigo.getText().trim());
                proveedor.setRazonSocial(txt_RazonSocial.getText().trim());
                proveedor.setIdFiscal(txt_Id_Fiscal.getText().trim());
                proveedor.setCondicionIVA((CondicionIVA) cmb_CondicionIVA.getSelectedItem());
                proveedor.setDireccion(txt_Direccion.getText().trim());
                proveedor.setLocalidad((Localidad) cmb_Localidad.getSelectedItem());
                proveedor.setTelPrimario(txt_TelPrimario.getText().trim());
                proveedor.setTelSecundario(txt_TelSecundario.getText().trim());
                proveedor.setContacto(txt_Contacto.getText().trim());
                proveedor.setEmail(txt_Email.getText().trim());
                proveedor.setWeb(txt_Web.getText().trim());
                proveedor.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
                RestClient.getRestTemplate().postForObject("/proveedores", proveedor, Proveedor.class);
                int respuesta = JOptionPane.showConfirmDialog(this,
                        "El proveedor se guardó correctamente.\n¿Desea dar de alta otro proveedor?",
                        "Aviso", JOptionPane.YES_NO_OPTION);
                this.limpiarYRecargarComponentes();
                if (respuesta == JOptionPane.NO_OPTION) {
                    this.dispose();
                }
            }

            if (operacion == TipoDeOperacion.ACTUALIZACION) {
                proveedorModificar.setCodigo(txt_Codigo.getText().trim());
                proveedorModificar.setRazonSocial(txt_RazonSocial.getText().trim());
                proveedorModificar.setIdFiscal(txt_Id_Fiscal.getText().trim());
                proveedorModificar.setCondicionIVA((CondicionIVA) cmb_CondicionIVA.getSelectedItem());
                proveedorModificar.setDireccion(txt_Direccion.getText().trim());
                proveedorModificar.setLocalidad((Localidad) cmb_Localidad.getSelectedItem());
                proveedorModificar.setTelPrimario(txt_TelPrimario.getText().trim());
                proveedorModificar.setTelSecundario(txt_TelSecundario.getText().trim());
                proveedorModificar.setContacto(txt_Contacto.getText().trim());
                proveedorModificar.setEmail(txt_Email.getText().trim());
                proveedorModificar.setWeb(txt_Web.getText().trim());
                proveedorModificar.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
                RestClient.getRestTemplate().put("/proveedores", proveedorModificar);
                JOptionPane.showMessageDialog(this, "El proveedor se modificó correctamente.",
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
    }//GEN-LAST:event_btn_GuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.cargarComboBoxCondicionesIVA();
        this.cargarComboBoxPaises();
        if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.cargarProveedorParaModificar();
        }
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JButton btn_NuevaCondicionIVA;
    private javax.swing.JButton btn_NuevaLocalidad;
    private javax.swing.JButton btn_NuevaProvincia;
    private javax.swing.JButton btn_NuevoPais;
    private javax.swing.JComboBox cmb_CondicionIVA;
    private javax.swing.JComboBox cmb_Localidad;
    private javax.swing.JComboBox cmb_Pais;
    private javax.swing.JComboBox cmb_Provincia;
    private javax.swing.JLabel lbl_Codigo;
    private javax.swing.JLabel lbl_CondicionIVA;
    private javax.swing.JLabel lbl_Contacto;
    private javax.swing.JLabel lbl_Direccion;
    private javax.swing.JLabel lbl_Email;
    private javax.swing.JLabel lbl_Id_Fiscal;
    private javax.swing.JLabel lbl_Localidad;
    private javax.swing.JLabel lbl_Pais;
    private javax.swing.JLabel lbl_Provincia;
    private javax.swing.JLabel lbl_RazonSocial;
    private javax.swing.JLabel lbl_TelPrimario;
    private javax.swing.JLabel lbl_TelSecundario;
    private javax.swing.JLabel lbl_Web;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JPanel panel3;
    private javax.swing.JTextField txt_Codigo;
    private javax.swing.JTextField txt_Contacto;
    private javax.swing.JTextField txt_Direccion;
    private javax.swing.JTextField txt_Email;
    private javax.swing.JTextField txt_Id_Fiscal;
    private javax.swing.JTextField txt_RazonSocial;
    private javax.swing.JTextField txt_TelPrimario;
    private javax.swing.JTextField txt_TelSecundario;
    private javax.swing.JTextField txt_Web;
    // End of variables declaration//GEN-END:variables
}
