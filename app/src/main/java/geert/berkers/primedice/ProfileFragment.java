package geert.berkers.primedice;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Primedice Application Created by Geert on 2-2-2016.
 */
public class ProfileFragment extends Fragment {

    private MainActivity activity;
    private View view, setPasswordView, twoFacterView, emergencyAddressView;

    private User user;
    private Bitmap otpQRImage;
    private LinearLayout showedLogLayout, showedAffiliateLayout;
    private RelativeLayout securityLayout, showedSecurityLayout, logLayout, affiliateLayout;

    private EditText edEmergencyAddress;
    private String tipsURL, emailURL, passwordURL, twoFactorURL, emergencyAddressURL, depositsURL, withdrawalsURL;
    private Button btnSetPassword, btnSetEmail, btnSetTwoFactor, btnSetEmergencyAddress, btnShowDeposits, btnShowWithdrawals, btnTipLog;//, btnContactSupport;
    private TextView txtUsername, txtDateJoined, lblEmail, txtTwoFactorSet, txtEmergencyAddressSet, securityPlus, logPlus, affiliatePlus, txtAffiliateLink, txtAffiliateInformation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            this.view = inflater.inflate(R.layout.fragment_profile, container, false);

            initControls();
            setListeners();
        }

        setInformation();

        return view;
    }

    private void initControls() {
        activity = (MainActivity) getActivity();

        securityLayout = (RelativeLayout) view.findViewById(R.id.securityLayout);
        showedSecurityLayout = (RelativeLayout) view.findViewById(R.id.showedSecurityLayout);
        logLayout = (RelativeLayout) view.findViewById(R.id.logsLayout);
        showedLogLayout = (LinearLayout) view.findViewById(R.id.showedLogsLayout);
        affiliateLayout = (RelativeLayout) view.findViewById(R.id.affiliateLayout);
        showedAffiliateLayout = (LinearLayout) view.findViewById(R.id.showedAffiliateLayout);

        logPlus = (TextView) view.findViewById(R.id.logsPlus);
        lblEmail = (TextView) view.findViewById(R.id.lblEmail);
        txtUsername = (TextView) view.findViewById(R.id.txtUsername);
        securityPlus = (TextView) view.findViewById(R.id.securityPlus);
        affiliatePlus = (TextView) view.findViewById(R.id.affiliatePlus);
        txtDateJoined = (TextView) view.findViewById(R.id.txtDateJoined);
        txtAffiliateLink = (TextView) view.findViewById(R.id.affiliateLink);
        txtTwoFactorSet = (TextView) view.findViewById(R.id.txtTwoFactorSet);
        txtEmergencyAddressSet = (TextView) view.findViewById(R.id.txtEmergencyAddressSet);
        txtAffiliateInformation = (TextView) view.findViewById(R.id.showAffiliateInformation);

        btnTipLog = (Button) view.findViewById(R.id.btnTipLog);
        btnSetEmail = (Button) view.findViewById(R.id.btnEmail);
        btnSetTwoFactor = (Button) view.findViewById(R.id.btnTwoFactor);
        btnSetPassword = (Button) view.findViewById(R.id.btnSetPassword);
        //btnContactSupport = (Button) view.findViewById(R.id.btnContactSupport);
        btnShowDeposits = (Button) view.findViewById(R.id.btnShowDeposits);
        btnShowWithdrawals = (Button) view.findViewById(R.id.btnShowWithdrawals);
        btnSetEmergencyAddress = (Button) view.findViewById(R.id.btnSetEmergencyAddress);

        showedLogLayout.setVisibility(View.GONE);
        showedSecurityLayout.setVisibility(View.GONE);
        showedAffiliateLayout.setVisibility(View.GONE);

        tipsURL = "https://api.primedice.com/api/tips?access_token=";
        emailURL = "https://api.primedice.com/api/email?access_token=";
        passwordURL = "https://api.primedice.com/api/password?access_token=";
        depositsURL = "https://api.primedice.com/api/deposits?access_token=";
        twoFactorURL = "https://api.primedice.com/api/2fa/enable?access_token=";
        withdrawalsURL = "https://api.primedice.com/api/withdrawals?access_token=";
        emergencyAddressURL = "https://api.primedice.com/api/emergency?access_token=";
    }

    private void setListeners() {
        securityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showedSecurityLayout.getVisibility() == View.VISIBLE) {
                    showedSecurityLayout.setVisibility(View.GONE);
                    securityPlus.setText("+");
                } else {
                    showedSecurityLayout.setVisibility(View.VISIBLE);
                    securityPlus.setText("-");
                }
            }
        });

        logLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showedLogLayout.getVisibility() == View.VISIBLE) {
                    showedLogLayout.setVisibility(View.GONE);
                    logPlus.setText("+");
                } else {
                    showedLogLayout.setVisibility(View.VISIBLE);
                    logPlus.setText("-");
                }
            }
        });

        affiliateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showedAffiliateLayout.getVisibility() == View.VISIBLE) {
                    showedAffiliateLayout.setVisibility(View.GONE);
                    affiliatePlus.setText("+");
                } else {
                    showedAffiliateLayout.setVisibility(View.VISIBLE);
                    affiliatePlus.setText("-");
                }
            }
        });

        btnSetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPassword();
            }
        });

        btnSetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEmail();
            }
        });

        btnSetTwoFactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTwoFactor();
            }
        });

        btnSetEmergencyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {setEmergencyAddress();
            }
        });

        btnShowDeposits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeposits(v);
            }
        });

        btnShowWithdrawals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWithdrawals(v);
            }
        });

        btnTipLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTipLog(v);
            }
        });

        txtAffiliateInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAffiliateInfo();
            }
        });

        txtAffiliateLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyAffiliateLink();
            }
        });

