package moe.waffle.nya;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

public class nyanfinity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nyanfinity);

        Log.d("nyanfinity", "nyanfinity discovered");

        // show an alert box telling the user what this is
        AlertDialog.Builder builder = new AlertDialog.Builder(nyanfinity.this);
        builder.setMessage("Welcome to nyanfinity mode! This mode uses a separate API to create an endless scroll of nyantastic images!\n \nWARNING: This may lag your device a lot. This feature is NOT final. Please report bugs.");
        builder.setTitle("You've found a secret!");
        builder.setPositiveButton("Cool!", (DialogInterface.OnClickListener) (dialog, which) -> {
           dialog.cancel();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // start generation of infinite scroll
        // get images :3
        Log.d("nyanfinity", "nyanfinity starting...");
        String API_URL = "https://api.nekosapi.com/v3/images?limit=25&offset=";
        AtomicInteger offset = new AtomicInteger();
        LinearLayout scrollable = findViewById(R.id.scrollable);


        RequestQueue volleyQueue = Volley.newRequestQueue(nyanfinity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL + offset, null, (Response.Listener<JSONObject>) response -> {
            // load new imageviews
            Log.d("nyanfinity", "got response!");
            try {
                JSONArray items = response.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    //Log.d("nyanfinity", items.get(i).toString());
                    JSONObject item = new JSONObject(items.get(i).toString());
                    String imgURL = item.getString("image_url");

                    // make new imageview
                    ImageView newImageView = new ImageView(nyanfinity.this);
                    Glide.with(nyanfinity.this).load(imgURL).into(newImageView);
                    // add to our layout
                    ((ViewGroup) scrollable).addView(newImageView);
                }
            } catch (JSONException e) {
                Log.d("nyanfinity", "failed to load images into view");
            }
        },
        (Response.ErrorListener) error -> {
            // die i guess
            Log.d("nyanfinity", "Failed to get images.");
            // alertbox because sometimes response bad
            AlertDialog.Builder errorDiag = new AlertDialog.Builder(nyanfinity.this);
            errorDiag.setTitle("Nyanfinity Error");
            errorDiag.setMessage("Error in Network Request to Nekos API. This usually means the server is down. Please try again.");
            errorDiag.setPositiveButton("OK", (DialogInterface.OnClickListener) (dg, which) -> { dg.cancel(); });

            AlertDialog ed = errorDiag.create();
            ed.show();
        });

        // start request
        volleyQueue.add(jsonObjectRequest);


        // detect end of scroll
        AtomicInteger globalscrollY = new AtomicInteger();
        scrollable.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View view = scrollable.getChildAt(scrollable.getChildCount() - 1);
            int bottomDetector = view.getBottom() - (scrollable.getHeight() + scrollY);
            if (bottomDetector == 0) {
                offset.addAndGet(25);
                Log.d("nyanfinity", "end reached");
                volleyQueue.add(jsonObjectRequest);
            } else {
                globalscrollY.set(scrollY);
            }
        });

        scrollable.setOnLongClickListener(v -> {
            Log.d("nyanfinity", "scroll value " + scrollable.getScrollY());
            return true;
        });
    }
}