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
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText matrikelnummer = (EditText) findViewById(R.id.matrikelnummer);
        Button abschicken = (Button) findViewById(R.id.abschicken);
        TextView servertext = (TextView) findViewById(R.id.servertext);
        TextView sorted = (TextView) findViewById(R.id.sorted);
        Button sortieren = (Button) findViewById(R.id.sort);

        sortieren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String matnr = matrikelnummer.getText().toString();
                sorted.setText(sortMatNr(matnr));
            }
        });

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

    private String sortMatNr(String matnr){
        Log.d("", "Sortiere Matrikelnummer: "+matnr);
        char[] c = matnr.toCharArray();
        Arrays.sort(c);
        return sortout(c);
    }

    private String sortout(char[] c){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < c.length; i++){
            if(!isPrime(c[i])){
                sb.append(c[i]);
            }
        }
        return sb.toString();
    }

    private boolean isPrime(char c){
        int z = Character.getNumericValue(c);
        if(z < 2){return false;}
        for(int i = 2; i < z; i++){
            if(z % i == 0){
                return false;
            }
        }
        return true;
    }

}