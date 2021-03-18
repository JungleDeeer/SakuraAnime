package com.example.sakuraanime.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sakuraanime.ListActivity;
import com.example.sakuraanime.MainActivity;
import com.example.sakuraanime.R;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.sakuraanime.MainActivity.getUrl;

public class HomeFragment extends Fragment {

    private EditText searchEditText;
    private Button searchButton;
    ImageView mLeftLogo;
    ImageView mRightLogo;
    ImageView mDeleteSearchWord;
    private InputMethodManager inputMethodManager;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        searchEditText = view.findViewById(R.id.word_search);
        searchButton = view.findViewById(R.id.btn_search);
        mLeftLogo = view.findViewById(R.id.iv_icon_left);
        mRightLogo = view.findViewById(R.id.iv_icon_right);
        mDeleteSearchWord = view.findViewById(R.id.delete_search);
        searchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);

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
                    intent.setClass(getActivity().getApplicationContext(), ListActivity.class);
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
                intent.setClass(getActivity().getApplicationContext(),ListActivity.class);
                startActivity(intent);

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view , Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

    }


}