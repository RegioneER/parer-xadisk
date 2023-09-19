/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package org.xadisk.tests.correctness;

class RunnableTest implements Runnable {

    private CoreXAFileSystemTests.testNames testName;
    private String testDirectory;

    public RunnableTest(CoreXAFileSystemTests.testNames testName, String testDirectory) {
        this.testName = testName;
        this.testDirectory = testDirectory;
    }

    public void run() {
        try {
            CoreXAFileSystemTests coreXAFileSystemTests = new CoreXAFileSystemTests();
            if (testName.equals(CoreXAFileSystemTests.testNames.testConcurrentMoneyTransfer)) {
                coreXAFileSystemTests.testConcurrentMoneyTransfer(testDirectory);
            } else if (testName.equals(CoreXAFileSystemTests.testNames.testIOOperations)) {
                coreXAFileSystemTests.testIOOperations(testDirectory);
            } else if (testName.equals(CoreXAFileSystemTests.testNames.testDynamicReadWrite)) {
                coreXAFileSystemTests.testDynamicReadWrite(testDirectory);
            } else if (testName.equals(CoreXAFileSystemTests.testNames.testFileSystemEventing)) {
                coreXAFileSystemTests.testFileSystemEventing(testDirectory);
            } else if (testName.equals(CoreXAFileSystemTests.testNames.testConcurrentMoneyTransferPostCrash)) {
                coreXAFileSystemTests.testConcurrentMoneyTransferPostCrash(testDirectory);
            } else if (testName.equals(CoreXAFileSystemTests.testNames.testDynamicReadWritePostCrash)) {
                coreXAFileSystemTests.testDynamicReadWritePostCrash(testDirectory);
            } else if (testName.equals(CoreXAFileSystemTests.testNames.testIOOperationsPostCrash)) {
                coreXAFileSystemTests.testIOOperationsPostCrash(testDirectory);
            } else if (testName.equals(CoreXAFileSystemTests.testNames.testFileSystemEventingPostCrash)) {
                coreXAFileSystemTests.testFileSystemEventingPostCrash(testDirectory);
            }
        } catch (Throwable t) {
            System.out.println("Test failed " + testName + " in " + testDirectory + " due to " + t);
            t.printStackTrace();
        }
    }
}
