package gov.iti.jets.client;

import java.rmi.*;

import gov.iti.jets.dto.MessageDTO;

public interface ClientInt<T> extends Remote
{
void sendMessage(T message) throws RemoteException;
}
