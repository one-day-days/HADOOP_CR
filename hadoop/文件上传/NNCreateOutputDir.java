//FileSystem.java 
	//FSDataOutPutStream
	create(...)
		//DistrbutedFlieSystem.java
		dfs.create(getPathName(p), permission,cflags, replication, blockSize, progress, bufferSize,checksumOpt);
			//DFSClient.java 
			create()
				//FSDataOutPutStream
				DFSOutputStream.newStreamForCreate(...);
					//不断重试，确保文件目录创建成功
					//DFSOutputStream.java 将创建请求发送给NameNode
					fsClient.namenode.create(...)
						//NameNodeRpcServer
						//检查Name Node是否启动
						checkNNStartup();
						//HdfsFlisStatus
						namesystem.startFile
							//FSNamesystem
							startFileInt(...)
								FSDirWriteFileOp.startFile
									//FSDirWriteFileOp.java
									//判断路径是否存在
									iip.getLastINode()
									//添加元数据
									addFile(...)								
										fsd.addINode
											//FSDirectory
											// 将数据写入到INode的目录树中
											ddLastINode(existing, child, modes, true);								

					// 创建输出流
					out = new DFSOutputStream(dfsClient, src, stat,	)
						////Directory => File => Block(128M) => packet(64K) => chunk（chunk 512byte + chunksum 4byte）
						computePacketChunkSize(dfsClient.getConf().getWritePacketSize(),bytesPerChecksum);
					out.start
						//DataStreamer extends Thread
						getStreamer().start();
				//开启契约
				beginFileLease(result.getFileId(), result);
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	