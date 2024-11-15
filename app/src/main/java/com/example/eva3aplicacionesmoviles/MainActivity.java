package com.example.eva3aplicacionesmoviles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private FloatingActionButton fabAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("items");

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList, this);  // Pasamos contexto al adaptador
        recyclerView.setAdapter(itemAdapter);

        // Leer datos de Firebase y actualizar RecyclerView
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null) {
                        itemList.add(item);
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores
                databaseError.toException().printStackTrace();
            }
        });

        // Botón flotante para agregar ítems
        fabAddItem = findViewById(R.id.fabAddItem);
        fabAddItem.setOnClickListener(view -> {
            // Abrir la actividad AddItemActivity para agregar un nuevo ítem
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivity(intent);
        });
    }

    // Método para eliminar un ítem de Firebase
    public void deleteItem(Item item) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items");
        databaseReference.child(item.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ítem eliminado.", Toast.LENGTH_SHORT).show();
                    itemList.remove(item); // Eliminar de la lista
                    itemAdapter.notifyDataSetChanged(); // Actualizar el RecyclerView
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar ítem.", Toast.LENGTH_SHORT).show());
    }

    // Método para editar un ítem (abrir la actividad EditItemActivity)
    public void editItem(Item item) {
        Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
        intent.putExtra("itemId", item.getId()); // Pasar el ID del ítem
        startActivity(intent);
    }
}


