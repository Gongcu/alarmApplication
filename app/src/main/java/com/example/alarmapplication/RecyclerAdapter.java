package com.example.alarmapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.alarmapplication.db.DateContract;
import com.example.alarmapplication.db.DbHelper;
import com.example.alarmapplication.dialog.DataDialog;
import com.example.alarmapplication.listener.DialogListener;


import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
    private Context mContext;
    private SQLiteDatabase mDb;
    private DbHelper dbHelperDate;
    private long PARENT_ID;
    private DataDialog dialog;
    private ArrayList<DateContract> list;
   // private TrainingDataDialog_edit dialog;

    public RecyclerAdapter(Context mContext, ArrayList<DateContract> list) {
        this.mContext = mContext;
        this.list = list;
        dbHelperDate = new DbHelper(mContext);
        mDb=dbHelperDate.getWritableDatabase();
    }

    @NonNull
    @Override
    public RecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_todo,viewGroup,false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ItemViewHolder itemViewHolder, int position) {
        int cursor_id=list.get(position).getId();
        Log.d("cusor_id",cursor_id+"");
        itemViewHolder.itemView.setTag(cursor_id);
        itemViewHolder.note_id=cursor_id;
        itemViewHolder.onBind(position);
    }

    public void onBindViewHolder(@NonNull RecyclerAdapter.ItemViewHolder holder, int position, @NonNull List<Object> payloads) {

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        }else {
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    String type = (String) payload;
                    if (TextUtils.equals(type, "update") && holder instanceof ItemViewHolder) {
                        Cursor c =mDb.rawQuery("SELECT * FROM " + DateContract.DateContractEntry.TABLE_NAME + " WHERE " + DateContract.DateContractEntry._ID +
                                "=" + "'"+holder.note_id+"'", null);
                        c.moveToFirst();
                        String name_value = c.getString(c.getColumnIndex(DateContract.DateContractEntry.COLUMN_NAME));
                        String date_value= c.getString(c.getColumnIndex(DateContract.DateContractEntry.COLUMN_DATE));
                        String time_value = c.getString(c.getColumnIndex(DateContract.DateContractEntry.COLUMN_TIME));
                        holder.itemView.setTag(c.getColumnIndex(DateContract.DateContractEntry._ID));
                        holder.todo.setText(name_value);
                        holder.deadline.setText(date_value+" "+time_value);

                        holder.note_id = c.getInt(c.getColumnIndex(DateContract.DateContractEntry._ID));
                        holder.data.setId(c.getInt(c.getColumnIndex(DateContract.DateContractEntry._ID)));
                        holder.data.setName(name_value);
                        holder.data.setDate(date_value+" "+time_value);
                    }
                }
            }
        }
    }

    // 커스텀 뷰홀더 item layout 에 존재하는 위젯들을 바인딩합니다.
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        private TextView todo;
        private TextView deadline;
        private DateContract data;
        private int note_id;

        public ItemViewHolder(View itemView) {
            super(itemView);
            todo = itemView.findViewById(R.id.todoTextView);
            deadline = itemView.findViewById(R.id.deadLineTextView);

            itemView.setOnCreateContextMenuListener(this); //2. OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정해둡니다.

        }

        public void onBind(int position){
            todo.setText(list.get(position).getName());
            deadline.setText(list.get(position).getDate()+" "+list.get(position).getTime());
            data = new DateContract(list.get(position).getName(),list.get(position).getDate(),list.get(position).getTime());
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
                        dialog = new DataDialog(mContext,true);
                        //기존엔 오류가 나서 mContext 전달값을  getApplicationContext()에서 ...activity.this로 변경 79줄
                        dialog.setDialogListener(new DialogListener() {
                            @Override
                            public void onPositiveClicked(String name, String date, String time, int alarmDay, String alarmTime) {

                            }

                            @Override
                            public void onPositiveClicked(String date, String time) {

                            }

                            @Override
                            public void onPositiveClicked(String time) {
                            }

                            @Override
                            public void onPositiveClicked(String name, String date, String time) {
                                editDB(name,date,time);
                                notifyItemChanged(getAdapterPosition(),"update");
                            }
                            @Override
                            public void onNegativeClicked() {
                                //Log.d("MyDialogListener","onNegativeClicked");
                            }
                        });
                        dialog.show();
                        //dialog.getNameTextView().setText(data.getExerciseName());
                        //dialog.getSetEditText().setText(String.valueOf(data.getSet()));
                        //dialog.getRepEditText().setText(String.valueOf(data.getRep()));
                        break;

                    case 1002:
                        boolean result=deleteDB();
                        if(result==true){
                            Toast.makeText(mContext,"삭제 성공",Toast.LENGTH_SHORT).show();
                            Log.d("delete","success");
                            notifyItemRemoved(getAdapterPosition());}
                        else{
                            Toast.makeText(mContext, "삭제 실패", Toast.LENGTH_SHORT).show();
                            Log.d("delete","fail");
                        }
                        break;

                }
                return true;
            }
        };
        public void editDB(String name, String date, String time){
            ContentValues cv=new ContentValues();
            cv.put(DateContract.DateContractEntry.COLUMN_NAME,name);
            cv.put(DateContract.DateContractEntry.COLUMN_DATE,date);
            cv.put(DateContract.DateContractEntry.COLUMN_TIME,time);
            if(mDb.update(DateContract.DateContractEntry.TABLE_NAME,cv,DateContract.DateContractEntry._ID + "=?",new String[] {String.valueOf(note_id)})>0)
                Toast.makeText(mContext,"편집 성공",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext,"편집 실패",Toast.LENGTH_SHORT).show();
        }

        public boolean deleteDB(){
            for (int i=0; i<list.size(); i++){
                if(list.get(i).getId()==note_id) {
                    list.remove(i);
                    break;
                }
            }
            return mDb.delete(DateContract.DateContractEntry.TABLE_NAME, DateContract.DateContractEntry._ID + "=" + note_id, null)>0;
        }
    }
    // 어댑터에 현재 보관되고 있는 커서를 새로운 것으로 바꿔 UI를 갱신한다.


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(DateContract data) {
        // 외부에서 item을 추가시킬 함수입니다.
        list.add(data);
    }
}
