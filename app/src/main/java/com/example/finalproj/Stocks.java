package com.example.finalproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class Stocks extends AppCompatActivity {

    EditText stockName;
    Button getDetails;
    public static String CODE = "CODE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);

        stockName = findViewById(R.id.id_stockName);
        getDetails = findViewById(R.id.id_getDetails);

        getDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stocks = stockName.getText().toString();
                Intent intent = new Intent(Stocks.this, StockView.class);
                intent.putExtra(CODE,stocks);
                startActivity(intent);
                Log.d("pOOP","HI");
            }
        });
    }

    public static void executeStmt(Connection conn, String stmt) {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(stmt);
            while (rs.next())
            {
                System.out.println(rs.getString(1));
            }
            rs.close();
            st.close();
        }
        catch(Exception e)
        {
            return;
        }
    }
}