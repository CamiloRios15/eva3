package com.example.eva3aplicacionesmoviles;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etExpirationDate, etPresentation, etDescription;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpirationDate = findViewById(R.id.etExpirationDate);
        etPresentation = findViewById(R.id.etPresentation);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveItemToFirebase());
    }

    private void saveItemToFirebase() {
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
        String id = databaseReference.push().getKey();

        if (id != null) {
            Item newItem = new Item(id, name, quantity, expirationDate, presentation, description);
            databaseReference.child(id).setValue(newItem)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Ítem agregado correctamente.", Toast.LENGTH_SHORT).show();
                        finish(); // Regresa a la pantalla principal
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al agregar ítem.", Toast.LENGTH_SHORT).show());
        }
    }
}
