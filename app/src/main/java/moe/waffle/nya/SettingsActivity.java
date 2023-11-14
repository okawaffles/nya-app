package moe.waffle.nya;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends AppCompatActivity {
    //private ActivityMainBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_settings);

        RadioButton api0 = findViewById(R.id.waifupics_sfw);
        RadioButton api1 = findViewById(R.id.waifupics_nsfw);
        RadioButton api2 = findViewById(R.id.nekoslife_sfw);
        RadioButton api3 = findViewById(R.id.nekoslife_gecg);

        // load which one is selected
        api0.setActivated(false);
        api1.setActivated(false);
        api2.setActivated(false);
        api3.setActivated(false);

        switch (MainActivity.activeEndpoint) {
            case "waifupics_nsfw":
                api1.setActivated(true);
                break;
            case "nekoslife_sfw":
                api2.setActivated(true);
                break;
            case "nekoslife_gecg":
                api3.setActivated(true);
                break;
            default:
                api0.setActivated(true);
                break;
        }

        api0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.activeEndpoint = "waifupics_sfw";
            }
        });
        api1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.activeEndpoint = "waifupics_nsfw";
                Snackbar.make(v, "Horny pastry puffer...", Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.settingsfab)
                        .setAction("Action", null).show();
            }
        });
        api2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.activeEndpoint = "nekoslife_sfw";
            }
        });
        api3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.activeEndpoint = "nekoslife_gecg";
            }
        });

        ImageView chocola = findViewById(R.id.chocola);
        chocola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, nyanfinity.class);
                startActivity(i);
            }
        });
    }
}
