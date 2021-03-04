package com.example.sakuraanime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;


public class MainActivity extends AppCompatActivity {
    private EditText searchEditText;
    private Button searchButton;
    ImageView mLeftLogo;
    ImageView mRightLogo;
    ImageView mDeleteSearchWord;
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
    }

    public static String getUrl(String key) {
        String url = "http://www.imomoe.ai/search.asp?searchword=";
        StringBuffer buf = new StringBuffer();
        try {
            byte[] bytes = key.getBytes("GB2312");
            for(byte b:bytes) {
                String code = Integer.toHexString(b);
                buf.append(code.substring(code.length()-2));
            }
            url = url + buf.toString();

        }catch (Exception e){

            e.printStackTrace();
        }
        return url;
    }



}