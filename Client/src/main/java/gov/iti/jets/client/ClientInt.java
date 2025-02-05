package gov.iti.jets.client;

import java.rmi.*;

import gov.iti.jets.dto.MessageDTO;

public interface ClientInt extends Remote
{
void sendMessage(MessageDTO message) throws RemoteException;
}
