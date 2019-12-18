package com.badrul.awla;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FutureInterview extends AppCompatActivity implements ApplyJobAdapter.OnItemClicked{

    Button searchJob;
    ImageView imgGone,imgJobOff;
    TextView txtGone,txtJobOff;
    List<Job> jobList;
    ImageButton logout,activeOn,activeOff;
    String userID;

    //the recyclerview
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_interview);

        recyclerView = findViewById(R.id.recylcerView);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString(Config.U_USER_ID, "Not Available");



        //initializing the joblist

        jobList = new ArrayList<>();

        loadJob();
    }public void loadJob(){
        final ProgressDialog loading = ProgressDialog.show(this,"Please Wait","Contacting Server",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_API+"loadapplyjob?userID="+userID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting job object from json array
                                JSONObject job = array.getJSONObject(i);

                                //adding the job to job list
                                jobList.add(new Job(
                                        job.getString("jobID"),
                                        job.getString("jobPosition"),
                                        job.getString("jobDetails"),
                                        job.getString("jobOpenDate"),
                                        job.getString("jobCloseDate"),
                                        job.getString("jobCategory"),
                                        job.getString("companyID"),
                                        job.getString("companyName"),
                                        job.getString("companyLogo")
                                ));
                            }

                            //creating adapter object and setting it to recyclerview
                            ApplyJobAdapter adapter = new ApplyJobAdapter(getApplicationContext(), jobList);
                            recyclerView.setAdapter(adapter);
                            adapter.setOnClick(FutureInterview.this);

                            if (adapter.getItemCount() == 0) {
                                imgGone.setVisibility(View.VISIBLE);
                                txtGone.setVisibility(View.VISIBLE);
                            } else{

                                imgGone.setVisibility(View.GONE);
                                txtGone.setVisibility(View.GONE);
                            }

                            //add shared preference ID,nama,credit here
                            loading.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(FutureInterview.this,"No internet . Please check your connection",
                                    Toast.LENGTH_LONG).show();
                        }
                        else{

                            Toast.makeText(FutureInterview.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //adding our stringrequest to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    } @Override
    public void onItemClick(int position) {
        // The onClick implementation of the RecyclerView item click
        //ur intent code here
        Job job = jobList.get(position);
        //Toast.makeText(FoodMenu.this, job.getLongdesc(),
        //      Toast.LENGTH_LONG).show();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,
                Context.MODE_PRIVATE);

        // Creating editor to store values to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Adding values to editor

        editor.putString(Config.A_JOB_ID, job.getJobID());
        editor.putString(Config.A_JOB_POSITION, job.getJobPosition());
        editor.putString(Config.A_JOB_DETAILS, job.getJobDetails());
        editor.putString(Config.A_JOB_OPEN_DATE, job.getJobOpenDate());
        editor.putString(Config.A_JOB_CLOSE_DATE, job.getJobCloseDate());
        editor.putString(Config.A_JOB_CATEGORY, job.getJobCategory());
        editor.putString(Config.A_COMPANY_ID, job.getCompanyID());
        editor.putString(Config.A_COMPANY_NAME, job.getCompanyName());
        editor.putString(Config.A_COMPANY_LOGO, job.getCompanyLogo());


        // Saving values to editor
        editor.commit();

        Intent i = new Intent(FutureInterview.this, RecordInterview.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        //finish();
    }
}
