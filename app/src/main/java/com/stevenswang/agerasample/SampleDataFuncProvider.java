package com.stevenswang.agerasample;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.agera.Function;
import com.google.android.agera.Functions;
import com.google.android.agera.Predicate;
import com.google.gson.Gson;
import com.stevenswang.agerasample.entity.FansCommonData;
import com.stevenswang.agerasample.entity.FansCountEntity;
import com.stevenswang.agerasample.entity.UiModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import static com.google.android.agera.Preconditions.checkNotNull;

/**
 * Created by wolun on 16/5/6.
 */
public class SampleDataFuncProvider {
    public Function<String, List<UiModel>> makeDataFunc(final Context context) {
        Function<String, FansCommonData> fileToDataFunc = new Function<String, FansCommonData>() {
            @NonNull
            @Override
            public FansCommonData apply(@NonNull String input) {
                checkNotNull(input);
                FansCommonData data = null;
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(input), "utf-8"));
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    Gson gson = new Gson();
                    data = gson.fromJson(content.toString(), FansCommonData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (data == null || data.getData().getFans().size() != data.getData().getxAxis().size()) {
                    data = new FansCommonData();
                }
                return data;
            }
        };
        Function<FansCommonData, List<FansCountEntity>> dataToList = new Function<FansCommonData, List<FansCountEntity>>() {
            @NonNull
            @Override
            public List<FansCountEntity> apply(@NonNull FansCommonData input) {
                List<FansCountEntity> retList = new LinkedList<>();
                for (int i = 0; i < input.getData().getFans().size(); i++) {
                    FansCountEntity fansCountEntity = new FansCountEntity();
                    fansCountEntity.setFans(input.getData().getFans().get(i));
                    fansCountEntity.setDate(input.getData().getxAxis().get(i));
                    retList.add(fansCountEntity);
                }
                return retList;
            }
        };
        Predicate<FansCountEntity> filter = new Predicate<FansCountEntity>() {
            @Override
            public boolean apply(@NonNull FansCountEntity value) {
                int intValue = Integer.parseInt(value.getFans());
                return intValue > 2 && intValue < 100;
            }
        };
        Function<FansCountEntity, UiModel> map = new Function<FansCountEntity, UiModel>() {
            @NonNull
            @Override
            public UiModel apply(@NonNull FansCountEntity input) {
                UiModel uiModel = new UiModel();
                uiModel.x = input.getDate();
                uiModel.y = input.getFans();
                return uiModel;
            }
        };

        Function<String, List<UiModel>>
                file2DataList = Functions.functionFrom(String.class)
                .apply(fileToDataFunc)
                .unpack(dataToList)
                .filter(filter)
                .thenMap(map);

        return file2DataList;
    }
}
