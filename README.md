# ChannelMangerDemo
#前言
最近开发的项目中有类似网易新闻的频道管理，在完成项目后，我将频道管理单独抽取成Library，方便以后开发，也把总结到的一点知识分享出来。先看看，我的频道管理有什么特点吧。
#特点
1.支持固定频道，支持排序，删除。删除，添加皆有动画效果。
![这里写图片描述](http://img.blog.csdn.net/20160530144050729)
2.在排序完成后，通过刷新Adapter而不是重新创建Adapter，性能更好，没有卡顿。
![这里写图片描述](http://img.blog.csdn.net/20160530151849037)
3.使用简单方便，Library中已经封装了数据库操作，获取需要显示的数据只需要一行代码即可

```
  final List<MyChannel> showChannels = dataHelepr.getShowChannels(alldata);
```
#集成

1.添加channellib到你的项目

![这里写图片描述](http://img.blog.csdn.net/20160530152327917)

2.让你的JavaBean实现ChannelEntityCreater接口

```
import com.trs.channellib.channel.channel.ChannelEntity;

/**
 * Created by zhuguohui on 2016/5/12.
 */
public class MyChannel implements ChannelEntity.ChannelEntityCreater {

   ...
    @Override
    public ChannelEntity createChannelEntity() {
        ChannelEntity entity=new ChannelEntity();
        //是否是固定频道
        entity.setFixed(isFix==1);
        //显示的名称
        entity.setName(title);
        return entity;
    }
}
```
3.在你的Activity中示例化ChannelDataHelepr，注意ChannelDatahelper需要一个泛型参数，即你用来表示频道的JavaBean

```
    ChannelDataHelepr<MyChannel> dataHelepr;
    dataHelepr = new ChannelDataHelepr(this, this, findViewById(R.id.top_bar));
```
构造函数声明如下,第一个为上下文，第二个为用来相应频道管理的监听器，第三个为需要将频道管理显示在哪个控件下方。

```
    public ChannelDataHelepr(@NonNull Context context, @NonNull ChannelDataRefreshListenter channelDataRefreshListenter, @NonNull View showView) 
```
ChannelDataRefreshListenter 声明
```
    public static interface ChannelDataRefreshListenter {
		//此方法为刷新数据的方法，只有在频道发生变化的时候才会触发
        public void updateData();
		//此方法为，点击频道中的item时触发，可根据是否有更新选择，ViewPager切换的时机
        public void onChannelSeleted(boolean update, int posisiton);
    }
```
如我的Activity中这两个方法的实现

```

    @Override
    public void updateData() {
        loadData();
    }

    @Override
    public void onChannelSeleted(boolean update,final int posisiton) {
	    //如果频道没有改变，则立即调整，否则记录下需要调整的position，在数据更新后调整
        if(!update) {
            viewPager.setCurrentItem(posisiton);
        }else {
            needShowPosition=posisiton;
        }
    }


  
```
4.设置用于触发频道管理的View

```
 dataHelepr.setSwitchView(switch_view);
```

5.在每次数据加载完成后，过滤一遍，只显示订阅的频道。

```
  private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String data = getFromRaw();
                List<MyChannel> alldata = GsonUtil.jsonToBeanList(data, MyChannel.class);
                //过滤数据，如果有新的频道会自动订阅并保存到数据库。
                final List<MyChannel> showChannels = dataHelepr.getShowChannels(alldata);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myChannels.clear();
                        myChannels.addAll(showChannels);
                        adapter.notifyDataSetChanged();
                        if(needShowPosition!=-1){
                            viewPager.setCurrentItem(needShowPosition);
                            needShowPosition=-1;
                        }
                    }
                });

            }
        }).start();
    }
```
6.为了实现Adapter可以重排序，需要这样实现Adpater,关于原理可以查看我之前的一篇博客[ViewPager重排序与更新](http://blog.csdn.net/qq_22706515/article/details/51426770)

```
public class TitleFragmentAdapter extends FragmentPagerAdapter {
    List<MyChannel> channels;
    int id=1;
    Map<String,Integer> IdsMap=new HashMap<>();
    List<String> preIds=new ArrayList<>();
    public TitleFragmentAdapter(FragmentManager fm,@NonNull List<MyChannel> channels) {
        super(fm);
        this.channels=channels;
    }

    @Override
    public Fragment getItem(int position) {
        SimpleTitleFragment fragment=new SimpleTitleFragment();
        Bundle bundle=new Bundle();
        bundle.putString(SimpleTitleFragment.KEY_TITLE,channels.get(position).getTitle());
        bundle.putString(SimpleTitleFragment.KEY_URL,channels.get(position).getUrl());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return channels.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return channels.get(position).getTitle();
    }

    @Override
    public long getItemId(int position) {
        return IdsMap.get(getPageTitle(position));
    }

    @Override
    public int getItemPosition(Object object) {
        SimpleTitleFragment fragment= (SimpleTitleFragment) object;
        String title=fragment.getTitle();
        int preId = preIds.indexOf(fragment.getTitle());
        int newId=-1;
        int i=0;
        int size=getCount();
        for(;i<size;i++){
            if(getPageTitle(i).equals(fragment.getTitle())){
                newId=i;
                break;
            }
        }
        if(newId!=-1&&newId==preId){
            return POSITION_UNCHANGED;
        }
        if(newId!=-1){
            return newId;
        }
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        for(MyChannel info:channels){
            if(!IdsMap.containsKey(info.getTitle())){
                IdsMap.put(info.getTitle(),id++);
            }
        }
        super.notifyDataSetChanged();
        preIds.clear();
        int size=getCount();
        for(int i=0;i<size;i++){
            preIds.add((String) getPageTitle(i));
        }
    }
}
```
#下载
https://github.com/zhuguohui/ChannelMangerDemo
