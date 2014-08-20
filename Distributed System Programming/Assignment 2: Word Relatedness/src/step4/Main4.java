package step4;

import java.io.IOException;
import java.util.Iterator;

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
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Main4 {

	public static class Mapper4 extends Mapper<LongWritable, Text, FinalData, PairData>{
		
		// for every pair <w1,w2> calculate the three measures using the data
		// for every measure, write:  <measureNumber,measureValue>	<PaireData>
		
		@Override
	    public void map(LongWritable key, Text value, Context context) throws IOException,  InterruptedException {
			String[] key_value = value.toString().split("\t");
			String[] pairData = key_value[1].split(",");
			Text w1 = new Text(pairData[0]);
			Text w2 = new Text(pairData[1]);
			int decade = Integer.parseInt(pairData[2]);
			double cw1w2 = Double.parseDouble(pairData[3]);
			double cw1 = Double.parseDouble(pairData[4]);
			double cw2 = Double.parseDouble(pairData[5]);
			double N = Double.parseDouble(pairData[6]);

			WordPair pair = new WordPair(w1,w2);
			PairData data = new PairData(pair, decade, (long)cw1w2, (long)cw1, (long)cw2, (long) N);
			
			double probability = cw1w2 / N;
			double dice = (2*cw1w2) / (cw1 + cw2);
			double geometricMean = Math.sqrt(probability*dice);
			
			FinalData fd = new FinalData(1,probability);
			context.write(fd, data);
			
			fd.set(2, dice);
			context.write(fd, data);
			
			fd.set(3, geometricMean);
			context.write(fd, data);
		}
	}
	
	public static class Reducer4 extends Reducer<FinalData,PairData,FinalData,PairData> {
		
		// key: measure number
		// value: pair
		// write to context the first k pairs for each measure
		
		@Override
	    public void reduce(FinalData key, Iterable<PairData> values, Context context) throws IOException, InterruptedException {
			
			int k = Integer.parseInt(context.getConfiguration().get("k","-1"));
			Iterator<PairData> itr = values.iterator();
			int i = 0;
			while (i < k && itr.hasNext()){
				PairData data = (PairData) itr.next();
				context.write(key, data);
				i++;
			}
		}
	}
	
	public static class Partitioner4 extends Partitioner<FinalData, PairData> {
		
		@Override
	    public int getPartition(FinalData key, PairData value, int numPartitions) {
			int reducerNumber = (value.decade.get() % 100)/10;
			if (value.decade.get() == 2000 || value.decade.get() == 2010){
				reducerNumber = reducerNumber + 10;
			}
			return reducerNumber;
		}
	}
	
	public static class NaturalKeyGroupingComparator extends WritableComparator {

		protected NaturalKeyGroupingComparator() {
			super(FinalData.class, true);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public int compare(WritableComparable d1, WritableComparable d2) {
			FinalData data1 = (FinalData)d1;
			FinalData data2 = (FinalData)d2;
			
			return data1.measure.get() - data2.measure.get();
		}
	}
	
		
	public static class CompositeKeyComparator extends WritableComparator {
	
		protected CompositeKeyComparator() {
			super(FinalData.class, true);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public int compare(WritableComparable d1, WritableComparable d2) {
			FinalData data1 = (FinalData)d1;
			FinalData data2 = (FinalData)d2;
			
			int result = data1.measure.get() - data2.measure.get();
			if(result == 0) {
				double temp = data2.num.get() - data1.num.get();
				if (temp < 0){
					result = -1;
				} else if (temp > 0){
					result = 1;
				} else {
					result = 0;
				}
			}
			return result;
		}
	}
	
	public static void main(String[] args) throws Exception {
	    Configuration conf = new Configuration();
	    conf.set("k", args[2]);
	    Job job = new Job(conf,"Step4");
	    job.setJarByClass(Main4.class);
	    job.setGroupingComparatorClass(NaturalKeyGroupingComparator.class);
		job.setSortComparatorClass(CompositeKeyComparator.class);
	    job.setMapperClass(Mapper4.class);
	    job.setPartitionerClass(Partitioner4.class);
	    job.setReducerClass(Reducer4.class);
	    job.setNumReduceTasks(12);
	    job.setOutputKeyClass(FinalData.class);
	    job.setOutputValueClass(PairData.class);
	    job.setOutputFormatClass(TextOutputFormat.class);
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
