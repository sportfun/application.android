package ovh.shr.sportsfun.sportsfunapplication.network;

import android.media.SoundPool;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;

public class SocketIOHelper
{

    //region Declarations

    private static Socket _socket;
    private static Boolean isConnected = false;
    private static String url = "http://api.sportsfun.shr.ovh:8080/";
    private static Emitter onConversationReceive;

    //endregion Declarations

    //region Getters & Setters

    public static Socket getSocket() {
        return _socket;
    }

    public static void setSocket(Socket _socket) {
        _socket = _socket;
    }

    //endregion Getters & Setters

    //region Public methods

    public static void Connect() {

        if (isConnected)
            return;

        try {
            _socket = IO.socket(url);
            _socket.on(Socket.EVENT_CONNECT, onConnect);
            _socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            _socket.on("message", onMessageReceived);
            _socket.on("info", onInfoReceived);

            _socket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openChannel(String channel, Emitter.Listener emitter) {
        _socket.on(channel, emitter);
    }

    public static void closeChannel(String channel, Emitter.Listener emitter) {
        _socket.off(channel, emitter);
    }

    public static void emit(String channel, Object obj) {
        _socket.emit(channel, obj);
    }

    //endregion Public methods


    //region Private methods

    //endregion Private methods

    //region Events

    private static Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("********** onConnect **********");
            isConnected = true;

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("token", SportsFunApplication.getAuthentificationToken());
                _socket.emit("registerMessages", jsonObject);
            } catch (Exception error) {

            }

        }
    };

    private static Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("********** onDisconnect **********");
            isConnected = false;
        }
    };

    private static Emitter.Listener onInfoReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("********** onInfoReceived **********");
            System.out.println(args[0]);


            try {

                JSONObject jsonObject = (JSONObject) args[0];
                System.out.println(jsonObject.toString());


            } catch (Exception error) {

            }


        }
    };

    private static Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    //endregion Events



}
//
//socket = IO.socket("http://localhost");
//        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//
//@Override
//public void call(Object... args) {
//        socket.emit("foo", "hi");
//        socket.disconnect();
//        }
//
//        }).on("event", new Emitter.Listener() {
//
//@Override
//public void call(Object... args) {}
//
//        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
//
//@Override
//public void call(Object... args) {}
//
//        });
//        socket.connect();