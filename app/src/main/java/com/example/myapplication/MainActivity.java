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
import java.net.UnknownHostException;
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

                        }catch (IOException e) {
                            s[0] = "Es konnte keine Verbindung zum Server aufgebaut werden...";
                            Log.e("", "Verbindungsaufbau fehlgeschlagen");
                        }
                    }
                });
                t.start();
                try {
                    t.join(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                servertext.setText(s[0]);
            }
        });
    }

    private String sortMatNr(String matnr){
        int[] numbers = new int[matnr.length()];
        int numericValue;
        for(int i = 0; i < matnr.length(); i++){
            numericValue = Character.getNumericValue(matnr.charAt(i));
            if(numericValue > 9 || numericValue < 0){
                throw new IllegalArgumentException("Matrikelnummer darf nur aus Ziffern bestehen! Ihre Matrikelnummer: '" + matnr + "'");
            }else{
                numbers[i] = numericValue;
            }
        }
        Arrays.sort(numbers);
        return sortoutPrimeNumbers(numbers);
    }

    private String sortoutPrimeNumbers(int[] numbers){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < numbers.length; i++){
            if(!isPrime(numbers[i])){
                sb.append(numbers[i]);
            }
        }
        return sb.toString();
    }

    private boolean isPrime(int z){
        if(z < 2){return false;}
        for(int i = 2; i < z; i++){
            if(z % i == 0){
                return false;
            }
        }
        return true;
    }

}