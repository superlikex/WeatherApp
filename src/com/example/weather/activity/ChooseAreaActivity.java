package com.example.weather.activity;




import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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

import com.example.weather.R;
import com.example.weather.model.City;
import com.example.weather.model.County;
import com.example.weather.model.Province;
import com.example.weather.model.WeatherDB;
import com.example.weather.util.HttpCallbackListener;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.Untility;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private boolean isFromWeatherActivity;
	
	private ListView listView;
	private TextView textView;
	
	private ArrayAdapter<String> adapter;
	private WeatherDB weatherDB;
	private List<String> dataList = new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	private City selectedCity;
	private Province selectedProvince;
	private County selectedCounty;
	
	private int currentLevel;
	
	
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.
				getDefaultSharedPreferences(this);
				if (prefs.getBoolean("city_selected", false)&& !isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
				finish();
				return;
				}
		setContentView(R.layout.choose_area);
		
		listView = (ListView)findViewById(R.id.list_view);
		textView = (TextView)findViewById(R.id.title_text);
		
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		
		weatherDB = WeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView <?> arg0,View view,int index,long arg3){
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(index);
					queryCities();
				}else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				}else if (currentLevel == LEVEL_COUNTY) {
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,
					WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
					}
					
						
					
			}
		});
		//queryFromServer(null , "province");
		queryProvinces();
		// 加载省级数据
		

	}
	
	private void queryProvinces(){
		provinceList = weatherDB.loadProvinces();
		if(provinceList.size()>0){
			dataList.clear();
			for(Province province : provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	private void queryCities(){
		cityList = weatherDB.loadCities(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City city : cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	
	private void queryCounties(){
		countyList = weatherDB.loadCounties(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county : countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"county");
		}
	}
	
	private void queryFromServer(final String code , final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://flash.weather.com.cn/wmaps/xml/"+code+".xml";
		}
		else
			address = "http://flash.weather.com.cn/wmaps/xml/china.xml";
		
		HttpUtil.sendRequestWithClient(address,new HttpCallbackListener(){
			boolean result = false;
			@Override
			public void onFinish(final String response){
				
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						
						switch(type){
							case "province":
								result = Untility.handleProvincesResponse(weatherDB,response);
								break;
							case "city":
								result = Untility.handleCitiesResponse(weatherDB,response,selectedProvince.getId());
								break;
							case "county":
								result = Untility.handleCountiesResponse(weatherDB,response,selectedCity.getId());
								break;
						}
						
					//	textView.setText(response.toString());
	//					textView.setText("wefdw");
					}
				});
				if (true) {
					// 通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
					@Override
					public void run() {
				//	closeProgressDialog();
					if ("province".equals(type)) {
					queryProvinces();
					} else if ("city".equals(type)) {
					queryCities();
					} else if ("county".equals(type)) {
					queryCounties();
					}
					}
					});
					}
			}
			
			@Override 
			public void onError(Exception e){
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						
					}
				
					
				
				});
			}
			
		});
		
		
	}
	
	
	
		
	
	@Override
	public void onBackPressed() {
	if (currentLevel == LEVEL_COUNTY) {
	queryCities();
	} else if (currentLevel == LEVEL_CITY) {
	queryProvinces();
	} else {if (isFromWeatherActivity) {
		Intent intent = new Intent(this, WeatherActivity.class);
		startActivity(intent);
		}
	finish();
	}
	}
	

}
