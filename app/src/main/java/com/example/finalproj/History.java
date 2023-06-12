package com.example.finalproj;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class History extends AppCompatActivity {
    Button back;
    User user;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        back = findViewById(R.id.backButton);
        lv = findViewById(R.id.listView);

        user = (User) getIntent().getSerializableExtra("user");

        ArrayList<Transaction> newTrans = (ArrayList<Transaction>) user.getTransactions().clone();
        newTrans.remove(0);

        CustomAdapter2 adapter2 = new CustomAdapter2(this, R.layout.adapter_history_layout, newTrans);
        lv.setAdapter(adapter2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(History.this, Stocks.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    public class CustomAdapter2 extends ArrayAdapter<Transaction> {
        ArrayList<Transaction> list;
        Context context;
        int xmlResource;
        public CustomAdapter2(Context context, int resource, ArrayList<Transaction> objects) {
            super(context, resource, objects);
            xmlResource = resource;
            list = objects;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            return super.getView(position, convertView, parent);
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterLayout = layoutInflater.inflate(xmlResource, null);

            TextView stockText = adapterLayout.findViewById(R.id.stockid);
            TextView sharesText = adapterLayout.findViewById(R.id.sharesid);
            TextView priceText = adapterLayout.findViewById(R.id.priceid);
            TextView timeText = adapterLayout.findViewById(R.id.timeid);
            TextView typeText = adapterLayout.findViewById(R.id.typeid);

            Transaction trans = list.get(position);

            stockText.setText(trans.getName());
            sharesText.setText(""+trans.getAmount());
            priceText.setText("$"+String.format("%.2f",trans.getPrice()));
            timeText.setText(trans.getTime());
            typeText.setText(trans.getType());

            return adapterLayout;
        }
    }
}