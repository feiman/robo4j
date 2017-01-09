/*
 * Copyright (C)  2016. Miroslav Kopecky
 * This CommandExecutor.java  is part of robo4j.
 *
 *  robo4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  robo4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with robo4j .  If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.core.client.command;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.robo4j.commons.agent.AgentConsumer;
import com.robo4j.commons.command.GenericCommand;
import com.robo4j.commons.concurrent.CoreBusQueue;
import com.robo4j.commons.concurrent.LegoThreadFactory;
import com.robo4j.commons.logging.SimpleLoggingUtil;
import com.robo4j.core.client.enums.RequestCommandEnum;
import com.robo4j.core.client.io.ClientException;
import com.robo4j.core.system.CommandProvider;
import com.robo4j.core.util.ConstantUtil;

/**
 *
 * Command Executor is the consumer of command produced by Command Processor
 *
 * @author Miro Kopecky (@miragemiko)
 * @since 10.06.2016
 */
public class CommandExecutor<QueueType extends CoreBusQueue> implements AgentConsumer, Runnable {

	private volatile ExecutorService executorForCommands;
	private volatile AtomicBoolean active;
	private CommandProvider commandsProvider;
	private QueueType commandsQueue;

	public CommandExecutor(AtomicBoolean active, CommandProvider commandsProvider) {
		this.executorForCommands = Executors.newFixedThreadPool(ConstantUtil.PLATFORM_ENGINES,
				new LegoThreadFactory(ConstantUtil.COMMAND_BUS));
		this.commandsProvider = commandsProvider;
		this.active = active;
		SimpleLoggingUtil.print(CommandExecutor.class, "CONSUMER UP active= " + active);
	}

	@SuppressWarnings(value = "unchecked")
	@Override
	public void setMessageQueue(CoreBusQueue commandsQueue) {
		this.commandsQueue = (QueueType) commandsQueue;
		SimpleLoggingUtil.print(getClass(), "SET MESSAGE QUEUE= " + commandsQueue);
	}

	@SuppressWarnings(value = "unchecked")
	@Override
	public void run() {
		if (commandsQueue == null) {
			throw new ClientException("ERROR: consumer queue");
		}

		while (active.get() && commandsQueue.peek() != null) {
			try {
				GenericCommand<RequestCommandEnum> command = (GenericCommand) commandsQueue.take().getEntry();
				SimpleLoggingUtil.debug(getClass(), "commandQueue: " + command);
				Future<Boolean> moveFuture;
				switch (command.getType()) {
				case BACK:
				case MOVE:
				case RIGHT:
				case LEFT:
				case STOP:
				case EXIT:
				case HAND:
				case FRONT_RIGHT:
				case FRONT_LEFT:
					moveFuture = executorForCommands.submit(() -> commandsProvider.process(command));
					break;
				default:
					System.err.println("NO SUCH COMMAND= " + command);
					throw new ClientException("NO SUCH COMMAND= " + command);
				}

				boolean result = moveFuture.get();
				SimpleLoggingUtil.print(getClass(), "CommandExecutor: " + result);
				// if(!result){
				// throw new ClientException("ERROR ENGINE EXECUTION");
				// }
			} catch (InterruptedException | ExecutionException e) {
				SimpleLoggingUtil.print(getClass(), "CommandExecutor e= " + e);
				throw new ClientException("ERROR CONSUMER command execution, ", e);
			}
		}
		executorForCommands.shutdown();
	}

}