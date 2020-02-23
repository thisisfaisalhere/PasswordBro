package com.virusX.passwordBro;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import es.dmoral.toasty.Toasty;

class AddBackupAns {

    private Context context;
    private EditText q1Edt, q2Edt, q3Edt, q4Edt, q5Edt;
    private RadioGroup radioGroup;
    private RadioButton iceCream, chocolate;
    private int choice;
    private String ans1 = "", ans2 = "", ans3 = "", ans4 = "", ans5 = "", username, objectId;
    private ProgressBar progressBar;
    private Button saveBtn;
    private SharedPreferences preferences;
    private ParseQuery<ParseObject> query;

    AddBackupAns(Context context) {
        this.context = context;
    }

    private void initContextData() {
        q1Edt = ((Activity)context).findViewById(R.id.q1Edt);
        q2Edt = ((Activity)context).findViewById(R.id.q2Edt);
        q3Edt = ((Activity)context).findViewById(R.id.q3Edt);
        q4Edt = ((Activity)context).findViewById(R.id.q4Edt);
        q5Edt = ((Activity)context).findViewById(R.id.q5Edt);
        radioGroup = ((Activity)context).findViewById(R.id.radioGroup);
        iceCream = ((Activity)context).findViewById(R.id.ice_cream_radio);
        chocolate = ((Activity)context).findViewById(R.id.chocolate_radio);
        progressBar = ((Activity) context).findViewById(R.id.progressBar);
        saveBtn = ((Activity) context).findViewById(R.id.qnBtn);

        preferences = context.getSharedPreferences("objectId", Context.MODE_PRIVATE);
        objectId = preferences.getString("objectId", "");
    }

    void addAns() {
        initContextData();
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
        query = ParseQuery.getQuery("backupAns");
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ans1 = q1Edt.getText().toString();
                ans2 = q2Edt.getText().toString();
                ans3 = q3Edt.getText().toString();
                ans4 = q4Edt.getText().toString();
                ans5 = q5Edt.getText().toString();

                if(ans1.equals("") || ans2.equals("") || ans3.equals("")
                        || ans4.equals("") || ans5.equals("") || choice == -1) {
                    Toasty.error(context, "A field is not filled",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    username = ParseUser.getCurrentUser().getUsername();
                    query.getInBackground(objectId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if(object != null && e == null) {
                                object.put("username", username);
                                object.put("ans1", ans1);
                                object.put("ans2", ans2);
                                object.put("ans3", ans3);
                                object.put("ans4", ans4);
                                object.put("ans5", ans5);
                                object.put("choice", choice);
                                progressBar.setVisibility(View.VISIBLE);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null) {
                                            Toasty.success(context, "Saved",
                                                    Toasty.LENGTH_SHORT, true).show();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toasty.error(context, e.getMessage() + "",
                                                    Toasty.LENGTH_SHORT, true).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            } else if (object == null) {
                                final ParseObject parseObject = new ParseObject("backupAns");
                                parseObject.put("username", username);
                                parseObject.put("ans1", ans1);
                                parseObject.put("ans2", ans2);
                                parseObject.put("ans3", ans3);
                                parseObject.put("ans4", ans4);
                                parseObject.put("ans5", ans5);
                                parseObject.put("choice", choice);
                                progressBar.setVisibility(View.VISIBLE);
                                parseObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null) {
                                            String objectId = parseObject.getObjectId();
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("objectId", objectId);
                                            editor.apply();
                                            Toasty.success(context, "Saved",
                                                    Toasty.LENGTH_SHORT, true).show();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toasty.error(context, e.getMessage() + "",
                                                    Toasty.LENGTH_SHORT, true).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    void checkAns() {
        initContextData();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("backupAns");
        objectId = preferences.getString("objectId", "");
        query.whereEqualTo("objectId", objectId);
        progressBar.setVisibility(View.VISIBLE);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(object != null && e == null) {
                    q1Edt.setText(object.getString("ans1"));
                    q2Edt.setText(object.getString("ans2"));
                    q3Edt.setText(object.getString("ans3"));
                    q4Edt.setText(object.getString("ans4"));
                    q5Edt.setText(object.getString("ans5"));
                    choice = object.getInt("choice");
                    if(choice == 1) {
                        iceCream.setChecked(true);
                    } else if (choice == 2) {
                        chocolate.setChecked(true);
                    } else {
                        iceCream.setChecked(false);
                        chocolate.setChecked(false);
                    }
                    progressBar.setVisibility(View.GONE);
                } else e.printStackTrace();
            }
        });
    }
}
