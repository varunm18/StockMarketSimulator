package com.example.finalproj;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class StockView extends AppCompatActivity {
    String name;
    GraphView graph;
    TextView stock_name, priceText;
    EditText amt;
    Button buy, back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_view);

        graph = (GraphView)findViewById(R.id.id_graph);
        stock_name = findViewById(R.id.textView2);
        priceText = findViewById(R.id.textView3);
        back = findViewById(R.id.back);

        name = getIntent().getStringExtra(Stocks.CODE);
        new AsyncThread().execute(name);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockView.this, Stocks.class);
                startActivity(intent);
            }
        });

    }

    public class AsyncThread extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                JSONObject json;

                String id = strings[0];
                URL url = new URL("https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + id + "&interval=1min&apikey=QVU2FF3Q9NWDO8U1");
                Log.d("url", url.toString());
                URLConnection connect = url.openConnection();
                InputStream stream = connect.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
                String text = "";
                String line = "";
                while ((line = buffer.readLine()) != null) {
                    text += line;
                }
                json = new JSONObject(text);

                return json;

            } catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            try {
                JSONObject time = json.getJSONObject("Time Series (1min)");
                JSONArray arr = time.names();
                String lastKey = (String) arr.get(arr.length()-1);
                String price = time.getJSONObject(lastKey).getString("1. open");
                Log.d("Poop", price);
                ArrayList<Double> priceList = new ArrayList<>();


                for(int i = arr.length()-1; i >= 0; i--){
                    priceList.add(Double.parseDouble(time.getJSONObject(arr.get(i).toString()).getString("1. open")));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String name = null;
                        try {
                            name = json.getJSONObject("Meta Data").getString("2. Symbol").toUpperCase();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        priceText.setText(priceList.get(priceList.size()-1)+"");
                        stock_name.setText("Stock ID: "+name);
                    }
                });

//                buy.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        startinPrice -= priceList.get(priceList.size()-1);
//                        Log.d("Hi", "" + startinPrice);
//                    }
//                });

//                Log.d("Hi", "" + startinPrice);

                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();;
                double x,y;
                x=0;
                graph.removeAllSeries();
                for(int i = 0; i< priceList.size(); i++){
                    x+=1;
                    y = priceList.get(i);
                    //Log.d("items", ""+priceList.get(i));
                    series.appendData(new DataPoint(x,y), true, 500);
                }
                graph.addSeries(series);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}