package com.trs.channellib.channel.channel;

import android.content.Context;
import android.support.annotation.NonNull;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.trs.channellib.R;
import com.trs.channellib.channel.channel.helper.ItemDragHelperCallback;


/**
 * Created by yuelin on 2016/5/26.
 */
public class ChannelManagerView extends FrameLayout {
    RecyclerView mRecy;
    private GridLayoutManager manager;
    private ChannelAdapter adapter;
    private ItemDragHelperCallback callback;
    private ItemTouchHelper helper;

    public ChannelManagerView(Context context) {
        this(context, null);
    }

    public ChannelManagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context,R.layout.view_channel_manager, this);
        init();
    }


    public void setDataChangeListenter(@NonNull ChannelAdapter.DataChangeListenter listenter) {
        adapter = new ChannelAdapter(getContext(), helper,listenter);
        mRecy.setAdapter(adapter);
    }

    private void init() {
        mRecy = (RecyclerView) findViewById(R.id.recy);
        manager = new GridLayoutManager(getContext(), 4);
        mRecy.setLayoutManager(manager);

        callback = new ItemDragHelperCallback();
        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecy);

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = adapter.getItemViewType(position);
                return viewType == ChannelAdapter.TYPE_MY || viewType == ChannelAdapter.TYPE_OTHER ? 1 : 4;
            }
        });

    }


}
