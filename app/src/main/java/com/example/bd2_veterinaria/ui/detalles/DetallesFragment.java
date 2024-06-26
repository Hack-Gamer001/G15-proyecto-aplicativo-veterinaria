package com.example.bd2_veterinaria.ui.detalles;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bd2_veterinaria.databinding.FragmentDetallesBinding;
import com.example.bd2_veterinaria.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetallesFragment extends Fragment {

    private FragmentDetallesBinding binding;
    private Spinner spinnerClientes;
    private Spinner spinnerTipoServicio;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DetallesViewModel detallesViewModel =
                new ViewModelProvider(this).get(DetallesViewModel.class);

        binding = FragmentDetallesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDetails;
        detallesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Configurar Spinner y otros elementos aquí
        configureSpinners();
        cargarClientesEnSpinner();

        Button btnGuardarDetalles = binding.btnGuardarDetalles;
        btnGuardarDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validar que se haya seleccionado un cliente y un tipo de servicio
                if (spinnerClientes.getSelectedItem() != null && spinnerTipoServicio.getSelectedItem() != null) {
                    guardarDetalles();
                } else {
                    mostrarMensaje("Selecciona un cliente y un tipo de servicio antes de guardar detalles.");
                }
            }
        });

        return root;
    }

    private void configureSpinners() {
        spinnerClientes = binding.spinnerClientes;
        spinnerTipoServicio = binding.spinnerTipoServicio;
        EditText editTextDetalles = binding.editTextDetalles;

        // Crear un mapa para asociar las opciones con las descripciones
        Map<String, String> mapaServicios = new HashMap<>();
        mapaServicios.put("Atención de Emergencia", "Atención veterinaria de emergencia");
        mapaServicios.put("Cirugía", "Procedimientos quirúrgicos");
        mapaServicios.put("Hospitalización", "Hospitalización de animales enfermos");
        mapaServicios.put("Vacunación", "Vacunación de mascotas");
        mapaServicios.put("Desparasitación", "Tratamiento para eliminar parásitos");

        // Configurar el spinner de Tipo de Servicio
        ArrayAdapter<String> tipoServicioAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>(mapaServicios.keySet())
        );
        tipoServicioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoServicio.setAdapter(tipoServicioAdapter);

        // Configurar un listener para el spinner de Tipo de Servicio
        spinnerTipoServicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Obtener la opción seleccionada
                String opcionSeleccionada = parentView.getItemAtPosition(position).toString();

                // Obtener y mostrar la descripción correspondiente
                String descripcion = mapaServicios.get(opcionSeleccionada);
                editTextDetalles.setText(descripcion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Manejar caso en el que no se selecciona nada
                editTextDetalles.setText("");
            }
        });
    }

    private void cargarClientesEnSpinner() {
        // Deshabilitar StrictMode para operaciones de red en el hilo principal
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


    private void guardarDetalles() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conexion = null;

        try {
            // Establecer la conexión con SQL Server
            conexion = DatabaseConnection.getConnection();

            // Obtener el ID del cliente seleccionado en el Spinner
            String clienteSeleccionado = spinnerClientes.getSelectedItem().toString();
            int clienteID = Integer.parseInt(clienteSeleccionado.split("\\|")[1]);

            // Obtener el ID del servicio seleccionado en el Spinner
            int servicioID = spinnerTipoServicio.getSelectedItemPosition() + 1; // Suponiendo que los IDs de servicio comienzan desde 1

            // Obtener la descripción de los detalles
            String detalles = binding.editTextDetalles.getText().toString();

            // Puedes obtener el ID de la cita de alguna manera
            int citaID = obtenerCitaID();

            // Inserción de datos en la tabla Detalles
            String query = "INSERT INTO Detalles (CitaID, ServicioID, Descripcion) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, citaID);
                preparedStatement.setInt(2, servicioID);
                preparedStatement.setString(3, detalles);
                preparedStatement.executeUpdate();
            }

            // Mostrar un mensaje de éxito
            mostrarMensaje("Detalles guardados con éxito");

            // Mostrar una notificación o un Toast
            mostrarNotificacion("Detalles agregados a la base de datos");

        } catch (Exception e) {
            // Manejar las excepciones, por ejemplo, mostrar un mensaje de error
            e.printStackTrace();
            mostrarMensaje("Error al guardar los detalles: " + e.getMessage());

        } finally {
            // Cerrar la conexión
            DatabaseConnection.closeConnection(conexion);
        }
    }

    private void mostrarNotificacion(String mensaje) {
        // Mostrar un Toast con el mensaje
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
    }


    // Método ficticio para obtener el ID de la cita, debes implementarlo según tu lógica
    private int obtenerCitaID() {
        // Implementa tu lógica para obtener el ID de la cita
        return 1; // Suponiendo que el ID de la cita es 1
    }

    private void mostrarMensaje(String mensaje) {
        // Mostrar el mensaje, por ejemplo, en un Toast
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}