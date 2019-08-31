package com.example.alarmapplication.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.alarmapplication.listener.DialogListener;
import com.example.alarmapplication.R;

import java.text.SimpleDateFormat;

public class DataDialog extends Dialog implements View.OnClickListener {
    private Context mContext;

    private DialogListener listener;
    private Button saveBtn;
    private Button quitBtn;


    private EditText todoEditText;


    private String date="";
    private String name="";
    private String time="";


    private boolean edit; //이건 Recycler_note에서 데이터 편집시 운동정보입력을 운동정보편집으로 바꾸기 위한 변수

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

    private EditText directEditText;

    private static boolean DIRECT_EDIT=false;
    private DateDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_todo_put);

        //다이얼로그 밖의 화면은 흐리게 및 사이즈 설정
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);


        todoEditText = findViewById(R.id.todoEditText);


        //셋팅
        saveBtn=(Button)findViewById(R.id.saveBtn);
        quitBtn=(Button)findViewById(R.id.quitBtn);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        saveBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
    }
    public DataDialog(@NonNull Context context) {
        super(context);
        mContext=context;
        this.edit=false;
    }
    public DataDialog(@NonNull Context context, boolean edit) {
        super(context);
        this.edit=edit;
    }

    public void setDialogListener(DialogListener dialogListener){
        this.listener = dialogListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saveBtn:
                name = todoEditText.getText().toString();
                if(name.equals("")||name==null){
                    Toast.makeText(mContext,"일정을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else if(name.length()<2)
                    Toast.makeText(mContext,"두 글자 이상 입력해주세요.",Toast.LENGTH_SHORT).show();
                else {
                    listener.onPositiveClicked(name);
                    dismiss();
                }
                // 다음 dialog / null 값일시 값 입력하라고 하기
                break;
            case R.id.quitBtn:
                cancel();
        }
    }

    private void setDate(String date){
        this.date=date;
    }
    private void setTime(String time){
        this.time=time;
    }

}
