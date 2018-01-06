package net.m3aak.parentapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by BD-2 on 8/13/2015.
 */
public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener{

    RelativeLayout relLayCall,relLayWhatsappChat;
    EditText edtTextUName,edtTextMobNo,edtTextMessage;
    TextView send_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us_layout);

        relLayCall= (RelativeLayout) findViewById(R.id.relLayCall);
        relLayCall.setOnClickListener(this);
        relLayWhatsappChat= (RelativeLayout) findViewById(R.id.relLayWhatsappChat);
        relLayWhatsappChat.setOnClickListener(this);
        send_txt= (TextView) findViewById(R.id.send_txt);
        send_txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.relLayCall:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:009665447570"));
                startActivity(callIntent);
                                break;
            case R.id.send_txt:

                                break;
            case R.id.relLayWhatsappChat:
               /* Uri uri = Uri.parse("smsto:" + "0123456789");
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                i.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(i, ""));*/
                                break;
        }
    }


}
