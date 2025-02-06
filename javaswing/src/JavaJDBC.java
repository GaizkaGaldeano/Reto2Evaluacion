import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class JavaJDBC {


    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                "jdbc:mysql://hl1354.dinaserver.com:3306/Reto2MySQL", "uxue", "G_nazaret_U2024"
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se pudo cargar el controlador JDBC", e);
        }
    }


    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM clientes;");


            while (resultSet.next()) {
                System.out.println(resultSet.getInt(2) + " " + resultSet.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o consulta: " + e);
        }
    }
}
