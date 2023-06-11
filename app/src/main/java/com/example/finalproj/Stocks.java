package com.example.finalproj;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class Stocks extends AppCompatActivity {

    EditText stockName;
    Button getDetails;
    TextView moneyText;
    public static String CODE = "CODE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);

        stockName = findViewById(R.id.id_stockName);
        getDetails = findViewById(R.id.id_getDetails);
        moneyText = findViewById(R.id.textView4);

        User user = (User) getIntent().getSerializableExtra("user");
        moneyText.setText("Balance: $"+String.format("%.2f",user.getMoney()));
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
    }
}