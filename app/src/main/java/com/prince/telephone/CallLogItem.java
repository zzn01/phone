package com.prince.telephone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CallLogItem extends Activity implements OnClickListener {

    private EditText edtContactName, edtContactNumber;
    private LinearLayout item ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcontact);

//        item = (LinearLayout) findViewById(R.id.call_item);

 //       item.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        switch (v.getId()){
        case R.id.call_item:
/*
            if (item.isSelected()) {
                TextView et = (TextView) findViewById(R.id.operation);
                et.setVisibility(View.VISIBLE);
            }
            startActivity(intent);
*/
        }

    }

}
