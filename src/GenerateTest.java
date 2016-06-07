import java.io.FileOutputStream;
import java.io.IOException;


public class GenerateTest {
	public static void main(String[] args) throws IOException{
		FileOutputStream out = new FileOutputStream("text.txt");
		byte b[] = {-128,-127,-126,-126,-125,-125,-124,-124,-124,-123,-123,-123,-122,-122,-122,-122};
		out.write(b);
		out.close();
	}
}
