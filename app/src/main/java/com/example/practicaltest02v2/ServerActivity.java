package com.example.practicaltest02v2;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerActivity extends AppCompatActivity {

    private TextView serverTimeTextView;
    private boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        serverTimeTextView = findViewById(R.id.server_time_text_view);

        // Pornim thread-ul client
        new Thread(new ClientRunnable()).start();
    }

    @Override
    protected void onDestroy() {
        isRunning = false; // Oprim bucla cand iesim
        super.onDestroy();
    }

    private class ClientRunnable implements Runnable {
        @Override
        public void run() {
            Socket socket = null;
            try {
                // 10.0.2.2 este "localhost"-ul PC-ului vazut din Emulator
                Log.d("ServerActivity", "Connecting to server...");
                socket = new Socket("localhost", 6000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (isRunning) {
                    // Citim linia trimisa de server (ora)
                    String line = reader.readLine();
                    if (line == null) break;

                    Log.d("ServerActivity", "Received: " + line);

                    // Actualizam UI-ul (obligatoriu pe UI Thread)
                    final String timeData = line;
                    runOnUiThread(() -> serverTimeTextView.setText(timeData));
                }

            } catch (Exception e) {
                Log.e("ServerActivity", "Error", e);
                runOnUiThread(() -> serverTimeTextView.setText("Error connecting!"));
            } finally {
                try { if (socket != null) socket.close(); } catch (Exception e) {}
            }
        }
    }
}