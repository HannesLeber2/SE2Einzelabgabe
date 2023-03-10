package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText matrikelnummer = (EditText) findViewById(R.id.matrikelnummer);
        Button abschicken = (Button) findViewById(R.id.abschicken);
        TextView servertext = (TextView) findViewById(R.id.servertext);

        abschicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String matrikelnr = matrikelnummer.getText().toString();
                //has to be final, so that the Thread can access it
                final String[] s = new String[1];
                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket("se2-isys.aau.at", 53212);
                            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
                            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            Log.d("", "Sende an Server: " + matrikelnr);
                            outToServer.writeBytes(matrikelnr+"\n");
                            String serverAntwort = inFromServer.readLine();
                            Log.d("", "Serverantwort: "+ serverAntwort);
                            socket.close();

                            s[0] = serverAntwort;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();


                try {
                    t.join(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                servertext.setText(s[0]);
            }
        });

    }







}