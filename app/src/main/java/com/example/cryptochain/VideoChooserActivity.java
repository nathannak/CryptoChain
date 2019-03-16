package com.example.cryptochain;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class VideoChooserActivity extends AppCompatActivity {

    Button pick_btn;
    Button send_btn;
    TextView retreived_name_txtview;
    EditText given_name_editext;

    static List<String> hashList = new ArrayList<>();
    static InputStream iStream;
    static String videoName ="";
    static String creationDate="";
    static String final_hash = "";

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        final Intent data_2 = data;

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                try {
                    iStream = this.getContentResolver().openInputStream(data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {

                    //hash from within getBytes no other way to avoid OOM
                    new AsyncTask<Void,Void,Void>() {

                        protected Void doInBackground(Void ... params)  {
                            try {
                                getBytes(iStream);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return null;

                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {

                            super.onPostExecute(aVoid);

                            System.out.println("LOGS: "+ hashList.toString());

                            //get hash of hashes
                            String aggregated_hash = "";

                            for(String str: hashList){

                                aggregated_hash += str;

                            }

                            try {

                                final_hash = (String) SHAsum(aggregated_hash.getBytes());

                            } catch (NoSuchAlgorithmException e) {

                                e.printStackTrace();

                            }

                            Toast.makeText(VideoChooserActivity.this,"Hash of concatenation of hashes: " + final_hash,Toast.LENGTH_LONG).show();

                            //reset static var
                            hashList=new ArrayList<>();

                            try {

                                iStream.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }.execute();

                    //get date
                    System.out.println("LOGS: "+ getDate(data_2.getData()) );

                    //getName
                    System.out.println("LOGS: "+ getName(data_2.getData()) );
                    retreived_name_txtview.setText(getName(data_2.getData()));

                    videoName = getName(data_2.getData());
                    creationDate= getDate(data_2.getData());

                    //we are done here the rest happens in send button

                }catch (Exception e){e.printStackTrace();};

            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chooser);

        pick_btn = (Button) findViewById(R.id.video_choose_button);
        send_btn = (Button) findViewById(R.id.send_button);

        retreived_name_txtview = (TextView) findViewById(R.id.textView);
        given_name_editext = (EditText) findViewById(R.id.given_name_edit_text);

        pick_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                i.setType("video/*");
                startActivityForResult(i, 1);

            }
        });


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(creationDate.equals("")){

                    Toast.makeText(VideoChooserActivity.this,"Can't send; creation date couldn't be retrieved",Toast.LENGTH_LONG).show();

                }else if(videoName.equals("")){

                    Toast.makeText(VideoChooserActivity.this,"Can't send; video name couldn't be retrieved",Toast.LENGTH_LONG).show();

                }else if(given_name_editext.getText().toString().equals("") || given_name_editext.getText().toString().length()<=2){

                    Toast.makeText(VideoChooserActivity.this,"Can't send; you must give video a name",Toast.LENGTH_LONG).show();

                }else{

                    Toast.makeText(VideoChooserActivity.this,"Sending...",Toast.LENGTH_LONG).show();

                    //ToDO send to server here

                }

            }
        });

    }


    public String getDate(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATE_TAKEN};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }

    public String getName(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }


    public static void getBytes(InputStream inputStream) throws IOException {

        final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int counter = 0;

        try {

            int len;
            while ((len = inputStream.read(buffer)) != -1) {

                System.gc();


                byteBuffer.write(buffer, 0, len);
                counter++;

                if(counter==15258){

                    hashList.add(SHAsum(byteBuffer.toByteArray()));

                    byteBuffer.reset();
                    byteBuffer.flush();
                    counter=0;
                }


                for(int i =0 ; i< buffer.length;i++){
                    buffer[i]='\0';
                }

                System.gc();

            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        } finally {

            try{ byteBuffer.close(); byteBuffer.flush(); byteBuffer.reset();
            } catch (IOException ignored){  }

        }

    }


    public static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return byteArray2Hex(md.digest(convertme));
    }

    private static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }


}
