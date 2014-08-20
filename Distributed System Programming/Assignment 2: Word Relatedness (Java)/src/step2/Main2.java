package step2;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Main2 {

	public static class Mapper2 extends Mapper<LongWritable, Text, WordPair, PairData>{

		// for every pair <w1,w2> write:
		// <w1,*>	< <w1,*>, decade, c(w1,w2), 0, 0, N >
		// <w1,a>	< <w1,w2>, decade, c(w1,w2), 0, 0, N >
		// <w2,*>	< <w2,*>, decade, c(w1,w2), 0, 0, N >
		// <w2,a>	< <w1,w2>, decade, c(w1,w2), 0, 0, N >
		
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException,  InterruptedException {
			String[] parsedValue = value.toString().split("\t");
			String[] pair = parsedValue[0].split(",");
			Text w1 = new Text(pair[0]);
			Text w2 = new Text(pair[1]);
			String[] pairOccurences = parsedValue[1].split(",");
			int decade = Integer.parseInt(pairOccurences[0]);
			long occurences = Long.parseLong(pairOccurences[1]);
			long N = Long.parseLong(pairOccurences[2]);
			
			if (!pair[0].equals("*")){ // ignore <*,*>
				
				WordPair keyPair = new WordPair(w1,new Text("*"));
				PairData data = new PairData(keyPair, decade, occurences, 0, 0, N);
				context.write(keyPair, data);
					
				keyPair.set(w1,new Text ("a"));
				data.setPair(w1,w2);
				context.write(keyPair, data);
					
				keyPair.set(w2, new Text("*"));
				data.setPair (w2, new Text("*"));
				context.write(keyPair, data);
				
				keyPair.set(w2, new Text("a"));
				data.setPair(w1,w2);
				context.write(keyPair, data);
			}
		}
	}

	public static class Reducer2 extends Reducer<WordPair,PairData,WordPair,PairData> {

		// for <w,*> , calculate c(w) by summing the number of occurrences of all the pairs containing w
		// for <w,a> , update c(w) in every pair containing w, according to its location in the pair,
		//             and write the pair to the context with the updated data
		
		@Override
		public void reduce(WordPair key, Iterable<PairData> values, Context context) throws IOException, InterruptedException {
			long cw = 0;
			String w2;
			for (PairData value : values){
				w2 = value.pair.w2.toString();
				if (w2.equals("*")){
					cw += value.pairCount.get();
				} else {
					String keyWord = key.w1.toString();
					if (keyWord.equals(w2)){
						value.setCw2(cw);
					} else {
						value.setCw1(cw);
					}
					context.write(value.pair, value);
				}
			}
		}
	}

	public static class Partitioner2 extends Partitioner<WordPair, PairData> {

		@Override
		public int getPartition(WordPair key, PairData value, int numPartitions) {
			int reducerNumber = (value.decade.get() % 100)/10;
			if (value.decade.get() == 2000 || value.decade.get() == 2010){
				reducerNumber = reducerNumber + 10;
			}
			return reducerNumber;
		}
	}
	
	public static class NaturalKeyGroupingComparator extends WritableComparator {

		protected NaturalKeyGroupingComparator() {
			super(WordPair.class, true);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public int compare(WritableComparable pair1, WritableComparable pair2) {
			WordPair p1 = (WordPair)pair1;
			WordPair p2 = (WordPair)pair2;
			
			return p1.w1.toString().compareTo(p2.w1.toString());
		}
	}
	
		
	public static class CompositeKeyComparator extends WritableComparator {
	
		protected CompositeKeyComparator() {
			super(WordPair.class, true);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public int compare(WritableComparable pair1, WritableComparable pair2) {
			WordPair p1 = (WordPair)pair1;
			WordPair p2 = (WordPair)pair2;
			
			int result = p1.w1.toString().compareTo(p2.w1.toString());
			if(0 == result) {
				result = p1.w2.toString().compareTo(p2.w2.toString());
			}
			return result;
		}
	}


	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf,"Step2");
		job.setJarByClass(Main2.class);
		job.setGroupingComparatorClass(NaturalKeyGroupingComparator.class);
		job.setSortComparatorClass(CompositeKeyComparator.class);
		job.setMapperClass(Mapper2.class);
		job.setPartitionerClass(Partitioner2.class);
		job.setReducerClass(Reducer2.class);
		job.setNumReduceTasks(12);
		job.setOutputKeyClass(WordPair.class);
		job.setOutputValueClass(PairData.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}