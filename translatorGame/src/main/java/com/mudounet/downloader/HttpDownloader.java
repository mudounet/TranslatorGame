package com.mudounet.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author Crunchify.com
 */

public class HttpDownloader {

    public static void main(String[] args) throws Throwable {
        String link = "https://raw.githubusercontent.com/Crunchify/All-in-One-Webmaster/master/all-in-one-webmaster-premium.php";
        URL myUrl = new URL(link);
        HttpURLConnection myHttp = (HttpURLConnection) myUrl.openConnection();
        Map<String, List<String>> myHeader = myHttp.getHeaderFields();

        // If URL is getting 301 and 302 redirection HTTP code then get new URL link.
        // This below for loop is totally optional if you are sure that your URL is not getting redirected to anywhere
        for (String header : myHeader.get(null)) {
            if (header.contains(" 302 ") || header.contains(" 301 ")) {
                link = myHeader.get("Location").get(0);
                myUrl = new URL(link);
                myHttp = (HttpURLConnection) myUrl.openConnection();
                myHeader = myHttp.getHeaderFields();
            }
        }
        InputStream myStream = myHttp.getInputStream();
        String myResponse = getStringFromStream(myStream);
        System.out.println(myResponse);
    }

    // ConvertStreamToString() Utility - we name it as getStringFromStream()
    private static String getStringFromStream(InputStream myStream) throws IOException {
        if (myStream != null) {
            Writer myWriter = new StringWriter();

            char[] myBuffer = new char[2048];
            try {
                Reader myReader = new BufferedReader(new InputStreamReader(myStream, "UTF-8"));
                int counter;
                while ((counter = myReader.read(myBuffer)) != -1) {
                    myWriter.write(myBuffer, 0, counter);
                }
            } finally {
                myStream.close();
            }
            return myWriter.toString();
        } else {
            return "No Contents";
        }
    }
}