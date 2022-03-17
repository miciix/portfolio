package com.csc301.students.BookBarter.SearchAds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.csc301.students.BookBarter.R;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

// Dazhi Chen: Method used in Search function
public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private LinkedList<Data> mData;

    public MyAdapter() {
    }

    public MyAdapter(LinkedList<Data> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_home_item, parent, false);
            holder = new ViewHolder();
            holder.img_icon = convertView.findViewById(R.id.img_icon);
            holder.txt_content = (TextView) convertView.findViewById(R.id.txt_content);
            holder.txt_price = (TextView) convertView.findViewById(R.id.txt_price);
            holder.txt_description=(TextView) convertView.findViewById(R.id.txt_description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        /*Picasso.with(mContext)
                .load(mData.get(position).getImage()) //load img from url
                .placeholder(R.mipmap.ic_launcher)//set specific img for loading img
                .error(R.mipmap.no_img) //set specific img for failed loaded img
                .fit()
                .into(holder.img_icon);//load img to ImageView
         */

        Glide.with(mContext)
                .asBitmap()
                .load(mData.get(position).getImage())
                .error(R.mipmap.no_img)
                .into(holder.img_icon);

        holder.txt_content.setText(mData.get(position).getTitle());
        holder.txt_price.setText(mData.get(position).getPrice());
        holder.txt_description.setText(mData.get(position).getDescription());
        return convertView;
    }

    //add data
    public void add(Data data) {
        if (mData == null) {
            mData = new LinkedList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    //add data to specific postition
    public void add(int position, Data data) {
        if (mData == null) {
            mData = new LinkedList<>();
        }
        mData.add(position, data);
        notifyDataSetChanged();
    }

    public void remove(Data data) {
        if (mData != null) {
            mData.remove(data);
        }
        notifyDataSetChanged();
    }

    public Data get(int position) {
        if (mData != null) {
            return mData.get(position);
        }
        return null;
    }

    public void remove(int position) {
        if (mData != null) {
            mData.remove(position);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
        }
        notifyDataSetChanged();
    }

    private class ViewHolder {
        CircleImageView img_icon;
        TextView txt_content;
        TextView txt_price;
        TextView txt_description;
    }

}