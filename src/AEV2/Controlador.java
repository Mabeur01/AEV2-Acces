package AEV2;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Controlador {
    private Model model;
    private Vista vista;

    public Controlador(Model model, Vista vista) {
        this.model = model;
        this.vista = vista;
        model.connectToDatabase("./connection-client.xml");
    }

    
    public void connect(String username, String password, String userType) throws SQLException {
        model.connect(username, password, userType);
    }

    public ResultSet executeQuery(String query) throws SQLException {
        return model.executeQuery(query);
    }

    public boolean login(String username, String password) throws SQLException {
        return model.login(username, password);
    }

    public void closeConnection() {
        model.closeConnection();
    }
}

