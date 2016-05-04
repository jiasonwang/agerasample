package com.stevenswang.agerasample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.agera.BaseObservable;
import com.google.android.agera.Function;
import com.google.android.agera.Merger;
import com.google.android.agera.Predicate;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.Updatable;
import com.stevenswang.agerasample.entity.TelInfoEntity;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Created by wolun on 16/5/3.
 */
public class SampleFragment extends Fragment implements Updatable {
    private View mRootView;
    private Repository<Result<TelInfoEntity>> mTelInfoRepository;
    private QueryObservableClick mSourceObservable;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sample_fg_ly, container, false);
        mRootView = rootView;
        setUpRepository();
        return rootView;
    }

    private void setUpRepository() {
        mSourceObservable = new QueryObservableClick();
        getView(R.id.btn,mRootView).setOnClickListener(mSourceObservable);
        mTelInfoRepository = Repositories
                .repositoryWithInitialValue(Result.<TelInfoEntity>absent())
                .observe(mSourceObservable)
                .onUpdatesPerLoop()
                .getFrom(new Supplier<String>() {
                    @NonNull
                    @Override
                    public String get() {
                        TextView inputView = getView(R.id.telphone, mRootView);
                        String telNum = inputView.getText().toString().trim();
                        return telNum;
                    }
                })
                .goTo(newSingleThreadExecutor())
                .attemptGetFrom(new TelSupplier(mSourceObservable))
                .
                .compile();

    }

    protected <T extends View> T getView(int id, View parent) {
        return (T) parent.findViewById(id);
    }

    @Override
    public void onPause() {
        super.onPause();
        mTelInfoRepository.removeUpdatable(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTelInfoRepository.addUpdatable(this);
    }

    @Override
    public void update() {
        //// TODO: 16/5/3
        Result<TelInfoEntity> result = mTelInfoRepository.get();
        if (result.isPresent()){
            TextView infoTv = getView(R.id.tel_info,mRootView);
            TelInfoEntity entity = result.get();
            StringBuilder builder = new StringBuilder();
            builder.append("归属地："+entity.getProvince());
            builder.append("\n运营商："+entity.getCarrier());
            infoTv.setText(builder.toString());
        }
    }

    private class QueryObservableClick extends BaseObservable implements Supplier<String>,View.OnClickListener {
        private String getPhone;
        public void query() {
            TextView inputView = getView(R.id.telphone, mRootView);
            String telNum = inputView.getText().toString().trim();
            getPhone = telNum;
            if (!TextUtils.isEmpty(telNum)) {
                dispatchUpdate();
            }
        }

        @NonNull
        @Override
        public String get() {
            if (getPhone == null){
                getPhone = "";
            }
            return getPhone;
        }

        @Override
        public void onClick(View v) {
            query();
        }
    }
}