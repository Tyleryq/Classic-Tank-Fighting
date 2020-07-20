import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class NetBean {
	static ServerSocket server=null;
	static Socket socket=null;
	static ObjectInputStream in;
	static ObjectOutputStream out;
}