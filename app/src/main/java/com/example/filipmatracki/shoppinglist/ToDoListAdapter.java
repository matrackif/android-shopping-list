package com.example.filipmatracki.shoppinglist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Filip Matracki on 9/8/2016.
 */
public class ToDoListAdapter extends RecyclerView.Adapter<ToDoDataViewHolder>
{
    private ArrayList<ToDoDataElement> elements_;
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_DATA = 1;
    private ToDoList toDoListActivity_;
    private RecyclerView rv_; // the same rv that is in the main window
    private int lastClickedDataRowPosition_ = 0; //
    private String lastClickedImageViewTag_ = "";
    private ImageView lastClickedImageView_ = null;
    /*
    private class OnClickRowListener implements View.OnClickListener {
        
        @Override
        public void onClick(final View view) {
            int itemPosition = rv_.getChildLayoutPosition(view);
            if(itemPosition != 0){
                ToDoDataElement item = elements_.get(itemPosition - 1);
                Toast.makeText(toDoListActivity_, item.toString(), Toast.LENGTH_LONG).show();
                lastClickedDataRowPosition_ = itemPosition - 1;
            }
            else{
                Toast.makeText(toDoListActivity_, "HEADER", Toast.LENGTH_LONG).show();
            }

        }
        
    }
    */
    private class OnClickTaskTextViewListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            final TextView tv = (TextView)v;
            final int lastClickedTvIndex = rv_.getChildLayoutPosition((View)tv.getParent()) - 1;
            final AlertDialog.Builder alert = new AlertDialog.Builder(toDoListActivity_);
            final EditText edittext = new EditText(toDoListActivity_);
            final Set<String> newElements = new LinkedHashSet<>();
            final ArrayList<String[]> decodedElements = new ArrayList<>();

            String[] decodedRow;
            for(String row : toDoListActivity_.getCodedElements()){
                decodedRow = row.split(toDoListActivity_.SEPERATOR);
                decodedElements.add(decodedRow);
            }

            alert.setMessage("Enter a new task");
            alert.setTitle("Modify Task");
            alert.setView(edittext);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    final String text = edittext.getText().toString();
                    Log.d("debug","Replacing the task text: " + decodedElements.get(lastClickedTvIndex)[0] + " with: " + text);
                    decodedElements.get(lastClickedTvIndex)[0] = text;
                    String element;
                    elements_.clear();
                    for(String[] row : decodedElements){

                        element = row[0] + toDoListActivity_.SEPERATOR + row[1] + toDoListActivity_.SEPERATOR + row[2] + toDoListActivity_.SEPERATOR + row[3];
                        elements_.add(new ToDoDataElement(row[0],row[1],row[2],row[3]));
                        newElements.add(element);
                        Log.d("debug", "in OnClickTaskTextViewListener(), adding element: \n" + element);
                    }

                    tv.setText(text);

