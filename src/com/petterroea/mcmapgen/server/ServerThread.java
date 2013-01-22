package com.petterroea.mcmapgen.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.LinkedList;

import com.petterroea.mcmapgen.MapGenSettings;
import com.petterroea.mcmapgen.Util;

public class ServerThread implements Runnable {
	private MapGenSettings settings;
	private boolean shouldGenerate;
	private ByteBuffer writeBuffer;
    private CharsetDecoder asciiDecoder;
    private ByteBuffer readBuffer;
    private Selector readSelector;
	private ServerSocketChannel sSChannel;
	private LinkedList<Client> clients;
	private boolean running = true;
	private int regionsFinished = 0;
	public boolean[] regions;
	
	public ServerThread(MapGenSettings settings, boolean generate, int port)
	{
		try {
			this.shouldGenerate = generate;
			this.settings = settings;
			clients = new LinkedList<Client>();
			sSChannel = ServerSocketChannel.open();
			sSChannel.configureBlocking(false);
			System.out.println("Opened the socket channel");
			asciiDecoder = Charset.forName( "UTF-8").newDecoder();
			InetAddress iAddr = InetAddress.getLocalHost();
			sSChannel.socket().bind(new InetSocketAddress(iAddr, port));
			System.out.println("Bound IP");
			readSelector = Selector.open();
			regions = new boolean[settings.regionsx*settings.regionsy];
		} catch(IOException e) {
			System.out.println("ERROR: Could not bind IP");
			System.exit(0);
		} catch(Exception e) {
			
		}

	}
	@Override
	public void run() {
		System.out.println("Started server thread...");
		System.out.println("");
		while(running)
		{
			long startTime = System.currentTimeMillis();
			//Do stuff here
			updateStatus();
			//This calculates the time to sleep. This is to relax the server.
			long timeLeft = 300 - (System.currentTimeMillis()-startTime);
			if(timeLeft>0)
			{
				Util.sleep(timeLeft);
			}
		}
	}
	public void updateStatus()
	{
		System.out.println("Regions done: " + regionsFinished + ", connected clients: " + clients.size() + ".\r");
		System.out.println("Region progress:");
		for(int x = 0; x < settings.regionsx; x++)
		{
			for(int y = 0; y < settings.regionsy; y++)
			{
				
			}
		}
	}
	private void acceptNewPeeps()
	{
		try {
		    SocketChannel clientChannel;
		    // since sSockChan is non-blocking, this will return immediately 
		    // regardless of whether there is a connection available
		    while ((clientChannel = sSChannel.accept()) != null) {
			clientChannel.configureBlocking( false);
		    SelectionKey readKey = clientChannel.register(readSelector, SelectionKey.OP_READ, new StringBuffer());
		    clients.add(new Client(clientChannel));
		    }		
		}
		catch (IOException ioe) {
		    System.out.println("error during accept(): " + ioe);
		}
		catch (Exception e) {
		    System.out.println("exception in acceptNewConnections()" + e);
		}
	}

}
