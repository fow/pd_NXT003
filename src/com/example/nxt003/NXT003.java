package com.example.nxt003;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class NXT003 extends Activity {

	private static final String TAG = "NXT003";
	
	public static TestSocket testSocket = new TestSocket();
	public static NxtController nxtController = new NxtController();
	static final int REQUEST_OPTION = 1;
	static final String DEFAULT_IP = "49.212.197.45";
	
	String ip = "";
	String port = "";
	
	private TextView textView1;
	private TextView textView2;
    
    EditText editText1;
    EditText editText2;
    
    int speed = 100;
    private int speed2 = 40;
	private boolean programMode = false;
	
	public static final String PREFS_NAME = "NXT003_PrefsFile";
	
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
    public void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    	
        super.onCreate(savedInstanceState);
		testSocket.reusableToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		nxtController.reusableToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        speed = settings.getInt("speed", 70);
        testSocket.f_showToast = settings.getBoolean("checkBox_display_log", false);
        testSocket.name = settings.getString("name", "");
        testSocket.pass = settings.getString("pass", "");
        ip              = settings.getString("ip", DEFAULT_IP);
        port            = settings.getString("port", "");
        nxtController.f_right   = settings.getBoolean("checkBox_morter_lr", false);
        nxtController.f_reverse = settings.getBoolean("checkBox_morter_reverse", false);

        setContentView(R.layout.activity_nxt003);
        
        textView1 = (TextView) findViewById(R.id.TextView01);
        textView2 = (TextView) findViewById(R.id.TextView02);
        
        textView1.setText("");
        textView2.setText("");
        
		((SeekBar) findViewById(R.id.seekBar1)).setMax(100);
		((SeekBar) findViewById(R.id.seekBar1)).setProgress(speed);
		((SeekBar) findViewById(R.id.seekBar1))
			.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				public void onStopTrackingTouch(SeekBar seekBar) { }
				public void onStartTrackingTouch(SeekBar seekBar) { }

				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					speed = progress;
					speed2 = speed - 20;
					if (speed2 < 0)
						speed2 = 0;
					textView1.setText("Morter:" + speed);
				}
			});
		
		editText1 =  (EditText) findViewById(R.id.editText1);
		editText2 =  (EditText) findViewById(R.id.editText2);
		editText1.setText(testSocket.name);
		editText2.setText(testSocket.pass);
		        
        textView1.setFocusable(true);
        textView1.setFocusableInTouchMode(true);
        textView1.requestFocus();/**/
        
        ViewIp();
        
        if(settings.getBoolean("checkBox_connect", false)){
        	socket_connect();
        }
    }
    
    void ViewIp(){
    	textView1.setText(ip + ":" + port);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_nxt003, menu);
        return true;
    }
    
    protected void onStop(){
        super.onStop();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("speed", speed);
		editor.putString("name", testSocket.name);
		editor.putString("pass", testSocket.pass);
		editor.putString("ip", ip);
		editor.putString("port", port);
		
		editor.commit();
     }
    
    public void btn10(View view){
    	socket_connect();
    }
    
    void socket_connect(){
    	String name = editText1.getText().toString();
    	String pass = editText2.getText().toString();
    	
    	if(name.equals("")){
    		Toast.makeText(this, "please input robo id", Toast.LENGTH_SHORT).show();
    	}else{
	    	if(!testSocket.f_connect){
	    		testSocket.name = name;
	    		testSocket.pass = pass;
	    		
		    	Log.i(TAG, "btn connect");
		    	testSocket.connect();
		    	Log.i(TAG, "btn connectSocketIO");
		    	testSocket.connectSocketIO(ip, port);
		    	
				Toast.makeText(this, "connectSocketIO", Toast.LENGTH_SHORT).show();
	    	}else{
				Toast.makeText(this, "already connect", Toast.LENGTH_SHORT).show();
	    	}
    	}
    }

    public void btn11(View view){
    	//socket test
    	if(testSocket.f_connect){
    		testSocket.emitMessage("test message android to server");
    	}
    }

    public void btn12(View view){
    	//close
    	if(testSocket.f_connect){
	    	Log.i(TAG, "btn disconnectSocketIO");
	    	testSocket.disconnectSocketIO();
	    	
			Toast.makeText(this, "disconnectSocketIO", Toast.LENGTH_SHORT).show();
    	}else{
			Toast.makeText(this, "not connect", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void btn13(View view){
    	//quit
		Toast.makeText(this, "quit", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "quit");

    	if(testSocket.f_connect){
	    	testSocket.disconnectSocketIO();
    	}
    	testSocket.mSocketManager_disconnect();
    	
    	if(nxtController.connected){
			nxtController.destroyBTCommunicator();
		}
    	
    	finish();
    }
    
    public void btn14(View view){
    	//NXT connect
    	if(nxtController.connected){
    		nxtController.destroyBTCommunicator();
    		Toast.makeText(this, "NXT disconnect:" + (nxtController.myBTCommunicator), Toast.LENGTH_SHORT).show();
    	}else{
    		selectNXT();
    	}
    }
    
    public void btn_setting(View view){
    	Intent i = new Intent(getApplicationContext(), Option.class);
    	startActivityForResult(i, REQUEST_OPTION);
    }
    
    void selectNXT() {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, NxtController.REQUEST_CONNECT_DEVICE);
    }
    
    public void btn15(View view){
        //test music
    	nxtController.TestMusic();
    }
    
    public void btn16(View view){
        //test nxt p11
    	if(nxtController.myBTCommunicator != null){
    		nxtController.startProgram("p11.rxe");
    	}
    }
    
    public void btn17(View view){
        //test nxt p12
    	if(nxtController.myBTCommunicator != null){
    		nxtController.startProgram("p12.rxe");
    	}
    }
    
    public void btn01(View view) { operateNXT(1);}
    public void btn02(View view) { operateNXT(2);}
    public void btn03(View view) { operateNXT(3);}
    public void btn04(View view) { operateNXT(4);}
    public void btn05(View view) { operateNXT(5);}
    public void btn06(View view) { operateNXT(6);}
    public void btn07(View view) { operateNXT(7);}
	public void btn08(View view) { operateNXT(8);}
	public void btn09(View view) { operateNXT(9);}
	
	protected void operateNXT(int x){
		if (nxtController.myBTCommunicator == null) { 
			return;
		}
		/*
		private Button button01;// up
		private Button button02;// down
		private Button button03;// right
		private Button button04;// left
		private Button button05;// music

		private Button button06;// R
		private Button button07;// L
		private Button button12;// stop
		private Button button13;// A1 action1
		private Button button14;// A2 action2
		*/
		if(programMode){
			switch(x){
				case 7: startProgram("p1.rxe"); break;
				case 1: startProgram("p2.rxe"); break;
				case 6: startProgram("p3.rxe"); break;
				case 3: startProgram("p4.rxe"); break;
				case 4: startProgram("p5.rxe"); break;
				case 2: startProgram("p6.rxe"); break;
				case 13: startProgram("p7.rxe"); break;
				case 12: startProgram("p8.rxe"); break;
				case 14: startProgram("p9.rxe"); break;
				case 5: nxtController.TestMusic();break;
				case 15: showProgramList(); break;
			}
		}else{
			switch(x){
				case 1: nxtController.updateMotorControl(speed, speed2); break;//l
				case 2: nxtController.updateMotorControl(speed, speed); break;
				case 3: nxtController.updateMotorControl(speed2, speed); break;//r
				case 4: nxtController.updateMotorControl(speed, -speed); break;//left
				case 5: nxtController.updateMotorControl(0, 0); 
						nxtController.sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.MOTOR_A, 0, 0);break;
				case 6: nxtController.updateMotorControl(-speed, speed); break;//right
				case 7: nxtController.sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.MOTOR_A_ACTION, speed, 0); break;
				case 8: nxtController.updateMotorControl(-speed, -speed); break;
				case 9: nxtController.sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.MOTOR_A_ACTION, -speed, 0); break;
			}
		}
	}
	
	void showProgramList(){
		if (nxtController.programList.size() == 0) {
			Toast.makeText(this, R.string.no_programs_found, Toast.LENGTH_SHORT).show();
			return;
        }
        
        FileDialog myFileDialog = new FileDialog(this, nxtController.programList);    		    	    		
        myFileDialog.show(true);//mRobotType == R.id.robot_type_lejos
	}
	
	 void startProgram(String programName){
		 //Toast.makeText(this, "programName:" + programName, Toast.LENGTH_SHORT).show();
		 nxtController.startProgram(programName);
	 }
	 
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case NxtController.REQUEST_CONNECT_DEVICE:			
				if (resultCode == Activity.RESULT_OK) {
					String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					nxtController.pairing = data.getExtras().getBoolean(DeviceListActivity.PAIRING);
					nxtController.startBTCommunicator(address, getResources());
					Toast.makeText(this, "startBT:" + address, Toast.LENGTH_SHORT).show();
				}
	
				break;
	
			case NxtController.REQUEST_ENABLE_BT:
				Toast.makeText(this, "REQUEST_ENABLE_BT", Toast.LENGTH_SHORT).show();
				break;
	
			case NxtController.TTS_CHECK_CODE:
				Toast.makeText(this, "TTS_CHECK_CODE", Toast.LENGTH_SHORT).show();
				break;
				
			case REQUEST_OPTION:
				if(data != null){
	    			//Log.d("", "" + data.getExtras().getString("ip"));

	    			testSocket.f_showToast = data.getExtras().getBoolean("checkBox_display_log", false);
	    			if(data.getExtras().getString("id") != null){
	    				testSocket.name        = data.getExtras().getString("id");
	    	        	testSocket.pass        = data.getExtras().getString("pass");
	        			editText1.setText(testSocket.name);
	        			editText2.setText(testSocket.pass);
	    			}
	    	        ip   = data.getExtras().getString("ip");
	    	        port = data.getExtras().getString("port");
	    	        
	    	        nxtController.f_right   = data.getExtras().getBoolean("checkBox_morter_lr", false);
	    	        nxtController.f_reverse = data.getExtras().getBoolean("checkBox_morter_reverse", false);
	    	        
	    			ViewIp();
	    			Toast.makeText(this, "change setting", Toast.LENGTH_SHORT).show();	
	    		}
				break;
		}
	}
    
}
