package com.example.example_mp3_player;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper {

    //핸드폰 내부에서 쓰는 디비
    //생성자에서 데이터 베이스를 만든다
    public MyDBHelper(@Nullable Context context) {
        super(context, "musicDB", null, 1);
    }

    // = 콜백함수 생성자가 만들어지면 자동으로 onCreate가 만들어진다
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE  musicTBL ( malbum CHAR(20) PRIMARY KEY, msinger CHAR);");
    }

    //무조건 오버라이드를 해줘야 하고 테이블을 지우고 다시 만든다(테이블 초기화)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS musicTBL");
        onCreate(db);

    }
}
