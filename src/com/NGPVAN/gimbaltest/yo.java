package com.NGPVAN.gimbaltest;

import android.app.Activity;
import android.os.Bundle;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.Place;
import com.gimbal.android.Visit;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.Communication;
import com.gimbal.android.Push;
import java.sql.Date;
import java.util.Collection;
import java.util.List;
import android.util.Log;
import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;
import android.content.Intent;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.DataOutputStream;
import java.io.IOException;


public class yo extends Activity
{

    private PlaceEventListener placeEventListener;
    private CommunicationListener communicationListener;
    private BeaconEventListener beaconSightingListener;
    private BeaconManager beaconManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	Gimbal.setApiKey(
			this.getApplication(), 
			"31d93371-3f7c-4f31-a6d3-67a4b0ed47f2"
			);

	//Set PlaceManager
	placeEventListener = new PlaceEventListener() {
            @Override
            public void onVisitStart(Visit visit) {
                // This will be invoked when a place is entered. 
	        // Example below shows a simple log upon enter
                Log.i("Info:", 
		    "Enter: " + visit.getPlace().getName() + 
		    ", at: " + new Date(visit.getArrivalTimeInMillis()));
            }

            @Override
            public void onVisitEnd(Visit visit) {
                // This will be invoked when a place is exited. 
		// Example below shows a simple log upon exit
                Log.i("Info:", 
		    "Exit: " + visit.getPlace().getName() + 
		    ", at: " + new Date(visit.getDepartureTimeInMillis()));
            }
        };
        PlaceManager.getInstance().addListener(placeEventListener);

	//Set CommunicationManager
	communicationListener = new CommunicationListener() {
            @Override
            public Collection<Communication> presentNotificationForCommunications(Collection<Communication> communications, Visit visit) {
                for (Communication comm : communications) {
                    Log.i("INFO", "Place Communication: " + visit.getPlace().getName() + ", message: " + comm.getTitle());

                }
                //allow Gimbal to show the notification for all communications
                return communications;
            }
        
            @Override
            public Collection<Communication> presentNotificationForCommunications(Collection<Communication> communications, Push push) {
                for (Communication comm : communications) {
                    Log.i("INFO", "Received a Push Communication with message: " + comm.getTitle());
                }
                //allow Gimbal to show the notification for all communications
                return communications;
            }

            @Override
            public void onNotificationClicked(List<Communication> communications) {
                Log.i("INFO", "Notification was clicked on");
		//open window
		Intent dialogIntent = new Intent(getBaseContext(), yo2.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(dialogIntent);
		//make api call
		MakeVANApiCall();
            }
        };
        CommunicationManager.getInstance().addListener(communicationListener);

	beaconSightingListener = new BeaconEventListener() {
            @Override
            public void onBeaconSighting(BeaconSighting sighting) {
                Log.i("INFO", sighting.toString());
            }
        };
        beaconManager = new BeaconManager();
        beaconManager.addListener(beaconSightingListener);

	PlaceManager.getInstance().startMonitoring();
        beaconManager.startListening();
        CommunicationManager.getInstance().startReceivingCommunications();

    }


    private boolean MakeVANApiCall()
    {
	OwnerInfo info = new OwnerInfo(this);
	String nameSafe = info.name.replace(' ', '_');

	try {
	    String url = "http://zz.vanjmoisan.dev.local:90/v4/ibeacons/savecontact?email=" + info.email + 
		    "&name=" + nameSafe;
	    URL urlol = new URL(url);
	    HttpURLConnection con = (HttpURLConnection) urlol.openConnection();
	    String urlParameters = "";

	    con.setDoOutput(true);
	    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	    wr.writeBytes(urlParameters);
	    wr.flush();
	    wr.close();
	    
	    int responseCode = con.getResponseCode();

	}
	catch (MalformedURLException e)
	{
	    return false;
	}
	catch (IOException e)
	{
	    return false;
	}
	return true;
	
    }

}
