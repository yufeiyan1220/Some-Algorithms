import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
	private static final double INFINITE = 1e8;
	private static final double FLOATING_POINT_EPSILON = 1E-5;
	private int numofteam;
	private List<String> teams;
	private int[] wins;
	private int[] rank;
	private int[] losses;
	private int[] remaining;
	private int[][] versus;
	private int num_versus_teams;
	private int temp_num_versus_teams;
	private FlowNetwork G;
	private FordFulkerson ff;
	public BaseballElimination(String filename) {
		if (filename == null) {
            throw new IllegalArgumentException("the argument to BaseballElimination() is null\n");
        }
		In input = new In((filename));
		String line;
		String[] line_split;
		int line_num = 0;
		num_versus_teams = 0;
		while (input.hasNextLine()) {
			line = input.readLine();
			line_num++;
			if (line_num == 1) {
				numofteam = Integer.parseInt(line);
				teams = new ArrayList<String>();
				wins = new int[numofteam];
				losses = new int[numofteam];
				remaining = new int[numofteam];
				versus = new int[numofteam][numofteam];
			}
			else {
				line_split = line.trim().split("\\s+");
				teams.add(line_split[0]);
				wins[line_num - 2] = Integer.parseInt(line_split[1]);
				losses[line_num - 2] = Integer.parseInt(line_split[2]);
				remaining[line_num - 2] = Integer.parseInt(line_split[3]);
				for (int i = 0; i < numofteam; i++) {
					versus[line_num - 2][i] = Integer.parseInt(line_split[i + 4]);
					if (versus[line_num - 2][i] != 0) num_versus_teams++;
				}
				
			}
		}		
		Ranking();
	}                  // create a baseball division from given filename in format specified below
	public	int numberOfTeams() {
		return numofteam;
	}                        // number of teams
	public Iterable<String> teams() {
		return teams;
	}                               // all teams
	public	int wins(String team) {
		validateTeam(team);
		return wins[teams.indexOf(team)];
	}                      // number of wins for given team
	public	int losses(String team) {
		validateTeam(team);
		return losses[teams.indexOf(team)];
	}                    // number of losses for given team
	public	int remaining(String team) {
		validateTeam(team);
		return remaining[teams.indexOf(team)];
	}                // number of remaining games for given team

	public	int against(String team1, String team2) {
		validateTeam(team1);
		validateTeam(team2);
		int i_team1 = teams.indexOf(team1);
		int i_team2 = teams.indexOf(team2); 
		return versus[i_team1][i_team2];
	}   // number of remaining games between team1 and team2
	public	boolean isEliminated(String team) {
		validateTeam(team);
		return certificateOfElimination(team) != null;		
	}             // is given team eliminated?
	public Iterable<String> certificateOfElimination(String team) {
		validateTeam(team);
		int i_team = teams.indexOf(team);
		ArrayList<String> R = new ArrayList<String>();
		//trivial 
		if (wins[i_team] + remaining[i_team] < rank[0]) {
			for (int i = 0; i < numofteam; i++) {
				if (wins[i_team] + remaining[i_team] < wins[i])
					R.add(teams.get(i));
			}
		}
		//non-trivial
		else {
			int num_versus_teams = this.num_versus_teams;
			for (int i = 0; i < numofteam; i++) {
				if(versus[i_team][i] != 0) num_versus_teams--;
				if(versus[i][i_team] != 0) num_versus_teams--;
			}
			temp_num_versus_teams = num_versus_teams/2;
			int numofnode = num_versus_teams/2 + numofteam - 1 + 2;
			G = new FlowNetwork(numofnode);
			int temp = 0;
			for (int i = 0; i < numofteam; i++) {
				for (int j = i; j < numofteam; j++) {
					if (versus[i][j] != 0 && i != i_team && j != i_team) {					
						G.addEdge(new FlowEdge(numofnode - 2, temp, versus[i][j]));
						if (i < i_team)
							G.addEdge(new FlowEdge(temp, num_versus_teams/2 + i, INFINITE));
						else
							G.addEdge(new FlowEdge(temp, num_versus_teams/2 + i - 1, INFINITE));
						if (j < i_team)
							G.addEdge(new FlowEdge(temp, num_versus_teams/2 + j, INFINITE));
						else
							G.addEdge(new FlowEdge(temp, num_versus_teams/2 + j - 1, INFINITE));
						temp++;
					}
				}
			}
			for (int i = 0; i < numofteam - 1; i++) {
				 if (i < i_team) {
					 G.addEdge(new FlowEdge(num_versus_teams/2 + i, numofnode - 1, remaining[i_team] + wins[i_team] - wins[i]));
				 }
				 else {
					 G.addEdge(new FlowEdge(num_versus_teams/2 + i , numofnode - 1, remaining[i_team] + wins[i_team] - wins[i + 1]));
				 }
			}
			ff = new FordFulkerson(G, numofnode - 2, numofnode - 1);
			for (int i = 0; i < numofteam - 1; i++) {
				if (ff.inCut(temp_num_versus_teams + i))
					if(i < i_team) R.add(teams.get(i));
					else R.add(teams.get(i + 1));
			}
		}
		if (R.isEmpty()) return null;
		else return R;
		
	}  // subset R of teams that eliminates given team; null if not eliminated
	private void Ranking() {
		rank = new int[numofteam];
		int k = 0;
		int temp;
		for (int i = 0; i < numofteam; i++) {
			rank[i] = wins[i];
			k = i - 1;
			while(k >= 0 && rank[k + 1] >= rank[k]) {
				temp = rank[k];
				rank[k] = rank[k + 1];
				rank[k + 1] =temp;
				k--;
			}
		}
	}
	public static void main(String[] args) {
	    BaseballElimination division = new BaseballElimination(args[0]);
	    for (String team : division.teams()) {
	        if (division.isEliminated(team)) {
	            StdOut.print(team + " is eliminated by the subset R = { ");
	            for (String t : division.certificateOfElimination(team)) {
	                StdOut.print(t + " ");
	            }
	            StdOut.println("}");
	        }
	        else {
	            StdOut.println(team + " is not eliminated");
	        }
	    }
	}
	private void validateTeam(String team) {
		if (team == null || !teams.contains(team)) {
            throw new IllegalArgumentException("invalid argument\n");
        }
	}
}
