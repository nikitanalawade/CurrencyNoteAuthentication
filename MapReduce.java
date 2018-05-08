
 
import java.io.IOException;
//import java.util.Iterator;
import java.lang.String;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
//import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
//import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
//import org.apache.hadoop.mapreduce.ReduceContext;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
public class MapReduce
{
 public static void main(String[] args) throws Exception
 {
   Configuration conf = new Configuration();
   FileSystem fs = FileSystem.get(conf);
   fs.delete(new Path("/user/output"), true);
   fs.copyFromLocalFile(new Path("/home/nikita/Desktop/countresult.txt"), 
   new Path("/user/resultcount"));
 
   Job job = Job.getInstance(conf,"MapReduce");
   
   job.setJobName("MapReduce");
   job.setJarByClass(MapReduce.class);
 
   job.setMapperClass(MapReduceMapper.class);
  job.setReducerClass(MapReduceReducer.class);
   
   job.setMapOutputKeyClass(Text.class);
   job.setMapOutputValueClass(IntWritable.class);
 
   job.setOutputKeyClass(Text.class);
   job.setOutputValueClass(Text.class);
   job.setInputFormatClass(TextInputFormat.class);
   job.setOutputFormatClass(TextOutputFormat.class);
 
   FileInputFormat.addInputPath(job, new Path("/user/resultcount"));
   FileOutputFormat.setOutputPath(job, new Path("/user/output"));
 
   System.exit(job.waitForCompletion(true) ? 0:1);

 }
}
class MapReduceMapper extends Mapper<LongWritable, Text, Text, IntWritable>
{
 
  public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
  {
	  float values[] = new float[10000];
	  String word[] ; int maxValue = 0;
	  float percentage[]=new float[10000];
	  int flagarray[]= new int[10];
	  String textarray[]=new String[20];
	  String words = value.toString();
	  word = words.split(",");
	  for (int i = 0; i < 3; i++)
	  {
	 	values[i] = Integer.parseInt(word[i]);
	  }	
	    if(values[0]==1)
	    {
		  int lowthreshold = 80;
		  int highthreshold = 150;
		  if(values[1] <= highthreshold && values[1] >= lowthreshold)
		  	{
			  flagarray[1]=1;
			  maxValue = flagarray[1];
			  textarray[1]="unique identification number";
		  	}
		  else
		  	{
			  flagarray[1]=0;
			  maxValue = flagarray[1];
			  textarray[1]="unique identification number";
		  	}
		  String text = textarray[1];
		  context.write(new Text(text), new IntWritable(maxValue));
	    }
	  
	    if(values[0]==2)
	    {
	      int lowthreshold =70 ;
	      int highthreshold =90 ;
	      percentage[1]=(values[1]/values[2])*100;
	      if(percentage[1]>=lowthreshold && percentage[1]<=highthreshold)
	      	{
	    	  flagarray[1]=1;
	    	  maxValue = flagarray[1];
	    	  textarray[1]="see through register";
	      	}
	      else
	      { 
	    	  flagarray[1]=0;
	    	  maxValue = flagarray[1];
	    	  textarray[1]="see through register";
	      }
	      String text = textarray[1];
	 	  context.write(new Text(text), new IntWritable(maxValue));
	    }
	 
	    if(values[0]==3)
	    {
	      int threshold = 2000;
	      if(values[1]>=threshold)
	      	{
	    	  flagarray[1]=1;
	    	  maxValue = flagarray[1];
	    	  textarray[1]="latent image";
	      	}
	      else
	      { 
	    	  flagarray[1]=0;
	    	  maxValue = flagarray[1];
	    	  textarray[1]="latent image";
	      }
	      String text = textarray[1];
	 	  context.write(new Text(text), new IntWritable(maxValue));
	    }
	  
	    if(values[0]==4)
	    {
	      int threshold = 1500000;
	      if(values[1]>=threshold )
	      	{
	    	  flagarray[1]=1;
	    	  maxValue = flagarray[1];
	    	  textarray[1]="optically variable ink";
	      	}
	      else
	      { 
	    	  flagarray[1]=0;
	    	  maxValue = flagarray[1];
	    	  textarray[1]="optically variable ink";
	      }
	      String text = textarray[1];
	 	  context.write(new Text(text), new IntWritable(maxValue));
	    }
 }
}




class MapReduceReducer extends Reducer<Text, IntWritable, IntWritable,  Text>
{
 
  public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
  {
   
    int c;
    for(IntWritable flagarray :values )
    {
    
        c= flagarray.get();
      
        if(c==1)
        {	
        String text = "The "+key+" of note is real";
	 	context.write(new IntWritable(c),new Text(text));
	 	
        }
        else
        {
        String text = "The "+key+" of note is fake";
	 	 context.write(new IntWritable(c),new Text(text));
	 	 
        }
        
    
    }
 
	   
  
  }   
  }
