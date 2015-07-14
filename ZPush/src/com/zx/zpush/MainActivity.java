package com.zx.zpush;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.teleal.cling.support.avtransport.callback.Play;
import org.teleal.cling.support.avtransport.callback.SetAVTransportURI;
import org.teleal.cling.support.connectionmanager.ConnectionManagerService;
import org.teleal.cling.support.connectionmanager.callback.GetProtocolInfo;
import org.teleal.cling.support.connectionmanager.callback.PrepareForConnection;
import org.teleal.cling.support.model.ProtocolInfo;
import org.teleal.cling.support.model.ProtocolInfos;



public class MainActivity extends Activity {
	
	private String s="AVTransport";
	private String s1="ConnectionManager";
//	ProtocolInfo sink;
	
	private Dialog listdialog;
	private Button btn,search;
	private ListView devicelist;
	private ArrayAdapter<DeviceDisplay> listAdapter;
	
	private AndroidUpnpService upnpService;
	
	private RegistryListener registryListener = new BrowseRegistryListener();
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {

			upnpService = (AndroidUpnpService) service;

			// Refresh the list with all known devices
			listAdapter.clear();
			for (Device device : upnpService.getRegistry().getDevices()) {
			((BrowseRegistryListener) registryListener).deviceAdded(device);
			}
			// Getting ready for future device advertisements
			upnpService.getRegistry().addListener(registryListener);
			// Search asynchronously for all devices
			upnpService.getControlPoint().search();
			}
			 
			public void onServiceDisconnected(ComponentName className) {
			upnpService = null;
			}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btn=(Button)findViewById(R.id.start_btn);
		
		btn.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v)	{
				if (upnpService != null) { 
			        upnpService.getRegistry().removeAllRemoteDevices(); 
			        upnpService.getControlPoint().search(); 
			    } 
				showDialog();
			}
		});
		
