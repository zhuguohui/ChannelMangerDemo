package com.trs.channellib.channel.channel.base;



import com.trs.channellib.channel.channel.ChannelEntity;

import net.endlessstudio.dbhelper.DBColumn;
import net.endlessstudio.dbhelper.types.AbsDBItem;


/**
 * Created by yuelin on 2016/5/27.
 */
public class AbsChannel extends AbsDBItem implements Comparable<AbsChannel>, ChannelEntity.ChannelEntityCreater {

    //用于排序的id
    @DBColumn
    private int abs_orderId =0;
    //用于判断是否已经订阅
    @DBColumn
    private int abs_isSubscible=1;
    @DBColumn
    private boolean isFix=false;

    public boolean getIsFix() {
        return isFix;
    }

    public void setIsFix(boolean isFix) {
        this.isFix = isFix;
    }

    @DBColumn
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public final void setIsSubscible(int isSubscible) {
        this.abs_isSubscible = isSubscible;
    }

    public  boolean IsSubscrible(){
        return abs_isSubscible==1;
    }

    @Override
    public final int compareTo(AbsChannel another) {
        return this.abs_orderId -another.getOrderId();
    }

    public final int getOrderId() {
        return abs_orderId;
    }

    public final void setOrderId(int abs_orderId) {
        this.abs_orderId = abs_orderId;
    }



    @Override
    public boolean equals(Object o) {
        if(o instanceof AbsChannel){
            AbsChannel c= (AbsChannel) o;
            ChannelEntity otherEntity = c.createChannelEntity();
            ChannelEntity myEntity=createChannelEntity();
            if(myEntity!=null&&myEntity.getName().equals(otherEntity.getName())){
                return true;
            }
        }
        return super.equals(o);
    }

    @Override
    public ChannelEntity createChannelEntity() {
        ChannelEntity entity=new ChannelEntity();
        entity.setFixed(isFix);
        entity.setName(title);
        return entity;
    }


}
