package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.LogUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class CoolWeatherDB {

	/*
	 * ���ݿ���
	 */
	public static final String DB_NAME = "cool_weather";
	/*
	 * ���ݿ�汾
	 */
	public static final int VERSION =1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	/*
	 * ���ṹ����˽�л�
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME,null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	/*
	 * ��ȡCoolWeatherDBʵ��
	 */
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB == null){
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	/*
	 * ��provinceʵ���洢�����ݿ�
	 * */
	public void saveProvince(Province province){
		
//		LogUtil.e("�жϴ���.....ʡ......�����Ƿ�Ϊ�ղ�Ϊ�ս���savaProvince����");
		
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
			
		}
	}
	/*
	 * �����ݿ��ȡȫ�����е�ʡ����Ϣ
	 * */
	public List<Province> loadProvinces(){
		
		List<Province> list = new ArrayList<Province>();
		
		/*cursor�����ָ����province_id��ǰ��֮�����ѭ���������province_id����������
		"province_id=?",new String[]{String.valueOf(provinceId)},
		����д����Ԫ������Ϊ�˷�ֹ���˵Ĺ�������"province_id=?",λ�ÿ���ֱ��д
		������������������ȫ*/
		
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null, null);
		

		if(cursor.moveToFirst()){
			do{
				
//				LogUtil.e("�ڹ�����Դ˶�ȡloadProvinces....name: " +cursor.getString(cursor
//						.getColumnIndex("province_name")));
				
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
			if(cursor!=null){
				cursor.close();
			}
		}
		return list;
	}
	/*
	 * ��cityʵ�����洢�����ݿ�:������д�뵽���ݿ�
	 * */
	
	public void saveCity(City city){
		
//		LogUtil.e("�жϴ���.....��......�����Ƿ�Ϊ�ղ�Ϊ�ս���savacity����");
		
		if(city!=null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id",city.getProvinceId());
			
			
			db.insert("City", null, values);

		}
	}
	/*
	 * �����ݿ��ж�ȡĳʡ�µ����г�����Ϣ
	 * */
	public List<City> loadCities(int provinceId){
		List<City> list = new ArrayList<City>();
	
		//Log.e("tag","i see working");
		
		Cursor cursor = db.query("City",null,"province_id=?",
				new String[]{String.valueOf(provinceId)},null,null,null);
		if(cursor.moveToFirst()){
			do{
				
//				LogUtil.e( "loadCities....name: " +cursor.getString(cursor
//				.getColumnIndex("city_name")));
				
				
				City city = new City();
				city.setId(cursor.getInt(cursor
						.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
		
	}
	/*
	 * ��countyʵ�����洢�����ݿ⣺д���ݵ����ݿ���
	 * */
	public void saveCounty(County county){
		
//		LogUtil.e("�жϴ���.....��......�����Ƿ�Ϊ�ղ�Ϊ�ս���savacounty����");
		
		if(county!=null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code",county.getCountyCode());
			values.put("city_id",county.getCityId());
			db.insert("County", null, values);
		}
	}
	/*
	 * �����ݿ��ȡĳ�����µ������ص���Ϣ
	 * */
	public List<County> loadCounty(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query( "County", null,"city_id=?", new String[]{String.valueOf(cityId)}, 
				 null,null,null);
		if(cursor.moveToFirst()){
			do{
				
//				LogUtil.e("�ڹ�����Դ˶�ȡloadcounty....name: " +cursor.getString(cursor
//						.getColumnIndex("county_name")));
				
				County county = new County();
				county.setId(cursor.getInt(cursor
						.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			}while(cursor.moveToNext());
			
			if(cursor !=null){
				cursor.close();
			}
		}
		return list;
		
	}
}
