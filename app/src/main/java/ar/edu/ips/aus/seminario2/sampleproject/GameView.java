package ar.edu.ips.aus.seminario2.sampleproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.provider.Settings;
import android.support.transition.Slide;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.abemart.wroup.client.WroupClient;
import com.abemart.wroup.common.messages.MessageWrapper;
import com.abemart.wroup.service.WroupService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = GameView.class.getSimpleName();
    public static final String STATUS_PAUSED = "paused";
    public static final String STATUS_UPDATING = "updating";
    private boolean updating = false;
    private GameAnimationThread thread;
    private Player player;
    private Map<String, Player> players = new HashMap<>();
    private PlayerSprite playerSprites;
    private int moves = 0;
    private static final int SERVER_UPDATE_RATIO = 3;
    private static final int CLIENT_UPDATE_RATIO = 2;
    private MazeBoardActivity activity = (MazeBoardActivity) this.getContext();

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GameView(Context context) {
        super(context);
        init();
    }

    private void init() {
        getHolder().addCallback(this);

        String id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        double tam = activity.getTamTab() - 0.5;
        switch (activity.getPosInicial()) {
            case 0:
                player = new Player(id, 0.5, 0.5);
                break;
            case 1:
                player = new Player(id, tam, 0.5);
                break;
            case 2:
                player = new Player(id, 0.5, tam);
                break;
            case 3:
                player = new Player(id, tam, tam);
                break;
        }

        players.put(id, player);

        playerSprites = new PlayerSprite(getResources());
        player.setOrder(playerSprites.getRandomSpriteNumber());

        thread = new GameAnimationThread(getHolder(), this);
        setFocusable(true);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                Log.d(TAG, "Error " + e.getMessage());
            }
            retry = false;
        }
    }

    // update game world
    public void update(long delta) {
        if (this.updating) {
            MazeBoard board = GameApp.getInstance().getMazeBoard();
            // update only actual player
            player.move(board, delta);
            this.moves++;

            // if we are server send all players data
            if (GameApp.getInstance().isGameServer()) {
                if (this.moves % SERVER_UPDATE_RATIO == 0) {
                    /* agregado */
                    if (board.isWinFlag(getPlayer().getX(), getPlayer().getY())) {
                        thread.setRunning(false);
                        activity.pauseChronometer(activity.crono);

                        WroupService server = GameApp.getInstance().getServer();
                        MessageWrapper message = new MessageWrapper();
                        Gson json = new Gson();
                        Message<String> data = new Message<>(Message.MessageType.WIN,
                                activity.getIntent().getExtras().getString("nombre"));
                        String msg = json.toJson(data);
                        message.setMessage(msg);
                        message.setMessageType(MessageWrapper.MessageType.NORMAL);
                        server.sendMessageToAllClients(message);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(activity, WinActivity.class);
                                i.putExtra("nombreGanador", activity.getIntent().getExtras().getString("nombre"));
                                activity.startActivity(i);
                                activity.finish();
                            }
                        });
                    }

                    /* ******* */

                    WroupService server = GameApp.getInstance().getServer();
                    MessageWrapper message = new MessageWrapper();
                    Gson json = new Gson();
                    Message<Player[]> data = new Message<Player[]>(Message.MessageType.PLAYER_DATA,
                            players.values().toArray(new Player[]{}));
                    String msg = json.toJson(data);
                    message.setMessage(msg);
                    message.setMessageType(MessageWrapper.MessageType.NORMAL);
                    server.sendMessageToAllClients(message);
                }
            } else {
                // if we are client send player data
                if (this.moves % CLIENT_UPDATE_RATIO == 0) {
                    /* agregado */
                    if (board.isWinFlag(getPlayer().getX(), getPlayer().getY())) {
                        thread.setRunning(false);
                        activity.pauseChronometer(activity.crono);
                        WroupClient client = GameApp.getInstance().getClient();
                        MessageWrapper message = new MessageWrapper();
                        Gson json = new Gson();
                        Message<String> data = new Message<>(Message.MessageType.WIN,
                                activity.getIntent().getExtras().getString("nombre"));
                        String msg = json.toJson(data);
                        message.setMessage(msg);
                        message.setMessageType(MessageWrapper.MessageType.NORMAL);
                        client.sendMessageToServer(message);
                    }
                    /* ******* */

                    WroupClient client = GameApp.getInstance().getClient();
                    MessageWrapper message = new MessageWrapper();
                    Gson json = new Gson();
                    Message<Player[]> data = new Message<Player[]>(Message.MessageType.PLAYER_DATA,
                            new Player[]{player});
                    String msg = json.toJson(data);
                    message.setMessage(msg);
                    message.setMessageType(MessageWrapper.MessageType.NORMAL);
                    client.sendMessageToServer(message);
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            MazeBoard board = GameApp.getInstance().getMazeBoard();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            if (board != null) {
                for (Player p : this.players.values()) {
                    Rect srcRect = playerSprites.getSourceRectangle(this, board, p, p.getOrder());
                    Rect dstRect = playerSprites.getDestinationRectangle(this, board, p);
                    canvas.drawBitmap(playerSprites.getSprites(), srcRect, dstRect, null);
                }
            }
        }
    }

    public void updatePlayerData(String message) {
        Gson gson = new Gson();
        Message<Player[]> playerData = gson.fromJson(message,
                new TypeToken<Message<Player[]>>() {
                }.getType());
        for (Player pd : playerData.getPayload()) {
            if (!player.getID().equals(pd.getID())) {
                Player p = players.get(pd.getID());
                if (p == null) {
                    p = new Player(pd.getID(), pd.getX(), pd.getY());
                    p.setOrder(pd.getOrder());
                    players.put(pd.getID(), p);
                }
                p.setX(pd.getX());
                p.setY(pd.getY());
                p.setXVel(pd.getXVel());
                p.setYVel(pd.getYVel());
            }
        }
    }

    public void updateStatus(String message) {
        Gson gson = new Gson();
        Message<String> gameStatus = gson.fromJson(message,
                new TypeToken<Message<String>>() {
                }.getType());
        if (gameStatus.getType() == Message.MessageType.GAME_STATUS) {
            String data = gameStatus.getPayload();
            switch (data) {
                case STATUS_PAUSED:
                    this.updating = false;
                    Log.d(TAG, "Game paused.");
                    this.post(new Runnable() {
                        @Override
                        public void run() {
                            /* agregado */
                            activity.pauseChronometer(activity.crono);
                            LayoutInflater inflater = activity.getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_layout,
                                    (ViewGroup) findViewById(R.id.toast_layout_root));

                            TextView text = (TextView) layout.findViewById(R.id.text);
                            text.setText("GAME PAUSED by SERVER");
                            Toast toast = new Toast(getContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.setView(layout);
                            toast.show();
                            /* ******** */
                            //Toast.makeText(getContext(), "GAME PAUSED by SERVER", Toast.LENGTH_LONG).show();
                        }
                    });
                    activity.soundPool.play(activity.peepSound, MazeBoardActivity.FX_VOLUME, MazeBoardActivity.FX_VOLUME, 0, 0, 1);
                    break;
                case STATUS_UPDATING:
                    this.updating = true;
                    Log.d(TAG, "Game updating.");
                    this.post(new Runnable() {
                        @Override
                        public void run() {
                            /* agregado */
                            activity.startChronometer(activity.crono);
                            LayoutInflater inflater = activity.getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_layout,
                                    (ViewGroup) findViewById(R.id.toast_layout_root));

                            TextView text = (TextView) layout.findViewById(R.id.text);
                            text.setText("GAME RESUMED by SERVER");
                            Toast toast = new Toast(getContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.setView(layout);
                            toast.show();
                            /* ******** */
                            //Toast.makeText(getContext(), "GAME RESUMED by SERVER", Toast.LENGTH_LONG).show();
                        }
                    });
                    activity.soundPool.play(activity.beepSound, MazeBoardActivity.FX_VOLUME, MazeBoardActivity.FX_VOLUME, 0, 0, 1);
                    break;
                default:
                    break;
            }
        }
    }

    public void toggleStatus() {
        if (GameApp.getInstance().isGameServer()) {
            String status = null;
            if (this.updating) {
                this.updating = false;
                status = STATUS_PAUSED;
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        /* agregado */
                        LayoutInflater inflater = activity.getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout,
                                (ViewGroup) findViewById(R.id.toast_layout_root));

                        TextView text = (TextView) layout.findViewById(R.id.text);
                        text.setText("GAME PAUSED");
                        Toast toast = new Toast(getContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        /* ******** */
                        //Toast.makeText(getContext(), "GAME PAUSED", Toast.LENGTH_LONG).show();
                    }
                });
                activity.soundPool.play(activity.peepSound, MazeBoardActivity.FX_VOLUME, MazeBoardActivity.FX_VOLUME, 0, 0, 1);
            } else {
                this.updating = true;
                status = STATUS_UPDATING;
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        /* agregado */
                        LayoutInflater inflater = activity.getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout,
                                (ViewGroup) findViewById(R.id.toast_layout_root));

                        TextView text = (TextView) layout.findViewById(R.id.text);
                        text.setText("GAME RESUMED");
                        Toast toast = new Toast(getContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        /* ******** */
                        //Toast.makeText(getContext(), "GAME RESUMED", Toast.LENGTH_LONG).show();
                    }
                });
                activity.soundPool.play(activity.beepSound, MazeBoardActivity.FX_VOLUME, MazeBoardActivity.FX_VOLUME, 0, 0, 1);
            }
            WroupService server = GameApp.getInstance().getServer();
            MessageWrapper message = new MessageWrapper();
            Gson json = new Gson();
            Message<String> data = new Message<String>(Message.MessageType.GAME_STATUS,
                    status);
            String msg = json.toJson(data);
            message.setMessage(msg);
            message.setMessageType(MessageWrapper.MessageType.NORMAL);
            server.sendMessageToAllClients(message);
        }
    }

    public void updateInitPos() {

        String id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        double tam = activity.getTamTab() - 0.5;
        Log.d(TAG,"Posición inicial: " + tam);
        switch (activity.getPosInicial()) {
            case 0:
                player.setX(0.5);
                player.setY(0.5);
                break;
            case 1:
                player.setX(tam);
                player.setY(0.5);
                break;
            case 2:
                player.setX(0.5);
                player.setY(tam);
                break;
            case 3:
                player.setX(tam);
                player.setY(tam);
                break;
        }

        players.put(id, player);

    }
}


