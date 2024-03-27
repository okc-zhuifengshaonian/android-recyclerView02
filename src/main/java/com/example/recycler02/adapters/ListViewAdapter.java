package com.example.recycler02.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.recycler02.R;
import com.example.recycler02.beans.ItemBean;

import java.util.List;


public class ListViewAdapter extends BaseAdapter {

    //普通的条目类型
    public static final int TYPE_NORMAL = 0;
    //加载更多
    public static final int TYPE_LOADER_MORE = 1;

    public ListViewAdapter(List<ItemBean> data) {
        super(data);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            //如果是最后一个条目，则返回加载更多
            return TYPE_LOADER_MORE;
        }
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getSubView(parent, viewType);
        if (viewType == TYPE_NORMAL) {
            return new InnerHolder(view);
        } else {
            return new LoaderMoreHolder(view);
        }
    }

    @Override
    protected View getSubView(ViewGroup parent, int viewType) {
        View view;

        if (viewType == TYPE_NORMAL) {
            view = View.inflate(parent.getContext(), R.layout.item_list_view, null);
        } else {
            view = View.inflate(parent.getContext(), R.layout.item_list_loader_more, null);
        }

        return view;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL && holder instanceof InnerHolder) {
            //在这里设置数据
            ((InnerHolder) holder).setData(mData.get(position), position);
        } else if (getItemViewType(position) == TYPE_LOADER_MORE && holder instanceof LoaderMoreHolder) {
            ((LoaderMoreHolder) holder).update(LoaderMoreHolder.LOADER_STATE_LOADING);
        }
    }

    private OnRefreshListener mUpPullRefreshListener;

    public interface OnRefreshListener {
        void onUpPullRefresh(LoaderMoreHolder loaderMoreHolder);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mUpPullRefreshListener = listener;
    }

    public class LoaderMoreHolder extends RecyclerView.ViewHolder {
        public static final int LOADER_STATE_LOADING = 0;
        public static final int LOADER_STATE_RELOAD = 1;
        public static final int LOADER_STATE_NORMAL = 2;

        private LinearLayout mLoading;
        private TextView mReLoad;

        public LoaderMoreHolder(View itemView) {
            super(itemView);

            mLoading = (LinearLayout) itemView.findViewById(R.id.loading);
            mReLoad = (TextView) itemView.findViewById(R.id.reload);

            mReLoad.setOnClickListener(v -> {
                //这里面要去触发加载数据
                update(LOADER_STATE_LOADING);
            });
        }

        public void update(int state) {
            //重置控件的状态
            mLoading.setVisibility(View.GONE);
            mReLoad.setVisibility(View.GONE);

            switch (state) {
                case LOADER_STATE_LOADING:
                    mLoading.setVisibility(View.VISIBLE);
                    //触发加载数据
                    if (mUpPullRefreshListener != null) {
                        mUpPullRefreshListener.onUpPullRefresh(this);
                    }
                    break;

                case LOADER_STATE_RELOAD:
                    mReLoad.setVisibility(View.VISIBLE);
                    break;

                case LOADER_STATE_NORMAL:
                    mLoading.setVisibility(View.GONE);
                    mReLoad.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
