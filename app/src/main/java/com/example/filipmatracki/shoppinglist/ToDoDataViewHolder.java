package com.example.filipmatracki.shoppinglist;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Filip Matracki on 10/29/2016.
 */

public class ToDoDataViewHolder extends RecyclerView.ViewHolder {
    private int viewType_;
    private TextView dateTv_;
    private TextView taskTv_;
    private TextView imageTv_; //only for header row
    private TextView taskCheckedTv_; //only for header row
    private ImageView imageView_;
    private CheckBox checkBox_;


    public ToDoDataViewHolder(View itemView, int viewType){
        super(itemView);
        viewType_ = viewType;
        taskTv_ = (TextView)itemView.findViewById(R.id.task_tv);
        dateTv_ = (TextView)itemView.findViewById(R.id.date_tv);
        imageView_ = (ImageView)itemView.findViewById(R.id.image_view);
        imageTv_ = (TextView)itemView.findViewById(R.id.image_tv);
        taskCheckedTv_ = (TextView)itemView.findViewById(R.id.tasked_checked_tv);
        checkBox_ = (CheckBox)itemView.findViewById(R.id.checkbox);
    }
    public void bindData(ToDoDataElement element){
        if(viewType_ == ToDoListAdapter.TYPE_HEADER){
            taskTv_.setText("TASK:");
            dateTv_.setText("DATE:");
            imageTv_.setText("PICTURE");
            taskCheckedTv_.setText("DONE");
        }
        else if(viewType_ == ToDoListAdapter.TYPE_DATA){
            taskTv_.setText(element.getTask());
            //taskTv_.setTag((String)element.getTask());
            dateTv_.setText(element.getDate());
            String path = element.getImagePath();
            if(path.contains("temp")){
                imageView_.setTag((String)path);
            }
            else{
                imageView_.setTag((String)path);
                Bitmap myBitmap = BitmapFactory.decodeFile(path);
                imageView_.setImageBitmap(myBitmap);
            }
            if(Integer.parseInt(element.getIsChecked()) > 0){
                checkBox_.setChecked(true);
            }
            else{
                checkBox_.setChecked(false);
            }


        }
    }
}
