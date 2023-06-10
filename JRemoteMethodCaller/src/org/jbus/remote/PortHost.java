package org.jbus.remote;

import mp.net.sockets.Serializer;
import org.jbus.JMBClient;
import org.jbus.remote.commands.RemoteMethodCall;
import org.jbus.remote.commands.RemoteMethodResponse;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PortHost<E> {

    final private JMBClient jmbClient;

    final private String channel;

    final private E portAdapterInstance;

    final private Map<String, Method> methodsByName;

    PortHost(JMBClient jmbClient, String channel, Class<E> portInterfaceClass, E portAdapterInstance) throws Exception {

        this.portAdapterInstance = portAdapterInstance;
        this.methodsByName = createMethodMap(portInterfaceClass);
        this.jmbClient = jmbClient;
        this.channel = channel+".response";

        String sendAddr = channel+".call";

        jmbClient.subscribe(sendAddr, this::receiveObject);

    }

    private void receiveObject(Serializable serializable) {

        switch (serializable) {
            case RemoteMethodCall rmc -> executeRemoteMethodCall(rmc);
            default -> {/* do nothing */}
        }

    }

    public void executeRemoteMethodCall(RemoteMethodCall remoteMethodCall) {

        Method method = methodsByName.get(remoteMethodCall.methodName());

        try {

            //deserialize method parameters
            Object[] methodParameters = Serializer.deserialize(remoteMethodCall.serializedArgs());

            //execute the method
            Object methodReturn = method.invoke(portAdapterInstance, methodParameters);

            //serialize whatever the method returned
            byte[] serializedReturn = Serializer.serializeObject((Serializable) methodReturn);

            //class type of what the method returned
            String returnTypeClassName = method.getReturnType().getName();

            //wrap up the RemoteMethodCall's return
            Serializable toSend = new RemoteMethodResponse(remoteMethodCall.requestId(), returnTypeClassName, serializedReturn);

            //send our response
            jmbClient.pushMessage(channel, toSend);

        } catch (Exception ex) {
            //log.log(Level.WARNING, "Failed to Execute Remote Method Call: " + remoteMethodCall.methodName() + " on instance: " + remoteMethodCall.instanceName() + ". Exception Message: " + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }

    }

    public static <E> PortHost with(JMBClient jmbClient, String channel, Class<E> portInterfaceClass, E portAdapterInstance) throws Exception {
        return new PortHost(jmbClient, channel, portInterfaceClass, portAdapterInstance);
    }

    private static Map<String, Method> createMethodMap(Class<?> interfaceClass) {

        Map<String, Method> methodsByName = new HashMap<>();

        Method[] methods = interfaceClass.getMethods();

        for(Method method : methods)
            methodsByName.put(method.getName(), method);

        return methodsByName;

    }

}