/* No API available
        btnContactSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactSupport();
            }
        });
*/
    }

    private void checkToHide() {
        if (user.address_enabled) {
            Log.w("Address", "Enabled");
            btnSetEmergencyAddress.setVisibility(View.INVISIBLE);
            txtEmergencyAddressSet.setVisibility(View.VISIBLE);
        } else {
            btnSetEmergencyAddress.setVisibility(View.VISIBLE);
            txtEmergencyAddressSet.setVisibility(View.INVISIBLE);
            Log.w("Address", "User need to set a address!");
        }

        if (user.email_enabled) {
            Log.w("Email", "Enabled");
            lblEmail.setVisibility(View.GONE);
            btnSetEmail.setVisibility(View.GONE);
            View emailDivider = view.findViewById(R.id.emailDivider);
            emailDivider.setVisibility(View.GONE);
        } else {
            Log.w("Email", "User need to set a email!");
        }

        String setPasswordText;
        if (user.password) {
            setPasswordText = "CHANGE";
        } else {
            setPasswordText = "SET";
        }

        btnSetPassword.setText(setPasswordText);

        if (user.otp_enabled) {
            Log.w("OTP", "Enabled");
            btnSetTwoFactor.setVisibility(View.INVISIBLE);
            txtTwoFactorSet.setVisibility(View.VISIBLE);
        } else {
            btnSetTwoFactor.setVisibility(View.VISIBLE);
            txtTwoFactorSet.setVisibility(View.INVISIBLE);
        }

    }

    private void setInformation() {
        user = activity.getUser();

        txtDateJoined.setText(user.getRegistered());

        String account = user.getUsername() + "'s Account";
        txtUsername.setText(account);

        String link = "https://primedice.com/?ref=" + user.getUsername();
        txtAffiliateLink.setText(link);

        checkToHide();

        String imageURL = user.otp_qr;
        if (imageURL != null) {
            try {
                imageURL = imageURL.replace("166x166", "500x500");
                DownloadImageTask downloadImageTask = new DownloadImageTask();
                otpQRImage = downloadImageTask.execute(imageURL).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    // Set Password
    private void setPassword() {
        LayoutInflater factory = LayoutInflater.from(activity);

        if (setPasswordView != null) {
            ViewGroup parent = (ViewGroup) setPasswordView.getParent();
            if (parent != null) {
                parent.removeView(setPasswordView);
            }
        }
        try {
            setPasswordView = factory.inflate(R.layout.setpassword_layout, null);
        } catch (InflateException e) {
            e.printStackTrace();
        }

        final TextView txtEmailInfo = (TextView) setPasswordView.findViewById(R.id.emailInfo);

        final EditText edNewPassword = (EditText) setPasswordView.findViewById(R.id.edNewPassword);
        final EditText edEmailOptional = (EditText) setPasswordView.findViewById(R.id.edEmailOptional);
        final EditText edCurrentPassword = (EditText) setPasswordView.findViewById(R.id.edCurrentPassword);
        final EditText edConfirmNewPassword = (EditText) setPasswordView.findViewById(R.id.edConfirmNewPassword);

        if (user.email_enabled) {
            txtEmailInfo.setVisibility(View.GONE);
            edEmailOptional.setVisibility(View.GONE);
        }

        if (!user.password) {
            edCurrentPassword.setVisibility(View.GONE);
        }

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("SET PASSWORD");
        alertDialog.setView(setPasswordView);
        alertDialog.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String newPassword = edNewPassword.getText().toString();
                String optionalEmail = edEmailOptional.getText().toString();
                String currentPassword = edCurrentPassword.getText().toString();
                String confirmedPassword = edConfirmNewPassword.getText().toString();

                if (newPassword.length() < 6) {
                    Toast.makeText(activity.getApplicationContext(), "Password to short!", Toast.LENGTH_LONG).show();

                } else if (newPassword.equals(confirmedPassword)) {

                    String result;
                    try {
                        SetPasswordTask setPasswordTask = new SetPasswordTask();
                        result = setPasswordTask.execute((passwordURL + activity.getAccess_token()), currentPassword, newPassword, optionalEmail).get();

                        if (result == null) {
                            Toast.makeText(activity.getApplicationContext(), "Password is NOT changed!", Toast.LENGTH_LONG).show();
                        } else {
                            user.password = true;

                            String message;
                            //Check if password is set or changed

                            if (!user.password) {
                                message = "Password set!";

                                if (optionalEmail != null & optionalEmail.length() >= 6) {
                                    user.email_enabled = true;
                                    message = "Email and Password set!";
                                }
                            } else {
                                message = "Password changed!";

                                if (optionalEmail != null & optionalEmail.length() >= 6) {
                                    user.email_enabled = true;
                                    message = "Email set and password changed!";
                                }
                            }

                            checkToHide();
                            Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException | ExecutionException | NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(activity.getApplicationContext(), "Passwords are not the same!", Toast.LENGTH_LONG).show();
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    // Set Email
    private void setEmail() {
        String message = "We recommend setting up an email address for recovery of your account in the event you lose your password. We will never, ever, send you any email for anything other than account recovery.";

        final EditText edEmail = new EditText(activity);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("RECOVERY EMAIL");
        alertDialog.setMessage(message);
        alertDialog.setView(edEmail);
        alertDialog.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String email = edEmail.getText().toString();

                if (email.length() >= 6 && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    String result;
                    Log.i("Email to set", email);

                    try {
                        SetEmailTask setEmailTask = new SetEmailTask();
                        result = setEmailTask.execute((emailURL + activity.getAccess_token()), email).get();

                        if (result == null) {
                            Toast.makeText(activity.getApplicationContext(), "Email is NOT set!", Toast.LENGTH_LONG).show();
                        } else {
                            user.email_enabled = true;
                            checkToHide();
                            Toast.makeText(activity.getApplicationContext(), "Email set!", Toast.LENGTH_LONG).show();
                        }

                    } catch (InterruptedException | ExecutionException | NullPointerException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(activity.getApplicationContext(), "Email not valid.", Toast.LENGTH_LONG).show();
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    // Set TwoFactor Authentication
    private void setTwoFactor() {
        LayoutInflater factory = LayoutInflater.from(activity);

        if (twoFacterView != null) {
            ViewGroup parent = (ViewGroup) twoFacterView.getParent();
            if (parent != null) {
                parent.removeView(twoFacterView);
            }
        }
        try {
            twoFacterView = factory.inflate(R.layout.twofactor_layout, null);
        } catch (InflateException e) {
            e.printStackTrace();
        }

        final EditText edOtp = (EditText) twoFacterView.findViewById(R.id.edOtp);
        final EditText edPassword = (EditText) twoFacterView.findViewById(R.id.edPassword);

        final TextView edOtpToken = (TextView) twoFacterView.findViewById(R.id.edOtpToken);
        edOtpToken.setText(user.otp_token);

        final ImageView otpQR = (ImageView) twoFacterView.findViewById(R.id.optQR);
        otpQR.setImageBitmap(otpQRImage);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("MULTIFACTOR LOGIN");
        alertDialog.setView(twoFacterView);
        alertDialog.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String result;
                String otp = edOtp.getText().toString();
                String otpToken = user.otp_token;
                String password = edPassword.getText().toString();

                try {
                    SetTwoFactorTask setTwoFactorTask = new SetTwoFactorTask();
                    result = setTwoFactorTask.execute((twoFactorURL + activity.getAccess_token()), otp, otpToken, password).get();

                    if (result == null) {
                        Toast.makeText(activity.getApplicationContext(), "Two Factor is NOT set!", Toast.LENGTH_LONG).show();
                    } else {
                        user.otp_enabled = true;
                        Toast.makeText(activity.getApplicationContext(), "Two Factor set!", Toast.LENGTH_LONG).show();
                        checkToHide();
                    }
                } catch (InterruptedException | ExecutionException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    // Set Emergency Address
    private void setEmergencyAddress() {

        LayoutInflater factory = LayoutInflater.from(activity);

        if (emergencyAddressView != null) {
            ViewGroup parent = (ViewGroup) emergencyAddressView.getParent();
            if (parent != null) {
                parent.removeView(emergencyAddressView);
            }
        }
        try {
            emergencyAddressView = factory.inflate(R.layout.emergency_address_layout, null);
        } catch (InflateException e) {
            e.printStackTrace();
        }

        edEmergencyAddress = (EditText) emergencyAddressView.findViewById(R.id.edEmergencyAddress);

        final Button btnScan = (Button) emergencyAddressView.findViewById(R.id.btnScan);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.initiateScan();
            }
        });

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("EMERGENCY INFORMATION");
        alertDialog.setMessage("We recommend setting up an address in the event we have to send you your balance.");
        alertDialog.setView(emergencyAddressView);
        alertDialog.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String address = edEmergencyAddress.getText().toString();

                if (address.length() < 20) {
                    Toast.makeText(activity.getApplicationContext(), "Bitcoin Address to short!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        SetEmergencyAddressTask setEmergencyAddressTask = new SetEmergencyAddressTask();
                        String result = setEmergencyAddressTask.execute((emergencyAddressURL + activity.getAccess_token()), address).get();

                        if (result == null) {
                            Toast.makeText(activity.getApplicationContext(), "Emergency Address is NOT set!", Toast.LENGTH_LONG).show();
                        } else {
                            user.address_enabled = true;
                            Toast.makeText(activity.getApplicationContext(), "Emergency Address set!", Toast.LENGTH_LONG).show();
                            checkToHide();
                        }
                    } catch (InterruptedException | ExecutionException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    // Show deposits log
    private void showDeposits(View v) {
        ArrayList<Payment> deposits = new ArrayList<>();

        try {
            GetJSONResultFromURLTask getDeposits = new GetJSONResultFromURLTask();
            String result = getDeposits.execute((depositsURL + activity.getAccess_token())).get();

            Log.w("DEPOSITS", result);

            if (result == null) {
                // No deposits
            } else {
                JSONObject json = new JSONObject(result);
                JSONArray jsonDeposits = json.getJSONArray("deposits");

                int counter = jsonDeposits.length();
                if (counter > 0) {
                    for (int i = 0; i < counter; i++) {
                        JSONObject jsonDeposit = jsonDeposits.getJSONObject(i);
                        deposits.add(new Payment(jsonDeposit));
                    }
                }
            }
        } catch (InterruptedException | ExecutionException | NullPointerException | JSONException e) {
            e.printStackTrace();
        }

        Intent paymentIntent = new Intent(v.getContext(), PaymentActivity.class);
        paymentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        paymentIntent.putExtra("title", "Deposits");
        paymentIntent.putExtra("payments", deposits);
        v.getContext().startActivity(paymentIntent);
    }

    // Show withdrawals log
    private void showWithdrawals(View v) {
        ArrayList<Payment> withdrawals = new ArrayList<>();

        try {
            GetJSONResultFromURLTask getWithdrawals = new GetJSONResultFromURLTask();
            String result = getWithdrawals.execute((withdrawalsURL + activity.getAccess_token())).get();

            Log.w("WITHDRAWALS", result);

            if (result != null) {
                JSONObject json = new JSONObject(result);
                JSONArray jsonWithdrawals = json.getJSONArray("withdrawals");

                int counter = jsonWithdrawals.length();
                if (counter > 0) {
                    for (int i = 0; i < counter; i++) {
                        JSONObject jsonWithdrawal = jsonWithdrawals.getJSONObject(i);
                        withdrawals.add(new Payment(jsonWithdrawal));
                    }
                }
            }
        } catch (InterruptedException | ExecutionException | NullPointerException | JSONException e) {
            e.printStackTrace();
        }

        Intent paymentIntent = new Intent(v.getContext(), PaymentActivity.class);
        paymentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        paymentIntent.putExtra("title", "Withdrawals");
        paymentIntent.putExtra("payments", withdrawals);
        v.getContext().startActivity(paymentIntent);
    }

    private void showTipLog(View v) {
        ArrayList<Tip> tips = new ArrayList<>();

        try {
            GetJSONResultFromURLTask getTips = new GetJSONResultFromURLTask();
            String result = getTips.execute((tipsURL + activity.getAccess_token())).get();

            Log.w("TIPS", result);

            if (result != null) {
                JSONObject json = new JSONObject(result);
                JSONArray jsonTips = json.getJSONArray("tips");

                int counter = jsonTips.length();
                if (counter > 0) {
                    for (int i = 0; i < counter; i++) {
                        JSONObject jsonTip = jsonTips.getJSONObject(i);
                        tips.add(new Tip(jsonTip));
                    }
                }
            }
        } catch (InterruptedException | ExecutionException | NullPointerException | JSONException e) {
            e.printStackTrace();
        }

        Intent paymentIntent = new Intent(v.getContext(), TipActivity.class);
        paymentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        paymentIntent.putExtra("tips", tips);
        v.getContext().startActivity(paymentIntent);
    }

    private void openAffiliateInfo() {
        Intent affiliateIntent = new Intent(activity.getApplicationContext(), AffiliateActivity.class);
        affiliateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        affiliateIntent.putExtra("user", activity.getUser());
        affiliateIntent.putExtra("access_token", activity.getAccess_token());
        activity.getApplicationContext().startActivity(affiliateIntent);
    }

    private void copyAffiliateLink() {
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Affiliate link Primedice", txtAffiliateLink.getText().toString());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(activity.getApplicationContext(), "Copied affiliate link!", Toast.LENGTH_SHORT).show();
    }

    public void setEmergencyAddress(String emergencyAddress) {
        edEmergencyAddress.setText(emergencyAddress);
    }
/*
    private void contactSupport() {
        //No API available
    }
*/
}
