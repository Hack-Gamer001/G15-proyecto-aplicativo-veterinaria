package com.example.bd2_veterinaria.ui.estado;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.bd2_veterinaria.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.example.bd2_veterinaria.R;

public class EstadoFragment extends Fragment {

    private Spinner spinnerClientes;
    private Spinner spinnerCitas;
    private Spinner spinnerEstadoCita;
    private Button btnGuardarCita;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estado, container, false);

        spinnerClientes = view.findViewById(R.id.spinnerClienteID);
        spinnerCitas = view.findViewById(R.id.spinnerCitaID);
        spinnerEstadoCita = view.findViewById(R.id.spinnerEstadoCita);
        btnGuardarCita = view.findViewById(R.id.btnGuardarCita);

        // Configurar adaptadores para los Spinners
        cargarClientesEnSpinner();

        // Configurar listener para el Spinner de Clientes
        spinnerClientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Cargar las citas para el cliente seleccionado
                cargarCitasEnSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Manejar caso en el que no se selecciona nada
            }
        });

        ArrayAdapter<CharSequence> estadoCitaAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.estado_cita_options, android.R.layout.simple_spinner_item);

        estadoCitaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstadoCita.setAdapter(estadoCitaAdapter);

        // Configurar listener para el Spinner de Estado de Cita
        spinnerEstadoCita.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Puedes hacer algo con el estado seleccionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Manejar caso en el que no se selecciona nada
            }
        });

        // Configurar listener para el botón de guardar cita
        btnGuardarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCita();
            }
        });

        return view;
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

    private void cargarCitasEnSpinner() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conexion = null;
        List<String> nombresCitas = new ArrayList<>();

        try {
            // Establecer la conexión con SQL Server
            conexion = DatabaseConnection.getConnection();

            // Obtener el ID del cliente seleccionado
            String clienteSeleccionado = spinnerClientes.getSelectedItem().toString();
            int clienteID = Integer.parseInt(clienteSeleccionado.split("\\|")[1]);

            // Consulta para obtener citas del cliente
            String query = "SELECT CitaID, FechaCita FROM Citas WHERE ClienteID = ?";
            try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, clienteID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int citaID = resultSet.getInt("CitaID");
                        String fechaCita = resultSet.getString("FechaCita");
                        // Agregar la cita al listado
                        nombresCitas.add("CitaID: " + citaID + " | Fecha: " + fechaCita);
                    }
                }
            }

            // Configurar el adaptador para el Spinner de Citas
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    nombresCitas
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCitas.setAdapter(adapter);

        } catch (Exception e) {
            // Manejar las excepciones, por ejemplo, mostrar un mensaje de error
            e.printStackTrace();
            mostrarMensaje("Error al cargar las citas: " + e.getMessage());

        } finally {
            // Cerrar la conexión
            DatabaseConnection.closeConnection(conexion);
        }
    }

    private void guardarCita() {
        // Tu código para guardar la cita en la base de datos
    }

    // Otros métodos que puedas necesitar

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
    }
}
