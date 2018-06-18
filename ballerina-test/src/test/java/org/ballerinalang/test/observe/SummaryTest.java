/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.test.observe;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BArray;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.exceptions.BLangRuntimeException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests for summary metric.
 */
public class SummaryTest extends MetricTest {
    private CompileResult compileResult;

    @BeforeClass
    public void setup() {
        compileResult = BCompileUtil.compile("test-src/observe/summary_test.bal");
    }

    @Test
    public void testCountSummary() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testCountSummary");
        Assert.assertEquals(returns[0], new BInteger(6));
    }

    @Test
    public void testSumSummary() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testSumSummary");
        Assert.assertEquals(returns[0], new BInteger(5050));
    }

    @Test
    public void testSummaryError() {
        try {
            BRunUtil.invoke(compileResult, "testSummaryError");
            Assert.fail("Summary with extra_tag should not be registered");
        } catch (BLangRuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("extra_tag"), "Unexpected Ballerina Error");
        }
    }

    @Test
    public void testMaxSummary() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testMaxSummary");
        Assert.assertEquals(returns[0], new BInteger(3));
    }

    @Test
    public void testMeanSummary() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testMeanSummary");
        Assert.assertEquals(returns[0], new BFloat(2));
    }

    @Test
    public void testPercentileSummary() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testPercentileSummary");
        BArray<BStruct> bPercentileValues = (BArray<BStruct>) returns[0];
        Assert.assertEquals(bPercentileValues.size(), 5);
        for (int i = 0; i < bPercentileValues.size(); i++) {
            BStruct bPercentileValue = bPercentileValues.get(i);
            double percentile = bPercentileValue.getFloatField(0);
            double value = bPercentileValue.getFloatField(1);
            Assert.assertTrue(percentile >= 0 && percentile <= 1);
            Assert.assertTrue(value > 0);
        }
    }

    @Test
    public void testValueSummary() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testValueSummary");
        Assert.assertEquals(returns[0], new BInteger(500));
    }

    @Test
    public void testSummaryWithoutTags() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testSummaryWithoutTags");
        Assert.assertEquals(returns[0], new BFloat(3));
    }
}
