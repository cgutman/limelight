package com.limelight.preferences;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import com.limelight.computers.ComputerManagerService;
import com.limelight.R;
import com.limelight.utils.Dialog;
import com.limelight.utils.UiHelper;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddComputerManually extends Activity {
	private Button addPcButton;
	private TextView hostText;
	private ComputerManagerService.ComputerManagerBinder managerBinder;
	private LinkedBlockingQueue<String> computersToAdd = new LinkedBlockingQueue<String>();
	private Thread addThread;
	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, final IBinder binder) {
			managerBinder = ((ComputerManagerService.ComputerManagerBinder)binder);
			startAddThread();
		}

		public void onServiceDisconnected(ComponentName className) {
			joinAddThread();
			managerBinder = null;
		}
	};
	
	private void doAddPc(String host) {
		String msg;
		boolean finish = false;
		try {
			InetAddress addr = InetAddress.getByName(host);
			
			if (!managerBinder.addComputerBlocking(addr)){
				msg = getResources().getString(R.string.addpc_fail);
			}
			else {
				msg = getResources().getString(R.string.addpc_success);
				finish = true;
			}
		} catch (UnknownHostException e) {
			msg = getResources().getString(R.string.addpc_unknown_host);
		}
		
		final boolean toastFinish = finish;
		final String toastMsg = msg;
		AddComputerManually.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(AddComputerManually.this, toastMsg, Toast.LENGTH_LONG).show();
				
				if (toastFinish && !isFinishing()) {
					// Close the activity
					AddComputerManually.this.finish();
				}
			}
		});
	}
	
	private void startAddThread() {
		addThread = new Thread() {
			@Override
			public void run() {
				while (!isInterrupted()) {
					String computer;
					
					try {
						computer = computersToAdd.take();
					} catch (InterruptedException e) {
						return;
					}
					
					doAddPc(computer);
				}
			}
		};
		addThread.setName("UI - AddComputerManually");
		addThread.start();
	}
	
	private void joinAddThread() {
		if (addThread != null) {
			addThread.interrupt();
			
			try {
				addThread.join();
			} catch (InterruptedException ignored) {}
			
			addThread = null;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		Dialog.closeDialogs();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (managerBinder != null) {
			joinAddThread();
			unbindService(serviceConnection);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_computer_manually);

        UiHelper.notifyNewRootView(this);

        this.addPcButton = (Button) findViewById(R.id.addPc);
		this.hostText = (TextView) findViewById(R.id.hostTextView);
		
		// Bind to the ComputerManager service
		bindService(new Intent(AddComputerManually.this,
				ComputerManagerService.class), serviceConnection, Service.BIND_AUTO_CREATE);
	
		addPcButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (hostText.getText().length() == 0) {
					Toast.makeText(AddComputerManually.this, getResources().getString(R.string.addpc_enter_ip), Toast.LENGTH_LONG).show();
					return;
				}
				
				Toast.makeText(AddComputerManually.this, getResources().getString(R.string.addpc_adding_pc), Toast.LENGTH_SHORT).show();
				computersToAdd.add(hostText.getText().toString());
			}
		});
	}
}
