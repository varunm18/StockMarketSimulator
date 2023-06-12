package com.example.finalproj;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Stocks extends AppCompatActivity {

    EditText stockName;
    Button getDetails;
    TextView moneyText, newsText, linkText, valText, profitText;
    ListView listView;
    User user;
    DatabaseReference reference;
    double balance = 0;
    CustomAdapter adapter;
    ImageView historyView, settingsView;
    double totalVal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);

        stockName = findViewById(R.id.stockID);
        getDetails = findViewById(R.id.getDetails);
        moneyText = findViewById(R.id.balanceT);
        listView = findViewById(R.id.list_id);
        historyView = findViewById(R.id.imageView);
        settingsView = findViewById(R.id.imageView2);
        newsText = findViewById(R.id.newsText);
        linkText = findViewById(R.id.newsText2);
        valText = findViewById(R.id.valutation);
        profitText = findViewById(R.id.profit);

        reference = FirebaseDatabase.getInstance().getReference("users");

        user = (User) getIntent().getSerializableExtra("user");
        balance = user.getMoney();
        moneyText.setText("Balance: $"+String.format("%.2f",balance));

        ArrayList<Portfolio> portfolios = TransactionsToPortfolio(user.getTransactions());

        adapter = new CustomAdapter(this, R.layout.adapter_layout, portfolios);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), i + "", Toast.LENGTH_LONG).show();
            }
        });

        getDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stocks = stockName.getText().toString();
                Intent intent = new Intent(Stocks.this, StockView.class);
                intent.putExtra("name",stocks);
                intent.putExtra("user", user);
                startActivity(intent);
                Log.d("pOOP","HI");
            }
        });

        historyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Stocks.this, History.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Stocks.this, Settings.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    public class CustomAdapter extends ArrayAdapter<Portfolio> {
        ArrayList<Portfolio> portfolios;
        Context context;
        int xmlResource;
        public CustomAdapter(Context context, int resource, ArrayList<Portfolio> objects) {
            super(context, resource, objects);
            xmlResource = resource;
            portfolios = objects;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            return super.getView(position, convertView, parent);
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterLayout = layoutInflater.inflate(xmlResource, null);

            TextView idText = adapterLayout.findViewById(R.id.stockId);
            TextView nameText = adapterLayout.findViewById(R.id.stockName);
            TextView sharesText = adapterLayout.findViewById(R.id.shares);
            TextView priceText = adapterLayout.findViewById(R.id.price);
            TextView totalText = adapterLayout.findViewById(R.id.total);
            EditText sellEdit = adapterLayout.findViewById(R.id.sellEditText);
            Button sell = adapterLayout.findViewById(R.id.sellButton);
            Button news = adapterLayout.findViewById(R.id.newsButton);

            Portfolio port = portfolios.get(position);
            if(position+1==portfolios.size())
            {
                new AsyncThread().execute(new AsyncParams(null, 1, port.getName(), priceText, totalText, port.getAmount(), true));//1->stock price, 2->stock name, 3->stock news
            }
            else
            {
                new AsyncThread().execute(new AsyncParams(null, 1, port.getName(), priceText, totalText, port.getAmount()));//1->stock price, 2->stock name, 3->stock news
            }
            new AsyncThread().execute(new AsyncParams(null, 2, port.getName(), nameText));//1->stock price, 2->stock name, 3->stock news

            idText.setText(port.getName());
            sharesText.setText(""+String.format("%.2f",port.getAmount()));

            news.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsText.setText("Loading...");
                    linkText.setText("");
                    new AsyncThreadNews().execute(new NewsParams(port.getName(), null));//1->stock price, 2->stock name, 3->stock news
                }
            });

            sell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    double amount = 0;
                    try {
                        amount = Double.parseDouble(sellEdit.getText().toString());
                    }
                    catch(Exception e)
                    {
                        amount = 0;
                    }
                    double price = Double.parseDouble(String.valueOf(priceText.getText()).replace("$", ""));

                    if(amount>port.getAmount())
                    {
                        Toast.makeText(getApplicationContext(), "Error: Not Enough Shares", Toast.LENGTH_SHORT).show();
                    }
                    else if(amount<0.01)
                    {
                        Toast.makeText(getApplicationContext(), "Error: Can't sell less than 0.01 stock", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();
                        String time = formatter.format(date);

                        Transaction transaction = new Transaction(port.getName(), time, "SELL", price, amount);
                        user.addTransactions(transaction);
                        user.sell(amount*price);

                        double amt = amount;

                        reference.child(user.getUsername()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                balance = user.getMoney();
                                moneyText.setText("Balance: $"+String.format("%.2f",balance));
                                Toast.makeText(getApplicationContext(),"Success! Sold "+amt+" share(s) of "+port.getName(), Toast.LENGTH_SHORT).show();
                                portfolios = TransactionsToPortfolio(user.getTransactions());
                                adapter = new CustomAdapter(Stocks.this, R.layout.adapter_layout, portfolios);
                                listView.setAdapter(adapter);
                            }
                        });
                    }
                }
            });

            return adapterLayout;
        }
    }

    public ArrayList<Portfolio> TransactionsToPortfolio(ArrayList<Transaction> transactions)
    {
        ArrayList<Portfolio> portfolios = new ArrayList<Portfolio>();

        boolean inside = false;

        for(int i=1; i<transactions.size(); i++)
        {
            int index = 0;
            Transaction trans = transactions.get(i);
            Log.d("hi", "Run "+i+", "+index+": "+portfolios.size());
            for(int j=0; j<portfolios.size(); j++)
            {
                if(!inside)
                {
                    if(trans.getName().equals(portfolios.get(j).getName()))
                    {
                        inside = true;
                        index = j;
                    }
                }
            }
            if(inside)
            {
                if(trans.getType().equals("BUY"))
                {
                    portfolios.get(index).addAmount(trans.getAmount());
                }
                else
                {
                    portfolios.get(index).addAmount(-1*trans.getAmount());
                }
            }
            else
            {
                if(trans.getType().equals("BUY")) {
                    portfolios.add(new Portfolio(trans.getName(), trans.getAmount()));
                }
                else
                {
                    portfolios.add(new Portfolio(trans.getName(), -1*trans.getAmount()));
                }
            }
            inside = false;
        }

        for(int f=portfolios.size()-1; f>=0; f--)
        {
            if(portfolios.get(f).getAmount()==0)
            {
                portfolios.remove(f);
            }
        }

        return portfolios;
    }

    public class AsyncThread extends AsyncTask<AsyncParams, Void, AsyncParams> {
        @Override
        protected AsyncParams doInBackground(AsyncParams... asyncParams) {
            try {
                JSONObject json;

                AsyncParams params = asyncParams[0];
                int action = params.getAction();
                String name = params.getName();
                TextView textView = params.getText();
                TextView totalView = null;
                double shares = 0;

                URL url;
                if(action==1)
                {
                    totalView = params.getTotal();
                    shares = params.getShares();
                    url = new URL("https://finnhub.io/api/v1/quote?symbol="+name+"&token=ci34231r01qmam6c1so0ci34231r01qmam6c1sog");
                }
                else if(action==2)
                {
                    url = new URL("https://finnhub.io/api/v1/search?q="+name+"&token=ci34231r01qmam6c1so0ci34231r01qmam6c1sog");
                }
                else
                {
                    String time = java.time.LocalDate.now().toString();
                    url = new URL("https://finnhub.io/api/v1/company-news?symbol="+name+"&from="+time+"&to="+time+"&token=ci34231r01qmam6c1so0ci34231r01qmam6c1sog");
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

                AsyncParams result = new AsyncParams(json, action, name, textView);
                if(action==1)
                {
                    result = new AsyncParams(json, action, name, textView, totalView, shares, params.isLast());
                }

                return result;

            } catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(AsyncParams params) {
            super.onPostExecute(params);

            try {
                JSONObject json = params.getJson();
                int action = params.getAction();
                TextView text = params.getText();
                if(action==1)
                {
                    TextView total = params.getTotal();
                    double shares = params.getShares();
                    double currPrice = json.getDouble("c");
                    text.setText("$"+String.format("%.2f",currPrice));
                    total.setText("$"+String.format("%.2f",currPrice*shares));
                    totalVal+=currPrice*shares;
                    if(params.isLast())
                    {
                        double possible = totalVal+user.getMoney();
                        valText.setText("Valuation: $"+String.format("%.2f",possible));
                        double profit = possible - 10000;
                        if(profit<0)
                        {
                            profitText.setTextColor(Color.rgb(220,20,60));
                            profitText.setText("Loss: -$"+String.format("%.2f",profit));
                        }
                        else if(profit>0)
                        {
                            profitText.setTextColor(Color.rgb(50,205,50));
                            profitText.setText("Profit: +$"+String.format("%.2f",profit));
                        }
                        else
                        {
                            profitText.setTextColor(Color.BLACK);
                            profitText.setText("No Gain: +$"+String.format("%.2f",profit));
                        }
                    }
                }
                else if(action==2)
                {
                    String fullName = json.getJSONArray("result").getJSONObject(0).getString("description");
                    text.setText(""+fullName);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class AsyncThreadNews extends AsyncTask<NewsParams, Void, NewsParams>
    {

        @Override
        protected NewsParams doInBackground(NewsParams... params) {
            try {
                JSONArray json;

                String name = params[0].getName();

                String time = java.time.LocalDate.now().toString();
                URL url = new URL("https://finnhub.io/api/v1/company-news?symbol="+name+"&from="+time+"&to="+time+"&token=ci34231r01qmam6c1so0ci34231r01qmam6c1sog");

                Log.d("url", url.toString());
                URLConnection connect = url.openConnection();
                InputStream stream = connect.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
                String text = "";
                String line = "";
                while ((line = buffer.readLine()) != null) {
                    text += line;
                }
                json = new JSONArray(text);

                NewsParams result = new NewsParams(name, json);

                return result;

            } catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(NewsParams param) {
            super.onPostExecute(param);
            try{
                JSONObject result = param.getJson().getJSONObject(0);
                String newsHeadline = result.getString("headline");
                String link = result.getString("url");
                Log.d("hi", "names: "+result.toString());
                newsText.setText(newsHeadline);
                linkText.setText(link);
            }
            catch(Exception e)
            {
                newsText.setText("Couldn't find any recent news on "+param.getName());
            }
        }
    }



//    public class CustomAdapter extends ArrayAdapter<String> {
//        List list;
//        Context context;
//        int xmlResource;
//        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
//            super(context, resource, objects);
//            xmlResource = resource;
//            list = objects;
//            this.context = context;
//        }
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            //return super.getView(position, convertView, parent);
//            //return a view that displays the data at a specific position.
//            // We are getting more specific, so were muting/deleting
//            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//            View adapterLayout = layoutInflater.inflate(xmlResource, null);
//            //root has to deal with the hierarchy of views, keep null for our purposes
//
//            TextView names = adapterLayout.findViewById(R.id.textView);
//            ImageView image = adapterLayout.findViewById(R.id.imageView);
//            Button remove = adapterLayout.findViewById(R.id.button);
//            remove.setText("REMOVE");
//            remove.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    list.remove(position);
//                    notifyDataSetChanged();
//                }
//            });
//            names.setText(list.get(position)+" "+position);
//            image.setImageResource(R.drawable.reindeer);
//            return adapterLayout;
//        }
//    }
}