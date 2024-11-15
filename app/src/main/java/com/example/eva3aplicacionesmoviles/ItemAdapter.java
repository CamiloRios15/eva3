package com.example.eva3aplicacionesmoviles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> itemList;
    private MainActivity mainActivity;

    // Constructor que recibe la lista de ítems y el contexto de MainActivity
    public ItemAdapter(List<Item> itemList, MainActivity mainActivity) {
        this.itemList = itemList;
        this.mainActivity = mainActivity;  // Pasar el contexto de MainActivity
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvQuantity.setText("Cantidad: " + item.getQuantity());
        holder.tvExpirationDate.setText("Vence: " + item.getExpirationDate());

        // Acción para editar el ítem
        holder.btnEdit.setOnClickListener(v -> mainActivity.editItem(item));

        // Acción para eliminar el ítem
        holder.btnDelete.setOnClickListener(v -> deleteItem(item, position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void deleteItem(Item item, int position) {
        // Eliminar el ítem de Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items");
        databaseReference.child(item.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(mainActivity, "Ítem eliminado.", Toast.LENGTH_SHORT).show();
                    itemList.remove(position); // Remover el ítem de la lista en memoria
                    notifyItemRemoved(position); // Notificar al adaptador para actualizar el RecyclerView
                })
                .addOnFailureListener(e -> Toast.makeText(mainActivity, "Error al eliminar ítem.", Toast.LENGTH_SHORT).show());
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvExpirationDate;
        Button btnEdit, btnDelete;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpirationDate = itemView.findViewById(R.id.tvExpirationDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);  // Botón de editar
            btnDelete = itemView.findViewById(R.id.btnDelete);  // Botón de eliminar
        }
    }
}



