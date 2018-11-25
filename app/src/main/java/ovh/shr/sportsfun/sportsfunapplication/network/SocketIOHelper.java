package ovh.shr.sportsfun.sportsfunapplication.network;

import android.content.Context;
import android.media.SoundPool;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.activity.MessageActivity;
import ovh.shr.sportsfun.sportsfunapplication.utilities.NotificationHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;

public class SocketIOHelper
{

    //region Declarations

    private static Socket _socket;
    private static Boolean isConnected = false;
    private static String url = "http://api.sportsfun.shr.ovh:8080/";
    private static Emitter onConversationReceive;
    private static Context context;

    public static MessageActivity messageActivity;

    //endregion Declarations

    //region Getters & Setters

    public static Socket getSocket() {
        return _socket;
    }

    public static void setSocket(Socket _socket) {
        _socket = _socket;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        SocketIOHelper.context = context;
    }

    public static Boolean isConnected() {
        return isConnected;
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
            _socket.on("registerMessages", OnRegisterMessages);

            _socket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Disconnect()
    {
        if (!isConnected)
            return;

        _socket.disconnect();
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

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token", SportsFunApplication.getAuthentificationToken());
                _socket.emit("registerMessages", jsonObject);
            } catch (Exception error) {

            }

        }
    };

    private static Emitter.Listener OnRegisterMessages = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("********** OnRegisterMessages **********");
            System.out.println(args[0]);
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

            System.out.println("********** onMessageReceived **********");

            try {

                JSONObject jsonObject = (JSONObject) args[0];
                System.out.println(jsonObject.toString());
                JsonParser parser = new JsonParser();
                JsonObject json = (JsonObject) parser.parse(jsonObject.toString());
                JsonObject author = json.get("author").getAsJsonObject();
                if (messageActivity != null) {
                    if (messageActivity.getPartnerID().equals(author.get("_id").getAsString())) {
                        messageActivity.onMessageReceived.onTaskCompleted(json);
                        return;
                    }
                }




                JsonObject notification = new JsonObject();
                notification.addProperty("partnerName", author.get("firstName").getAsString() + " " + author.get("lastName").getAsString());
                notification.addProperty("partnerID", author.get("_id").getAsString());
                notification.addProperty("message", json.get("content").getAsString());
                NotificationHelper.newNotification(getContext(), notification);

            } catch (Exception error) {

            }
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