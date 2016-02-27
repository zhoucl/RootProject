package com.eboji.agent.transfer.tcp;

import io.netty.channel.socket.SocketChannel;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eboji.agent.transfer.tcp.connection.ServerClientConnection;
import com.eboji.model.constant.Constant;

public class ServerClientTransfer {
	private static final Logger logger = LoggerFactory.getLogger(ServerClientTransfer.class);
	
	private static Map<String, SocketChannel> socketChannelMap = new ConcurrentHashMap<String, SocketChannel>();
	
	private static Map<Integer, Set<String>> serviceMap = new ConcurrentHashMap<Integer, Set<String>>();
	
	public static Map<String, SocketChannel> getSocketChannelMap() {
		return socketChannelMap;
	}

	public static void setSocketChannelMap(
			Map<String, SocketChannel> socketChannelMap) {
		ServerClientTransfer.socketChannelMap = socketChannelMap;
	}

	public static Map<Integer, Set<String>> getServiceMap() {
		return serviceMap;
	}

	public static void setServiceMap(Map<Integer, Set<String>> serviceMap) {
		ServerClientTransfer.serviceMap = serviceMap;
	}
	
	public static void parse(Map<Integer, Set<String>> _serviceMap) {
		Map<Integer, Set<String>> needSets = findNeedInitial(_serviceMap);
		
		//初始化
		for(Map.Entry<Integer, Set<String>> entry : needSets.entrySet()) {
			Set<String> entrysets = entry.getValue();
			for(String serviceaddress : entrysets) {
				String[] addresses = serviceaddress.split(Constant.STR_COLON);
				ServerClientConnection client = new ServerClientConnection(
						Integer.parseInt(addresses[1]), addresses[0]);
				ServerClientTransfer.getSocketChannelMap().put(serviceaddress, client.getSocketChannel());
				Set<String> sets = ServerClientTransfer.getServiceMap().get(entry.getKey());
				if(sets == null) {
					sets = new HashSet<String>();
				}
				
				sets.add(serviceaddress);
				ServerClientTransfer.getServiceMap().put(entry.getKey(), sets);
			}
		}
		
		System.out.println("+++++++++++++" + ServerClientTransfer.getServiceMap().toString());
		System.out.println("+++++++++++++" + ServerClientTransfer.getSocketChannelMap().toString());
	}
	
	protected static Map<Integer, Set<String>> findNeedInitial(Map<Integer, Set<String>> _serviceMap) {
		Map<Integer, Set<String>> needSets = new ConcurrentHashMap<Integer, Set<String>>();
		
		boolean isIncludeKey = false;
		for(Map.Entry<Integer, Set<String>> entry : _serviceMap.entrySet()) {
			//AgentServer-->LoginServer|AgentServer-->GameServer|AgentServer-->IMServer
			if(entry.getKey().intValue() == Constant.SRV_LOGIN.intValue() 
					|| entry.getKey().intValue() == Constant.SRV_GAME.intValue() 
					|| entry.getKey().intValue() == Constant.SRV_IM.intValue()) {
				Set<String> centerService = entry.getValue();
				for(Map.Entry<Integer, Set<String>> innerEntry : serviceMap.entrySet()) {
					if(entry.getKey().intValue() == innerEntry.getKey().intValue()) {
						isIncludeKey = true;
						Set<String> services = innerEntry.getValue();
						boolean flag = false;
						for(String cService : centerService) {
							for(String service : services) {
								if(cService.equals(service)) {
									flag = true;
									break;
								}
							}
							
							if(!flag) {
								if(needSets.get(entry.getKey()) == null) {
									Set<String> sets = new HashSet<String>();
									sets.add(cService);
									needSets.put(entry.getKey(), sets);
								} else {
									needSets.get(entry.getKey()).add(cService);
								}
							}
						}
					}
				}
				
				if(!isIncludeKey) {
					for(String cService : centerService) {
						if(needSets.get(entry.getKey()) == null) {
							Set<String> sets = new HashSet<String>();
							sets.add(cService);
							needSets.put(entry.getKey(), sets);
						} else {
							needSets.get(entry.getKey()).add(cService);
						}
					}
				}
			}
		}
		
		return needSets;
	}

	public static void write(Object obj) {
		for(Map.Entry<String, SocketChannel> entry : socketChannelMap.entrySet()) {
			String key = entry.getKey();
			
			logger.info("send request to [" + key +  "]");
			
			entry.getValue().writeAndFlush(obj);
		}
	}
	
	public static void login(Object obj) {
		Set<String> serviceSet = serviceMap.get(Constant.SRV_LOGIN);
		if(serviceSet != null && serviceSet.size() > 0) {
			int index = 0;
			if(serviceSet.size() > 1) {
				Random rand = new Random(System.currentTimeMillis());
				index = rand.nextInt(serviceSet.size());
			}
				
			socketChannelMap.get(serviceSet.toArray()[index]).writeAndFlush(obj);
		}
	}
	
	public static void processMj(Object obj) {
		Set<String> serviceSet = serviceMap.get(Constant.SRV_GAME);
		if(serviceSet != null && serviceSet.size() > 0) {
			int index = 0;
			if(serviceSet.size() > 1) {
				Random rand = new Random(System.currentTimeMillis());
				index = rand.nextInt(serviceSet.size());
			}
				
			socketChannelMap.get(serviceSet.toArray()[index]).writeAndFlush(obj);
		}
	}
}
