package jp.kinwork.Common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

public class KinworkDialgo extends AlertDialog implements DialogInterface {


    public KinworkDialgo(@NonNull Context context) {
        super(context);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }
}
