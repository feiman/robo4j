/*
 * Copyright (c) 2014, 2019, Marcus Hirt, Miroslav Wengner
 *
 * Robo4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Robo4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Robo4J. If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.net;

import com.robo4j.scheduler.RoboThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
class MessageServerThreadExecutor {
    private static final MessageServerThreadExecutor INSTANCE = new MessageServerThreadExecutor();
    private static final int KEEP_ALIVE_TIME = 10;
    private static final int POOL_SIZE_CORE = 20;
    private static final int POOL_SIZE_MAX = 30;
    private static final String POOL_WORKER_NAME = "Robo4J communication executor";
    private static final String POOL_THREAD_NAME = "Robo4J-communication";

    private final LinkedBlockingQueue<Runnable> executorQueue;
    private final ExecutorService executor;


    MessageServerThreadExecutor() {
        executorQueue = new LinkedBlockingQueue<>();
        executor = new ThreadPoolExecutor(POOL_SIZE_CORE, POOL_SIZE_MAX, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, executorQueue, new RoboThreadFactory(new ThreadGroup(POOL_WORKER_NAME), POOL_THREAD_NAME, true));
    }

    static ExecutorService getExecutor(){
        return INSTANCE.executor;
    }

    static void shutdown(){
        INSTANCE.executor.shutdown();
    }
}
