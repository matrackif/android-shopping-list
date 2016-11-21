package com.example.filipmatracki.shoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Handler;

public class ToDoList extends AppCompatActivity {
    public static final String SEPERATOR = ";";
    private RecyclerView toDoList_;
    private LinearLayoutManager layoutManager_;
    private Set<String> codedElements_ = null;
    private Button clearListButton_;
    private ToDoListAdapter adapter_;
    private ArrayList<ToDoDataElement> data_;
    private ArrayList<String[]> decodedElements_ = new ArrayList<>();
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath_ = "";
    private int uniqueImgPathCount = 0;
    private int uniqueIsNotCheckedCount_ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        PrefSingleton.getInstance().initialize(getApplicationContext());
        data_ = new ArrayList<ToDoDataElement>();

        codedElements_ = PrefSingleton.getInstance().getSetOfElementsFromSharedPref();

        if (codedElements_ != null) {
            String debug = "";
            for (String s : codedElements_)
                debug += (s + "\n");
            Log.d("debug", "in onCreate() codedElements: \n" + debug);


            String[] decodedRow;
            for (String row : codedElements_) {
                decodedRow = row.split(SEPERATOR);
                decodedElements_.add(decodedRow);
            }

            String imgDebugStr = "";
            for (int i = 0; i < decodedElements_.size(); i++) {
                ToDoDataElement element = new ToDoDataElement(decodedElements_.get(i)[0], decodedElements_.get(i)[1], decodedElements_.get(i)[2], decodedElements_.get(i)[3]);
                data_.add(element);
                imgDebugStr += ("Data index: " + i + " " + element.toString() + "\n");
            }
            Log.d("debug", "in onCreate() adding the following elements: \n" + imgDebugStr);
            // }
            /*
            new AlertDialog.Builder(ToDoList.this)
                    .setMessage("Size of codedTasks_: " + codedTasks_.size() + " codedDates_: " + codedDates_.size()
                            + " codedImagePaths_: " + codedImagePaths_.size()
                            +" getFilesDir(): "+getFilesDir() + " ImagePaths: " + imgDebugStr + " getLastImgPath(): " + getLastSavedImagePath())
                    .setCancelable(true)
                    .create()
                    .show();
                    */
        } else {
            codedElements_ = new LinkedHashSet<String>();
            data_.clear();
            decodedElements_.clear();
        }

