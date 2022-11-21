ReduceTask.java
	//TaskUmbilicalProtocol 将MapTask和ReduceTask处理结果反馈给MRAppMaster
	run(JobConf job, final TaskUmbilicalProtocol umbilical)
		copyPhase = getProgress().addPhase("copy");
		sortPhase  = getProgress().addPhase("sort");
		reducePhase = getProgress().addPhase("reduce");
		//初始化Output
		initialize(job, getJobID(), reporter, useNewApi);
			outputFormat = ReflectionUtils.newInstance(taskContext.getOutputFormatClass(), job);
			committer = outputFormat.getOutputCommitter(taskContext);
		//reducer阶段负责拉取数据,和执行合并的事情,都是由ShuffleConsumerPlugin 这个组件来完成的
		ShuffleConsumerPlugin shuffleConsumerPlugin = null;
		shuffleConsumerPlugin.init(shuffleContext);
			//ShuffleSchedulerImpl 负责拉取数据的
			scheduler = new ShuffleSchedulerImpl<K, V>(jobConf, taskStatus, reduceId,this, copyPhase, context.getShuffledMapsCounter(),context.getReduceShuffleBytes(), context.getFailedShuffleCounter());		
			//负责执行合并的
			merger = createMergeManager(context);
				this.inMemoryMerger = createInMemoryMerger(); //内存合并
				this.onDiskMerger = new OnDiskMerger(this); //磁盘合并
		//拉取数据和合并操作
		rIter = shuffleConsumerPlugin.run();
			//拉取事件
			eventFetcher.start();
			//fetcher个数
			final int numFetchers = isLocal ? 1 : jobConf.getInt(MRJobConfig.SHUFFLE_PARALLEL_COPIES, 5);
			Fetcher<K,V>[] fetchers = new Fetcher[numFetchers];
			fetchers.start;
			eventFetcher.close;
			fetchers.shutDown();
			scheduler.shutDown();
			copyPhase.complete(); // copy 结束
			taskStatus.setPhase(TaskStatus.Phase.SORT); //sort阶段
			reduceTask.statusUpdate(umbilical);
		
		sortPhase.complete();
		setPhase(TaskStatus.Phase.REDUCE);
		runNewReducer(job,job, umbilical, reporter, rIter, comparator, 
					  keyClass, valueClass)
			reducer.run(reducerContext);