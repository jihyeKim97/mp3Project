package com.example.example_mp3_player;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static android.graphics.Color.GRAY;
import static com.example.example_mp3_player.MainActivity.selectedMP3;

//리스트 뷰를 사용하려면 어뎁터를 사용해야 한다
public class MyAdapter extends BaseAdapter {
    // 레이아웃의 아이디를 가지고 인플레이터를 이용해서 객체만듬
    private LayoutInflater inflater;
    //데이터
    private ArrayList<MyData> list;
    // 레이아웃의 아이디
    private int layoutID;
    //어뎁터가 어디서 사용될지 알아야 한다
    private Context context;

    //생성자를 만들때 inflater는 빼고 생성자를 만든 다음 따로 inflater를 작성
    public MyAdapter(ArrayList<MyData> list, int layoutID, Context context) {
        this.list = list;
        this.layoutID = layoutID;
        this.context = context;
        //고정
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //리스트에 리스트의 사이즈를 반환
    @Override
    public int getCount() {
        return list.size();
    }

    //리스트 안에 리스트의 위치값을 구성함
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    //리스트의 각위치
    @Override
    public long getItemId(int position) {
        return position;
    }

    //메인 창에 모든것을 값을 적용시켜 뿌려주는것
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //객체를 만들어서 진짜 만드는것
        if (convertView == null) {
            convertView = inflater.inflate(layoutID, null);
        }
        TextView txtAlbum = convertView.findViewById(R.id.txtAlbum);
        TextView txtSinger = convertView.findViewById(R.id.txtSinger);

        //txtAlbum에 각 아이디를 위치값에 넣어준다
        txtAlbum.setText(list.get(position).getAblumId());
        txtSinger.setText(list.get(position).getSingerId());

        //txtAlbum만 클릭해도 한칸 전체가 잡히는 함수
        txtAlbum.setTag(position);

        return convertView;
    }
}

