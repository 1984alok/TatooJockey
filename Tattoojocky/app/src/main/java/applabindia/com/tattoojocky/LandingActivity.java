package applabindia.com.tattoojocky;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LandingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);



    }



    public void  btnClick (View v){
        switch (v.getId()){
            case R.id.btnSignin:
                startActivity(new Intent(LandingActivity.this,LoginActivity.class));
                break;
            case R.id.btnSignup:
                break;

        }
    }

}
