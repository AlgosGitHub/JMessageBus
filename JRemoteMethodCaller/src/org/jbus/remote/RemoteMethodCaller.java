package org.jbus.remote;

import mp.net.sockets.Serializer;
import org.jbus.JMBClient;
import org.jbus.remote.commands.RemoteMethodCall;
import org.jbus.remote.commands.RemoteMethodResponse;
import org.jbus.remote.exceptions.RemoteMethodCallTimedOut;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.reflect.Proxy.newProxyInstance;

public class RemoteMethodCaller {

    private final static AtomicLong nextValidRequestId = new AtomicLong();

    private final static CallResponseHandler callResponseHandler = new CallResponseHandler();

    public static <E> E wrap(Class<?> portClass, JMBClient jmbClient, String address) throws Exception {

        final Set<Method> definedMethods = Set.of(portClass.getMethods());
        final Object substitute = new Object();

        String sendAddr = address+".call", respAddr = address+".response";

        jmbClient.subscribe(respAddr, RemoteMethodCaller::receiveObject);

        return (E) newProxyInstance(
                portClass.getClassLoader(),
                new Class[] { portClass },
                (proxy, method, args) -> {

                    if(definedMethods.contains(method))
                        return callRemoteMethod(sendAddr, jmbClient, method.getName(), args);
                    else {
                        return method.invoke(substitute, args);
                    }

                }
        );
        
    }

    private static void receiveObject(Serializable serializable) {

        switch (serializable) {
            case RemoteMethodResponse rmr -> handleRemoteCallResponse(rmr);
            default -> {/* do nothing */}
        }
    }

    private static void handleRemoteCallResponse(RemoteMethodResponse rmr) {
        try {
            callResponseHandler.responseReceived(rmr.requestId(), Serializer.deserializeObject(rmr.serializedReturn()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize response to remote method call. Class type: " + rmr.returnClassName(), e);
        }
    }

    private static Object callRemoteMethod(String address, JMBClient jmbClient, String methodName, Object[] methodArgs) throws IOException, RemoteMethodCallTimedOut, InterruptedException, ClassNotFoundException {

        // prepare our parameters for transport
        byte[][] serializedArgs = Serializer.serialize(methodArgs);

        // get a requestId for the remote method call, so we can collate the response
        long requestId = nextValidRequestId.getAndIncrement();

        // send our remote method call
        jmbClient.pushMessage(address, new RemoteMethodCall(requestId, methodName, serializedArgs));

        // await response
        return callResponseHandler.waitForResponse(requestId);

    }

}
