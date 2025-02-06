import java.awt.*;
import java.sql.*;
import javax.swing.*;


public class AdminLogin extends JFrame {


    private static final String ADMIN_PASSWORD = "admin123";


    public AdminLogin() {
        setTitle("Acceso de Administrador");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JLabel lblPassword = new JLabel("Contraseña:");
        JPasswordField passwordField = new JPasswordField();
        JButton btnLogin = new JButton("Ingresar");
        JButton btnCancel = new JButton("Cancelar");


        btnLogin.addActionListener(e -> {
            String inputPassword = new String(passwordField.getPassword());
            if (inputPassword.equals(ADMIN_PASSWORD)) {
                JOptionPane.showMessageDialog(this, "Acceso concedido.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                abrirVentanaAdministradores();
            } else {
                JOptionPane.showMessageDialog(this, "Contraseña incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        btnCancel.addActionListener(e -> dispose());


        panel.add(lblPassword);
        panel.add(passwordField);
        panel.add(btnLogin);
        panel.add(btnCancel);


        add(panel);
        setVisible(true);
    }


    private void abrirVentanaAdministradores() {
        JFrame adminFrame = new JFrame("Tablas de la Base de Datos");
        adminFrame.setSize(900, 600);
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setLocationRelativeTo(null);


        JTabbedPane tabbedPane = new JTabbedPane();


        String[] tablas = {"clientes", "coches", "alquileres", "modelo", "empleado", "habitual", "puntual"};
        for (String tabla : tablas) {
            tabbedPane.addTab(tabla, new JScrollPane(crearTablaDesdeBD(tabla)));
        }


        adminFrame.add(tabbedPane);
        adminFrame.setVisible(true);
    }


    private JTable crearTablaDesdeBD(String nombreTabla) {
        String url = "jdbc:mysql://hl1354.dinaserver.com:3306/Reto2MySQL";
        String usuario = "uxue";
        String contraseña = "G_nazaret_U2024";
        String consulta = "SELECT * FROM " + nombreTabla;


        try (Connection connection = DriverManager.getConnection(url, usuario, contraseña);
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery(consulta)) {


            int columnas = resultSet.getMetaData().getColumnCount();
            String[] columnNames = new String[columnas];
            for (int i = 0; i < columnas; i++) {
                columnNames[i] = resultSet.getMetaData().getColumnName(i + 1);
            }


            resultSet.last();
            int filas = resultSet.getRow();
            resultSet.beforeFirst();


            Object[][] data = new Object[filas][columnas];
            int fila = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnas; col++) {
                    data[fila][col] = resultSet.getObject(col + 1);
                }
                fila++;
            }


            return new JTable(data, columnNames);


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos de " + nombreTabla + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new JTable(new Object[0][0], new String[]{"Error"});
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminLogin::new);
    }
}


