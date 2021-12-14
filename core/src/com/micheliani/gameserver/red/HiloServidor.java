package com.micheliani.gameserver.red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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
//		System.out.println("Mensaje = " + msg);

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
					enviarMensaje("OK-"+(cantClientes+1), clientes[cantClientes].getIp(), clientes[cantClientes++].getPuerto());
					if (cantClientes == 2) {
						Global.empieza = true;
						for (int i = 0; i < clientes.length; i++) {
							enviarMensaje("Empieza", clientes[i].getIp(), clientes[i].getPuerto());

						}
					}
				}
			}
		} else {
			if(nroCliente != -1) {
				
				if(msg.equals("ApreteArriba")) {
					if(nroCliente==0) {
						app.isArriba1 = true;
					}else {
						app.isArriba2 = true;
					}
					
				}else if(msg.equals("ApreteDerecha")) {
					if(nroCliente == 0) {
						app.isDerecha1 = true;
					}else {
						app.isDerecha2 = true;
					}
					
				} else if(msg.equals("ApreteIzquierda")){
					if(nroCliente == 0) {
						app.isIzquierda1 = true;
					}else {
						app.isIzquierda2 = true;
					}
					
				}else if(msg.equals("DejeApretarArriba")) {
					if(nroCliente==0) {
						app.isArriba1 = false;
					}else {
						app.isArriba2 = false;
					}
					
				}else if(msg.equals("DejeApretarDerecha")) {
					if(nroCliente==0) {
						app.isDerecha1 = false;
					}else {
						app.isDerecha2 = false;
					}
					
				}else if(msg.equals("DejeApretarIzquierda")) {
					if(nroCliente == 0) {
						app.isIzquierda1 = false;
					}else {
						app.isIzquierda2 = false;
					}
					
				}
				
			}
		}
		
		
	}

	public void enviarMensajeATodos(String msg) {
		for (int i = 0; i < clientes.length; i++) {
			enviarMensaje(msg, clientes[i].getIp(), clientes[i].getPuerto());
		}
		
	}
	
	
}
	

	


