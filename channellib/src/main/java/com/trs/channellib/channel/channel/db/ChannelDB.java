package com.trs.channellib.channel.channel.db;

import android.content.Context;
import android.support.annotation.NonNull;

import com.trs.channellib.channel.channel.base.AbsChannel;

import net.endlessstudio.dbhelper.DataDB;
import net.endlessstudio.dbhelper.query.QueryClause;
import net.endlessstudio.dbhelper.query.QueryClauseBuilder;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by john on 14-6-12.
 */
public class ChannelDB extends DataDB<AbsChannel> {
    private static ChannelDB sInstance;
    public static ChannelDB getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ChannelDB(context);
        }

        return sInstance;
    }

    public ChannelDB(Context context) {
         super(context);
    }

    @Override
    public String getTableName() {
        return "channel";
    }

    @Override
    protected Class<AbsChannel> getItemClass() {
        return AbsChannel.class;
    }

    public List<AbsChannel> getSubscribedList() {
        List<AbsChannel> savedList = getSavedList();
        List<AbsChannel> itemList=new ArrayList<>();
        for(AbsChannel t:savedList){
            if(t.IsSubscrible()){
                itemList.add(t);
            }
        }
        return itemList;
    }

    public List<AbsChannel> getUnSubscribedList() {
        List<AbsChannel> savedList = getSavedList();
        List<AbsChannel> itemList=new ArrayList<>();
        for(AbsChannel t:savedList){
            if(!t.IsSubscrible()){
                itemList.add(t);
            }
        }
        return itemList;
    }

    public void saveList(@NonNull List<AbsChannel> channels) {
        for (AbsChannel channel : channels) {
            updateOrInsert(channel);
        }
    }

    public List<AbsChannel> getSavedList() {
        QueryClause clause = new QueryClauseBuilder().create();
        List<AbsChannel> itemList = get(clause);
     

        return itemList;
    }


    public List<AbsChannel> getShowSubScribedList(List<AbsChannel> list) {
        List<AbsChannel> savedList = getSavedList();
        List<AbsChannel> needShowList = new ArrayList<>();
        List<AbsChannel> needSaveList = new ArrayList<>();
        for (AbsChannel channel : list) {
            int indexOf = savedList.indexOf(channel);
            if (indexOf != -1) {
                AbsChannel savedChannel = savedList.get(indexOf);
                if (savedChannel.IsSubscrible()) {
                    needShowList.add(savedChannel);
                }
            } else {
                channel.setIsSubscible(1);
                needShowList.add(channel);
                needSaveList.add(channel);
            }
        }
        if (needSaveList.size() > 0) {
            saveList(needSaveList);
        }
        Collections.sort(needShowList);
        return needShowList;
    }

    public void clear(){
        delete(getSavedList());
    }

    AbsChannel getInstanceOfT ()
    {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class< AbsChannel > type = (Class<AbsChannel>) superClass.getActualTypeArguments()[ 0 ];
        try
        {
            return type.newInstance() ;
        }
        catch (Exception e)
        {
            // Oops, no default constructor
            throw new RuntimeException(e) ;
        }
    }
}
