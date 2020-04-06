package home.com.epd42nkcu;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

class CareRecordDialog extends Dialog {
    private Context  context;
    private Button   dialogSend, dialogCancel;   //確定&取消 按鈕
    private TextView dialogTitle;                //本文Title
    private String   titleStr;                   //來自外界的Title
    private TextView dialogQuestion;

    private onNoOnclickListener noOnclickListener;   //取消按鈕被點擊了的監聽器
    private onYesOnclickListener yesOnclickListener; //確定按鈕被點擊了的監聽器

    public void setNoOnclickListener(onNoOnclickListener onNoOnclickListener) {
        this.noOnclickListener = onNoOnclickListener;
    }

    public void setYesOnclickListener(onYesOnclickListener onYesOnclickListener) {
        this.yesOnclickListener = onYesOnclickListener;
    }

    public CareRecordDialog(Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        setCanceledOnTouchOutside(false); //周邊沒作用

        initView();

        initData();

        initEvent();
    }

    private void initEvent() {
        dialogSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }

    private void initData() {
        if (titleStr != null) {
            dialogTitle.setText(titleStr);
        }
    }

    private void initView() {
        dialogCancel = (Button) findViewById(R.id.bt_nkcu_dialog_cancel);
        dialogSend = (Button) findViewById(R.id.bt_nkcu_dialog_send);
        dialogTitle = (TextView) findViewById(R.id.tv_nkcu_dialog_title);
        dialogQuestion = (TextView) findViewById(R.id.tv_nkcu_dialog_question);
    }

    /****************************************
     *
     *  從外界Activity為Dialog設置dialog的Title
     *  @param title
     *
     * ***************************************/
    public void setTitle(String title) {
        titleStr = title;
    }

    /************************************
     *
     * 設置確定按鈕和取消被點擊的interface
     *
     * **********************************/
    public interface onNoOnclickListener{
        public void onNoClick();
    }

    public interface onYesOnclickListener{
        public void onYesClick();
    }
}
