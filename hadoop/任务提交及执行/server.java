ContainerAllocatorRouter.java
serviceStart();
	if(job.isUber()){
		// 本地uber小任务模式，map、reduce任务都在同一个container中完成
		this.containerAllocator = new LocalContainerAllocator(this.clientService, this.context, nmHost, nmPort, nmHttpPort, containerID);
	}
	else{
		this.containerAllocator = new RMContainerAllocator(this.clientService, this.context);

	    super.serviceStart()
	}
	// 调子类及父类的相关函数完成服务的初始化并启动
	// 主要核心包括调用父类的RMCommunicator.serviceStart()方法，
	// 构造与ResourceManager通信的RPC协议ApplicationMasterProtocol的客户端代理
	// 并启动对应的资源申请线程，周期性执行heartbeat()方法，与ResourceManager通信并申请资源
	((Service)this.containerAllocator).init(getConfig());
	((Service)this.containerAllocator).start();
		serviceStart();
	 		this.eventHandlingThread.start();	 				
			super.serviceStart();
				//RMCommunicator.java
				serviceStart();
				 	scheduler= createSchedulerProxy(); // 构造与RM通信的RPC Client代理
					JobID id = TypeConverter.fromYarn(this.applicationId);
					JobId jobId = TypeConverter.toYarn(id);
					job = context.getJob(jobId);
					register(); // 向RM中的ApplicationMasterService注册
					startAllocatorThread(); // 启动周期性线程，申请对应的资源
					    allocatorThread = new Thread(new AllocatorRunnable());
					    allocatorThread.setName("RMCommunicator Allocator");
					    allocatorThread.start();
					    	//allocatorThread.run
					    	hearbeat();
					    		List<Container> allocatedContainers = getResources(); // 获取申请到的container资源
					    		scheduledRequests.assign(allocatedContainers);
						    		// while循环内都是在判断该container是否满足资源限制以及是否不在黑名单节点上等条件，
								    // 如果container所在的Node节点在黑名单上，则其会寻找一个与该container相匹配的任务，并重新为其申请资源
								    // 该处while内会将不满足条件的container从该列表中去掉，添加到release列表中，
								    // 并在下一次心跳请求过程中，返回给RM进行对应container资源的释放
				 					//去除不能用的容器
					    		    // release container if we could not assign it 
								    // 最终将没有分配的container添加到需要release的列表中，并在下一次心跳请求过程中
								    // 返回给RM进行对应container资源的释放
				 					assignContainers(allocatedContainers);
				 						// 在二次分配的过程中；其首先会根据container的优先级，
									  	// 优先将其分配给failed的MapTask、之后再是Reduce任务、最后才是分配给对应的正常的MapTask任务，、
									  	// 在最后的正常MapTask任务的分配中，其会根据任务本地性的原则：
									  	// 优先分配node-local(数据与container在同一个节点)
									  	// rack-local(数据与container在同一机架)、
									  	// no-local(数据与container不在同一个机架)的顺序方式来进行map任务的分配
				 						assignWithoutLocality(); // 优先分配failed的MapTask
				 						containerAssigned(allocated, assigned)
				 						// 按照任务本地性的原则进行分配，
										// 从对应的本地mapsHostMapping、机架mapsRackMapping、maps任务取出任务进行分配
				 						assignMapsWithLocality();
				 							containerAssigned(allocated, assigned)
				 								//将容器分配的事件发送给任务尝试
				 								eventHandler.handle(new TaskAttemptContainerAssignedEvent(assigned.attemptID, allocated, applicationACLs));
super.serviceStart();
		




ContainerLauncherImpl.java
serviceInit(Configuration);
serviceStart();
	eventHandlingThread.start();
		//run()
		launcherPool.execute(createEventProcessor(event));
			// 使用对应的Container来执行具体的事件
    		case CONTAINER_REMOTE_LAUNCH:
	    		ContainerRemoteLaunchEvent launchEvent = (ContainerRemoteLaunchEvent) event;
			    c.launch(launchEvent);
			    	// 构造ContainerManagementProtocol协议的RPC客户端
  					ContainerManagementProtocolProxyData proxy = null;
  					// rpc远程调用startContainers()与对应的NodeManager通信启动该container
    				StartContainersResponse response = proxy.getContainerManagementProtocol().startContainers(requestList);

			    break;
  			case CONTAINER_REMOTE_CLEANUP:
			    c.kill();
			    break;
