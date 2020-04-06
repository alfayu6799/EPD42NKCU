package home.com.epd42nkcu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();  //debug
    private String err_msg;
    private JSONObject jsonObject;

    public ImageView advertisement;

    private TextView month,date,week;
    private TextView patientName, patientBedNo, patientAge;
    private TextView lastUpdate;
    private TextView announcement;
    private TextView lang_chinese;

    private Button ioCancel, ioSend;
    private EditText ioInput, ioOutput;

    private LinearLayout care_1,care_2, care_3, care_4, care_5, care_6;
    private RelativeLayout careDialogLayout, care_0;

    private LinearLayout fullNkcuLayout;
    private RelativeLayout careRecordLayout;

    private CareRecordDialog RecordDialog; //Dialog Customer
    private String CareStr = "";

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //title隱藏
        setContentView(R.layout.activity_main);

        initView();

        getDateInfo(); //get時間

        reflashLayout.run(); //刷頁面
    }

    private void getPatientInfo() { //日後請改為NKCU's API fxn
        new Thread(){
            public void run(){
                String jsonFullURL = "http://10.58.177.12" + "/EPD42DEMO/api/GetDevicePairInfo";
                Log.d(TAG, "GetDevicePairInfo_json_FullURL : " + jsonFullURL);

                JSONObject post_jsonObject = new JSONObject();

                try {
                    post_jsonObject.accumulate("apiName", "getDevicePairInfo");
                    post_jsonObject.accumulate("DeviceIDKey", "epd42devicekey");
                    post_jsonObject.accumulate("hospitalCode", "landmark");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "GetDevicePairInfo_Url_json POST = " + post_jsonObject.toString());

                String result;
                result = Utils.excutePostJson(jsonFullURL, post_jsonObject);
                Log.d(TAG, "GetDevicePairInfo_Url_json Result = " + result);

                err_msg = "";
                if (result != null && !("".equalsIgnoreCase(result))) {
                    try {
                        jsonObject = new JSONObject(result);
                        String rtn_result = jsonObject.getString("result");
                        String rtn_message = "";
                        if(jsonObject.has("message")){
                            rtn_message = jsonObject.getString("message");
                        }

                        if (rtn_result.equalsIgnoreCase("Y") || rtn_result.equalsIgnoreCase("N") ) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String status = jsonObject.getString("status");
                                        if(status.equalsIgnoreCase("9")){
                                            if (jsonObject.has("data")) {
                                                String stationNo = "";
                                                String bedNo = "";
                                                String accountNo = "";
                                                String roomNo = "";

                                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                                JSONObject dataobj = dataArray.getJSONObject(0);

                                                if (dataobj.has("stationNo")) {
                                                    stationNo = dataobj.getString("stationNo");
                                                }
                                                if (dataobj.has("bedNo")) {
                                                    bedNo = dataobj.getString("bedNo");
                                                }
                                                if (dataobj.has("accountNo")) {
                                                    accountNo = dataobj.getString("accountNo");
                                                }
                                                if (dataobj.has("roomNo")) {
                                                    roomNo = dataobj.getString("roomNo");
                                                }
                                                //將以上收集到的資料填入相對應的TextView or ImageView物件
                                                patientBedNo.setText(bedNo);
                                            }else{
                                                Log.d(TAG, "GetDevicePairInfo_Url_json 失敗 : jsonObject data error");
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{

                }
            }
        }.start();
    }

    private void getDateInfo() {
        Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);           //年
        int weekday = cal.get(Calendar.DAY_OF_WEEK);  //星期
        String thisWeek = getWeek(weekday);           //今天星期幾
        String thisYear = Integer.toString(mYear);    //今年
        week.setText(thisYear + " " + thisWeek);      //xxxx xxxx
        SimpleDateFormat df = new SimpleDateFormat("MM/dd");  //月/日
        String str_today = df.format(cal.getTime());   //今天(含月)
        String monthandday[] = str_today.split("/"); //以"/"分割
        String thisMonth = monthandday[0]; //月
        String thisdate = monthandday[1];  //日
        month.setText(thisMonth + "\n" + "月");
        date.setText(thisdate);
        String nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date());
        lastUpdate.setText("前次更新時間: " + nowDate);  //前次更新時間
    }

    @Nullable
    private String getWeek(int weekday) {
        String thisweek = null;
        if (weekday == 1)
        {
            thisweek = getString(R.string.sunday);
        }
        if (weekday == 2)
        {
            thisweek = getString(R.string.monday);
        }
        if (weekday == 3)
        {
            thisweek = getString(R.string.tuesday);
        }
        if (weekday == 4)
        {
            thisweek = getString(R.string.wednesday);
        }
        if (weekday == 5)
        {
            thisweek = getString(R.string.thursday);
        }
        if (weekday == 6)
        {
            thisweek = getString(R.string.friday);
        }
        if (weekday == 7)
        {
            thisweek = getString(R.string.saturday);
        }
        return thisweek;
    }

    private void initView() {
        month = (TextView) findViewById(R.id.tv_p_month); //月
        date = (TextView) findViewById(R.id.tv_p_date);   //日
        week = (TextView) findViewById(R.id.tv_yyweek);   //星期

        patientName = (TextView) findViewById(R.id.tv_p_name);    //病患姓名
        patientBedNo = (TextView) findViewById(R.id.tv_p_bed_no); //病床編號
        patientAge = (TextView) findViewById(R.id.tv_p_age);      //病患年紀

        lastUpdate = (TextView) findViewById(R.id.tv_p_updatetime); //最後更新日期

        care_1 = (LinearLayout) findViewById(R.id.ly_care_1);
        care_1.setOnClickListener(this);
        care_2 = (LinearLayout) findViewById(R.id.ly_care_2);
        care_2.setOnClickListener(this);
        care_3 = (LinearLayout) findViewById(R.id.ly_care_3);
        care_3.setOnClickListener(this);
        care_4 = (LinearLayout) findViewById(R.id.ly_care_4);
        care_4.setOnClickListener(this);
        care_5 = (LinearLayout) findViewById(R.id.ly_care_5);
        care_5.setOnClickListener(this);
        care_6 = (LinearLayout) findViewById(R.id.ly_care_6);
        care_6.setOnClickListener(this);

        ioCancel = (Button) findViewById(R.id.bt_io_cancel);
        ioCancel.setOnClickListener(this);
        ioSend = (Button) findViewById(R.id.bt_io_send);
        ioSend.setOnClickListener(this);

        ioInput = (EditText) findViewById(R.id.et_io_input);
        ioOutput = (EditText) findViewById(R.id.et_io_output);

        lang_chinese = (TextView) findViewById(R.id.tv_lang_chinese);
        lang_chinese.setOnClickListener(this);

        careDialogLayout = (RelativeLayout) findViewById(R.id.ry_care_dialog);
        careRecordLayout = (RelativeLayout) findViewById(R.id.ry_care_record);
        fullNkcuLayout = (LinearLayout) findViewById(R.id.ly_nkcu_full); //整個layout
        care_0 = (RelativeLayout) findViewById(R.id.ry_care);  //please wait to reload messages layout

        advertisement = (ImageView) findViewById(R.id.iv_ad);

        announcement = (TextView) findViewById(R.id.tv_ncku_announcement);
        announcement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ly_care_1:
                careRecordLayout.setVisibility(View.GONE);
                careDialogLayout.setVisibility(View.VISIBLE);
                ioInput.setText("");   //clear
                ioOutput.setText("");  //clear
                break;
            case R.id.ly_care_2:
                CareStr = "拍背/翻身";
                showNormalDialog();
                break;
            case R.id.ly_care_3:
                CareStr = "抽痰";
                showNormalDialog();
                break;
            case R.id.ly_care_4:
                CareStr = "用藥";
                showNormalDialog();
                break;
            case R.id.ly_care_5:
                CareStr = "關節運動";
                showNormalDialog();
                break;
            case R.id.ly_care_6:
                CareStr = "換藥";
                showNormalDialog();
                break;
            case R.id.bt_io_cancel: //攝取排出(取消)
            case R.id.bt_io_send:   //攝取排出(送出)
                hideKeyboard(MainActivity.this);   //hide softkeyboard
                //上傳資料到後台 (After...)
                careDialogLayout.setVisibility(View.GONE);
                careRecordLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_lang_chinese:
                getPatientInfo(); //get病患資訊 20200331 日後可放在OnCreate內(馬上啟動)
                break;
        }
    }

    //照護紀錄對話框
    private void showNormalDialog() {
        RecordDialog = new CareRecordDialog(MainActivity.this); //繼承CareRecordDialog.java
        RecordDialog.setTitle(CareStr);  //將Title寫到Dialog xml(動態)

        //確定Button
        RecordDialog.setYesOnclickListener(new CareRecordDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                //上傳資料到後台 (After...)
                RecordDialog.dismiss(); //dialog close
                careRecordLayout.setVisibility(View.GONE);//消除殘影 20200330
                try {
                    Thread.sleep(500);
                    care_0.setVisibility(View.VISIBLE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                reload(); //重新read此Activity,主要是消除殘影
            }
        });

        //取消Button
        RecordDialog.setNoOnclickListener(new CareRecordDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                RecordDialog.dismiss();
                reflash();
            }
        });

        RecordDialog.show();
    }

    //強迫隱藏softkeyboard fxn
    public static void hideKeyboard(Activity activity) {
        Log.d(TAG, "hideKeyboard: onClick");
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //only刷新頁面
    private void reflash(){
        fullNkcuLayout.setVisibility(View.GONE); //整個頁面消失
        fullNkcuLayout.post(new Runnable() {
            @Override
            public void run() {
                fullNkcuLayout.setVisibility(View.VISIBLE); //整個頁面顯示
            }
        });
    }

    //every 60secs to reload main's xml(only xml)
    private Runnable reflashLayout = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "reflashLayout Run !");
            reflash();
            handler.postDelayed(this, 1000 * 60 * 60 ); //60 = 1 hour
        }
    };

    //重新讀取MainActivity 主要是消除殘影
    public void reload() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
