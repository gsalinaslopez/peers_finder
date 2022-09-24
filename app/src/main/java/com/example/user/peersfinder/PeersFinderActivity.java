package com.example.user.peersfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Method;

public class PeersFinderActivity extends AppCompatActivity {

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel wifiP2pManagerChannel;
    private BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peers_finder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        setWifiP2pOperatingChannel(6,6);

        intentFilterInitialization();

        wifiP2PManagerChannelInitialization();

    }

    @Override
    public void onResume()  {
        super.onResume();
        this.broadcastReceiver = new WifiDirectBroadcastReceiver(wifiP2pManager, wifiP2pManagerChannel, this);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause()   {
        super.onPause();
        unregisterReceiver(this.broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_peers_finder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search_peers) {
            this.startSearchPeers();
            return true;
        }
        else if (id == R.id.action_restart_wifi_interface)  {
            this.wifiManager.setWifiEnabled(false);
            while(this.wifiManager.isWifiEnabled())    {
                ;
            }

            this.wifiManager.setWifiEnabled(true);
            while(!this.wifiManager.isWifiEnabled())    {
                ;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.startSearchPeers();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startSearchPeers() {
        /*FragmentManager fm = getSupportFragmentManager();
        final DevicesListFragment devicesListFragment = (com.example.user.peersfinder.DevicesListFragment) fm.findFragmentById(R.id.devices_list_fragment);
        devicesListFragment.onInitiateDiscovery();*/

        Log.d("NSBULLSHIT", "DISCOVER PEERS START");
        this.wifiP2pManager.discoverPeers(this.wifiP2pManagerChannel, new WifiP2pManager.ActionListener()   {
            @Override
            public void onSuccess() {
                Log.d("NSBULLSHIT", "DISCOVER PEERS SUCCESS");
            }

            @Override
            public void onFailure(int i)    {
                Toast.makeText(PeersFinderActivity.this, "Discovery failed", Toast.LENGTH_SHORT);
                Log.d("NSBULLSHIT", "DISCOVER PEERS FAILURE");
            }
        });
        Log.d("NSBULLSHIT", "DISCOVER PEERS END");
    }
    /*
     * Miscellaneous functions
     */
    private void intentFilterInitialization()   {
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void wifiP2PManagerChannelInitialization()  {
        this.wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        this.wifiP2pManagerChannel = this.wifiP2pManager.initialize(this, getMainLooper(), null);
    }

    private void setWifiP2pOperatingChannel(final int listeningChannel, final int operatingChannel) {
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for(int i = 0; i < methods.length; i++) {
                Log.d("LOOKING", "LOOKING FOR METHODS " + methods[i].getName());
                if (methods[i].getName().equals("setWifiP2pChannels"))  {
                    Log.d("FOUND", "FOUND SETCHANNELMETHOD");
                    methods[i].invoke(this.wifiP2pManager, this.wifiP2pManagerChannel, listeningChannel, operatingChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(PeersFinderActivity.this, "Channel set to: \n" + "listening:" + Integer.toString(listeningChannel) + "\nOperating:" + Integer.toString(operatingChannel), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int reason) {
                            Toast.makeText(PeersFinderActivity.this, "Channel set to: \n" + "listening:" + Integer.toString(listeningChannel) + "\nOperating:" + Integer.toString(operatingChannel) + "\nFailed", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
                }
            }
        } catch (Exception e)   {

        }
    }
}
