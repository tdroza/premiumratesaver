package net.droza.android.premiumratesaver.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class LongRunningGetIO extends AsyncTask<Void, Void, String> {
	JSONListener jsonListener;
	private String query = null;
	private static final String LOGTAG = LongRunningGetIO.class.toString();
	
	public LongRunningGetIO(String q, JSONListener listener) {
		this.query = q;
		this.jsonListener = listener;
	}
	protected String getASCIIContentFromEntity(HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = in.read(b);
			if (n > 0)
				out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	@Override
	protected String doInBackground(Void... params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		Log.v(LOGTAG, "query: " + query);
		HttpGet httpGet = new HttpGet(
				"http://0870.me/get/json/" + query);
		String text = null;
		try {
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			text = getASCIIContentFromEntity(entity);
		} catch (Exception e) {
			return e.getLocalizedMessage();
		}
		return text;
	}

	protected void onPostExecute(String results) {
		if (results != null) {
			JSONObject json = null;
			try {
				json = new JSONObject(results);
			} catch (JSONException e) {
				Log.e(LOGTAG, e.getMessage());
			}
			jsonListener.onRemoteCallComplete(json);
		}
	}
	
	public interface JSONListener {
		public void onRemoteCallComplete(JSONObject json);		
	}		
}