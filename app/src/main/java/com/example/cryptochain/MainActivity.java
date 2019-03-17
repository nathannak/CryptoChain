package com.example.cryptochain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    EditText email_et;
    EditText username_et;
    EditText password_et;
    Button btn;
    TextView txt_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        email_et = (EditText) findViewById(R.id.editText2);
        password_et = (EditText) findViewById(R.id.editText5);
        username_et = (EditText) findViewById(R.id.editText22);
        btn = (Button) findViewById(R.id.button);
        txt_view = (TextView) findViewById(R.id.editText100);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //sign up logic

                if (!email_et.getText().toString().equals("") && !username_et.getText().toString().equals("") && !password_et.getText().toString().equals(""))
                {
                    try {
                        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                        String URL = "http://ec2-18-219-112-9.us-east-2.compute.amazonaws.com/register";
                        JSONObject jsonBody = new JSONObject();

                        //sample working cred
//                    jsonBody.put("email", "noah.andrews21@gmail.com");
//                    jsonBody.put("username", "test123123");
//                    jsonBody.put("password", "test123");

                        jsonBody.put("email", email_et.getText().toString());
                        jsonBody.put("username", username_et.getText().toString());
                        jsonBody.put("password", password_et.getText().toString());

                        final String requestBody = jsonBody.toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("VOLLEY", response);
                                Toast.makeText(getApplicationContext(), "Sign up successful", Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.e("VOLLEY", error.toString());
                                Toast.makeText(getApplicationContext(), "Sign up failed", Toast.LENGTH_LONG).show();

                            }
                        }) {
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }

                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                try {
                                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                                } catch (UnsupportedEncodingException uee) {
                                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                    return null;
                                }
                            }

                            @Override
                            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                String responseString = "";
                                if (response != null) {
                                    responseString = String.valueOf(response.statusCode);
                                    // can get more details such as response.headers
                                }
                                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                            }
                        };

                        requestQueue.add(stringRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });


        txt_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this,SignIn.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);

            }
        });

    }

}

