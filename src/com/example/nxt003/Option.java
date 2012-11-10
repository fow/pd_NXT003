package com.example.nxt003;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Option extends Activity {
	
	private TextView textView1;
	
	EditText EditText_id1;
	EditText EditText_id2;
	EditText EditText_id3;
	EditText EditText_pass1;
	EditText EditText_pass2;
	EditText EditText_pass3;

	EditText EditText_ip1;
	EditText EditText_ip2;
	EditText EditText_ip3;
	EditText EditText_port1;
	EditText EditText_port2;
	EditText EditText_port3;

	int _checkedId = 0;
	int _checkedIp = 0;
	
	CheckBox checkBox_morter_lr;
	CheckBox checkBox_morter_reverse;
	CheckBox checkBox_connect;
	CheckBox checkBox_display_log;
	
	public static final String PREFS_NAME = NXT003.PREFS_NAME;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        
        textView1 = (TextView) findViewById(R.id.textView1);
        
        EditText_id1 =   (EditText) findViewById(R.id.EditText_id1);
        EditText_id2 =   (EditText) findViewById(R.id.EditText_id2);
        EditText_id3 =   (EditText) findViewById(R.id.EditText_id3);
        EditText_pass1 = (EditText) findViewById(R.id.EditText_pass1);
        EditText_pass2 = (EditText) findViewById(R.id.EditText_pass2);
        EditText_pass3 = (EditText) findViewById(R.id.EditText_pass3);
        
        EditText_id1.setFilters(filters1);
        EditText_id2.setFilters(filters1);
        EditText_id3.setFilters(filters1);
        EditText_pass1.setFilters(filters1);
        EditText_pass2.setFilters(filters1);
        EditText_pass3.setFilters(filters1);

    	EditText_ip1 =   (EditText) findViewById(R.id.EditText_ip1);
    	EditText_ip2 =   (EditText) findViewById(R.id.EditText_ip2);
    	EditText_ip3 =   (EditText) findViewById(R.id.EditText_ip3);
    	EditText_port1 = (EditText) findViewById(R.id.EditText_port1);
    	EditText_port2 = (EditText) findViewById(R.id.EditText_port2);
    	EditText_port3 = (EditText) findViewById(R.id.EditText_port3);
    	
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        EditText_id1.setText(settings.getString("id1", ""));
        EditText_id2.setText(settings.getString("id2", ""));
        EditText_id3.setText(settings.getString("id3", ""));
        EditText_pass1.setText(settings.getString("pass1", ""));
        EditText_pass2.setText(settings.getString("pass2", ""));
        EditText_pass3.setText(settings.getString("pass3", ""));

        EditText_ip1.setText(settings.getString("ip1", NXT003.DEFAULT_IP));
        EditText_ip2.setText(settings.getString("ip2", ""));
        EditText_ip3.setText(settings.getString("ip3", ""));
        EditText_port1.setText(settings.getString("port1", ""));
        EditText_port2.setText(settings.getString("port2", ""));
        EditText_port3.setText(settings.getString("port3", ""));
        
        _checkedId = settings.getInt("_checkedId", R.id.radio_id1);
        _checkedIp = settings.getInt("_checkedIp", R.id.radio_ip1);
        
        RadioGroup radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        radioGroup1.check(_checkedId);
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) { 
                _checkedId = checkedId;
            }
        });

        RadioGroup radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
        radioGroup2.check(_checkedIp);
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) { 
                _checkedIp = checkedId;
            }
        });
        
        checkBox_morter_lr = (CheckBox) findViewById(R.id.checkBox_morter_lr);
        checkBox_morter_lr.setChecked(settings.getBoolean("checkBox_morter_lr", false));
        checkBox_morter_reverse = (CheckBox) findViewById(R.id.checkBox_morter_reverse);
        checkBox_morter_reverse.setChecked(settings.getBoolean("checkBox_morter_reverse", false));

        checkBox_connect = (CheckBox) findViewById(R.id.checkBox_connect);
        checkBox_connect.setChecked(settings.getBoolean("checkBox_connect", false));
        checkBox_display_log = (CheckBox) findViewById(R.id.checkBox_display_log);
        checkBox_display_log.setChecked(settings.getBoolean("checkBox_display_log", false));
        
        textView1.setFocusable(true);
        textView1.setFocusableInTouchMode(true);
        textView1.requestFocus();/**/
    }
	
	private InputFilter[] filters1 = { new MyFilter(), new InputFilter.LengthFilter(12) };
	class MyFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if( source.toString().matches("^[-_@\\.a-zA-Z0-9]+$") ){
                return source;
            }else{
                return "";
            }
        }
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    
    void save(){
    	
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        
        editor.putString("id1", EditText_id1.getText().toString());
        editor.putString("id2", EditText_id2.getText().toString());
        editor.putString("id3", EditText_id3.getText().toString());
        editor.putString("pass1", EditText_pass1.getText().toString());
        editor.putString("pass2", EditText_pass2.getText().toString());
        editor.putString("pass3", EditText_pass3.getText().toString());

        editor.putString("ip1", EditText_ip1.getText().toString());
        editor.putString("ip2", EditText_ip2.getText().toString());
        editor.putString("ip3", EditText_ip3.getText().toString());
        editor.putString("port1", EditText_port1.getText().toString());
        editor.putString("port2", EditText_port2.getText().toString());
        editor.putString("port3", EditText_port3.getText().toString());

        editor.putInt("_checkedId", _checkedId);
        editor.putInt("_checkedIp", _checkedIp);

        editor.putBoolean("checkBox_morter_lr", checkBox_morter_lr.isChecked());
        editor.putBoolean("checkBox_morter_reverse", checkBox_morter_reverse.isChecked());
        editor.putBoolean("checkBox_connect", checkBox_connect.isChecked());
        editor.putBoolean("checkBox_display_log", checkBox_display_log.isChecked());
        editor.commit();
        
        
    	Intent intent = new Intent();
        Bundle data = new Bundle();
        
        String id,pass,ip,port;
        id = pass = ip = port = "";
        switch(_checkedId){
        	case R.id.radio_id1: id = EditText_id1.getText().toString();
        						 pass = EditText_pass1.getText().toString(); break;
        	case R.id.radio_id2: id = EditText_id2.getText().toString();
        						 pass = EditText_pass2.getText().toString(); break;
        	case R.id.radio_id3: id = EditText_id3.getText().toString();
        						 pass = EditText_pass3.getText().toString(); break;
        }
        
        switch(_checkedIp){
	    	case R.id.radio_ip1: ip = EditText_ip1.getText().toString();
	    						 port = EditText_port1.getText().toString(); break;
	    	case R.id.radio_ip2: ip = EditText_ip2.getText().toString();
	    						 port = EditText_port2.getText().toString(); break;
	    	case R.id.radio_ip3: ip = EditText_ip3.getText().toString();
	    						 port = EditText_port3.getText().toString(); break;
	    }
        
       	data.putString("id", id);
       	data.putString("pass", pass);
        data.putString("ip", ip);
        data.putString("port", port);
        
        data.putBoolean("checkBox_morter_lr", checkBox_morter_lr.isChecked());
        data.putBoolean("checkBox_morter_reverse", checkBox_morter_reverse.isChecked());
        data.putBoolean("checkBox_connect", checkBox_connect.isChecked());
        data.putBoolean("checkBox_display_log", checkBox_display_log.isChecked());

        intent.putExtras(data);
        setResult(RESULT_OK, intent);
        finish();
    }
    
    public void btn01(View view){
    	save();
    }
    
    public void btn02(View view){
    	finish();
    }
    
}
