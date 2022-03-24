package fr.isep.news;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import fr.isep.news.databinding.ActivityWelcomeBinding;


public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;

    int SPLASH_TIME = 500; //0.5 seconds

    //flag
    final Message message = new Message();

    final Thread thread;
    final Handler handler;

    public WelcomeActivity() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else if (msg.what == 0) {
                    thread.interrupt();
                }
            }
        };

        thread = new Thread(() -> {
            try {
                Thread.sleep(SPLASH_TIME);
                message.what = 1;
                handler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        thread.start();

        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.WbtnSkip.setOnClickListener(v -> {
            message.what = 0;
            handler.sendMessage(message);
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
