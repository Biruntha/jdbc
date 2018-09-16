/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.test.service.websub;

import org.awaitility.Duration;
import org.ballerinalang.test.BaseTest;
import org.ballerinalang.test.context.BMainInstance;
import org.ballerinalang.test.context.BServerInstance;
import org.ballerinalang.test.context.BallerinaTestException;
import org.ballerinalang.test.context.LogLeecher;
import org.ballerinalang.test.util.HttpResponse;
import org.ballerinalang.test.util.HttpsClientRequest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.Executors;

import static org.awaitility.Awaitility.given;
import static org.ballerinalang.test.service.websub.WebSubTestUtils.updateNotified;
import static org.ballerinalang.test.service.websub.WebSubTestUtils.updateSubscribed;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * This class includes an integration scenario which covers the following:
 * 1. Multiple subscriber services binding to the same endpoint
 * 2. Sending a subscription request to the hub and topic identified via WebSub discovery for the resource URL
 * specified as an annotation
 * 3. Intent verification for subscription request sent following WebSub discovery
 * 4. Prioritizing hub and topic specified as annotations over the resource URL if specified
 */
public class WebSubDiscoveryWithMultipleSubscribersTestCase extends BaseTest {

    private BServerInstance webSubSubscriber;
    private BMainInstance webSubPublisher;
    private BServerInstance webSubPublisherService;

    private final int subscriberServicePort = 8484;
    private final String helperServicePortAsString = "8093";

    private static String hubUrl = "https://localhost:9494/websub/hub";
    private static final String INTENT_VERIFICATION_SUBSCRIBER_ONE_LOG = "ballerina: Intent Verification agreed - Mode "
            + "[subscribe], Topic [http://www.websubpubtopic.com], Lease Seconds [3600]";
    private static final String INTENT_VERIFICATION_SUBSCRIBER_TWO_LOG = "ballerina: Intent Verification agreed - Mode "
            + "[subscribe], Topic [http://websubpubtopictwo.com], Lease Seconds [1200]";
    private static final String INTERNAL_HUB_NOTIFICATION_SUBSCRIBER_ONE_LOG =
            "WebSub Notification Received: {\"action\":\"publish\", \"mode\":\"internal-hub\"}";
    private static final String INTERNAL_HUB_NOTIFICATION_SUBSCRIBER_TWO_LOG =
            "WebSub Notification Received: {\"action\":\"publish\", \"mode\":\"internal-hub-two\"}";

    private LogLeecher intentVerificationLogLeecherOne = new LogLeecher(INTENT_VERIFICATION_SUBSCRIBER_ONE_LOG);
    private LogLeecher intentVerificationLogLeecherTwo = new LogLeecher(INTENT_VERIFICATION_SUBSCRIBER_TWO_LOG);
    private LogLeecher internalHubNotificationLogLeecherOne =
            new LogLeecher(INTERNAL_HUB_NOTIFICATION_SUBSCRIBER_ONE_LOG);
    private LogLeecher internalHubNotificationLogLeecherTwo =
            new LogLeecher(INTERNAL_HUB_NOTIFICATION_SUBSCRIBER_TWO_LOG);

    @BeforeClass
    public void setup() throws BallerinaTestException {
        webSubSubscriber = new BServerInstance(balServer);
        webSubPublisher = new BMainInstance(balServer);
        webSubPublisherService = new BServerInstance(balServer);

        String publisherBal = new File("src" + File.separator + "test" + File.separator + "resources"
                + File.separator + "websub" + File.separator + "websub_test_publisher.bal").getAbsolutePath();
        String[] publisherArgs = {"-e b7a.websub.hub.port=9494", "-e b7a.websub.hub.remotepublish=true",
                "-e test.hub.url=" + hubUrl, "-e test.helper.service.port=" + helperServicePortAsString};

        String publisherServiceBal = new File("src" + File.separator + "test" + File.separator + "resources"
                + File.separator + "websub" + File.separator + "websub_test_publisher_service.bal").getAbsolutePath();
        webSubPublisherService.startServer(publisherServiceBal);

        String subscriberBal = new File("src" + File.separator + "test" + File.separator + "resources"
                + File.separator + "websub" + File.separator +
                "websub_test_multiple_subscribers.bal").getAbsolutePath();
        webSubSubscriber.addLogLeecher(intentVerificationLogLeecherOne);
        webSubSubscriber.addLogLeecher(intentVerificationLogLeecherTwo);
        webSubSubscriber.addLogLeecher(internalHubNotificationLogLeecherOne);
        webSubSubscriber.addLogLeecher(internalHubNotificationLogLeecherTwo);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                webSubPublisher.runMain(publisherBal, publisherArgs, new String[]{});
            } catch (BallerinaTestException e) {
                //ignored since any errors here would be reflected as test failures
            }
        });

        //Allow to bring up the hub
        given().ignoreException(ConnectException.class).with().pollInterval(Duration.ONE_SECOND).await()
                .atMost(60, SECONDS).until(() -> {
            //using same pack location, hence server home is same
            HttpResponse response = HttpsClientRequest.doGet(hubUrl, webSubPublisherService.getServerHome());
            return response.getResponseCode() == 202;
        });

        String[] subscriberArgs = {"-e test.hub.url=" + hubUrl};
        webSubSubscriber.startServer(subscriberBal, subscriberArgs, new int[]{subscriberServicePort});
    }

    @Test
    public void testDiscoveryAndIntentVerification() throws BallerinaTestException, IOException {
        intentVerificationLogLeecherOne.waitForText(30000);
        intentVerificationLogLeecherTwo.waitForText(30000);
        updateSubscribed(helperServicePortAsString);
    }

    @Test(dependsOnMethods = "testDiscoveryAndIntentVerification")
    public void testContentReceipt() throws BallerinaTestException {
        internalHubNotificationLogLeecherOne.waitForText(45000);
        internalHubNotificationLogLeecherTwo.waitForText(45000);
    }

    @AfterClass
    private void cleanup() throws Exception {
        updateNotified(helperServicePortAsString);
        webSubSubscriber.shutdownServer();
        webSubPublisherService.shutdownServer();
    }
}
