import java.io.IOException;
import java.util.Scanner;

public class Driver {

	public static boolean run = false;

	public static void main(String[] args){
		run =true;
		Scanner sc = new Scanner(System.in);
		ServerProtocolFactory p = new MiniIRCProtocolFactory();
		int port = 6667;
		MultipleClientProtocolServer m = new MultipleClientProtocolServer(port ,p );
		Thread t = new Thread(m);
		t.start();
		sc.nextLine();
		try {
			m.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {t.join();} catch (InterruptedException e) {}
		System.out.println("server is closed");
		sc.close();
	}

}