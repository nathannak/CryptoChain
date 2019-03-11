package com.example.cryptochain;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class VideoChooserActivity extends AppCompatActivity {


    Button btn;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(getApplicationContext(), "Video Retrieved", Toast.LENGTH_LONG).show();

        Log.v("TAG", "" + resultCode);
        Log.v("TAG", "onActivityResultCalled");

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                String selectedVideoPath = getPath(data.getData());
                try {
                    if(selectedVideoPath == null) {

                        Log.v("TAG","selected video path = null!");
                        finish();

                    } else {

                        Toast.makeText(getApplicationContext(), "Video Path is: " +selectedVideoPath , Toast.LENGTH_LONG).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chooser);

        btn = (Button) findViewById(R.id.video_choose_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                i.setType("video/*");
                startActivityForResult(i, 1);

            }
        });




    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }


}
