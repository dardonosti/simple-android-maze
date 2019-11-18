package ar.edu.ips.aus.seminario2.sampleproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.abemart.wroup.client.WroupClient;
import com.abemart.wroup.common.WiFiDirectBroadcastReceiver;
import com.abemart.wroup.common.WiFiP2PError;
import com.abemart.wroup.common.WiFiP2PInstance;
import com.abemart.wroup.common.WroupDevice;
import com.abemart.wroup.common.WroupServiceDevice;
import com.abemart.wroup.common.listeners.ServiceConnectedListener;
import com.abemart.wroup.common.listeners.ServiceDiscoveredListener;
import com.abemart.wroup.common.listeners.ServiceRegisteredListener;
import com.abemart.wroup.service.WroupService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GroupCreationDialog.GroupCreationAcceptButtonListener {


    /* Agregado */
    private final static String[] tablero = {" 3 x 3 "," 4 x 4 "," 5 x 5 ",
            " 6 x 6 "," 7 x 7 "," 8 x 8 "," 9 x 9 "};
    private EditText et;
    private Spinner spTablero;
    /* ******** */


    private static final String TAG = MainActivity.class.getSimpleName();

    private WiFiDirectBroadcastReceiver wiFiDirectBroadcastReceiver;
    private WroupService wroupService;
    private WroupClient wroupClient;

    private GroupCreationDialog groupCreationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Agregado */
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb(238,157,49));
        window.setNavigationBarColor(Color.rgb(238,157,49));
        /* ******** */

        setContentView(R.layout.activity_main);

        /* Agregado */
        final Button btnIniciar = (Button) findViewById(R.id.btnIniciar);
        final Switch switchServidorCliente = (Switch) findViewById(R.id.switchServidorCliente);

        switchServidorCliente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchServidorCliente.isChecked()) {
                    spTablero.setEnabled(false);
                    btnIniciar.setText("INICIAR CLIENTE");
                }
                else {
                    spTablero.setEnabled(true);
                    btnIniciar.setText("INICIAR SERVIDOR");
                }
            }
        });
        /* ******** */

        /* Agregado */
        et = (EditText) findViewById(R.id.nJugador);
        spTablero = (Spinner)findViewById(R.id.spinner);
        spTablero.setEnabled(false);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, tablero);

        spTablero.setAdapter(adapter);
        spTablero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Typeface font = ResourcesCompat.getFont(getApplicationContext(),R.font.earlygameboy);
                ((TextView) adapterView.getChildAt(0)).setTypeface(font);
                /* Otra forma */
                /*((TextView) adapterView.getChildAt(0)).setTypeface(getResources().getFont(R.font.earlygameboy)); */
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorSpinner));
                ((TextView) adapterView.getChildAt(0)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                Object item = adapterView.getItemAtPosition(0);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        /* ******** */

        wiFiDirectBroadcastReceiver = WiFiP2PInstance.getInstance(this).getBroadcastReceiver();

        /* Botones */

        //Button btnCreateGroup = (Button) findViewById(R.id.btnCreateGroup);
        //Button btnJoinGroup = (Button) findViewById(R.id.btnJoinGroup);

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchServidorCliente.isChecked()) {
                    if(et.getText().toString().isEmpty()) {

                        // Toast Customizado...
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout,
                                (ViewGroup) findViewById(R.id.toast_layout_root));

                        TextView text = (TextView) layout.findViewById(R.id.text);
                        text.setText("Jugador desconocido...");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    } else {
                        searchAvailableGroups();
                    }
                    /* */
                    //searchAvailableGroups();
                } else {
                    if(et.getText().toString().isEmpty()) {

                        // Toast Customizado...
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout,
                                (ViewGroup) findViewById(R.id.toast_layout_root));

                        TextView text = (TextView) layout.findViewById(R.id.text);
                        text.setText("Jugador desconocido...");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    } else {
                        groupCreationDialog = new GroupCreationDialog();
                        groupCreationDialog.addGroupCreationAcceptListener(MainActivity.this);
                        groupCreationDialog.show(getSupportFragmentManager(), GroupCreationDialog.class.getSimpleName());
                    }
                    /* */

                    //groupCreationDialog = new GroupCreationDialog();
                    //groupCreationDialog.addGroupCreationAcceptListener(MainActivity.this);
                    //groupCreationDialog.show(getSupportFragmentManager(), GroupCreationDialog.class.getSimpleName());
                }
            }
        });

        /* ******** */
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        registerReceiver(wiFiDirectBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wiFiDirectBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (wroupService != null) {
            wroupService.disconnect();
        }

        if (wroupClient != null) {
            wroupClient.disconnect();
        }
    }

    @Override
    public void onAcceptButtonListener(final String groupName) {
        if (!groupName.isEmpty()) {
            wroupService = WroupService.getInstance(getApplicationContext());
            wroupService.registerService(groupName, new ServiceRegisteredListener() {

                @Override
                public void onSuccessServiceRegistered() {
                    Log.i(TAG, "Group created. Launching GroupChatActivity...");
                    startGameActivity(groupName, true);
                    groupCreationDialog.dismiss();
                }

                @Override
                public void onErrorServiceRegistered(WiFiP2PError wiFiP2PError) {
                    /* agregado */
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout,
                            (ViewGroup) findViewById(R.id.toast_layout_root));

                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText("Error creating group");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    /* ******** */
                    //Toast.makeText(getApplicationContext(), "Error creating group", Toast.LENGTH_SHORT).show();
                }

            });
        } else {
            /* agregado */
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout,
                    (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Please, insert a group name");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            /* ******** */
            //Toast.makeText(getApplicationContext(), "Please, insert a group name", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchAvailableGroups() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        //progressDialog.setMessage(getString(R.string.prgrss_searching_groups));
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        wroupClient = WroupClient.getInstance(getApplicationContext());
        wroupClient.discoverServices(5000L, new ServiceDiscoveredListener() {

            @Override
            public void onNewServiceDeviceDiscovered(WroupServiceDevice serviceDevice) {
                Log.i(TAG, "New group found:");
                Log.i(TAG, "\tName: " + serviceDevice.getTxtRecordMap().get(WroupService.SERVICE_GROUP_NAME));
            }

            @Override
            public void onFinishServiceDeviceDiscovered(List<WroupServiceDevice> serviceDevices) {
                Log.i(TAG, "Found '" + serviceDevices.size() + "' groups");
                progressDialog.dismiss();

                if (serviceDevices.isEmpty()) {
                    // Toast Customizado... //
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout,
                            (ViewGroup) findViewById(R.id.toast_layout_root));

                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText("No se encontraron servidores...");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                    // ******************* //

                    //Toast.makeText(getApplicationContext(), getString(R.string.toast_not_found_groups),Toast.LENGTH_LONG).show();
                } else {
                    showPickGroupDialog(serviceDevices);
                }
            }

            @Override
            public void onError(WiFiP2PError wiFiP2PError) {
                /* agregado */
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_layout,
                        (ViewGroup) findViewById(R.id.toast_layout_root));

                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText("Error searching groups: " + wiFiP2PError);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                /* ******** */
                //Toast.makeText(getApplicationContext(), "Error searching groups: " + wiFiP2PError, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPickGroupDialog(final List<WroupServiceDevice> devices) {
        List<String> deviceNames = new ArrayList<>();
        for (WroupServiceDevice device : devices) {
            deviceNames.add(device.getTxtRecordMap().get(WroupService.SERVICE_GROUP_NAME));
        }

        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialog));
        builder.setTitle("Select a group");
        builder.setItems(deviceNames.toArray(new String[deviceNames.size()]), new DialogInterface.OnClickListener()  {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final WroupServiceDevice serviceSelected = devices.get(which);
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                //progressDialog.setMessage(getString(R.string.prgrss_connecting_to_group));
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog_connect);



                wroupClient.connectToService(serviceSelected, new ServiceConnectedListener() {
                    @Override
                    public void onServiceConnected(WroupDevice serviceDevice) {
                        progressDialog.dismiss();
                        startGameActivity(serviceSelected.getTxtRecordMap().get(WroupService.SERVICE_GROUP_NAME), false);
                    }
                });
            }
        });

        AlertDialog pickGroupDialog = builder.create();
        pickGroupDialog.show();
    }


    private void startGameActivity(String groupName, boolean isGroupOwner) {
        Intent intent = new Intent(getApplicationContext(), MazeBoardActivity.class);

        /* agregado */
        intent.putExtra("nombre", et.getText().toString());


            if (et.getText().toString().equals("wolf3d")) {
                intent.putExtra("tamTablero", "wolf3d");
            } else {
                intent.putExtra("tamTablero", spTablero.getSelectedItem().toString());
            }

        /* ******** */
        intent.putExtra(MazeBoardActivity.EXTRA_SERVER_NAME, groupName);
        intent.putExtra(MazeBoardActivity.EXTRA_IS_SERVER, isGroupOwner);
        GameApp.getInstance().setServerName(groupName);
        GameApp.getInstance().setGameServer(isGroupOwner);
        startActivity(intent);
    }
}
