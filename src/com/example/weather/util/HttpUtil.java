package com.example.weather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	public static void sendRequestWithClient(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable(){
			
			@Override
			public void run(){
				HttpURLConnection connection = null;
				try{
					URL url = new URL(address);
					connection = (HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setReadTimeout(8000);
					connection.setConnectTimeout(8000);
					
					InputStream in = connection.getInputStream();
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					if(listener != null){
						listener.onFinish(response.toString());
						
					}
				}catch(Exception e){
						if(listener != null){
							listener.onError(e);
						}
						
				}finally{
						if(connection != null){
							connection.disconnect();
						}
				}
					
				
			}
		}).start();
	}
	
//	 public static InputStream httpMethod(String path, String encode)
//	    {
//	        HttpClient httpClient = new DefaultHttpClient();
//	        
//	        try
//	        {
//	            HttpPost httpPost = new HttpPost(path);
//	            HttpResponse httpResponse = httpClient.execute(httpPost);
//	            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
//	            {
//	                HttpEntity httpEntity = httpResponse.getEntity();
//	                return httpEntity.getContent();
//	            }
//	        }
//	        catch (Exception e)
//	        {
//	            e.printStackTrace();
//	        }
//	        finally
//	        {
//	            httpClient.getConnectionManager().shutdown();
//	        }
//	        
//	        return null;
//	    }

}

