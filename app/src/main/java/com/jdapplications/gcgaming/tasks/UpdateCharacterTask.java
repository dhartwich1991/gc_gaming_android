package com.jdapplications.gcgaming.tasks;

import android.os.AsyncTask;

import com.jdapplications.gcgaming.listener.OnAsyncResultListener;
import com.jdapplications.gcgaming.models.Character;

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
public class UpdateCharacterTask extends AsyncTask<Void, String, String> {
    private OnAsyncResultListener listener;
    private Character myUpdateChar;
    Exception error = null;


    public UpdateCharacterTask(OnAsyncResultListener listener, Character character) {
        this.listener = listener;
        this.myUpdateChar = character;
    }

    @Override
    protected String doInBackground(Void... params) {
        String responseString = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            HttpPost postRequest = new HttpPost("http://api.dotards.net:3000/api/v1/characters/update");
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("id", String.valueOf(myUpdateChar.id)));
            nvps.add(new BasicNameValuePair("lastModified", String.valueOf(myUpdateChar.lastModified)));
            nvps.add(new BasicNameValuePair("name", myUpdateChar.name));
            nvps.add(new BasicNameValuePair("realm", myUpdateChar.realm));
            nvps.add(new BasicNameValuePair("battlegroup", myUpdateChar.battlegroup));
            nvps.add(new BasicNameValuePair("class", String.valueOf(myUpdateChar.charClass)));
            nvps.add(new BasicNameValuePair("race", String.valueOf(myUpdateChar.race)));
            nvps.add(new BasicNameValuePair("gender", String.valueOf(myUpdateChar.gender)));
            nvps.add(new BasicNameValuePair("level", String.valueOf(myUpdateChar.level)));
            nvps.add(new BasicNameValuePair("achievementPoints", String.valueOf(myUpdateChar.achievementPoints)));
            nvps.add(new BasicNameValuePair("thumbnailurl", myUpdateChar.thumbNailUrl));
            nvps.add(new BasicNameValuePair("itemleveltotal", String.valueOf(myUpdateChar.itemLevelTotal)));
            nvps.add(new BasicNameValuePair("itemlevelequipped", String.valueOf(myUpdateChar.itemLevelEquipped)));
            nvps.add(new BasicNameValuePair("userid", String.valueOf(myUpdateChar.userid)));
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
