package com.example.alarmapplication.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmapplication.R;
import com.example.alarmapplication.activity.DialogActivity;
import com.example.alarmapplication.activity.MainActivity;
import com.example.alarmapplication.component.AlarmReceiver;
import com.example.alarmapplication.component.AlarmService;
import com.example.alarmapplication.db.AlarmContract;
import com.example.alarmapplication.db.DateContract;
import com.example.alarmapplication.db.DbHelper;
import com.example.alarmapplication.db.DbHelper_alarm;
import com.example.alarmapplication.db.EntireContract;
import com.example.alarmapplication.dialog.DataDialog;
import com.example.alarmapplication.listener.DialogListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
    private static final String TAG = "RecyclerAdapter";
    private Context mContext;
    private SQLiteDatabase mDb, nDb;
    private DbHelper_alarm dbHelper_alarm;
    private DbHelper dbHelperDate;
    private long PARENT_ID;
    private DataDialog dialog;
    private ArrayList<EntireContract> list;
    // private TrainingDataDialog_edit dialog;

    public static AlarmManager mAlarmMgr = null;
    public static PendingIntent mAlarmIntent = null;



    public RecyclerAdapter(Context mContext, ArrayList<EntireContract> list) {
        this.mContext = mContext;
        this.list = list;
        dbHelperDate = new DbHelper(mContext);
        mDb = dbHelperDate.getWritableDatabase();

        dbHelper_alarm = new DbHelper_alarm(mContext);
        nDb = dbHelper_alarm.getWritableDatabase();
    }

    @NonNull
    @Override
    public RecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_todo, viewGroup, false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ItemViewHolder itemViewHolder, int position) {
        int cursor_id = list.get(position).getId();
        itemViewHolder.itemView.setTag(cursor_id);
        itemViewHolder.note_id = cursor_id;
        itemViewHolder.onBind(position);
    }

    public void onBindViewHolder(@NonNull RecyclerAdapter.ItemViewHolder holder, int position, @NonNull List<Object> payloads) {

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    String type = (String) payload;
                    if (TextUtils.equals(type, "update") && holder instanceof ItemViewHolder) {
                        Cursor c = mDb.rawQuery("SELECT * FROM " + DateContract.DateContractEntry.TABLE_NAME + " WHERE " + DateContract.DateContractEntry._ID +
                                "=" + "'" + holder.note_id + "'", null);
                        c.moveToFirst();
                        String name_value = c.getString(c.getColumnIndex(DateContract.DateContractEntry.COLUMN_NAME));
                        String date_value = c.getString(c.getColumnIndex(DateContract.DateContractEntry.COLUMN_DATE));
                        String time_value = c.getString(c.getColumnIndex(DateContract.DateContractEntry.COLUMN_TIME));
                        holder.itemView.setTag(c.getColumnIndex(DateContract.DateContractEntry._ID));
                        holder.todo.setText(name_value);
                        holder.deadline.setText(date_value + " " + time_value);

                        holder.note_id = c.getInt(c.getColumnIndex(DateContract.DateContractEntry._ID));
                        holder.data.setId(c.getInt(c.getColumnIndex(DateContract.DateContractEntry._ID)));
                        holder.data.setName(name_value);
                        holder.data.setDate(date_value + " " + time_value);
                    }
                }
            }
        }
    }

    // 커스텀 뷰홀더 item layout 에 존재하는 위젯들을 바인딩합니다.
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private TextView todo;
        private TextView deadline;
        private EntireContract data;
        private int note_id;

        public ItemViewHolder(View itemView) {
            super(itemView);
            todo = itemView.findViewById(R.id.todoTextView);
            deadline = itemView.findViewById(R.id.deadLineTextView);

            itemView.setOnCreateContextMenuListener(this); //2. OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정해둡니다.

        }

        public void onBind(int position) {
            todo.setText(list.get(position).getName());
            deadline.setText(list.get(position).getDate() + " " + list.get(position).getTime());
            data = new EntireContract(list.get(position).getName(), list.get(position).getDate(), list.get(position).getTime(), list.get(position).getAlarm());
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {  // 3. 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록해줍니다. ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 됩니다.
            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1001:  // 5. 편집 항목을 선택시
                        Intent intent = new Intent(mContext, DialogActivity.class);
                        intent.putExtra("edit", 1);
                        intent.putExtra("todo", data.getName());
                        intent.putExtra("date", data.getDate());
                        intent.putExtra("time", data.getTime());
                        mContext.startActivity(intent);

                        break;

                    case 1002:
                        boolean result = deleteDB();
                        if (result == true) {
                            Toast.makeText(mContext, "삭제 성공", Toast.LENGTH_SHORT).show();
                            Log.d("delete", "success");
                            notifyItemRemoved(getAdapterPosition());
                        } else {
                            Toast.makeText(mContext, "삭제 실패", Toast.LENGTH_SHORT).show();
                            Log.d("delete", "fail");
                        }
                        break;

                }
                return true;
            }
        };

        public void editDB(String name, String date, String time) {
            ContentValues cv = new ContentValues();
            cv.put(DateContract.DateContractEntry.COLUMN_NAME, name);
            cv.put(DateContract.DateContractEntry.COLUMN_DATE, date);
            cv.put(DateContract.DateContractEntry.COLUMN_TIME, time);
            if (mDb.update(DateContract.DateContractEntry.TABLE_NAME, cv, DateContract.DateContractEntry._ID + "=?", new String[]{String.valueOf(note_id)}) > 0)
                Toast.makeText(mContext, "편집 성공", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext, "편집 실패", Toast.LENGTH_SHORT).show();
        }

        public boolean deleteDB() {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() == note_id) {
                    list.remove(i);
                    break;
                }
            }
            nDb.delete(AlarmContract.Entry.TABLE_NAME, AlarmContract.Entry.COLUMN_KEY + "=" + note_id, null);
            cancelAlarmManger(note_id);
            return mDb.delete(DateContract.DateContractEntry.TABLE_NAME, DateContract.DateContractEntry._ID + "=" + note_id, null) > 0;
        }
    }
    // 어댑터에 현재 보관되고 있는 커서를 새로운 것으로 바꿔 UI를 갱신한다.


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(EntireContract data) {
        // 외부에서 item을 추가시킬 함수입니다.
        list.add(data);
    }

    public void setAlarmManager(int ID) {
        mAlarmMgr = (AlarmManager)DialogActivity.getAppContext().getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(DialogActivity.getAppContext(), AlarmReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }



    public void cancelAlarmManger(int id) {
        setAlarmManager(id);
        if (mAlarmIntent != null) {
            mAlarmMgr = (AlarmManager)DialogActivity.getAppContext().getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(DialogActivity.getAppContext(), AlarmReceiver.class);
            intent.putExtra("state","alarm on");
            mAlarmIntent = PendingIntent.getBroadcast(DialogActivity.getAppContext(), id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            mAlarmMgr.cancel(mAlarmIntent);
            mAlarmIntent.cancel();
            mAlarmMgr = null;
            mAlarmIntent = null;
            setAlarm();
        }
    }

    private void setAlarm(){
        final PendingIntent pendingIntent;
        final AlarmManager alarmManager=(AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        final Intent my_intent = new Intent(mContext, AlarmReceiver.class);
        Calendar calendar;
        int REQUEST_CODE =1;
        my_intent.putExtra("state","alarm on");

        Cursor c = nDb.rawQuery("select * from "+ AlarmContract.Entry.TABLE_NAME+" order by "+AlarmContract.Entry.COLUMN_DAY+" asc, "
                +AlarmContract.Entry.COLUMN_TIME+" asc",null);
        if(c.getCount()>0) {
            c.moveToFirst();
            String date = c.getString(c.getColumnIndex(AlarmContract.Entry.COLUMN_DAY));
            String alarmTime = c.getString(c.getColumnIndex(AlarmContract.Entry.COLUMN_TIME));
            REQUEST_CODE = c.getInt(c.getColumnIndex(AlarmContract.Entry.COLUMN_KEY));
            Log.d(TAG,REQUEST_CODE+"");
            calendar=init(date, alarmTime);
            c.close();
            pendingIntent = PendingIntent.getBroadcast(mContext, REQUEST_CODE, my_intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Log.d("before reset", calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " +
                    calendar.get(Calendar.HOUR_OF_DAY) + "시" + calendar.get(Calendar.MINUTE) + "분");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pendingIntent);
            }
        }
    }

    private Calendar init(String date, String alarmTime){
        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(date.substring(0,4));
        int month = Integer.parseInt(date.substring(5,7))-1;
        int day = Integer.parseInt(date.substring(8,10));
        int hour, minute;

        if(alarmTime.length()==5){
            hour = Integer.parseInt(alarmTime.substring(0, 1));
            minute = Integer.parseInt(alarmTime.substring(2, 4));
        }else{
            hour = Integer.parseInt(alarmTime.substring(0, 2));
            minute = Integer.parseInt(alarmTime.substring(3, 5));
        }
        calendar.set(year,month,day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }
}