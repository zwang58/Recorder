package com.example.croft.recorder;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView list;
    TextView text;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = findViewById(R.id.mainList);
    }

    protected void onResume(){
        super.onResume();
        TextView text = findViewById(R.id.empty);
        text.setVisibility(View.INVISIBLE);
        try{

            File f = new File(getFilesDir(), "file.ser");
            Log.d("Main", "file length is " + f.length());
            if (f.length() == 0){
                try {

                    FileOutputStream fo = new FileOutputStream(f);
                    ObjectOutputStream os = new ObjectOutputStream(fo);
                    os.writeObject(Integer.toString(0));
                    os.close();
                    fo.close();
                    Log.d("Main", "file length is now " + f.length());

                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }

            int count = 0;
            try{
                FileInputStream fi = new FileInputStream(f);
                ObjectInputStream o = new ObjectInputStream(fi);
                count = Integer.parseInt((String) o.readObject());
                o.close();
                fi.close();

            } catch (ClassNotFoundException e){
            }
            Log.d("Main", "count is " + count);

            if (count == 0) {

                list.setEnabled(false);
                list.setVisibility(View.INVISIBLE);
                text.setVisibility(View.VISIBLE);

            }else {
                // Show the list
                final List<String> aList = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    aList.add("Audio Recording "+ (i+1));
                }

                // Show the list view with the each list item an element from listItems
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, aList);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String filename = Integer.toString(position+1) + ".3gp";
                        try {
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath()
                                    + "/" + filename);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e){

                        }

                        Toast.makeText(MainActivity.this, "Playing " + filename, Toast.LENGTH_SHORT).show();
                    }

                });
            }
        }
        catch(IOException e){

            list.setEnabled(false);
            list.setVisibility(View.INVISIBLE);
            text.setVisibility(View.VISIBLE);
        }
    }

    // This method will just show the menu item (which is our button "ADD")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        // the menu being referenced here is the menu.xml from res/menu/menu.xml
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                Intent i = new Intent(this, WebActivity.class);
                startActivity(i);
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

}
