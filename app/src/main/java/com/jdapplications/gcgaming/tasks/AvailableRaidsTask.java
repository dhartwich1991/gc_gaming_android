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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielhartwich on 12/19/14.
 */
public class AvailableRaidsTask extends AsyncTask<String, String, String> {
    private OnAsyncResultListener listener;

    public AvailableRaidsTask(OnAsyncResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String responseString = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            HttpGet getRequest = new HttpGet("http://api.dotards.net:3000/api/v1/raids");

            httpResponse = httpClient.execute(getRequest);
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
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.onResult(s);
    }
}
