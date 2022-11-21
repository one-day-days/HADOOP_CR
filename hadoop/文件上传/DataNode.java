DataNode.java
	main()
		secureMain(args, null);
			createDataNode(args, null, resources);
				//创建DataNode dn
				instantiateDataNode(args, conf, resources);
					makeInstance(dataLocations, conf, resources)
						new DataNode(conf, locations, storageLocationChecker, resources);
							startDataNode(dataDirs, resources);
								 // 创建数据存储对象
								 storage = new DataStorage();						  
								 // global DN settings
								 registerMXBean();
								 // 初始化DataXceiver
								 initDataXceiver();						  
								 // 启动HttpServer
								 startInfoServer();
								 // 初始化RPC服务
								 initIpcServer();
								 // 创建BlockPoolManager blockPoolManager
								 new BlockPoolManager(this);
								 // 心跳管理 BlockPoolManager
								 blockPoolManager.refreshNamenodes(getConf());
									doRefreshNamenodes(newAddressMap, newLifelineAddressMap);
										 //根据NameNode个数创建成正比
										 bpos = createBPOS(nsToAdd, addrs, lifelineAddrs);
											//创建BPOfferService并加入list
											new BPOfferService(nameserviceId, nnAddrs, lifelineNnAddrs, dn);
										 startAll();
											//启动所有bpos
											//BPOfferService
											bpos.start();
												//BPServiceActor
												actor.start()
													// 表示开启一个线程，所有查找该线程的run方法
													bpThread.start();
														 connectToNNAndHandshake();
															// get NN proxy 获取NN的RPC客户端对象 bpNamenode
															dn.connectToNN(nnAddr);
																new DatanodeProtocolClientSideTranslatorPB(nnAddr, getConf())
																	createNamenode(nameNodeAddr, conf, ugi);
																		RPC.getProxy
															// 注册
															register(nsInfo);
															// 创建注册信息newBpRegistration
															bpos.createRegistration();
																bpNamenode.registerDatanode(newBpRegistration);
													offerService();
														// 发送心跳信息
														sendHeartBeat();
															bpNamenode.sendHeartbeat(...)
								 
				dn.runDatanodeDaemon();
				

NameNodeRpcServer.java
	sendHeartbeat(...)
		// 处理DN发送的心跳
		namesystem.handleHeartbeat(.....)
			//DatanodeManager.java
			blockManager.getDatanodeManager().handleHeartbeat()
				//HeartbeatManager.java
				 heartbeatManager.updateHeartbeat()
					//BlockManager
					blockManager.updateHeartbeat()
					//DatanodeDescriptor.java
					node.updateHeartbeat()
			new HeartbeatResponse(cmds, haState, rollingUpgradeInfo,blockReportLeaseId);
	registerDatanode(DatanodeRegistration nodeReg)
		// 注册DN FSNamesystem
		namesystem.registerDatanode(nodeReg);
			//BlockManager
			blockManager.registerDatanode
				//DataNodeBlockManager
				datanodeManager.registerDatanode(nodeReg);
					 // register new datanode
					 addDatanode(nodeDescr);
					 // 将DN添加到心跳管理
					 heartbeatManager.addDatanode(nodeDescr);
						  networktopology.add(node); // may throw InvalidTopologyException
						  host2DatanodeMap.add(node);
						  checkIfClusterIsNowMultiRack(node);
				