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
 */
package org.ballerinalang.test.packaging;

import org.ballerinalang.test.context.BallerinaTestException;
import org.ballerinalang.test.util.BaseTest;
import org.ballerinalang.test.utils.PackagingTestUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Testing building of a single bal file.
 */
public class PackagingSingleBalBuildTestCase extends BaseTest {
    private Path tempProjectDirectory;
    private Path balFilePath;
    private String[] envVariables;

    @BeforeClass()
    public void setUp() throws BallerinaTestException, IOException {
        tempProjectDirectory = Files.createTempDirectory("bal-test-integration-packaging-single-bal-");
        envVariables = PackagingTestUtils.getEnvVariables();
        Path tempPackage = tempProjectDirectory.resolve("sourcePkg");
        Files.createDirectories(tempPackage);

        // Write bal file
        balFilePath = tempPackage.resolve("main.bal");
        Files.createFile(balFilePath);
        String mainFuncContent = "import ballerina/io;\n" +
                "\n" +
                "documentation {\n" +
                "   Prints `Hello World`.\n" +
                "}\n" +
                "function main(string... args) {\n" +
                "    io:println(\"Hello World!\");\n" +
                "}\n";
        Files.write(balFilePath, mainFuncContent.getBytes(), StandardOpenOption.CREATE);
    }

    @Test(description = "Test building a bal file by giving the absolute path")
    public void testBuildingSourceWithAbsolutePath() throws Exception {
        Path currentDirPath = tempProjectDirectory.resolve("foo");
        Files.createDirectories(currentDirPath);

        // Test ballerina build
        String[] clientArgs = {balFilePath.toString()};
        serverInstance.runMain(clientArgs, envVariables, "build", currentDirPath.toString());
        Path generatedBalx = currentDirPath.resolve("main.balx");
        Assert.assertTrue(Files.exists(generatedBalx));
    }

    @Test(description = "Test building a bal file by giving the path from the current directory")
    public void testBuildingSourceWithCurrentDir() throws Exception {
        // Test ballerina build
        String[] clientArgs = {Paths.get("sourcePkg", "main.bal").toString()};
        serverInstance.runMain(clientArgs, envVariables, "build", tempProjectDirectory.toString());
        Path generatedBalx = tempProjectDirectory.resolve("main.balx");
        Assert.assertTrue(Files.exists(generatedBalx));
    }

    @Test(description = "Test building a bal file to the output file givenby the user")
    public void testBuildingSourceToOutput() throws Exception {
        Path targetDirPath = tempProjectDirectory.resolve("target");
        Files.createDirectories(targetDirPath);

        // Test ballerina build
        String[] clientArgs = {"-o", targetDirPath.resolve("main.bal").toString(), balFilePath.toString()};
        serverInstance.runMain(clientArgs, envVariables, "build", tempProjectDirectory.toString());
        Path generatedBalx = targetDirPath.resolve("main.balx");
        Assert.assertTrue(Files.exists(generatedBalx));
    }

    @AfterClass
    private void cleanup() throws Exception {
        PackagingTestUtils.deleteFiles(tempProjectDirectory);
    }
}
