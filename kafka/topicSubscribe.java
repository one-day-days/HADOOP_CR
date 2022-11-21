kafkaConsumer.subscribe(topics);
	// 异常情况处理
	if (topics == null)
	// 清空订阅异常主题的缓存数据
	fetcher.clearBufferedDataForUnassignedTopics(topics);
	// 判断是否需要更改订阅主题，如果需要更改主题，则更新元数据信息
	if (this.subscriptions.subscribe(new HashSet<>(topics),listener))
		subscribe()
				// 注册负载均衡监听（例如消费者组中，其他消费者退出触发再平衡）
				registerRebalanceListener(listener);
				// 按照设置的主题开始订阅，自动分配分区
				setSubscriptionType(SubscriptionType.AUTO_TOPICS);
				// 修改订阅主题信息
				return changeSubscription(topics);
					// 如果订阅的主题和以前订阅的一致，就不需要修改订阅信息。如果不一致，就需要修改。
					if (subscription.equals(topicsToSubscribe))
					return false;
					subscription = topicsToSubscribe;
					return true;
		// 如果订阅的和以前不一致，需要更新元数据信息																
		metadata.requestUpdateForNewTopics();