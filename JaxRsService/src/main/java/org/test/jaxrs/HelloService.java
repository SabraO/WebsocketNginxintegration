package org.test.jaxrs;

/**
 * Created by sabra on 7/2/15.
 */
import javax.websocket.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.glassfish.tyrus.client.ClientManager;

@Path("/")
public class HelloService {

    private static CountDownLatch messageLatch;

    @GET
    @Path("/{message}")
    public void connectToWSServer(@PathParam("message") final String message) throws URISyntaxException, IOException, DeploymentException, InterruptedException {

        String endPoint = "ws://localhost:9765/WebSocketServer/echo";

        messageLatch = new CountDownLatch(1);

        final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

        ClientManager client = ClientManager.createClient();

        client.connectToServer(new Endpoint() {

            @Override
            public void onOpen(Session session, EndpointConfig config) {
                try {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {

                        @Override
                        public void onMessage(String message) {
                            System.out.println("Jax-rs : "+message);
                            messageLatch.countDown();
                        }
                    });
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, cec, new URI(endPoint));
        messageLatch.await(100, TimeUnit.SECONDS);

    }

}


