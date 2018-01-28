package vitalinstinct.homecontrol;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Reece on 24/09/2017, vitalinstinct.housecontrolepd, HouseControlepd
 */

public class JSONSend extends AsyncTask<String, Void, String> {

    JSONCreate jsonObject = new JSONCreate();

    public void sendData(JSONObject data, String ipAddress) throws IOException {
        //server contact and send
        String serverName = ipAddress;
        int port = Integer.parseInt("6066");
        Socket client = new Socket(serverName, port);
        OutputStream outToServer = null;
        DataOutputStream out = null;
        BufferedReader in = null;
        boolean retry = true;
        while (retry == true) {
            outToServer = client.getOutputStream();
            out = new DataOutputStream(outToServer);
            out.writeUTF(data.toString());
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line = null;
            String response = "";
            while ((line = in.readLine()) != null) {
                response = response + line;
            }
            if (response.equals("received"))
            {
                retry = false;
            }
        }
        in.close();
        outToServer.close();
        out.close();
    }

    @Override
    protected String doInBackground(String[] params) {
        try {
            sendData(jsonObject.createData(params), params[1]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}