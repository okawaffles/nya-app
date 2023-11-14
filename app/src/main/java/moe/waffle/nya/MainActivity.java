package moe.waffle.nya;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import moe.waffle.nya.databinding.ActivityMainBinding;
import moe.waffle.nya.SettingsActivity.*;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    String name;
    public static String activeEndpoint = "waifupics_sfw";
    public static int fab = R.id.fab;

    // helper funcs
    /*private String getActivatedAPIEndpoint() {


        if (api0.isActivated()) return "waifupics_sfw";
        if (api1.isActivated()) return "waifupics_nsfw";
        if (api2.isActivated()) return "nekoslife_sfw";
        if (api3.isActivated()) return "nekoslife_cuddle";
        else return "waifupics_sfw";
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView iv;
        Switch sw;
        ProgressBar loading;
        FloatingActionButton settings;

        // endpoints list
        HashMap<String, String> endpoints = new HashMap<String, String>();
        endpoints.put("waifupics_sfw", "https://api.waifu.pics/sfw/neko");
        endpoints.put("waifupics_nsfw", "https://api.waifu.pics/nsfw/neko");
        endpoints.put("nekoslife_sfw", "https://nekos.life/api/v2/img/neko");
        endpoints.put("nekoslife_gecg", "https://nekos.life/api/v2/img/gecg");


        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iv = findViewById(R.id.imageView);
        //sw = findViewById(R.id.nsfw);
        loading = findViewById(R.id.progressBar);
        settings = findViewById(R.id.settings);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                String url;
                url = endpoints.get(activeEndpoint);

                try {
                    RequestQueue volleyQueue = Volley.newRequestQueue(MainActivity.this);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            // using GET HTTP method:
                            Request.Method.GET,
                            // url that we wanna get:
                            url,
                            // this parameter is used to send a JSON object to the
                            // server, since this is not required in our case,
                            // we are keeping it `null`
                            null,

                            // lambda function for handling the case
                            // when the HTTP request succeeds
                            (Response.Listener<JSONObject>) response -> {
                                // get the image url from the JSON object
                                String imageUrl;
                                try {
                                    imageUrl = response.getString("url");

                                    if (imageUrl.startsWith("https://i.waifu.pics"))
                                        name = imageUrl.split("https://i.waifu.pics/")[1];
                                    else if (imageUrl.startsWith("https://cdn.nekos.life/neko/")) name = imageUrl.split("https://cdn.nekos.life/neko/")[1];
                                    else if (imageUrl.startsWith("https://cdn.nekos.life/gecg/")) name = imageUrl.split("https://cdn.nekos.life/gecg/")[1];

                                    // load the image into the ImageView using Glide.
                                    Glide.with(MainActivity.this).load(imageUrl).into(iv);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },

                            // lambda function for handling the case
                            // when the HTTP request fails
                            (Response.ErrorListener) error -> {
                                // make a Toast telling the user
                                // that something went wrong
                                loading.setVisibility(View.GONE);
                                Snackbar.make(view, "Failed to Nya! 3:", Snackbar.LENGTH_SHORT)
                                        .setAnchorView(R.id.fab)
                                        .setAction("Action", null).show();
                                // log the error message in the error stream
                                Log.e("MainActivity", "retrieve nya error: ${error.localizedMessage}");
                            }
                    );

                    volleyQueue.add(jsonObjectRequest);

                    Snackbar.make(view, "Nya!", Snackbar.LENGTH_SHORT)
                            .setAnchorView(R.id.fab)
                            .setAction("Action", null).show();
                } catch (Exception e) {
                    System.out.println(e);
                    Snackbar.make(view, "Failed to Nya! 3: :: "+e, Snackbar.LENGTH_SHORT)
                            .setAnchorView(R.id.fab)
                            .setAction("Action", null).show();
                }
            }
        });

        binding.saveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BitmapDrawable draw = (BitmapDrawable) iv.getDrawable();
                    Bitmap bitmap = draw.getBitmap();
                    FileOutputStream outStream = null;
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/Download/nya");
                    dir.mkdirs();
                    String fileName = "nya_"+System.currentTimeMillis()+".png";
                    File outFile = new File(dir, fileName);
                    outStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(outFile));
                    sendBroadcast(intent);

                    Snackbar.make(view, "Saved image!", Snackbar.LENGTH_LONG)
                            .setAnchorView(R.id.fab)
                            .setAction("Action", null).show();
                } catch (Exception e) {
                    Log.e("SaveImage", e.toString());
                    Snackbar.make(view, "Failed to save image 3:", Snackbar.LENGTH_LONG)
                            .setAnchorView(R.id.fab)
                            .setAction("Action", null).show();
                }
            }
        });

        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        /*binding.nsfw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.nsfw.isChecked()) return;

                Snackbar.make(v, "Horny pastry puffer...", Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });*/
    }

    void setName(String pname) {
        name = pname;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}