 +KafkaConsumer.java
		// 获取消费者组id
		this.groupId = Optional.ofNullable(groupRebalanceConfig.groupId);
		// 客户端id
		this.clientId = config.getString(CommonClientConfigs.CLIENT_ID_CONFIG);
		// 客户端请求服务端等待时间request.timeout.ms 默认是30s
        this.requestTimeoutMs = config.getInt(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG);
		// 重试时间 100
		this.retryBackoffMs = config.getLong(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG);

		// 拦截器
		List<ConsumerInterceptor<K, V>> interceptorList = (List) config.getConfiguredInstances(
				ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG,
				ConsumerInterceptor.class,
				Collections.singletonMap(ConsumerConfig.CLIENT_ID_CONFIG, clientId));
	    // key和value 的反序列化
		// offset从什么位置开始消费 默认，latest
        OffsetResetStrategy offsetResetStrategy = OffsetResetStrategy.valueOf(config.getString(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG).toUpperCase(Locale.ROOT));
		 // 元数据
		// retryBackoffMs 重试时间
		// 是否允许访问系统主题 exclude.internal.topics  默认是true，表示不允许
		// 是否允许自动创建topic  allow.auto.create.topics 默认是true
		this.metadata = new ConsumerMetadata(retryBackoffMs,
				config.getLong(ConsumerConfig.METADATA_MAX_AGE_CONFIG),
				!config.getBoolean(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG),
				config.getBoolean(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG),
				subscriptions, logContext, clusterResourceListeners);
		
		//连接kafka集群
		 List<InetSocketAddress> addresses = ClientUtils.parseAndValidateAddresses(
                    config.getList(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG), config.getString(ConsumerConfig.CLIENT_DNS_LOOKUP_CONFIG));
		// 心跳时间,默认 3s
		int heartbeatIntervalMs = 	config.getInt(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG);
		// 创建客户端对象
		// 连接重试时间 默认50ms
		// 最大连接重试时间 默认1s
		// 发送缓存 默认128kb
		// 接收缓存  默认64kb
		// 客户端请求服务端等待时间request.timeout.ms 默认是30s
		NetworkClient netClient = new NetworkClient(
				new Selector(config.getLong(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG), metrics, time, metricGrpPrefix, channelBuilder, logContext),
				this.metadata,
				clientId,
				100, // a fixed large enough value will suffice for max in-flight requests
				config.getLong(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG),
				config.getLong(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG),
				config.getInt(ConsumerConfig.SEND_BUFFER_CONFIG),
				config.getInt(ConsumerConfig.RECEIVE_BUFFER_CONFIG),
				config.getInt(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG),
				config.getLong(ConsumerConfig.SOCKET_CONNECTION_SETUP_TIMEOUT_MS_CONFIG),
				config.getLong(ConsumerConfig.SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS_CONFIG),
				time,
				true,
				apiVersions,
				throttleTimeSensor,
				logContext);
		// 消费者客户端
		// 客户端请求服务端等待时间request.timeout.ms 默认是30s
		this.client = new ConsumerNetworkClient(
				logContext,
				netClient,
				metadata,
				time,
				retryBackoffMs,
				config.getInt(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG),
				heartbeatIntervalMs); //Will avoid blocking an extended period of time to prevent heartbeat thread starvation
		// 消费者分区分配策略
		this.assignors = ConsumerPartitionAssignor.getAssignorInstances(
				config.getList(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG),
				config.originals(Collections.singletonMap(ConsumerConfig.CLIENT_ID_CONFIG, clientId))
		);

		// no coordinator will be constructed for the default (null) group id
		//  为消费者组准备的
		// auto.commit.interval.ms  自动提交offset时间 默认5s
		this.coordinator = !groupId.isPresent() ? null :
			new ConsumerCoordinator(groupRebalanceConfig,
					logContext,
					this.client,
					assignors,
					this.metadata,
					this.subscriptions,
					metrics,
					metricGrpPrefix,
					this.time,
					enableAutoCommit,
					config.getInt(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG),
					this.interceptors,
					config.getBoolean(ConsumerConfig.THROW_ON_FETCH_STABLE_OFFSET_UNSUPPORTED));
		// 配置抓数据的参数
		// fetch.min.bytes 默认最少一次抓取1个字节
		// fetch.max.bytes 默认最多一次抓取50m
		// fetch.max.wait.ms 抓取等待最大时间 500ms
		// max.partition.fetch.bytes 默认是1m
		// max.poll.records  默认一次处理500条
		this.fetcher = new Fetcher<>(
				logContext,
				this.client,
				config.getInt(ConsumerConfig.FETCH_MIN_BYTES_CONFIG),
				config.getInt(ConsumerConfig.FETCH_MAX_BYTES_CONFIG),
				config.getInt(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG),
				config.getInt(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG),
				config.getInt(ConsumerConfig.MAX_POLL_RECORDS_CONFIG),
				config.getBoolean(ConsumerConfig.CHECK_CRCS_CONFIG),
				config.getString(ConsumerConfig.CLIENT_RACK_CONFIG),
				this.keyDeserializer,
				this.valueDeserializer,
				this.metadata,
				this.subscriptions,
				metrics,
				metricsRegistry,
				this.time,
				this.retryBackoffMs,
				this.requestTimeoutMs,
				isolationLevel,
				apiVersions);
				
	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
