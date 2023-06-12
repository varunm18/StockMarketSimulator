package com.example.finalproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {
    TextView balanceText, dateText;
    EditText usernameEdit, passEdit;
    Button back, change, change2;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        balanceText = findViewById(R.id.balanceText);
        dateText = findViewById(R.id.dateText);
        usernameEdit = findViewById(R.id.editTextUsername);
        passEdit = findViewById(R.id.editTextPassword);
        back = findViewById(R.id.backBtn);
        change = findViewById(R.id.change);
        change2 = findViewById(R.id.change2);

        user = (User) getIntent().getSerializableExtra("user");
        double money = user.getMoney();
        balanceText.setText("Balance: $"+String.format("%.2f",money));
        String date = user.getDate();
        dateText.setText("Account Created On: "+date);

        usernameEdit.setHint("Username: "+user.getUsername());
        passEdit.setHint("Password: "+user.getPassword());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, Stocks.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usernameEdit.length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Error: Enter Username", Toast.LENGTH_SHORT).show();
                }
                else if(usernameEdit.getText().toString().equals(user.getUsername()))
                {
                    Toast.makeText(getApplicationContext(),"Error: Enter New Username", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    changeName(usernameEdit.getText().toString());
                }
            }
        });

        change2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passEdit.length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Error: Enter Password", Toast.LENGTH_SHORT).show();
                }
                else if(passEdit.getText().toString().equals(user.getPassword()))
                {
                    Toast.makeText(getApplicationContext(),"Error: Enter New Password", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    changePass(passEdit.getText().toString());
                }
            }
        });

    }

    public void changeName(String name){
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
                    reference.child(user.getUsername()).removeValue();
                    user.setUsername(name);
                    reference.child(name).setValue(user);
                    usernameEdit.setHint("Username: "+user.getUsername());
                    Toast.makeText(getApplicationContext(),"Success! Info Changed. NEW Username: "+name, Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void changePass(String pass){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        user.setPassword(pass);

        reference.child(user.getUsername()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                passEdit.setHint("Password: "+user.getPassword());
                Toast.makeText(getApplicationContext(),"Success! Info Changed. NEW Password: "+pass, Toast.LENGTH_LONG).show();
            }
        });
    }
}