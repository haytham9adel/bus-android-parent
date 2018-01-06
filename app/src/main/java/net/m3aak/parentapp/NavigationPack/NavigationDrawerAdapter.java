package net.m3aak.parentapp.NavigationPack;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.m3aak.parentapp.R;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

public class NavigationDrawerAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private static String[] titles = null;
    private static int[] list_img = null;

    public NavigationDrawerAdapter(Context context, String[] titles, int[] list_img) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.titles = titles;
        this.list_img = list_img;
    }

    @Override
    public int getCount() {

        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title,count;
        ImageView img_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            count = (TextView) itemView.findViewById(R.id.count);
            img_item = (ImageView) itemView.findViewById(R.id.img_item);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        try {
            MyViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater li = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.single_list_layout, null);
                viewHolder = new MyViewHolder(v);
                v.setTag(viewHolder);
            } else {
                viewHolder = (MyViewHolder) v.getTag();
            }
            if (position == 2) {
                if (!Utility.isStringNullOrBlank(Utility.getSharedPreferences(context, ConstantKeys.COUNT_CHAT_NOTI))) {
                    viewHolder.count.setText(Utility.getSharedPreferences(context, ConstantKeys.COUNT_CHAT_NOTI));
                    Log.e("ChatCount", "" + Utility.getSharedPreferences(context, ConstantKeys.COUNT_CHAT_NOTI));
                    viewHolder.count.setVisibility(View.VISIBLE);
                }
            } else if (position == 3) {
                if (!Utility.isStringNullOrBlank(Utility.getSharedPreferences(context, ConstantKeys.COUNT_OTHER_NOTI))) {
                    Log.e("NotiCount", "" + Utility.getSharedPreferences(context, ConstantKeys.COUNT_OTHER_NOTI));
                    if (!Utility.getSharedPreferences(context, ConstantKeys.COUNT_OTHER_NOTI).equals("0")) {
                        viewHolder.count.setText(Utility.getSharedPreferences(context, ConstantKeys.COUNT_OTHER_NOTI));
                        viewHolder.count.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                viewHolder.count.setVisibility(View.GONE);
            }
            viewHolder.title.setText(titles[position]);
            viewHolder.img_item.setImageResource(list_img[position]);
        }catch (Exception e) {e.printStackTrace();}

        return v;
    }


//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        String current = titles.get(position);
//        holder.img_item.setImageResource(icons.get(position));
//
//
//
//    }

}