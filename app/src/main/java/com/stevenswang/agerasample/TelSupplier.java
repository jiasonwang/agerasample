package com.stevenswang.agerasample;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.gson.Gson;
import com.stevenswang.agerasample.entity.TelInfoEntity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Created by wolun on 16/5/3.
 */
public class TelSupplier implements Supplier<Result<TelInfoEntity>> {
    private Supplier<String> mTel;

    public TelSupplier(Supplier<String> tel) {
        mTel = tel;
    }

    private static final String mBaseUrl = "http://tcc.taobao.com/cc/json/mobile_tel_segment.htm?";

    private TelInfoEntity getTelInfo() throws Exception {
        URL url = new URL(mBaseUrl + "tel=" + mTel.get());

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
//        httpURLConnection.addRequestProperty("tel", mTel.get());
        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode != 200) {
            if (responseCode == 301) {
                String jumpURL = httpURLConnection.getHeaderField("Location");
                if (jumpURL != null) {
                    url = new URL(jumpURL);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    responseCode = httpURLConnection.getResponseCode();
                }
            }
            if (responseCode != 200) {
                throw new Exception();
            }
        }
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
        StringBuilder content = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (!TextUtils.isEmpty(line)) {
                content.append(line);
            }
        }
        JSONObject jsonObject = new JSONObject("{" + content.toString() + "}");
        JSONObject contentObject = jsonObject.optJSONObject("__GetZoneResult_");
        Gson gson = new Gson();
        TelInfoEntity entity = gson.fromJson(contentObject.toString(), TelInfoEntity.class);
        return entity;
    }


    @NonNull
    @Override
    public Result<TelInfoEntity> get() {
        try {
            TelInfoEntity entity = getTelInfo();
            if (entity != null && !TextUtils.isEmpty(entity.getProvince())) {
                return Result.success(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.failure();
    }
}
