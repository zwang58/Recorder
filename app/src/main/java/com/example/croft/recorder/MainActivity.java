package com.example.croft.recorder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public JSONObject jos = null;
    public JSONArray ja = null;
    private ListView list;
    TextView text;
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

        jos = null;
        try{
            // Reading a file that already exists
            File f = new File(getFilesDir(), "file.ser");
            FileInputStream fi = new FileInputStream(f);
            ObjectInputStream o = new ObjectInputStream(fi);
            String j = null;
            try{
                j = (String) o.readObject();
            }
            catch(ClassNotFoundException c){
                c.printStackTrace();
            }
            try {
                jos = new JSONObject(j);
                ja = jos.getJSONArray("data");
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            if (ja.length() == 0) {
                list.setEnabled(false);
                list.setVisibility(View.INVISIBLE);
                text.setVisibility(View.VISIBLE);

            }else {
                // Show the list
                final List<String> aList = new ArrayList<>();
                for (int i = 0; i < ja.length(); i++) {
                    try {
                        aList.add(ja.getJSONObject(i).getString("title"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }

                // Show the list view with the each list item an element from listItems
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, aList);
                list.setAdapter(adapter);

                // Set an OnItemClickListener for each of the list items
                final Context context = this;
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            JSONObject tmp = ja.getJSONObject(position);

                            Intent detailIntent = new Intent(context, DetailsActivity.class);
                            detailIntent.putExtra("title", tmp.getString("title"));
                            detailIntent.putExtra("desc", tmp.getString("desc"));
                            detailIntent.putExtra("time", tmp.getString("time"));
                            detailIntent.putExtra("date", tmp.getString("date"));
                            detailIntent.putExtra("gps", tmp.getString("gps"));
                            detailIntent.putExtra("index", position);
                            startActivity(detailIntent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

}
