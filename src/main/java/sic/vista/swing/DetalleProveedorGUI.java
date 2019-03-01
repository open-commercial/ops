package sic.vista.swing;

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
import sic.modelo.CategoriaIVA;
import sic.modelo.EmpresaActiva;
import sic.modelo.Proveedor;
import sic.modelo.Rol;
import sic.modelo.TipoDeOperacion;
import sic.modelo.Ubicacion;
import sic.modelo.UsuarioActivo;

public class DetalleProveedorGUI extends JDialog {

    private Proveedor proveedorModificar;
    private Proveedor proveedorNuevo;
    private Ubicacion ubicacion;
    private final TipoDeOperacion operacion;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleProveedorGUI() {
        this.initComponents();
        this.setIcon();
        operacion = TipoDeOperacion.ALTA;
    }

    public DetalleProveedorGUI(Proveedor prov) {
        this.initComponents();
        this.setIcon();
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
        txtIdFiscal.setValue(proveedorModificar.getIdFiscal());
        cmbCategoriaIVA.setSelectedItem(proveedorModificar.getCategoriaIVA());
        txtDireccion.setText(proveedorModificar.getDireccion());
        if (proveedorModificar.getUbicacion() != null) {
            txtUbicacion.setText(proveedorModificar.getUbicacion().getCalle()
                    + " "
                    + proveedorModificar.getUbicacion().getNumero()
                    + (proveedorModificar.getUbicacion().getPiso() != null
                    ? ", " + proveedorModificar.getUbicacion().getPiso() + " "
                    : " ")
                    + (proveedorModificar.getUbicacion().getDepartamento() != null
                    ? proveedorModificar.getUbicacion().getDepartamento()
                    : "")
                    + (proveedorModificar.getUbicacion().getNombreLocalidad() != null
                    ? ", " + proveedorModificar.getUbicacion().getNombreLocalidad()
                    : " ")
                    + " "
                    + (proveedorModificar.getUbicacion().getNombreProvincia() != null
                    ? proveedorModificar.getUbicacion().getNombreProvincia()
                    : " "));
            this.ubicacion = proveedorModificar.getUbicacion();
        }
        txtTelPrimario.setText(proveedorModificar.getTelPrimario());
        txtTelSecundario.setText(proveedorModificar.getTelSecundario());
        txtContacto.setText(proveedorModificar.getContacto());
        txtEmail.setText(proveedorModificar.getEmail());
        txtWeb.setText(proveedorModificar.getWeb());

    }

    private void limpiarYRecargarComponentes() {
        txtCodigo.setText("");
        txtRazonSocial.setText("");
        txtIdFiscal.setText("");
        txtDireccion.setText("");
        txtTelPrimario.setText("");
        txtTelSecundario.setText("");
        txtContacto.setText("");
        txtEmail.setText("");
        txtWeb.setText("");
        this.cargarComboBoxCondicionesIVA();
    }

