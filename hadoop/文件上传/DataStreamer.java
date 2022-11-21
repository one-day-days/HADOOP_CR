DataStreamer
	-run()
	// 如果dataQueue里面没有数据，代码会阻塞在这儿
	dataQueue.wait(timeout);
	//  队列不为空，从队列中取出packet
	one = dataQueue.getFirst(); // regular data packet
	
	// 步骤一：向NameNode 申请block 并建立数据管道
·	setPipeline(nextBlockOutputStream());
		// 向NN获取向哪个DN写数据
		locateFollowingBlock(...)
			DFSOutputStream.addBlock
				//NameNodeRpcServer.java
				dfsClient.namenode.addBlock(src, dfsClient.clientName, prevBlock,excludedNodes, fileId, favoredNodes, allocFlags);
					//FSNamesystrm.java
					namesystem.getAdditionalBlock(src, fileId,clientName, previous, excludedNodes, favoredNodes, addBlockFlags);
						  // 选择块存储位置
						  DatanodeStorageInfo[] targets = FSDirWriteFileOp.chooseTargetForNewBlock(blockManager, src, excludedNodes, favoredNodes, flags, r);
							  bm.chooseTarget4NewBlock
								blockplacement.chooseTarget
									//BlockPlacementPolicyDefault.java
									chooseTarget(...)
										// 获取不可用的DN
										addToExcludedNodes(storage.getDatanodeDescriptor(), excludedNodes);
										// 有数据正在写，避免都写入本地
										if (avoidLocalNode) 
											//根据策略选择节点
											chooseTargetInOrder
		// 创建管道
		success = createBlockOutputStream(nodes, nextStorageTypes, nextStorageIDs,0L, false);
			// 和DN创建socket
			s = createSocketForPipeline(nodes[0], nodes.length, dfsClient);
			// 获取输出流，用于写数据到DN
			OutputStream unbufOut = NetUtils.getOutputStream(s, writeTimeout);
			// 获取输入流，用于读取写数据到DN的结果
			InputStream unbufIn = NetUtils.getInputStream(s, readTimeout);
			//// 发送数据
			Sender(out).writeBlock
		
	// 步骤二：启动ResponseProcessor用来监听packet发送是否成功
	initDataStreaming();
		response = new ResponseProcessor(nodes);
		response.start();
			//从dataQueue把要发送的这个packet移除出去
		   dataQueue.removeFirst();
		   //ackQueue里面添加这个packet
		   ackQueue.addLast(one);
		   //唤醒wait线程
  
	// 步骤三：从dataQueue 把要发送的这个packet 移除出去
	dataQueue.removeFirst();
	// 步骤四：然后往ackQueue 里面添加这个packet
	ackQueue.addLast(one);
	//  将数据写出去
	// out = new DataOutputStream(new BufferedOutputStream(unbufOut,
            DFSUtilClient.getSmallBufferSize(dfsClient.getConfiguration())));
	//blockStream = out;
	one.writeTo(blockStream);
	