        //Initializing private variables
        clearListButton_ = (Button)findViewById(R.id.clear_list_button);
        if(PrefSingleton.getInstance().isClearButtonEnabled()){
            clearListButton_.setEnabled(true);
        }
        else{
            clearListButton_.setEnabled(false);
        }
        toDoList_ = (RecyclerView) findViewById(R.id.calculation_history_table);
        layoutManager_ = new LinearLayoutManager(this);
        layoutManager_.setOrientation(LinearLayoutManager.VERTICAL);
        adapter_ = new ToDoListAdapter(data_, this);
        toDoList_.setAdapter(adapter_);
        toDoList_.addItemDecoration(new SimpleDividerItemDecoration(this));
        toDoList_.setLayoutManager(layoutManager_);
        adapter_.notifyDataSetChanged();
        Log.d("debug", "onCreate() executed, RecyclerView childCount: " + toDoList_.getChildCount());
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayout ll = (LinearLayout) findViewById(R.id.to_do_list_root_layout);
        Log.d("debug", "getBackgroundColorAsInt() returned: " + PrefSingleton.getInstance().getBackroundColorAsInt());
        ll.setBackgroundColor(PrefSingleton.getInstance().getBackroundColorAsInt());
        if(PrefSingleton.getInstance().isClearButtonEnabled()){
            clearListButton_.setEnabled(true);
        }
        else{
            clearListButton_.setEnabled(false);
        }
    }


    public void clearListButtonClick(View view) {
        PrefSingleton.getInstance().clearAllSharedPrefs();
        data_.clear();
        codedElements_.clear();
        decodedElements_.clear();
        adapter_.setData(data_);
        adapter_.notifyDataSetChanged();

    }

    public void addItemButtonClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ToDoList.this);
        LayoutInflater inflater = getLayoutInflater();
        //final View inflatedView = inflater.inflate(R.layout.add_item_dialog, null);
        final EditText et = new EditText(this);
        et.setHint("Enter new task here");
        builder.setTitle("Add new item");

        builder.setView(et)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskText = et.getText().toString();
                        String date = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss").format(new Date());
                        uniqueImgPathCount++;
                        String imgPath = ("temp" + uniqueImgPathCount);
                        String isChecked = "-1";
                        while (getListOfImagePaths().contains(imgPath)) {
                            uniqueImgPathCount++;
                            imgPath = ("temp" + uniqueImgPathCount);
                        }
                        while (getListOfIsCheckedValues().contains(isChecked)) {
                            uniqueIsNotCheckedCount_--;
                            isChecked = Integer.toString(uniqueIsNotCheckedCount_);
                        }
                        String elementAsString = taskText + SEPERATOR + date + SEPERATOR + imgPath + SEPERATOR + isChecked;
                        Log.d("debug", "in addItemButtonClick() we add the following item: " + elementAsString);
                        codedElements_.add(elementAsString);
                        data_.clear();
                        String debug = "";
                        for (String s : codedElements_){
                            String[] row = s.split(SEPERATOR);
                            data_.add(new ToDoDataElement(row[0],row[1],row[2],row[3]));
                            debug += (s + "\n");
                        }

                        Log.d("debug", "in addItemButtonClick() new state of codedElements: \n" + debug);
                        //codedTasks_.add(taskText);
                        //codedDates_.add(date);
                        //codedImagePaths_.add(imgPath);
                        //String debug = "";
                        //for(String s: codedImagePaths_)
                        //     debug += (s + ", ");
                        // Log.d("debug","in addItemButtonClick() before item added decodedImagePaths: " + debug + "imgPath just added: "+ imgPath);


                        PrefSingleton.getInstance().writeSetOfElementsToSharedPref(codedElements_);
                        adapter_.setData(data_);
                        adapter_.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // TODO right now when we add something to the imagePath set it is pushed to the front. we must reverse the array
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            int rvChildIndex = adapter_.getLastClickedDataRowPosition() + 1;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            /*
            String[] decodedImagePaths = codedImagePaths_.toArray(new String[codedImagePaths_.size()]);
            String debug = "";
            for(String s : decodedImagePaths)
                debug += (s + ", ");
            Log.d("debug","in onActivityResult() decodedImagePaths: " + debug);
            */
            try {
                saveBitmapToFile(imageBitmap); // this actually sets the getLastSavedBitmap() string
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] decodedImagePaths = new String[codedElements_.size()];
            int count = 0;
            for (String s : getListOfImagePaths()) {
                decodedImagePaths[count] = s;
                count++;
            }

            if (decodedImagePaths.length != codedElements_.size()) {
                Log.e("ERROR", "in onActivityResult() size of decodedImagePaths(" + decodedImagePaths.length + ") is different than size of codedElements_(" + codedElements_.size() + ")");
                return;
            }
            for (int i = 0; i < decodedImagePaths.length; i++) {
                if (decodedImagePaths[i].equals(adapter_.getLastClickedImageViewTag())) {
                    Log.d("debug", "in onActivityResult() the path " + decodedImagePaths[i] + " will be replaced with " + getLastSavedImagePath());
                    decodedImagePaths[i] = getLastSavedImagePath();
                    break;
                }
            }
            Set<String> imgPathSet = new LinkedHashSet<String>();
            Set<String> newElements = new LinkedHashSet<String>();
            for (String s : decodedImagePaths) {
                Log.d("debug", "in onActivityResult() we add the string: " + s + "\n");
                imgPathSet.add(s);
            }
            int i = 0;
            for (String s : codedElements_) {
                String[] row = s.split(SEPERATOR);
                String newElement = row[0] + SEPERATOR + row[1] + SEPERATOR + decodedImagePaths[i] + SEPERATOR + row[3];
                newElements.add(newElement);
                i++;
            }
            PrefSingleton.getInstance().writeStringSetToPref(PrefSingleton.TO_DO_ELEMENTS, newElements);
            updateCodedElements(newElements);
            adapter_.getLastClickedImageView().setImageBitmap(imageBitmap);
            adapter_.notifyDataSetChanged();


        }
    }

    public RecyclerView getRecyclerView() {
        return toDoList_;
    }

    private void saveBitmapToFile(Bitmap bmp) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = getFilesDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath_ = image.getAbsolutePath();
        Log.d("debug", "current photo path: " + currentPhotoPath_);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(currentPhotoPath_);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String getLastSavedImagePath() {
        return currentPhotoPath_;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
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
            Intent settingsIntent = new Intent(this, SettingsScreen.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.action_delete) {
            final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
            alert.setMessage("Are you sure you want to delete all checked tasks?")
                    .setTitle("Warning")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Set<String> newElements = new LinkedHashSet<String>();
                            String[] row;
                            int k = 0;
                            String[] codedElementsArr = new String[codedElements_.size()];
                            for(String s : codedElements_){
                                codedElementsArr[k] = s;
                                k++;
                            }
                            for (int i = 0; i < codedElementsArr.length; i++) {
                                row = codedElementsArr[i].split(SEPERATOR);
                                if (Integer.parseInt(row[3]) > 0) {
                                    codedElementsArr[i] = null;
                                }
                            }

                            ArrayList<ToDoDataElement> elements = new ArrayList<ToDoDataElement>();
                            String newRow = "";
                            for(int j = 0; j < codedElementsArr.length; j++){
                                if(codedElementsArr[j] != null){
                                    row = codedElementsArr[j].split(SEPERATOR);
                                    newRow = row[0] + SEPERATOR + row[1] + SEPERATOR + row[2] + SEPERATOR + row[3];
                                    elements.add(new ToDoDataElement(row[0],row[1],row[2],row[3]));
                                    newElements.add(newRow);
                                }
                            }
                            adapter_.setData(elements);
                            adapter_.notifyDataSetChanged();
                            PrefSingleton.getInstance().writeSetOfElementsToSharedPref(newElements);
                            updateCodedElements(newElements);
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.
                        }
                    });

            alert.show();


            //do not need to update codedElements here because we already work on it
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<String> getListOfTasks() {
        ArrayList<String> decodedTasks = new ArrayList<String>();
        String[] row;
        for (String s : codedElements_) {
            row = s.split(SEPERATOR);
            decodedTasks.add(row[0]);
        }
        return decodedTasks;
    }

    public ArrayList<String> getListOfImagePaths() {
        ArrayList<String> decodedImagePaths = new ArrayList<String>();
        String[] row;
        for (String s : codedElements_) {
            row = s.split(SEPERATOR);
            decodedImagePaths.add(row[2]);
        }
        return decodedImagePaths;
    }

    public ArrayList<String> getListOfIsCheckedValues() {
        ArrayList<String> decodedIsCheckedVals = new ArrayList<String>();
        String[] row;
        for (String s : codedElements_) {
            row = s.split(SEPERATOR);
            decodedIsCheckedVals.add(row[3]);
        }
        return decodedIsCheckedVals;
    }

    public Set<String> getCodedElements() {
        //return PrefSingleton.getInstance().getStringSetFromPref(PrefSingleton.TO_DO_ELEMENTS);
        return codedElements_;
    }

    public void updateCodedElements(Set<String> newElements) {
        codedElements_ = newElements;
    }


}