    private void cargarComboBoxCondicionesIVA() {
        cmbCategoriaIVA.removeAllItems();
        for (CategoriaIVA c : CategoriaIVA.values()) {
            cmbCategoriaIVA.addItem(c);
        }
    }

    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)) {
            btnUbicacion.setEnabled(true);
            btnUbicacion.setEnabled(true);
        } else {
            btnUbicacion.setEnabled(false);
            btnUbicacion.setEnabled(false);
        }
    }

    public Proveedor getProveedorCreado() {
        return proveedorNuevo;
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
        lblCondicionIVA = new javax.swing.JLabel();
        cmbCategoriaIVA = new javax.swing.JComboBox();
        lblDireccion = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
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
        txtIdFiscal = new javax.swing.JFormattedTextField();
        lblUbicacion = new javax.swing.JLabel();
        txtUbicacion = new javax.swing.JTextField();
        btnUbicacion = new javax.swing.JButton();

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

        cmbCategoriaIVA.setMaximumRowCount(5);

        lblDireccion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDireccion.setText("Dirección:");

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

        txtIdFiscal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#"))));
        txtIdFiscal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIdFiscalFocusLost(evt);
            }
        });

        lblUbicacion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUbicacion.setText("Ubicación:");

        txtUbicacion.setEnabled(false);

        btnUbicacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btnUbicacion.setText("Modificar");
        btnUbicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbicacionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblUbicacion, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIdFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCondicionIVA, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTelPrimario, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTelSecundario, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblContacto, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblWeb, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRazonSocial, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTelPrimario, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTelSecundario, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtContacto, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtWeb, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmbCategoriaIVA, 0, 436, Short.MAX_VALUE)
                    .addComponent(txtIdFiscal)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(txtUbicacion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUbicacion)))
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
                    .addComponent(txtIdFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCondicionIVA)
                    .addComponent(cmbCategoriaIVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDireccion)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnUbicacion)
                    .addComponent(txtUbicacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUbicacion))
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

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        try {
            if (operacion == TipoDeOperacion.ALTA) {
                Proveedor proveedor = new Proveedor();
                proveedor.setCodigo(txtCodigo.getText().trim());
                proveedor.setRazonSocial(txtRazonSocial.getText().trim());
                proveedor.setIdFiscal((Long) txtIdFiscal.getValue());
                proveedor.setCategoriaIVA((CategoriaIVA) cmbCategoriaIVA.getSelectedItem());
                proveedor.setDireccion(txtDireccion.getText().trim());
                proveedor.setTelPrimario(txtTelPrimario.getText().trim());
                proveedor.setTelSecundario(txtTelSecundario.getText().trim());
                proveedor.setContacto(txtContacto.getText().trim());
                proveedor.setEmail(txtEmail.getText().trim());
                proveedor.setWeb(txtWeb.getText().trim());
                proveedor = RestClient.getRestTemplate().postForObject("/proveedores?idEmpresa="
                        + (EmpresaActiva.getInstance().getEmpresa()).getId_Empresa(), proveedor, Proveedor.class);
                if (this.ubicacion != null) {
                    RestClient.getRestTemplate().postForObject("/ubicaciones/proveedores/" + proveedor.getId_Proveedor(), this.ubicacion, Ubicacion.class);
                }
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
                proveedorModificar.setIdFiscal((Long) txtIdFiscal.getValue());
                proveedorModificar.setCategoriaIVA((CategoriaIVA) cmbCategoriaIVA.getSelectedItem());
                proveedorModificar.setDireccion(txtDireccion.getText().trim());
                proveedorModificar.setTelPrimario(txtTelPrimario.getText().trim());
                proveedorModificar.setTelSecundario(txtTelSecundario.getText().trim());
                proveedorModificar.setContacto(txtContacto.getText().trim());
                proveedorModificar.setEmail(txtEmail.getText().trim());
                proveedorModificar.setWeb(txtWeb.getText().trim());
                RestClient.getRestTemplate().put("/proveedores?idEmpresa=" + (EmpresaActiva.getInstance().getEmpresa()).getId_Empresa(), proveedorModificar);
                if (proveedorModificar.getUbicacion() == null && this.ubicacion != null) {
                    RestClient.getRestTemplate().postForObject("/ubicaciones/proveedores/" + proveedorModificar.getId_Proveedor(), this.ubicacion, Ubicacion.class);
                } else if (proveedorModificar.getUbicacion() != null && this.ubicacion != null) {
                    RestClient.getRestTemplate().put("/ubicaciones", this.ubicacion);
                }
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
        this.cambiarEstadoDeComponentesSegunRolUsuario();
        if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.setTitle("Modificar Proveedor");
            this.cargarProveedorParaModificar();
        } else if (operacion == TipoDeOperacion.ALTA) {
            this.setTitle("Nuevo Proveedor");
        }
    }//GEN-LAST:event_formWindowOpened

    private void txtIdFiscalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIdFiscalFocusLost
        if (txtIdFiscal.getText().equals("")) {
            txtIdFiscal.setValue(null);
        }
    }//GEN-LAST:event_txtIdFiscalFocusLost

    private void btnUbicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbicacionActionPerformed
        DetalleUbicacionGUI guiDetalleUbicacion = new DetalleUbicacionGUI(this.ubicacion);
        guiDetalleUbicacion.setModal(true);
        guiDetalleUbicacion.setLocationRelativeTo(this);
        guiDetalleUbicacion.setVisible(true);
        if (guiDetalleUbicacion.getUbicacionModificada() != null) {
            this.ubicacion = guiDetalleUbicacion.getUbicacionModificada();
            txtUbicacion.setText(this.ubicacion.getCalle()
                    + " "
                    + this.ubicacion.getNumero()
                    + (this.ubicacion.getPiso() != null
                    ? ", " + this.ubicacion.getPiso() + " "
                    : " ")
                    + (this.ubicacion.getDepartamento() != null
                    ? this.ubicacion.getDepartamento()
                    : "")
                    + (this.ubicacion.getNombreLocalidad() != null
                    ? ", " + this.ubicacion.getNombreProvincia()
                    : " ")
                    + " "
                    + (this.ubicacion.getNombreProvincia() != null
                    ? this.ubicacion.getNombreProvincia()
                    : " "));
        }
    }//GEN-LAST:event_btnUbicacionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUbicacion;
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JComboBox cmbCategoriaIVA;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblCondicionIVA;
    private javax.swing.JLabel lblContacto;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblIdFiscal;
    private javax.swing.JLabel lblRazonSocial;
    private javax.swing.JLabel lblTelPrimario;
    private javax.swing.JLabel lblTelSecundario;
    private javax.swing.JLabel lblUbicacion;
    private javax.swing.JLabel lblWeb;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtContacto;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JFormattedTextField txtIdFiscal;
    private javax.swing.JTextField txtRazonSocial;
    private javax.swing.JTextField txtTelPrimario;
    private javax.swing.JTextField txtTelSecundario;
    private javax.swing.JTextField txtUbicacion;
    private javax.swing.JTextField txtWeb;
    // End of variables declaration//GEN-END:variables
}
