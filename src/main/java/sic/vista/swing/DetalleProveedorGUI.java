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
import sic.modelo.Rol;
import sic.modelo.TipoDeOperacion;
import sic.modelo.UsuarioActivo;

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
        txtCodigo.setText(proveedorModificar.getCodigo());
        txtRazonSocial.setText(proveedorModificar.getRazonSocial());
        txt_Id_Fiscal.setText(String.valueOf(proveedorModificar.getIdFiscal()));
        cmbCondicionIVA.setSelectedItem(proveedorModificar.getCondicionIVA());
        txtDireccion.setText(proveedorModificar.getDireccion());
        cmbPais.setSelectedItem(proveedorModificar.getLocalidad().getProvincia().getPais());
        cmbProvincia.setSelectedItem(proveedorModificar.getLocalidad().getProvincia());
        cmbLocalidad.setSelectedItem(proveedorModificar.getLocalidad());
        txtTelPrimario.setText(proveedorModificar.getTelPrimario());
        txtTelSecundario.setText(proveedorModificar.getTelSecundario());
        txtContacto.setText(proveedorModificar.getContacto());
        txtEmail.setText(proveedorModificar.getEmail());
        txtWeb.setText(proveedorModificar.getWeb());
    }

    private void limpiarYRecargarComponentes() {
        txtCodigo.setText("");
        txtRazonSocial.setText("");
        txt_Id_Fiscal.setText("");
        txtDireccion.setText("");
        txtTelPrimario.setText("");
        txtTelSecundario.setText("");
        txtContacto.setText("");
        txtEmail.setText("");
        txtWeb.setText("");
        this.cargarComboBoxCondicionesIVA();
        this.cargarComboBoxPaises();
    }

    public void cargarComboBoxCondicionesIVA() {
        cmbCondicionIVA.removeAllItems();
        try {
            List<CondicionIVA> condicionesIVA = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/condiciones-iva", CondicionIVA[].class)));
            condicionesIVA.stream().forEach((c) -> {
                cmbCondicionIVA.addItem(c);
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
        cmbPais.removeAllItems();
        try {
            List<Pais> paises = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/paises", Pais[].class)));
            paises.stream().forEach((pais) -> {
                cmbPais.addItem(pais);
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
        cmbProvincia.removeAllItems();
        try {
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/provincias/paises/" + paisSeleccionado.getId_Pais(),
                    Provincia[].class)));
            provincias.stream().forEach((p) -> {
                cmbProvincia.addItem(p);
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
        cmbLocalidad.removeAllItems();
        try {
            List<Localidad> Localidades = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/localidades/provincias/" + provSeleccionada.getId_Provincia(),
                    Localidad[].class)));
            Localidades.stream().forEach((l) -> {
                cmbLocalidad.addItem(l);
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

    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)) {
            btnNuevaCondicionIVA.setEnabled(true);
        } else {
            btnNuevaCondicionIVA.setEnabled(false);
            if (!rolesDeUsuarioActivo.contains(Rol.ENCARGADO)) {
                btnNuevaLocalidad.setEnabled(true);
                btnNuevaProvincia.setEnabled(true);
                btnNuevoPais.setEnabled(true);
            } else {
                btnNuevaLocalidad.setEnabled(false);
                btnNuevaProvincia.setEnabled(false);
                btnNuevoPais.setEnabled(false);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_Guardar = new javax.swing.JButton();
        panelPrincipal = new javax.swing.JPanel();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblRazonSocial = new javax.swing.JLabel();
        txtRazonSocial = new javax.swing.JTextField();
        lblIdFiscal = new javax.swing.JLabel();
        txt_Id_Fiscal = new javax.swing.JTextField();
        lblCondicionIVA = new javax.swing.JLabel();
        cmbCondicionIVA = new javax.swing.JComboBox();
        btnNuevaCondicionIVA = new javax.swing.JButton();
        lblDireccion = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        lblPais = new javax.swing.JLabel();
        cmbPais = new javax.swing.JComboBox();
        btnNuevoPais = new javax.swing.JButton();
        lblProvincia = new javax.swing.JLabel();
        cmbProvincia = new javax.swing.JComboBox();
        btnNuevaProvincia = new javax.swing.JButton();
        lblLocalidad = new javax.swing.JLabel();
        cmbLocalidad = new javax.swing.JComboBox();
        btnNuevaLocalidad = new javax.swing.JButton();
        lblTelPrimario = new javax.swing.JLabel();
        txtTelPrimario = new javax.swing.JTextField();
        txtTelSecundario = new javax.swing.JTextField();
        lblTelSecundario = new javax.swing.JLabel();
        lblContacto = new javax.swing.JLabel();
        txtContacto = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblWeb = new javax.swing.JLabel();
        txtWeb = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nuevo Proveedor");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        panelPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblCodigo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo.setText("Código:");

        lblRazonSocial.setForeground(java.awt.Color.red);
        lblRazonSocial.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRazonSocial.setText("* Razón Social:");

        lblIdFiscal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIdFiscal.setText("ID Fiscal:");

        lblCondicionIVA.setForeground(java.awt.Color.red);
        lblCondicionIVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCondicionIVA.setText("* Condición IVA:");

        cmbCondicionIVA.setMaximumRowCount(5);

        btnNuevaCondicionIVA.setForeground(java.awt.Color.blue);
        btnNuevaCondicionIVA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMoney_16x16.png"))); // NOI18N
        btnNuevaCondicionIVA.setText("Nueva");
        btnNuevaCondicionIVA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaCondicionIVAActionPerformed(evt);
            }
        });

        lblDireccion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDireccion.setText("Dirección:");

        lblPais.setForeground(java.awt.Color.red);
        lblPais.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPais.setText("* Pais:");

        cmbPais.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbPaisItemStateChanged(evt);
            }
        });

        btnNuevoPais.setForeground(java.awt.Color.blue);
        btnNuevoPais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btnNuevoPais.setText("Nuevo");
        btnNuevoPais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoPaisActionPerformed(evt);
            }
        });

        lblProvincia.setForeground(java.awt.Color.red);
        lblProvincia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblProvincia.setText("* Provincia:");

        cmbProvincia.setMaximumRowCount(5);
        cmbProvincia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbProvinciaItemStateChanged(evt);
            }
        });

        btnNuevaProvincia.setForeground(java.awt.Color.blue);
        btnNuevaProvincia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btnNuevaProvincia.setText("Nueva");
        btnNuevaProvincia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaProvinciaActionPerformed(evt);
            }
        });

        lblLocalidad.setForeground(java.awt.Color.red);
        lblLocalidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLocalidad.setText("* Localidad:");

        btnNuevaLocalidad.setForeground(java.awt.Color.blue);
        btnNuevaLocalidad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btnNuevaLocalidad.setText("Nueva");
        btnNuevaLocalidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaLocalidadActionPerformed(evt);
            }
        });

        lblTelPrimario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTelPrimario.setText("Teléfono #1:");

        lblTelSecundario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTelSecundario.setText("Teléfono #2:");

        lblContacto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblContacto.setText("Contacto:");

        lblEmail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEmail.setText("Email:");

        lblWeb.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblWeb.setText("Página Web:");

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCodigo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRazonSocial, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIdFiscal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCondicionIVA, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDireccion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPais, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProvincia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLocalidad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTelPrimario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTelSecundario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblContacto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblWeb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(cmbLocalidad, 0, 342, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btnNuevaLocalidad))
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(cmbPais, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btnNuevoPais))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                        .addComponent(cmbProvincia, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btnNuevaProvincia))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                        .addComponent(cmbCondicionIVA, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btnNuevaCondicionIVA))
                    .addComponent(txt_Id_Fiscal, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtRazonSocial, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTelPrimario, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTelSecundario, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtContacto, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtWeb, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCodigo)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblRazonSocial)
                    .addComponent(txtRazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblIdFiscal)
                    .addComponent(txt_Id_Fiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCondicionIVA)
                    .addComponent(cmbCondicionIVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevaCondicionIVA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDireccion)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPais)
                    .addComponent(cmbPais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevoPais))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblProvincia)
                    .addComponent(cmbProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevaProvincia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLocalidad)
                    .addComponent(cmbLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevaLocalidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTelPrimario)
                    .addComponent(txtTelPrimario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTelSecundario)
                    .addComponent(txtTelSecundario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblContacto)
                    .addComponent(txtContacto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblWeb)
                    .addComponent(txtWeb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnNuevoPais, cmbPais});

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnNuevaProvincia, cmbProvincia});

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnNuevaLocalidad, cmbLocalidad});

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnNuevaCondicionIVA, cmbCondicionIVA});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 466, Short.MAX_VALUE)
                        .addComponent(btn_Guardar))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevaCondicionIVAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaCondicionIVAActionPerformed
        DetalleCondicionIvaGUI gui_DetalleCondicionIVA = new DetalleCondicionIvaGUI();
        gui_DetalleCondicionIVA.setModal(true);
        gui_DetalleCondicionIVA.setLocationRelativeTo(this);
        gui_DetalleCondicionIVA.setVisible(true);
        this.cargarComboBoxCondicionesIVA();
    }//GEN-LAST:event_btnNuevaCondicionIVAActionPerformed

    private void btnNuevoPaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoPaisActionPerformed
        DetallePaisGUI gui_DetallePais = new DetallePaisGUI();
        gui_DetallePais.setModal(true);
        gui_DetallePais.setLocationRelativeTo(this);
        gui_DetallePais.setVisible(true);
        this.cargarComboBoxPaises();
    }//GEN-LAST:event_btnNuevoPaisActionPerformed

    private void btnNuevaProvinciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaProvinciaActionPerformed
        DetalleProvinciaGUI gui_DetalleProvincia = new DetalleProvinciaGUI();
        gui_DetalleProvincia.setModal(true);
        gui_DetalleProvincia.setLocationRelativeTo(this);
        gui_DetalleProvincia.setVisible(true);
        this.cargarComboBoxProvinciasDelPais((Pais) cmbPais.getSelectedItem());
    }//GEN-LAST:event_btnNuevaProvinciaActionPerformed

    private void cmbPaisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbPaisItemStateChanged
        if (cmbPais.getItemCount() > 0) {
            this.cargarComboBoxProvinciasDelPais((Pais) cmbPais.getSelectedItem());
        } else {
            cmbProvincia.removeAllItems();
        }
    }//GEN-LAST:event_cmbPaisItemStateChanged

    private void btnNuevaLocalidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaLocalidadActionPerformed
        DetalleLocalidadGUI gui_DetalleLocalidad = new DetalleLocalidadGUI();
        gui_DetalleLocalidad.setModal(true);
        gui_DetalleLocalidad.setLocationRelativeTo(this);
        gui_DetalleLocalidad.setVisible(true);
        this.cargarComboBoxLocalidadesDeLaProvincia((Provincia) cmbProvincia.getSelectedItem());
    }//GEN-LAST:event_btnNuevaLocalidadActionPerformed

    private void cmbProvinciaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbProvinciaItemStateChanged
        if (cmbProvincia.getItemCount() > 0) {
            this.cargarComboBoxLocalidadesDeLaProvincia((Provincia) cmbProvincia.getSelectedItem());
        } else {
            cmbLocalidad.removeAllItems();
        }
    }//GEN-LAST:event_cmbProvinciaItemStateChanged

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        try {
            if (operacion == TipoDeOperacion.ALTA) {
                Proveedor proveedor = new Proveedor();
                proveedor.setCodigo(txtCodigo.getText().trim());
                proveedor.setRazonSocial(txtRazonSocial.getText().trim());
                proveedor.setIdFiscal(txt_Id_Fiscal.getText().trim());
                proveedor.setCondicionIVA((CondicionIVA) cmbCondicionIVA.getSelectedItem());
                proveedor.setDireccion(txtDireccion.getText().trim());
                proveedor.setLocalidad((Localidad) cmbLocalidad.getSelectedItem());
                proveedor.setTelPrimario(txtTelPrimario.getText().trim());
                proveedor.setTelSecundario(txtTelSecundario.getText().trim());
                proveedor.setContacto(txtContacto.getText().trim());
                proveedor.setEmail(txtEmail.getText().trim());
                proveedor.setWeb(txtWeb.getText().trim());
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
                proveedorModificar.setCodigo(txtCodigo.getText().trim());
                proveedorModificar.setRazonSocial(txtRazonSocial.getText().trim());
                proveedorModificar.setIdFiscal(txt_Id_Fiscal.getText().trim());
                proveedorModificar.setCondicionIVA((CondicionIVA) cmbCondicionIVA.getSelectedItem());
                proveedorModificar.setDireccion(txtDireccion.getText().trim());
                proveedorModificar.setLocalidad((Localidad) cmbLocalidad.getSelectedItem());
                proveedorModificar.setTelPrimario(txtTelPrimario.getText().trim());
                proveedorModificar.setTelSecundario(txtTelSecundario.getText().trim());
                proveedorModificar.setContacto(txtContacto.getText().trim());
                proveedorModificar.setEmail(txtEmail.getText().trim());
                proveedorModificar.setWeb(txtWeb.getText().trim());
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
        this.cambiarEstadoDeComponentesSegunRolUsuario();
        if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.cargarProveedorParaModificar();
        }        
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNuevaCondicionIVA;
    private javax.swing.JButton btnNuevaLocalidad;
    private javax.swing.JButton btnNuevaProvincia;
    private javax.swing.JButton btnNuevoPais;
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JComboBox cmbCondicionIVA;
    private javax.swing.JComboBox cmbLocalidad;
    private javax.swing.JComboBox cmbPais;
    private javax.swing.JComboBox cmbProvincia;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblCondicionIVA;
    private javax.swing.JLabel lblContacto;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblIdFiscal;
    private javax.swing.JLabel lblLocalidad;
    private javax.swing.JLabel lblPais;
    private javax.swing.JLabel lblProvincia;
    private javax.swing.JLabel lblRazonSocial;
    private javax.swing.JLabel lblTelPrimario;
    private javax.swing.JLabel lblTelSecundario;
    private javax.swing.JLabel lblWeb;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtContacto;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtRazonSocial;
    private javax.swing.JTextField txtTelPrimario;
    private javax.swing.JTextField txtTelSecundario;
    private javax.swing.JTextField txtWeb;
    private javax.swing.JTextField txt_Id_Fiscal;
    // End of variables declaration//GEN-END:variables
}
