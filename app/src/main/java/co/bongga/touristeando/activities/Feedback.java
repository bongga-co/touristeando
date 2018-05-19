package co.bongga.touristeando.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import co.bongga.touristeando.R;
import co.bongga.touristeando.interfaces.DataCallback;
import co.bongga.touristeando.models.HelpFeedback;
import co.bongga.touristeando.utils.DataManager;
import co.bongga.touristeando.utils.UtilityManager;

public class Feedback extends AppCompatActivity {
    private EditText fbName;
    private EditText fbEmail;
    private EditText fbMsg;
    private Button btnFbSubmit;

    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        loader = UtilityManager.showLoader(this, getString(R.string.loader_message));

        fbName = (EditText) findViewById(R.id.feedback_name);
        fbEmail = (EditText) findViewById(R.id.feedback_email);
        fbMsg = (EditText) findViewById(R.id.feedback_msg);

        btnFbSubmit = (Button) findViewById(R.id.btnSubmitFeedback);
        btnFbSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = fbName.getText().toString();
                String email = fbEmail.getText().toString();
                String msg = fbMsg.getText().toString();

                if(name.isEmpty()){
                    UtilityManager.showMessage(fbName, getString(R.string.feedback_empty_name));
                    return;
                }
                else if(!UtilityManager.validateName(name)){
                    UtilityManager.showMessage(fbName, getString(R.string.feedback_invalid_name));
                    return;
                }
                else if(email.isEmpty()){
                    UtilityManager.showMessage(fbEmail, getString(R.string.feedback_empty_email));
                    return;
                }
                else if(!UtilityManager.validateEmail(email)){
                    UtilityManager.showMessage(fbEmail, getString(R.string.feedback_invalid_email));
                    return;
                }
                else if(msg.isEmpty()){
                    UtilityManager.showMessage(fbMsg, getString(R.string.feedback_empty_msg));
                    return;
                }

                loader.show();

                HelpFeedback helpFeedback = new HelpFeedback(name, email, msg, getString(R.string.app_name));

                DataManager.saveFeedback(helpFeedback, new DataCallback() {
                    @Override
                    public void didReceiveData(List<Object> data) {
                        loader.dismiss();

                        if(data != null){
                            clearFields();
                            UtilityManager.showMessage(btnFbSubmit, getString(R.string.feedback_sent_successfully));
                        }
                        else{
                            UtilityManager.showMessage(btnFbSubmit, getString(R.string.feedback_error_sending));
                        }
                    }
                });
            }
        });
    }

    private void clearFields(){
        fbName.setText(null);
        fbEmail.setText(null);
        fbMsg.setText(null);
    }
}
