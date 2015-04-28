package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by Mr_zhang on 2015/4/26.
 */
public abstract class CommonAdapter extends BaseAdapter {
    protected Context mContext;
    protected JSONArray mDatas;
    protected LayoutInflater mInflater;

    public CommonAdapter(Context context){
        this.mContext = context;
        this.mDatas = new JSONArray();
        mInflater = LayoutInflater.from(context);
    };


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    public void addData(JSONObject mData){
        if(mData != null){
            mDatas.add(mData);
            notifyDataSetChanged();
        }
    }

    public void addDatas(JSONArray datas){
        if(datas.size() > 0){
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void clearDatas(){
        if(mDatas.size() > 0){
            mDatas.clear();
            notifyDataSetChanged();
        }
    }
}
