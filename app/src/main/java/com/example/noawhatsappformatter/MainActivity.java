package com.example.noawhatsappformatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    protected String space = "\u200B";     //space
    protected String zero = "\u200C";     //0
    protected String one = "\u200D";      //1
    String bold = "*";
    String italic = "_";
    String strike = "~";
    String mono = "```";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button boldBtn = (Button) findViewById(R.id.boldBtn);
        Button italicBtn = (Button) findViewById(R.id.italicBtn);
        Button monoBtn = (Button) findViewById(R.id.monoBtn);
        Button strikeBtn = (Button) findViewById(R.id.strikeBtn);
        Button copyBtn = (Button) findViewById(R.id.copyBtn);
        EditText userText = (EditText) findViewById(R.id.insertText);
        TextView formattedText = (TextView) findViewById(R.id.formatedTxt);
        checkPermission();

        boldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFormattetText(userText.getText().toString(), bold);
                copyBtn.setEnabled(true);
            }
        });
        italicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFormattetText(userText.getText().toString(), italic);
                copyBtn.setEnabled(true);
            }
        });
        monoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFormattetText(userText.getText().toString(), mono);
                copyBtn.setEnabled(true);
            }
        });
        strikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFormattetText(userText.getText().toString(), strike);
                copyBtn.setEnabled(true);
            }
        });
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = formattedText.getText().toString();
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String ipAddress = getDeviceIpAddress();
                String binaryIpAddress = stringToBinary(ipAddress);
                String stangIpAddress = binaryToSteganography(binaryIpAddress);
                //Debug
                String binaryAfter = steganographyToBinary(stangIpAddress);
                String ipAfter = binaryToString(binaryAfter);
                Log.d("debug", "onClick: ipAddress: " + ipAddress);
                Log.d("debug", "onClick: binaryIpAddress: " + binaryIpAddress);
                Log.d("debug", "onClick: stangIpAddress: " + stangIpAddress);
                //Debug
                Log.d("debug", "onClick: binaryAfter: " + binaryAfter);
                Log.d("debug", "onClick: ipAfter: " + ipAfter);

                ClipData clipData = ClipData.newPlainText("text", stangIpAddress + message);
                manager.setPrimaryClip(clipData);
            }
        });
    }

    public void setFormattetText(String message, String sign) {
        TextView formattedText = (TextView) findViewById(R.id.formatedTxt);
        String text = sign + message + sign;
        Log.d("debug", text);
        formattedText.setText("");
        formattedText.setText(text);
    }

    public String stringToBinary(String text) {
        String bString = "";
        String temp = "";
        for (int i = 0; i < text.length(); i++) {
            temp = Integer.toBinaryString(text.charAt(i));
            for (int j = temp.length(); j < 8; j++) {
                temp = "0" + temp;
            }
            bString += temp + " ";
        }

        Log.d("Debug", bString);
        return bString;
    }

    public String binaryToString(@NonNull String binaryCode) {
        String[] code = binaryCode.split(" ");
        String word = "";
        for (int i = 0; i < code.length; i++) {
            word += (char) Integer.parseInt(code[i], 2);
        }
        Log.d("TAG", word);
        return word;
    }

    public String binaryToSteganography(@NonNull String binaryCode) {
        String stnag = "";
        stnag = binaryCode.replaceAll("[0]", zero);
        stnag = stnag.replaceAll("[1]", one);
        stnag = stnag.replaceAll(" ", space);
        return stnag;
    }

    public String steganographyToBinary(@NonNull String steganographyCode) {
        String binary = "";
        binary = steganographyCode.replaceAll("((\\''')*\\**~*_*[a-zA-Z]+\\**~*_*(\\''')*)", "");
        binary = binary.replaceAll(zero, "0");
        binary = binary.replaceAll(one, "1");
        binary = binary.replaceAll(space, " ");
        return binary;
    }


    @NonNull
    private String getDeviceIpAddress() {
        String actualConnectedToNetwork = null;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected()) {
                actualConnectedToNetwork = getWifiIp();
            }
        }
        if (TextUtils.isEmpty(actualConnectedToNetwork)) {
            actualConnectedToNetwork = getNetworkInterfaceIpAddress();
        }
        if (TextUtils.isEmpty(actualConnectedToNetwork)) {
            actualConnectedToNetwork = "127.0.0.1";
        }
        return actualConnectedToNetwork;
    }

    @Nullable
    private String getWifiIp() {
        final WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
            int ip = mWifiManager.getConnectionInfo().getIpAddress();
            return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                    + ((ip >> 24) & 0xFF);
        }
        return null;
    }


    @Nullable
    public String getNetworkInterfaceIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String host = inetAddress.getHostAddress();
                        if (!TextUtils.isEmpty(host)) {
                            return host;
                        }
                    }
                }

            }
        } catch (Exception ex) {
            Log.e("IP Address", "getLocalIpAddress", ex);
        }
        return null;
    }
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 3);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 3);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 3);
        }
    }
}