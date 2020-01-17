package info.overflow_bde.storybuilder.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HTTPUtils {

    /**
     * Fetch bitmap from url
     *
     * @param src
     * @return Bitmap | null
     */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL               url        = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input    = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static void executeHttpRequest(final String sUrl, final Map<String, String> params, final SimpleCallback sc) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(sUrl);
                    HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json; utf-8");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);

                    if (!params.containsKey("ClientID"))
                        params.put("ClientID", "FjeS_tzie13-s");

                    String input = new JSONObject(params).toString();

                    try(OutputStream os = con.getOutputStream()) {
                        byte[] inputBytes = input.getBytes("utf-8");
                        os.write(inputBytes, 0, inputBytes.length);
                    }

                    if (con.getResponseCode() == 200) {
                        InputStream is = con.getInputStream();
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        int nRead;
                        byte[] data = new byte[1024];
                        while ((nRead = is.read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, nRead);
                        }

                        buffer.flush();
                        byte[] byteArray = buffer.toByteArray();

                        String text = new String(byteArray, StandardCharsets.UTF_8);
                        sc.callback(text);
                    } else {
                        Log.i("STORYBUILDER", "Error in the request: " + con.getResponseCode() + " (" + con.getContent().toString() + ")");
                    }
                } catch(Exception e) {
                    Log.d("STORYBUILDER", e.getMessage());
                }
            }
        });
    }
}
