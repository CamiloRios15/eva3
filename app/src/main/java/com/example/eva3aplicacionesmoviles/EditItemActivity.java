package com.example.eva3aplicacionesmoviles;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditItemActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etExpirationDate, etPresentation, etDescription;
    private Button btnSave;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Obtener el ID del ítem a editar
        itemId = getIntent().getStringExtra("itemId");

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpirationDate = findViewById(R.id.etExpirationDate);
        etPresentation = findViewById(R.id.etPresentation);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);

        // Cargar los datos del ítem a editar desde Firebase (se puede omitir si no es necesario cargar antes de editar)
        // Aquí debes implementar la carga de los datos a través del `itemId`

        btnSave.setOnClickListener(v -> updateItemInFirebase());
    }

    private void updateItemInFirebase() {
        String name = etName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String expirationDate = etExpirationDate.getText().toString().trim();
        String presentation = etPresentation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(expirationDate)) {
            Toast.makeText(this, "Por favor, completa todos los campos obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items");
        databaseReference.child(itemId).setValue(new Item(itemId, name, quantity, expirationDate, presentation, description))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ítem actualizado correctamente.", Toast.LENGTH_SHORT).show();
                    finish(); // Regresa a la pantalla principal
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar ítem.", Toast.LENGTH_SHORT).show());
    }
}
