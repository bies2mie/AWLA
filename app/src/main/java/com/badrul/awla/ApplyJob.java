package com.badrul.awla;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class ApplyJob extends AppCompatActivity implements AdapterView.OnItemSelectedListener,ApplyJobAdapter.OnItemClicked{

    Spinner sp;
    List<String> list;
    ArrayAdapter<String> adp;
    String locat = "";
    Button searchJob;
    ImageView imgGone,imgJobOff;
    TextView txtGone,txtJobOff;
    List<Job> jobList;
    ImageButton logout,activeOn,activeOff;

    //the recyclerview
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);
        recyclerView = findViewById(R.id.recylcerView);
        searchJob = findViewById(R.id.searchBtn);
        sp = findViewById(R.id.category);
        sp.setOnItemSelectedListener(this);
        list = new ArrayList<>();

        list.add("IT");
        list.add("Business");
        list.add("Manufacturing");

        adp = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, list);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adp);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        //initializing the joblist

        jobList = new ArrayList<>();

        loadJob();

        searchJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ("".equalsIgnoreCase(locat)){

                    Toast.makeText(ApplyJob.this, "Please select any category", Toast.LENGTH_LONG).show();

                }else{



                }

            }
        });


    }
    public void loadJob(){
        final ProgressDialog loading = ProgressDialog.show(this,"Please Wait","Contacting Server",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_API+"loadjob?jobCategory="+locat,
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
                            adapter.setOnClick(ApplyJob.this);

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
                            Toast.makeText(ApplyJob.this,"No internet . Please check your connection",
                                    Toast.LENGTH_LONG).show();
                        }
                        else{

                            Toast.makeText(ApplyJob.this, error.toString(), Toast.LENGTH_LONG).show();
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


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        locat = parent.getSelectedItem().toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + locat, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        Toast.makeText(arg0.getContext(), "Please Select Your Category", Toast.LENGTH_LONG).show();

    }

    @Override
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

        editor.putString(Config.J_JOB_ID, job.getJobID());
        editor.putString(Config.J_JOB_POSITION, job.getJobPosition());
        editor.putString(Config.J_JOB_DETAILS, job.getJobDetails());
        editor.putString(Config.J_JOB_OPEN_DATE, job.getJobOpenDate());
        editor.putString(Config.J_JOB_CLOSE_DATE, job.getJobCloseDate());
        editor.putString(Config.J_JOB_CATEGORY, job.getJobCategory());
        editor.putString(Config.J_COMPANY_ID, job.getCompanyID());
        editor.putString(Config.J_COMPANY_NAME, job.getCompanyName());
        editor.putString(Config.J_COMPANY_LOGO, job.getCompanyLogo());


        // Saving values to editor
        editor.commit();

        Intent i = new Intent(ApplyJob.this, JobDetails.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        //finish();
    }
}
