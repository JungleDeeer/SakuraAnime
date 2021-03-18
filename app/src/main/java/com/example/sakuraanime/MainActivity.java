package com.example.sakuraanime;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sakuraanime.feedBack.SendMailUtil;
import com.example.sakuraanime.fragment.HistoryFragment;
import com.example.sakuraanime.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xuexiang.xupdate.XUpdate;

import org.eclipse.jetty.util.component.LifeCycle;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;
    String mIp;

    private Fragment homeFragment;
    private Fragment historyFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNav = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        new ipThread().start();  //counting users
        changeLocale();

        homeFragment = new HomeFragment();
        if(savedInstanceState == null){
            fragmentTransaction
                    .add(R.id.mainFragment,homeFragment)
                    .commit();
        }


        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_home:
                        fragmentManager.beginTransaction().hide(historyFragment).show(homeFragment).commit();
                        break;
                    case R.id.action_history:
                        if(historyFragment==null){
                            historyFragment = new HistoryFragment();
                            fragmentManager.beginTransaction()
                                    .add(R.id.mainFragment,historyFragment)
                                    .hide(homeFragment).show(historyFragment).commit();
                        }else {
                            fragmentManager.beginTransaction().hide(homeFragment).show(historyFragment).commit();
                        }

                        break;
                }
                return false;
            }
        });

//        new IpThread().start();
    }

    public static String getUrl(String key) {
        String url = "http://www.imomoe.ai/search.asp?searchword=";
        StringBuffer buf = new StringBuffer();
        try {
            byte[] bytes = key.getBytes("GB2312");
            for(byte b:bytes) {
                String code = Integer.toHexString(b);
                buf.append('%'+code.substring(code.length()-2));
            }
            url = url + buf.toString();

        }catch (Exception e){

            e.printStackTrace();
        }
        return url;
    }

    public static String getIPAddress(Context context) {//from service
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                Log.d("IP", "getIPAddress: My IP:"+inetAddress.getHostAddress());
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                Log.d("IP", "getIPAddress: MY wifiIP:"+ipAddress);
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        Log.d("IP", "getIPAddress: MY  IP is NULL");
        return null;
    }

    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public static String getNetIp() {//from net
        URL infoUrl = null;
        InputStream inStream = null;
        String line = "";
        try {
            infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            Log.d("TAG", "getNetIp: my responseCode"+responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                while ((line = reader.readLine()) != null)
                    strber.append(line + "\n");
                inStream.close();
                // 从反馈的结果中提取出IP地址
                int start = strber.indexOf("{");
                int end = strber.indexOf("}");
                String json = strber.substring(start, end + 1);
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        line = jsonObject.optString("cip");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    class ipThread extends Thread{
        @Override
        public void run(){
            mIp = getNetIp();
            senTextMail(mIp);
            XUpdate.newBuild(MainActivity.this)
//                        .updateUrl("https://gitee.com/xuexiangjys/XUpdate/raw/master/jsonapi/update_test.json")
                    .updateUrl("http://47.108.218.71:8989/server/api/updateData")
                    .promptThemeColor(getResources().getColor(R.color.update_theme_color))
                    .promptButtonTextColor(Color.WHITE)
                    .promptTopResId(R.drawable.ic_zone_background_gaitubao_468x156)
                    .promptWidthRatio(0.7F)
                    .update();
        }
    }


    public void senTextMail(String mIp) {

        SendMailUtil.send("kj1110great@outlook.com",mIp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(MainActivity.this,"检查更新中",Toast.LENGTH_SHORT).show();
                XUpdate.newBuild(MainActivity.this)
//                        .updateUrl("https://gitee.com/xuexiangjys/XUpdate/raw/master/jsonapi/update_test.json")
                        .updateUrl("http://47.108.218.71:8989/server/api/updateData")
                        .promptThemeColor(getResources().getColor(R.color.update_theme_color))
                        .promptButtonTextColor(Color.WHITE)
                        .promptTopResId(R.drawable.ic_zone_background_gaitubao_468x156)
                        .promptWidthRatio(0.7F)
                        .update();
                break;
            default:
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    private void changeLocale() {
        Resources resource = getResources();
        Configuration config = resource.getConfiguration();
        config.setLocale(Locale.SIMPLIFIED_CHINESE);
        getResources().updateConfiguration(config, null);
    }




}