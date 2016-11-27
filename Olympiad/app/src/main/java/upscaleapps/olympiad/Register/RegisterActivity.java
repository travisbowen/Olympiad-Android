package upscaleapps.olympiad.Register;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import upscaleapps.olympiad.R;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView regIV;
    private EditText regNameET;
    private EditText regAgeET;
    private EditText regGenderET;
    private EditText regLocationET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regIV = (ImageView)findViewById(R.id.regIV);
        regNameET = (EditText)findViewById(R.id.regNameET);
        regAgeET = (EditText)findViewById(R.id.regAgeET);
        regGenderET = (EditText)findViewById(R.id.regGenderET);
        regLocationET = (EditText)findViewById(R.id.regLocationET);
        Button backBT = (Button)findViewById(R.id.backBT);
        Button nextBT = (Button)findViewById(R.id.nextBT);

        backBT.setOnClickListener(this);
        nextBT.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.backBT:
                back();
                break;

            case R.id.nextBT:
                next();
                break;
        }
    }

    //Go back to login
    public void back(){
//        Intent intent = new Intent(this, RegisterActivity.class);
//        startActivity(intent);
    }


    //Go to register 2
    public void next(){
//        Intent intent = new Intent(this, TabBarActivity.class);
//        startActivity(intent);
    }
}
