package geert.berkers.primedice;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Geert on 28-1-2016.
 */
public class TipDeveloperTask extends AsyncTask<String, Void, String>{

    HttpURLConnection connection;

    @Override
    protected String doInBackground(String... params) {
        return placeBetUpdateUser(params[0], params[1], params[2]);
    }

    public String placeBetUpdateUser(String tipURL, String username, String amount) {
        String tipResult = null;

        try {
            URL url = new URL(tipURL);

            String urlParameters =
                        "username=" + URLEncoder.encode(username, "UTF-8") +
                            "&amount=" + URLEncoder.encode(amount, "UTF-8");

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.connect();

            // Send request to site
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(urlParameters);
            dataOutputStream.flush();
            dataOutputStream.close();

            // Get Response from site
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            bufferedReader.close();

            tipResult = response.toString();
            Log.i("response", tipResult);

        } catch (Exception ex) {

            Log.e("Exception", ex.toString());
            ex.printStackTrace();

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
        return tipResult;
    }
}
