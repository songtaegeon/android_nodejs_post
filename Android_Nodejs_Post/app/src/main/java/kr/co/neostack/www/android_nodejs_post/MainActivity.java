package kr.co.neostack.www.android_nodejs_post;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView tvData;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tvData = (TextView)findViewById(R.id.textView);
        btn = (Button)findViewById(R.id.httpTest);

        //button click시 listener
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AsyncTask 실행 배열 첫번째 인자로 http url 전달
                new JSONTask().execute("http://admin.neostack.co.kr:8081/post");
            }
        });
    }

    //시작파라미터, 진행상태, 서버로받은 데이터 리턴할때의 type
    /*
    1. Params: background 작업을 할 때 필요한 데이터 타입정의
    2. Progress: background 작업 도중 진행하는데 사용될 데이터 타입정의
    3. Result: 작업 결과로서 리턴받을 데이터 타입정의
     */
    public class JSONTask extends AsyncTask<String, String, String>{

        @Override
        //background으로 돌아가며 .... 은 파라미터가 배열처럼 넘어옴
        //첫번째 인자로 http://admin.neostack.co.kr:8081/post  를 urls[0] 에 저장
        protected String doInBackground(String... urls) {
            try {
                //jsonObject 객체생성
                JSONObject jsonObject = new JSONObject();
                //key, value 방식으로 값 저장
                jsonObject.accumulate("user_id", "androidTest");
                jsonObject.accumulate("name", "yun");

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //url[0] 에 http://admin.neostack.co.kr:8081/post 저장
                    URL url = new URL(urls[0]);
                    //url.opneConnection()을 이용해 http connection 열고 호출
                    con = (HttpURLConnection) url.openConnection();
                    //post or get 방식 설정
                    con.setRequestMethod("POST");
                    //컨트롤 캐쉬 설정
                    con.setRequestProperty("Cache-Control", "no-cache");
                    //타입설정 (application/json) 형식으로 전송 (Request Body 전달시 application/json로 서버에 전달
                    con.setRequestProperty("Content-Type", "application/json");
                    //서버 response data를 html로 받음
                    con.setRequestProperty("Accept", "text/html");
                    //outputStream으로 데이터 넘겨주겠다고 설정
                    con.setDoOutput(true);
                    //inputStream으로 데이터 읽겠다고 설정
                    con.setDoInput(true);
                    //http 연결
                    con.connect();

                    //request body에 data를 담기위해 OutputStream 객체생성
                    OutputStream outStream = con.getOutputStream();
                    //버퍼생성 및 버퍼에 값 입력
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    //버퍼닫기
                    writer.close();

                    //서버로부터 데이터 받기
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    //서버로부터 받은 값을 리턴해줌. ok 값 return
                    return buffer.toString();

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                //가장 자세한 예외처리 제공
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //서버로부터 받은 값을 출력
            tvData.setText(result);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