//		devicelist = (ListView) findViewById(R.id.devicelist);
//		listAdapter = new ArrayAdapter<DeviceDisplay>( this, android.R.layout.simple_list_item_1 );
//	    devicelist.setAdapter(listAdapter);
//	    
//	    devicelist.setOnItemClickListener(new OnItemClickListener(){
//	    	public void onItemClick(AdapterView<?> parent, View v,
//					int position, long id) {
//	    		Toast.makeText(getApplicationContext(), "即将转到第"+position+"项播放",  Toast.LENGTH_SHORT).show();
//	    		DeviceDisplay devicePlay=listAdapter.getItem(position);
//	    		Device device= devicePlay.getDevice();
//	    		String url="http://home.ustc.edu.cn/~zx1064/messi.mp4";
////	    		String url="http://tv.sohu.com/20131219/n392032143.shtml";
////	    		String url="http://p.you.video.sina.com.cn/swf/quotePlayer20130723_V4_4_42_4.swf?vid=122142766&as=0";
////	    		String url="http://freedownloads.last.fm/download/533190694/more%2Bthan%2Ba%2Bdime.mp3";
////	    		GetInfo(device);
//	    		executeAVTransportURI(device,url);
//	    		executePlay(device);
//
//	    	}
//	    });
//
//	        getApplicationContext().bindService(
//	            new Intent(this, MyUpnpService.class),
//	            serviceConnection,
//	            Context.BIND_AUTO_CREATE
//	        );
	}
	public void showDialog(){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("可选择设备……");
		final LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(R.layout.listview, null);
		
		getApplicationContext().bindService(
	            new Intent(this, MyUpnpService.class),
	            serviceConnection,
	            Context.BIND_AUTO_CREATE
	        );
		devicelist = (ListView) v.findViewById(R.id.devicelist);
		listAdapter = new ArrayAdapter<DeviceDisplay>( this, android.R.layout.simple_list_item_1 );
	    devicelist.setAdapter(listAdapter);

		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("搜索", new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
//				if (upnpService != null) { 
//			        upnpService.getRegistry().removeAllRemoteDevices(); 
//			        upnpService.getControlPoint().search(); 
//			    } 
			}
		});
		listdialog=builder.create();
		listdialog.show();
		
	    devicelist.setOnItemClickListener(new OnItemClickListener(){
	    	public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
	    		Toast.makeText(getApplicationContext(), "即将转到第"+position+"项播放",  Toast.LENGTH_SHORT).show();
	    		DeviceDisplay devicePlay=listAdapter.getItem(position);
	    		Device device= devicePlay.getDevice();
//	    		String url="http://stream2.ahtv.cn/ahws/cd/live.m3u8";
//	    		String url=Environment.getExternalStorageDirectory().getAbsolutePath()+"/tvfun/imagecache/2.png";
	    		String url="http://home.ustc.edu.cn/~zx1064/tvfun/channnle_image/1.png";
	    		Uri.parse(url);
	    		Log.e("URL",url);
	    		GetInfo(device);
	    		executeAVTransportURI(device,url);
	    		executePlay(device);
	    		listdialog.dismiss();
	    	}
	    });
	}

	
	 protected void onDestroy() {
	        super.onDestroy();
	        if (upnpService != null) {
	            upnpService.getRegistry().removeListener(registryListener);
	        }
	        getApplicationContext().unbindService(serviceConnection);
	    }

	 
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) { 
	    menu.add(0, 0, 0, R.string.search) 
	        .setIcon(android.R.drawable.ic_menu_search); 
	    return true; 
	} 
	 
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) { 
	    if (item.getItemId() == 0 && upnpService != null) { 
	        upnpService.getRegistry().removeAllRemoteDevices(); 
	        upnpService.getControlPoint().search(); 
	    } 
	    return false; 
	} 

	
	class BrowseRegistryListener extends DefaultRegistryListener { 
		 
	    @Override 
	    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) { 
	        deviceAdded(device); 
	    } 
	 
	    @Override 
	    public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) { 
	        
	        deviceRemoved(device); 
	    } 
	 
	    @Override 
	    public void remoteDeviceAdded(Registry registry, RemoteDevice device) { 
	        deviceAdded(device); 
	    } 
	 
	    @Override 
	    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) { 
	        deviceRemoved(device); 
	    } 
	 
	    @Override 
	    public void localDeviceAdded(Registry registry, LocalDevice device) { 
	        deviceAdded(device); 
	    } 
	 
	    @Override 
	    public void localDeviceRemoved(Registry registry, LocalDevice device) { 
	        deviceRemoved(device); 
	    } 
	 
	    public void deviceAdded(final Device device) { 
	        runOnUiThread(new Runnable() { 
	            public void run() { 
	                DeviceDisplay d = new DeviceDisplay(device); 
	                int position = listAdapter.getPosition(d); 
	                if (position >= 0) { 
	                    // Device already in the list, re-set new value at same position 
	                    listAdapter.remove(d); 
	                    listAdapter.insert(d, position); 
	                } else { 
	                    listAdapter.add(d); 
	                } 
//	                listAdapter.sort(DISPLAY_COMPARATOR);
					 listAdapter.notifyDataSetChanged();
	            } 
	        }); 
	    } 
	 
	    public void deviceRemoved(final Device device) { 
	        runOnUiThread(new Runnable() { 
	            public void run() { 
	                listAdapter.remove(new DeviceDisplay(device)); 
	            } 
	        }); 
	    } 
	}
	
	public void executeAVTransportURI(Device device, String uri){
		
		ServiceId AVTransportId = new UDAServiceId(s);
		Service service = device.findService(AVTransportId);
		ActionCallback callback = new SetAVTransportURI(service, uri){

			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1,
					String arg2) {
				// TODO Auto-generated method stub
				Log.e("SetAVTransportURI","failed^^^^^^^");
			}
			
		};
		upnpService.getControlPoint().execute(callback);

	}
	public void executePlay(Device device){
		ServiceId AVTransportId = new UDAServiceId(s);
		Service service = device.findService(AVTransportId);
		ActionCallback playcallback = new Play(service){

			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1,
					String arg2) {
				// TODO Auto-generated method stub
				Log.e("Play","failed^^^^^^^");
			}
			
		};
		upnpService.getControlPoint().execute(playcallback);

	}

	public void GetInfo(Device device){
		ServiceId AVTransportId = new UDAServiceId(s1);
		Service service = device.findService(AVTransportId);
		ActionCallback getInfocallback=new GetProtocolInfo(service){

			@Override
			public void received(ActionInvocation actionInvocation,
					ProtocolInfos sinkProtocolInfos,
					ProtocolInfos sourceProtocolInfos) {
				// TODO Auto-generated method stub
				Log.v("sinkProtocolInfos",sinkProtocolInfos.toString());
				Log.v("sourceProtocolInfos",sourceProtocolInfos.toString());
			}

			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1,
					String arg2) {
				// TODO Auto-generated method stub
				Log.v("GetProtocolInfo","failed^^^^^^^");
			}
			
		};
		upnpService.getControlPoint().execute(getInfocallback);
	}
	
	public void PrepareConn(Device device){
		ServiceId AVTransportId = new UDAServiceId(s1);
		Service service = device.findService(AVTransportId);
		ActionCallback prepareConncallback=new PrepareForConnection(service,null,null,-1, null){

			@Override
			public void received(ActionInvocation invocation, int connectionID,
					int rcsID, int avTransportID) {
				// TODO Auto-generated method stub
				Log.v("avTransportID",Integer.toString(avTransportID));
			}

			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1,
					String arg2) {
				// TODO Auto-generated method stub
				Log.v("PrepareForConnection","failed^^^^^^^");
			}
			
		};
		upnpService.getControlPoint().execute(prepareConncallback);
	}



}
