import java.io.FileInputStream;

public class CompareFiles {
	public static void main(String[] args) throws Exception{
		FileInputStream in1 = new FileInputStream("java.jpg");
		FileInputStream in2 = new FileInputStream("unpack.jpg");
		byte buf1[] = new  byte[1000];
		byte buf2[] = new byte[1000];
		int l1;
		int l2;
		long read = 0L;
		while ((l1 = in1.read(buf1)) != -1){
			l2 = in2.read(buf2);
			if(l2 == -1) break;
			for (int i = 0; i < 1000; i++) {
				read++;
				if(buf1[i] != buf2[i]){
					System.out.println(read);
					return;
				}
			}
		}
	}
}
