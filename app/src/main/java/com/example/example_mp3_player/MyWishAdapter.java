package com.example.example_mp3_player;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.example_mp3_player.MainActivity.myDBHelper;
import static com.example.example_mp3_player.MainActivity.selectedMP3;
import static com.example.example_mp3_player.MainActivity.sqlDB;

//찜 목록에서 사용되는 어뎁터
public class MyWishAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<MyData> list;
    // 레이아웃의 아이디
    private int layoutID;
    private Context context;

    public MyWishAdapter(ArrayList<MyData> list, int layoutID, Context context) {
        this.list = list;
        this.layoutID = layoutID;
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //객체를 만들어서 진짜 만드는것
        if (convertView == null) {
            convertView = inflater.inflate(layoutID, null);
        }
        TextView txtAlbum = convertView.findViewById(R.id.txtAlbum);
        TextView txtSinger = convertView.findViewById(R.id.txtSinger);

        txtAlbum.setText(list.get(position).getAblumId());
        txtSinger.setText(list.get(position).getSingerId());

        txtAlbum.setTag(position);

        return convertView;
    }
}

