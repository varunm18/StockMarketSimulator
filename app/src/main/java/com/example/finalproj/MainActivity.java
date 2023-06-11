package com.example.finalproj;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText usernameEdit, passwordEdit;
    Button login, register;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEdit = findViewById(R.id.userName);
        passwordEdit = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();

                login(username, password);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();

                register(username, password);
            }
        });
    }

    public void login(String name, String pass) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("username").equalTo(name);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String password = snapshot.child(name).child("password").getValue(String.class);
                    if (password.equals(pass)) {
                        int money = snapshot.child(name).child("money").getValue(int.class);
                        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
                        for (DataSnapshot snap : snapshot.child(name).child("transactions").getChildren()) {

                            double amount = snap.child("amount").getValue(double.class);
                            String stockName = snap.child("name").getValue(String.class);
                            double price = snap.child("price").getValue(double.class);
                            String time = snap.child("time").getValue(String.class);
                            String type = snap.child("type").getValue(String.class);

                            transactions.add(new Transaction(stockName, time, type, price, amount));
                        }
                        Log.d("hi", "trans: "+transactions.toString());
                        User user = new User(name, pass, money, transactions);

                        Intent intent = new Intent(MainActivity.this, Stocks.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Password is Incorrect.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Username is Incorrect.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void register(String name, String pass){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("username").equalTo(name);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Toast.makeText(getApplicationContext(),"Username already taken. Retype username.", Toast.LENGTH_SHORT).show();
                }
                else if(name.contains(".")||name.contains("$")||name.contains("[")||name.contains("]")||name.contains(" ")||name.contains("/"))
                {
                    Toast.makeText(getApplicationContext(),"Invalid Characters. Retype username.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Transaction test = new Transaction("init", "init", "init", 0, 0);
                    ArrayList<Transaction> testList = new ArrayList<Transaction>();
                    testList.add(test);
                    User user = new User(name, pass, 10000, testList);
                    reference.child(name).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Intent intent = new Intent(MainActivity.this, Stocks.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                            }
                        }
                    });
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}