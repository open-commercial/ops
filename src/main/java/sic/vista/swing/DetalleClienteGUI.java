package sic.vista.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.CondicionIVA;
import sic.modelo.EmpresaActiva;
import sic.modelo.Localidad;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Pais;
import sic.modelo.Provincia;
import sic.modelo.Rol;
import sic.modelo.TipoDeOperacion;
import sic.modelo.Usuario;
import sic.modelo.UsuarioActivo;

public class DetalleClienteGUI extends JDialog {

    private Cliente cliente = new Cliente();
    private List<CondicionIVA> condicionesIVA;
    private List<Pais> paises;
    private List<Provincia> provincias;
    private List<Localidad> localidades;
    private List<Usuario> credenciales;
    private List<Usuario> viajantes;
    private final TipoDeOperacion operacion;    
    private final List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
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

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleClienteGUI.class.getResource("/sic/icons/Client_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void seleccionarCondicionIVASegunId(Long idCondicionIVA) {
        int indice = 0;
        for (int i=0; i<condicionesIVA.size(); i++) {
            if (condicionesIVA.get(i).getId_CondicionIVA() == idCondicionIVA) {
                indice = i;
            }
        }
        cmbCondicionIVA.setSelectedItem(condicionesIVA.get(indice));
    }
    
    private void seleccionarCredencialSegunId(Long idCredencial) {
        if (idCredencial != null) {
            int indice = 0;
            for (int i=0; i<credenciales.size(); i++) {
                if (credenciales.get(i).getId_Usuario() == idCredencial) {
                    indice = i;
                }
            }
            cmbCredencial.setSelectedItem(credenciales.get(indice));
        }
    }
    
    private void seleccionarViajanteSegunId(Long idViajante) {
        if (idViajante != null) {
            int indice = 0;
            for (int i=0; i<viajantes.size(); i++) {
                if (viajantes.get(i).getId_Usuario() == idViajante) {
                    indice = i;
                }
            }
            cmbViajante.setSelectedItem(viajantes.get(indice));
        }
    }  
    
    private void seleccionarPaisSegunId(Long idPais) {
        int indice = 0;
        for (int i=0; i<paises.size(); i++) {
            if (paises.get(i).getId_Pais() == idPais) {
                indice = i;
            }
        }
        cmbPais.setSelectedItem(paises.get(indice));
    }
    
    private void seleccionarProvinciaSegunId(Long idProvincia) {
        int indice = 0;
        for (int i=0; i<provincias.size(); i++) {
            if (provincias.get(i).getId_Provincia() == idProvincia) {
                indice = i;
            }
        }
        cmbProvincia.setSelectedItem(provincias.get(indice));
    }
    
    private void seleccionarLocalidadSegunId(Long idLocalidad) {
        int indice = 0;
        for (int i=0; i<localidades.size(); i++) {
            if (localidades.get(i).getId_Localidad() == idLocalidad) {
                indice = i;
            }
        }
        cmbLocalidad.setSelectedItem(localidades.get(indice));
    }
    
    private void cargarClienteParaModificar() {
        txt_Id_Fiscal.setText(cliente.getIdFiscal());
        txt_RazonSocial.setText(cliente.getRazonSocial());
        txt_NombreFantasia.setText(cliente.getNombreFantasia());                
        this.seleccionarCondicionIVASegunId(cliente.getIdCondicionIVA());
        txt_Direccion.setText(cliente.getDireccion());                
        this.seleccionarPaisSegunId(cliente.getIdPais());
        this.seleccionarProvinciaSegunId(cliente.getIdProvincia());
        this.seleccionarLocalidadSegunId(cliente.getIdLocalidad());
        this.seleccionarCredencialSegunId(cliente.getIdCredencial());
        this.seleccionarViajanteSegunId(cliente.getIdViajante());
        txt_TelPrimario.setText(cliente.getTelPrimario());
        txt_TelSecundario.setText(cliente.getTelSecundario());
        txt_Contacto.setText(cliente.getContacto());
        txt_Email.setText(cliente.getEmail());        
    }

