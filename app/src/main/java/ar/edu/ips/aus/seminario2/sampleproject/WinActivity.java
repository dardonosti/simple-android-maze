package ar.edu.ips.aus.seminario2.sampleproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.w3c.dom.Text;

public class WinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setStatusBarColor(Color.rgb(238,157,49));
        window.setNavigationBarColor(Color.rgb(238,157,49));

        setContentView(R.layout.activity_win);
        TextView t = (TextView) findViewById(R.id.textView6);
        t.setText(getIntent().getExtras().getString("nombreGanador"));
    }

    public void NuevoJuego(View v) {
        Intent i = new Intent(WinActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void Salir(View v) {
        finish();
    }
}
