//package com.example.myapplication;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//
//public class updclient  extends Thread{
//
////    String serverIP = "192.168.1.27"; // Replace with the IP  address of your Android device
////    String serverIP = "192.168.191.3"; // Replace with the IP address of your Android device
//    String serverIP = "172.20.10.6"; // Replace with the IP address of your Android device
//    int serverPort = 9876; // The port should match the server's listening port
//    public String message;
//
//    public updclient(String message){
//        this.message = message;
//    }
//
//    public void run(){
//
//
//        try {
//            threadInvoke();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    public void threadInvoke() throws IOException {
//
//        InetAddress serverAddress = InetAddress.getByName(serverIP);
//        DatagramSocket socket = new DatagramSocket();
//
//        byte[] buffer = message.getBytes();
//        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
//
//        socket.send(packet);
//        System.out.println("Padding value sent to the server: " + message);
//
//        socket.close();
//
//    }
//
//}
