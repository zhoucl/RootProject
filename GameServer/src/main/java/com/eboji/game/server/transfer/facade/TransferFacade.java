package com.eboji.game.server.transfer.facade;

import com.eboji.game.server.transfer.tcp.ServerClientTransfer;
import com.eboji.model.message.dt.DtCreGGRoomMsg;

public class TransferFacade {
	public static void facade(Object obj) {
		//游戏消息持久化处理
		if(obj instanceof DtCreGGRoomMsg) {
			ServerClientTransfer.persist(obj);
		}
	}
}