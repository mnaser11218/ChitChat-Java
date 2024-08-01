import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SocketServer {

    ServerSocket server;
    Socket sk;
    InetAddress addr;
    
    ArrayList<ServerThread> list = new ArrayList<ServerThread>();
    FileLogSinkClass logSink = new FileLogSinkClass("app.log");

    public SocketServer() {

        try {

            addr = InetAddress.getByName("127.0.0.1");
        	//addr = InetAddress.getByName("192.168.43.1");
        	server = new ServerSocket(1234,50,addr);
            System.out.println("\n Waiting for Client connection");
            SocketClient.main(null);
            while(true) {

                sk = server.accept();
                System.out.println(sk.getInetAddress() + " connect");

                //Thread connected clients to ArrayList
                ServerThread st = new ServerThread(this);

                addThread(st);
                logSink.log("thread: " + String.valueOf(st) + "end of thread");
                st.start();
            }
        } catch(IOException e) {
            System.out.println(e + "-> ServerSocket failed");
        }
    }

    public void addThread(ServerThread st) {
        list.add(st);
    }

    public void removeThread(ServerThread st){
        list.remove(st); //remove
    }

    public void broadCast(String message){
        for(ServerThread st : list){
            st.pw.println(message);
        }
    }

    public static void main(String[] args) {
        new SocketServer();
    }
}

class ServerThread extends Thread {
    SocketServer server;
    PrintWriter pw;
    String name;

    public ServerThread(SocketServer server) {
        this.server = server;
    }
    public List<String> userNames = new ArrayList<>();
    FileLogSinkClass logSink = new FileLogSinkClass("app.log");


    @Override
    public void run() {
       // FileLogSinkClass userNames = new FileLogSinkClass("userNames.log");

        try {
            // read
            BufferedReader br = new BufferedReader(new InputStreamReader(server.sk.getInputStream()));

            // writing
            pw = new PrintWriter(server.sk.getOutputStream(), true);
            name = br.readLine();
           if(!this.userNames.contains(this.name)){
               this.userNames.add(name);
           }
            // here is the username
           // logSink.log(name);

            server.broadCast("**["+name+"] Entered**");
            server.broadCast("hello: " + name + " welcome back");
           // printPreviousUsers();


            String data;
            while((data = br.readLine()) != null ){
                if(data == "/list"){
                    pw.println("a");
                }
                server.broadCast("["+name+"] "+ data);

              //  logSink.log("Time: " + new Date() + "\n Name: " + this.name + "\n Message : " + data);
              // logSink.log(server.sk.getInetAddress()+" - ["+name+"]");
            }
        } catch (Exception e) {
            //Remove the current thread from the ArrayList.
            server.removeThread(this);
            server.broadCast("**["+name+"] Left**");
            System.out.println(server.sk.getInetAddress()+" - ["+name+"] Exit");
            System.out.println(e + "---->");
        }
    }

    public void printPreviousUsers(){
        server.broadCast("Here are the previous usernames that signed in: ");
        for(String user : this.userNames){
            server.broadCast(user);
        }

    }
}