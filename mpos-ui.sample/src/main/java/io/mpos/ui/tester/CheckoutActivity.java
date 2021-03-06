/*
 * mpos-ui : http://www.payworksmobile.com
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 payworks GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.mpos.ui.tester;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.EnumSet;

import io.mpos.Mpos;
import io.mpos.accessories.AccessoryFamily;
import io.mpos.accessories.parameters.AccessoryParameters;
import io.mpos.errors.MposError;
import io.mpos.provider.ProviderMode;
import io.mpos.transactionprovider.processparameters.TransactionProcessParameters;
import io.mpos.transactions.Currency;
import io.mpos.transactions.parameters.TransactionParameters;
import io.mpos.ui.acquirer.ApplicationName;
import io.mpos.ui.shared.MposUi;
import io.mpos.ui.shared.model.MposUiConfiguration;

import static android.view.View.OnClickListener;

public class CheckoutActivity extends AppCompatActivity {
    private final static String MERCHANT_ID = "<create a test merchant in the gateway manager>";
    private final static String MERCHANT_SECRET = "<create a test merchant in the gateway manager>";

    private String mLastTransactionIdentifier = null;
    private boolean mIsAcquirerMode = false;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        TextView sdkVersionText = (TextView) findViewById(R.id.sdk_version);
        sdkVersionText.setText("SDK version : " + Mpos.getVersion());

        MposUi.initialize(CheckoutActivity.this, ProviderMode.TEST, MERCHANT_ID, MERCHANT_SECRET);

        findViewById(R.id.transaction_signature).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initMockPaymentController();

                //Styling the Payment Controller.
                MposUi.getInitializedInstance()
                        .getConfiguration().getAppearance()
                        .setColorPrimary(Color.parseColor("#ff9800"))
                        .setColorPrimaryDark(Color.parseColor("#f57c00"))
                        .setBackgroundColor(Color.parseColor("#FFF3E0"))
                        .setTextColorPrimary(Color.BLACK);
                startPayment(108.20);
            }
        });

        findViewById(R.id.transaction_application_selection).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initMockPaymentController();

                //Styling the Payment Controller.
                MposUi.getInitializedInstance()
                        .getConfiguration().getAppearance()
                        .setColorPrimary(Color.parseColor("#7cb342"))
                        .setColorPrimaryDark(Color.parseColor("#689f38"))
                        .setBackgroundColor(Color.parseColor("#E8F5E9"))
                        .setTextColorPrimary(Color.WHITE);
                startPayment(113.73);
            }
        });

        findViewById(R.id.transaction_declined).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initMockPaymentController();

                //Styling the Payment Controller.
                MposUi.getInitializedInstance()
                        .getConfiguration().getAppearance()
                        .setColorPrimary(Color.parseColor("#F4511E"))
                        .setColorPrimaryDark(Color.parseColor("#D84315"))
                        .setBackgroundColor(Color.parseColor("#FFEBEE"))
                        .setTextColorPrimary(Color.WHITE);
                startPayment(110.40);
            }
        });

        findViewById(R.id.transaction_ask_for_tip).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initMockPaymentController();

                //Styling the Payment Controller.
                MposUi.getInitializedInstance()
                        .getConfiguration().getAppearance()
                        .setColorPrimary(Color.parseColor("#ffbb00"))
                        .setColorPrimaryDark(Color.parseColor("#D49B00"))
                        .setBackgroundColor(Color.parseColor("#FFF3E0"))
                        .setTextColorPrimary(Color.BLACK);

                TransactionProcessParameters processParameters = new TransactionProcessParameters.Builder()
                        .addAskForTipStep()
                        .build();

                startPayment(105, true, processParameters);
            }
        });

        findViewById(R.id.transaction_e105_charge).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsAcquirerMode) {
                    MposUi mposUi = MposUi.getInitializedInstance();
                    AccessoryParameters accessoryParameters = new AccessoryParameters.Builder(AccessoryFamily.VERIFONE_E105).audioJack().build();
                    mposUi.getConfiguration().setTerminalParameters(accessoryParameters);
                    mposUi.getConfiguration().setSummaryFeatures(EnumSet.allOf(MposUiConfiguration.SummaryFeature.class));
                }
                startPayment(13.37);
            }
        });

        findViewById(R.id.transaction_miura_charge).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsAcquirerMode) {
                    MposUi mposUi = MposUi.getInitializedInstance();
                    AccessoryParameters accessoryParameters = new AccessoryParameters.Builder(AccessoryFamily.MIURA_MPI).bluetooth().build();
                    mposUi.getConfiguration().setTerminalParameters(accessoryParameters);
                    mposUi.getConfiguration().setSummaryFeatures(EnumSet.allOf(MposUiConfiguration.SummaryFeature.class));
                }
                startPayment(13.37);
            }
        });

        findViewById(R.id.transaction_miura_preauthorize).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsAcquirerMode) {
                    MposUi mposUi = MposUi.getInitializedInstance();
                    AccessoryParameters accessoryParameters = new AccessoryParameters.Builder(AccessoryFamily.MIURA_MPI).bluetooth().build();
                    mposUi.getConfiguration().setTerminalParameters(accessoryParameters);
                    mposUi.getConfiguration().setSummaryFeatures(EnumSet.allOf(MposUiConfiguration.SummaryFeature.class));
                }
                startPayment(13.37, false);
            }
        });

        findViewById(R.id.transaction_miura_charge_with_tip).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsAcquirerMode) {
                    MposUi mposUi = MposUi.getInitializedInstance();
                    AccessoryParameters accessoryParameters = new AccessoryParameters.Builder(AccessoryFamily.MIURA_MPI).bluetooth().build();
                    mposUi.getConfiguration().setTerminalParameters(accessoryParameters);
                    mposUi.getConfiguration().setSummaryFeatures(EnumSet.allOf(MposUiConfiguration.SummaryFeature.class));
                }
                TransactionProcessParameters processParameters = new TransactionProcessParameters.Builder().addAskForTipStep().build();
                startPayment(13.37, true, processParameters);
            }
        });

        findViewById(R.id.summary_last).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent summaryIntent = MposUi.getInitializedInstance().createTransactionSummaryIntent(mLastTransactionIdentifier);
                startActivityForResult(summaryIntent, MposUi.REQUEST_CODE_SHOW_SUMMARY);
            }
        });

        findViewById(R.id.refund_last).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionParameters transactionParameters = new TransactionParameters.Builder().refund(mLastTransactionIdentifier)
                        .subject("subject-refund")
                        .build();
                Intent refundIntent = MposUi.getInitializedInstance().createTransactionIntent(transactionParameters);
                startActivity(refundIntent);
            }
        });

        findViewById(R.id.capture_last).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionParameters transactionParameters = new TransactionParameters.Builder().capture(mLastTransactionIdentifier).build();
                Intent captureIntent = MposUi.getInitializedInstance().createTransactionIntent(transactionParameters);
                startActivity(captureIntent);
            }
        });

        findViewById(R.id.print_last).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                printReceipt(mLastTransactionIdentifier);
            }
        });


        findViewById(R.id.show_login).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MposUi.getInitializedInstance().createLoginIntent();
                startActivityForResult(intent, MposUi.REQUEST_CODE_LOGIN);
            }
        });

        findViewById(R.id.show_settings).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MposUi.getInitializedInstance().createSettingsIntent();
                startActivityForResult(intent, MposUi.REQUEST_CODE_SETTINGS);
            }
        });

        findViewById(R.id.logout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MposUi.getInitializedInstance().logout();
            }
        });

        findViewById(R.id.init_with_concardis).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsAcquirerMode = true;
                MposUi.initialize(CheckoutActivity.this, ProviderMode.TEST, ApplicationName.CONCARDIS, "test-integrator");
            }
        });

        findViewById(R.id.init_with_mcashier).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsAcquirerMode = true;
                MposUi.initialize(CheckoutActivity.this, ProviderMode.TEST, ApplicationName.MCASHIER, "test-integrator");
            }
        });

        findViewById(R.id.init_with_secure_retail).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsAcquirerMode = true;
                MposUi.initialize(CheckoutActivity.this, ProviderMode.TEST, ApplicationName.SECURE_RETAIL, "test-integrator");
            }
        });

        findViewById(R.id.init_with_yourbrand).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsAcquirerMode = true;
                MposUi.initialize(CheckoutActivity.this, ProviderMode.TEST, ApplicationName.YOURBRAND, "test-integrator");
            }
        });

        findViewById(R.id.init_with_provider).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsAcquirerMode = false;
                MposUi mposUi = MposUi.initialize(CheckoutActivity.this, ProviderMode.TEST, MERCHANT_ID, MERCHANT_SECRET);
                mposUi.getConfiguration().setSummaryFeatures(EnumSet.allOf(MposUiConfiguration.SummaryFeature.class));
            }
        });

        updateViews();
    }

    void initMockPaymentController() {
        MposUi mposUi = MposUi.initialize(this, ProviderMode.MOCK, "mock", "mock");
        AccessoryParameters mockAccessoryParameters = new AccessoryParameters.Builder(AccessoryFamily.MOCK).mocked().build();
        mposUi.getConfiguration().setTerminalParameters(mockAccessoryParameters);
        mposUi.getConfiguration().setSummaryFeatures(EnumSet.allOf(MposUiConfiguration.SummaryFeature.class));
    }

    void startPayment(double amount, boolean autoCapture) {
        startPayment(amount, autoCapture, null);
    }

    void startPayment(double amount) {
        startPayment(amount, true, null);
    }

    void startPayment(double amount, boolean autoCapture, TransactionProcessParameters processParameters) {
        TransactionParameters params = new TransactionParameters.Builder()
                .charge(BigDecimal.valueOf(amount), Currency.EUR)
                .subject("How much wood would a woodchuck chuck if a woodchuck could chuck wood?")
                .customIdentifier("customId")
                .autoCapture(autoCapture)
                .build();
        Intent intent = MposUi.getInitializedInstance().createTransactionIntent(params, processParameters);
        startActivityForResult(intent, MposUi.REQUEST_CODE_PAYMENT);
    }

    void printReceipt(String transactionIdentifier) {
        AccessoryParameters printerAccessoryParams = new AccessoryParameters.Builder(AccessoryFamily.SEWOO).bluetooth().build();
        MposUi.getInitializedInstance().getConfiguration().setPrinterParameters(printerAccessoryParams);
        Intent intent = MposUi.getInitializedInstance().createPrintReceiptIntent(transactionIdentifier);
        startActivityForResult(intent, MposUi.REQUEST_CODE_PRINT_RECEIPT);
    }

    void updateViews() {
        int visibility = mLastTransactionIdentifier != null ? View.VISIBLE : View.GONE;
        findViewById(R.id.summary_last).setVisibility(visibility);
        findViewById(R.id.refund_last).setVisibility(visibility);
        findViewById(R.id.print_last).setVisibility(visibility);
        findViewById(R.id.capture_last).setVisibility(visibility);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MposUi.REQUEST_CODE_PAYMENT) {

            if (resultCode == MposUi.RESULT_CODE_APPROVED) {
                Toast.makeText(this, "Transaction Approved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show();
            }

            mLastTransactionIdentifier = data.getStringExtra(MposUi.RESULT_EXTRA_TRANSACTION_IDENTIFIER);
            updateViews();

        } else if (requestCode == MposUi.REQUEST_CODE_PRINT_RECEIPT) {

            if (resultCode == MposUi.RESULT_CODE_PRINT_SUCCESS) {
                Toast.makeText(this, "Printing Receipt Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Printing Receipt Failed", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == MposUi.REQUEST_CODE_SHOW_SUMMARY) {
            // resultCode is always MposUi.RESULT_CODE_SUMMARY_CLOSED

            Toast.makeText(this, "Summary Closed", Toast.LENGTH_SHORT).show();
        } else if (requestCode == MposUi.REQUEST_CODE_LOGIN) {
            if (resultCode == MposUi.RESULT_CODE_LOGIN_SUCCESS) {
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MposUi.REQUEST_CODE_SETTINGS) {
            // resultCode is always MposUi.RESULT_CODE_SETTINGS_CLOSED
            Toast.makeText(this, "Summary Closed", Toast.LENGTH_SHORT).show();
        }

        // an error could have occurred regardless of the main outcome
        // e.g. a transaction was successful but there was an error when sending the receipt
        MposError error = MposUi.getInitializedInstance().getError();
        if (error != null) {
            Toast.makeText(this, "An error occurred: " + error.getInfo(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_info) {
            new InfoDialog().show(getSupportFragmentManager(), "INFO");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class InfoDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get app version
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            View rootView = layoutInflater.inflate(R.layout.dialog_info, null);
            ((TextView) rootView.findViewById(R.id.info_sdk_version)).setText("SDK Version: " + Mpos.getVersion());
            ((TextView) rootView.findViewById(R.id.info_body)).setText(Html.fromHtml(getString(R.string.info_body)));
            ((TextView) rootView.findViewById(R.id.info_body)).setMovementMethod(LinkMovementMethod.getInstance());

            return new AlertDialog.Builder(getActivity(), getTheme())
                    .setView(rootView)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    }).create();
        }
    }

}
