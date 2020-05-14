package com.virusX.passwordBro;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.parse.ParseUser;

import es.dmoral.toasty.Toasty;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class AccountFragment extends Fragment {

    private DataBackupHelper helper;
    private Button restoreBackup, deleteBackup, delAccount, logoutBtn, backupBtn;
    private TextView textView;
    private String text;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        requireActivity().setTitle("Account Details");

        restoreBackup = view.findViewById(R.id.restoreBackupBtn);
        deleteBackup = view.findViewById(R.id.deleteBackupBtn);
        delAccount = view.findViewById(R.id.delAccountBtn);
        TextView nickName = view.findViewById(R.id.nickNameTxt);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        backupBtn = view.findViewById(R.id.backupBtn);
        textView = view.findViewById(R.id.acFragmentTxt);

        if (text.equals(""))
            textView.setText(R.string.no_details_found);
        else
            textView.setText(text);

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
                helper = new DataBackupHelper(getContext());
                helper.retrieveData();
            }
        });

        deleteBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PrettyDialog prettyDialog = new PrettyDialog(requireContext());
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
                helper = new DataBackupHelper(getContext());
                final boolean result = helper.createBackup();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (result) {
                            helper.backupData();
                        } else {
                            Toasty.error(requireContext(),
                                    "An error occurred while creating Backup", Toasty.LENGTH_SHORT, true).show();
                            text = "Last Backup Failed";
                        }
                    }
                }, 1000);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Logging user out...");
                ParseUser.logOut();
                Toasty.info(requireContext(), "User Logged out",
                        Toasty.LENGTH_SHORT, true).show();
                FragmentManager fragmentManager =
                        requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                progressDialog.dismiss();
            }
        });

        delAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper = new DataBackupHelper(getContext());
                final PrettyDialog prettyDialog = new PrettyDialog(requireContext());
                prettyDialog.setIcon(R.drawable.ic_error)
                        .setTitle("Alert")
                        .setMessage(getString(R.string.delete_ac))
                        .addButton("Delete Account",
                                R.color.pdlg_color_white,
                                R.color.pdlg_color_red,
                                new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        ProgressDialog progressDialog = new ProgressDialog(getContext());
                                        progressDialog.setMessage("Deleting Account");
                                        helper.deleteAccount();
                                        FragmentManager fragmentManager =
                                                requireActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                                                new HomeFragment()).commit();
                                        prettyDialog.dismiss();
                                        progressDialog.dismiss();
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
        textView.setText(text);
    }
}
