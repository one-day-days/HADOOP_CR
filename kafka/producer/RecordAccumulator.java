append(...)
	 // 获取或者创建一个队列（按照每个主题的分区）
     Deque<ProducerBatch> dq = getOrCreateDeque(tp);
	 // 尝试向队列里面添加数据（没有分配内存、批次对象，所以失败）
     RecordAppendResult appendResult = tryAppend(timestamp, key, value, headers, callback, dq, nowMs);
	  // this.batchSize 默认16k    数据大小17k
	// 取批次大小（默认 16k）和消息大小的最大值（上限默认 1m）。这样设计的主要原因是有可能一条消息的大小大于批次大小。
	int size = Math.max(this.batchSize, AbstractRecords.estimateSizeInBytesUpperBound(maxUsableMagic, compression, key, value, headers));
	// 申请内存  内存池分配内存  双端队列
	// 根据批次大小（默认 16k）和消息大小中最大值，分配内存
	buffer = free.allocate(size, maxTimeToBlock);
	// 尝试向队列里面添加数据（有内存，但是没有批次对象）
	RecordAppendResult appendResult = tryAppend(timestamp, key, value, headers, callback, dq, nowMs);
	// 封装内存buffer
	MemoryRecordsBuilder recordsBuilder = recordsBuilder(buffer, maxUsableMagic);
	// 再次封装（得到真正的批次大小）（有内存、有批次对象）
	ProducerBatch batch = new ProducerBatch(tp, recordsBuilder, nowMs);
	// 尝试向队列里面添加数据
	FutureRecordMetadata future = Objects.requireNonNull(batch.tryAppend(timestamp, key, value, headers,
			callback, nowMs));
	// 向队列的末尾添加批次
	dq.addLast(batch);
	incomplete.add(batch);