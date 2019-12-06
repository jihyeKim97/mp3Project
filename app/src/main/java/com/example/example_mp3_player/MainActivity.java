package com.example.example_mp3_player;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.PaintDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.graphics.Color.GRAY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout line1;
    private ListView listViewMP3;
    private SeekBar seekBar;
    private TextView textViewPlayingState, textViewPlayingMusicName;
    private ImageButton btPlay, btPause, btMyList;

    public static SQLiteDatabase sqlDB;
    public static MyDBHelper myDBHelper;

    private MyAdapter adapter;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ArrayList<MyData> list = new ArrayList<>();
    public static String selectedMP3;

    public static final String MP3_PATH = Environment.getExternalStorageDirectory().getPath() + "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("TGA","onCreate()");
        setTitle("[ # MY MP3 PLAYER ]");

        listViewMP3 = findViewById(R.id.listViewMP3);
        seekBar = findViewById(R.id.seekBar);
        textViewPlayingState = findViewById(R.id.textViewPlayingState);
        textViewPlayingMusicName = findViewById(R.id.textViewPlayingMusicName);
        btPlay = findViewById(R.id.btPlay);
        btPause = findViewById(R.id.btPause);
        btMyList = findViewById(R.id.btMyList);
        line1 = findViewById(R.id.line1);

        myDBHelper = new MyDBHelper(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        getFileList();

        //어뎁터를 객체로 만들어 내가 만든 xml을 적용시킨다
        MyAdapter adapter = new MyAdapter(list, R.layout.listview_item, MainActivity.this);
        //만든 어뎁터를 listViewMP3라는 ListView에 적용시킨다
        listViewMP3.setAdapter(adapter);
        //ListView의 내가 원하는 곡을 눌렀을때 List의 위치값을 앨범아이디로 받아와 selectedMP3에 적용
        listViewMP3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMP3 = list.get(position).getAblumId();
            }
        });
        //리스트 뷰의 칸을 길게 눌렀을때 발생되는 이벤트
        listViewMP3.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //앨범 이름을  String selectedMP3을 넣는다(선택한 곡을 재생시킬수 있게 해주는 행동)
                selectedMP3 = list.get(position).getAblumId();
                //dailog.xml을 객체화 시킨다
                View dialogView = View.inflate(view.getContext(), R.layout.dialog, null);
                final EditText edtAlbum = dialogView.findViewById(R.id.edtAlbum);
                final EditText edtSinger = dialogView.findViewById(R.id.edtSinger);
                edtAlbum.setText(list.get(position).getAblumId());
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setTitle("[ MY PLAY LIST ]");
                dialog.setView(dialogView);
                dialog.setPositiveButton("♬내맘속에 저장♬", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            //모달창을 띄어서 내가 원하는 작곡자의 이름을 넣어 DB에 저장하고 중복이 되면 트라이 캐치로 막음
                            myDBHelper = new MyDBHelper(MainActivity.this);
                            sqlDB = myDBHelper.getWritableDatabase();
                            sqlDB.execSQL("INSERT INTO musicTBL VALUES('" + edtAlbum.getText().toString() + "','"
                                    + edtSinger.getText().toString() + "');");
                            sqlDB.close();
                            Toast.makeText(getApplicationContext(), "♬내 마음속에 저장♬", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            toastDisplay("중복된 음악입니다");
                        }
                    }
                });

                dialog.setNegativeButton("안해안해!", null);
                dialog.show();

                return true;
            }
        });
        btPlay.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btMyList.setOnClickListener(this);

        buttonSetting(true, false, 0);


    }

    //-------------------------------------------------------------------------------------------------------------------------------------

    //FileList
    //mp3와 mp4가 포함되어있는 이름들만 가져와 list에 담아 준다
    private void getFileList() {

        File[] fileList = new File(MP3_PATH).listFiles();

        for (File file : fileList) {
            String fileName = file.getName();
            String extendSting = fileName.substring(fileName.length() - 3);

            if (extendSting.equals("mp3") || extendSting.equals("mp4")) {

                list.add(new MyData(fileName, null));
            }
        }
    }//end of FileList

    //-------------------------------------------------------------------------------------------------------------------------------------

    //buttonSetting
    //버튼을 셋팅
    //버튼 셋팅을 하지 않으면 어떠한 오류가 날수 있으니 그것을 방지 하기위해서 사용
    private void buttonSetting(boolean b, boolean b1, int progress) {
        btPlay.setClickable(b);
        btPause.setClickable(b1);
        seekBar.setProgress(progress);
    }//end of buttonSetting

    //-------------------------------------------------------------------------------------------------------------------------------------

    //onClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btPlay:
                buttonPlayAction();
                break;
            case R.id.btPause:
                buttonPauseAction();
                break;
            case R.id.btMyList:
                buttonMyListAction();
                break;
        }
    }//end of onClick

    //-------------------------------------------------------------------------------------------------------------------------------------

    //btPlay Button EventAction
    private void buttonPlayAction() {
        try {
            mediaPlayer.setDataSource(MP3_PATH + selectedMP3);
            mediaPlayer.prepare();
            mediaPlayer.start();

            //버튼 셋팅
            buttonSetting(false, true, 0);
            textViewPlayingMusicName.setText(selectedMP3);
            toastDisplay("현재 듣고 계신 음악은 [ " + textViewPlayingMusicName.getText().toString() + " ] 입니다");

            //
            Thread thread = new Thread() {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

                            //재생도 하면서 메인스레드도 같이 돈다
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {

                                if (mediaPlayer == null) {
                                    return;
                                }

                                //노래재생에 관해서 상태를 표시해 주는 위젯들의 값을 바꾼다
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textViewPlayingState.setText(selectedMP3 + mediaPlayer.getDuration());
                                        //시크바의 최대 길이 만큼 표시 후 셋팅
                                        seekBar.setMax(mediaPlayer.getDuration());
                                    }
                                });
                                //시크바의 재생길이 만큼 움직인다(노래재생시간 = 시크바의 싱크)
                                while (mediaPlayer.isPlaying()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //미디어 플레이어의 현 시간 만큼 움직인다
                                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                            textViewPlayingState.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                                        }
                                    });
                                    //시스템의 과부하를 막기위해 0.15초동안 스레드가 정지한다
                        SystemClock.sleep(150);
                    }
                }
            };

            //스레드 시작
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }//end of buttonPlayAction

    //------------------------------------------------------------------------------------------------------------------------------------

    //btPause Button EventAction
    //버튼을 중지 하였을때 음악은 멈추고 다시 재생을 누르면 처음부터 재생
    private void buttonPauseAction() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        btPlay.setClickable(true);
        btPause.setClickable(false);
        textViewPlayingMusicName.setText("Running Music -  ");
        seekBar.setProgress(0);
        toastDisplay("현재 음악을 중지 하였습니다.");
    }//end of buttonPauseAction

    //-------------------------------------------------------------------------------------------------------------------------------------

    //btMyList Button EventAction
    //찜 이미지 버튼을 눌렀을 때 인텐트로 화면 전환
    private void buttonMyListAction() {
        Intent intent = new Intent(MainActivity.this, SubActivity.class);
        startActivity(intent);

    }//end of buttonMyListAction

    // -------------------------------------------------------------------------------------------------------------------------------------

    //Toast message
    //토스트 메세지
    private void toastDisplay(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }//end of Toast message

}
