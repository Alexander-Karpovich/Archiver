import java.io.Serializable;
import java.util.HashSet;

public class Node implements Serializable,Comparable<Node>{
	Node left, right;
	HashSet<Byte> set;
	long frequency;
	int size;

	public Node(Node left, Node right){
		this.left = left;
		this.right = right;
		set = new HashSet<>();
		frequency = 0;
		if(left != null){
			set.addAll(left.set);
			frequency += left.frequency;
		}

		if(right != null){
			set.addAll(right.set);
			frequency += right.frequency;
		}
		size = set.size();
	}

	public Node(Byte b, long frequency){
		left = null;
		right = null;
		this.frequency = frequency;
		set= new HashSet<>();
		set.add(b);
		size = set.size();
	}

	@Override
	public int compareTo(Node o) {
		return Long.signum( this.frequency - o.frequency);
	}

	@Override
	public String toString(){
		return set.toString() +" "+ frequency;
	}
}
