package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {
	/*
	 * �����ʹ�����������ص�ʡ������
	 * */
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB
			,String response){
		
//		LogUtil.e("handleProvinceResponse����....ʡ....���ݲ���ͷ�ж�response�Ƿ�Ϊ��:"+response);
		
		if(!TextUtils.isEmpty(response)){
			
			String[] allProvinces = response.split(",");
			if(allProvinces!=null && allProvinces.length>0){
				for(String p : allProvinces){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					
//					LogUtil.e("handleProvinceResponse���������ݵ���saveProvince��������");
					
					//���������������ݴ洢��province��
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
				return false;
		
	}
	/*
	 * �����ʹ�����������ص��м�����
	 * */
	public static boolean handCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response,int provinceId){
		
		//Log.e("tag", "city������..........:"+response);
		
		if(!TextUtils.isEmpty(response)){
			
			//Log.e("tag", "TextUtils.isEmpty:"+!TextUtils.isEmpty(response));
			
			String [] allCities = response.split(",");
			if(allCities !=null && allCities.length>0){
				for(String c: allCities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					
//					Log.e("tag","getcityName: "+city.getCityName());
					
					coolWeatherDB.saveCity(city);
					//break; ��֤������savageCityֻ������һ��
				}
				return true;
			}
		}
				return false;
		
	}
  /*
   * �����ʹ�����������ص��ؼ�����
   * */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
			String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties!=null && allCounties.length>0){
				for(String c : allCounties){
					String []array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	/*
	 * �����������Żص�JSON���ݣ������������������ݴ洢������
	 * */
	public static void handleWeatherResponse(Context context,String response){
		try{
			
//			LogUtil.e("handleWeatherResponse JSON");
			
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			
//			LogUtil.e("publishTime: "+publishTime);
			
			savaWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
			
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	/*
	 * �����������ص����������洢��SharaPreferences�ļ���
	 * */
	private static void savaWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
		
	}
/*	 SimpleDateFormat myFmt=new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��");
     SimpleDateFormat myFmt1=new SimpleDateFormat("yy/MM/dd HH:mm"); 
     SimpleDateFormat myFmt2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�ȼ���now.toLocaleString()
     SimpleDateFormat myFmt3=new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss�� E ");
     SimpleDateFormat myFmt4=new SimpleDateFormat(
             "һ���еĵ� D �� һ���е�w������ һ���е�W������ ��һ����kʱ zʱ��");
     Date now=new Date();
     System.out.println(myFmt.format(now));
     System.out.println(myFmt1.format(now));
     System.out.println(myFmt2.format(now));
     System.out.println(myFmt3.format(now));
     System.out.println(myFmt4.format(now));
     System.out.println(now.toGMTString());
     System.out.println(now.toLocaleString());
     System.out.println(now.toString());

	
	Ч����
	2004��12��16�� 17ʱ24��27��
	04/12/16 17:24
	2004-12-16 17:24:27
	2004��12��16�� 17ʱ24��27�� ������ 
	һ���еĵ� 351 �� һ���е�51������ һ���е�3������ ��һ����17ʱ CSTʱ��
	16 Dec 2004 09:24:27 GMT
	2004-12-16 17:24:27
	Thu Dec 16 17:24:27 CST 2004*/
}
