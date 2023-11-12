package AEV2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Model {
	private static Connection connection;

    public Model() {
        
    }
    public boolean connectToDatabase(String xmlFile) {
        try {
            String path = System.getProperty("user.dir") + File.separator + xmlFile;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File(path));
            

            NodeList nodeList = document.getElementsByTagName("connectionInfo");
            Node conexionNode = nodeList.item(0);
            if (conexionNode.getNodeType() == Node.ELEMENT_NODE) {
                Element conexionElement = (Element) conexionNode;

                
                String url = conexionElement.getElementsByTagName("url").item(0).getTextContent();
                String username = conexionElement.getElementsByTagName("username").item(0).getTextContent();
                String password = conexionElement.getElementsByTagName("password").item(0).getTextContent();

                
                connection = DriverManager.getConnection(url, username, password);
                return true;
            } else {
                System.out.println("Error al leer el nodo de conexión en el archivo XML.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void connect(String username, String password, String userType) throws SQLException {
        connectToDatabase("connection-" + userType + ".xml");
    }

    public ResultSet executeQuery(String query) throws SQLException {
        ResultSet resultSet = null;

        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                resultSet = statement.executeQuery(query);
                
            } else {
                throw new SQLException("No hay conexión a la base de datos.");
            }
        } catch (SQLException e) {
            throw e;
        }

        return resultSet;
    }

    public boolean login(String username, String password) throws SQLException {
        try {
            String query = "SELECT type, pass FROM users WHERE user=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String tipoUsuario = resultSet.getString("type");
                String hashFromDB = resultSet.getString("pass");

                
                String hashedPassword = getMD5(password);

                
                if (hashFromDB.equals(hashedPassword)) {
                    if (tipoUsuario.equals("admin")) {
                        connection.close();
                        connectToDatabase("connection-admin.xml");
                        System.out.println("Se cierra conex y abre admin xml");
                    }
                    return true; 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return ""; 
        }
    }
    
    public boolean executeInsert(String query, Object... values) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean executeUpdate(String query, Object... values) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean executeDelete(String query, Object... values) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet executeSelect(String query, Object... values) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }

            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cerrar la conexión");
        }
    }
}
