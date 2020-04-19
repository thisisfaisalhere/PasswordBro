package com.virusX.passwordBro;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class AccountFragment extends Fragment {

    private DataBackupHelper helper;
    private ProgressBar progressBar;
    private TextView textView;
    private Date currentTime;
    private Button restoreBackup, deleteBackup, delAccount, logoutBtn, backupBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        Objects.requireNonNull(getActivity()).setTitle("Account Details");

        restoreBackup = view.findViewById(R.id.restoreBackupBtn);
        deleteBackup = view.findViewById(R.id.deleteBackupBtn);
        delAccount = view.findViewById(R.id.delAccountBtn);
        TextView nickName = view.findViewById(R.id.nickNameTxt);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        backupBtn = view.findViewById(R.id.backupBtn);
        progressBar = view.findViewById(R.id.acFrgmntPBar);
        textView = view.findViewById(R.id.acFrgmntTxt);

        helper = new DataBackupHelper(getContext());

        String text = "Nickname: " + ParseUser.getCurrentUser().getUsername();

        nickName.setText(text);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        restoreBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.retrieveData();
            }
        });

        deleteBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PrettyDialog prettyDialog = new PrettyDialog(Objects.requireNonNull(getContext()));
                prettyDialog.setIcon(R.drawable.ic_error)
                        .setTitle("Alert")
                        .setMessage(getString(R.string.delete))
                        .addButton("Delete Backup",
                                R.color.pdlg_color_white,
                                R.color.pdlg_color_red,
                                new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        helper.deleteBackup();
                                        prettyDialog.dismiss();
                                    }
                                })
                        .addButton("Cancel",
                                R.color.pdlg_color_white,
                                R.color.pdlg_color_gray,
                                new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        prettyDialog.dismiss();
                                    }
                                }).show();
            }
        });

        backupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                textView.setText(R.string.creating_backup);
                helper = new DataBackupHelper(getContext());
                final boolean result = helper.createBackup();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(result) {
                            textView.setText(R.string.backing_up_data);
                            helper.backupData();
                            currentTime = Calendar.getInstance().getTime();
                        } else {
                            Toasty.error(Objects.requireNonNull(getContext()),
                                    "An error occurred", Toasty.LENGTH_SHORT, true).show();
                            textView.setText("");
                        }
                    }
                },1000);
                textView.setText(String.format("Last backup Completed at: %s", currentTime));
                progressBar.setVisibility(View.GONE);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Toasty.info(Objects.requireNonNull(getContext()), "User Logged out",
                        Toasty.LENGTH_SHORT, true).show();
                FragmentManager fragmentManager =
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
            }
        });

        delAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PrettyDialog prettyDialog = new PrettyDialog(Objects.requireNonNull(getContext()));
                prettyDialog.setIcon(R.drawable.ic_error)
                        .setTitle("Alert")
                        .setMessage(getString(R.string.delete))
                        .addButton("Delete Account",
                                R.color.pdlg_color_white,
                                R.color.pdlg_color_red,
                                new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        helper.deleteAccount();
                                        FragmentManager fragmentManager =
                                                Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                                                new HomeFragment()).commit();
                                        prettyDialog.dismiss();
                                    }
                                })
                        .addButton("Cancel",
                                R.color.pdlg_color_white,
                                R.color.pdlg_color_green,
                                new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        prettyDialog.dismiss();
                                    }
                                }).show();


            }
        });
    }
}
