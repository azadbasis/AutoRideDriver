package org.autoride.driver.message;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.autoride.driver.R;
import org.autoride.driver.configs.TypefaceCache;

public class PopupMessage {

    private PopupActionListener popupActionListener;

    //    private final Vibrator mVibrator;
    private Typeface typeface;
    private final Activity mContext;
    private Dialog dialogPopupMessage;
    private TextView txtMessage;
    private final Handler mHandler = new Handler();
    private AlertDialog alertDialog;
    private TextView txtAlertMsg;
    private Button btnRefresh, btnSettings;

    public PopupMessage(Activity mContext) {

        this.mContext = mContext;

        try {
            typeface = TypefaceCache.getInstance().getTypeface(mContext.getApplicationContext(), "Roboto-Regular.ttf");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        setMessageDialog();
    }

    public void show(String message) {
        setNetAlertDialog(message);
        dialogPopupMessage.show();
    }

    public void show(String message, String loc) {
        setLocationAlertDialog(message);
        dialogPopupMessage.show();
    }

    public void show(String message, long time) {

/*
        setMessageDialog();
        txtMessage.setText(message);
        dialogPopupMessage.show();

        try {
            if (time != 0)
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogPopupMessage.dismiss();
                    }
                }, time * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        setMessageDialog(message);
        alertDialog.show();
    }

    public void show(String title, String message, long time) {

        setMessageDialog(title, message);
        alertDialog.show();

        try {
            if (time != 0)
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                    }
                }, time * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(String message, long time, String positiveButtonLabel) {

        setMessageDialog(message, time, positiveButtonLabel);
        alertDialog.show();

        try {
            if (time != 0)
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                    }
                }, time * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(String title, String message, long time, String positiveButtonLabel) {

        setMessageDialog(title, message, time, positiveButtonLabel);
        alertDialog.show();

        try {
            if (time != 0)
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                    }
                }, time * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(String message, long time, String positiveButtonLabel, String negativeButtonLabel) {

        setMessageDialog(message, time, positiveButtonLabel, negativeButtonLabel);
        alertDialog.show();

        try {
            if (time != 0)
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                    }
                }, time * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(String title, String message, long time, String positiveButtonLabel, String negativeButtonLabel) {

        setMessageDialog(title, message, time, positiveButtonLabel, negativeButtonLabel);
        alertDialog.show();

        try {
            if (time != 0)
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                    }
                }, time * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMessageDialog() {

        dialogPopupMessage = new Dialog(mContext, R.style.ThemeDialogCustom);
        dialogPopupMessage.setContentView(R.layout.dialog_message_popup);

        Button btnOK = (Button) dialogPopupMessage.findViewById(R.id.btn_dialog_message_popup_ok);
        txtMessage = (TextView) dialogPopupMessage.findViewById(R.id.txt_dialog_message_popup);

        btnOK.setTypeface(typeface);

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                //mVibrator.vibrate(25);
                try {
                    popupActionListener.actionCompletedSuccessfully(true);
                } catch (Exception e) {
                }
                dialogPopupMessage.dismiss();
            }
        });
    }

    private void setMessageDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ThemeDialogCustom);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(mContext.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //mVibrator.vibrate(25);
                try {
                    popupActionListener.actionCompletedSuccessfully(true);
                } catch (Exception e) {
                }
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
    }

    private void setMessageDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ThemeDialogCustom);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(mContext.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //mVibrator.vibrate(25);
                try {
                    popupActionListener.actionCompletedSuccessfully(true);
                } catch (Exception e) {
                }
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
    }

    private void setMessageDialog(String message, long time, String positiveButtonLabel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ThemeDialogCustom);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(positiveButtonLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //mVibrator.vibrate(25);
                try {
                    popupActionListener.actionCompletedSuccessfully(true);
                } catch (Exception e) {
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(mContext.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int paramInt) {
                //mVibrator.vibrate(25);
                try {
                    popupActionListener.actionFailed();
                } catch (Exception e) {
                }
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
    }

    private void setMessageDialog(String title, String message, long time, String positiveButtonLabel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ThemeDialogCustom);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(positiveButtonLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //mVibrator.vibrate(25);
                try {
                    popupActionListener.actionCompletedSuccessfully(true);
                } catch (Exception e) {
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(mContext.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int paramInt) {
                //mVibrator.vibrate(25);
                try {
                    popupActionListener.actionFailed();
                } catch (Exception e) {
                }
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
    }

    private void setMessageDialog(String message, long time, String positiveButtonLabel, String negativeButtonLabel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ThemeDialogCustom);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(positiveButtonLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //mVibrator.vibrate(25);
                try {
                    popupActionListener.actionCompletedSuccessfully(true);
                } catch (Exception e) {
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(negativeButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int paramInt) {
                //mVibrator.vibrate(25);
                try {
                    popupActionListener.actionFailed();
                } catch (Exception e) {
                }
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
    }

    private void setMessageDialog(String title, String message, long time, String positiveButtonLabel, String negativeButtonLabel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ThemeDialogCustom);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(positiveButtonLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //mVibrator.vibrate(25);
                try {
                    popupActionListener.actionCompletedSuccessfully(true);
                } catch (Exception e) {
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(negativeButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int paramInt) {
                //mVibrator.vibrate(25);
                try {
                    popupActionListener.actionFailed();
                } catch (Exception e) {
                }
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
    }

    public interface PopupActionListener {

        void actionCompletedSuccessfully(boolean result);

        void actionFailed();
    }

    public PopupActionListener getPopupActionListener() {
        return popupActionListener;
    }

    public void setPopupActionListener(PopupActionListener popupActionListener) {
        this.popupActionListener = popupActionListener;
    }

    private void setNetAlertDialog(String msg) {

        dialogPopupMessage = new Dialog(mContext);
        dialogPopupMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPopupMessage.setCancelable(false);
        dialogPopupMessage.setContentView(R.layout.internet_alert);

        txtAlertMsg = dialogPopupMessage.findViewById(R.id.tv_net_alert_msg);
        txtAlertMsg.setText(msg);

        btnRefresh = dialogPopupMessage.findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.finish();
                mContext.startActivity(mContext.getIntent());
            }
        });

        btnSettings = dialogPopupMessage.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
            }
        });
    }

    private void setLocationAlertDialog(String msg) {

        dialogPopupMessage = new Dialog(mContext);
        dialogPopupMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPopupMessage.setCancelable(false);
        dialogPopupMessage.setContentView(R.layout.location_alert);

        txtAlertMsg = dialogPopupMessage.findViewById(R.id.tv_net_alert_msg);
        txtAlertMsg.setText(msg);

        btnRefresh = dialogPopupMessage.findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.finish();
                mContext.startActivity(mContext.getIntent());
            }
        });

        btnSettings = dialogPopupMessage.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        });
    }
}