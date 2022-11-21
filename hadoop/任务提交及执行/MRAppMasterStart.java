MRAppMaster.java
	// 初始化一个container
	ContainerId.fromString(containerIdStr);
	// 创建appMaster对象
	new MRAppMaster(applicationAttemptId, containerId, nodeHostString,
            Integer.parseInt(nodePortString),
            Integer.parseInt(nodeHttpPortString), appSubmitTime);
	// 初始化并启动AppMaster
    initAndStartAppMaster(appMaster, conf, jobUserName);
		run()
			// 初始化
		    appMaster.init(conf);
				// 调用MRAppMaster中的serviceInit()方法
				serviceInit(config);
					//创建分配器
					dispatcher = createDispatcher();
					// 创建提交路径
					clientService = createClientService(context);
					// 创建调度器
					clientService.init(conf);
					// 创建job提交RPC客户端
					containerAllocator = createContainerAllocator(clientService, context);
					containerLauncher = createContainerLauncher(context);
				notifyListeners();		  		  
		    // 启动appMaster
		    appMaster.start();
				// 调用MRAppMaster中的serviceStart()方法
				serviceStart();
					// 调用createJob()方法创建作业Job实例job   
  					job = createJob(getConfig(), forcedState, shutDownMessage);
  						Job newJob =  new JobImpl(jobId, appAttemptID, conf, dispatcher.getEventHandler(), ......);  
  						// 将新创建的作业newJob的jobId与其自身的映射关系存储到应用运行上下文信息context中的jobs集合中  
  						((RunningAppContext) context).jobs.put(newJob.getID(), newJob); 
  						// 异步事件分发器dispatcher注册作业完成事件JobFinishEvent对应的事件处理器，通过createJobFinishEventHandler()方法获得  
  						dispatcher.register(JobFinishEvent.Type.class, createJobFinishEventHandler());       
						 // 返回新创建的作业newJob  
						 return newJob; 
  					// 创建一个Job初始化事件initJobEvent  
    				JobEvent initJobEvent = new JobEvent(job.getID(), JobEventType.JOB_INIT);
    				// 调用jobEventDispatcher的handle()方法，处理Job初始化事件initJobEvent，即将Job初始化事件交由事件分发器jobEventDispatcher处理，  
    				jobEventDispatcher.handle(initJobEvent);
    					((EventHandler<JobEvent>)context.getJob(event.getJobId())).handle(event);
	    					//jobImp.handle 
	    					getStateMachine().doTransition(event.getType(), event);
	    						Initransition.transition(JobImpl job, JobEvent event)

    				 // 调用父类的serviceStart()，启动所有组件  
  					super.serviceStart(); 
  					// 如果作业初始化失败，构造作业初始化失败JOB_INIT_FAILED事件，并交由事件分发器jobEventDispatcher处理  
				    JobEvent initFailedEvent = new JobEvent(job.getID(), JobEventType.JOB_INIT_FAILED);  
				    jobEventDispatcher.handle(initFailedEvent);
					// 初始化成功后，提交Job到队列中

					startJobs();	
