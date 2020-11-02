package com.example.calendar_app;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class note_editor extends AppCompatActivity{
    EditText title;
    EditText note;
    FileOutputStream fos = null;
    int mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_editor);
        //setTitle("Add Note");
        Intent intent = getIntent();
        title = findViewById(R.id.notetitle);
        note = findViewById(R.id.note);
        mode = intent.getIntExtra("mode",0);

        if(mode == 1){
            setTitle("Add Note");
        }else if(mode == 2){
            setTitle("Edit Note");
            File dir = new File(getFilesDir(), "notes");
            String savetitle = intent.getStringExtra("filename");
            File textfile = new File(dir,savetitle + ".txt");
            FileInputStream fis = null;
            title.setText(savetitle);
            try {
                fis = new FileInputStream(textfile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String text;
                while((text = br.readLine()) != null){
                    sb.append(text).append("\n");
                }
                note.setText(sb.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fis != null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String notetitle = title.getText().toString();
        String notetext = note.getText().toString();
        File dir = new File(getFilesDir(), "notes");
        File textfile = new File(dir,notetitle + ".txt");
        Intent resultintent = new Intent();
        switch(item.getItemId()){
            case R.id.check:
                //Toast.makeText(this, "Clicked the add button", Toast.LENGTH_SHORT).show();
                String namelist[] = dir.list();
                //List filenames = new ArrayList<>();
                boolean exists = false;
                if(mode == 2){
                    textfile.delete();
                    textfile = new File(dir,notetitle + ".txt");
                    resultintent.putExtra("mode", 1);
                }else{
                    resultintent.putExtra("mode", 2);
                    for(String i:namelist){
                        if(i.endsWith(".txt")){
                            i = i.substring(0,i.length() - 4);
                            if(notetitle.equals(i)){
                                exists = true;
                                break;
                            }
                        }
                    }
                }

                if(exists){
                    Toast.makeText(this, "Note Title Already Exists", Toast.LENGTH_LONG).show();
                }else{
                    try {
                        fos = new FileOutputStream(textfile);
                        fos.write(notetext.getBytes());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        if(fos != null){
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
                    resultintent.putExtra("filename", notetitle);
                    setResult(3,resultintent);
                    finish();
                }
                break;
            case R.id.clear:
                textfile.delete();
                Toast.makeText(this, "Note Deleted", Toast.LENGTH_LONG).show();
                resultintent.putExtra("filename", notetitle);
                setResult(2,resultintent);
                finish();
                break;
            case R.id.send:
                Intent email = new Intent(Intent.ACTION_SEND);
                //email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
                email.putExtra(Intent.EXTRA_SUBJECT, notetitle);
                email.putExtra(Intent.EXTRA_TEXT, notetext);
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
