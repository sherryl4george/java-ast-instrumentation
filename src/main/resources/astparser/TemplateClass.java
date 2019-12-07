package astparser;

import com.neovisionaries.ws.client.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class TemplateClass {
    /**
     * The echo server on websocket.org.
     */
    private static final String SERVER = "ws://localhost:8080/greeter";

    /**
     * The timeout value in milliseconds for socket connection.
     */
    private static final int TIMEOUT = 5000;


    /**
     * The entry point of this command line application.
     */
    private static WebSocket webSocket;

    static {
        try {
            webSocket = connect();
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }
    /**
     * Connect to the server.
     */
    private static WebSocket connect() throws IOException, WebSocketException {
        return new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(new WebSocketAdapter() {
                    // A text message arrived from the server.
                    public void onTextMessage(WebSocket websocket, String message) {
                        System.out.println(message);
                    }
                })
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
    }

    public static void instrum(int lineNumber, String typeOfStatement, AP... args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("line", lineNumber);
        jsonObject.put("statementType", typeOfStatement);
        JSONArray jsonArray = new JSONArray();
        for(AP arg: args)
            jsonArray.put(arg.toJSON());
        jsonObject.put("data", jsonArray);
        webSocket.sendText(jsonObject.toString());
    }

    public static void finalizeInstrum(){
        webSocket.disconnect();
    }
}