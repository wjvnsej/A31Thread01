package com.kosmo.a31thread01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "KOSMO61";

    TextView textView1;
    Button button1;
    ProgressHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = findViewById(R.id.textView1);
        button1 = findViewById(R.id.button1);

        //앱이 실행되면 ProgressHandler 객체를 생성한다.
        handler = new ProgressHandler();
    }
    public void onBtn1Clicked(View v) {
        //버튼을 눌렀을 때 두번 누를 수 없도록 비활성화 시켜줌
        button1.setEnabled(false);
        /*
        쓰레드의 메인 메소드는 run()이지만, start()를 통해 간접적으로
        호출해야 쓰레드가 생성된다. 만약 run()을 호출하면 단순한 실행만
        되고 쓰레드는 생성되지 않는다.
         */
        RequestThread thread = new RequestThread();
        thread.start();
    }

    class RequestThread extends Thread {

        @Override
        public void run() {
            for(int i = 0; i < 20; i++) {
                Log.d(TAG, "Request Thread.. " + i);

                /*
                쓰데드에서 메인쓰레드의 UI(위젯)으로 접근은 불가능함
                    : 해당 쓰레드에서 메인쓰레드의 UI를 접근하면 ANR 이
                    발생된다. 이때 앱은 강제적으로 종료된다. 외부 쓰레드의
                    접근이 메인쓰레드의 동작에 영항을 미칠 수 있기 때문에
                    접근을 제한하게 된다.

                ANR 이란?
                    : ANR 은 Application Not Responding 의 약자로 그대로
                    해석해보면 의미를 쉽게 파악할 수 있다.
                    '애플리케이션이 응답하지 않는다.' 인 것이다.
                    이 에러의 원인은 Main Thread(UI Thread)가
                    일정 시간 어떤 Task 에 잡혀 있으면 발생하게 된다.
                 */
                //textView1.setText("Request Thread.. " + i);  --> 오류발생(앱 종료)

                //핸들러 객체로 전달 할 메세지(데이터) 작성
                Message msg = handler.obtainMessage();

                //핸들러로 전달할 메세지는 번들객체를 통해 저장한다.
                Bundle bundle = new Bundle();
                bundle.putString("data1", "Request Thread... " + i);
                bundle.putString("data2", String.valueOf(i));
                //저장한 데이터를 세팅한 후 핸들러로 보내준다.
                msg.setData(bundle);
                handler.sendMessage(msg);

                //쓰레드의 반복을 1초씩 멈추기 위해 sleep()메소드 호출
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }//// RequestThread End

    /*
    핸들러 클래스 생성
        : 메인 쓰레드에서 생성한 UI(위젯)는 다른 쓰레드에서 접근할 수
        없으므로, 핸들러 객체를 사용해서 간접적으로 접근한다. 메세지를
        전달하여 해당 위젯에 접근하게 된다.
     */
    class ProgressHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            //번들객체를 통해 전달된 메세지(데이터)를 확인한다.
            Bundle bundle = msg.getData();
            String data1 = bundle.getString("data1");
            String data2 = bundle.getString("data2");

            //핸들러 객체 내에서 UI를 접근하여 텍스트를 설정한다.
            textView1.setText(data1);

            /*
            20번 반복한 후 버튼과 텍스트뷰를 원상태로 설정한다.
            쓰레드의 동작이 끝나기 전에는 버튼을 누를 수 없도록
            비활성화 상태를 유지한다.
             */
            if(data2.equals("19")) {
                textView1.setText("쓰레드 테스트");
                button1.setEnabled(true); //활성화
            }
            else {
                button1.setEnabled(false); //비활성화
            }
        }
    }//// ProgressHandler End
}
