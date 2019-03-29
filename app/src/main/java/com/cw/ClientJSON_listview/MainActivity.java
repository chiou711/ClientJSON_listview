/*
 * Copyright (C) 2019 CW Chiu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cw.ClientJSON_listview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cw.ClientJSON_listview.uil.UilCommon;
import com.google.android.youtube.player.YouTubeIntents;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
	private ListView list;
	int [] Id;
	String [] Uri;
	String [] Title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("MainActivity / _onCreate");

		MyTask task = new MyTask();
		task.execute();

		list = findViewById(R.id.list_data);

		UilCommon.init();
	}

	private class MyTask extends AsyncTask<Void,Void,Void>{
		@Override
		protected Void doInBackground(Void... voids) {
			System.out.println("MainActivity / MyTask /_doInBackground");

			String strResult = "";

			// HTTPS POST
			String project = "LiteNote";
			String urlStr =  "https://" + project + ".ddns.net:8443/"+ project +"Web/viewNote/viewNote_json.jsp";
			//refer https://stackoverflow.com/questions/16504527/how-to-do-an-https-post-from-android
			try {
				URL url = new URL(urlStr);
				trustEveryone();
				HttpsURLConnection urlConnection = ((HttpsURLConnection)url.openConnection());

				// set Timeout and method
				urlConnection.setReadTimeout(7000);
				urlConnection.setConnectTimeout(7000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);

				// Add any data you wish to post here
				urlConnection.connect();
				InputStream in = urlConnection.getInputStream();

				if(in != null) {
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String inputLine;

					while ((inputLine = br.readLine()) != null) {
						System.out.println("MainActivity / inputLine = " + inputLine);
						strResult += inputLine;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// HTTP POST
//	        String url = "http://10.1.1.3:8080/LiteNoteWeb/viewNote/viewNote_json.jsp";
//			try {
//				HttpClient client = new DefaultHttpClient();
//				HttpPost post = new HttpPost(url);
//
//				HttpResponse response = client.execute(post);
//
//				strResult = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
//				System.out.println("strResult = " + strResult);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

			System.out.println("MainActivity / result final = " + strResult);


			// JSON array
			try {
				JSONArray jsonArray = new JSONArray(strResult);

				//Set Array size
				Id = new int[jsonArray.length()];
				Uri = new String[jsonArray.length()];
				Title = new String[jsonArray.length()];

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					Id[i] = jsonObject.getInt("note_id");
					Uri[i] = jsonObject.getString("note_link_uri");
					Title[i] = jsonObject.getString("note_title");
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return null;
		}
		@Override
		protected void onPostExecute(Void Result){
			MyAdapter adapter = new MyAdapter(MainActivity.this);
			list.setAdapter(adapter);

			list.setItemsCanFocus(true);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String idStr = getYoutubeId(Uri[position]);
					Intent intent = YouTubeIntents.createPlayVideoIntentWithOptions(MainActivity.this, idStr, false/*fullscreen*/, true/*finishOnEnd*/);
					startActivity(intent);
				}
			});

			super.onPostExecute(Result);
		}
	}

	private void trustEveryone() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}});

			SSLContext context = SSLContext.getInstance("TLS");

			context.init(null, new X509TrustManager[]
							{
								new X509TrustManager()
								{
									public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
									{}

									public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException
									{}

									public X509Certificate[] getAcceptedIssuers() {
										return new X509Certificate[0];
									}
								}
							},
					new SecureRandom()
			);

			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}
	}


	public class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		public MyAdapter(Context c){
			inflater = LayoutInflater.from(c);
		}
		@Override
		public int getCount() {
			if(Id == null)
				return 0;
			else
				return Id.length;
		}

		@Override
		public Object getItem(int i) {
			return Id[i];
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(final int i, View view, ViewGroup viewGroup) {
			view = inflater.inflate(R.layout.layout_adapter,null);
			TextView aName,aAddress,aTitle;
			aName = (TextView) view.findViewById(R.id.text_id);
			aAddress = (TextView) view.findViewById(R.id.text_uri);
			aTitle = (TextView) view.findViewById(R.id.text_title);
			ImageView thumbPicture = (ImageView) view.findViewById(R.id.thumb_picture);
			ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.thumb_progress);
			String pictureUri = "http://img.youtube.com/vi/"+getYoutubeId(Uri[i])+"/0.jpg";
			//System.out.println("pictureUri = " + pictureUri);

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String idStr = getYoutubeId(Uri[i]);
					Intent intent = YouTubeIntents.createPlayVideoIntentWithOptions(MainActivity.this, idStr, false/*fullscreen*/, true/*finishOnEnd*/);
					startActivity(intent);
				}
			});

			view.setFocusable(true);
			view.setClickable(true);


			aName.setText(String.valueOf(Id[i]));
			aAddress.setText(Uri[i]);
			aTitle.setText(Title[i]);


			// load bitmap to image view
			try
			{
				new UtilImage_bitmapLoader(thumbPicture,
						pictureUri,
						progressBar,
						UilCommon.optionsForFadeIn,
						MainActivity.this);
			}
			catch(Exception e)
			{
				Log.e("PageAdapter_recycler", "UtilImage_bitmapLoader error");
			}

			return view;
		}
	}


	// Get YouTube Id
	public static String getYoutubeId(String url) {

		String videoId = "";

		if (url != null && url.trim().length() > 0 && url.startsWith("http")) {
			String expression = "^.*((youtu.be\\/)|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??(v=)?([^#\\&\\?]*).*";
			CharSequence input = url;
			Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(input);
			if (matcher.matches()) {
				String groupIndex1 = matcher.group(8);
				if (groupIndex1 != null && groupIndex1.length() == 11)
					videoId = groupIndex1;
			}
		}
		return videoId;
	}

}
