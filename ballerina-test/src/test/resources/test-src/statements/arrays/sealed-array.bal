// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

// Int Arrays

function createIntSealedArray() returns int {
    int[5] sealedArray = [2, 15, 200, 1500, 5000];
    return lengthof sealedArray;
}

function createIntSealedArrayWithLabel() returns int {
    sealed int[] sealedArray = [2, 15, 200, 1500, 5000];
    return lengthof sealedArray;
}

function createIntDefaultSealedArray() returns (int[], int) {
    int[5] sealedArray;
    return (sealedArray, lengthof sealedArray);
}

// Boolean Arrays

function createBoolSealedArray() returns int {
    boolean[5] sealedArray = [true, false, false, true, false];
    return lengthof sealedArray;
}

function createBoolSealedArrayWithLabel() returns int {
    sealed boolean[] sealedArray = [true, false, false, true, false];
    return lengthof sealedArray;
}

function createBoolDefaultSealedArray() returns (boolean[], int) {
    boolean[5] sealedArray;
    return (sealedArray, lengthof sealedArray);
}

// Float Arrays

function createFloatSealedArray() returns int {
    float[5] sealedArray = [0.0, 15.2, 1100f, -25.8, -10f];
    return lengthof sealedArray;
}

function createFloatSealedArrayWithLabel() returns int {
    sealed float[] sealedArray = [0.0, 15.2, 1100f, -25.8, -10f];
    return lengthof sealedArray;
}

function createFloatDefaultSealedArray() returns (float[], int) {
    float[5] sealedArray;
    return (sealedArray, lengthof sealedArray);
}

// String Arrays

function createStringSealedArray() returns int {
    string[5] sealedArray = ["a", "abc", "12", "-12", "."];
    return lengthof sealedArray;
}

function createStringSealedArrayWithLabel() returns int {
    sealed string[] sealedArray = ["a", "abc", "12", "-12", "."];
    return lengthof sealedArray;
}

function createStringDefaultSealedArray() returns (string[], int) {
    string[5] sealedArray;
    return (sealedArray, lengthof sealedArray);
}

// Any Type Arrays

function createAnySealedArray() returns int {
    any[5] sealedArray = ["a", true, 12, -12.5, "."];
    return lengthof sealedArray;
}

function createAnySealedArrayWithLabel() returns int {
    sealed any[] sealedArray = ["a", true, 12, -12.5, "."];
    return lengthof sealedArray;
}

// Record Type Arrays

type Person record {
    string name;
    int age;
};

function createRecordSealedArray() returns int {
    Person[5] sealedArray = [{}, {}, {}, {}, {}];
    return lengthof sealedArray;
}

function createRecordSealedArrayWithLabel() returns int {
    sealed Person[] sealedArray = [{}, {}, {}, {}, {}];
    return lengthof sealedArray;
}

// Byte Arrays

function createByteSealedArray() returns int {
    byte a = base16 `aaabcfccadafcd341a4bdfabcd8912df`;
    byte[5] sealedArray = [a, a, a, a, a];
    return lengthof sealedArray;
}

function createByteSealedArrayWithLabel() returns int {
    byte a = base16 `aaabcfccadafcd341a4bdfabcd8912df`;
    sealed byte[] sealedArray = [a, a, a, a, a];
    return lengthof sealedArray;
}
// Tuple Arrays

function createTupleSealedArray() returns int {
    (int, boolean)[3] sealedArray = [(2, true), (3, false), (6, true)];
    sealedArray[2] = (5, false);
    return lengthof sealedArray;
}

function createTupleSealedArrayWithLabel() returns int {
    sealed (int, boolean)[] sealedArray = [(2, true), (3, false), (6, true)];
    return lengthof sealedArray;
}

// Function Params and Returns

function functionParametersAndReturns() returns (int, int) {
    boolean[3] sealedArray = [true, false, false];
    boolean[3] returnedBoolArray;
    string[2] returnedStrArray;
    (returnedBoolArray, returnedStrArray) = mockFunction(sealedArray);

    return (lengthof returnedBoolArray, lengthof returnedStrArray);
}

function mockFunction(boolean[3] sealedArray) returns (boolean[3], string[2]) {
    sealed string[] sealedStrArray = ["Sam", "Smith"];
    return (sealedArray, sealedStrArray);
}

// Runtime Exceptions

function invalidIndexAccess(int index) {
    boolean[3] x1 = [true, false, true];
    boolean x2 = x1[index];
}

function assignedArrayInvalidIndexAccess() {
    int[3] x1 = [1, 2, 3];
    int[] x2 = x1;
    x2[4] = 10;
}

// Match Statments

function unionAndMatchStatementSealedArray(float[4] x) returns string {
    return unionTest(x);
}

function unionAndMatchStatementUnsealedArray(float[] x) returns string {
    return unionTest(x);
}

function unionTest(boolean | int[] | float[4] | float[] x) returns string {
    match x {
        boolean k => return "matched boolean";
        int[] k => return "matched int array";
        float[4] k => return "matched sealed float array size 4";
        float[] k => return "matched float array";
    }
}
