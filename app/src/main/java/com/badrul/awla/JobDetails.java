package com.badrul.awla;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.HashMap;
import java.util.Map;

public class JobDetails extends AppCompatActivity {

    Button applyBtn;
    TextView jobTitleTxt,companyNameTxt,jobDetailsTxt;
    ImageView companyLogoImg;
    ProgressBar progressBar;

    String jobID,userID,companyID,companyName,companyLogoURL,jobPosition,jobDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        applyBtn = findViewById(R.id.applyBtn);
        jobTitleTxt = findViewById(R.id.jobTitle);
        companyNameTxt = findViewById(R.id.companyName);
        jobDetailsTxt = findViewById(R.id.jobDetails);
        companyLogoImg = findViewById(R.id.companyLogo);
        progressBar = findViewById(R.id.progress);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        jobID = sharedPreferences.getString(Config.J_JOB_ID, "Not Available");
        userID = sharedPreferences.getString(Config.U_USER_ID, "Not Available");
        companyID = sharedPreferences.getString(Config.J_COMPANY_ID, "Not Available");
        companyName = sharedPreferences.getString(Config.J_COMPANY_NAME, "Not Available");
        companyLogoURL = sharedPreferences.getString(Config.J_COMPANY_LOGO, "Not Available");
        jobPosition = sharedPreferences.getString(Config.J_JOB_POSITION, "Not Available");
        jobDetails = sharedPreferences.getString(Config.J_JOB_DETAILS, "Not Available");
        String from_apply = sharedPreferences.getString(Config.FROM_APPLY, "Not Available");

        jobTitleTxt.setText(jobPosition);
        companyNameTxt.setText(companyName);
        jobDetailsTxt.setText(jobDetails);

        if("YES".equalsIgnoreCase(from_apply)){
            applyBtn.setVisibility(View.VISIBLE);

        }else{


            applyBtn.setVisibility(View.GONE);
        }

        RequestOptions options = new RequestOptions().centerCrop().dontAnimate().placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher);
        Glide
                .with(JobDetails.this)
                .load(companyLogoURL).apply(options).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                companyLogoImg.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                companyLogoImg.setVisibility(View.VISIBLE);
                return false;
            }
        })
                .into(companyLogoImg);

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(JobDetails.this);
                alertDialogBuilder.setTitle(getString(R.string.txt_confirm));
                alertDialogBuilder.setMessage(getString(R.string.txt_tnc));

                final Dialog dialog = new Dialog(JobDetails.this);

                alertDialogBuilder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                dialog.setCanceledOnTouchOutside(true);

                                        final ProgressDialog loading = ProgressDialog.show(JobDetails.this,"Please Wait","Contacting Server",false,false);

                                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                                Config.URL_API+"applyjob.php", new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                loading.dismiss();

                                                if(response.contains("Success")){

                                                    Toast.makeText(JobDetails.this, "Apply success. Thank you", Toast.LENGTH_LONG)
                                                            .show();

                                                    Intent intent = new Intent(JobDetails.this, ApplyJob.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();



                                                }
                                                else if(response.contains("Exist")) {

                                                    Toast.makeText(JobDetails.this, "Sorry. You already apply for this position", Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                loading.dismiss();
                                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                    Toast.makeText(JobDetails.this,"No internet . Please check your connection",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                                else{

                                                    Toast.makeText(JobDetails.this, error.toString(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("jobID", jobID);
                                                params.put("userID", userID);
                                                params.put("companyID", companyID);
                                                params.put("applyStatus", "Processing");
                                                return params;
                                            }

                                        };

                                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                                30000,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                        requestQueue.add(stringRequest);

                                    }

                        });

                alertDialogBuilder.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                dialog.setCanceledOnTouchOutside(true);

                            }
                        });
                alertDialogBuilder.setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {

                            }
                        }
                );

                //Showing the alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

        }
    });
    }
}
