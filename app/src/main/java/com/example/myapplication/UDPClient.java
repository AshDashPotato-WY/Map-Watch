//package com.example.myapplication;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//
////public class UDPClient {
////
////    public static void main(String[] args) {
////        String serverIP = "192.168.1.27"; // Replace with the IP address of your Android device
////        int serverPort = 9876; // The port should match the server's listening port
////        String message = "-800"; // This is the padding value that you want to send
////
////        try {
////            InetAddress serverAddress = InetAddress.getByName(serverIP);
////            DatagramSocket socket = new DatagramSocket();
////
////            byte[] buffer = message.getBytes();
////            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
////
////            socket.send(packet);
////            System.out.println("Padding value sent to the server: " + message);
////
////            socket.close();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////    }
////}
