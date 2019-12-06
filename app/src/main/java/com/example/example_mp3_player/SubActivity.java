package com.example.example_mp3_player;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static com.example.example_mp3_player.MainActivity.myDBHelper;
import static com.example.example_mp3_player.MainActivity.sqlDB;

public class SubActivity extends AppCompatActivity implements View.OnClickListener {

    //인텐트로 화면전환을 한뒤 찜목록 안에서 여러가지 적용하고 사용하는 클래스
    ImageButton btMyList;
    ListView myListView;

    private ArrayList<MyData> list = new ArrayList<>();
    private MyWishAdapter myWishAdapterdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wishlistview);
        setTitle("[ # MY PLAY LIST ]");

        btMyList = findViewById(R.id.btMyList);
        myListView = findViewById(R.id.myListView);
        //디비에있는데이터를 가져와서 리스트에 넣는 함수
        insertListData();
        btMyList.setOnClickListener(this);
        //어뎁터를 객체화 시킨다
        myWishAdapterdapter = new MyWishAdapter(list,R.layout.wish_listview,this);
        myListView.setAdapter(myWishAdapterdapter);

        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                myDBHelper = new MyDBHelper(parent.getContext());
                sqlDB = myDBHelper.getWritableDatabase();
                //내가 찜한 목록중 선택한 리스트를 롱 클릭을 하면 지워지는 기능
                sqlDB.execSQL("DELETE FROM musicTBL WHERE malbum ='" + list.get(position).getAblumId() + "';");
                sqlDB.close();
                //변한 값을 다시 적용시키기 위해 디비에서 모든 데이터를 가져오는 함수를 호출
                insertListData();
                //어뎁터에게 데이터가 변한 것을 알려준다
                //알려주면 그 변환 데이터로 다시 화면 구성
                myWishAdapterdapter.notifyDataSetChanged();
                return false;
            }
        });

    }

    //디비에있는데이터를 가져와서 리스트에 넣는 함수
    public void insertListData() {
        myDBHelper = new MyDBHelper(this);
        sqlDB = myDBHelper.getReadableDatabase();
        Cursor cursor;//ResultSet과 같은 기능

        //쿼리문을 실행한 데이터들을 전부 가져와서 저장
        cursor = sqlDB.rawQuery("SELECT * FROM musicTBL", null);
        //리스트를 한번 초기화 시켜준다 = 초기화를 시켜주지 않으면 값이 계속 쌓인다
        list.removeAll(list);
        //반복문으로 값이 있으면 디비에있는 컬럼 0번째 1번째를 가져와서 각 객체를 만들어서 리스트로 저장
        while(cursor.moveToNext()) {
            String albumName = cursor.getString(0);
            String singerName = cursor.getString(1);
            list.add(new MyData(albumName, singerName));
        }
        //자원을 닫아준다
        cursor.close();
        sqlDB.close();
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        setResult(1001,intent);
        //자기를 죽이면서 원위치로 돌아간다
        finish();
    }
}
