package com.example.bd2_veterinaria.ui.home;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.example.bd2_veterinaria.R;
import com.example.bd2_veterinaria.DatabaseConnection;

public class HomeFragment extends Fragment {

    private EditText editTextNombre, editTextApellido, editTextTelefono, editTextDireccion;
    private Spinner spinnerTipoCargo;
    private Button btnEnviar;

    private Button btnLimpiar;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar vistas
        editTextNombre = root.findViewById(R.id.editTextNombre);
        editTextApellido = root.findViewById(R.id.editTextApellido);
        editTextTelefono = root.findViewById(R.id.editTextTelefono);
        editTextDireccion = root.findViewById(R.id.editTextDireccion);
        spinnerTipoCargo = root.findViewById(R.id.spinnerTipoCargo);
        btnEnviar = root.findViewById(R.id.btnEnviar);

        // Configurar el spinner con los tipos de cargo
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.tipos_cargo_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoCargo.setAdapter(adapter);

        // Configurar el botón de enviar
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarDatosAServidor();
            }
        });

        return root;
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private void enviarDatosAServidor() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conexion = null;

        try {
            // Establecer la conexión con SQL Server
            conexion = DatabaseConnection.getConnection();

            // Obtener los valores ingresados por el usuario
            String nombre = editTextNombre.getText().toString();
            String apellido = editTextApellido.getText().toString();
            String telefono = editTextTelefono.getText().toString();
            String direccion = editTextDireccion.getText().toString();
            String tipoCargo = spinnerTipoCargo.getSelectedItem().toString();

            // Cambios en la inserción de datos
            String query = "INSERT INTO Clientes (Nombre, Apellido, Telefono, Direccion) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setString(1, nombre);
                preparedStatement.setString(2, apellido);
                preparedStatement.setString(3, telefono);
                preparedStatement.setString(4, direccion);
                preparedStatement.executeUpdate();
            }

            // Mostrar un mensaje de éxito
            mostrarMensaje("Datos enviados con éxito");

        } catch (Exception e) {
            // Manejar las excepciones, por ejemplo, mostrar un mensaje de error
            e.printStackTrace();
            mostrarMensaje("Error al enviar los datos: " + e.getMessage());

        } finally {
            // Cerrar la conexión
            DatabaseConnection.closeConnection(conexion);
        }
    }

}