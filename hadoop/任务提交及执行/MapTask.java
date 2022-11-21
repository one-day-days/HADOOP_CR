MapTask.java
	initialize(job, getJobID(), reporter, useNewApi);
		outputFormat = ReflectionUtils.newInstance(taskContext.getOutputFormatClass(), job);
		committer = outputFormat.getOutputCommitter(taskContext);

	runNewMapper(job, splitMetaInfo, umbilical, reporter);
		//创建任务上下文
		taskContext = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(job, 
                                                                getTaskID(),
                                                                reporter);
		//TODO 通过反射的形式来进行构建的 mapper
		mapper = ReflectionUtils.newInstance(taskContext.getMapperClass(), job);
		
		//TODO 构建一个输入组件, 默认是TextInputFormat.class 
        inputFormat = ReflectionUtils.newInstance(taskContext.getInputFormatClass(), job);
		
		//TODO 每个 逻辑切片启动一个MapTask
		org.apache.hadoop.mapreduce.InputSplit split = null;
		split = getSplitDetails(new Path(splitIndex.getSplitLocation()),splitIndex.getStartOffset());
		input = new NewTrackingRecordReader<INKEY,INVALUE>(split, inputFormat, reporter, taskContext);
		//TODO RecordWriter 负责数据写出
		org.apache.hadoop.mapreduce.RecordWriter output = null;
		
		if(job.getNumReduceTasks() == 0) {
			output = new NewDirectOutputCollector(taskContext, job, umbilical, reporter);
		  } else {
			 *  context.write()
			 *  output.collect()
			output = new NewOutputCollector(taskContext, job, umbilical, reporter);}
				collector = createSortingCollector(job, reporter);
				//获取分区器
					//TODO MapOutputBuffer 这个就是环形缓冲区
					Class<?>[] collectorClasses = job.getClasses(JobContext.MAP_OUTPUT_COLLECTOR_CLASS_ATTR, MapOutputBuffer.class);
					MapOutputCollector<KEY, VALUE> collector = ReflectionUtils.newInstance(subclazz, job);
					collector.init(context);	  
		//TODO MapContext 初始化 其实就是传播了很多的信息 来进行让其他的组件也能看到
		//mapper.run(context) 中context 具备四个方法,其实就是调用了mapperContext的四个方法
		mapContext = new MapContextImpl<INKEY, INVALUE, OUTKEY, OUTVALUE>(job, getTaskID(),input, output, 
		committer, reporter, split);
		//装饰者设计模式.就是 把 mapContext 包装了一下
		mapperContext = new WrappedMapper<INKEY, INVALUE, OUTKEY, OUTVALUE>().getMapContext(mapContext);
		input.initialize(split, mapperContext);
		
		//TODO map开始执行,其实就是我们自己写的程序的Mapper实例对象
		mapper.run(mapperContext);
			//MapOutPutBuffer.java
			map=>context.write(k,v)=>collector.collect();
				bufferRemaining -= METASIZE;
				if(bufferRemaining <= 0) 中做的事情,就是在对那80%的数据溢写到磁盘
					//
					spilllock.lock
					startSpill();
						spillReady.signal();
						/**
						 * 关于 SplillThread:
						 * 四个重要的组件:
						 * 1. SpillThread sortAndSpill() startSpill()
						 * 2. 可重入锁: ReentrantLock spillLock
						 * 3. 信号变量: Condition spillDone
						 * 4. 信号变量: Condition spillReady
						 *
						 * 再介绍两个重要的线程
						 * 1. 写数据线程
						 * 2. 一个spill线程
						 *
						 * 重要的工作机制
						 * 1. 当 kvbuffer写满百分之80的时候,应该被锁定
						 * 2. 写入数据线程,就应该重新确定 equ
						 */
						SpillThread.run();
							sortAndSpill();
								writer.close();
					spilllock.unlock
			
			
		setPhase(TaskStatus.Phase.SORT);
		statusUpdate(umbilical);
		input.close();
		input = null;
		output.close(mapperContext);
			collector.flush();
				sortAndSpill();
				mergeParts();
			collector.close();
			
		output = null;
		  
		  

		  
		  

		

		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  