    private void cargarComboBoxCondicionesIVA() {
        cmbCondicionIVA.removeAllItems();
        try {
            condicionesIVA = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/condiciones-iva", CondicionIVA[].class)));
            condicionesIVA.stream().forEach(c -> cmbCondicionIVA.addItem(c));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarComboBoxPaises() {
        cmbPais.removeAllItems();
        try {
            paises = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/paises", Pais[].class)));
            paises.stream().forEach(p -> cmbPais.addItem(p));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarComboBoxViajantes() {
        cmbViajante.removeAllItems();
        cmbViajante.addItem(null);
        try {
            PaginaRespuestaRest<Usuario> response = RestClient.getRestTemplate()
                    .exchange("/usuarios/busqueda/criteria?"
                            + "roles=" + Rol.VIAJANTE
                            + "&pagina=0&tamanio=" + Integer.MAX_VALUE, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Usuario>>() {
                    })
                    .getBody();
            viajantes = response.getContent();
            viajantes.stream().forEach(v -> cmbViajante.addItem(v));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (rolesDeUsuarioActivo.contains(Rol.VIAJANTE)
                && !rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                && !rolesDeUsuarioActivo.contains(Rol.ENCARGADO)
                && !rolesDeUsuarioActivo.contains(Rol.VENDEDOR)) {
            cmbViajante.setSelectedItem(UsuarioActivo.getInstance().getUsuario());
            cmbViajante.setEnabled(false);
        }
    }
    
    private void cargarComboBoxCredenciales() {
        cmbCredencial.removeAllItems();
        cmbCredencial.addItem(null);
        try {
            PaginaRespuestaRest<Usuario> response = RestClient.getRestTemplate()
                    .exchange("/usuarios/busqueda/criteria?"
                            + "roles=" + Rol.COMPRADOR
                            + "&pagina=0&tamanio=" + Integer.MAX_VALUE, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Usuario>>() {
                    })
                    .getBody();
            credenciales = response.getContent();
            credenciales.stream().forEach(c -> cmbCredencial.addItem(c));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarComboBoxProvinciasDelPais(Pais pais) {
        cmbProvincia.removeAllItems();
        try {
            provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/provincias/paises/" + pais.getId_Pais(),
                            Provincia[].class)));
            provincias.stream().forEach(p -> cmbProvincia.addItem(p));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarComboBoxLocalidadesDeLaProvincia(Provincia provincia) {
        cmbLocalidad.removeAllItems();
        try {
            localidades = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/localidades/provincias/" + provincia.getId_Provincia(),
                    Localidad[].class)));
            localidades.stream().forEach(l -> cmbLocalidad.addItem(l));
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
        if (!rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)) {
            btn_NuevaCondicionIVA.setEnabled(false);
            btn_NuevaCredencial.setEnabled(false);
            lbl_Credencial.setEnabled(false);
            cmbCredencial.setEnabled(false);
            if (!rolesDeUsuarioActivo.contains(Rol.ENCARGADO)) {
                btn_NuevaLocalidad.setEnabled(false);
                btn_NuevaProvincia.setEnabled(false);
                btn_NuevoPais.setEnabled(false);
            }
        }
    }
        
    public Cliente getClienteDadoDeAlta() {
        return cliente;
    }
       
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel2 = new javax.swing.JPanel();
        lbl_Direccion = new javax.swing.JLabel();
        txt_Direccion = new javax.swing.JTextField();
        lbl_Provincia = new javax.swing.JLabel();
        cmbProvincia = new javax.swing.JComboBox<>();
        lbl_Localidad = new javax.swing.JLabel();
        cmbPais = new javax.swing.JComboBox<>();
        lbl_Pais = new javax.swing.JLabel();
        btn_NuevoPais = new javax.swing.JButton();
        btn_NuevaProvincia = new javax.swing.JButton();
        btn_NuevaLocalidad = new javax.swing.JButton();
        cmbLocalidad = new javax.swing.JComboBox<>();
        panel3 = new javax.swing.JPanel();
        lbl_Email = new javax.swing.JLabel();
        txt_Email = new javax.swing.JTextField();
        lbl_TelPrimario = new javax.swing.JLabel();
        txt_TelPrimario = new javax.swing.JTextField();
        lbl_TelSecundario = new javax.swing.JLabel();
        txt_TelSecundario = new javax.swing.JTextField();
        txt_Contacto = new javax.swing.JTextField();
        lbl_Contacto = new javax.swing.JLabel();
        lbl_Viajante = new javax.swing.JLabel();
        cmbViajante = new javax.swing.JComboBox<>();
        lbl_Credencial = new javax.swing.JLabel();
        cmbCredencial = new javax.swing.JComboBox<>();
        btn_NuevaCredencial = new javax.swing.JButton();
        panel1 = new javax.swing.JPanel();
        lbl_CondicionIVA = new javax.swing.JLabel();
        cmbCondicionIVA = new javax.swing.JComboBox<>();
        lbl_RazonSocial = new javax.swing.JLabel();
        txt_RazonSocial = new javax.swing.JTextField();
        lbl_Id_Fiscal = new javax.swing.JLabel();
        txt_Id_Fiscal = new javax.swing.JTextField();
        btn_NuevaCondicionIVA = new javax.swing.JButton();
        lbl_NombreFantasia = new javax.swing.JLabel();
        txt_NombreFantasia = new javax.swing.JTextField();
        btn_Guardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nuevo Cliente");
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

        cmbProvincia.setMaximumRowCount(5);
        cmbProvincia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbProvinciaItemStateChanged(evt);
            }
        });

