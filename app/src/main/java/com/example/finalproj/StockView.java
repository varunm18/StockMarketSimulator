package com.example.finalproj;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.core.util.Pair;

import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

public class StockView extends AppCompatActivity {
    String name;
    GraphView graph;
    TextView dateText, priceText, nameText, balanceText;
    EditText amt;
    Button buy, back, dateButton, create;
    Spinner dropdown;
    ArrayList<String> list;
    ElegantNumberButton increment;
    String date = "";
    String interval = "";
    int frequency = 0;
    boolean first = true;
    double price = 0;
    double balance = 0;
    User user;
    double amount = 0;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_view);

        graph = (GraphView)findViewById(R.id.graph);
        increment = (ElegantNumberButton)findViewById(R.id.number_button);
        dateText = findViewById(R.id.date);
        priceText = findViewById(R.id.price);
        back = findViewById(R.id.backButton);
        buy = findViewById(R.id.buy);
        amt = findViewById(R.id.amount);
        dateButton = findViewById(R.id.changeDate);
        dropdown = findViewById(R.id.spinner);
        create = findViewById(R.id.create);
        nameText = findViewById(R.id.stockName);
        balanceText = findViewById(R.id.balance);

        graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        graph.getGridLabelRenderer().setHighlightZeroLines(false);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().reloadStyles();

        reference = FirebaseDatabase.getInstance().getReference("users");

        name = getIntent().getStringExtra("name");
        user = (User) getIntent().getSerializableExtra("user");
        balance = user.getMoney();

        nameText.setText(name);
        balanceText.setText("Balance: $"+String.format("%.2f",balance));

        new AsyncThread().execute(name);

        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        Log.d("hi", "reached34");

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                date = materialDatePicker.getHeaderText();
                Log.d("hi", "date: "+date);
                dateText.setText(date);
            }
        });

        list = new ArrayList<String>();
        list.add("Select Interval:");
        list.add("Minute");
        list.add("Hour");
        list.add("Day");
        list.add("Week");
        list.add("Month");
        list.add("Quarter");
        list.add("Year");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list, list);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_list);
        dropdown.setAdapter(spinnerAdapter);
        dropdown.setPrompt("Select Interval");
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0){
                    interval = list.get(i).toLowerCase();
                }
                else
                {
                    interval = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        increment.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                frequency = Integer.parseInt(increment.getNumber());
                Log.d("hi", "increment: "+frequency);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date=="")
                {
                    Toast.makeText(getApplicationContext(),"Select a Date Range", Toast.LENGTH_SHORT).show();
                }
                else if(frequency<=0)
                {
                    Toast.makeText(getApplicationContext(),"Frequency Multiplier has to be Positive", Toast.LENGTH_SHORT).show();
                }
                else if(interval=="")
                {
                    Toast.makeText(getApplicationContext(),"Select an Interval", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Log.d("hi", "name: "+name+"date: "+date+"freq: "+frequency+"interval: "+interval);
                    new AsyncThread().execute(name);
                }
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    amount = Double.parseDouble(amt.getText().toString());
                }
                catch(Exception e)
                {
                    amount = 0;
                }

                if(amount<0.01)
                {
                    Toast.makeText(getApplicationContext(),"Error: Can't buy less than 0.01 shares", Toast.LENGTH_SHORT).show();
                }
                else if(amount*price>balance)
                {
                    Toast.makeText(getApplicationContext(),"Error: Not Enough Money in Account", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    String time = formatter.format(date);

                    Transaction transaction = new Transaction(name, time, "BUY", price, amount);
                    user.addTransactions(transaction);
                    user.purchase(amount*price);

                    reference.child(user.getUsername()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            balance = user.getMoney();
                            balanceText.setText("Balance: $"+String.format("%.2f",balance));
                            Toast.makeText(getApplicationContext(),"Success! Bought "+amount+" share(s) of "+name, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockView.this, Stocks.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

    }

    public class AsyncThread extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                JSONObject json;
                Log.d("hi", "name: "+name+"\ndate: "+date+"\nfreq: "+frequency+"\ninterval: "+interval);
                URL url;
                if(first)
                {
                    url = new URL("https://finnhub.io/api/v1/quote?symbol="+name+"&token=ci34231r01qmam6c1so0ci34231r01qmam6c1sog");
                }
                else
                {
                    String[] dates = convertDateRange(date);
                    Log.d("hi", "date 1: "+dates[0]+"\ndate 2: "+dates[1]);
                    url = new URL("https://api.polygon.io/v2/aggs/ticker/"+name+"/range/"+frequency+"/"+interval+"/"+dates[0]+"/"+dates[0]+"?adjusted=true&sort=asc&limit=50000&apiKey=k_NcvppBbThJp93qy0WcXHL_AtIeuDDB");
                }
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Error: Incorrect Date Range. Only allowed 5 calls/min and a 2 year range.", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            try {
                if(first)
                {
                    price = json.getDouble("c");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            priceText.setText("Price per Share: $"+String.format("%.2f",price));
                        }
                    });
                    if(price==0)
                    {
                        Toast.makeText(getApplicationContext(),"Error: Invalid Stock. Try Again", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StockView.this, Stocks.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }
                    first = false;
                }
                else
                {
                    JSONArray results = json.getJSONArray("results");
                    Log.d("hi", "result: "+results);

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                    double price;
                    graph.removeAllSeries();
                    for(int i = 0; i<results.length(); i++)
                    {
                        price = results.getJSONObject(i).getDouble("c");
                        series.appendData(new DataPoint(i,price), true, 500);
                    }
                    if(results.length()<2)
                    {
                        Toast.makeText(getApplicationContext(),"Choose Larger Range for Graph", Toast.LENGTH_LONG).show();
                    }
                    series.setColor(Color.WHITE);
                    series.setDrawBackground(true);
                    series.setDrawDataPoints(true);
                    graph.addSeries(series);
                }

            } catch (Exception e) {
                if(first)
                {
                    Toast.makeText(getApplicationContext(),"Error: Invalid Stock. Try Again", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StockView.this, Stocks.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
                Toast.makeText(getApplicationContext(),"Choose A Different Range for Graph", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public static String[] convertDateRange(String dateRange) {
        // Split the input string into two date parts
        String[] parts = dateRange.split(" – ");
        String startDate = parts[0];
        String endDate = parts[1];

        int count = dateRange.split(String.valueOf(","), -1).length - 1;
        if(count==1)
        {
            String year = endDate.substring(endDate.length() - 4);
            dateRange = startDate+", "+year+" – "+endDate;
            parts = dateRange.split(" – ");
            startDate = parts[0];
            endDate = parts[1];
        }

        int currentYear = Year.now().getValue();

        LocalDate start;
        LocalDate end;

        if (startDate.contains(",")) {
            start = parseDate(startDate);
            end = parseDate(endDate);
        } else {
            start = parseDate(startDate + ", " + currentYear);
            end = parseDate(endDate + ", " + currentYear);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startFinal = start.format(formatter);
        String endFinal = end.format(formatter);

        String[] result = {startFinal, endFinal};
        return result;
    }

    private static LocalDate parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        return LocalDate.parse(dateStr, formatter);
    }

}