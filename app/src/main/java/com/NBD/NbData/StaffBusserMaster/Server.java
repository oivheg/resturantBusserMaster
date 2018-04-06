package com.NBD.NbData.StaffBusserMaster;

// --Commented out by Inspection START (31.01.2018 09.07):
//class Server {
//    private static final int socketServerPORT = 8080;
//    private final MainActivity activity;
//    private ServerSocket serverSocket;
//    private String message = "";
//
//// --Commented out by Inspection START (31.01.2018 09.07):
////    public Server(MainActivity activity) {
////        this.activity = activity;
////        Thread socketServerThread = new Thread(new SocketServerThread());
////        socketServerThread.start();
////    }
//// --Commented out by Inspection STOP (31.01.2018 09.07)
//
//// --Commented out by Inspection START (31.01.2018 09.07):
////    public int getPort() {
////        return socketServerPORT;
////    }
//// --Commented out by Inspection STOP (31.01.2018 09.07)
//
//// --Commented out by Inspection START (31.01.2018 09.07):
////    public void onDestroy() {
////        if (serverSocket != null) {
////            try {
////                serverSocket.close();
////            } catch (IOException e) {
////                // TODO Auto-generated catch block
////                e.printStackTrace();
////            }
////        }
////    }
//// --Commented out by Inspection STOP (31.01.2018 09.07)
//
//// --Commented out by Inspection START (31.01.2018 09.07):
////    public String getIpAddress() {
////        StringBuilder ip = new StringBuilder();
////        try {
////            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
////                    .getNetworkInterfaces();
////            while (enumNetworkInterfaces.hasMoreElements()) {
////                NetworkInterface networkInterface = enumNetworkInterfaces
////                        .nextElement();
////                Enumeration<InetAddress> enumInetAddress = networkInterface
////                        .getInetAddresses();
////                while (enumInetAddress.hasMoreElements()) {
////                    InetAddress inetAddress = enumInetAddress
////                            .nextElement();
////
////                    if (inetAddress.isSiteLocalAddress()) {
////                        ip.append("Server running at : ").append(inetAddress.getHostAddress());
////                    }
////                }
////            }
////
////        } catch (SocketException e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////            ip.append("Something Wrong! ").append(e.toString()).append("\n");
////        }
////        return ip.toString();
////    }
//// --Commented out by Inspection STOP (31.01.2018 09.07)
//
//// --Commented out by Inspection START (31.01.2018 09.07):
////    private class SocketServerThread extends Thread {
////
////        int count = 0;
////
////        @Override
////        public void run() {
////            try {
////                serverSocket = new ServerSocket(socketServerPORT);
////                DataInputStream dataInputStream = null;
////                while (true) {
////                    Socket socket = serverSocket.accept();
////                    count++;
////                    DataInputStream os = new DataInputStream(socket.getInputStream());
////
////                        final String data = os.readUTF();
////
////                        System.out.println("Data: " + data);
////
////
////                    message += "#" + count + " from "
////                            + socket.getInetAddress() + ":"
////                            + socket.getPort() + "\n";
////
////                    activity.runOnUiThread(new Runnable() {
////
////                        @Override
////                        public void run() {
////                            activity.msg.setText(message);
////                            activity.addUser(data);
////                        }
////                    });
////
////                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
////                            socket, count);
////                    socketServerReplyThread.run();
////
////                }
////            } catch (IOException e) {
////                // TODO Auto-generated catch block
////                e.printStackTrace();
////            }
////        }
////
////    }
//// --Commented out by Inspection STOP (31.01.2018 09.07)
//
//    private class SocketServerReplyThread extends Thread {
//
//        final int cnt;
//        private final Socket hostThreadSocket;
//
//// --Commented out by Inspection START (31.01.2018 09.07):
////        SocketServerReplyThread(Socket socket, int c) {
////            hostThreadSocket = socket;
////            cnt = c;
////        }
//// --Commented out by Inspection STOP (31.01.2018 09.07)
//
//        @Override
//        public void run() {
//            OutputStream outputStream;
//            String msgReply = "Hello from Server, you are #" + cnt;
//
//            try {
//                outputStream = hostThreadSocket.getOutputStream();
// --Commented out by Inspection STOP (31.01.2018 09.07)
//                PrintStream printStream = new PrintStream(outputStream);
//                printStream.print(msgReply);
//                printStream.close();
//
//                message = "replayed: " + msgReply + "\n";
//
//                activity.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        activity.msg.setText(message);
//                    }
//                });

//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                message += "Something wrong! " + e.toString() + "\n";
//            }
//
//            activity.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//
//
//                    activity.msg.setText(message);
//                }
//            });
//        }
//
//    }
//}
