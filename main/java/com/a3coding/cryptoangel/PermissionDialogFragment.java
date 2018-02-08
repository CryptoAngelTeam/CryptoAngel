/*
 * Copyright (c) 2017 3Coding Inc.
 * All right, including trade secret rights, reserved.
 */

package com.a3coding.cryptoangel;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;

public class PermissionDialogFragment extends DialogFragment {

    public static final String TAG = PermissionDialogFragment.class.getSimpleName();

    public static final String BODY = "body";

    public static PermissionDialogFragment newInstance(int title) {
        PermissionDialogFragment frag = new PermissionDialogFragment();
        frag.setCancelable(false);
        Bundle args = new Bundle();
        args.putInt(BODY, title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int body = getArguments().getInt(BODY);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.permission_dialog_title)
                .setMessage(body)
                .setPositiveButton(R.string.permission_dialog_action,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }
                )
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                            getActivity().finish();
                        return false;
                    }
                }).create();
    }
}
