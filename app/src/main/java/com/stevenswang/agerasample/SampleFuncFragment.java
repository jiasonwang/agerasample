package com.stevenswang.agerasample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.agera.Function;
import com.google.android.agera.Predicate;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.RepositoryConfig;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.Updatable;
import com.stevenswang.agerasample.entity.UiModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Created by wolun on 16/5/6.
 */
public class SampleFuncFragment extends Fragment implements Updatable {
    private View mRootView;
    private Repository<Result<List<UiModel>>> mRepository;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.list_ly, container, false);
        mRecyclerView = getView(R.id.list, mRootView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new InnerAdapter());
        setUpRepository();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRepository.addUpdatable(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRepository.removeUpdatable(this);
    }

    protected <T extends View> T getView(int id, View parent) {
        return (T) parent.findViewById(id);
    }

    private void setUpRepository() {
        mRepository = Repositories
                .repositoryWithInitialValue(Result.<List<UiModel>>absent())
                .observe()
                .onUpdatesPerLoop()
                .goTo(newSingleThreadExecutor())
                .getFrom(new Supplier<List<UiModel>>() {
                    @NonNull
                    @Override
                    public List<UiModel> get() {
                        return new SampleDataFuncProvider().makeDataFunc(getContext()).apply("data.json");
                    }
                }).goTo(new Executor() {
                    @Override
                    public void execute(Runnable command) {
                        new Handler(Looper.getMainLooper()).post(command);
                    }
                }).check(new Predicate<List<UiModel>>() {
                    @Override
                    public boolean apply(@NonNull List<UiModel> value) {
                        return !value.isEmpty();
                    }
                })
                .orSkip()
                .thenTransform(new Function<List<UiModel>, Result<List<UiModel>>>() {
                    @NonNull
                    @Override
                    public Result<List<UiModel>> apply(@NonNull List<UiModel> input) {
                        Result<List<UiModel>> result = Result.success(input);
                        return result;
                    }
                })
                .onDeactivation(RepositoryConfig.CANCEL_FLOW)
                .compile();
    }

    @Override
    public void update() {
        Result<List<UiModel>> dataResult = mRepository.get();
        if (dataResult.isPresent()) {
            InnerAdapter adapter = (InnerAdapter) mRecyclerView.getAdapter();
            adapter.setData(dataResult.get());
        }

    }

    private class InnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<UiModel> mItems;

        public void setData(List<UiModel> items) {
            if (items == null) {
                return;
            }
            if (mItems != null) {
                mItems.clear();
            }
            if (mItems == null) {
                mItems = new ArrayList<>(items);
            }
            mItems.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.item_ly, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView tempTv = (TextView) holder.itemView.findViewById(R.id.textX);
            tempTv.setText(mItems.get(position).x);
            tempTv = (TextView) holder.itemView.findViewById(R.id.textY);
            tempTv.setText(mItems.get(position).y);
        }

        @Override
        public int getItemCount() {
            return mItems == null ? 0 : mItems.size();
        }
    }

    private static class VH extends RecyclerView.ViewHolder {

        public VH(View itemView) {
            super(itemView);
        }
    }
}
