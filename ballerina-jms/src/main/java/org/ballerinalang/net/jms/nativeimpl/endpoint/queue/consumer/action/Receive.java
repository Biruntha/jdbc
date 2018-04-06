/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.ballerinalang.net.jms.nativeimpl.endpoint.queue.consumer.action;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.net.jms.AbstractBlockinAction;
import org.ballerinalang.net.jms.Constants;
import org.ballerinalang.net.jms.utils.BallerinaAdapter;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.util.Objects;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

/**
 * {@code Send} is the send action implementation of the JMS Connector.
 */
@BallerinaFunction(orgName = "ballerina",
                   packageName = "jms",
                   functionName = "receive",
                   receiver = @Receiver(type = TypeKind.STRUCT,
                                        structType = "QueueConsumerConnector",
                                        structPackage = "ballerina.jms"),
                   args = {
                           @Argument(name = "timeInMilliSeconds",
                                     type = TypeKind.INT)
                   },
                   returnType = {
                           @ReturnType(type = TypeKind.STRUCT,
                                       structPackage = "ballerina.jms",
                                       structType = "Message")
                   },
                   isPublic = true
)
public class Receive extends AbstractBlockinAction {

    @Override
    public void execute(Context context, CallableUnitCallback callableUnitCallback) {

        Struct queueConsumerConnectorBObject = BallerinaAdapter.getReceiverStruct(context);
        MessageConsumer messageConsumer = BallerinaAdapter.getNativeObject(queueConsumerConnectorBObject,
                                                                           Constants.JMS_QUEUE_CONSUMER_OBJECT,
                                                                           MessageConsumer.class,
                                                                           context
        );

        long timeInMilliSeconds = context.getIntArgument(0);

        try {
            Message message = messageConsumer.receive(timeInMilliSeconds);
            if (Objects.nonNull(message)) {
                BStruct messageBObject = BLangConnectorSPIUtil.createBStruct(context,
                                                                             Constants.BALLERINA_PACKAGE_JMS,
                                                                             Constants.JMS_MESSAGE_STRUCT_NAME);
                messageBObject.addNativeData(Constants.JMS_MESSAGE_OBJECT, message);
                context.setReturnValues(messageBObject);
            } else {
                context.setReturnValues();
            }
        } catch (JMSException e) {
            throw new BallerinaException("Message receiving failed", e, context);
        }
    }
}