// 这里将job存放到yarn队列
dispatcher.getEventHandler().handle(startJobEvent);
	StartTransition.transition(JobImpl job, JobEvent event);
		// 设置作业的起始时间startTime
		job.startTime
		 // 创建作业已初始化事件JobInitedEvent实例jie
	  	JobInitedEvent jie = new JobInitedEvent(job.oldJobId,job.startTime,job.numMapTasks, job.numReduceTasks,
	         job.getState().toString(),job.isUber());      
	  	// 将作业已初始化事件JobInitedEvent实例jie包装成作业历史事件JobHistoryEvent，并交给作业的事件处理器eventHandler处理
	  	job.eventHandler.handle(new JobHistoryEvent(job.jobId, jie));						      
	 	// 创建作业信息变更事件JobInfoChangeEvent实例jice
	  	JobInfoChangeEvent jice = new JobInfoChangeEvent(job.oldJobId,job.appSubmitTime, job.startTime);						      
	  	// 将作业信息变更事件JobInfoChangeEvent实例jice包装成作业历史事件JobHistoryEvent，并交给作业的事件处理器eventHandler处理
	  	job.eventHandler.handle(new JobHistoryEvent(job.jobId, jice));						      
	  	// 调用作业度量指标metrics的runningJob()方法，标识作业已开始运行
	  	job.metrics.runningJob(job);						 
	  	// 构造提交作业Setup事件CommitterJobSetupEvent，并交由作业的事件处理器eventHandler处理
	  	job.eventHandler.handle(new CommitterJobSetupEvent(
	          job.jobId, job.jobContext));
	  		createCommitterEventHandler(context, committer);
	  			serviceStart();
	  				eventHandlingThread.start();
	  					launcherPool.execute(new EventProcessor(event));
	  						//EventProcessor.run()
			      			case JOB_SETUP:
						        handleJobSetup((CommitterJobSetupEvent) event);
						        	//创建作业中所有任务工作的临时根目录。
						        	committer.setupJob(event.getJobContext());
									context.getEventHandler().handle(new JobSetupCompletedEvent(event.getJobID()));
										SetupCompletedTransition.transition();
							      			// 通过设置作业的setupProgress为1，标记作业setup已完成
									      	job.setupProgress = 1.0f;
									      
									      	// 调度作业的Map Task
									      	job.scheduleTasks(job.mapTasks, job.numReduceTasks == 0);
									      	// 调度作业的Reduce Task
									      	job.scheduleTasks(job.reduceTasks, true);
									      		// 遍历传入的任务集合taskIDs中的每个TaskId
									      		// 根据taskID从集合completedTasksFromPreviousRun中移除对应元素，并获取被移除的元素TaskInfo实例taskInfo
													TaskInfo taskInfo = completedTasksFromPreviousRun.remove(taskID);
													// 构造T_RECOVER类型任务恢复事件TaskRecoverEvent，交给eventHandler处理，标志位recoverTaskOutput表示是否恢复任务的输出，
										    	// 对于Map-Only型Map任务和所有的Reduce任务，都需要恢复，标志位recoverTaskOutput为true
										        eventHandler.handle(new TaskRecoverEvent(taskID, taskInfo,
										            committer, recoverTaskOutput));

										        // 否则，构造T_SCHEDULE类型任务调度事件TaskEvent，交给eventHandler处理
												eventHandler.handle(new TaskEvent(taskID, TaskEventType.T_SCHEDULE));
													InitialScheduleTransition.transition();
														// 添加并调度任务运行尝试TaskAttempt，Avataar.VIRGIN表示它是第一个Attempt，
												      	// 而剩余的Avataar.SPECULATIVE表示它是为拖后腿任务开启的一个Attempt，即推测执行原理
												      	task.addAndScheduleAttempt(Avataar.VIRGIN);
															// 调用addAttempt()方法，创建一个任务运行尝试TaskAttempt实例attempt，
															// 并将其添加到attempt集合attempts中，还会设置attempt的Avataar属性
														    TaskAttempt attempt = addAttempt(avataar);	
														    														    
														    // 将attempt的id添加到正在执行的attempt集合inProgressAttempts中
														    inProgressAttempts.add(attempt.getID());														    
														    //schedule the nextAttemptNumber
														    // 调度TaskAttempt														    
														    // 如果集合failedAttempts大小大于0，说明该Task之前有TaskAttempt失败过，此次为重新调度，
														    // TaskAttemp事件类型为TA_RESCHEDULE，
														    if (failedAttempts.size() > 0) {
														      eventHandler.handle(new TaskAttemptEvent(attempt.getID(),
														          TaskAttemptEventType.TA_RESCHEDULE));
														    } else {
														      // 否则为TaskAttemp事件类型为TA_SCHEDULE
														      eventHandler.handle(new TaskAttemptEvent(attempt.getID(),
														          TaskAttemptEventType.TA_SCHEDULE));
														    }
												      	// 设置任务的调度时间scheduledTime为当前时间
												      	task.scheduledTime = task.clock.getTime();
												     	// 发送任务启动事件
												     	task.sendTaskStartedEvent();



									      	// If we have no tasks, just transition to job completed
									      	// 如果没有task了，则生成JOB_COMPLETED事件并交由作业的事件处理器eventHandler进行处理
									      	if (job.numReduceTasks == 0 && job.numMapTasks == 0) {
									        	job.eventHandler.handle(new JobEvent(job.jobId,
									            JobEventType.JOB_COMPLETED));
										        break;




public JobStateInternal transition(JobImpl job, JobEvent event) {  
        
      // 调用作业度量指标体系metrics的submittedJob()方法，提交作业  
      job.metrics.submittedJob(job);  
        
      // 调用作业度量指标体系metrics的preparingJob()方法，开始作业准备  
      job.metrics.preparingJob(job);  
  
      // 新旧API创建不同的作业上下文JobContextImpl实例  
      if (job.newApiCommitter) {  
        job.jobContext = new JobContextImpl(job.conf,  
            job.oldJobId);  
      } else {  
        job.jobContext = new org.apache.hadoop.mapred.JobContextImpl(  
            job.conf, job.oldJobId);  
      }  
        
      try {  
            
        // 调用setup()方法，完成作业启动前的部分初始化工作  
        setup(job);  
          
        // 设置作业job对应的文件系统fs  
        job.fs = job.getFileSystem(job.conf);  
  
        //log to job history  
        // 创建作业已提交事件JobSubmittedEvent实例jse  
        JobSubmittedEvent jse = new JobSubmittedEvent(job.oldJobId,  
              job.conf.get(MRJobConfig.JOB_NAME, "test"),   
            job.conf.get(MRJobConfig.USER_NAME, "mapred"),  
            job.appSubmitTime,  
            job.remoteJobConfFile.toString(),  
            job.jobACLs, job.queueName,  
            job.conf.get(MRJobConfig.WORKFLOW_ID, ""),  
            job.conf.get(MRJobConfig.WORKFLOW_NAME, ""),  
            job.conf.get(MRJobConfig.WORKFLOW_NODE_NAME, ""),  
            getWorkflowAdjacencies(job.conf),  
            job.conf.get(MRJobConfig.WORKFLOW_TAGS, ""));  
          
        // 将作业已提交事件JobSubmittedEvent实例jse封装成作业历史事件JobHistoryEvent交由作业的时事件处理器eventHandler处理  
        job.eventHandler.handle(new JobHistoryEvent(job.jobId, jse));  
        //TODO JH Verify jobACLs, UserName via UGI?  
  
        // 调用createSplits()方法，创建分片，并获取任务分片元数据信息TaskSplitMetaInfo数组taskSplitMetaInfo  
        TaskSplitMetaInfo[] taskSplitMetaInfo = createSplits(job, job.jobId);  
          
        // 确定Map Task数目numMapTasks：分片元数据信息数组的长度，即有多少分片就有多少numMapTasks  
        job.numMapTasks = taskSplitMetaInfo.length;  
        // 确定Reduce Task数目numReduceTasks，取作业参数mapreduce.job.reduces，参数未配置默认为0  
        job.numReduceTasks = job.conf.getInt(MRJobConfig.NUM_REDUCES, 0);  
  
        // 确定作业的map和reduce权重mapWeight、reduceWeight  
        if (job.numMapTasks == 0 && job.numReduceTasks == 0) {  
          job.addDiagnostic("No of maps and reduces are 0 " + job.jobId);  
        } else if (job.numMapTasks == 0) {  
          job.reduceWeight = 0.9f;  
        } else if (job.numReduceTasks == 0) {  
          job.mapWeight = 0.9f;  
        } else {  
          job.mapWeight = job.reduceWeight = 0.45f;  
        }  
  
        checkTaskLimits();  
  
        // 根据分片元数据信息计算输入长度inputLength，也就是作业大小  
        long inputLength = 0;  
        for (int i = 0; i < job.numMapTasks; ++i) {  
          inputLength += taskSplitMetaInfo[i].getInputDataLength();  
        }  
  
        // 根据作业大小inputLength，调用作业的makeUberDecision()方法，决定作业运行模式是Uber模式还是Non-Uber模式  
        job.makeUberDecision(inputLength);  
          
        // 根据作业的Map、Reduce任务数目之和，外加10，  
        // 初始化任务尝试完成事件TaskAttemptCompletionEvent列表taskAttemptCompletionEvents  
        job.taskAttemptCompletionEvents =  
            new ArrayList<TaskAttemptCompletionEvent>(  
                job.numMapTasks + job.numReduceTasks + 10);  
          
        // 根据作业的Map任务数目，外加10，  
        // 初始化Map任务尝试完成事件TaskCompletionEvent列表mapAttemptCompletionEvents  
        job.mapAttemptCompletionEvents =  
            new ArrayList<TaskCompletionEvent>(job.numMapTasks + 10);  
          
        // 根据作业的Map、Reduce任务数目之和，外加10，  
        // 初始化列表taskCompletionIdxToMapCompletionIdx  
        job.taskCompletionIdxToMapCompletionIdx = new ArrayList<Integer>(  
            job.numMapTasks + job.numReduceTasks + 10);  
  
        // 确定允许Map、Reduce任务失败百分比，  
        // 取参数mapreduce.map.failures.maxpercent、mapreduce.reduce.failures.maxpercent，  
        // 参数未配置均默认为0，即不允许Map和Reduce任务失败  
        job.allowedMapFailuresPercent =  
            job.conf.getInt(MRJobConfig.MAP_FAILURES_MAX_PERCENT, 0);  
        job.allowedReduceFailuresPercent =  
            job.conf.getInt(MRJobConfig.REDUCE_FAILURES_MAXPERCENT, 0);  

        job.addDiagnostic("Job init failed : "  
            + StringUtils.stringifyException(e));  
        // Leave job in the NEW state. The MR AM will detect that the state is  
        // not INITED and send a JOB_INIT_FAILED event.  
          
        // 返回作业内部状态，JobStateInternal.NEW，即初始化失败后的新建  
        return JobStateInternal.NEW;  
      }  
    }  
1、调用setup()方法，完成作业启动前的部分初始化工作，实际上最重要的两件事就是：
	1.1、获取并设置作业远程提交路径remoteJobSubmitDir；
    1.2、获取并设置作业远程配置文件remoteJobConfFile；
2、调用createSplits()方法，创建分片，并获取任务分片元数据信息TaskSplitMetaInfo数组taskSplitMetaInfo：
	通过SplitMetaInfoReader的静态方法readSplitMetaInfo()，从作业远程提交路径remoteJobSubmitDir中读取作业分片元数据信息，也就是每个任务的分片元数据信息，以此确定Map任务数、作业运行方式等一些列后续内容；
3、确定Map Task数目numMapTasks：分片元数据信息数组的长度，即有多少分片就有多少numMapTasks；
4、确定Reduce Task数目numReduceTasks，取作业参数mapreduce.job.reduces，参数未配置默认为0；
5、根据分片元数据信息计算输入长度inputLength，也就是作业大小；
6、根据作业大小inputLength，调用作业的makeUberDecision()方法，决定作业运行模式是Uber模式还是Non-Uber模式：
    小作业会通过Uber模式运行，相反，大作业会通过Non-Uber模式运行，可参见《Yarn源码分析之MRAppMaster：作业运行方式Local、Uber、Non-Uber》一文！
7、确定允许Map、Reduce任务失败百分比，取参数mapreduce.map.failures.maxpercent、mapreduce.reduce.failures.maxpercent，参数未配置均默认为0，即不允许Map和Reduce任务失败；
8、创建Map Task；
9、创建Reduce Task；
10、返回作业内部状态，JobStateInternal.INITED，即已经初始化；
11、如果出现异常：
11.1、记录warn级别日志信息:Job init failed，并打印出具体异常；
11.2、返回作业内部状态，JobStateInternal.NEW，即初始化失败后的新建；