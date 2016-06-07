import java.io.*;
import java.util.HashMap;
import java.util.PriorityQueue;


public class Archiver {
	private static final int BUF_SIZE = 4096;

	static byte buff[] = new byte[BUF_SIZE];
	static int pos = -1;

	static HashMap<String, Byte> binval;
	static {
		initBinval();
	}
	private static void initBinval(){
		if(binval != null) return;
		binval = new HashMap<>(258);
		for (int i = 0; i < 128; i++) {
			Integer a = i;
			String s = Integer.toBinaryString(i);
			int l = s.length();
			for (int j = 0; j < 8 - l; j++) {
				s = "0" + s;
			}
			binval.put(s,(byte) i);
		}
		for (int i = -1; i >= -128; i--) {
			binval.put(Integer.toBinaryString(i).substring(24), (byte) i);
		}
	}
	static void archivate(File name){
		initBinval();
		name = name.getAbsoluteFile();
		System.out.println("Archivation started");
		byte a[] = new byte[BUF_SIZE];
		long writtenBytes = 0L;
		long count[] = new long[256];
		try(FileInputStream in = new FileInputStream(name)){
			DataInputStream dataInputStream = new DataInputStream(in);
			int l;
			while ((l = in.read(a)) != -1){
				for (int i = 0; i < l; i++) {
					++count[a[i] + 128];
				}
			}
		}catch (IOException e){
			System.out.println("exc");
		}
		PriorityQueue<Node> queue = new PriorityQueue<>();
		for (int i = 0; i < count.length; i++) {
			if (count[i] > 0)
				queue.add(new Node((byte)(i - 128), count[i]));
		}
		while (queue.size() > 1){
			queue.add(new Node(queue.poll(),queue.poll()));
		}
		Node head = queue.poll();
		HashMap<Byte, String> codeVal = new HashMap<>();
		for (int i = -128; i < 128; i++) {
			Byte b = (byte) i;
			if(count[i + 128] ==0) continue;
			Node x = head;
			String ans = "";
			while (x.set.size()> 1){
				if(x.left != null && x.left.set.contains(b)){
					x = x.left;
					ans += "0";
				}else if(x.right != null && x.right.set.contains(b)){
					x = x.right;
					ans += "1";
				}else {
					System.out.println("Error occured");
				}
			}
			codeVal.put(b,ans);
		}
		File output = new File(name.getAbsoluteFile().getParent()+"/" + name.getName()+ ".arch");
		try (FileOutputStream fileOutputStream = new FileOutputStream(output);
		     ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		     FileInputStream in = new FileInputStream(name)){
			int l;
			objectOutputStream.writeObject(head);
			StringBuilder out = new StringBuilder("");

			while ((l = in.read(a)) != -1){
				for (int i = 0; i < l; i++) {
					out.append( codeVal.get(a[i]));
					writtenBytes++;
					if(out.length() >=8){
						String key = out.substring(0,8);
						byte zap = binval.get(key);
						push(fileOutputStream, zap);
						out =out.delete(0,8);
					}
					if(writtenBytes % 1000_000 == 0) System.out.println("Written mbytes =" + writtenBytes / 1000_000);
				}
			}
			if(out.length() > 0){
				int o = out.length();
				for (int i = 0; i < 8 - o; i++) {
					out.append("0");
				}
				push(fileOutputStream,binval.get(out.substring(0,8)));
			}
			if(pos > -1)
				fileOutputStream.write(buff,0,pos + 1);
			pos = -1;
			fileOutputStream.close();
		}catch (IOException e){
			System.out.println("IOException");
			e.printStackTrace();
		}
		System.out.println(writtenBytes);
		System.out.println("Archivation completed");
	}

	static void dearchivate(File oldName){
		initBinval();
		System.out.println("dearchivation started");
		Node head;
		byte a[] = new byte[BUF_SIZE];
		pos = -1;
		Long writtenBytes = 0L;
		Long fileSize = 0L;
		String old = oldName.getAbsoluteFile().toString();
		File newName = new File(old.substring(0,old.length() - 4));
		try(FileInputStream fileInputStream = new FileInputStream(oldName);
		    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		    FileOutputStream fileOutputStream = new FileOutputStream(newName)) {
			head = (Node) objectInputStream.readObject();
			fileSize = head.frequency;
			int l;
			StringBuilder out = new  StringBuilder("");
			while ((l = fileInputStream.read(a)) != -1){
				for (int i = 0; i < l; i++) {
					String s= Integer.toBinaryString(a[i]);
					if(s.length() < 8){
						int k = s.length();
						for (int j = 0; j < 8 - k; j++) {
							s = "0" + s;
						}
						out .append( s);
					}else out.append(  s.substring(24));

					while (out.length() > 20){
						Node x = head;
						int k = 0;
						while (x.size > 1){
							switch (out.charAt(k)){
								case '0':x = x.left;
									break;
								default:x = x.right;
							}
							k++;
						}
						push(fileOutputStream,(byte)x.set.toArray()[0] );
						writtenBytes++;
						if (writtenBytes % 1_000_000 ==0) System.out.println(writtenBytes / 1_000_000 + "mbytes written");
						out.delete(0, k);
					}
				}
			}
			while (writtenBytes < head.frequency){
				Node x = head;
				int k = 0;
				while (x.size > 1){
					switch (out.charAt(k)){
						case '0':x = x.left;
							break;
						default:x = x.right;
					}
					k++;
				}
				push(fileOutputStream,(byte)x.set.toArray()[0] );
				writtenBytes++;
				out.delete(0, k);
			}
			if (pos > -1){
				fileOutputStream.write(buff, 0, pos + 1);
				pos = -1;
			}
			if (pos > -1){
				fileOutputStream.write(buff, 0, pos + 1);
			}
		}catch (IOException e){
			e.printStackTrace();
		}catch (ClassNotFoundException e){

		}catch (IndexOutOfBoundsException e){
			e.printStackTrace();
		}
	}

	static void push(FileOutputStream out, byte b)throws IOException{
		pos++;
		buff[pos] = b;
		if (pos == buff.length - 1){
			out.write(buff);
			pos = -1;
		}
	}
}
