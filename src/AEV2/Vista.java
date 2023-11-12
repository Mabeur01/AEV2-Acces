package AEV2;

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.event.ActionEvent;

public class Vista extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
    private Controlador controller;
    private Model model;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea queryTextArea;
    private JTextArea resultTextArea;
    private JPanel queryPanel;
    private JPanel resultPanel;
    

    public Vista() {
        
        setTitle("Aplicación de Gestión de Base de Datos");
        setSize(800, 624);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Color.LIGHT_GRAY);
        usernameField = new JTextField(20);
        usernameField.setBounds(298, 54, 166, 20);
        passwordField = new JPasswordField(20);
        passwordField.setBounds(298, 110, 166, 20);
        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    handleLogin();
                } catch (SQLException e1) {
                    showErrorMessage("Error al iniciar sesión: " + e1.getMessage());
                }
            }
        });
        loginButton.setBounds(322, 141, 123, 23);
        loginPanel.setLayout(null);
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setBounds(355, 29, 56, 14);
        loginPanel.add(lblUsuario);
        loginPanel.add(usernameField);
        JLabel label_1 = new JLabel("Contraseña: ");
        label_1.setBounds(348, 85, 76, 14);
        loginPanel.add(label_1);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        
        queryPanel = new JPanel();
        queryPanel.setBackground(Color.LIGHT_GRAY);
        queryPanel.setVisible(false);
        JScrollPane queryScrollPane = new JScrollPane();
        queryScrollPane.setBounds(532, 96, 2, 2);
        JButton queryButton = new JButton("Ejecutar Consulta");
        queryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    handleExecuteQuery();
                } catch (SQLException e1) {
                    showErrorMessage("Error al ejecutar consulta: " + e1.getMessage());
                }
            }
        });
        queryButton.setBounds(539, 85, 117, 23);
        queryPanel.setLayout(null);
        JLabel label_2 = new JLabel("Consulta SQL: ");
        label_2.setBounds(127, 90, 71, 14);
        queryPanel.add(label_2);
        queryTextArea = new JTextArea(10, 40);
        queryTextArea.setBounds(203, 5, 324, 184);
        queryPanel.add(queryTextArea);
        queryPanel.add(queryScrollPane);
        queryPanel.add(queryButton);
        
        resultPanel = new JPanel();
        resultPanel.setBackground(Color.LIGHT_GRAY);
        resultPanel.setVisible(false);
        resultTextArea = new JTextArea(10, 40);
        resultTextArea.setEditable(false); 
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
        resultScrollPane.setBounds(200, 0, 326, 186);
        JButton closeSessionButton = new JButton("Cerrar Sesión");
        closeSessionButton.setBounds(538, 70, 97, 23);
        closeSessionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        JButton closeConnectionButton = new JButton("Cerrar Conexión");
        closeConnectionButton.setVisible(false);
        closeConnectionButton.setBounds(536, 104, 111, 23);
        closeConnectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleCloseConnection();
            }
        });
        resultPanel.setLayout(null);
        JLabel label_3 = new JLabel("Resultados: ");
        label_3.setBounds(130, 85, 60, 14);
        resultPanel.add(label_3);
        resultPanel.add(resultScrollPane);
        resultPanel.add(closeSessionButton);
        resultPanel.add(closeConnectionButton);
        
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(loginPanel);
        getContentPane().add(queryPanel);
        getContentPane().add(resultPanel);
        
        
    }
    private void handleLogin() throws SQLException {
        String username = getUsername();
        String password = getPassword();
        if (controller.login(username, password)) {
            queryPanel.setVisible(true);
            resultPanel.setVisible(true);
            System.out.println("Iniciada sesion correctamente");
        } else {
            showErrorMessage("Inicio de sesión fallido. Verifica tus credenciales.");
        }
    }
    
    private void handleExecuteQuery() {
        try {
            String query = getQuery().trim();
            
            if (query.toLowerCase().startsWith("insert")) {
                handleInsert(query);
            } else if (query.toLowerCase().startsWith("update")) {
                handleUpdate(query);
            } else if (query.toLowerCase().startsWith("delete")) {
                handleDelete(query);
            } else {
                handleSelect(query);
            }
        } catch (SQLException e1) {
            showErrorMessage("Error al ejecutar la consulta: " + e1.getMessage());
        }
    }

    private void handleInsert(String query) throws SQLException {
        try {
            String tableName = JOptionPane.showInputDialog("Ingrese el nombre de la tabla para la inserción:");
            String values = JOptionPane.showInputDialog("Ingrese los valores para la inserción (separados por coma):");
            String[] valuesArray = values.split(",");

            if (model.executeInsert(query, valuesArray)) {
                showErrorMessage("Inserción exitosa.");
            } else {
                showErrorMessage("Error al realizar la inserción.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Error al realizar la inserción.");
        }
    }

    private void handleUpdate(String query) throws SQLException {
        try {
            String tableName = JOptionPane.showInputDialog("Ingrese el nombre de la tabla para la actualización:");
            String column = JOptionPane.showInputDialog("Ingrese el nombre de la columna a actualizar:");
            String value = JOptionPane.showInputDialog("Ingrese el nuevo valor:");
            String conditionColumn = JOptionPane.showInputDialog("Ingrese el nombre de la columna de condición:");
            String conditionValue = JOptionPane.showInputDialog("Ingrese el valor de la condición:");

            if (model.executeUpdate(query, tableName, column, value, conditionColumn, conditionValue)) {
                showErrorMessage("Actualización exitosa.");
            } else {
                showErrorMessage("Error al realizar la actualización.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Error al realizar la actualización.");
        }
    }

    private void handleDelete(String query) throws SQLException {
        try {
            String tableName = JOptionPane.showInputDialog("Ingrese el nombre de la tabla para la eliminación:");
            String conditionColumn = JOptionPane.showInputDialog("Ingrese el nombre de la columna de condición:");
            String conditionValue = JOptionPane.showInputDialog("Ingrese el valor de la condición:");

            if (model.executeDelete(query, tableName, conditionColumn, conditionValue)) {
                showInfoMessage("Eliminación exitosa.");
            } else {
                showErrorMessage("Error al realizar la eliminación.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Error al realizar la eliminación.");
        }
    }

    private void handleSelect(String query) throws SQLException {
        try {
            ResultSet resultSet = model.executeSelect(query);

            StringBuilder resultText = new StringBuilder();

            while (resultSet.next()) {
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    resultText.append(resultSet.getString(i)).append("\t");
                }
                resultText.append("\n");
            }

            if (resultText.length() > 0) {
                resultTextArea.setText(resultText.toString());
                resultTextArea.setCaretPosition(resultTextArea.getDocument().getLength());
            } else {
                resultTextArea.setText("Sin resultados");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resultTextArea.setText("Error al ejecutar la consulta");
        }
    }
    
    private void handleLogout() {
        
        clearLoginFields();
        queryTextArea.setText("");
        resultTextArea.setText("");
        queryPanel.setVisible(true);
        resultPanel.setVisible(true);

        System.out.println("Sesión cerrada correctamente");
    }
    
    private void handleCloseConnection() {
        controller.closeConnection();
    
        clearLoginFields();
        queryTextArea.setText("");
        resultTextArea.setText("");
        
        System.out.println("Conexión cerrada correctamente");
        this.dispose();
    }
    
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearLoginFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    public void setController(Controlador controller) {
        this.controller = controller;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getQuery() {
        return queryTextArea.getText();
    }

    public void setResultText(String result) {
        resultTextArea.setText(result);
    }


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	String userType = "client";
                    Model model = new Model();
                    Vista vista = new Vista();
                    Controlador controller = new Controlador(model, vista);
                    vista.setController(controller);
                    vista.setVisible(true);
                    controller.connect(vista.getUsername(), vista.getPassword(), userType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
