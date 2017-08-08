package com.spotreview.spotreview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.info.object.SRUser;
import com.spotreview.services.Connect;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends Activity implements OnClickListener, AdapterView.OnItemSelectedListener{

    private ImageView ivBack;
    private Button btnCreateAccount;
    private EditText etUserName, etEmail, etPassword, etMobileNumber, etAnswer;
    private Spinner spSecurityQuestion;

    String strSecurityQuestion;

    private UserRegisterTask userRegisterTask = null;
    List<NameValuePair> parameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_page);

        findID();

        setSpinnerAdapter();
    }

    private void findID() {
        ivBack = (ImageView) findViewById(R.id.iv_register_back);
        ivBack.setOnClickListener(this);
        btnCreateAccount = (Button) findViewById(R.id.btn_register_createAccount);
        btnCreateAccount.setOnClickListener(this);

        etUserName = (EditText) findViewById(R.id.et_register_username);
        etEmail =(EditText) findViewById(R.id.et_register_email);
        etPassword = (EditText) findViewById(R.id.et_register_password);
        etMobileNumber = (EditText) findViewById(R.id.et_register_mobileNumber);
        spSecurityQuestion = (Spinner) findViewById(R.id.sp_register_securityQuestion);
        spSecurityQuestion.setOnItemSelectedListener(this);
        etAnswer = (EditText) findViewById(R.id.et_register_answer);
    }

    private void setSpinnerAdapter() {
        List<String> categories = new ArrayList<String>();
        categories.add("Your pet's name?");
        categories.add("Your mother's name?");
        categories.add("Your favorite animal?");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSecurityQuestion.setAdapter(dataAdapter);
    }

    public void onClick(View v) {
        if (v == ivBack)
            finish();
        if (v == btnCreateAccount) {
            String userName, email, password, phoneNumber, secQuestion, answer;
            userName = etUserName.getText().toString();
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();
            phoneNumber = etMobileNumber.getText().toString();
            secQuestion = strSecurityQuestion;
            answer = etAnswer.getText().toString();

            if (userName.isEmpty())
                Toast.makeText(this, "Please enter valid username.", Toast.LENGTH_SHORT).show();
            else if (email.isEmpty())
                Toast.makeText(this, "Please enter email.", Toast.LENGTH_SHORT).show();
            else if (password.isEmpty())
                Toast.makeText(this, "Please enter password.", Toast.LENGTH_SHORT).show();
            else if (phoneNumber.isEmpty())
                Toast.makeText(this, "Please enter mobile number.", Toast.LENGTH_SHORT).show();
            else if (secQuestion.isEmpty())
                Toast.makeText(this, "Security Question is required.", Toast.LENGTH_SHORT).show();
            else if (answer.isEmpty())
                Toast.makeText(this, "Answer is required.", Toast.LENGTH_SHORT).show();
            else {
                parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("username", userName));
                parameters.add(new BasicNameValuePair("email", email));
                parameters.add(new BasicNameValuePair("password", password));
                parameters.add(new BasicNameValuePair("phone", phoneNumber));
                parameters.add(new BasicNameValuePair("question", secQuestion));
                parameters.add(new BasicNameValuePair("answer", answer));
                parameters.add(new BasicNameValuePair("usertype", email));

                userRegisterTask = new UserRegisterTask();
                userRegisterTask.execute();
            }
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mLoadingDialog = new ProgressDialog(RegisterActivity.this);

        @Override
        protected void onPreExecute() {

            mLoadingDialog.setMessage("Registering ...");
            mLoadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            SRUser user= Connect.getInstance(getBaseContext()).userRegister(parameters);
            if (user != null && user.userEmail != null) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            userRegisterTask = null;
            mLoadingDialog.dismiss();

            if (success) {
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
                RegisterActivity.this.finish();
            } else {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            userRegisterTask = null;
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        strSecurityQuestion = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
