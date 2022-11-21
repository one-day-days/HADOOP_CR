YarnChild.java
	//main()
	// Create a final reference to the task for the doAs block
    final Task taskFinal = task;
	// 调用task执行（maptask或者reducetask）
    taskFinal.run(job, umbilical); // run the task
		// 如果reducetask个数为零，maptask占用整个任务的100%
		getProgress().addPhase("map", 1.0f);
		// 如果reduceTask个数不为零，MapTask占用整个任务的66.7% sort阶段占比
		getProgress().addPhase("map", 0.667f);
        getProgress().addPhase("sort", 0.333f);
		//调用新的API执行maptask
		runNewMapper(job, splitMetaInfo, umbilical, reporter);
			// 运行maptask
			mapper.run(mapperContext);
				map(context.getCurrentKey(), context.getCurrentValue(), context)
		//调用新的API执行maptask
		runNewReducer(job, umbilical, reporter, rIter, comparator, 
                    keyClass, valueClass);
			while (context.nextKey()) {
				reduce(context.getCurrentKey(), context.getValues(), context);
				// If a back up store is used, reset it
				Iterator<VALUEIN> iter = context.getValues().iterator();
				if(iter instanceof ReduceContext.ValueIterator) {
				  ((ReduceContext.ValueIterator<VALUEIN>)iter).resetBackupStore();        
				}
			  }