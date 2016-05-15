package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.coolweather.app.util.HttpCallbackListener;

public class HttpUtil {

	public static  void sendHttpRequest(final String address,
			final HttpCallbackListener listener){
		new Thread(new Runnable(){
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try{
					
//					LogUtil.e("sendHttpRequest...try");
					
					URL url = new URL(address);
					connection = (HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
//					LogUtil.e("5");
					InputStream in = connection.getInputStream();
//					LogUtil.e("6");
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();

					String line;
					while((line = reader.readLine())!=null){
						response.append(line);
					}
					
//					LogUtil.e("listener是否为空："+(listener==null));
					
					if(listener!=null){
						
//						LogUtil.e("listener不为空调用onFinish");
						
						listener.onFinish(response.toString());
					}
				}catch(Exception e){
					if(listener !=null){
						listener.onError(e);
					}
				}finally{
					if(connection !=null){
						connection.disconnect();
					}
				}
				
			}
		}).start();
	}
}
