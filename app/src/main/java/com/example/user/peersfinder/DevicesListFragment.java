package com.example.user.peersfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import androidx.fragment.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 10/6/2016.
 */

public class DevicesListFragment extends ListFragment implements WifiP2pManager.PeerListListener    {

    private List<WifiP2pDevice> peersList = new ArrayList<WifiP2pDevice>();
    private WifiP2pDevice device;

    View contentView = null;
    ProgressDialog progressDialog = null;

    /**
     * View and ListFragment implementation
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState)    {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WifiPeerListAdapter(getActivity(), R.layout.row_devices, peersList));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)   {
        contentView = inflater.inflate(R.layout.devices_list_fragment, null);
        //contentView = inflater.inflate(R.layout.devices_list_fragment, container, false);
        return contentView;
    }
    /**
     * PeerListListener implementation
     */

    public void onInitiateDiscovery()   {
        if (this.progressDialog != null && this.progressDialog.isShowing())
            this.progressDialog.dismiss();

        this.progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true, true, new DialogInterface.OnCancelListener()    {
            @Override
            public void onCancel(DialogInterface dialogInterface)   {

            }
        });
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        peersList.clear();
        peersList.addAll(peers.getDeviceList());
        ((WifiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public void peersAvailable(WifiP2pDeviceList peers) {
        Log.d("NSBULLSHIT", "P2P peers list retrieval");
        if (this.progressDialog != null && this.progressDialog.isShowing())
            this.progressDialog.dismiss();

        peersList.clear();
        peersList.addAll(peers.getDeviceList());
        ((WifiPeerListAdapter) getListAdapter()).notifyDataSetChanged();

        if (peersList.size() == 0) {
            Log.d("NSBULLSHIT", "No devices found");
            return;
        } else if (peersList.size() != 0)   {
            Log.d("NSBULLSHIT", "KEEP DISCOVERING!!");
        }
    }
    /**
     * PeerListAdapter implementation
     */

    public class WifiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

        private List<WifiP2pDevice> items;

        public WifiPeerListAdapter(Context context, int textViewResourceId,
                                   List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)   {
            View v = convertView;
            WifiP2pDevice wifiP2pDevice = items.get(position);

            if (v == null)  {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, null);
            }

            if (wifiP2pDevice != null) {
                TextView deviceName = (TextView) v.findViewById(R.id.deviceName);
                TextView deviceDetails = (TextView) v.findViewById(R.id.deviceDetails);
                TextView deviceDiscoveryTime = (TextView) v.findViewById(R.id.deviceDiscoveryTime);

                if (deviceName != null) {
                    deviceName.setText(wifiP2pDevice.deviceName);
                }
                if (deviceDetails != null)  {
                    deviceDetails.setText(getDeviceStatus(wifiP2pDevice.status));
                }
                if (deviceDiscoveryTime != null)    {
                    deviceDiscoveryTime.setText("");
                }

                Log.d("NSBULLSHIT", "DEVICE NOT NULL");
            }
            else    {
                Log.d("NSBULLSHIT", "DEVICE IS NULL");
            }
            return v;
        }
    }

    /**
     * Miscellaneous
     */
    private static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }
}
