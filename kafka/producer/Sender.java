Sender.java
run()
	runOnce()
		long currentTimeMs = time.milliseconds();
		// 将准备好的数据发送到服务器端
		
		long pollTimeout = sendProducerData(currentTimeMs);
			sendProducerData(long now)
				// 获取元数据
        		Cluster cluster = metadata.fetch();
				// 1、判断32m缓存是否准备好
				RecordAccumulator.ReadyCheckResult result = this.accumulator.ready(cluster, now);
					ready(...)
							// 如果不是第一次拉取，  且等待时间小于重试时间 默认100ms ,backingOff=true
							boolean backingOff = batch.attempts() > 0 && waitedTimeMs < retryBackoffMs;
							// 如果backingOff是true 取retryBackoffMs； 如果不是第一次拉取取lingerMs，默认0
							long timeToWaitMs = backingOff ? retryBackoffMs : lingerMs;
							// 批次大小满足发送条件
							boolean full = deque.size() > 1 || batch.isFull();
							// 如果超时，也要发送
							boolean expired = waitedTimeMs >= timeToWaitMs;

				// 如果 Leader 信息不知道，是不能发送数据的
				!result.unknownLeaderTopics.isEmpty()
				// 2.发送每个节点数据，进行封装
				Map<Integer, List<ProducerBatch>> batches = this.accumulator.drain(cluster, result.readyNodes, this.maxRequestSize, now);
					// 2、发往同一个 broker 节点的数据，打包为一个请求批次。
					drain(...)
						batches.put(node.id(), ready);
						
				// 3、发送请求
				sendProduceRequests(batches, now);
					// 创建发送请求对象
					ClientRequest clientRequest = client.newClientRequest(nodeId, requestBuilder, now, acks != 0,
		                requestTimeoutMs, callback);
					// 发送请求 NetworkClient
					client.send(clientRequest, now);
	
			// 等待发送响应
			client.poll(pollTimeout, currentTimeMs);
				this.selector.poll()
		
			