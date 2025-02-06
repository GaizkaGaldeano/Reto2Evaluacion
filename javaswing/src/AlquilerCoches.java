import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.Date;
import javax.swing.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

public class AlquilerCoches extends JFrame {
    private final JPanel panelCampos;
    private final JPanel panelBotones;

    public AlquilerCoches() {
        setTitle("Alquiler de Coches");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        panelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        JTextField txtNombre = new JTextField(20);
        JTextField txtApellido = new JTextField(20);
        JTextField txtTelefono = new JTextField(20);
        JTextField txtDireccion = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JComboBox<String> cmbMarca = new JComboBox<>(new String[]{"Toyota", "Ford", "Mercedes"});
        JComboBox<String> cmbModelo = new JComboBox<>(new String[]{"Corolla", "Escape", "Clase C"});
        JSpinner spnFechaInicio = new JSpinner(new SpinnerDateModel());
        JSpinner spnFechaFin = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorInicio = new JSpinner.DateEditor(spnFechaInicio, "yyyy-MM-dd");
        JSpinner.DateEditor editorFin = new JSpinner.DateEditor(spnFechaFin, "yyyy-MM-dd");
        spnFechaInicio.setEditor(editorInicio);
        spnFechaFin.setEditor(editorFin);
        JComboBox<String> cmbMetodoPago = new JComboBox<>(new String[]{"Tarjeta de crédito", "Efectivo"});

        agregarCampo("* Nombre:", txtNombre, gbc);
        agregarCampo("* Apellido:", txtApellido, gbc);
        agregarCampo("* Teléfono:", txtTelefono, gbc);
        agregarCampo("* Dirección:", txtDireccion, gbc);
        agregarCampo("* Email:", txtEmail, gbc);
        agregarCampo("* Marca:", cmbMarca, gbc);
        agregarCampo("* Modelo:", cmbModelo, gbc);
        agregarCampo("* Fecha de inicio:", spnFechaInicio, gbc);
        agregarCampo("* Fecha de fin:", spnFechaFin, gbc);
        agregarCampo("Método de pago:", cmbMetodoPago, gbc);
        
        JCheckBox chkTerminos = new JCheckBox("Acepto los términos y condiciones");
        gbc.gridx = 1;
        panelCampos.add(chkTerminos, gbc);

        panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnReservar = new JButton("Reservar");

        btnReservar.addActionListener(e -> procesarFormulario(txtNombre, txtApellido, txtTelefono, txtDireccion, txtEmail,
                cmbMarca, cmbModelo, spnFechaInicio, spnFechaFin, cmbMetodoPago, chkTerminos));

        panelBotones.add(btnReservar);
        add(panelCampos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        setVisible(true);
    }
    
    private void agregarCampo(String label, JComponent campo, GridBagConstraints gbc) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.BOLD, 14));
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        panelCampos.add(jLabel, gbc);
        gbc.gridx = 1;
        panelCampos.add(campo, gbc);
        gbc.gridy++;
    }

    private void procesarFormulario(JTextField txtNombre, JTextField txtApellido, JTextField txtTelefono,
                                    JTextField txtDireccion, JTextField txtEmail, JComboBox<String> cmbMarca, 
                                    JComboBox<String> cmbModelo, JSpinner spnFechaInicio, JSpinner spnFechaFin,
                                    JComboBox<String> cmbMetodoPago, JCheckBox chkTerminos) {
        if (!chkTerminos.isSelected()) {
            JOptionPane.showMessageDialog(this, "Debes aceptar los términos y condiciones.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://hl1354.dinaserver.com:3306/Reto2MySQL", "uxue", "G_nazaret_U2024")) {
            con.setAutoCommit(false);

            PreparedStatement pstCliente = con.prepareStatement(
                "INSERT INTO clientes (nombre, apellido, telefono, email, direccion, fecha_registro) VALUES (?, ?, ?, ?, ?, CURDATE())", 
                Statement.RETURN_GENERATED_KEYS
            );
            pstCliente.setString(1, txtNombre.getText());
            pstCliente.setString(2, txtApellido.getText());
            pstCliente.setString(3, txtTelefono.getText());
            pstCliente.setString(4, txtEmail.getText());
            pstCliente.setString(5, txtDireccion.getText());
            pstCliente.executeUpdate();

            ResultSet rsCliente = pstCliente.getGeneratedKeys();
            int idCliente = -1;
            if (rsCliente.next()) {
                idCliente = rsCliente.getInt(1);
            }

            PreparedStatement pstCoche = con.prepareStatement(
                "SELECT id_coche FROM coches WHERE marca = ? AND modelo = ? LIMIT 1"
            );
            pstCoche.setString(1, cmbMarca.getSelectedItem().toString());
            pstCoche.setString(2, cmbModelo.getSelectedItem().toString());
            ResultSet rsCoche = pstCoche.executeQuery();

            int idCoche = -1;
            if (rsCoche.next()) {
                idCoche = rsCoche.getInt("id_coche");
            } else {
                JOptionPane.showMessageDialog(this, "No hay coches disponibles para esta marca y modelo.", "Error", JOptionPane.ERROR_MESSAGE);
                con.rollback();
                return;
            }

            PreparedStatement pstAlquiler = con.prepareStatement(
                "INSERT INTO alquileres (id_cliente, id_coche, fecha_inicio, fecha_fin, metodo_pago, fecha_pago) VALUES (?, ?, ?, ?,?, CURDATE())"
            );
            pstAlquiler.setInt(1, idCliente);
            pstAlquiler.setInt(2, idCoche);
            pstAlquiler.setDate(3, new java.sql.Date(((Date) spnFechaInicio.getValue()).getTime()));
            pstAlquiler.setDate(4, new java.sql.Date(((Date) spnFechaFin.getValue()).getTime()));
            pstAlquiler.setString(5, cmbMetodoPago.getSelectedItem().toString());
            pstAlquiler.executeUpdate();

            con.commit();

            agregarReservaXML(txtNombre.getText(), txtApellido.getText(), txtTelefono.getText(), txtDireccion.getText(),
                    txtEmail.getText(), cmbMarca.getSelectedItem().toString(), cmbModelo.getSelectedItem().toString(),
                    new java.text.SimpleDateFormat("yyyy-MM-dd").format(spnFechaInicio.getValue()),
                    new java.text.SimpleDateFormat("yyyy-MM-dd").format(spnFechaFin.getValue()), 
                    cmbMetodoPago.getSelectedItem().toString());

            JOptionPane.showMessageDialog(this, "Reserva registrada con éxito.", "Confirmación", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar la reserva: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarReservaXML(String nombre, String apellido, String telefono, String direccion, String email,
                                   String marca, String modelo, String fechaInicio, String fechaFin, String metodoPago) {
        try {
            File xmlFile = new File("src/xml/reservas.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();

            Element reserva = document.createElement("reserva");

            crearElemento(document, reserva, "nombre", nombre);
            crearElemento(document, reserva, "apellido", apellido);
            crearElemento(document, reserva, "telefono", telefono);
            crearElemento(document, reserva, "direccion", direccion);
            crearElemento(document, reserva, "email", email);
            crearElemento(document, reserva, "marca", marca);
            crearElemento(document, reserva, "modelo", modelo);
            crearElemento(document, reserva, "fecha_inicio", fechaInicio);
            crearElemento(document, reserva, "fecha_fin", fechaFin);
            crearElemento(document, reserva, "metodo_pago", metodoPago);

            root.appendChild(reserva);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void crearElemento(Document document, Element parent, String nombreElemento, String valor) {
        Element elemento = document.createElement(nombreElemento);
        elemento.setTextContent(valor);
        parent.appendChild(elemento);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AlquilerCoches::new);
    }
}