        lbl_Localidad.setForeground(java.awt.Color.red);
        lbl_Localidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Localidad.setText("* Localidad:");

        cmbPais.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbPaisItemStateChanged(evt);
            }
        });

        lbl_Pais.setForeground(java.awt.Color.red);
        lbl_Pais.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
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
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Provincia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Pais, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Direccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Localidad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbPais, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbProvincia, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbLocalidad, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btn_NuevaLocalidad)
                                .addComponent(btn_NuevaProvincia))
                            .addComponent(btn_NuevoPais)))
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
                    .addComponent(cmbPais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevoPais))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Provincia)
                    .addComponent(cmbProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevaProvincia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Localidad)
                    .addComponent(cmbLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevaLocalidad))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevoPais, cmbPais});

        panel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaProvincia, cmbProvincia});

        panel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaLocalidad, cmbLocalidad});

        panel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Email.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Email.setText("Email:");

        lbl_TelPrimario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TelPrimario.setText("Teléfono #1:");

        lbl_TelSecundario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TelSecundario.setText("Teléfono #2:");

        lbl_Contacto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Contacto.setText("Contacto:");

        lbl_Viajante.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Viajante.setText("Viajante:");

        lbl_Credencial.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Credencial.setText("Credencial:");

        btn_NuevaCredencial.setForeground(java.awt.Color.blue);
        btn_NuevaCredencial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Client_16x16.png"))); // NOI18N
        btn_NuevaCredencial.setText("Nueva");
        btn_NuevaCredencial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaCredencialActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Viajante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Email, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_TelPrimario, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                    .addComponent(lbl_TelSecundario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Contacto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Credencial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_Email, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_Contacto, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_TelSecundario, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_TelPrimario, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmbViajante, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addComponent(cmbCredencial, 0, 357, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_NuevaCredencial)))
                .addContainerGap())
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Credencial)
                    .addComponent(cmbCredencial, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevaCredencial))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Viajante)
                    .addComponent(cmbViajante, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbl_Credencial, lbl_TelPrimario, lbl_Viajante});

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_CondicionIVA.setForeground(java.awt.Color.red);
        lbl_CondicionIVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_CondicionIVA.setText("* Condición IVA:");

        cmbCondicionIVA.setMaximumRowCount(5);

        lbl_RazonSocial.setForeground(java.awt.Color.red);
        lbl_RazonSocial.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_RazonSocial.setText("* Razon Social:");

        lbl_Id_Fiscal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Id_Fiscal.setText("ID Fiscal:");

        btn_NuevaCondicionIVA.setForeground(java.awt.Color.blue);
        btn_NuevaCondicionIVA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMoney_16x16.png"))); // NOI18N
        btn_NuevaCondicionIVA.setText("Nueva");
        btn_NuevaCondicionIVA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaCondicionIVAActionPerformed(evt);
            }
        });

        lbl_NombreFantasia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_NombreFantasia.setText("Nombre Fantasia:");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbl_RazonSocial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_CondicionIVA, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_NombreFantasia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Id_Fiscal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(cmbCondicionIVA, 0, 348, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_NuevaCondicionIVA))
                    .addComponent(txt_NombreFantasia)
                    .addComponent(txt_RazonSocial)
                    .addComponent(txt_Id_Fiscal, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Id_Fiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Id_Fiscal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_RazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_RazonSocial))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_NombreFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_NombreFantasia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbCondicionIVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_CondicionIVA)
                    .addComponent(btn_NuevaCondicionIVA))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaCondicionIVA, cmbCondicionIVA});

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_Guardar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Guardar)
                .addContainerGap())
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
        this.cargarComboBoxProvinciasDelPais(cmbPais.getItemAt(cmbPais.getSelectedIndex()));        
    }//GEN-LAST:event_btn_NuevoPaisActionPerformed

    private void btn_NuevaProvinciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaProvinciaActionPerformed
        DetalleProvinciaGUI gui_DetalleProvincia = new DetalleProvinciaGUI();
        gui_DetalleProvincia.setModal(true);
        gui_DetalleProvincia.setLocationRelativeTo(this);
        gui_DetalleProvincia.setVisible(true);        
        this.cargarComboBoxProvinciasDelPais(cmbPais.getItemAt(cmbPais.getSelectedIndex()));
    }//GEN-LAST:event_btn_NuevaProvinciaActionPerformed

    private void cmbPaisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbPaisItemStateChanged
        if (cmbPais.getItemCount() > 0) {
            this.cargarComboBoxProvinciasDelPais(cmbPais.getItemAt(cmbPais.getSelectedIndex()));
        }
    }//GEN-LAST:event_cmbPaisItemStateChanged

    private void btn_NuevaLocalidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaLocalidadActionPerformed
        DetalleLocalidadGUI gui_DetalleLocalidad = new DetalleLocalidadGUI();
        gui_DetalleLocalidad.setModal(true);
        gui_DetalleLocalidad.setLocationRelativeTo(this);
        gui_DetalleLocalidad.setVisible(true);      
        this.cargarComboBoxLocalidadesDeLaProvincia(cmbProvincia.getItemAt(cmbProvincia.getSelectedIndex()));      
    }//GEN-LAST:event_btn_NuevaLocalidadActionPerformed

    private void cmbProvinciaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbProvinciaItemStateChanged
        if (cmbProvincia.getItemCount() > 0) {            
            this.cargarComboBoxLocalidadesDeLaProvincia(cmbProvincia.getItemAt(cmbProvincia.getSelectedIndex()));
        } else {
            cmbLocalidad.removeAllItems();
        }
    }//GEN-LAST:event_cmbProvinciaItemStateChanged

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        try {
            String idCondicionIVA = "";
            String idLocalidad = "";
            String idViajante = "";
            String idCredencial = "";
            cliente.setIdFiscal(txt_Id_Fiscal.getText().trim());
            cliente.setRazonSocial(txt_RazonSocial.getText().trim());
            cliente.setNombreFantasia(txt_NombreFantasia.getText().trim());
            cliente.setDireccion(txt_Direccion.getText().trim());
            cliente.setTelPrimario(txt_TelPrimario.getText().trim());
            cliente.setTelSecundario(txt_TelSecundario.getText().trim());
            cliente.setContacto(txt_Contacto.getText().trim());
            cliente.setEmail(txt_Email.getText().trim());
            if (cmbCondicionIVA.getSelectedItem() != null) {
                idCondicionIVA = String.valueOf(
                        ((CondicionIVA) cmbCondicionIVA.getSelectedItem()).getId_CondicionIVA());
            }
            if (cmbLocalidad.getSelectedItem() != null) {
                idLocalidad = String.valueOf(
                        ((Localidad) cmbLocalidad.getSelectedItem()).getId_Localidad());
            }
            if (cmbViajante.getSelectedItem() != null) {                
                idViajante = String.valueOf(
                        ((Usuario) cmbViajante.getSelectedItem()).getId_Usuario());
            }
            if (cmbCredencial.getSelectedItem() != null) {
                idCredencial = String.valueOf(
                        ((Usuario) cmbCredencial.getSelectedItem()).getId_Usuario());
            }
            if (operacion == TipoDeOperacion.ALTA) {
                cliente = RestClient.getRestTemplate().postForObject(
                        "/clientes?idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                        + "&idCondicionIVA=" + idCondicionIVA
                        + "&idLocalidad=" + idLocalidad
                        + "&idUsuarioViajante=" + idViajante
                        + "&idUsuarioCredencial=" + idCredencial,
                        cliente, Cliente.class);
                JOptionPane.showMessageDialog(this, "El Cliente se guardó correctamente!",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);                
            }
            if (operacion == TipoDeOperacion.ACTUALIZACION) {
                RestClient.getRestTemplate().put(
                        "/clientes?idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                        + "&idCondicionIVA=" + idCondicionIVA
                        + "&idLocalidad=" + idLocalidad
                        + "&idUsuarioViajante=" + idViajante
                        + "&idUsuarioCredencial=" + idCredencial,
                        cliente);
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
        this.cargarComboBoxCondicionesIVA();
        this.cargarComboBoxPaises();
        this.cargarComboBoxCredenciales();
        this.cargarComboBoxViajantes();
        this.cambiarEstadoDeComponentesSegunRolUsuario();
        if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.setTitle("Modificar Cliente");
            this.cargarClienteParaModificar();
        } else if (operacion == TipoDeOperacion.ALTA) {
            this.setTitle("Nuevo Cliente");            
        }
    }//GEN-LAST:event_formWindowOpened
        
    private void btn_NuevaCredencialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaCredencialActionPerformed
        DetalleUsuarioGUI gui_DetalleUsuario = new DetalleUsuarioGUI();
        gui_DetalleUsuario.setModal(true);
        gui_DetalleUsuario.setLocationRelativeTo(this);
        gui_DetalleUsuario.setVisible(true);
        this.cargarComboBoxCredenciales();
    }//GEN-LAST:event_btn_NuevaCredencialActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JButton btn_NuevaCondicionIVA;
    private javax.swing.JButton btn_NuevaCredencial;
    private javax.swing.JButton btn_NuevaLocalidad;
    private javax.swing.JButton btn_NuevaProvincia;
    private javax.swing.JButton btn_NuevoPais;
    private javax.swing.JComboBox<CondicionIVA> cmbCondicionIVA;
    private javax.swing.JComboBox<Usuario> cmbCredencial;
    private javax.swing.JComboBox<Localidad> cmbLocalidad;
    private javax.swing.JComboBox<Pais> cmbPais;
    private javax.swing.JComboBox<Provincia> cmbProvincia;
    private javax.swing.JComboBox<Usuario> cmbViajante;
    private javax.swing.JLabel lbl_CondicionIVA;
    private javax.swing.JLabel lbl_Contacto;
    private javax.swing.JLabel lbl_Credencial;
    private javax.swing.JLabel lbl_Direccion;
    private javax.swing.JLabel lbl_Email;
    private javax.swing.JLabel lbl_Id_Fiscal;
    private javax.swing.JLabel lbl_Localidad;
    private javax.swing.JLabel lbl_NombreFantasia;
    private javax.swing.JLabel lbl_Pais;
    private javax.swing.JLabel lbl_Provincia;
    private javax.swing.JLabel lbl_RazonSocial;
    private javax.swing.JLabel lbl_TelPrimario;
    private javax.swing.JLabel lbl_TelSecundario;
    private javax.swing.JLabel lbl_Viajante;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JPanel panel3;
    private javax.swing.JTextField txt_Contacto;
    private javax.swing.JTextField txt_Direccion;
    private javax.swing.JTextField txt_Email;
    private javax.swing.JTextField txt_Id_Fiscal;
    private javax.swing.JTextField txt_NombreFantasia;
    private javax.swing.JTextField txt_RazonSocial;
    private javax.swing.JTextField txt_TelPrimario;
    private javax.swing.JTextField txt_TelSecundario;
    // End of variables declaration//GEN-END:variables
}
