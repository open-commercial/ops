package sic;

import org.springframework.boot.SpringApplication;
import sic.vista.swing.LoginGUI;

public class App {

    public static void main(String[] args) {                              
        SpringApplication app = new SpringApplication(App.class);
        app.setHeadless(false);
        app.run(args);
        LoginGUI gui_LogIn = new LoginGUI();
        gui_LogIn.setVisible(true);
    }
}
