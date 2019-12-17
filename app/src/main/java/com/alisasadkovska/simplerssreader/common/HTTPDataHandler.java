package com.alisasadkovska.simplerssreader.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPDataHandler {
    private static String stream = null;

    public HTTPDataHandler(){
    }

    public String GetHTTPData(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                    stringBuilder.append(line);
                stream = stringBuilder.toString();
                urlConnection.disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }
}
