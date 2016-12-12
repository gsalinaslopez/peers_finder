package com.example.user.peersfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;

/**
 * Created by User on 10/5/2016.
 */

public class WifiDirectBroadcastReceiver extends BroadcastReceiver  {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private PeersFinderActivity activity;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       PeersFinderActivity activity)    {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent)   {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))    {

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d("NSBULLSHIT", "P2P state enabled");
            } else {
                Log.d("NSBULLSHIT", "P2P state not enabled");
            }
            Log.d("NSBULLSHIT", "P2P state changed - " + state);

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))   {
            if (this.manager != null)   {
                Log.d("NSBULLSHIT", "P2P peers list changed");
                this.manager.requestPeers(this.channel, this.pll);//(WifiP2pManager.PeerListListener) activity.getFragmentManager().findFragmentById(R.id.devices_list_fragment));
                Log.d("NSBULLSHIT", "P2P peers requested");
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))   {

        }
    }

    private WifiP2pManager.PeerListListener pll = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            Log.d("NSBULLSHIT", "HERE IT WORKS");

            FragmentManager fm = activity.getSupportFragmentManager();
            final DevicesListFragment devicesListFragment = (com.example.user.peersfinder.DevicesListFragment) fm.findFragmentById(R.id.devices_list_fragment);
            devicesListFragment.peersAvailable(peers);
        }
    };
}
