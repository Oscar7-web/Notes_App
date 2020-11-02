package com.example.calendar_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> names;
    ArrayAdapter<String> namesadapter;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = findViewById(R.id.listview);
        File dir = new File(getFilesDir()+"/notes");
        if(!dir.exists()){
            dir.mkdir();
        }
        String filenames[] = dir.list();
        names = new ArrayList<>();
        for(String i:filenames){
            if(i.endsWith(".txt")){
                i = i.substring(0,i.length() - 4);
                names.add(i);
            }
        }
        namesadapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,names);
        listview.setAdapter(namesadapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                //Toast.makeText(MainActivity.this,"Selected " + names.get(i).toString(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, note_editor.class);
                intent.putExtra("mode",2);
                intent.putExtra("filename",names.get(i).toString());
                startActivityForResult(intent, 1);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1,menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.add:
                //Toast.makeText(this, "Clicked the add button", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, note_editor.class);
                intent.putExtra("mode",1);
                startActivityForResult(intent, 1);
                break;
            case R.id.alphabetically:
                Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
                namesadapter.notifyDataSetChanged();
                break;
            case R.id.mostrecent:
                Collections.sort(names, new Comparator<String>(){
                    @Override
                    public int compare(String f1, String f2) {
                        File dir = new File(getFilesDir(), "notes");
                        File file1 = new File(dir,f1 + ".txt");
                        File file2 = new File(dir,f2 + ".txt");

                        //return Long.valueOf(file1.lastModified()).compareTo(file2.lastModified());
                        return Long.valueOf(file2.lastModified()).compareTo(file1.lastModified());
                    } });
                namesadapter.notifyDataSetChanged();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == 3){
                String addedfile = data.getStringExtra("filename");
                int mode = data.getIntExtra("mode",0);
                if(mode == 2){
                    names.add(addedfile);
                    //Log.d("Log", "added file: " + addedfile);
                    namesadapter.notifyDataSetChanged();
                }
            }else if(resultCode == 2){

                //Log.d("Log", "Visits result code 2");

                String deletedfile = data.getStringExtra("filename");
                //Log.d("Log", "deleted file is : " + deletedfile + " First in list: " + names.get(0));

                for(String i:names){
                    //i = i.substring(0,i.length() - 4);
                    //Log.d("Log", "checking :" + i + " against :" + deletedfile);
                    if(deletedfile.equals(i)){
                        names.remove(i);
                        //Log.d("Log", "Updated list");
                        namesadapter.notifyDataSetChanged();
                        break;
                    }
                }

            }
        }
    }
}
