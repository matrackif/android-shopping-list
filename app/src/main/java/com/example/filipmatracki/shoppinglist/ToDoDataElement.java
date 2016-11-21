package com.example.filipmatracki.shoppinglist;

/**
 * Created by Filip Matracki on 10/29/2016.
 */

public class ToDoDataElement {
    private String date_;
    private String task_;
    private String imagePath_;
    private String isChecked_;
    public ToDoDataElement( String task, String date, String imagePath, String isChecked){
        task_ = task;
        date_ = date;
        imagePath_ = imagePath;
        isChecked_ = isChecked;
    }
    public String getDate(){
        return date_;
    }
    public String getTask(){
        return task_;
    }
    public String getImagePath(){return  imagePath_;}
    public String getIsChecked(){return isChecked_;}
    //public boolean

    @Override
    public String toString(){
        return ("Date: "+ date_ + " Task: " + task_ + " Image Path: " +imagePath_ + " isChecked: " + isChecked_);
        }
}
