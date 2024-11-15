package com.example.eva3aplicacionesmoviles;

import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VencimientoWorker extends Worker {

    private static final String CHANNEL_ID = "vencimiento_channel";
    private Context context;

    public VencimientoWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        // Obtener la referencia de Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items");

        // Escuchar los cambios en los datos
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long currentTime = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sdf.format(new Date(currentTime));

                // Iterar sobre los ítems y verificar la fecha de vencimiento
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null) {
                        String expirationDate = item.getExpirationDate();
                        if (expirationDate.equals(currentDate)) {
                            // Si la fecha de vencimiento es hoy, enviar una notificación
                            sendNotification(item);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores
                Toast.makeText(context, "Error al consultar los datos", Toast.LENGTH_SHORT).show();
            }
        });

        return Result.success();  // Retornar el resultado exitoso
    }

    private void sendNotification(Item item) {
        // Crear la notificación
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Vencimiento de Ítem: " + item.getName())
                .setContentText("El ítem '" + item.getName() + "' está por vencer hoy.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Obtener el servicio de notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, notificationBuilder.build());  // Mostrar la notificación
        }
    }
}

