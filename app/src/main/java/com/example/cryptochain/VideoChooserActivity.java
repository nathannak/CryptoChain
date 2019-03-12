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


    Button btn;
    static List<String> hashList = new ArrayList<>();
    static InputStream iStream;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        final Intent data_2 = data;

        //Toast.makeText(getApplicationContext(), "Video Retrieved", Toast.LENGTH_LONG).show();

        Log.v("TAG", "" + resultCode);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                //this is to get path for later if needed
                //String selectedVideoPath = getPath(data.getData());

                try {
                    iStream = this.getContentResolver().openInputStream(data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {

                    //debug here -> OOM we have to divide to chinks
                    //Toast.makeText(this,Arrays.toString(getBytes(iStream)),Toast.LENGTH_LONG).show();

                    //debug here -> OOM we have to divide to chinks
                    //Toast.makeText(this,SHAsum(getBytes(iStream)),Toast.LENGTH_LONG).show();

                    //hash from within getBytes no other way
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

                    //TODO:post to server



                    //getBytes(iStream);
                    //call this from within getBytes
                    //SHAsum(getBytes(iStream));

                }catch (Exception e){e.printStackTrace();};

//                        //Toast.makeText(getApplicationContext(), "Video Path is: " + selectedVideoPath , Toast.LENGTH_LONG).show();
//
//                        // open as bytestream
//                        // pass to messagedigest class
//                        // read the stream
//                        // does the math compute sha-2 hash
//                        // meta data: hash primary key:
//
//                        // post request : video name, hash, creation date(? nice to have), comment(free entry)
//
//                        //button
//                        //text field -> user enters a name
//                        //Description -> user enters a description
//
//                        //send button should be disabled if no name, but ok with no description

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


        //TODO: send to server here




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
