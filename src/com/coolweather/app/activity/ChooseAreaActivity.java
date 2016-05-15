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
	
	//ʡ�б�
	private List<Province> provinceList;
	//���б�
	private List<City> cityList;
	//���б�
	private List<County> countyList;
	//ѡ�е�ʡ��
	private Province selectedProvince;
	//ѡ�еĳ���
	private City selectedCity;
	//��ǰѡ�м���
	private int currentLevel;
	
	/*
	 * �Ƿ��WeatherActivity����ת����
	 * */
	private boolean isFromWeatherActivity;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
//		LogUtil.e("��һ������CoolWeather��currentlevelû��ֵ��ֵ��"+currentLevel);
		
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		//�Ѿ�ѡ���˳����Ҳ��Ǵ�WeatherActivity��ת�������Ż�ֱ����ת��WeatherActivity
		
		
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
					return;//��return�����Զ��������ε��item��
				}
				if(currentLevel == LEVEL_CITY){
					
//					LogUtil.e("currentLevel == LEVEL_CITY");
					for(City city : cityList){
//						LogUtil.e("onClickItem��ѭ�������"+city.getCityName());
					}
	
					selectedCity = cityList.get(index);
					queryCounties();
					return;
				}else if(currentLevel == LEVEL_COUNTY){
					
//					LogUtil.e("currentLevel == LEVEL_COUNTY��ʼ����weatherActivity");
					
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}

			
		});
		queryProvinces();//����ʡ������
	}
	/*
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û����ȥ�����������ѯ
	 * */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();//�ӱ��ز�ѯ
		
//		LogUtil.e( "provinceList>0:"+(provinceList.size()>0));
		
		if(provinceList.size()>0){
			
//			LogUtil.e("�ɹ��ӱ��ػ�ȡ��......ʡ......���ݲ���ʼִ����ʾ");
			
			dataList.clear();
			for(Province province : provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
			
//			LogUtil.e("��ʾ���ݳɹ���������currentLevelΪLEVEL_PROVINCE");
			
		}else{
			
			
//			LogUtil.e("��һ�β�ѯ.....ʡ....ͨ��queryFromServer�����ѯ");
			
			
			queryFromServer(null,"province");
//				while(count<3){
//					queryFromServer(null,"province");
//					count++;
//				}
			
		}
		
	}

	/*
	 * ��ѯȫ�����е��У����ȴ����ݿ��ѯ�����û����ȥ�����������ѯ
	 * */
	private void queryCities() {
		
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		
//		LogUtil.e("queryCities>0?:"+(cityList.size()>0));
		
		if(cityList.size()>0){
			
//			LogUtil.e("�ɹ��ӱ��ػ�ȡ��....����....���ݲ���ʼִ����ʾ");
			
			dataList.clear();
			for(City city : cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
			
//			LogUtil.e("��ʾ���ݳɹ���������currentLevelΪLEVEL_CITY");
			
		}else{
			 
//			LogUtil.e("��һ�β�ѯ.....����....ͨ��queryFromServer�����ѯ");
			
			
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
		
	}
	/*
	 * ��ѯȫ�����е��أ����ȴ����ݿ��ѯ�����û����ȥ�����������ѯ
	 * */


	private void queryCounties() {
	
		countyList = coolWeatherDB.loadCounty(selectedCity.getId());
		
//		LogUtil.e("countyList>0?: "+(countyList.size()>0));
		
		if(countyList.size()>0){
			
//			LogUtil.e("�ɹ��ӱ��ػ�ȡ��......��.......���ݲ���ʼִ����ʾ");
			
			dataList.clear();
			for(County county : countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
			
//			LogUtil.e("��ʾ���ݳɹ���������currentLevelΪLEVEL_COUNTY");
			
		}else{
			
//			LogUtil.e("��һ�β�ѯ.....��....ͨ��queryFromServer�����ѯ");
			
			queryFromServer(selectedCity.getCityCode(),"county");
		}
	}
	/*
	 * �ӷ����������ѯ����
	 * */
	private void queryFromServer(final String code, final String type) {
		
		String address;	
		if(!TextUtils.isEmpty(code)){
			
//			LogUtil.e( "queryFromServer��Ϊ��ִ�м�code��ѯ�л���: "+code);
			
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
			
		}else{
			
//			LogUtil.e("queryFromServer��CodeΪ�գ�ִ�е�һ�β�ѯ....ʡ....��ַ");
			
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
							
//							LogUtil.e("province is working��ʼ��ѯ��ʾʡ����----��queryProvince()");
							
							queryProvinces();
						}else if("city".equals(type)){
							
//							LogUtil.e("province is working��ʼ��ѯ��ʾ������----��querycities()");
							
							queryCities();
						}else if("county".equals(type)){
							
//							LogUtil.e("province is working��ʼ��ѯ��ʾ������----��querycounties()");
							
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
								"����ʧ��", Toast.LENGTH_SHORT).show();
						
					}
				});
			}
		});
	}
	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���....");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		
	}
	/*����back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳�*/
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
