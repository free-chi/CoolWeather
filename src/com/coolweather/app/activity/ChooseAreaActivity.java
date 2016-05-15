package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.LogUtil;
import com.coolweather.app.util.Utility;




import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ChooseAreaActivity extends Activity{
	public static final int LEVEL_PROVINCE =0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	public static int count=0;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private CoolWeatherDB coolWeatherDB;
	private ArrayAdapter<String> adapter;
	private List<String> dataList = new ArrayList<String>();
	
	//省列表
	private List<Province> provinceList;
	//市列表
	private List<City> cityList;
	//县列表
	private List<County> countyList;
	//选中的省份
	private Province selectedProvince;
	//选中的城市
	private City selectedCity;
	//当前选中级别
	private int currentLevel;
	
	/*
	 * 是否从WeatherActivity中跳转过来
	 * */
	private boolean isFromWeatherActivity;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
//		LogUtil.e("第一次启动CoolWeather中currentlevel没赋值的值："+currentLevel);
		
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		//已经选择了城市且不是从WeatherActivity跳转过来，才会直接跳转到WeatherActivity
		
		
		if(prefs.getBoolean("city_selected", false) && !isFromWeatherActivity){
			Intent intent = new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
				dataList);
		listView.setAdapter(adapter);
		coolWeatherDB  = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int index, long id) {
				if(currentLevel == LEVEL_PROVINCE){
					
//					LogUtil.e("currentLevel == LEVEL_PROVINCE");
					
					selectedProvince = provinceList.get(index);
					queryCities();
					return;//加return避免自动触发两次点击item；
				}
				if(currentLevel == LEVEL_CITY){
					
//					LogUtil.e("currentLevel == LEVEL_CITY");
					for(City city : cityList){
//						LogUtil.e("onClickItem中循环输出："+city.getCityName());
					}
	
					selectedCity = cityList.get(index);
					queryCounties();
					return;
				}else if(currentLevel == LEVEL_COUNTY){
					
//					LogUtil.e("currentLevel == LEVEL_COUNTY开始启动weatherActivity");
					
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}

			
		});
		queryProvinces();//加载省级数据
	}
	/*
	 * 查询全国所有的省，优先从数据库查询，如果没有再去服务器上面查询
	 * */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();//从本地查询
		
//		LogUtil.e( "provinceList>0:"+(provinceList.size()>0));
		
		if(provinceList.size()>0){
			
//			LogUtil.e("成功从本地获取到......省......数据并开始执行显示");
			
			dataList.clear();
			for(Province province : provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
			
//			LogUtil.e("显示数据成功，并更改currentLevel为LEVEL_PROVINCE");
			
		}else{
			
			
//			LogUtil.e("第一次查询.....省....通过queryFromServer网络查询");
			
			
			queryFromServer(null,"province");
//				while(count<3){
//					queryFromServer(null,"province");
//					count++;
//				}
			
		}
		
	}

	/*
	 * 查询全国所有的市，优先从数据库查询，如果没有再去服务器上面查询
	 * */
	private void queryCities() {
		
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		
//		LogUtil.e("queryCities>0?:"+(cityList.size()>0));
		
		if(cityList.size()>0){
			
//			LogUtil.e("成功从本地获取到....城市....数据并开始执行显示");
			
			dataList.clear();
			for(City city : cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
			
//			LogUtil.e("显示数据成功，并更改currentLevel为LEVEL_CITY");
			
		}else{
			 
//			LogUtil.e("第一次查询.....城市....通过queryFromServer网络查询");
			
			
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
		
	}
	/*
	 * 查询全国所有的县，优先从数据库查询，如果没有再去服务器上面查询
	 * */


	private void queryCounties() {
	
		countyList = coolWeatherDB.loadCounty(selectedCity.getId());
		
//		LogUtil.e("countyList>0?: "+(countyList.size()>0));
		
		if(countyList.size()>0){
			
//			LogUtil.e("成功从本地获取到......县.......数据并开始执行显示");
			
			dataList.clear();
			for(County county : countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
			
//			LogUtil.e("显示数据成功，并更改currentLevel为LEVEL_COUNTY");
			
		}else{
			
//			LogUtil.e("第一次查询.....县....通过queryFromServer网络查询");
			
			queryFromServer(selectedCity.getCityCode(),"county");
		}
	}
	/*
	 * 从服务器上面查询数据
	 * */
	private void queryFromServer(final String code, final String type) {
		
		String address;	
		if(!TextUtils.isEmpty(code)){
			
//			LogUtil.e( "queryFromServer不为空执行加code查询市或县: "+code);
			
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
			
		}else{
			
//			LogUtil.e("queryFromServer中Code为空，执行第一次查询....省....地址");
			
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				
//			LogUtil.e("onFinish: "+ Thread.currentThread().getName());
				
			boolean result = false;
			if("province".equals(type)){
				
//				LogUtil.e("queryFromServer......province:"+response);
				
				result = Utility.handleProvinceResponse(coolWeatherDB, response);
			}else if("city".equals(type)){
				
//				LogUtil.e("queryFromServer......city:"+response);
				
				result = Utility.handCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
			}else if("county".equals(type)){
				
//				LogUtil.e("queryFromServer......county:"+response);
				
				result = Utility.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
			}
			
//			LogUtil.e( "reslut: "+result);
			
			if(result){
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						closeProgressDialog();
						if("province".equals(type)){
							
//							LogUtil.e("province is working开始查询显示省数据----》queryProvince()");
							
							queryProvinces();
						}else if("city".equals(type)){
							
//							LogUtil.e("province is working开始查询显示市数据----》querycities()");
							
							queryCities();
						}else if("county".equals(type)){
							
//							LogUtil.e("province is working开始查询显示县数据----》querycounties()");
							
							queryCounties();
						}
					}
				
				});
			}
			}
			
			@Override
			public void onError(Exception e) {
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						//Log.e("tag","onError: "+ Thread.currentThread().getName());
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
								"加载失败", Toast.LENGTH_SHORT).show();
						
					}
				});
			}
		});
	}
	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载....");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		
	}
	/*捕获back按键，根据当前的级别来判断，此时应该返回市列表，省列表，还是直接退出*/
	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			if(isFromWeatherActivity){
				Intent intent = new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}

	}

	
}
