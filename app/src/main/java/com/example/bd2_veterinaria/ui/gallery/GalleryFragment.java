package com.example.bd2_veterinaria.ui.gallery;

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

import com.example.bd2_veterinaria.R;
import com.example.bd2_veterinaria.DatabaseConnection;

public class GalleryFragment extends Fragment {

    private EditText editTextNombre, editTextApellido;
    private Spinner spinnerTipoCargo;
    private Button btnInsertar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        editTextNombre = root.findViewById(R.id.editTextNombre);
        editTextApellido = root.findViewById(R.id.editTextApellido);
        spinnerTipoCargo = root.findViewById(R.id.spinnerTipoCargo);
        btnInsertar = root.findViewById(R.id.btnInsertar);  // Corregido el nombre del botón

        // Configurar el spinner con los tipos de cargo
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.tipos_cargo_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoCargo.setAdapter(adapter);

        // Configurar el botón de insertar
        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarDatosAPersonal();  // Corregido el nombre del método
            }
        });
        return root;
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private void insertarDatosAPersonal() {  // Cambiado el nombre del método
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conexion = null;

        try {
            // Establecer la conexión con SQL Server
            conexion = DatabaseConnection.getConnection();

            // Obtener los valores ingresados por el usuario
            String nombre = editTextNombre.getText().toString();
            String apellido = editTextApellido.getText().toString();
            String tipoCargo = spinnerTipoCargo.getSelectedItem().toString();

            // Cambios en la inserción de datos
            String query = "INSERT INTO Personal (Nombre, Apellido, TipoCargo) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setString(1, nombre);
                preparedStatement.setString(2, apellido);
                preparedStatement.setString(3, tipoCargo);
                preparedStatement.executeUpdate();
            }

            // Mostrar un mensaje de éxito
            mostrarMensaje("Datos enviados con éxito a Personal");

        } catch (Exception e) {
            // Manejar las excepciones, por ejemplo, mostrar un mensaje de error
            e.printStackTrace();
            mostrarMensaje("Error al enviar los datos a Personal: " + e.getMessage());

        } finally {
            // Cerrar la conexión
            DatabaseConnection.closeConnection(conexion);
        }
    }
}
