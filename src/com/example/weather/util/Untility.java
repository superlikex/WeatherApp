package com.example.weather.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.weather.model.City;
import com.example.weather.model.County;
import com.example.weather.model.Province;
import com.example.weather.model.WeatherDB;

//解析数据
//01|北京,02|上海,03|天津,04|重庆,05|黑龙江,06|吉林,07|辽宁,08|内蒙古,09|河北,10|山西,11|
//
//可以看到,北京的代号是 01,上海的代号是 02,不同省份之间以逗号分隔,省份名称
//和省级代号之间以单竖线分隔。那么如何才能知道某个省内有哪些城市呢?其实也很简单,
//比如江苏的省级代号是 19,访问如下地址即可:
//http://www.weather.com.cn/data/list3/city19.xml
//也就是说,只需要将省级代号添加到 city 的后面就行了,现在服务器返回的数据如下:
//1901| 南 京 ,1902| 无 锡 ,1903| 镇 江 ,1904| 苏 州 ,1905| 南 通 ,1906| 扬 州 ,1907| 盐 城 ,1908| 徐
//州,1909|淮安,1910|连云港,1911|常州,1912|泰州,1913|宿迁
//这样我们就得到江苏省内所有城市的信息了,可以看到,现在返回的数据格式和刚才查
//看省份信息时返回的数据格式是一样的。相信此时你已经可以举一反三了,比如说苏州的市
//级代号是 1904,那么想要知道苏州市下又有哪些县的时候,只需访问如下地址:
//http://www.weather.com.cn/data/list3/city1904.xml
//这次服务器返回的数据如下:
//190401| 苏 州 ,190402| 常 熟 ,190403| 张 家 港 ,190404| 昆 山 ,190405| 吴 县 东 山 ,190406| 吴
//县,190407|吴江,190408|太仓
//通过这种方式,我们就能把全国所有的省、市、县都罗列出来了。那么解决了全国省市
//县数据的获取,我们又怎样才能查看到具体的天气信息呢?这就必须找到某个地区对应的天
//气代号。比如说昆山的县级代号是 190404,那么访问如下地址:
//http://www.weather.com.cn/data/list3/city190404.xml
//这时服务器返回的数据非常简短:
//190404|101190404
//其中,后半部分的 101190404 就是昆山所对应的天气代号了。这个时候再去访问查询天
//气接口,将相应的天气代号填入即可,接口地址如下:
//http://www.weather.com.cn/data/cityinfo/101190404.html
//这样,服务器就会把昆山当前的天气信息以 JSON 格式返回给我们了,如下所示:
//{"weatherinfo":
//{"city":"昆山","cityid":"101190404","temp1":"21°C","temp2":"9°C",
//"weather":"多云转小雨","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}
//}
//其中 city 表示城市名,cityid 表示城市对应的天气代号,temp1 和 temp2 表示气温是几度
//到几度,weather 表示今日天气信息的描述,img1 和 img2 表示今日天气对应的图片,ptime
//498第 14 章 进入实战,开发酷欧天气
//表示天气发布的时间。至于 JSON 数据的解析,对你来说应该很轻松了吧。
//确定了技术完全可行之后,接下来就可以开始编码了。不过别着急,我们准备让酷欧天
//气成为一个开源软件,并使用 GitHub 来进行代码托管,因此先让我们进入到本书最后一次
//的 Git 时间。
public class Untility {
//	public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB,String response){
//		if(!TextUtils.isEmpty(response)){
//			String[] allProvinces = response.split(",");
//			if(allProvinces != null &&allProvinces.length > 0){
//				for(String p : allProvinces){
//					String[] array = p.split("\\|");
//					Province province = new Province();
//					province.setProvinceName(array[1]);
//					province.setProvinceCode(array[0]);
//					
//					weatherDB.saveProvince(province);
//				}
//				return true;
//			}
//		}
//		return false;
//	}
	public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB,String response){
		
		ByteArrayInputStream stream = new ByteArrayInputStream(response.getBytes());
	    
	        //得到 DocumentBuilderFactory 对象, 由该对象可以得到 DocumentBuilder 对象
	        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
	        
	        try {
	            //得到DocumentBuilder对象
	            DocumentBuilder builder=factory.newDocumentBuilder();
	            //得到代表整个xml的Document对象
	            Document document=builder.parse(stream);
	            //得到 "根节点" 
	            Element root=document.getDocumentElement();
	            //获取根节点的所有items的节点
	            NodeList items=root.getElementsByTagName("city");  
	            //遍历所有节点
	            for(int i=0;i<items.getLength();i++)
	            {
	                Province province=new Province();
	                Element item=(Element)items.item(i);
	                province.setProvinceName (item.getAttribute("quName"));
	                province.setProvinceCode(item.getAttribute("pyName"));
	                weatherDB.saveProvince(province);
	                Log.d("test",item.getAttribute("quName").toString());
	                Log.d("test",item.getAttribute("pyName").toString());
	            }
	            return true;
	            
	        } catch (ParserConfigurationException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (SAXException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return false;
	}

	
	public static boolean handleCitiesResponse(WeatherDB weatherDB,String response,int id){
		ByteArrayInputStream stream = new ByteArrayInputStream(response.getBytes());
	    
        //得到 DocumentBuilderFactory 对象, 由该对象可以得到 DocumentBuilder 对象
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        
        try {
            //得到DocumentBuilder对象
            DocumentBuilder builder=factory.newDocumentBuilder();
            //得到代表整个xml的Document对象
            Document document=builder.parse(stream);
            //得到 "根节点" 
            Element root=document.getDocumentElement();
            //获取根节点的所有items的节点
            NodeList items=root.getElementsByTagName("city");  
            //遍历所有节点
            for(int i=0;i<items.getLength();i++)
            {
                City city=new City();
                Element item=(Element)items.item(i);
                city.setCityName (item.getAttribute("cityname"));
                city.setCityCode(item.getAttribute("pyName"));
                city.setProvinceId(id);
                weatherDB.saveCity(city);
                Log.d("test",item.getAttribute("cityname").toString());
                Log.d("test",item.getAttribute("pyName").toString());
            }
            return true;
            
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
	}
	
	
	public static boolean handleCountiesResponse(WeatherDB weatherDB,
			String response,int id) {
ByteArrayInputStream stream = new ByteArrayInputStream(response.getBytes());
	    
        //得到 DocumentBuilderFactory 对象, 由该对象可以得到 DocumentBuilder 对象
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        
        try {
            //得到DocumentBuilder对象
            DocumentBuilder builder=factory.newDocumentBuilder();
            //得到代表整个xml的Document对象
            Document document=builder.parse(stream);
            //得到 "根节点" 
            Element root=document.getDocumentElement();
            //获取根节点的所有items的节点
            NodeList items=root.getElementsByTagName("city");  
            //遍历所有节点
            for(int i=0;i<items.getLength();i++)
            {
                County county=new County();
                Element item=(Element)items.item(i);
                county.setCountyName (item.getAttribute("cityname"));
                county.setCountyCode(item.getAttribute("url"));
                county.setCityId(id);
                weatherDB.saveCounty(county);
                Log.d("test",item.getAttribute("cityname").toString());
                Log.d("test",item.getAttribute("url").toString());
            }
            return true;
            
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
	}
	
	
	/**
	* 解析服务器返回的JSON数据,并将解析出的数据存储到本地。
	*/
	public static void handleWeatherResponse(Context context, String response) {
	try {
	JSONObject jsonObject = new JSONObject(response);
	JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
	String cityName = weatherInfo.getString("city");
	String weatherCode = weatherInfo.getString("cityid");
	String temp1 = weatherInfo.getString("temp1");
	String temp2 = weatherInfo.getString("temp2");
	String weatherDesp = weatherInfo.getString("weather");
	String publishTime = weatherInfo.getString("ptime");
	saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
	weatherDesp, publishTime);
	} catch (JSONException e) {
	e.printStackTrace();
	}
	}
	/**
	* 将服务器返回的所有天气信息存储到SharedPreferences文件中。
	*/
	public static void saveWeatherInfo(Context context, String cityName,
	String weatherCode, String temp1, String temp2, String weatherDesp, String
	publishTime) {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",
	Locale.CHINA);
	SharedPreferences.Editor editor = PreferenceManager
	.getDefaultSharedPreferences(context).edit();
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
			

}
