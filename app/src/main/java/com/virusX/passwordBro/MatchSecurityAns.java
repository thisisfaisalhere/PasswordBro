package com.virusX.passwordBro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import es.dmoral.toasty.Toasty;

class MatchSecurityAns {
    private Context context;
    private String objAns1, objAns2, objAns3, objAns4, objAns5,
            ans1, ans2, ans3, ans4, ans5;
    private int choice, objChoice, countAns;
    private EditText q1Edt, q2Edt, q3Edt, q4Edt, q5Edt;
    private RadioGroup radioGroup;
    private ProgressBar progressBar;
    private Button checkBtn;

    private void setter(String objAns1, String objAns2, String objAns3,
                       String objAns4, String objAns5, int objChoice) {
        this.objAns1 = objAns1;
        this.objAns2 = objAns2;
        this.objAns3 = objAns3;
        this.objAns4 = objAns4;
        this.objAns5 = objAns5;
        this.objChoice = objChoice;

    }

    MatchSecurityAns(Context context) {
        this.context = context;
    }

    private void initContextData() {
        q1Edt = ((Activity)context).findViewById(R.id.q1Edt);
        q2Edt = ((Activity)context).findViewById(R.id.q2Edt);
        q3Edt = ((Activity)context).findViewById(R.id.q3Edt);
        q4Edt = ((Activity)context).findViewById(R.id.q4Edt);
        q5Edt = ((Activity)context).findViewById(R.id.q5Edt);
        radioGroup = ((Activity)context).findViewById(R.id.radioGroup);
        progressBar = ((Activity) context).findViewById(R.id.progressBar);
        checkBtn = ((Activity) context).findViewById(R.id.qnBtn);
    }

    private void getAns(String username) {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("backupAns");
        parseQuery.whereEqualTo("username", username);
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(object != null && e == null) {
                    ans1 = object.getString("ans1");
                    ans2 = object.getString("ans2");
                    ans3 = object.getString("ans3");
                    ans4 = object.getString("ans4");
                    ans5 = object.getString("ans5");
                    choice = object.getInt("choice");
                    setter(ans1, ans2, ans3, ans4, ans5, choice);
                } else {
                    Toasty.error(context, e.getMessage() + "",
                            Toasty.LENGTH_SHORT, true).show();
                }
            }
        });
    }

    void getUserAns(final String username, final String email) {
        initContextData();
        getAns(username);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.ice_cream_radio:
                        choice = 1;
                        break;
                    case R.id.chocolate_radio:
                        choice = 2;
                        break;

                    default:
                        choice = -1;
                }
            }
        });
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ans1 = q1Edt.getText().toString();
                ans2 = q2Edt.getText().toString();
                ans3 = q3Edt.getText().toString();
                ans4 = q4Edt.getText().toString();
                ans5 = q5Edt.getText().toString();

                if (ans1.equals("") || ans2.equals("") || ans3.equals("")
                        || ans4.equals("") || ans5.equals("")) {
                    Toasty.error(context, "A field is not filled",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    if(objAns1.equals(ans1)) {
                        countAns++;
                    }
                    if(objAns2.equals(ans2)) {
                        countAns++;
                    }
                    if(objAns3.equals(ans3)) {
                        countAns++;
                    }
                    if(objAns4.equals(ans4)) {
                        countAns++;
                    }
                    if(objAns5.equals(ans5)) {
                        countAns++;
                    }
                    if(objChoice == choice) {
                        countAns++;
                    }
                    if(countAns >= 5) {
                        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null) {
                                    Toasty.success(context, "Reset Password Email Sent Successfully",
                                            Toasty.LENGTH_SHORT, true).show();
                                    ((Activity)context).finish();
                                } else {
                                    Toasty.error(context, e.getMessage() + "",
                                            Toasty.LENGTH_SHORT, true).show();
                                }
                            }
                        });
                    } else {
                        Toasty.error(context, "Answers did not Match.\nTry Again.",
                                Toasty.LENGTH_SHORT, true).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
