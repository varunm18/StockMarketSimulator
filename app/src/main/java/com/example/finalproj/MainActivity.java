package com.example.finalproj;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.id_userName);
        password = findViewById(R.id.id_password);
        login = findViewById(R.id.id_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //username.getText().toString().equals("hi") && password.getText().toString().equals("hello")
                Intent intent = new Intent(MainActivity.this, Stocks.class);
                startActivity(intent);
            }
        });


        /*double x,y;
        x=-5.0;
        series = new LineGraphSeries<DataPoint>();
        for(int i = 0; i<500; i++){

            x+=0.1;
            y = Math.sin(x);
            series.appendData(new DataPoint(x,y), true, 500);

        }
        graph.addSeries(series);

         */
    }
}