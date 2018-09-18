import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
public class WordNet {
	private Digraph G;
	private SAP sap;
	private List<String> synsets = new ArrayList<String>();
	private List<String> synsets_withoutsame = new ArrayList<String>();
	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) {
		In input = new In(synsets);
		String line = null;
		String[] words;
		int num = 0;
		ArrayList<Integer> temp_value;
		while ((line = input.readLine()) != null) {
			words = line.split(",");
			//System.out.println(words[0]);
			this.synsets.add(words[1]);				
	   	}
		synsets_withoutsame = removeDuplicateWithOrder(this.synsets);
		G = new Digraph(synsets_withoutsame.size());
		
	   	In input2 = new In(hypernyms);
	   	String temp_key;
	   	String temp_key2;		   		   	      
        List<String> synsets_withoutsame = new ArrayList<String>(this.synsets_withoutsame);
            
	   	while ((line = input2.readLine()) != null) {
	   		String[] temp_hypernym = line.split(",");
	   		temp_key = this.synsets.get(Integer.parseInt(temp_hypernym[0]));
	   		for (int i = 1; i < temp_hypernym.length; i++) {
	   			temp_key2 = this.synsets.get(Integer.parseInt(temp_hypernym[i]));
	   			G.addEdge(this.synsets_withoutsame.indexOf(temp_key), this.synsets_withoutsame.indexOf(temp_key2));
	   		}
	   		if (synsets_withoutsame.contains(temp_key)) {
    			synsets_withoutsame.remove(temp_key);
    		}
	   	}
	   	
	   	//test for root: no more than one candidate root  
	   	if (synsets_withoutsame.size() > 1){  
            throw new java.lang.IllegalArgumentException();  
        }  
	   	//test for root: no cycle  
        DirectedCycle directedCycle = new DirectedCycle(G);  
        if (directedCycle.hasCycle()){  
            throw new java.lang.IllegalArgumentException();  
        }  
        sap = new SAP(G);  		
	}
	private List<String> removeDuplicateWithOrder(List<String> list) {    
		Set<String> set = new HashSet<String>();    
	    List<String> newList = new ArrayList<String>();    
	    for (Iterator<String> iter = list.iterator(); iter.hasNext();) {    
	    	String element = iter.next();    
	        if (set.add(element))    
	        	newList.add(element);    
	      	}     
	    return newList;
	}

	// returns all WordNet nouns
	public Iterable<String> nouns() {
		return synsets_withoutsame;
	}

	// is the word a WordNet noun?
	public boolean isNoun(String word) {
	   return synsets_withoutsame.contains(word);
	}

	// distance between nounA and nounB (defined below)
   	public int distance(String nounA, String nounB) {
   		if (!isNoun(nounA)) {  
            throw new java.lang.IllegalArgumentException();  
        }  
        if (!isNoun(nounB)) {  
            throw new java.lang.IllegalArgumentException();  
        }
        return sap.length(synsets_withoutsame.indexOf(nounA), synsets_withoutsame.indexOf(nounB));
   	}

   	// a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
   	// in a shortest ancestral path (defined below)
   	public String sap(String nounA, String nounB) {
   		if (!isNoun(nounA)) {  
            throw new java.lang.IllegalArgumentException();  
        }  
        if (!isNoun(nounB)) {  
            throw new java.lang.IllegalArgumentException();  
        }
        int index_ancestor = sap.ancestor(synsets_withoutsame.indexOf(nounA), synsets_withoutsame.indexOf(nounB));
        return synsets_withoutsame.get(index_ancestor);
   	}  	
	
}
