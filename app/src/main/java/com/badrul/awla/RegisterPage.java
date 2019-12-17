package com.badrul.awla;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {

    Button register;
    EditText name,age,email,workexp,pass,confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);


        register = findViewById(R.id.registerBtn);
        name = findViewById(R.id.regisName);
        age = findViewById(R.id.regisAge);
        workexp = findViewById(R.id.regisWorkExp);
        email = findViewById(R.id.regisEmail);
        pass = findViewById(R.id.regisPass);
        confirmPass = findViewById(R.id.regisConfirmPass);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String nm = name.getText().toString().trim();
                final String em = email.getText().toString().trim();
                final String ag = age.getText().toString().trim();
                final String wexp = workexp.getText().toString().trim();
                final String pss = pass.getText().toString().trim();
                String conpss = confirmPass.getText().toString().trim();

                if (nm.length()<5) {
                    Toast.makeText(getApplicationContext(), "Please enter minimum 5 characters for Name",
                            Toast.LENGTH_LONG).show();
                } else if (em.length()<8) {
                    Toast.makeText(getApplicationContext(), "Please enter proper email address",
                            Toast.LENGTH_LONG).show();
                } else if (ag.length()<1) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter proper age", Toast.LENGTH_LONG).show();
                }
                else if (pss.length()<8) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter minimum 8 character for Password", Toast.LENGTH_LONG).show();
                }
                else if (!pss.equals(conpss)) {
                    Toast.makeText(getApplicationContext(),
                            "Your password does not match", Toast.LENGTH_LONG).show();
                }else if(wexp.length()<1) {
                    Toast.makeText(getApplicationContext(), "Please enter work experience",
                            Toast.LENGTH_LONG).show();
                }

                else{

                    final ProgressDialog loading = ProgressDialog.show(RegisterPage.this,"Please Wait","Contacting Server",false,false);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            Config.URL_API+"registeruser.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            loading.dismiss();

                            if (response.equalsIgnoreCase("Success")) {

                                Toast.makeText(RegisterPage.this, "Successfully Registered", Toast.LENGTH_LONG)
                                        .show();
                                Intent i = new Intent(RegisterPage.this, LoginPage.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }
                            else if(response.equalsIgnoreCase("Exist")){

                                Toast.makeText(RegisterPage.this, "Email already exist", Toast.LENGTH_LONG)
                                        .show();
                            }else{

                                Toast.makeText(RegisterPage.this, "Cannot Register", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(RegisterPage.this,"No internet . Please check your connection",
                                        Toast.LENGTH_LONG).show();
                            }
                            else{

                                Toast.makeText(RegisterPage.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("userName", nm);
                            params.put("userAge", ag);
                            params.put("userEmail", em);
                            params.put("userPass", pss);
                            params.put("userWorkExp", wexp);
                            return params;
                        }

                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            30000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);

                }}
        });
    }
}
