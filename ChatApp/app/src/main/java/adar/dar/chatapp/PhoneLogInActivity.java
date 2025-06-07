package adar.dar.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hbb20.CountryCodePicker;

public class PhoneLogInActivity extends AppCompatActivity implements View.OnClickListener {

    Button next;
    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.phone_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    private void initViews() {
        next=findViewById(R.id.PLIbtn);
        countryCodePicker=findViewById(R.id.CountryCodePicker);
        phoneInput=findViewById(R.id.ICusername);
        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
            if(!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Phone number not valid");
                return;
            }
            Intent intent= (new Intent(PhoneLogInActivity.this,OTPLogIn.class));
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);

    }
}