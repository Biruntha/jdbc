/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.ballerinalang.nativeimpl.io;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.model.NativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.nativeimpl.io.channels.base.DataChannel;
import org.ballerinalang.nativeimpl.io.events.EventContext;
import org.ballerinalang.nativeimpl.io.events.EventManager;
import org.ballerinalang.nativeimpl.io.events.EventResult;
import org.ballerinalang.nativeimpl.io.events.data.CloseDataChannelEvent;
import org.ballerinalang.nativeimpl.io.utils.IOUtils;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;

import java.util.concurrent.CompletableFuture;

/**
 * Native function ballerina.io#DataChannel.close().
 *
 * @since 0.974.1
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "io",
        functionName = "close",
        receiver = @Receiver(type = TypeKind.OBJECT, structType = "DataChannel", structPackage = "ballerina.io"),
        returnType = {@ReturnType(type = TypeKind.RECORD, structType = "IOError", structPackage = "ballerina.io")},
        isPublic = true
)
public class CloseDataChannel implements NativeCallableUnit {
    /**
     * The index of the DataChannel.
     */
    private static final int DATA_CHANNEL_INDEX = 0;

    private static EventResult closeResponse(EventResult<Boolean, EventContext> result) {
        EventContext eventContext = result.getContext();
        Context context = eventContext.getContext();
        CallableUnitCallback callback = eventContext.getCallback();
        Throwable error = eventContext.getError();
        if (null != error) {
            BStruct errorStruct = IOUtils.createError(context, error.getMessage());
            context.setReturnValues(errorStruct);
        }
        callback.notifySuccess();
        return result;
    }

    /**
     * <p>
     * Closes data channel.
     * </p>
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void execute(Context context, CallableUnitCallback callback) {
        BStruct dataChannelStruct = (BStruct) context.getRefArgument(DATA_CHANNEL_INDEX);
        DataChannel channel = (DataChannel) dataChannelStruct.getNativeData(IOConstants.DATA_CHANNEL_NAME);
        EventContext eventContext = new EventContext(context, callback);
        CloseDataChannelEvent dataChannelCloseEvt = new CloseDataChannelEvent(channel, eventContext);
        CompletableFuture<EventResult> publish = EventManager.getInstance().publish(dataChannelCloseEvt);
        publish.thenApply(CloseDataChannel::closeResponse);
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
