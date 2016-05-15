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
	 * 数据库名
	 */
	public static final String DB_NAME = "cool_weather";
	/*
	 * 数据库版本
	 */
	public static final int VERSION =1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	/*
	 * 将结构方法私有化
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME,null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	/*
	 * 获取CoolWeatherDB实例
	 */
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB == null){
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	/*
	 * 将province实例存储到数据库
	 * */
	public void saveProvince(Province province){
		
//		LogUtil.e("判断传入.....省......对象是否为空不为空进行savaProvince保存");
		
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
			
		}
	}
	/*
	 * 从数据库读取全国所有的省份信息
	 * */
	public List<Province> loadProvinces(){
		
		List<Province> list = new ArrayList<Province>();
		
		/*cursor将光标指定到province_id列前，之后可以循环输出符合province_id的所有数据
		"province_id=?",new String[]{String.valueOf(provinceId)},
		这样写两个元素是是为了防止别人的攻击，在"province_id=?",位置可以直接写
		条件，但是这样不安全*/
		
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null, null);
		

		if(cursor.moveToFirst()){
			do{
				
//				LogUtil.e("在光标中以此读取loadProvinces....name: " +cursor.getString(cursor
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
	 * 将city实例化存储到数据库:将数据写入到数据库
	 * */
	
	public void saveCity(City city){
		
//		LogUtil.e("判断传入.....市......对象是否为空不为空进行savacity保存");
		
		if(city!=null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id",city.getProvinceId());
			
			
			db.insert("City", null, values);

		}
	}
	/*
	 * 从数据库中读取某省下的所有城市信息
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
	 * 将county实例化存储到数据库：写数据到数据库中
	 * */
	public void saveCounty(County county){
		
//		LogUtil.e("判断传入.....县......对象是否为空不为空进行savacounty保存");
		
		if(county!=null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code",county.getCountyCode());
			values.put("city_id",county.getCityId());
			db.insert("County", null, values);
		}
	}
	/*
	 * 将数据库读取某城市下的所有县的信息
	 * */
	public List<County> loadCounty(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query( "County", null,"city_id=?", new String[]{String.valueOf(cityId)}, 
				 null,null,null);
		if(cursor.moveToFirst()){
			do{
				
//				LogUtil.e("在光标中以此读取loadcounty....name: " +cursor.getString(cursor
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
