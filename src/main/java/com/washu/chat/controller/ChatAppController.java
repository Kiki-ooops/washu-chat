package com.washu.chat.controller;

import com.washu.chat.model.constant.Constant;
import com.washu.chat.model.DataStorage;
import com.washu.chat.model.DispatchAdapter;
import com.washu.chat.model.Status;

import static spark.Spark.*;

/**
 * The chat app controller communicates with all the clients on the web socket.
 */
public class ChatAppController {
    private static DispatchAdapter da = DispatchAdapter.getInstance();

    // Enables CORS on requests. This method is an initialization method and should be called once.
    private static void enableCORS(final String origin, final String methods, final String headers) {

        options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
            // Note: this may or may not be necessary in your particular application
            //response.type("application/json");
        });
    }

    /**
     * Chat App entry point.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        DataStorage.initDataSource();
        staticFiles.location("/react");

        webSocket("/tofu", WebSocketController.class);
        init();
//        enableCORS(
//                "http://localhost:3000",
//                "GET, PUT, POST, DELETE",
//                "Authorization, Content-Type, Origin, X-Request-With, X-Session-Id"
//        );

        post("/create", (req, res) -> {
            int statusCode = da.creatChatRoom(req.body());
            return Constant.gson.toJson(new Status(statusCode));
        });

        post("/join", (req, res) -> {
            int statusCode = da.joinChatRoom(req.body());
            return Constant.gson.toJson(new Status(statusCode));
        });

        post("/leave", (req, res) -> {
            int statusCode = da.leaveChatRoom(req.body());
            return Constant.gson.toJson(new Status(statusCode));
        });

        post( "/list", (req, res) -> {
            return da.getList(req.body());
        });
    }

    /**
     * Get the heroku assigned port number.
     *
     * @return The heroku assigned port number
     */
    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; // return default port if heroku-port isn't set.
    }
}
