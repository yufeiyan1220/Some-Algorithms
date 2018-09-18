import java.util.Set;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
	private BreadthFirstDirectedPath_Marked bfd_v;
	private BreadthFirstDirectedPath_Marked bfd_w;
	private Digraph G;

	// constructor takes a digraph (not necessarily a DAG)
	public SAP(Digraph G) {
		this.G = G;
	}
	private void buildBFD(int v, int w) {
		bfd_v = new BreadthFirstDirectedPath_Marked(G, v);
		bfd_w = new BreadthFirstDirectedPath_Marked(G, w);
	}
	private void buildBFD(Iterable<Integer> v, Iterable<Integer> w) {
		bfd_v = new BreadthFirstDirectedPath_Marked(G, v);
		bfd_w = new BreadthFirstDirectedPath_Marked(G, w);
	}
	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) {
		int shortest_anscestor = ancestor(v, w);
		int length_ancestor = -1;
		if (shortest_anscestor != -1) {
			length_ancestor = bfd_v.distTo(shortest_anscestor) + bfd_w.distTo(shortest_anscestor);
		}
		return length_ancestor;
	}

	// a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
	public int ancestor(int v, int w) {
		buildBFD(v, w);
		Set<Integer> set_ancestor = bfd_v.getMarked();
		set_ancestor.retainAll(bfd_w.getMarked());
		int distance = Integer.MAX_VALUE;
		int temp = 0;
		int shortest_ancestor = -1;
		for (int i : set_ancestor) {
			temp = bfd_v.distTo(i) + bfd_w.distTo(i);
			if (temp < distance) {
				distance = temp;
				shortest_ancestor = i;
			}
		}
		return shortest_ancestor;
	}

	// length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		int shortest_anscestor = ancestor(v, w);
		int length_ancestor = -1;
		if (shortest_anscestor != -1) {
			length_ancestor = bfd_v.distTo(shortest_anscestor) + bfd_w.distTo(shortest_anscestor);
		}
		return length_ancestor;
	}

	// a common ancestor that participates in shortest ancestral path; -1 if no such path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		buildBFD(v, w);
		Set<Integer> set_ancestor = bfd_v.getMarked();
		set_ancestor.retainAll(bfd_w.getMarked());
		int distance = Integer.MAX_VALUE;
		int temp = 0;
		int shortest_ancestor = -1;
		for (int i : set_ancestor) {
			temp = bfd_v.distTo(i) + bfd_w.distTo(i);
			if (temp < distance) {
				shortest_ancestor = i;
			}
		}
		return shortest_ancestor;
	}
}
