package sic.vista.swing;

import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sic.modelo.EmpresaActiva;
import sic.modelo.UsuarioActivo;

public class PuntoDeVentaContainerGUI extends JFrame {
    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeFrame = new Dimension(1200, 700);

    public PuntoDeVentaContainerGUI() {
        this.initComponents();
        this.setIcon();
    }
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(PuntoDeVentaGUI.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }
    
    private void llamarSeleccionEmpresaGUI() {
        SeleccionEmpresaGUI seleccionEmpresaGUI = new SeleccionEmpresaGUI();
        seleccionEmpresaGUI.setModal(true);
        seleccionEmpresaGUI.setLocationRelativeTo(this);
        seleccionEmpresaGUI.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktopPaneContainer = new javax.swing.JDesktopPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        javax.swing.GroupLayout desktopPaneContainerLayout = new javax.swing.GroupLayout(desktopPaneContainer);
        desktopPaneContainer.setLayout(desktopPaneContainerLayout);
        desktopPaneContainerLayout.setHorizontalGroup(
            desktopPaneContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 784, Short.MAX_VALUE)
        );
        desktopPaneContainerLayout.setVerticalGroup(
            desktopPaneContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 465, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPaneContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPaneContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setSize(sizeFrame);
        this.setExtendedState(MAXIMIZED_BOTH);
        this.llamarSeleccionEmpresaGUI();       
        PuntoDeVentaGUI puntoDeVentaGUI = new PuntoDeVentaGUI();
        puntoDeVentaGUI.setLocation(desktopPaneContainer.getWidth() / 2 - puntoDeVentaGUI.getWidth() / 2,
                desktopPaneContainer.getHeight() / 2 - puntoDeVentaGUI.getHeight() / 2);
        desktopPaneContainer.add(puntoDeVentaGUI);
        puntoDeVentaGUI.setResizable(false);
        puntoDeVentaGUI.setVisible(true);
        this.setTitle("S.I.C. Ops " + ResourceBundle.getBundle("Mensajes").getString("version")
                + " - Empresa: " + EmpresaActiva.getInstance().getEmpresa().getNombre() 
                + " - Usuario: " + UsuarioActivo.getInstance().getUsuario().getNombre());
        try {
            puntoDeVentaGUI.setSelected(true);
            puntoDeVentaGUI.setMaximum(true);
        } catch (PropertyVetoException ex) {
            String msjError = "No se pudo seleccionar la ventana requerida.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(desktopPaneContainer, msjError, "Error", JOptionPane.ERROR_MESSAGE);
        }        
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane desktopPaneContainer;
    // End of variables declaration//GEN-END:variables
}
