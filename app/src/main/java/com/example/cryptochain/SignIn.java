package com.example.cryptochain;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    EditText username_et;
    EditText password_et;
    Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        password_et = (EditText) findViewById(R.id.editText55);
        username_et = (EditText) findViewById(R.id.editText222);
        btn = (Button) findViewById(R.id.button1);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!username_et.getText().toString().equals("") && !password_et.getText().toString().equals("") ){

                    //SIGN IN LOGIC
                    String url = "http://ec2-18-219-112-9.us-east-2.compute.amazonaws.com/do_this";

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response)
                                {

                                    Toast.makeText(getApplicationContext(),"Response from server is: " + response+ "\nLOGIN SUCCESS",Toast.LENGTH_LONG).show();

                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error)
                                {

                                    Toast.makeText(getApplicationContext(),error.toString()+"\nLOGIN FAILURE:(",Toast.LENGTH_LONG).show();

                                }
                            })

                    {


                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> params = new HashMap<String, String>();
                            String creds = String.format("%s:%s","testtest12345","test12345612345");
                            String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                            params.put("Authorization", auth);
                            return params;
                        }

                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(SignIn.this);
                    requestQueue.add(stringRequest);

                }

            }
        });




    }
}
