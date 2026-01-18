package com.example.practicaltest02v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.practicaltest02v2.network.DictionaryThread;

public class PracticalTest02v2MainActivity extends AppCompatActivity {

    private EditText wordEditText;
    private TextView definitionTextView;
    private Button searchButton, serverButton;

    private DictionaryReceiver receiver = new DictionaryReceiver();
    private IntentFilter filter = new IntentFilter(DictionaryThread.ACTION_DICTIONARY);

    private class DictionaryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 3c) Primire si afisare
            String def = intent.getStringExtra(DictionaryThread.DATA_DEFINITION);
            definitionTextView.setText(def);
            Toast.makeText(context, "Definition received!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02v2_main);

        wordEditText = findViewById(R.id.word_edit_text);
        definitionTextView = findViewById(R.id.definition_text_view);
        searchButton = findViewById(R.id.search_button);
        serverButton = findViewById(R.id.navigate_server_button);

        searchButton.setOnClickListener(v -> {
            String word = wordEditText.getText().toString();
            if (word.isEmpty()) return;
            definitionTextView.setText("Searching...");
            new DictionaryThread(getApplicationContext(), word).start();
        });

        // Navigare catre Activitatea 2 (Server)
        serverButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ServerActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(receiver, filter);
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }
}