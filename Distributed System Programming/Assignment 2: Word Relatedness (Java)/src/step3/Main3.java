package step3;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Main3 {

	public static class Mapper3 extends Mapper<LongWritable, Text, WordPair, PairData>{
		
		// write <pair,data> to context as is
		
		@Override
	    public void map(LongWritable key, Text value, Context context) throws IOException,  InterruptedException {
			String[] parsedValue = value.toString().split("\t");
			String[] pair_temp = parsedValue[0].split(",");
			Text w1 = new Text(pair_temp[0]);
			Text w2 = new Text(pair_temp[1]);
			WordPair pair = new WordPair(w1,w2);
			String[] pairData = parsedValue[1].split(",");
			int decade = Integer.parseInt(pairData[2]);
			long cw1w2 = Long.parseLong(pairData[3]);
			long cw1 = Long.parseLong(pairData[4]);
			long cw2 = Long.parseLong(pairData[5]);
			long N = Long.parseLong(pairData[6]);
			PairData data = new PairData(pair, decade, cw1w2, cw1, cw2, N);
			context.write(pair, data);
		}
	}
	
	public static class Reducer3 extends Reducer<WordPair,PairData,WordPair,PairData> {
		
		// for every pair we have two data values: one that has c(w1) and one that has c(w2)
		// merge the c(w1) and c(w2) values and write the pair to the context with the updated data
		
		@Override
	    public void reduce(WordPair key, Iterable<PairData> values, Context context) throws IOException, InterruptedException {
			int decade = 0;
			long N = 0;
			long cw1w2 = 0;
			long cw1 = 0;
			long cw2 = 0;
			for (PairData value : values){
				decade = value.decade.get();
				cw1 += value.firstWordCount.get();
				cw2 += value.secondWordCount.get();
				N = value.N.get();
				cw1w2 = value.pairCount.get();
			}
			PairData data = new PairData(key,decade,cw1w2,cw1,cw2,N);
			context.write(key, data);
		}
	}
	
	public static class Partitioner3 extends Partitioner<WordPair, PairData> {
		
		@Override
	    public int getPartition(WordPair key, PairData value, int numPartitions) {
			int reducerNumber = (value.decade.get() % 100)/10;
			if (value.decade.get() == 2000 || value.decade.get() == 2010){
				reducerNumber = reducerNumber + 10;
			}
			return reducerNumber;
	    }
	}
	
	public static void main(String[] args) throws Exception {
	    Configuration conf = new Configuration();
	    Job job = new Job(conf,"Step3");
	    job.setJarByClass(Main3.class);
	    job.setMapperClass(Mapper3.class);
	    job.setPartitionerClass(Partitioner3.class);
	    job.setReducerClass(Reducer3.class);
	    job.setNumReduceTasks(12);
	    job.setOutputKeyClass(WordPair.class);
	    job.setOutputValueClass(PairData.class);
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

