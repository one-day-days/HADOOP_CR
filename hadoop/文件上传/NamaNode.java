NameNode.java
	main(...)
		// 创建NameNode
		createNameNode(argv, null);	
			//参数解析
			//创建NameNode对象
			new NameNode(conf)；
				 initialize(getConf());
					// 启动HTTP服务端（9870）
					startHttpServer(conf);
						//创建HttpServer httpServer
						NameNodeHttpServer(conf, this, getHttpServerBindAddress(conf));
							getHttpServerAddress(conf);
								 getHttpAddress(conf);
									int  DFS_NAMENODE_HTTP_PORT_DEFAULT = 9870;
									conf.getTrimmed(DFS_NAMENODE_HTTP_ADDRESS_KEY, DFS_NAMENODE_HTTP_ADDRESS_DEFAULT));								
						httpServer.start();
							// Hadoop自己封装了HttpServer，形成自己的HttpServer2
							HttpServer2.Builder builder = DFSUtil.httpServerTemplateForNNAndJN(conf,
								  httpAddr, httpsAddr, "hdfs",
								  DFSConfigKeys.DFS_NAMENODE_KERBEROS_INTERNAL_SPNEGO_PRINCIPAL_KEY,
								  DFSConfigKeys.DFS_NAMENODE_KEYTAB_FILE_KEY);
	
					// 加载镜像文件和编辑日志到内存
					loadNamesystem(conf);
						FSNamesystem.loadFromDisk(conf);
						//fsImage
						FSNamesystem.getNamespaceDirs(conf),
						FSNamesystem.getNamespaceEditsDirs(conf)
						//namesystem
						new FSNamesystem(conf, fsImage, false);
						namesystem.loadFSImage(startOpt);
					
					// 创建NN的RPC服务端rpcServer
					createRpcServer(conf);
						new NameNodeRpcServer(conf, this);
						
					// NN启动资源检查
					startCommonServices(conf);
						//FSNamesystem
						namesystem.startCommonServices(conf, haContext);
							 //NameNodeResourceChecker.java
							 nnResourceChecker = new NameNodeResourceChecker(conf);
								// dfs.namenode.resource.du.reserved默认值 1024 * 1024 * 100 =》100m
								 duReserved = conf.getLong(DFSConfigKeys.DFS_NAMENODE_DU_RESERVED_KEY,
									  DFSConfigKeys.DFS_NAMENODE_DU_RESERVED_DEFAULT);
								// 对所有路径进行资源检查
								addDirToCheck(editsDirToCheck,
										FSNamesystem.getRequiredNamespaceEditsDirs(conf).contains(
											editsDirToCheck));
											
							 // 检查是否有足够的磁盘存储元数据（fsimage（默认100m） editLog（默认100m））FNNamesystem.java
							 checkAvailableResources();
								// 判断资源是否足够，不够返回false
								nnResourceChecker.hasAvailableDiskSpace();
									//NameNodeResourceChecker.java
									NameNodeResourcePolicy.areResourcesAvailable(volumes.values(),minimumRedundantVolumes);
										// 判断资源是否充足
										isResourceAvailable();
											// 获取当前目录的空间大小
											long availableSpace = df.getAvailable();
											// 如果当前空间大小，小于100m，返回false
											if (availableSpace < duReserved) 	
										
							// 开始进入安全模式
							prog.beginPhase(Phase.SAFEMODE);
							
							// 获取所有可以正常使用的block
							long completeBlocksTotal = getCompleteBlocksTotal(); 
								// 获取正在构建的block
								numUCBlocks = leaseManager.getNumUnderConstructionBlocks();
								// 获取所有的块 - 正在构建的block = 可以正常使用的block
								return getBlocksTotal() - numUCBlocks;
								
							 // 安全模式
							prog.setTotal(Phase.SAFEMODE, STEP_AWAITING_REPORTED_BLOCKS,
							completeBlocksTotal);
							// 启动块服务
							blockManager.activate(conf, completeBlocksTotal);
								//BloclManager.java
								blockManager.activate(...)
									//DatanodeManager.java
										datanodeManager.activate(conf);
								//BloclManagerSafeMode.java
								bmSafeMode.activate(blockTotal);
									// 计算是否满足块个数的阈值
									setBlockTotal(total);
										// 计算阈值：例如：1000个正常的块 * 0.999 = 999
										this.blockThreshold = (long) (total * threshold);
										
									// 判断DataNode节点和块信息是否达到退出安全模式标准
									areThresholdsMet
										// 已经正常注册的块数 》= 块的最小阈值 》=最小可用DataNode
										return blockSafe >= blockThreshold && datanodeNum >= datanodeThreshold
									 

//NN对心跳超时判断					
DatanodeManager.java
		heartbeatRecheckInterval = conf.getInt(
			DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY, 
			DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_DEFAULT); // 5 minutes
			
		heartbeatIntervalSeconds = conf.getTimeDuration(
			DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY,
			DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_DEFAULT, TimeUnit.SECONDS);
		DFS_HEARTBEAT_INTERVAL_DEFAULT = 3;
	//10分钟 + 30秒
	this.heartbeatExpireInterval = 2 * heartbeatRecheckInterval + 10 * 1000 * heartbeatIntervalSeconds;
	activate(...)
		heartbeatManager.activate();
			heartbeatThread.start();
				run()
					// 心跳检查
					heartbeatCheck();
					
						// 判断DN节点是否挂断
						dm.isDatanodeDead(d)
							
							return (node.getLastUpdateMonotonic() < (monotonicNow() - heartbeatExpireInterval));
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					