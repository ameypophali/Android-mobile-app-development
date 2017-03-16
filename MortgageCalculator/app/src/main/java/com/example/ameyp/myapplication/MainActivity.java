package com.example.ameyp.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    private EditText HousePriceET, DownPaymentET, AnnualInterestRateET,LoanLengthET;
    private TextView MonthlyPaymentTextView, TotalPaymentTextView; // shows calculated Monthly payments
    float MonthlyPayment, TotalPayment;
    float totalMonths;
    final int MONTHSINYEAR = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HousePriceET = (EditText) findViewById(R.id.HousePriceEditText);
        DownPaymentET = (EditText) findViewById(R.id.DownPaymentEditText);
        AnnualInterestRateET = (EditText) findViewById(R.id.InterestEditText);
        LoanLengthET = (EditText) findViewById(R.id.LoanLengthEditText);

        MonthlyPaymentTextView = (TextView) findViewById(R.id.MonthlyPaymentTextView);
        TotalPaymentTextView = (TextView) findViewById(R.id.TotalPaymentTextView);

        Button CalculateButton = (Button) findViewById(R.id.Calculatebutton);
        Button CancelButton = (Button) findViewById(R.id.CancelButton);

        TotalPaymentTextView.setText("");
        MonthlyPaymentTextView.setText("");

        HousePriceET.setText("");
        DownPaymentET.setText("");
        AnnualInterestRateET.setText("");
        LoanLengthET.setText("");

        // create click listener calculate button
        OnClickListener CalculateButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculateMonthlyPayment();   // calculate Monthly payments and send it to the text box
                CalculateTotalPayment();    //calculate Total payments and output it
            }
        };

        CalculateButton.setOnClickListener(CalculateButtonListener);     //assign the listener to Calculate button

        // create click listener for cancel button
        OnClickListener CancelButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthlyPaymentTextView.setText("");
                TotalPaymentTextView.setText("");

                HousePriceET.setText("");
                DownPaymentET.setText("");
                AnnualInterestRateET.setText("");
                LoanLengthET.setText("");
            }
        };

        CancelButton.setOnClickListener(CancelButtonListener);
    }

    //function to calculate monthly payments
    public void CalculateMonthlyPayment()
    {
        float monthlyInterestRate;
        float LoanAmount;

        try {
            float HousePrice = Float.parseFloat(HousePriceET.getText().toString());
            float DownPayment = Float.parseFloat(DownPaymentET.getText().toString());
            float AnnualInterestRate = Float.parseFloat(AnnualInterestRateET.getText().toString());
            float LoanLength = Float.parseFloat(LoanLengthET.getText().toString());

            totalMonths = (LoanLength * MONTHSINYEAR);

            monthlyInterestRate = (AnnualInterestRate) / (12 * 100);
            LoanAmount = HousePrice - DownPayment;

            if(totalMonths != 0) {
                MonthlyPayment = (float) ((LoanAmount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, -totalMonths)));
            }
            else{
                MonthlyPayment = 0.0f;
            }

            if(Float.isNaN(MonthlyPayment)){
                MonthlyPayment = 0.0f;
            }


        }
        catch(NumberFormatException NF){
            float HousePrice = 0.0f;
            float DownPayment = 0;
            float AnnualInterestRate = 0.0f;
            float LoanLength = 0.0f;
            MonthlyPayment = 0.0f;
        }

        MonthlyPaymentTextView.setText(currencyFormat.format(MonthlyPayment));
    }

    //function to calculate total payments
    public void CalculateTotalPayment()
    {
        TotalPayment = MonthlyPayment*totalMonths;
        TotalPaymentTextView.setText(currencyFormat.format(TotalPayment)); //output the value
    }

}