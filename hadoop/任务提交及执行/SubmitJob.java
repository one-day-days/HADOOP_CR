//Job.java
job.waitForCompletion(true);
	//JobSubmitter.java
	submit()；
		//再次检查确保作业状态为define
		ensureState(JobState.DEFINE);
		//处理新旧API
		setUseNewAPI();
		//创建连接
		connect();
			//ClientProtocolProvider 客户端通信协议提供者  YarnClientProtocolProvider、LocalClientProtocolProvider
			//ClientProtocol client  客户端通信协议 YARNRunner LocalJobRunner
			new Cluster();

		//JobSubmitter.java 
		submitter.submitJobInternal(Job.this, cluster);
			//validate the jobs output specs 检查作业的输出规范的有效性
			//aw:比如检查输出路径是否配置并且是否存在。正确情况是已经配置且不存在
			checkSpecs(job);
			//1)获取作业准备区路径，用于作业及相关资源的提交存放,比如：jar、切片信息、配置信息等
			//默认是/tmp/hadoop-yarn/staging/提交作业用户名/.staging
			Path jobStagingArea = JobSubmissionFiles.getStagingDir(cluster, conf);
			//2）获取jobid ，并创建Job路径
			//创建最终作业准备区路径,jobStagingArea后接/jobID
			JobID jobId = submitClient.getNewJobID();
			Path submitJobDir = new Path(jobStagingArea, jobId.toString());			
			//3）拷贝jar包到集群
			copyAndConfigureFiles(job, submitJobDir);
				//FlieSystem	
				rUploader.uploadFiles(job, jobSubmitDir);
			//创建文件job.xml 用于保存作业的配置信息
			Path submitJobFile = JobSubmissionFiles.getJobConfPath(submitJobDir);
			//指定任务队列
			String queue = conf.get(MRJobConfig.QUEUE_NAME,JobConf.DEFAULT_QUEUE_NAME);
			//4）计算切片，生成切片规划文件
			writeSplits(job, submitJobDir);
			
				writeOldSplits(JobConf job, Path jobSubmitDir);
					job.getInputFormat().getSplits(job, job.getNumMapTasks());
						//文件总大小totalsize;
						long goalSize = totalSize / (numSplits == 0 ? 1 : numSplits);
						//mapreduce.input.fileinputformat.split.minsize=0 minSplitSize=1
						long minSize = Math.max(job.getLong(org.apache.hadoop.mapreduce.lib.input.FileInputFormat.SPLIT_MINSIZE, 1), minSplitSize);
						//128mb
						long blockSize = file.getBlockSize();
						//Math.max(minSize, Math.min(goalSize, blockSize))
						long splitSize = computeSplitSize(goalSize, minSize, blockSize);
				
				writeNewSplits();
					input.getSplits(job);
						// 1 1
						long minSize = Math.max(getFormatMinSplitSize(), getMinSplitSize(job));
						//Long.MAX_VALUE
						long maxSize = getMaxSplitSize(job);
						//128
						long blockSize = file.getBlockSize();
						Math.max(minSize, Math.min(maxSize, blockSize));
						long splitSize = computeSplitSize(blockSize, minSize, maxSize);
					
								
			input.getSplits(job);
			//5）向Stag路径写XML配置文件
			writeConf(conf, submitJobFile);
			conf.writeXml(out);
			// 6）提交Job,返回提交状态
			//YARNRunner.java
			submitClient.submitJob(jobId, submitJobDir.toString(), job.getCredentials());
				//创建提交环境
				createApplicationSubmissionContext(conf, jobSubmitDir, ts);
					  // 封装了本地资源相关路径 localResources
					  setupLocalResources(jobConf, jobSubmitDir);
					  
					  // 封装了启动mrappMaster和运行container的命令
					  setupAMCommand(jobConf);
							//启动指令
							vargs.add(MRApps.crossPlatformifyMREnv(jobConf, Environment.JAVA_HOME)+ "/bin/java");
							// org.apache.hadoop.mapreduce.v2.app.MRAppMaster	
							vargs.add(MRJobConfig.APPLICATION_MASTER_CLASS);
					  
				// 向RM提交一个应用程序，appContext里面封装了启动mrappMaster和运行container的命令 applicationId
				//ResourceMgrDelegare负责与Yarn集群进行通信，完成诸如作业提交、作业状态查询等过程 
				resMgrDelegate.submitApplication(appContext);
					YarnClientImpl.java
					// 创建一个提交请求
					Records.newRecord(SubmitApplicationRequest.class);
					// 继续提交，实现类是ApplicationClientProtocolPBClientImpl
					rmClient.submitApplication(request);
					// 如果提交失败，则再次提交
					rmClient.submitApplication(request);
						//ClientRMService.java
						//获取提交会话上下文
						request.getApplicationSubmissionContext();
						//提交任务
						rmAppManager.submitApplication(submissionContext,System.currentTimeMillis(), user);
				
				// 获取提交响应 appMaster
				resMgrDelegate.getApplicationReport(applicationId);
		JobState.RUNNING;
	//aw:随着进度和任务的进行，实时监视作业和打印状态
	monitorAndPrintJob();
	//设定时间轮询任务状态