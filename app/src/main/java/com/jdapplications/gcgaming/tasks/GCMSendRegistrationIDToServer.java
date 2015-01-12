package com.jdapplications.gcgaming.tasks;

import android.os.AsyncTask;

import com.jdapplications.gcgaming.listener.OnAsyncResultListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielhartwich on 1/9/15.
 */
public class GCMSendRegistrationIDToServer extends AsyncTask<String, String, String> {
    private OnAsyncResultListener listener;
    Exception error = null;

    public GCMSendRegistrationIDToServer(OnAsyncResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String responseString = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            HttpPost postRequest = new HttpPost("http://api.dotards.net:3000/api/v1/users/save_gcm_regid");
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("id", params[0]));
            nvps.add(new BasicNameValuePair("gcm_id", params[1]));
            postRequest.setEntity(new UrlEncodedFormEntity(nvps));

            httpResponse = httpClient.execute(postRequest);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else {
                //Closes the connection.
                httpResponse.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
            error = e1;
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            error = e1;
        } catch (IOException e1) {
            e1.printStackTrace();
            error = e1;
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        if (error == null) {
            super.onPostExecute(s);
            listener.onResult(s);
        } else {
            listener.onError(error);
        }
    }
}
