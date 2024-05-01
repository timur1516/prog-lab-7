package common.net.requests;

import common.net.dataTransfer.UserInfo;

import java.io.Serializable;

/**
 * Method to store and transfer requests from client to server
 */
public class ClientRequest implements Serializable{
    private final ClientRequestType requestType;
    private final Serializable data;
    private final UserInfo user;
    private static UserInfo globalUser;

    static {
        globalUser = null;
    }

    public ClientRequest(ClientRequestType requestType, Serializable data){
        this.requestType = requestType;
        this.data = data;
        this.user = globalUser;
    }

    public static void setUser(UserInfo user){
        globalUser = user;
    }
    public static UserInfo getUser(){
        return globalUser;
    }

    public ClientRequestType getRequestType(){
        return this.requestType;
    }

    public Serializable data(){
        return this.data;
    }

    public UserInfo user(){
        return this.user;
    }
}