                    toDoListActivity_.updateCodedElements(newElements);
                    PrefSingleton.getInstance().writeSetOfElementsToSharedPref(newElements);
                    notifyDataSetChanged();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // what ever you want to do with No option.
                }
            });

            alert.show();

        }
    }

    private class OnClickCheckBoxViewListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            final ArrayList<String[]> decodedElements = new ArrayList<>();
            final Set<String> newElements = new LinkedHashSet<>();
            CheckBox cb = (CheckBox)v;
            final int lastClickedCbIndex = rv_.getChildLayoutPosition((View)cb.getParent()) - 1;
            int count = 0;

            String[] decodedRow;
            //Log.d("debug","in OnCheckBoxViewListener() size of codedElements: \n"+ toDoListActivity_.getCodedElements().size());
            for(String row : toDoListActivity_.getCodedElements()){
                decodedRow = row.split(toDoListActivity_.SEPERATOR);
                decodedElements.add(decodedRow);
                Log.d("debug","in OnCheckBoxViewListener() decoded element before manipulation: \n"+ row);
            }

            //Log.d("debug","in OnCheckBoxViewListener() size of decodedElements: "+ decodedElements.size());
            if(Integer.parseInt(decodedElements.get(lastClickedCbIndex)[3]) > 0){
                Log.d("debug","in OnCheckBoxViewListener() the checkbox was CHECKED, now we shall UNCHECK it. val: " + decodedElements.get(lastClickedCbIndex)[3]);
                count = -1;
                String newValue = Integer.toString(count);
                while(toDoListActivity_.getListOfIsCheckedValues().contains(newValue)){
                    count--;
                    newValue = Integer.toString(count);
                }
                decodedElements.get(lastClickedCbIndex)[3] = newValue;
                Log.d("debug","in OnCheckBoxViewListener() new value for checkbox: " + newValue);
            }
            else{
                Log.d("debug","in OnCheckBoxViewListener() the checkbox was UNCHECKED, now we shall CHECK it val: " + decodedElements.get(lastClickedCbIndex)[3]);
                count = 1;
                String newValue = Integer.toString(count);
                while(toDoListActivity_.getListOfIsCheckedValues().contains(newValue)){
                    count++;
                    newValue = Integer.toString(count);
                }
                decodedElements.get(lastClickedCbIndex)[3] = newValue;
                Log.d("debug","in OnCheckBoxViewListener() new value for checkbox: " + newValue);

            }
            String element = "";
            elements_.clear();
            for(String[] row : decodedElements){

                element = row[0] + toDoListActivity_.SEPERATOR + row[1] + toDoListActivity_.SEPERATOR + row[2] + toDoListActivity_.SEPERATOR + row[3];
                elements_.add(new ToDoDataElement(row[0],row[1],row[2],row[3]));
                newElements.add(element);
                Log.d("debug", "in OnClickCheckBoxTextViewListener(), adding element: \n" + element);
            }
            toDoListActivity_.updateCodedElements(newElements);
            PrefSingleton.getInstance().writeSetOfElementsToSharedPref(newElements);
            notifyDataSetChanged();



        }
    }
    private class OnClickImageListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            ImageView iv = (ImageView)v;
            lastClickedImageView_ = iv;
            lastClickedDataRowPosition_ = rv_.getChildLayoutPosition((View)iv.getParent()) - 1;
            lastClickedImageViewTag_ = (String)iv.getTag();
            dispatchTakePictureIntent();
            Toast.makeText(toDoListActivity_, "lastRowIndex: " + lastClickedDataRowPosition_, Toast.LENGTH_LONG).show();
        }
    }
    //private OnClickRowListener onClickListener = new OnClickRowListener();
    private OnClickImageListener onClickImageListener = new OnClickImageListener();
    private OnClickTaskTextViewListener onClickTaskTextViewListener = new OnClickTaskTextViewListener();
    private OnClickCheckBoxViewListener onClickCheckBoxViewListener = new OnClickCheckBoxViewListener();

    public ToDoListAdapter(ArrayList<ToDoDataElement> elements, ToDoList activity)
    {
        elements_ = elements;
        toDoListActivity_ = activity;
        rv_ = ((ToDoList)toDoListActivity_).getRecyclerView();
        notifyDataSetChanged();
    }

    public void setData(ArrayList<ToDoDataElement> data)
    {
        elements_ = data;
        notifyDataSetChanged();
    }
    @Override
    public ToDoDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        if(viewType == TYPE_HEADER)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_header_row,parent,false);
            //view.setOnClickListener(onClickListener);
            
        }
        if(viewType == TYPE_DATA)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_data_row,parent,false);
            //view.setOnClickListener(onClickListener);
            ImageView iv = (ImageView)view.findViewById(R.id.image_view);
            TextView tv = (TextView)view.findViewById(R.id.task_tv);
            CheckBox cb = (CheckBox)view.findViewById(R.id.checkbox);
            iv.setOnClickListener(onClickImageListener);
            tv.setOnClickListener(onClickTaskTextViewListener);
            cb.setOnClickListener(onClickCheckBoxViewListener);

        }
        return new ToDoDataViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(ToDoDataViewHolder holder, int position)
    {
        ToDoDataElement element = null;

        if(position != 0)
        {
            element = elements_.get(position - 1);
        }

        holder.bindData(element);

    }

    @Override
    public int getItemCount()
    {
        return elements_.size()+1;
    }


    public int getItemViewType(int position)
    {
        if(position == 0)
            return TYPE_HEADER;
        else
            return TYPE_DATA;
    }
    private void dispatchTakePictureIntent() {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // TODO add additional information to intent
                if (takePictureIntent.resolveActivity(toDoListActivity_.getPackageManager()) != null) {

                   toDoListActivity_.startActivityForResult(takePictureIntent, ToDoList.REQUEST_IMAGE_CAPTURE);
                    /*
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(toDoListActivity_,
                                "com.example.filipmatracki.shoppinglist",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        toDoListActivity_.startActivityForResult(takePictureIntent, ToDoList.REQUEST_IMAGE_CAPTURE);
                    }
                    */
                }

    }
    public int getLastClickedDataRowPosition(){
        return lastClickedDataRowPosition_;
    }

    public ImageView getLastClickedImageView(){
        return lastClickedImageView_;
    }
    public String getLastClickedImageViewTag(){
        return lastClickedImageViewTag_;
    }
}
