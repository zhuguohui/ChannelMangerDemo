package com.trs.channellib.channel.channel.helper;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;


import com.trs.channellib.R;
import com.trs.channellib.channel.channel.ChannelAdapter;
import com.trs.channellib.channel.channel.ChannelEntity;
import com.trs.channellib.channel.channel.ChannelManagerView;
import com.trs.channellib.channel.channel.base.AbsChannel;
import com.trs.channellib.channel.channel.db.ChannelDB;

import net.endlessstudio.dbhelper.types.AbsDBItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuguohui on 2016/5/27.
 */
public class ChannelDataHelepr<T extends  ChannelEntity.ChannelEntityCreater> implements ChannelAdapter.DataChangeListenter, View.OnClickListener {

    private Map<String, AbsChannel> mChannelMap = new HashMap<>();
    private Context mContext;
    private View mSwitchView;
    private PopupWindow popupWindow = null;
    private ChannelManagerView cmv;
    private View mShowView;
    ChannelDB mDB;


    public ChannelDataHelepr(@NonNull Context context, @NonNull ChannelDataRefreshListenter channelDataRefreshListenter, @NonNull View showView) {
        mChannelDataRefreshListenter = channelDataRefreshListenter;
        mContext = context;
        mShowView = showView;
        mDB = ChannelDB.getInstance(mContext);
    }

    public void setSwitchView(View switchView) {
        this.mSwitchView = switchView;
        switchView.setOnClickListener(this);
    }


    @Override
    public void saveData(boolean isChanged, List<ChannelEntity> myChannels, List<ChannelEntity> otherChannels) {
        if (isChanged) {
            ArrayList<AbsChannel> channels = new ArrayList<>();
            int i = 0;
            for (ChannelEntity entity : myChannels) {
                AbsChannel channel = mChannelMap.get(entity.getName());
                if (channel != null) {
                    channel.setIsSubscible(1);
                    channel.setOrderId(i++);
                    channels.add(channel);
                }
            }
            for (ChannelEntity entity : otherChannels) {
                AbsChannel channel = mChannelMap.get(entity.getName());
                if (channel != null) {
                    channel.setIsSubscible(0);
                    channel.setOrderId(i++);
                    channels.add(channel);
                }
            }
            mDB.saveList(channels);
        }
    }

    @Override
    public void close(boolean update) {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        if(update) {
            mChannelDataRefreshListenter.updateData();
        }
    }

    @Override
    public List<ChannelEntity> getMyChannel() {
        return getChannels(true);
    }

    @Override
    public List<ChannelEntity> getOtherChannel() {
        return getChannels(false);
    }

    private List<ChannelEntity> getChannels(boolean subscribed) {
        final List<ChannelEntity> items = new ArrayList<>();
        List<AbsChannel> channels;
        if (subscribed) {
            channels = mDB.getSubscribedList();
        } else {
            channels = mDB.getUnSubscribedList();
        }
        Collections.sort(channels);
        for (AbsChannel channel : channels) {
            ChannelEntity entity = channel.createChannelEntity();
            items.add(entity);
            mChannelMap.put(entity.getName(), channel);
        }
        return items;
    }

    @Override
    public void OnItemClick(View v, boolean update, int position) {
        close(update);
        if (position != -1) {
            mChannelDataRefreshListenter.onChannelSeleted(update,position);
        }
    }

    private ChannelDataRefreshListenter mChannelDataRefreshListenter;

    @Override
    public void onClick(View v) {
        showPop();
    }

    private void showPop() {
        if (popupWindow == null) {
            View popupView = LayoutInflater.from(mContext).inflate(R.layout.pop_subscribe_v2, null);
            popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setAnimationStyle(R.style.subscribe_popwindow_anim_style);
            cmv = (ChannelManagerView) popupView.findViewById(R.id.cmv);
            cmv.setDataChangeListenter(this);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mSwitchView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                return;
            }
        }
        popupWindow.showAsDropDown(mShowView);

    }


    public static interface ChannelDataRefreshListenter {
        public void updateData();

        public void onChannelSeleted(boolean update, int posisiton);
    }

    public List<T> getShowChannels(List<T> list) {
        List<AbsChannel> data=new ArrayList<>();
        HashMap<String,T> allMap=new HashMap<>();
        for(T t:list){
            if(t==null){
                continue;
            }
            ChannelEntity entity = t.createChannelEntity();
            AbsChannel item=new AbsChannel();
            item.setTitle(entity.getName());
            item.setIsFix(entity.isFixed());
            data.add(item);
            allMap.put(entity.getName(),t);
        }
        List<AbsChannel> subScribedList = mDB.getShowSubScribedList(data);
        List<T> showData=new ArrayList<>();
        for(AbsChannel channel:subScribedList){
            if(allMap.containsKey(channel.getTitle())){
                showData.add(allMap.get(channel.getTitle()));
            }
        }
        return showData;
    }
}
