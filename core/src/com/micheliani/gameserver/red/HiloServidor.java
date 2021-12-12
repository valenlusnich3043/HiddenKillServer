package com.micheliani.gameserver.red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.micheliani.gameserver.HiddenKIllServer;
import com.micheliani.gameserver.pantallas.PantallaJuego;
import com.micheliani.gameserver.utiles.Global;

public class HiloServidor extends Thread{

	private DatagramSocket conexion;
	private boolean fin = false;
	private DireccionRed[] clientes = new DireccionRed[2];
	private int cantClientes = 0;
	private PantallaJuego app;
	
	public HiloServidor(PantallaJuego app){
		this.app = app;
		
		try {
			conexion = new DatagramSocket(9990);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	 
	public void enviarMensaje(String msg, InetAddress ip, int puerto) {

		byte[] data = msg.getBytes();
		DatagramPacket dp = new DatagramPacket(data, data.length, ip, puerto);
		try {
			conexion.send(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		do {
			byte[] data = new byte[1024];
			DatagramPacket dp = new DatagramPacket(data, data.length);
			try {
				conexion.receive(dp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			procesarMensaje(dp);
		}while(!fin);
	}

	private void procesarMensaje(DatagramPacket dp) {
		String msg = (new String(dp.getData())).trim();
		System.out.println("Mensaje = " + msg);
<<<<<<< HEAD
		if(msg.equals("Conexion")) {
			System.out.println("Llega msg conexion cliente " + cantClientes);
			if(cantClientes < 2) {
				clientes[cantClientes] = new DireccionRed(dp.getAddress(), dp.getPort());
				enviarMensaje("OK", clientes[cantClientes].getIp(),clientes[cantClientes++].getPuerto());
				if(cantClientes == 2) {
					Global.empieza = true;
					for (int i = 0; i < clientes.length; i++) {
						enviarMensaje("Empieza", clientes[i].getIp(),clientes[i].getPuerto());
						
=======

		int nroCliente = -1;

		if (cantClientes > 1) {
			for (int i = 0; i < clientes.length; i++) {
				if (dp.getPort() == clientes[i].getPuerto() && dp.getAddress().equals(clientes[i].getIp())) {
					nroCliente = i;
				}
			}
		}

		if (cantClientes < 2) {
			if (msg.equals("Conexion")) {
				System.out.println("Llega msg conexion cliente " + cantClientes);
				if (cantClientes < 2) {
					clientes[cantClientes] = new DireccionRed(dp.getAddress(), dp.getPort());
					enviarMensaje("OK", clientes[cantClientes].getIp(), clientes[cantClientes++].getPuerto());
					if (cantClientes == 2) {
						Global.empieza = true;
						for (int i = 0; i < clientes.length; i++) {
							enviarMensaje("Empieza", clientes[i].getIp(), clientes[i].getPuerto());

						}
>>>>>>> d452f0f3d15f0b5a49dce56ec6c1c56514a5460f
					}
				}
			}
		} else {
			if(nroCliente != -1) {
				if(msg.equals("Aribba")) {
					
				}else if(msg.equals("Derecha")) {
					
				} else {
					
				}
				
			}
		}
		
		
	}
	
	
}
