package com.example.croft.recorder;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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
    MediaPlayer mediaPlayer = new MediaPlayer();

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
            // Reading a file that already exists
            File f = new File(getFilesDir(), "file.ser");
            FileInputStream fi = new FileInputStream(f);
            ObjectInputStream o = new ObjectInputStream(fi);
            int count = 0;
            try{
                count = (int) o.readObject();
            }
            catch(ClassNotFoundException c){
                c.printStackTrace();
                try {
                    // Reading a file that already exists
                    //File f = new File(getFilesDir(), "file.ser");
                    FileOutputStream fo = new FileOutputStream(f);
                    ObjectOutputStream os = new ObjectOutputStream(fo);
                    os.writeObject(0);
                    Toast.makeText(MainActivity.this, "writing 0 to file.ser", Toast.LENGTH_SHORT);

                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }

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
                        //mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath()
                                    + "/" + (position+1) + ".3gp");
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e){

                        }
                        mediaPlayer.start();
                        //Toast.makeText(MainActivity.this, "Playing", Toast.LENGTH_SHORT).show();
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
