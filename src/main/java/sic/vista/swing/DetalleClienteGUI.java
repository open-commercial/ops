package sic.vista.swing;

import java.math.BigDecimal;
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
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.modelo.Rol;
import sic.modelo.TipoDeOperacion;
import sic.modelo.Usuario;
import sic.modelo.Ubicacion;
import sic.modelo.UsuarioActivo;

public class DetalleClienteGUI extends JDialog {

    private Cliente cliente = new Cliente();
    private Ubicacion ubicacionDeFacturacion;
    private Ubicacion ubicacionDeEnvio;
    private final TipoDeOperacion operacion;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleClienteGUI() {
        this.initComponents();
        this.setIcon();
        operacion = TipoDeOperacion.ALTA;
    }

    public DetalleClienteGUI(Cliente cliente) {
        this.initComponents();
        this.setIcon();
        operacion = TipoDeOperacion.ACTUALIZACION;
        this.cliente = cliente;
    }

    public Cliente getClienteDadoDeAlta() {
        return (cliente.getId_Cliente() != 0L ? cliente : null);
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleClienteGUI.class.getResource("/sic/icons/Client_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void seleccionarCredencialSegunId(Long idCredencial) {
        if (idCredencial != null) {
            try {
                Usuario usuario = RestClient.getRestTemplate().getForObject("/usuarios/"
                        + idCredencial, Usuario.class);
                cmbCredencial.addItem(usuario);
                cmbCredencial.addItem(null);
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void seleccionarViajanteSegunId(Long idViajante) {
        if (idViajante != null) {
            try {
                Usuario usuario = RestClient.getRestTemplate().getForObject("/usuarios/"
                        + idViajante, Usuario.class);
                cmbViajante.addItem(usuario);
                cmbViajante.addItem(null);                
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarClienteParaModificar() {
        txtIdFiscal.setValue(cliente.getIdFiscal());
        txtNombreFiscal.setText(cliente.getNombreFiscal());
        txtNombreFantasia.setText(cliente.getNombreFantasia());
        txtBonificacion.setValue(cliente.getBonificacion());
        cmbCategoriaIVA.setSelectedItem(cliente.getCategoriaIVA());
        if (cliente.getUbicacionFacturacion() != null) {
            lblDetalleUbicacionFacturacion.setText(cliente.getUbicacionFacturacion().toString());
        }
        if (cliente.getUbicacionEnvio() != null) {
            lblDetalleUbicacionEnvio.setText(cliente.getUbicacionEnvio().toString());
        }
        this.seleccionarCredencialSegunId(cliente.getIdCredencial());
        this.seleccionarViajanteSegunId(cliente.getIdViajante());
        txtTelefono.setText(cliente.getTelefono());
        txtContacto.setText(cliente.getContacto());
        txtEmail.setText(cliente.getEmail());
    }

    private void cargarComboBoxCategoriasIVA() {
        cmbCategoriaIVA.removeAllItems();
        for (CategoriaIVA c : CategoriaIVA.values()) {
            cmbCategoriaIVA.addItem(c);
        }
    }
    
    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)) {
            btnNuevoUsuarioViajante.setEnabled(true);
            lblCredencial.setEnabled(true);
            cmbCredencial.setEnabled(true);
        } else {
            btnNuevoUsuarioViajante.setEnabled(false);
            lblCredencial.setEnabled(false);
            cmbCredencial.setEnabled(false);
        }
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)) {
            lblViajante.setEnabled(true);
            cmbViajante.setEnabled(true);
            btnBuscarUsuarioViajante.setEnabled(true);
            lblCredencial.setEnabled(true);
            cmbCredencial.setEnabled(true);
            btnBuscarCredencial.setEnabled(true);
            lblBonificacion.setEnabled(true);
            txtBonificacion.setEnabled(true);
        } else {
            lblViajante.setEnabled(false);
            cmbViajante.setEnabled(false);
            btnBuscarUsuarioViajante.setEnabled(false);
            lblCredencial.setEnabled(false);
            cmbCredencial.setEnabled(false);
            btnBuscarCredencial.setEnabled(false);
            lblBonificacion.setEnabled(false);
            txtBonificacion.setEnabled(false);
        }
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)
                || rolesDeUsuarioActivo.contains(Rol.VENDEDOR)) {
            btnNuevaCredencial.setEnabled(true);
            btnBuscarCredencial.setEnabled(true);
            lblCredencial.setEnabled(true);
            cmbCredencial.setEnabled(true);
        } else {
            btnNuevaCredencial.setEnabled(false);
            btnBuscarCredencial.setEnabled(false);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_Guardar = new javax.swing.JButton();
        panelPrincipal = new javax.swing.JPanel();
        lblBonificacion = new javax.swing.JLabel();
        txtBonificacion = new javax.swing.JFormattedTextField();
        lblIdFiscal = new javax.swing.JLabel();
        txtIdFiscal = new javax.swing.JFormattedTextField();
        lblNombreFiscal = new javax.swing.JLabel();
        txtNombreFiscal = new javax.swing.JTextField();
        lblNombreFantasia = new javax.swing.JLabel();
        txtNombreFantasia = new javax.swing.JTextField();
        lblCondicionIVA = new javax.swing.JLabel();
        cmbCategoriaIVA = new javax.swing.JComboBox<>();
        lblUbicacionFacturacion = new javax.swing.JLabel();
        lblViajante = new javax.swing.JLabel();
        cmbViajante = new javax.swing.JComboBox<>();
        btnBuscarUsuarioViajante = new javax.swing.JButton();
        btnNuevoUsuarioViajante = new javax.swing.JButton();
        lblTelefono = new javax.swing.JLabel();
        lblContacto = new javax.swing.JLabel();
        txtContacto = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblCredencial = new javax.swing.JLabel();
        cmbCredencial = new javax.swing.JComboBox<>();
        btnBuscarCredencial = new javax.swing.JButton();
        btnNuevaCredencial = new javax.swing.JButton();
        lblUbicacionEnvio = new javax.swing.JLabel();
        btnUbicacionFacturacion = new javax.swing.JButton();
        btnUbicacionEnvio = new javax.swing.JButton();
        lblDetalleUbicacionFacturacion = new javax.swing.JLabel();
        lblDetalleUbicacionEnvio = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nuevo Cliente");
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

        lblBonificacion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBonificacion.setText("% Bonificación:");

        txtBonificacion.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtBonificacion.setValue(0);

        lblIdFiscal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIdFiscal.setText("CUIT o DNI:");

        txtIdFiscal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#"))));
        txtIdFiscal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIdFiscalFocusLost(evt);
            }
        });

        lblNombreFiscal.setForeground(java.awt.Color.red);
        lblNombreFiscal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNombreFiscal.setText("* R. Social o Nombre:");

        lblNombreFantasia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNombreFantasia.setText("Nombre Fantasia:");

        lblCondicionIVA.setForeground(java.awt.Color.red);
        lblCondicionIVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCondicionIVA.setText("* Categoria IVA:");

        lblUbicacionFacturacion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUbicacionFacturacion.setText("Ubicación Facturación:");

        lblViajante.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblViajante.setText("Viajante:");

        cmbViajante.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbViajanteItemStateChanged(evt);
            }
        });

        btnBuscarUsuarioViajante.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarUsuarioViajante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarUsuarioViajanteActionPerformed(evt);
            }
        });

        btnNuevoUsuarioViajante.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddGroup_16x16.png"))); // NOI18N
        btnNuevoUsuarioViajante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoUsuarioViajanteActionPerformed(evt);
            }
        });

        lblTelefono.setForeground(java.awt.Color.red);
        lblTelefono.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTelefono.setText("* Teléfono:");

        lblContacto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblContacto.setText("Contacto:");

        lblEmail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEmail.setText("Email:");

        lblCredencial.setForeground(java.awt.Color.red);
        lblCredencial.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCredencial.setText("* Credencial:");

        cmbCredencial.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCredencialItemStateChanged(evt);
            }
        });

        btnBuscarCredencial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarCredencial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarCredencialActionPerformed(evt);
            }
        });

        btnNuevaCredencial.setForeground(java.awt.Color.blue);
        btnNuevaCredencial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddGroup_16x16.png"))); // NOI18N
        btnNuevaCredencial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaCredencialActionPerformed(evt);
            }
        });

        lblUbicacionEnvio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUbicacionEnvio.setText("Ubicación Envío:");

        btnUbicacionFacturacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditMap_16x16.png"))); // NOI18N
        btnUbicacionFacturacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbicacionFacturacionActionPerformed(evt);
            }
        });

        btnUbicacionEnvio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditMap_16x16.png"))); // NOI18N
        btnUbicacionEnvio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbicacionEnvioActionPerformed(evt);
            }
        });

        lblDetalleUbicacionFacturacion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDetalleUbicacionFacturacion.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblDetalleUbicacionEnvio.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDetalleUbicacionEnvio.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblUbicacionFacturacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblUbicacionEnvio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCondicionIVA, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblBonificacion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblNombreFiscal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblNombreFantasia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblIdFiscal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBonificacion, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtNombreFiscal, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtNombreFantasia, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtIdFiscal)
                            .addComponent(cmbCategoriaIVA, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblContacto, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblViajante, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCredencial, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtContacto, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                                .addComponent(cmbViajante, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(0, 0, 0)
                                .addComponent(btnBuscarUsuarioViajante)
                                .addGap(0, 0, 0)
                                .addComponent(btnNuevoUsuarioViajante))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblDetalleUbicacionFacturacion, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblDetalleUbicacionEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0)
                                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnUbicacionFacturacion, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnUbicacionEnvio, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                                .addComponent(cmbCredencial, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(0, 0, 0)
                                .addComponent(btnBuscarCredencial)
                                .addGap(0, 0, 0)
                                .addComponent(btnNuevaCredencial))
                            .addComponent(txtTelefono))))
                .addContainerGap())
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblBonificacion, lblCondicionIVA, lblContacto, lblCredencial, lblEmail, lblIdFiscal, lblNombreFantasia, lblNombreFiscal, lblTelefono, lblUbicacionEnvio, lblUbicacionFacturacion, lblViajante});

        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblBonificacion)
                    .addComponent(txtBonificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNombreFiscal)
                    .addComponent(txtNombreFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNombreFantasia)
                    .addComponent(txtNombreFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(lblUbicacionFacturacion)
                    .addComponent(lblDetalleUbicacionFacturacion)
                    .addComponent(btnUbicacionFacturacion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblUbicacionEnvio)
                    .addComponent(lblDetalleUbicacionEnvio)
                    .addComponent(btnUbicacionEnvio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblViajante)
                    .addComponent(cmbViajante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarUsuarioViajante)
                    .addComponent(btnNuevoUsuarioViajante))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTelefono))
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
                    .addComponent(btnNuevaCredencial)
                    .addComponent(btnBuscarCredencial)
                    .addComponent(cmbCredencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCredencial))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblCredencial, lblTelefono, lblViajante});

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarCredencial, btnNuevaCredencial, cmbCredencial, cmbViajante});

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarUsuarioViajante, btnNuevoUsuarioViajante});

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblBonificacion, lblIdFiscal});

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtBonificacion, txtIdFiscal});

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnUbicacionFacturacion, lblDetalleUbicacionEnvio, lblDetalleUbicacionFacturacion});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_Guardar)))
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
            String idViajante = "";
            String idCredencial = "";
            cliente.setIdFiscal((Long) txtIdFiscal.getValue());
            cliente.setNombreFiscal(txtNombreFiscal.getText().trim());
            cliente.setNombreFantasia(txtNombreFantasia.getText().trim());
            cliente.setBonificacion(new BigDecimal(txtBonificacion.getText()));
            cliente.setCategoriaIVA((CategoriaIVA) cmbCategoriaIVA.getSelectedItem());
            cliente.setTelefono(txtTelefono.getText().trim());
            cliente.setContacto(txtContacto.getText().trim());
            cliente.setEmail(txtEmail.getText().trim());
            if (cmbViajante.getSelectedItem() != null) {
                idViajante = String.valueOf(
                        ((Usuario) cmbViajante.getSelectedItem()).getId_Usuario());
            }
            if (cmbCredencial.getSelectedItem() != null) {
                idCredencial = String.valueOf(
                        ((Usuario) cmbCredencial.getSelectedItem()).getId_Usuario());
            }
            if (operacion == TipoDeOperacion.ALTA) {
                if (this.ubicacionDeFacturacion != null) {
                    cliente.setUbicacionFacturacion(this.ubicacionDeFacturacion);
                }
                if (this.ubicacionDeEnvio != null) {
                    cliente.setUbicacionEnvio(this.ubicacionDeEnvio);
                }
                cliente = RestClient.getRestTemplate().postForObject(
                        "/clientes?idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                        + "&idViajante=" + idViajante
                        + "&idCredencial=" + idCredencial,
                        cliente, Cliente.class);
                JOptionPane.showMessageDialog(this, "El Cliente se guardó correctamente!",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
            if (operacion == TipoDeOperacion.ACTUALIZACION) {
                RestClient.getRestTemplate().put(
                        "/clientes?idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                        + "&idViajante=" + idViajante
                        + "&idCredencial=" + idCredencial,
                        cliente);
                if (cliente.getUbicacionFacturacion() == null && this.ubicacionDeFacturacion != null) { //revisar
                    RestClient.getRestTemplate().postForObject("/ubicaciones/clientes/" + cliente.getId_Cliente() + "/facturacion", this.ubicacionDeFacturacion, Ubicacion.class);
                } else if (cliente.getUbicacionFacturacion() != null && this.ubicacionDeFacturacion != null) {
                    RestClient.getRestTemplate().put("/ubicaciones", this.ubicacionDeFacturacion);
                }
                if (cliente.getUbicacionEnvio() == null && this.ubicacionDeEnvio != null) {
                    RestClient.getRestTemplate().postForObject("/ubicaciones/clientes/" + cliente.getId_Cliente() + "/envio", this.ubicacionDeEnvio, Ubicacion.class);
                } else if (cliente.getUbicacionEnvio() != null && this.ubicacionDeEnvio != null) {
                    RestClient.getRestTemplate().put("/ubicaciones", this.ubicacionDeEnvio);
                }
                JOptionPane.showMessageDialog(this, "El Cliente se modificó correctamente!",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
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

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.cargarComboBoxCategoriasIVA();
        this.cambiarEstadoDeComponentesSegunRolUsuario();
        if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.setTitle("Modificar Cliente Nº " + cliente.getNroCliente());
            this.cargarClienteParaModificar();
        } else if (operacion == TipoDeOperacion.ALTA) {
            this.setTitle("Nuevo Cliente");
        }
    }//GEN-LAST:event_formWindowOpened

    private void btnNuevaCredencialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaCredencialActionPerformed
        DetalleUsuarioGUI gui_DetalleUsuario = new DetalleUsuarioGUI(Rol.COMPRADOR);
        gui_DetalleUsuario.setModal(true);
        gui_DetalleUsuario.setLocationRelativeTo(this);
        gui_DetalleUsuario.setVisible(true);
        if (gui_DetalleUsuario.getUsuarioCreado() != null && gui_DetalleUsuario.getUsuarioCreado().getRoles().contains(Rol.COMPRADOR)) {
            cmbCredencial.removeAllItems();
            cmbCredencial.addItem(gui_DetalleUsuario.getUsuarioCreado());
            cmbCredencial.addItem(null);
            cmbCredencial.setSelectedIndex(0);
        }
    }//GEN-LAST:event_btnNuevaCredencialActionPerformed

    private void btnBuscarUsuarioViajanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarUsuarioViajanteActionPerformed
        BuscarUsuariosGUI buscarUsuariosGUI = new BuscarUsuariosGUI(new Rol[]{Rol.VIAJANTE}, "Buscar Viajante");
        buscarUsuariosGUI.setModal(true);
        buscarUsuariosGUI.setLocationRelativeTo(this);
        buscarUsuariosGUI.setVisible(true);
        if (buscarUsuariosGUI.getUsuarioSeleccionado() != null) {
            cmbViajante.removeAllItems();
            cmbViajante.addItem(buscarUsuariosGUI.getUsuarioSeleccionado());
            cmbViajante.addItem(null);
        }
    }//GEN-LAST:event_btnBuscarUsuarioViajanteActionPerformed

    private void cmbViajanteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbViajanteItemStateChanged
        if (cmbViajante.getSelectedItem() == null) {
            cmbViajante.removeAllItems();
        }
    }//GEN-LAST:event_cmbViajanteItemStateChanged

    private void cmbCredencialItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCredencialItemStateChanged
        if (cmbCredencial.getSelectedItem() == null) {
            cmbCredencial.removeAllItems();
        }
    }//GEN-LAST:event_cmbCredencialItemStateChanged

    private void btnBuscarCredencialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarCredencialActionPerformed
        Rol[] rolesParaFiltrar = new Rol[]{Rol.ADMINISTRADOR, Rol.ENCARGADO, Rol.VENDEDOR, Rol.VIAJANTE, Rol.COMPRADOR};
        BuscarUsuariosGUI buscarUsuariosGUI = new BuscarUsuariosGUI(rolesParaFiltrar, "Buscar Usuario");
        buscarUsuariosGUI.setModal(true);
        buscarUsuariosGUI.setLocationRelativeTo(this);
        buscarUsuariosGUI.setVisible(true);
        if (buscarUsuariosGUI.getUsuarioSeleccionado() != null) {
            cmbCredencial.removeAllItems();
            cmbCredencial.addItem(buscarUsuariosGUI.getUsuarioSeleccionado());
            cmbCredencial.addItem(null);
        }
    }//GEN-LAST:event_btnBuscarCredencialActionPerformed

    private void btnNuevoUsuarioViajanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoUsuarioViajanteActionPerformed
        DetalleUsuarioGUI gui_DetalleUsuario = new DetalleUsuarioGUI(Rol.VIAJANTE);
        gui_DetalleUsuario.setModal(true);
        gui_DetalleUsuario.setLocationRelativeTo(this);
        gui_DetalleUsuario.setVisible(true);
        if (gui_DetalleUsuario.getUsuarioCreado() != null) {
            cmbViajante.removeAllItems();
            cmbViajante.addItem(gui_DetalleUsuario.getUsuarioCreado());
            cmbViajante.addItem(null);
            cmbViajante.setSelectedIndex(0);
        }
    }//GEN-LAST:event_btnNuevoUsuarioViajanteActionPerformed

    private void txtIdFiscalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIdFiscalFocusLost
        if (txtIdFiscal.getText().equals("")) {
            txtIdFiscal.setValue(null);
        }
    }//GEN-LAST:event_txtIdFiscalFocusLost

    private void btnUbicacionFacturacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbicacionFacturacionActionPerformed
        DetalleUbicacionGUI guiDetalleUbicacion = new DetalleUbicacionGUI(this.cliente.getUbicacionFacturacion(), "Ubicación Facturación");
        guiDetalleUbicacion.setModal(true);
        guiDetalleUbicacion.setLocationRelativeTo(this);
        guiDetalleUbicacion.setVisible(true);
        if (guiDetalleUbicacion.getUbicacionModificada() != null) {
            this.ubicacionDeFacturacion = guiDetalleUbicacion.getUbicacionModificada();
            lblDetalleUbicacionFacturacion.setText(
                    (this.ubicacionDeFacturacion.getCalle() != null
                    ? this.ubicacionDeFacturacion.getCalle() + " " : "")
                    + (this.ubicacionDeFacturacion.getNumero() != null
                    ? this.ubicacionDeFacturacion.getNumero() + " " : "")
                    + (this.ubicacionDeFacturacion.getPiso() != null
                    ? this.ubicacionDeFacturacion.getPiso() + " "
                    : "")
                    + (this.ubicacionDeFacturacion.getDepartamento() != null
                    ? this.ubicacionDeFacturacion.getDepartamento() + " "
                    : "")
                    + (this.ubicacionDeFacturacion.getNombreLocalidad() != null
                    ? this.ubicacionDeFacturacion.getNombreLocalidad() + " "
                    : "")
                    + (this.ubicacionDeFacturacion.getNombreProvincia() != null
                    ? this.ubicacionDeFacturacion.getNombreProvincia()
                    : ""));
        }
    }//GEN-LAST:event_btnUbicacionFacturacionActionPerformed

    private void btnUbicacionEnvioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbicacionEnvioActionPerformed
        DetalleUbicacionGUI guiDetalleUbicacion = new DetalleUbicacionGUI(this.cliente.getUbicacionEnvio(), "Ubicación Envío");
        guiDetalleUbicacion.setModal(true);
        guiDetalleUbicacion.setLocationRelativeTo(this);
        guiDetalleUbicacion.setVisible(true);
        if (guiDetalleUbicacion.getUbicacionModificada() != null) {
            this.ubicacionDeEnvio = guiDetalleUbicacion.getUbicacionModificada();
            lblDetalleUbicacionEnvio.setText(this.ubicacionDeEnvio.getCalle()
                    + " "
                    + this.ubicacionDeEnvio.getNumero()
                    + (this.ubicacionDeEnvio.getPiso() != null
                    ? ", " + this.ubicacionDeEnvio.getPiso() + " "
                    : " ")
                    + (this.ubicacionDeEnvio.getDepartamento() != null
                    ? this.ubicacionDeEnvio.getDepartamento()
                    : "")
                    + (this.ubicacionDeEnvio.getNombreLocalidad() != null
                    ? ", " + this.ubicacionDeEnvio.getNombreLocalidad()
                    : " ")
                    + " "
                    + (this.ubicacionDeEnvio.getNombreProvincia() != null
                    ? this.ubicacionDeEnvio.getNombreProvincia()
                    : " "));
        }
    }//GEN-LAST:event_btnUbicacionEnvioActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarCredencial;
    private javax.swing.JButton btnBuscarUsuarioViajante;
    private javax.swing.JButton btnNuevaCredencial;
    private javax.swing.JButton btnNuevoUsuarioViajante;
    private javax.swing.JButton btnUbicacionEnvio;
    private javax.swing.JButton btnUbicacionFacturacion;
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JComboBox<CategoriaIVA> cmbCategoriaIVA;
    private javax.swing.JComboBox<Usuario> cmbCredencial;
    private javax.swing.JComboBox<Usuario> cmbViajante;
    private javax.swing.JLabel lblBonificacion;
    private javax.swing.JLabel lblCondicionIVA;
    private javax.swing.JLabel lblContacto;
    private javax.swing.JLabel lblCredencial;
    private javax.swing.JLabel lblDetalleUbicacionEnvio;
    private javax.swing.JLabel lblDetalleUbicacionFacturacion;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblIdFiscal;
    private javax.swing.JLabel lblNombreFantasia;
    private javax.swing.JLabel lblNombreFiscal;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblUbicacionEnvio;
    private javax.swing.JLabel lblUbicacionFacturacion;
    private javax.swing.JLabel lblViajante;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JFormattedTextField txtBonificacion;
    private javax.swing.JTextField txtContacto;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JFormattedTextField txtIdFiscal;
    private javax.swing.JTextField txtNombreFantasia;
    private javax.swing.JTextField txtNombreFiscal;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
