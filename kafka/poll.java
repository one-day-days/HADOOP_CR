poll(...)
	// 记录开始拉取消息时间
	this.kafkaConsumerMetrics.recordPollStart(timer.currentTimeMs());
	// 1、消费者或者消费者组的初始化
	updateAssignmentMetadataIfNeeded(timer, false);
		coordinator.poll(timer, waitForJoinGroup);
			// 获取最新元数据
			maybeUpdateSubscriptionMetadata();
			// 如果没有指定分区分配策略  直接抛异常
			if (protocol == null)
			// 3s 发送一次心跳
			pollHeartbeat(timer.currentTimeMs());
			// 保证和 Coordinator 正常通信（寻找服务器端的 coordinator）
			if (coordinatorUnknown() && !ensureCoordinatorReady(timer))
					//ensureCoordinatorReady(......)
					// 如果找到 coordinator，直接返回
					if (!coordinatorUnknown())
					// 如果没有找到，循环给服务器端发送请求，直到找到 coordinator
					do {
					if (fatalFindCoordinatorException != null) {
						final RuntimeException fatalException = fatalFindCoordinatorException;
						fatalFindCoordinatorException = null;
						throw fatalException;
					}
					// 创建寻找 coordinator 的请求
					final RequestFuture<Void> future = lookupCoordinator();
						// 有节点接收查找Coordinator请求
						findCoordinatorFuture = sendFindCoordinatorRequest(node);
							// 创建发送Coordinator 请求数据信息
							FindCoordinatorRequestData data = new FindCoordinatorRequestData()
									.setKeyType(CoordinatorType.GROUP.id())
									.setKey(this.rebalanceConfig.groupId);
							// 进一步封装
							FindCoordinatorRequest.Builder requestBuilder = new FindCoordinatorRequest.Builder(data);
							// 消费者向服务器端发送请求
							return client.send(node, requestBuilder)
									.compose(new FindCoordinatorResponseHandler());
								
					// 发送寻找 coordinator 的请求给服务器端
					client.poll(future, timer);
				}
	
			// 判断是否需要加入消费者组
			if (rejoinNeededOrPending())
			// 是否自动提交 offset
			maybeAutoCommitOffsetsAsync(timer.currentTimeMs());
				
		
	// 2 抓取数据
    final Map<TopicPartition, List<ConsumerRecord<K, V>>> records = pollForFetches(timer);
		// 第一次拉取不到数据
        final Map<TopicPartition, List<ConsumerRecord<K, V>>> records = fetcher.fetchedRecords();
		// 2.1 发送请求并抓取数据
		fetcher.sendFetches();
			// maxWaitMs 默认是500ms
            // minBytes 最少一次抓取1个字节
            // maxBytes 最多一次抓取多少数据  默认50m
            final FetchRequest.Builder request = FetchRequest.Builder
                    .forConsumer(this.maxWaitMs, this.minBytes, data.toSend())
                    .isolationLevel(isolationLevel)
                    .setMaxBytes(this.maxBytes)
                    .metadata(data.metadata())
                    .toForget(data.toForget())
                    .rackId(clientRackId);
			// 发送拉取数据请求
            RequestFuture<ClientResponse> future = client.send(fetchTarget, request);	
            // 监听服务器端返回的数据
            future.addListener(....)
            	// 成功的获取到数据
                FetchResponse response = (FetchResponse) resp.responseBody();	
                // 把数据按照分区，添加到消息队列里面
                completedFetches.add(new CompletedFetch(partition, partitionData,
                        metricAggregator, batches, fetchOffset, responseVersion));
					
		// 2.2 把数据按照分区封装好后，一次处理默认 500 条数据
		return fetcher.fetchedRecords();
			 // 每次处理的最多条数是500条
			 int recordsRemaining = maxPollRecords;
			 // 获取数据
			CompletedFetch records = completedFetches.peek();
			// 如果没有数据了 可以退出循环
			if (records == null) break;
			// 处理数据
            completedFetches.poll();
	
	// 3 拦截器处理数据
    return this.interceptors.onConsume(new ConsumerRecords<>(records));