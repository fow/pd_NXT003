package com.example.nxt003;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class NxtController implements BTConnectable {
	
	private static final String TAG = "NxtController";
	
	BTCommunicator myBTCommunicator = null;
	Handler btcHandler;
	boolean pairing;
	boolean connected;
	Toast reusableToast;
	String old_result = "";
	int speed = 0;

	static final int REQUEST_CONNECT_DEVICE = 1000;
	static final int REQUEST_ENABLE_BT = 2000;
	static final int TTS_CHECK_CODE = 9991;

	String my_mac_address = "";

	int test_cnt = 0;
	TextView textView1;

	public List<String> programList;
	static final int MAX_PROGRAMS = 20;
	String programToStart = "";
	public boolean f_reverse = false;
	public boolean f_right = false;

	public void startBTCommunicator(String mac_address, Resources r) {
		myBTCommunicator = new BTCommunicator(this, myHandler,
				BluetoothAdapter.getDefaultAdapter(), r);
		btcHandler = myBTCommunicator.getHandler();

		myBTCommunicator.setMACAddress(mac_address);
		myBTCommunicator.start();

		my_mac_address = mac_address;
	}

	@Override
	public boolean isPairing() {
		return pairing;
	}

	public void showToast(String textToShow, int length) {
		reusableToast.setText(textToShow);
		reusableToast.setDuration(length);
		reusableToast.show();
	}

	/**
	 * Receive messages from the BTCommunicator
	 */
	final Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {
			switch (myMessage.getData().getInt("message")) {
			case BTCommunicator.DISPLAY_TOAST:
				showToast(myMessage.getData().getString("toastText"), Toast.LENGTH_SHORT);
				// showToast("NXT DISPLAY_TOAST", Toast.LENGTH_SHORT);
				break;
			case BTCommunicator.STATE_CONNECTED:
				Log.d("", "BTCommunicator.STATE_CONNECTED");
				showToast("NXT STATE_CONNECTED", Toast.LENGTH_SHORT);
				connected = true;

				programList = new ArrayList<String>();
				sendBTCmessage(BTCommunicator.NO_DELAY,	BTCommunicator.GET_FIRMWARE_VERSION, 0, 0);

				break;
			case BTCommunicator.MOTOR_STATE:
				showToast("NXT MOTOR_STATE", Toast.LENGTH_SHORT);
				break;

			case BTCommunicator.STATE_CONNECTERROR_PAIRING:
				showToast("NXT STATE_CONNECTERROR_PAIRING", Toast.LENGTH_SHORT);
				destroyBTCommunicator();
				break;

			case BTCommunicator.STATE_CONNECTERROR:
				showToast("NXT STATE_CONNECTERROR", Toast.LENGTH_SHORT);
				destroyBTCommunicator();
				break;

			case BTCommunicator.STATE_RECEIVEERROR:
				showToast("NXT STATE_RECEIVEERROR", Toast.LENGTH_SHORT);
				destroyBTCommunicator();
				break;

			case BTCommunicator.STATE_SENDERROR:
				showToast("NXT STATE_SENDERROR", Toast.LENGTH_SHORT);
				destroyBTCommunicator();
				break;

			case BTCommunicator.FIRMWARE_VERSION:

				if (myBTCommunicator != null) {
					//byte[] firmwareMessage = myBTCommunicator.getReturnMessage();
					sendBTCmessage(BTCommunicator.NO_DELAY,	BTCommunicator.FIND_FILES, 0, 0);
				}

				break;
			case BTCommunicator.FIND_FILES:

				if (myBTCommunicator != null) {
					byte[] fileMessage = myBTCommunicator.getReturnMessage();
					String fileName = new String(fileMessage, 4, 20);
					fileName = fileName.replaceAll("\0", "");

					if (fileName.endsWith(".nxj") || fileName.endsWith(".rxe")) {
						programList.add(fileName);
					}

					if (programList.size() <= MAX_PROGRAMS)
						sendBTCmessage(BTCommunicator.NO_DELAY,
								BTCommunicator.FIND_FILES, 1,
								byteToInt(fileMessage[3]));
				}

				break;
			case BTCommunicator.PROGRAM_NAME:
				
				//showToast("endsWith rxe " + name,Toast.LENGTH_SHORT);
				if (myBTCommunicator != null) {
					byte[] returnMessage = myBTCommunicator.getReturnMessage();
					showToast("returnMessage[2] " + returnMessage[2],Toast.LENGTH_SHORT);
					startRXEprogram(returnMessage[2]);
				}

				break;
			}
		}
	};

	/**
	 * Starts a program on the NXT robot.
	 * 
	 * @param name
	 *            The program name to start. Has to end with .rxe on the LEGO
	 *            firmware and with .nxj on the leJOS NXJ firmware.
	 */
	public void startProgram(String name) {
		sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.START_PROGRAM, name);
	}

	public void startRXEprogram(byte status) {
		if (status == 0x00) {
			sendBTCmessage(BTCommunicator.NO_DELAY,
					BTCommunicator.STOP_PROGRAM, 0, 0);
			sendBTCmessage(1000, BTCommunicator.START_PROGRAM, programToStart);
		} else {
			sendBTCmessage(BTCommunicator.NO_DELAY,
					BTCommunicator.START_PROGRAM, programToStart);
		}
	}

	public void destroyBTCommunicator() {

		if (myBTCommunicator != null) {
			sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.DISCONNECT,
					0, 0);
			myBTCommunicator = null;
		}
		my_mac_address = "";
		connected = false;
	}

	public void sendBTCmessage(int delay, int message, int value1, int value2) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("message", message);
		myBundle.putInt("value1", value1);
		myBundle.putInt("value2", value2);
		Message myMessage = myHandler.obtainMessage();
		myMessage.setData(myBundle);

		if (delay == 0)
			btcHandler.sendMessage(myMessage);

		else
			btcHandler.sendMessageDelayed(myMessage, delay);
	}

	/**
	 * Sends the message via the BTCommuncator to the robot.
	 * 
	 * @param delay
	 *            time to wait before sending the message.
	 * @param message
	 *            the message type (as defined in BTCommucator)
	 * @param String
	 *            a String parameter
	 */
	public void sendBTCmessage(int delay, int message, String name) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("message", message);
		myBundle.putString("name", name);
		Message myMessage = myHandler.obtainMessage();
		myMessage.setData(myBundle);

		if (delay == 0)
			btcHandler.sendMessage(myMessage);
		else
			btcHandler.sendMessageDelayed(myMessage, delay);
	}

	public void updateMotorControl(int left, int right) {

		if (myBTCommunicator != null) {
			// send messages via the handler
			int v = 1;

			if (f_reverse) {
				left = left * -1;
				right = right * -1;
			}

			if (f_right) {
				int tmp = left;
				left = right;
				right = tmp;
			}
			sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.MOTOR_B, left * v, 0);
			sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.MOTOR_C,	right * v, 0);
		}
	}

	public void stopMotorControl() {
		stopMotorControl(BTCommunicator.NO_DELAY);
	}
	
	public void stopMotorControl(int delay) {

		if (myBTCommunicator != null) {
			// send messages via the handler
			sendBTCmessage(delay, BTCommunicator.MOTOR_A, 0, 0);
			sendBTCmessage(delay, BTCommunicator.MOTOR_B, 0, 0);
			sendBTCmessage(delay, BTCommunicator.MOTOR_C, 0, 0);
		}
	}

	public void TestMusic() {
		if (myBTCommunicator != null) {
			sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.DO_BEEP, 392, 100);
			sendBTCmessage(200, BTCommunicator.DO_BEEP, 440, 100);
			sendBTCmessage(400, BTCommunicator.DO_BEEP, 494, 100);
			sendBTCmessage(600, BTCommunicator.DO_BEEP, 523, 100);
			sendBTCmessage(800, BTCommunicator.DO_BEEP, 587, 300);
			sendBTCmessage(1200, BTCommunicator.DO_BEEP, 523, 300);
			sendBTCmessage(1600, BTCommunicator.DO_BEEP, 494, 300);
		}
	}

	public void WebControl(String tmp) {
	    	
    	String pfx = tmp.substring(0, 1);
    	if(pfx.equals("c")){
    		if (tmp.length() >= 13) {
    			Log.i(TAG, " WebControl " + tmp);
    			WebControl_normal(tmp.substring(1, 13));
    		}
    	}else if(pfx.equals("p")){
    		if (tmp.length() >= 4) {
    			Log.i(TAG, " WebControl " + tmp);
    			WebControl_pg(tmp.substring(1, 4));
    		}
    	}
	}
	
	public void WebControl_normal(String now_result) {
		//ADK002.testSocket.showToast(":" + now_result, Toast.LENGTH_SHORT);
		//Log.i(TAG, " WebControl " + now_result);
	
		if(!old_result.equals(now_result)){
			old_result = now_result;
		}else{
			//return;
		}
		
		try{
			String tmp2 = now_result;
			
			int morter_a_speed = ConvertSpeed(Integer.parseInt(tmp2.substring(0, 3)));
			int morter_b_speed = ConvertSpeed(Integer.parseInt(tmp2.substring(3, 6)));
			int morter_c_speed = ConvertSpeed(Integer.parseInt(tmp2.substring(6, 9)));
			int delay = Integer.parseInt(tmp2.substring(9, 12));
		
			if (myBTCommunicator != null) {
				if (morter_a_speed == 0 && morter_b_speed == 0 && morter_c_speed == 0) {
					stopMotorControl();
				} else {
					if (morter_a_speed != 0) {
						sendBTCmessage(BTCommunicator.NO_DELAY,	BTCommunicator.MOTOR_A, morter_a_speed, 0);
					}else{
						sendBTCmessage(BTCommunicator.NO_DELAY,	BTCommunicator.MOTOR_A, 0, 0);
					}
					updateMotorControl(morter_c_speed, morter_b_speed);
				}
				
				if(delay != 0){
					stopMotorControl(delay * 100);
				}
			}
		}catch(Exception e){
			Log.i(TAG, " WebControl_normal Exception " + e.toString());
		}
	}
	
	int ConvertSpeed(int v) {
		if (v > 100)
			v = (v - 100) * -1;
		return v;
	}

	public void WebControl_pg(String now_result) {
		try {

			int x =  Integer.parseInt(now_result);

			if (myBTCommunicator != null) {
				if (x == 0) {
					showToast("program: stop",Toast.LENGTH_SHORT);
					stopMotorControl();
				} else {
					showToast("program: p" + x + ".rxe",Toast.LENGTH_SHORT);
					startProgram("p" + x + ".rxe");
				}
			}
		} catch (Exception e) {
			showToast("WebControl_pg Exception " + e.toString(),	Toast.LENGTH_SHORT);
		}
	}
/**/

	private int byteToInt(byte byteValue) {
		int intValue = (byteValue & (byte) 0x7f);

		if ((byteValue & (byte) 0x80) != 0)
			intValue |= 0x80;

		return intValue;
	}
	
	void action(int target_morter, int  action2_up, int  action2_down, int target_speed){
		if ((action2_up == 1 || action2_down == 1) && target_morter > 0) {
			int do_target_morter = 1;
			switch (target_morter) {
				case 1:	do_target_morter = BTCommunicator.MOTOR_A; break;
				case 2:	do_target_morter = BTCommunicator.MOTOR_B; break;
				case 3: do_target_morter = BTCommunicator.MOTOR_C; break;
			}
			if (action2_up == 1) {
				sendBTCmessage(BTCommunicator.NO_DELAY, do_target_morter, target_speed, 0);
			} else if (action2_down == 1) {
				sendBTCmessage(BTCommunicator.NO_DELAY, do_target_morter, -target_speed, 0);
			}
		}
	}
}
