package step1;

import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Main1 {

	public static class Mapper1 extends Mapper<LongWritable, Text, WordPair, PairOccurences>{

		private WordPair wordPair = new WordPair(); // <w1,w2>
		private PairOccurences data = new PairOccurences(); // <decade, occurrences, N>
		private HashMap<String,String> stopWords = initializeStopWords();

		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException,  InterruptedException {

			// parse n-gram
			String[] parsedValue = value.toString().split("\t");
			String[] sentence = parsedValue[0].split(" ");
			
			if(parsedValue.length > 1){
				
				int year = Integer.parseInt(parsedValue[1]);
				int decade = year-(year%10);
				long occurences = Long.parseLong(parsedValue[2]);
				data.set(decade,occurences,0);

				boolean flag = (year >= 1900) && sentence.length>1;
				
				// take the middle word in the sentence
				String middleWord = clearWord(sentence[sentence.length/2]);

				// check if the middle word is not a stop word, if the year is in a relevant decade, 
				// and if the n-gram contains more that one word
				if (flag && !stopWords.containsKey(middleWord)){
					for (int i=0 ; i<sentence.length ; i++){
						String currentWord = clearWord(sentence[i]);
						// create pair only if the current word is not the same as the middle word and is not a stop word
						if (currentWord != null && middleWord != null &&
									!stopWords.containsKey(currentWord) && !(currentWord.equals(middleWord))){
							// arrange the words in a lexicographic order
							if (currentWord.compareTo(middleWord)<0){
								wordPair.set(new Text(currentWord),new Text(middleWord));
							}
							else if(currentWord.compareTo(middleWord)>0){
								wordPair.set(new Text(middleWord),new Text(currentWord));
							}
							context.write(wordPair, data);
							
							// in order to calculate N later
							context.write(new WordPair(new Text("*"),new Text("*")), new PairOccurences(decade,occurences,0));
						}
					}
				}
			}
		}
	
		private HashMap<String,String> initializeStopWords(){
			String[] StopWordsArr = {null,"","a","about","above","after","again","against","all","am","an","and","any","are","aren't","as",
					"at","be","because","been","before","being","below","between","both","but","by","can't","cannot","could","couldn't","did",
					"didn't","do","does","doesn't","doing","don't","down","during","each","few","for","from","further","had","hadn't","has",
					"hasn't","have","haven't","having","he","he'd","he'll","he's","her","here","here's","hers","herself","him","himself","his",
					"how","how's","i","i'd","i'll","i'm","i've","if","in","into","is","isn't","it","it's","its","itself","let's","me","more",
					"most","mustn't","my","myself","no","nor","not","of","off","on","once","only","or","other","ought","our","ours","ourselves",
					"out","over","own","same","shan't","she","she'd","she'll","she's","should","shouldn't","so","some","such","than","that",
					"that's","the","their","theirs","them","themselves","then","there","there's","these","they","they'd","they'll","they're",
					"they've","this","those","through","to","too","under","until","up","very","was","wasn't","we","we'd","we'll","we're","we've",
					"were","weren't","what","what's","when","when's","where","where's","which","while","who","who's","whom","why","why's","with",
					"won't","would","wouldn't","you","you'd","you'll","you're","you've","your","yours","yourself","yourselves","b","c","e","f",
					"g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
			HashMap<String,String> stopWords = new HashMap<String,String>();
			for (int i = 0 ; i < StopWordsArr.length ; i++){
				stopWords.put(StopWordsArr[i],"");
			}
			return stopWords;
		}
	
		private String clearWord(String word){
			String notLetters = ".,/;:'\"!@#$%^&*()~`-—{}[]+=_?<>1234567890°";
			String res = "";
			for (int i=0 ; i<word.length() ; i++){
				char currentChar = word.charAt(i);
				if (notLetters.indexOf(currentChar)<0){ // if the char is a letter, keep it
					res = res+currentChar;
				}
			}
			res = res.toLowerCase();
			return res;
		}
	}
	
	public static class Reducer1 extends Reducer<WordPair, PairOccurences, WordPair, PairOccurences> {
	
		LongWritable N;
	
		@Override
		public void reduce(WordPair key, Iterable<PairOccurences> values, Context context) throws IOException, InterruptedException {
	
			// calculate N
			String w1 = key.w1.toString();
			if (w1.equals("*")){
				long n = 0;
				int decade = 0;
				for (PairOccurences value : values){
					n += value.occurences.get();
					decade = value.decade.get();
				}
				N = new LongWritable(n);
				context.write(key, new PairOccurences(decade,N.get(),N.get()));
			} 
			// calculate c(w1,w2) for every pair, and add N to the value
			else {
				int decade = 0;
				long sum = 0;
				for (PairOccurences value : values){
					sum += value.occurences.get();
					decade = value.decade.get();
				}
				context.write(key, new PairOccurences(decade,sum,N.get()));
			}
		}
	}
	
	public static class Partitioner1 extends Partitioner<WordPair, PairOccurences> {
	
		// part keys by decade
		@Override
		public int getPartition(WordPair key, PairOccurences value, int numPartitions) {
			int reducerNumber = (value.decade.get() % 100)/10;
			if (value.decade.get() == 2000 || value.decade.get() == 2010){
				reducerNumber = reducerNumber + 10;
			}
			return reducerNumber;
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf,"Step1");
		job.setJarByClass(Main1.class);
		job.setMapperClass(Mapper1.class);
		job.setPartitionerClass(Partitioner1.class);
		job.setReducerClass(Reducer1.class);
		job.setNumReduceTasks(12);
		job.setOutputKeyClass(WordPair.class);
		job.setOutputValueClass(PairOccurences.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
