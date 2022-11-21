KafkaProducer.java
	KafkaProducer(ProducerConfig config,
                  Serializer<K> keySerializer,
                  Serializer<V> valueSerializer,
                  ProducerMetadata metadata,
                  KafkaClient kafkaClient,
                  ProducerInterceptors<K, V> interceptors,
                  Time time)
				  
			// 获取事务 id
			String transactionalId = config.getString(ProducerConfig.TRANSACTIONAL_ID_CONFIG);
			// 获取客户端 id
			this.clientId = config.getString(ProducerConfig.CLIENT_ID_CONFIG);
			 // 监控kafka运行情况
            JmxReporter jmxReporter = new JmxReporter()
			 // 获取分区器
            this.partitioner = config.getConfiguredInstance(
                    ProducerConfig.PARTITIONER_CLASS_CONFIG,
                    Partitioner.class,
                    Collections.singletonMap(ProducerConfig.CLIENT_ID_CONFIG, clientId));
			// key和value的序列化
            if (keySerializer == null) {
                this.keySerializer = config.getConfiguredInstance(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                                                                                         Serializer.class);
                this.keySerializer.configure(config.originals(Collections.singletonMap(ProducerConfig.CLIENT_ID_CONFIG, clientId)), true);
            } else {
                config.ignore(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG);
                this.keySerializer = keySerializer;
            }
			
            // 拦截器处理（拦截器可以有多个）
            List<ProducerInterceptor<K, V>> interceptorList = (List) config.getConfiguredInstances(
                    ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
                    ProducerInterceptor.class,
                    Collections.singletonMap(ProducerConfig.CLIENT_ID_CONFIG, clientId));
					
			// 单条日志大小 默认1m
            this.maxRequestSize = config.getInt(ProducerConfig.MAX_REQUEST_SIZE_CONFIG);
            // 缓冲区大小 默认32m
            this.totalMemorySize = config.getLong(ProducerConfig.BUFFER_MEMORY_CONFIG);
            // 压缩，默认是none
            this.compressionType = CompressionType.forName(config.getString(ProducerConfig.COMPRESSION_TYPE_CONFIG));
			 // 缓冲区对象 默认是32m
            // 批次大小 默认16k
            // 压缩方式，默认是none
            // liner.ms 默认是0
            //  内存池
            this.accumulator = new RecordAccumulator(logContext,
                    config.getInt(ProducerConfig.BATCH_SIZE_CONFIG),
                    this.compressionType,
                    lingerMs(config),
                    retryBackoffMs,
                    deliveryTimeoutMs,
                    metrics,
                    PRODUCER_METRIC_GROUP_NAME,
                    time,
                    apiVersions,
                    transactionManager,
                    new BufferPool(this.totalMemorySize, config.getInt(ProducerConfig.BATCH_SIZE_CONFIG), metrics, time, PRODUCER_METRIC_GROUP_NAME));

            // 连接上kafka集群地址
            List<InetSocketAddress> addresses = ClientUtils.parseAndValidateAddresses(
                    config.getList(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG),
                    config.getString(ProducerConfig.CLIENT_DNS_LOOKUP_CONFIG));
            // 获取元数据
            if (metadata != null) {
                this.metadata = metadata;
            } else {
			// metadata.max.age.ms 默认值 5 分钟。生产者每隔多久需要更新一下自己的元数据
			// metadata.max.idle.ms 默认值 5 分钟。网络最多空闲时间设置，超过该阈值，就关闭该网络
                this.metadata = new ProducerMetadata(retryBackoffMs,
                        config.getLong(ProducerConfig.METADATA_MAX_AGE_CONFIG),
                        config.getLong(ProducerConfig.METADATA_MAX_IDLE_CONFIG),
                        logContext,
                        clusterResourceListeners,
                        Time.SYSTEM);
                this.metadata.bootstrap(addresses);
            }
			
			// 初始化 sender 线程
			this.sender = newSender(logContext, kafkaClient, this.metadata);
			String ioThreadName = NETWORK_THREAD_PREFIX + " | " + clientId;
			// 启动发送线程
			this.ioThread = new KafkaThread(ioThreadName, this.sender,true);
			this.ioThread.start();
			
			newSender(LogContext logContext, KafkaClient kafkaClient,ProducerMetadata metadata) {
				// 缓存的发送请求，默认值是 5。
				int maxInflightRequests = configureInflightRequests(producerConfig);
				// request.timeout.ms 默认值 30s。
				int requestTimeoutMs = producerConfig.getInt(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG);
				ChannelBuilder channelBuilder = ClientUtils.createChannelBuilder(producerConfig, time,logContext);
				ProducerMetrics metricsRegistry = new ProducerMetrics(this.metrics);
				Sensor throttleTimeSensor = Sender.throttleTimeSensor(metricsRegistry.senderMetrics);
				// maxInflightRequests 缓存的发送请求，默认值是 5。
				// 创建一个客户端对象
        // clientId  客户端id
        // reconnect.backoff.ms 默认值 50ms。重试时间间隔
        // reconnect.backoff.max.ms 默认值 1000ms。重试的总时间。每次重试失败时，呈指数增加重试时间，直至达到此最大值
        // send.buffer.bytes 默认值 128k。 socket 发送数据的缓冲区大小
        // receive.buffer.bytes 默认值 32k。socket 接收数据的缓冲区大小
        // request.timeout.ms 默认值 30s。
        // socket.connection.setup.timeout.ms 默认值 10s。生产者和服务器通信连接建立的时间。如果在超时之前没有建立连接，将关闭通信。
        // socket.connection.setup.timeout.max.ms 默认值 30s。生产者和服务器通信，每次连续连接失败时，连接建立超时将呈指数增加，直至达到此最大值。
        KafkaClient client = kafkaClient != null ? kafkaClient : new NetworkClient(
                new Selector(producerConfig.getLong(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG),
                        this.metrics, time, "producer", channelBuilder, logContext),
                metadata,
                clientId,
                maxInflightRequests,
                producerConfig.getLong(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG),
                producerConfig.getLong(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG),
                producerConfig.getInt(ProducerConfig.SEND_BUFFER_CONFIG),
                producerConfig.getInt(ProducerConfig.RECEIVE_BUFFER_CONFIG),
                requestTimeoutMs,
                producerConfig.getLong(ProducerConfig.SOCKET_CONNECTION_SETUP_TIMEOUT_MS_CONFIG),
                producerConfig.getLong(ProducerConfig.SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS_CONFIG),
                time,
                true,
                apiVersions,
                throttleTimeSensor,
                logContext);
				// acks 默认值是-1。
				// acks=0, 生产者发送给 Kafka 服务器后，不需要应答
				// acks=1，生产者发送给 Kafka 服务器后，Leader 接收后应答
				// acks=-1（all），生产者发送给 Kafka 服务器后，Leader 和在 ISR 队列的所
				有 Follower 共同应答
				short acks = configureAcks(producerConfig, log);
				// max.request.size 默认值 1m。 生产者发往 Kafka 集群单条信息的最大值
				// retries 重试次数，默认值 Int 的最大值
				// retry.backoff.ms 默认值 100ms。重试时间间隔
				return new Sender(logContext,client,metadata,this.accumulator,maxInflightRequests == 1,
				producerConfig.getInt(ProducerConfig.MAX_REQUEST_SIZE_CONFIG),
				acks,producerConfig.getInt(ProducerConfig.RETRIES_CONFIG),metricsRegistry.senderMetrics,time,
				requestTimeoutMs,
				producerConfig.getLong(ProducerConfig.RETRY_BACKOFF_MS_CONFIG),
				this.transactionManager,
				apiVersions);
				}
			
			send(...)
				//拦截器Intercepetor
				doSend(...)
					// 从 Kafka 拉取元数据。maxBlockTimeMs 表示最多能等待多长时间
					clusterAndWaitTime = waitOnMetadata(record.topic(), record.partition(), nowMs, maxBlockTimeMs);
					// 剩余时间 = 最多能等待时间 - 用了多少时间
					long remainingWaitMs = Math.max(0, maxBlockTimeMs - clusterAndWaitTime.waitedOnMetadataMs);
					// 更新集群元数据
					Cluster cluster = clusterAndWaitTime.cluster;
					// 序列化相关操作
					byte[] serializedKey;
					// 分区操作（根据元数据信息）
					int partition = partition(record, serializedKey, serializedValue, cluster);
					tp = new TopicPartition(record.topic(), partition);
					//计算压缩后的信息大小，key，value，headers总和
					int serializedSize = AbstractRecords.estimateSizeInBytesUpperBound(apiVersions.maxUsableProduceMagic(),
                    compressionType, serializedKey, serializedValue, headers);
					 // 保证数据大小能够传输(序列化后的  压缩后的)
					ensureValidRecordSize(serializedSize);
					// 消息发送的回调函数
					// producer callback will make sure to call both 'callback' and interceptor callback
					Callback interceptCallback = new InterceptorCallback<>(callback, this.interceptors, tp);
					// accumulator缓存  追加数据  result是是否添加成功的结果
					// 内存，默认 32m，里面是默认 16k 一个批次
					RecordAccumulator.RecordAppendResult result = accumulator.append(tp, timestamp, serializedKey,
							serializedValue, headers, interceptCallback, remainingWaitMs, true, nowMs);
					// 批次大小已经满了 获取有一个新批次创建		
					// 唤醒发送线程
					this.sender.wakeup();
							
			
					
			
			
			
			
			
			
			