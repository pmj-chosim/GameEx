package shh.room.gameex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;


//게임내에 보여지게될 모든 유닛의 정보를 저장하기 위한 최상위 클래스
class Unit {
    int x,y;
    int ax,ay;
    Bitmap image;
    int health;
}

public class MainActivity extends Activity {

    int score = 0;    //게임 스코어
    ArrayList total;  //모든 모기를 저장하기 위한 변수
    int count = 100; //유닛에 개수
    Bitmap image;
    MyView m;
    int x, y;   //터치한 위치 좌표값 저장을 위해 선언
    int ax = 5; //x 축에 대한 가속도
    int ay = 5; //y 축에 대한 가속도

    int width = 480;   //현재 핸드폰 가로 해상도
    int height = 800;  //현재 핸드폰 세로 해상도


    class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            //UI Thread에 부탁할 화면 갱신 작업을 코딩
            //모든 Unit 좌표값 갱신
            for (int i = 0 ; i<count; i++) {

                Unit u = (Unit)total.get(i); //i번째 유닛 가져오기

                if ( u.x > width || u.x < 0) {   //화면 좌측 또는 우측을 벗어난 상황
                    u.ax = -u.ax;
                }
                if ( u.y > height || u.y < 0) { //화면 위쪽 또는 아래쪽으로 벗어난 상황
                    u.ay = -u.ay;
                }
                u.x = u.x + u.ax;
                u.y = u.y + u.ay;
            }

            m.invalidate();
            sendEmptyMessageDelayed(0, 20);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //화면이 터치되었을 때 호출되는 콜백메소드

        //유닛 선택 판정 처리
        int x2 = (int)event.getX();
        int y2 = (int)event.getY();

        for (int i = 0 ; i<count; i++) {
            Unit u = (Unit)total.get(i);
            //유닛의 좌표값과 터치된 위치에 좌표값 연결하여 직각삼각형 구성 후
            //빗변 길이 구해서 빗변의 길이가 유닛의 반지름보다 작다면 선택된걸로 판정
            double b =  Math.sqrt(Math.pow(x2-u.x,2) + Math.pow( y2-u.y , 2));
            if ( b < 150 ) {
                u.health = 0; 
                total.remove(i);  //유닛 사망처리 , total에서 제거
                count--;
                score = score + 1;
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    class MyView extends View {
        public MyView(Context context) {
            super(context);
            //res/drawable 에서 이미지 Bitmap type으로 가져오기
            image = BitmapFactory.decodeResource(getResources(),
                    R.drawable.mo);

            MyHandler handler = new MyHandler();
            handler.sendEmptyMessage(0);

            //모기 생성
            for (int i = 0 ; i<count ; i++) {
                Unit u = new Unit();
                u.x = (int)(Math.random()*width) + 1;
                u.y = (int)(Math.random()*height) + 1;
                u.ax = (int)(Math.random()*10) + 1;
                u.ay = (int)(Math.random()*10) + 1;
                u.image = image;
                u.health = 500;
                total.add(u);  //Unit 저장
            }
        }
        @Override
        protected void onDraw(Canvas canvas) {
            Paint p = new Paint();
            p.setColor(Color.BLUE);
            p.setTextSize(70);
            //모든 Unit 그리기
            for (int i = 0 ; i<count; i++) {
                Unit u = (Unit)total.get(i);
                if ( u.health > 0 ) { //피가 있는 살아있는 유닛만 그리기
                    canvas.drawBitmap(u.image, u.x, u.y, null);
                }
            }
            canvas.drawText("점수 : "+score, width-400, 150, p);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //현재 핸드폰 해상도 가져오기
        WindowManager wm = (WindowManager)this.getSystemService(
                Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();

        //타이틀 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Notification Bar 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        total = new ArrayList();

        m = new MyView(this);
        m.setBackgroundResource(R.drawable.back);
        setContentView( m );

    }
}








