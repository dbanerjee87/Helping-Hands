package co.chatsdk.ui.Survey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.CountryPickerListener;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.defines.Availability;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.NM;
import co.chatsdk.core.session.StorageManager;
import co.chatsdk.core.utils.ImageUtils;
import co.chatsdk.ui.R;
import co.chatsdk.ui.chat.MediaSelector;
import co.chatsdk.ui.main.BaseActivity;
import co.chatsdk.ui.manager.BaseInterfaceAdapter;
import co.chatsdk.ui.manager.InterfaceManager;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by ben on 8/14/17.
 */

public class surveyActivity extends BaseActivity {


    private Button surveyButton;
    private EditText s1, s2, s3, s4, s5, s6, s7, s8, s9;
    public static double riskScore = 0.0;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.chat_sdk_survey);


        initViews();
    }

    private void initViews() {

        surveyButton = (Button) findViewById(R.id.surveyButton);
        s1 = (EditText) findViewById(R.id.sres1);
        s2 = (EditText) findViewById(R.id.sres2);
        s3 = (EditText) findViewById(R.id.sres3);
        s4 = (EditText) findViewById(R.id.sres4);
        s5 = (EditText) findViewById(R.id.sres5);
        s6 = (EditText) findViewById(R.id.sres6);
        s7 = (EditText) findViewById(R.id.sres7);
        s8 = (EditText) findViewById(R.id.sres8);
        s9 = (EditText) findViewById(R.id.sres9);


        surveyButton.setOnClickListener(view -> calcRisk());


    }

    private void calcRisk () {

        int totalScore = Integer.parseInt(s1.getText().toString()) +
                Integer.parseInt(s2.getText().toString()) + Integer.parseInt(s3.getText().toString())
                + Integer.parseInt(s4.getText().toString()) + Integer.parseInt(s5.getText().toString())
                + Integer.parseInt(s6.getText().toString()) + Integer.parseInt(s7.getText().toString())
                + Integer.parseInt(s8.getText().toString()) + Integer.parseInt(s9.getText().toString());

        if (totalScore >= 20 || totalScore <=27) {
            riskScore = 5.0;
        } else if (totalScore >= 15 || totalScore <=19) {
            riskScore = 4.0;
        } else if (totalScore >= 10 || totalScore <=14) {
            riskScore = 3.0;
        } else if (totalScore >= 5 || totalScore <=9) {
            riskScore = 2.0;
        } else {
            riskScore = 1.0;
        }

    }

}
