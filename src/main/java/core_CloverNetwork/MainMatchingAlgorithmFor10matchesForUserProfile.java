package core_CloverNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
 
 



public class MainMatchingAlgorithmFor10matchesForUserProfile {

	
	

 
		private static final int[] IMPORTANCE_POINTS = new int[]{0, 1, 10, 50, 250};

		public static void main(String[] args)
		{
			 
 			try {
			    Scanner scan = new Scanner(new File("/Users/cydobolctbnem/Documents/Development/CloverChallenge/src/main/resources/input.json"));//please customize path for the input json file
			    String str = new String();
	    
			    while (scan.hasNext()){
			        str += scan.nextLine();
			    }
			    
			    scan.close();
			    JSONObject obj = new JSONObject(str);

 				HashMap<Integer, PriorityQueue> map = new HashMap<Integer,PriorityQueue>();
				JSONArray profiles = obj.getJSONArray("profiles");
				initializeMap(map, profiles);
				buildMap(map, profiles);
				System.out.println(buildResult(map));
			}
			catch (FileNotFoundException ex){
				System.out.println("OOOOOps! Please debug!, there was a problem");
			}

		}

		public static void initializeMap(HashMap map, JSONArray profiles) 
		{
			for(int i = 0; i < profiles.length(); i++) 
			{
				JSONObject profile = profiles.getJSONObject(i);
				PriorityQueue<CompaMatch> pq = new PriorityQueue<CompaMatch>(100, new MatchPercentage()); 
				int profileId = profile.getInt("id");
				map.put(profileId, pq);
			}
		}

		public static void buildMap(HashMap map, JSONArray profiles) 
		{
			for(int i = 0; i < profiles.length(); i++) 
			{
				JSONObject A = profiles.getJSONObject(i);
				JSONArray answersA = A.getJSONArray("answers");
				for(int j = 0; j < profiles.length(); j++) 
				{
					JSONObject B = profiles.getJSONObject(j);
					if(A == B)
						continue;
					JSONArray answersB = B.getJSONArray("answers");
					int setSize = getCommonQuestionsCount(answersA, answersB);

					double score = getScore(answersA, answersB, setSize);
					
					CompaMatch bestMatch = new CompaMatch(B.getInt("id"), score);
					((PriorityQueue<CompaMatch>)map.get(A.getInt("id"))).add(bestMatch);
				}
			}
		}

		public static int getCommonQuestionsCount(JSONArray a, JSONArray b)
		{
			HashSet<Integer> questionSet = new HashSet<Integer>();
			int count = 0;
			for(int i = 0; i < a.length(); i++) 
			{
				questionSet.add(a.getJSONObject(i).getInt("questionId"));
			}

			JSONObject answer;
			for(int i = 0; i < b.length(); i++) 
			{
				answer = b.getJSONObject(i);
				if(questionSet.contains(answer.getInt("questionId"))) {
					count++;
				}
			}
			return count;
		}

		public static double getScore(JSONArray answersA, JSONArray answersB, int setSize) 
		{
			double score = Math.sqrt(getScoreHelper(answersA, answersB) * getScoreHelper(answersA, answersB));
			double marginOfError = 1.0/(double)setSize;
			double truescore = score - marginOfError;

			if(truescore < 0)
				return 0;

			return truescore;
		}

		public static double getScoreHelper(JSONArray answersA, JSONArray answersB) 
		{
			double possible = 0;
			double earned = 0;

			JSONObject answerA;
			JSONObject answerB;
			JSONArray acceptableAnswers;

			for(int i = 0; i < answersA.length(); i++) 
			{
				answerA = answersA.getJSONObject(i);
				acceptableAnswers = answerA.getJSONArray("acceptableAnswers");

				for(int j = 0; j < answersB.length(); j++) 
				{
					answerB = answersB.getJSONObject(j);

					if(answerA.getInt("questionId") == answerB.getInt("questionId")) {
						int importance = IMPORTANCE_POINTS[answerA.getInt("importance")];
						possible += importance;

						for(int k = 0; k < acceptableAnswers.length(); k++) 
						{
							if(acceptableAnswers.getInt(k) == answerB.getInt("answer")) {
								earned += importance;
							}
						}
					}
				}
			}

			if(possible == 0)
				return 0;
			
			return earned/possible;
		}

		public static String buildResult(HashMap map)
		{
			ArrayList<Integer> keys = new ArrayList<Integer>(map.keySet());
			ArrayList<PriorityQueue<CompaMatch>> values =  new ArrayList<PriorityQueue<CompaMatch>>(map.values());

			String result = "{ \n\t\"results\": \n\t\t[  ";
			Integer key;
			PriorityQueue value;
			CompaMatch match;
			Iterator it;

			for(int i = 0; i < keys.size(); i++)
			{
				key = keys.get(i);
				result += "\n\t\t\t{ \n\t\t\t\t\"profileId\":" + key + ", \n\t\t\t\t\"matches\":[";

				value = values.get(i);
				it = value.iterator();

				for(int j = 0; j < 10 && it.hasNext(); j++)
				{
					match = (CompaMatch)it.next();
						result += " \n\t\t\t\t\t{  \n\t\t\t\t\t\"profileId\":" + match.id + ", \n\t\t\t\t\t\"score\":" + match.score + "\n\t\t\t\t\t}";
						if(i < 10 && it.hasNext())
							result += ",";
				}
				result += "\n\t\t\t\t] \n\t\t\t}";

				if(i < keys.size() - 1)
					result += ",";
			}

			result += " \n\t\t] \n\t}";

			return result;
		}
	}
	
	
 
