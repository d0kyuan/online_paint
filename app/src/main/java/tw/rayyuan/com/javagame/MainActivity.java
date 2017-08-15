package tw.rayyuan.com.javagame;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static java.security.AccessController.getContext;
import android.provider.Settings.Secure;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> list_record = new ArrayList<>();
    public static Handler mHandler = new Handler();
    ListView TextView01;    // 用來顯示文字訊息
    EditText EditText01;    // 文字方塊
    EditText EditText02;    // 文字方塊
    String tmp;                // 暫存文字訊息
    Socket clientSocket;    // 客戶端socket
    TextView sendto_text;
    int serverPort = 5050;
    String URL = "120.96.74.33";
    String name = "旅客";
    int action = 0;
    listadapter adapter = null;
    DataOutputStream a;
    String name1 ;
    String name2;
    int guess;
    AlertDialog.Builder builder;
     EditText input;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 從資源檔裡取得位址後強制轉型成文字方塊
        TextView01 = (ListView) findViewById(R.id.TextView01);
        adapter = new listadapter(this);
        TextView01.setAdapter(adapter);
        EditText02 = (EditText) findViewById(R.id.message_text);
        name +=  Secure.getString(MainActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        setTitle("您的大名 ："+name);
        // 以新的執行緒來讀取資料
        Thread t = new Thread(readData);

        // 啟動執行緒
        t.start();
        Log.d("123",t.getState()+"");
        // 從資源檔裡取得位址後強制轉型成按鈕
        Button button1 = (Button) findViewById(R.id.sendbtn);

        // 設定按鈕的事件
        button1.setOnClickListener(new Button.OnClickListener() {
            // 當按下按鈕的時候觸發以下的方法
            public void onClick(View v) {
                // 如果已連接則
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(!TextUtils.isEmpty(EditText02.getText())){
                            if(clientSocket.isConnected()) {


                                BufferedWriter bw;

                                try {
                                    // 取得網路輸出串流
                                    // 取得網路輸出串流
                                     bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                                    // 寫入訊息
                                   // bw.write(name+":"+EditText02.getText()+"\n");
                                    JSONObject job = new JSONObject();
                                    job.put("name",name);
                                    job.put("action",action+"");
                                    job.put("message",EditText02.getText().toString());
                                    job.put("to",name);
                                    job.put("from",name);
                                    bw.write(job+"\n\r");
                                    // 立即發送
                                    bw.flush();


                                    //c.close();
                                   // bw.close();
                                } catch (Exception e) {
                                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                // 將文字方塊清空
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {

                                        EditText02.setText("");

                                    }
                                });

                            }
                        }

                    }
                }).start();


                }

        });
        sendto_text = (TextView) findViewById(R.id.textView4);
        sendto_text.setText("聊天");
        ImageButton drawer = (ImageButton) findViewById(R.id.button3);
        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("123");
                PopupMenu popup = new PopupMenu(MainActivity.this,view);
                popup.getMenuInflater().inflate(R.menu.name, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.allp:
                                action = 0;
                                sendto_text.setText("聊天");
                                break;

                            case R.id.onep:
                                action = 1;
                                sendto_text.setText("加入遊戲");

                                break;
                            case R.id.guess:
                                action = 2;
                                sendto_text.setText("猜數字");
                                 builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("猜數字");
                                 input = new EditText(MainActivity.this);
                                input.setInputType(InputType.TYPE_CLASS_TEXT );
                                builder.setView(input);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(clientSocket.isConnected()) {


                                            BufferedWriter bw;

                                            try {
                                                // 取得網路輸出串流
                                                // 取得網路輸出串流
                                                bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

                                                JSONObject job = new JSONObject();
                                                job.put("name",name);
                                                job.put("action",action+"");
                                                job.put("message",input.getText().toString());
                                                job.put("to",name);
                                                job.put("from",name);
                                                bw.write(job+"\n\r");
                                                // 立即發送
                                                bw.flush();
                                            } catch (Exception e) {
                                                //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                                break;
                            case R.id.addvoi:
                                action = 5;
                                sendto_text.setText("發起投票");

                                break;
                            case R.id.leave:
                                action = 6;
                                sendto_text.setText("離開遊戲");

                                break;
                            case R.id.voi:
                                action = 3;
                                sendto_text.setText("投票");

                                break;
                            case R.id.list:
                                action=4;
                                sendto_text.setText("遊戲列表");

                                break;
                            case R.id.name:
                                builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("更改名稱");
                                input = new EditText(MainActivity.this);
                                input.setInputType(InputType.TYPE_CLASS_TEXT );
                                builder.setView(input);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        name = input.getText().toString();
                                        Toast.makeText(MainActivity.this,"名稱設定為" +name,Toast.LENGTH_SHORT).show();
                                        setTitle("您的大名 ："+name);
                                        if(clientSocket.isConnected()) {


                                            BufferedWriter bw;

                                            try {
                                                // 取得網路輸出串流
                                                // 取得網路輸出串流
                                                bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

                                                // 寫入訊息
                                                bw.write(name + ":/n "+name);

                                                // 立即發送
                                                bw.flush();
                                            } catch (Exception e) {
                                               //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                                break;
                        }

                        return  true;
                    }
                });
                popup.show();
            }

        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // 顯示更新訊息
    private Runnable updateText = new Runnable() {
        public void run() {
            // 加入新訊息並換行
            //TextView01.append(tmp + "\n");
        }
    };
    ProgressDialog login_dialog;
    private Runnable reconnt = new Runnable() {
        @Override
        public void run() {
            try {
                serverIp = InetAddress.getByName(URL);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            Boolean recon = true;
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {

                     login_dialog = ProgressDialog.show(MainActivity.this,
                            "重新連結", "衝新連結中.....請稍候....", true);
                }
            });
                    while (recon) {

                        int serverPort = 5050;
                        try {
                            clientSocket = new Socket(serverIp, serverPort);
                            recon = false;
                            login_dialog.cancel();
                            Thread t = new Thread(readData);

                            // 啟動執行緒
                            t.start();
                        }catch (Exception e){

                        }

                    }



        }
    };

    InetAddress serverIp;
    // 取得網路資料
    private Runnable readData = new Runnable() {
        public void run() {
            // server端的IP


            try {
                // 以內定(本機電腦端)IP為Server端
                serverIp = InetAddress.getByName(URL);
                int serverPort = 5050;
                clientSocket = new Socket(serverIp, serverPort);
                Log.d("ss","asd");
                // 取得網路輸入串流
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));
                Log.i("asd",clientSocket.isConnected()+"");
                // 當連線後
                while (clientSocket.isConnected()) {
                    // 取得網路訊息
                    tmp = br.readLine();



                    if(br==null){
                        Toast.makeText(getApplication(),"伺服器斷線",Toast.LENGTH_SHORT).show();
                        break;
                    }else if(!clientSocket.isConnected()) {
                        Toast.makeText(getApplication(),"伺服器斷線",Toast.LENGTH_SHORT).show();
                        break;
                    }else if(TextUtils.isEmpty(tmp)){
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                // 以新的執行緒來讀取資料
                                Thread t = new Thread(reconnt);

                                // 啟動執行緒
                                t.start();
                                //Toast.makeText(getApplication(),"伺服器斷線",Toast.LENGTH_SHORT).show();
                            }
                        });

                        break;
                    }
                    Log.i("123","123");
                    MainActivity.this.runOnUiThread(new Runnable() {

                        public void run() {
                           // Log.d("13",tmp);

                                list_record.add(tmp);
                                adapter.notifyDataSetChanged();


                            TextView01.setAdapter(adapter);


                            //adapter.notify();
                        }
                    });
                }

            } catch (final IOException e) {
                MainActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Thread t = new Thread(reconnt);

                        // 啟動執行緒
                        t.start();
                        Toast.makeText(getApplication(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }
        }


    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        BufferedWriter bw;

        try {
            // 取得網路輸出串流
            bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            // 寫入訊息
            bw.write("@leave@"+EditText01.getText() + "離開了");

            // 立即發送
            bw.flush();
        } catch (IOException e) {

        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    @Override
    protected void onDestroy(){
        super.onStop();
        BufferedWriter bw;

        try {
            // 取得網路輸出串流
            bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            // 寫入訊息
            bw.write("@leave@"+EditText01.getText() + "離開了");

            // 立即發送
            bw.flush();
        } catch (IOException e) {

        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    public class listadapter extends BaseAdapter {
        private LayoutInflater myInflater;
        public listadapter(Context context){
            myInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return list_record.size();
        }

        @Override
        public Object getItem(int i) {
            return list_record.get(i);
        }

        @Override
        public long getItemId(int i) {
            return list_record.indexOf(getItem(i));
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = myInflater.inflate(R.layout.fuck, null);
            TextView jjjj = (TextView) view.findViewById(R.id.textView44);
          // jjjj.setBackgroundColor(Color.RED);
            try {
                if(new JSONObject(list_record.get(i)).get("name").toString().equals(name)){

                    jjjj.setGravity(5);
                    jjjj.setText(new JSONObject(list_record.get(i)).get("message").toString());
                    //jjjj.setBackgroundColor(255);
                }else{

                    jjjj.setGravity(0);
                    jjjj.setText(new JSONObject(list_record.get(i)).get("name")+":"+new JSONObject(list_record.get(i)).get("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return view;
        }
    }
}
