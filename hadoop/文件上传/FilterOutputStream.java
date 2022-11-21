FilterOutputStream.java	
	write(...)
		FSOutputSummer.java
			write(...);
				flushBuffer(...);
					// 向队列中写数据   
					// Directory => File => Block(128M) => package(64K) => chunk（chunk 512byte + chunksum 4byte）
					writeChecksumChunks(buf, 0, lenToFlush)
						// 计算chunk的校验和
						sum.calculateChunkedSums(b, off, len, checksum, 0);
						// 一个chunk一个chunk的将数据写入队列
						writeChunk(b, off + i, chunkLen, checksum, ckOffset,getChecksumSize());
							//DFSOutputStream.java
							writeChunkImpl(b, offset, len, checksum, ckoff, cklen);
								// 往packet里面写chunk的校验和 4byte
								currentPacket.writeChecksum(checksum, ckoff, cklen);
								// 往packet里面写一个chunk 512 byte
								currentPacket.writeData(b, offset, len);
								// 记录写入packet中的chunk个数，累计到127个chuck，这个packet就满了
								currentPacket.incNumChunks();
								enqueueCurrentPacketFull();
									 enqueueCurrentPacket();
										getStreamer().waitAndQueuePacket(currentPacket);
											dataQueue.wait();
											queuePacket(packet);
												// 向队列中添加数据
												dataQueue.addLast(packet);
												// 通知队列添加数据完成
												dataQueue.notifyAll();