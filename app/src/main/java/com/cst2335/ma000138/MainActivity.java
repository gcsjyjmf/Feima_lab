package com.cst2335.ma000138;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cst2335.ma000138.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    protected String cityName;
    RequestQueue queue = null;
    protected ActivityMainBinding binding;
    protected Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getForecast.setOnClickListener(click -> {

            cityName = binding.editText.getText().toString();
            String stringURL = null;
            try {
                stringURL = "https://api.openweathermap.org/data/2.5/weather?q="
                        + URLEncoder.encode(cityName,"UTF-8")
                        + "&appid=7e943c97096a9784391a981c4d878b22&units=metric";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //this goes in the button click handler:
            JsonObjectRequest request = new  JsonObjectRequest(Request.Method.GET, stringURL, null,
                    (response) -> {
                        try {
                            JSONObject coord = response.getJSONObject("coord");
                            int visibility = response.getInt("visibility");
                            String name = response.getString("name");
                            JSONArray weatherArray = response.getJSONArray( "weather");
                            JSONObject postion0 = weatherArray.getJSONObject(0);
                            String description = postion0.getString("description");
                            String iconName = postion0.getString("icon");

                            JSONObject mainObject = response.getJSONObject("main");
                            double current = mainObject.getDouble("temp");
                            double temp_min = mainObject.getDouble("temp_min");
                            double temp_max = mainObject.getDouble("temp_max");//
                            int humidity = mainObject.getInt("humidity");

                            String imageUrl = "https://openweathermap.org/img/w/" + iconName + ".png";
                            String pathname = getFilesDir()+"/" + iconName +".png";
                            File file = new File( pathname );

                            if(file.exists()){
                                image = BitmapFactory.decodeFile(pathname);
                            }
                            else{
                                ImageRequest imgReq = new ImageRequest( imageUrl,
                                        new Response.Listener<Bitmap> () {
                                            @Override
                                            public void onResponse(Bitmap bitmap) {

                                                try {
                                                    image = bitmap;
                                                    image.compress(Bitmap.CompressFormat.PNG, 100,
                                                            MainActivity.this.openFileOutput( iconName + ".png",
                                                                    Activity.MODE_PRIVATE));
                                                    FileOutputStream fOut = null;
                                                    try {
                                                        fOut = openFileOutput( iconName +".png", Context.MODE_PRIVATE);
                                                        image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                                        fOut.flush();
                                                        fOut.close();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                                runOnUiThread( () -> {
                                                    binding.icon.setImageBitmap(image);
                                                    binding.icon.setVisibility(View.VISIBLE);

                                                });


                                            }
                                        }, 1024, 1024, ImageView.ScaleType.CENTER, null,
                                        ( error) -> {

                                        });
                                queue.add(imgReq);
                            }



                            runOnUiThread( ( ) -> {
                                binding.temp.setText("The current temperature is " + current);
                                binding.temp.setVisibility(View.VISIBLE);

                                binding.min.setText("The min temperature is " + temp_min);
                                binding.min.setVisibility(View.VISIBLE);

                                binding.max.setText("The max temperature is " + temp_max);
                                binding.max.setVisibility(View.VISIBLE);

                                binding.hum.setText("The humidity is " + humidity);
                                binding.hum.setVisibility(View.VISIBLE);

                                binding.icon.setImageBitmap(image);
                                binding.icon.setVisibility(View.VISIBLE);

                                binding.desc.setText( description);
                                binding.desc.setVisibility(View.VISIBLE);
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    ( error ) -> {

                    }
            );

            queue.add( request );

        });


    }
}