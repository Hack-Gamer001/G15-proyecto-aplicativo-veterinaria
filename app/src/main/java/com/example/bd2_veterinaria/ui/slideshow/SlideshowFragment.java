package com.example.bd2_veterinaria.ui.slideshow;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.bd2_veterinaria.R;
import com.example.bd2_veterinaria.DatabaseConnection;

public class SlideshowFragment extends Fragment {

    private Spinner spinnerClientes;
    private DatePicker datePickerFecha;
    private TimePicker timePickerHora;
    private EditText editTextMotivoCita;
    private Button btnGuardarCita;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        spinnerClientes = root.findViewById(R.id.spinnerClientes);
        datePickerFecha = root.findViewById(R.id.datePickerFecha);
        timePickerHora = root.findViewById(R.id.timePickerHora);
        editTextMotivoCita = root.findViewById(R.id.editTextMotivoCita);
        btnGuardarCita = root.findViewById(R.id.btnGuardarCita);

        // Configurar el botón de guardar cita
        btnGuardarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCita();
            }
        });

        // Cargar los nombres y apellidos de los clientes en el Spinner
        cargarClientesEnSpinner();

        return root;
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private void cargarClientesEnSpinner() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conexion = null;
        List<String> nombresClientes = new ArrayList<>();

        try {
            // Establecer la conexión con SQL Server
            conexion = DatabaseConnection.getConnection();

            // Consulta para obtener nombres y apellidos de clientes
            String query = "SELECT ClienteID, Nombre, Apellido FROM Clientes";
            try (PreparedStatement preparedStatement = conexion.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int clienteID = resultSet.getInt("ClienteID");
                    String nombre = resultSet.getString("Nombre");
                    String apellido = resultSet.getString("Apellido");
                    // Agregar el nombre completo al listado
                    nombresClientes.add(nombre + " " + apellido + "|" + clienteID);
                }
            }

            // Configurar el adaptador para el Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    nombresClientes
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerClientes.setAdapter(adapter);

        } catch (Exception e) {
            // Manejar las excepciones, por ejemplo, mostrar un mensaje de error
            e.printStackTrace();
            mostrarMensaje("Error al cargar los clientes: " + e.getMessage());

        } finally {
            // Cerrar la conexión
            DatabaseConnection.closeConnection(conexion);
        }
    }

    private void guardarCita() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conexion = null;

        try {
            // Establecer la conexión con SQL Server
            conexion = DatabaseConnection.getConnection();

            // Obtener la selección del Spinner (nombre completo | ID)
            String seleccion = spinnerClientes.getSelectedItem().toString();
            // Dividir la selección para obtener el ID del cliente
            int clienteID = Integer.parseInt(seleccion.split("\\|")[1]);

            // Obtener los valores de fecha y hora
            int year = datePickerFecha.getYear();
            int month = datePickerFecha.getMonth();
            int day = datePickerFecha.getDayOfMonth();

            int hour = timePickerHora.getCurrentHour();
            int minute = timePickerHora.getCurrentMinute();

            // Crear un objeto Timestamp para almacenar la fecha y hora
            Timestamp fechaHora = new Timestamp(year - 1900, month, day, hour, minute, 0, 0);

            // Obtener el motivo ingresado por el usuario
            String motivoCita = editTextMotivoCita.getText().toString();

            // Resto de los IDs que puedes obtener de tus fuentes
            int personalID = 2;
            int servicioID = 1;
            int estadoCitaID = 1;

            // Formatear la fecha y hora según el formato de tu base de datos
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fechaHoraFormateada = dateFormat.format(new Date(fechaHora.getTime()));

            // Inserción de datos en la tabla Citas
            String query = "INSERT INTO Citas (FechaCita, ClienteID, PersonalID, ServicioID, EstadoCitaID, Descripcion) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setString(1, fechaHoraFormateada);
                preparedStatement.setInt(2, clienteID);
                preparedStatement.setInt(3, personalID);
                preparedStatement.setInt(4, servicioID);
                preparedStatement.setInt(5, estadoCitaID);
                preparedStatement.setString(6, motivoCita);
                preparedStatement.executeUpdate();
            }

            // Mostrar un mensaje de éxito
            mostrarMensaje("Cita guardada con éxito");

        } catch (Exception e) {
            // Manejar las excepciones, por ejemplo, mostrar un mensaje de error
            e.printStackTrace();
            mostrarMensaje("Error al guardar la cita: " + e.getMessage());

        } finally {
            // Cerrar la conexión
            DatabaseConnection.closeConnection(conexion);
        }
    }

}

