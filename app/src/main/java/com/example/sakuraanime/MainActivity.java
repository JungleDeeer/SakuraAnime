package com.example.sakuraanime;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sakuraanime.feedBack.SendMailUtil;

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



public class MainActivity extends AppCompatActivity {
    private EditText searchEditText;
    private Button searchButton;
    ImageView mLeftLogo;
    ImageView mRightLogo;
    ImageView mDeleteSearchWord;
    String mIp;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        searchEditText = findViewById(R.id.word_search);
        searchButton = findViewById(R.id.btn_search);
        mLeftLogo = findViewById(R.id.iv_icon_left);
        mRightLogo = findViewById(R.id.iv_icon_right);
        mDeleteSearchWord = findViewById(R.id.delete_search);
        searchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);

        mLeftLogo.setImageResource(R.drawable.ic_22);
        mRightLogo.setImageResource(R.drawable.ic_33);

        new ipThread().start();  //counting users

        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && searchEditText.getText().length() > 0) {
                mDeleteSearchWord.setVisibility(View.VISIBLE);
            } else {
                mDeleteSearchWord.setVisibility(View.GONE);
            }
            mLeftLogo.setImageResource(R.drawable.ic_22_hide);
            mRightLogo.setImageResource(R.drawable.ic_33_hide);
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 0) {
                    // 有内容时候 显示删除按钮
                    mDeleteSearchWord.setVisibility(View.VISIBLE);
                } else {
                    // 没内容时候 不显示删除按钮
                    mDeleteSearchWord.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    // 有内容时候 显示删除按钮
                    mDeleteSearchWord.setVisibility(View.VISIBLE);
                } else {
                    // 没内容时候 不显示删除按钮
                    mDeleteSearchWord.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    // 有内容时候 显示删除按钮
                    mDeleteSearchWord.setVisibility(View.VISIBLE);
                } else {
                    // 没内容时候 不显示删除按钮
                    mDeleteSearchWord.setVisibility(View.GONE);
                }
            }
        });


        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND || (event!=null&&event.getKeyCode()==KeyEvent.KEYCODE_ENTER)){
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(searchEditText.getApplicationWindowToken(), 0);
                    }
                    if(searchEditText.getText().toString().equals("")){
                        Toast.makeText(v.getContext(),"你咋没输关键词呢",Toast.LENGTH_LONG).show();
                        return false;
                    }
//                    mIp = getIPAddress(v.getContext());
//                    senTextMail(mIp);
                    String search = searchEditText.getText().toString();
                    String url = getUrl(search);

                    Toast.makeText(v.getContext(),"搜索资源中，请等等哦",Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent();
                    bundle.putString("searchResultUrl",url);
                    bundle.putString("search",search);
                    intent.putExtras(bundle);
                    intent.setClass(MainActivity.this,ListActivity.class);
                    startActivity(intent);


                }
                return false;
            }
        });



        mDeleteSearchWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                mDeleteSearchWord.setVisibility(View.GONE);
                searchEditText.setFocusable(true);
                searchEditText.setFocusableInTouchMode(true);
                searchEditText.clearFocus();
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(searchEditText.getApplicationWindowToken(), 0);
                }
                mLeftLogo.setImageResource(R.drawable.ic_22);
                mRightLogo.setImageResource(R.drawable.ic_33);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(searchEditText.getApplicationWindowToken(), 0);
                }
                if(searchEditText.getText().toString().equals("")){
                    Toast.makeText(v.getContext(),"你咋没输关键词呢",Toast.LENGTH_LONG).show();
                    return;
                }

//                mIp = getIPAddress(v.getContext());
//                senTextMail(mIp);
//                new mailThread().start();

                String search = searchEditText.getText().toString();
                String url = getUrl(search);

                Toast.makeText(v.getContext(),"搜索资源中，请等等哦",Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                bundle.putString("searchResultUrl",url);
                bundle.putString("search",search);
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this,ListActivity.class);
                startActivity(intent);

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
        }
    }


    public void senTextMail(String mIp) {

        SendMailUtil.send("kj1110great@outlook.com",mIp);
//        Toast.makeText(MainActivity.this, "邮件已发送", Toast.LENGTH_SHORT).show();
    }


}