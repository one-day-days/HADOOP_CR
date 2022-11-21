DataXceiverServer.java
	-run()
	// 接收socket的请求
    peer = peerServer.accept();
	// 客户端每发送一个block，都启动一个DataXceiver去处理block
	DataXceiver.create(peer, datanode, this)).start();
	

DataXceiver
	-run()
	// 读取这次数据的请求类型
    op = readOp();
	// 根据操作类型处理我们的数据
    processOp(op);
		opWriteBlock(in);
			writeBlock(...)
				//创建一个BlockReceiver
				getBlockReceiver(...)
					new BlockReceiver(...)
						//创建通道
						datanode.data.createRbw(...)
							// 创建输出流的临时写文件 
							newReplicaInfo = v.createRbw(b);
								// 有可能有多个临时写文件
								ref = volumes.getNextVolume(storageType, storageId, b.getNumBytes());
								// 创建输出流的临时写文件 
								newReplicaInfo = v.createRbw(b);
									File f = createRbwFile
						
						
				// 向新的副本发送socket
				mirrorSock = datanode.newSocket();
				// 往下游socket发送数据
				new Sender(...)