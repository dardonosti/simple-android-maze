package ar.edu.ips.aus.seminario2.sampleproject;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.abemart.wroup.client.WroupClient;
import com.abemart.wroup.common.WroupDevice;
import com.abemart.wroup.common.listeners.ClientConnectedListener;
import com.abemart.wroup.common.listeners.ClientDisconnectedListener;
import com.abemart.wroup.common.listeners.DataReceivedListener;
import com.abemart.wroup.common.messages.MessageWrapper;
import com.abemart.wroup.service.WroupService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static ar.edu.ips.aus.seminario2.sampleproject.Message.MessageType.NAME_DATA;
import static ar.edu.ips.aus.seminario2.sampleproject.Message.MessageType.WIN;
import static java.util.Collections.shuffle;

public class MazeBoardActivity extends AppCompatActivity
        implements DataReceivedListener, ClientConnectedListener,
        ClientDisconnectedListener, GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    public static final String EXTRA_SERVER_NAME = "SERVER_NAME";
    public static final String EXTRA_IS_SERVER = "IS_SERVER";
    private static final String TAG = MazeBoardActivity.class.getSimpleName();
    private static final int MAX_DEVICES = 3;
    private static final int SWIPE_TRESHOLD = 100;
    private static final int SWIPE_VELOCITY_TRESHOLD = 100;
    private GestureDetectorCompat gestureDetector;

    public static final float SOUND_VOLUME = 0.5f;
    public static final float FX_VOLUME = 1.0f;
    private static final int FX_AUDIO_STREAMS = 3;
    public MediaPlayer mediaPlayer = null;
    public SoundPool soundPool = null;
    public int beepSound, peepSound;
    private ProgressBar progressBar;
    private boolean firstAcc = true;
    private int players = 0;
    private int posInicial;
    private List<Integer> list = Arrays.asList(0, 1, 2, 3);
    private int tamTab = 0;


    public int getTamTab() {
        return tamTab;
    }

    ImageView[] imageViews = null;

    private GameView mazeView;
    private final HashMap<String, WroupDevice> devices = new HashMap<String, WroupDevice>();

    private final ArrayList<ObjectAnimator> animaciones = new ArrayList<ObjectAnimator>();

    /* ***** */
    Gson gson = new Gson();
    String json;
    /* ****** */

    /* agregado */
    Chronometer crono;
    boolean isON = false;
    private long pauseOffset;
    long Time = 0;
    /* ******** */

    /* agregado */
    private TextView playersname[] = new TextView[4];

    /* ******** */
    public int getPosInicial() {
        return posInicial;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shuffle(list);
        if(GameApp.getInstance().isGameServer()) {
            tamTab = Integer.parseInt("" + getIntent().getExtras().getString("tamTablero").charAt(1));
        }
        super.onCreate(savedInstanceState);
        posInicial = list.get(0);
        setContentView(R.layout.maze);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        /* agregado */
        crono = (Chronometer) findViewById(R.id.tiempo);
        /* ******** */

        /* agregado */
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb(56, 56, 56));
        window.setNavigationBarColor(Color.rgb(238, 157, 49));
        /* ******** */
        /* agregado */

        playersname[0] = findViewById(R.id.jugador1);
        playersname[1] = findViewById(R.id.jugador2);
        playersname[2] = findViewById(R.id.jugador3);
        playersname[3] = findViewById(R.id.jugador4);
        manageBlinkEffect(playersname[0], animaciones);
        manageBlinkEffect(playersname[1], animaciones);
        manageBlinkEffect(playersname[2], animaciones);
        manageBlinkEffect(playersname[3], animaciones);
        /* ******** */

        mazeView = (GameView) findViewById(R.id.gameView);
        mazeView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mazeView.setZOrderMediaOverlay(true);
        mazeView.setZOrderOnTop(true);

        GameApp.getInstance().setServerName(getIntent().getStringExtra(this.EXTRA_SERVER_NAME));
        GameApp.getInstance().setGameServer(getIntent().getBooleanExtra(this.EXTRA_IS_SERVER, false));

        if (GameApp.getInstance().isGameServer()) {

            WroupService server = WroupService.getInstance(this);
            server.setDataReceivedListener(this);
            server.setClientDisconnectedListener(this);
            server.setClientConnectedListener(this);
            GameApp.getInstance().setServer(server);
            playersname[players].setText(players + 1 + "P\n" + getIntent().getStringExtra("nombre"));
            animaciones.get(0).cancel();
            progressBar.setVisibility(View.GONE);
        } else {

            WroupClient client = WroupClient.getInstance(this);
            client.setDataReceivedListener(this);
            client.setClientDisconnectedListener(this);
            client.setClientConnectedListener(this);
            GameApp.getInstance().setClient(client);
            players++;
            MessageWrapper message = new MessageWrapper();
            Message<String> jug = new Message<>(NAME_DATA,
                    getIntent().getStringExtra("nombre"));
            json = gson.toJson(jug);
            message.setMessage(json);
            message.setMessageType(MessageWrapper.MessageType.NORMAL);
            client.sendMessageToServer(message);

        }

        if (GameApp.getInstance().isGameServer()) {
            /* agregado */
            MazeBoard board = MazeBoard.from((getIntent().getExtras().getString("tamTablero")));
            /* ******** */

            //MazeBoard board = MazeBoard.from("asdasd");
            GameApp.getInstance().setMazeBoard(board);
            setupMazeBoard(board);
        }

        gestureDetector = new GestureDetectorCompat(this, this);

        mediaPlayer = MediaPlayer.create(this, R.raw.mario_castle);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(SOUND_VOLUME, SOUND_VOLUME);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attrs)
                    .setMaxStreams(FX_AUDIO_STREAMS)
                    .build();
        } else {
            soundPool = new SoundPool(FX_AUDIO_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        beepSound = soundPool.load(this, R.raw.beeep, 1);
        peepSound = soundPool.load(this, R.raw.peeeeeep, 1);
    }

    /* agregado */
    @SuppressLint("WrongConstant")
    protected void manageBlinkEffect(TextView j, ArrayList<ObjectAnimator> a) {
        ObjectAnimator anim = ObjectAnimator.ofInt(j, "textColor", Color.argb(255, 241, 244, 177), Color.argb(255, 56, 56, 56));
        anim.setDuration(500);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.start();
        a.add(anim);
    }
    /* ******** */

    /* agregado */
    protected void startChronometer(View v) {
        if (!isON) {
            crono.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            crono.start();
            isON = true;
        }
    }

    protected void pauseChronometer(View v) {
        if (isON) {
            crono.stop();
            pauseOffset = SystemClock.elapsedRealtime() - crono.getBase();
            isON = false;
        }
    }
    /* ******** */

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.release();
        soundPool.release();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupMazeBoard(MazeBoard board) {

        int height = board.getVerticalTileCount();
        int width = board.getHorizontalTileCount();

        imageViews = new ImageView[width * height];

        int resId = 0;

        TableLayout table = findViewById(R.id.mazeBoard);
        for (int i = 0; i < height; i++) {
            TableRow row = new TableRow(this);
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams();
            rowParams.width = TableRow.LayoutParams.MATCH_PARENT;
            rowParams.height = TableRow.LayoutParams.MATCH_PARENT;
            rowParams.weight = 1;
            rowParams.gravity = Gravity.CENTER;
            row.setLayoutParams(rowParams);
            table.addView(row);

            for (int j = 0; j < width; j++) {
                BoardPiece piece = board.getPiece(j, i);

                resId = lookupResource(piece);

                ImageView imageView = new ImageView(this);
                imageView.setBackgroundResource(resId);
                TableRow.LayoutParams imageViewParams = new TableRow.LayoutParams();
                imageViewParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                imageViewParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                imageViewParams.weight = 1;
                imageView.setLayoutParams(imageViewParams);

                row.addView(imageView);

                imageViews[(j % board.getHorizontalTileCount()) + board.getVerticalTileCount() * i] = imageView;

                if (piece.isWinFlag() && !piece.isWolf3d()) {
                    imageView.setForeground(getDrawable(R.drawable.winflag_f));
                }
                if (piece.isWinFlag() && piece.isWolf3d()) {
                    imageView.setForeground(getDrawable(R.drawable.winflag_e));
                }


            }
        }
        table.invalidate();
    }

    private int lookupResource(BoardPiece piece) {
        int iconIndex = 0b1000 * (piece.isOpen(MazeBoard.Direction.WEST) ? 1 : 0) +
                0b0100 * (piece.isOpen(MazeBoard.Direction.NORTH) ? 1 : 0) +
                0b0010 * (piece.isOpen(MazeBoard.Direction.EAST) ? 1 : 0) +
                0b0001 * (piece.isOpen(MazeBoard.Direction.SOUTH) ? 1 : 0);

        int[] iconLookupTable = {0,
                R.drawable.m1b,
                R.drawable.m1r,
                R.drawable.m2rb,
                R.drawable.m1t,
                R.drawable.m2v,
                R.drawable.m2tr,
                R.drawable.m3l,
                R.drawable.m1l,
                R.drawable.m2bl,
                R.drawable.m2h,
                R.drawable.m3t,
                R.drawable.m2lt,
                R.drawable.m3r,
                R.drawable.m3b,
                R.drawable.m4};

        return iconLookupTable[iconIndex];
    }

    @Override
    public void onClientConnected(final WroupDevice wroupDevice) {
        if (GameApp.getInstance().isGameServer()) {
            addToDeviceList(wroupDevice);
            players++;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                /* agregado */

                WroupService server = GameApp.getInstance().getServer();
                MessageWrapper message = new MessageWrapper();
                int[] aux = new int[]{list.get(players), Integer.parseInt("" + getIntent().getExtras().getString("tamTablero").charAt(1))};
                Message<int[]> intPos = new Message<>(Message.MessageType.PLAYER_INIT,
                        aux);
                json = gson.toJson(intPos);
                message.setMessage(json);
                message.setMessageType(MessageWrapper.MessageType.NORMAL);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "run: ", e);
                }

                server.sendMessageToAllClients(message);

                MazeBoard board = GameApp.getInstance().getMazeBoard();
                Message<MazeBoard> data = new Message<>(Message.MessageType.GAME_DATA,
                        board);
                json = gson.toJson(data);
                message.setMessage(json);
                message.setMessageType(MessageWrapper.MessageType.NORMAL);


                server.sendMessageToAllClients(message);


                /* ******** */

                /* agregado */
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_layout,
                        (ViewGroup) findViewById(R.id.toast_layout_root));

                TextView text = layout.findViewById(R.id.text);
                text.setText(getString(R.string.device_connected, wroupDevice.getDeviceName()));
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                /* ******** */
                //Toast.makeText(getApplicationContext(), getString(R.string.device_connected, wroupDevice.getDeviceName()), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClientDisconnected(final WroupDevice wroupDevice) {
        if (GameApp.getInstance().isGameServer()) {
            removeFromDeviceList(wroupDevice);
            players--;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /* agregado */
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_layout,
                        (ViewGroup) findViewById(R.id.toast_layout_root));

                TextView text = layout.findViewById(R.id.text);
                text.setText(getString(R.string.device_disconnected, wroupDevice.getDeviceName()));
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                /* ******** */
                //Toast.makeText(getApplicationContext(), getString(R.string.device_disconnected, wroupDevice.getDeviceName()), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean addToDeviceList(WroupDevice wroupDevice) {
        if (devices.size() < MAX_DEVICES) {
            devices.put(wroupDevice.getDeviceMac(), wroupDevice);
            return true;
        }
        return false;
    }

    private boolean removeFromDeviceList(WroupDevice wroupDevice) {
        return (devices.remove(wroupDevice.getDeviceMac()) != null);
    }

    @Override
    public void onDataReceived(final MessageWrapper messageWrapper) {
        if (!GameApp.getInstance().isGameServer()) {
            // client may receive different kind of messages from server
            JsonObject object = JsonParser.parseString(messageWrapper.getMessage()).getAsJsonObject();
            JsonElement typeElement = object.get("type");
            switch (Message.MessageType.valueOf(typeElement.getAsString())) {
                case PLAYER_DATA:
                    mazeView.updatePlayerData(messageWrapper.getMessage());
                    break;
                case GAME_DATA:
                    if (firstAcc) {
                        progressBar = findViewById(R.id.progressBar);

                        final Type tipoMessageMazeboard = new TypeToken<Message<MazeBoard>>() {
                        }.getType();
                        final Gson gson = new Gson();
                        final Message<MazeBoard> messageMazeboard = gson.fromJson(messageWrapper.getMessage(), tipoMessageMazeboard);
                        GameApp.getInstance().setMazeBoard(messageMazeboard.getPayload());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.VISIBLE);
                                setupMazeBoard(messageMazeboard.getPayload());
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                        firstAcc = false;
                    }

                    break;
                case NAME_DATA:

                    final Type tipoTextView = new TypeToken<Message<String[]>>() {
                    }.getType();
                    final Gson gson = new Gson();
                    final Message<String[]> messageJug = gson.fromJson(messageWrapper.getMessage(),
                            tipoTextView);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            for (int cant = 0; cant < messageJug.getPayload().length; cant++) {
                                playersname[cant].setText(messageJug.getPayload()[cant]);
                                animaciones.get(cant).cancel();
                            }
                        }
                    });

                    break;
                case GAME_STATUS:
                    mazeView.updateStatus(messageWrapper.getMessage());
                    break;
                case WIN:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Gson gson = new Gson();
                            final Message<String> mWin = gson.fromJson(messageWrapper.getMessage(),
                                    new TypeToken<Message<String>>() {
                                    }.getType());


                            Intent i = new Intent(getApplicationContext(), WinActivity.class);
                            i.putExtra("nombreGanador", mWin.getPayload());
                            startActivity(i);
                            finish();
                        }
                    });
                    break;

                case PLAYER_INIT:
                    final Gson gson2 = new Gson();
                    final Message<int[]> posIni = gson2.fromJson(messageWrapper.getMessage(),
                            new TypeToken<Message<int[]>>() {
                            }.getType());

                    posInicial = posIni.getPayload()[0];
                    tamTab = posIni.getPayload()[1];
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mazeView.updateInitPos();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mazeView.refreshDrawableState();
                        }
                    });

                    break;

            }

        } else {
            JsonObject object = JsonParser.parseString(messageWrapper.getMessage()).getAsJsonObject();
            JsonElement typeElement = object.get("type");

            if (Message.MessageType.valueOf(typeElement.getAsString()) == WIN) {

                final Message<String> mWin = gson.fromJson(messageWrapper.getMessage(),
                        new TypeToken<Message<String>>() {
                        }.getType());

                WroupService server = GameApp.getInstance().getServer();
                MessageWrapper message = new MessageWrapper();
                Gson json = new Gson();
                Message<String> data = new Message<>(Message.MessageType.WIN,
                        mWin.getPayload());
                String msg = json.toJson(data);
                message.setMessage(msg);
                message.setMessageType(MessageWrapper.MessageType.NORMAL);
                server.sendMessageToAllClients(message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout,
                                (ViewGroup) findViewById(R.id.toast_layout_root));
                        Gson gson = new Gson();
                        TextView text = (TextView) layout.findViewById(R.id.text);
                        text.setText(mWin.getPayload());
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.setView(layout);
                        toast.show();
                        */

                        Intent i = new Intent(getApplicationContext(), WinActivity.class);
                        i.putExtra("nombreGanador", mWin.getPayload());
                        startActivity(i);
                        finish();
                    }
                });
            } else if (Message.MessageType.valueOf(typeElement.getAsString()) == NAME_DATA) {
                final Gson gson2 = new Gson();
                final Message<String> mJug = gson2.fromJson(messageWrapper.getMessage(),
                        new TypeToken<Message<String>>() {
                        }.getType());


                runOnUiThread(new Runnable() {
                    public void run() {
                        playersname[players].setText(players + 1 + "P\n" + mJug.getPayload());
                        animaciones.get(players).cancel();
                        WroupService server = GameApp.getInstance().getServer();
                        MessageWrapper message2 = new MessageWrapper();
                        String[] str = new String[players + 1];
                        for (int i = 0; i <= players; i++)
                            str[i] = playersname[i].getText().toString();
                        Message<String[]> jug = new Message<>(NAME_DATA,
                                str);
                        Gson gson3 = new Gson();
                        message2.setMessage(gson3.toJson(jug));
                        message2.setMessageType(MessageWrapper.MessageType.NORMAL);
                        server.sendMessageToAllClients(message2);
                    }
                });

            } else if (devices.containsKey(messageWrapper.getWroupDevice().getDeviceMac())) {
                mazeView.updatePlayerData(messageWrapper.getMessage());
            }
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        mazeView.toggleStatus();

        if (GameApp.getInstance().isGameServer()) {
            if (!isON) {
                startChronometer(crono);
            } else {
                pauseChronometer(crono);
            }
        }
        return true;
    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velX, float velY) {
        boolean eventConsumed = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_TRESHOLD && Math.abs(velX) > SWIPE_VELOCITY_TRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
                eventConsumed = true;
            }
        } else {
            if (Math.abs(diffY) > SWIPE_TRESHOLD && Math.abs(velY) > SWIPE_VELOCITY_TRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
                eventConsumed = true;
            }
        }
        return eventConsumed;
    }

    private void onSwipeTop() {
        mazeView.getPlayer().setNewDirection(MazeBoard.Direction.NORTH);
    }

    private void onSwipeBottom() {
        mazeView.getPlayer().setNewDirection(MazeBoard.Direction.SOUTH);
    }

    private void onSwipeLeft() {
        mazeView.getPlayer().setNewDirection(MazeBoard.Direction.WEST);
    }

    private void onSwipeRight() {
        mazeView.getPlayer().setNewDirection(MazeBoard.Direction.EAST);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
            return true;
        return super.onTouchEvent(event);
    }
}