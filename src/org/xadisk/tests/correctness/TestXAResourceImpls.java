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

/*
 */
package org.xadisk.tests.correctness;

import java.io.File;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.xadisk.bridge.proxies.interfaces.XASession;
import org.xadisk.connector.inbound.EndPointActivation;
import org.xadisk.connector.inbound.XADiskActivationSpecImpl;
import org.xadisk.filesystem.NativeXAFileSystem;
import org.xadisk.filesystem.XAFileSystemCommonness;
import org.xadisk.filesystem.TransactionInformation;
import org.xadisk.filesystem.standalone.StandaloneFileSystemConfiguration;
import org.xadisk.filesystem.utilities.FileIOUtility;

public class TestXAResourceImpls {

    private static final String SEPERATOR = File.separator;
    private static final String currentWorkingDirectory = "C:\\test";
    private static final String XADiskSystemDirectory = "C:\\XADiskSystem";
    private static long txnId = System.currentTimeMillis();

    public static void main(String args[]) {
        XAFileSystemCommonness xafs = null;
        try {
            TestUtility.cleanupDirectory(new File(XADiskSystemDirectory));
            boolean commitDuringRecovery = true;
            File f[] = new File[3];
            for (int i = 0; i < 3; i++) {
                File dir = new File(currentWorkingDirectory + SEPERATOR + i);
                FileIOUtility.deleteDirectoryRecursively(dir);
                FileIOUtility.createDirectoriesIfRequired(dir);
                f[i] = new File(dir, "a.txt");
                if (f[i].exists()) {
                    FileIOUtility.deleteFile(f[i]);
                }
            }

            xafs = bootXAFileSystemCompletely();

            XASession xaSession = xafs.createSessionForXATransaction();
            XAResource xar = xaSession.getXAResource();
            Xid xid = getNewXid();
            xar.start(xid, XAResource.TMNOFLAGS);
            xaSession.createFile(f[0], false);
            xar.end(xid, XAResource.TMSUCCESS);
            assertNoCommit(f[0]);

            xaSession = xafs.createSessionForXATransaction();
            xar = xaSession.getXAResource();
            xid = getNewXid();
            xar.start(xid, XAResource.TMNOFLAGS);
            xaSession.createFile(f[1], false);
            xar.end(xid, XAResource.TMSUCCESS);
            xar.prepare(xid);
            assertNoCommit(f[1]);

            xaSession = xafs.createSessionForXATransaction();
            xar = xaSession.getXAResource();
            xid = getNewXid();
            xar.start(xid, XAResource.TMNOFLAGS);
            xaSession.createFile(f[2], false);
            xar.end(xid, XAResource.TMSUCCESS);
            xar.prepare(xid);
            xar.commit(xid, false);
            assertCommit(f[2]);

            xafs.shutdown();
            xafs = bootXAFileSystem();
            Thread.sleep(1000);

            xar = xafs.getXAResourceForRecovery();
            Xid[] preparedXids = xar.recover(XAResource.TMSTARTRSCAN);
            for (Xid prepared : preparedXids) {
                if (commitDuringRecovery) {
                    xar.commit(prepared, true);
                } else {
                    xar.rollback(prepared);
                }
            }

            assertNoCommit(f[0]);
            if (commitDuringRecovery) {
                assertCommit(f[1]);
                assertCommit(f[2]);
            }

            xafs.shutdown();
            xafs = bootXAFileSystemCompletely();

            for (int i = 0; i < 3; i++) {
                if (f[i].exists()) {
                    FileIOUtility.deleteFile(f[i]);
                }
            }

            SimulatedMessageEndpointFactory smef = new SimulatedMessageEndpointFactory();
            smef.goTill = SimulatedMessageEndpointFactory.GoTill.consume;
            XADiskActivationSpecImpl as = new XADiskActivationSpecImpl();
            as.setAreFilesRemote("false");
            as.setFileNamesAndEventInterests(new File(currentWorkingDirectory + SEPERATOR + 0).getAbsolutePath()
                    + "::111");
            xafs.registerEndPointActivation(new EndPointActivation(smef, as));

            smef = new SimulatedMessageEndpointFactory();
            smef.goTill = SimulatedMessageEndpointFactory.GoTill.prepare;
            as = new XADiskActivationSpecImpl();
            as.setAreFilesRemote("false");
            as.setFileNamesAndEventInterests(new File(currentWorkingDirectory + SEPERATOR + 1).getAbsolutePath()
                    + "::111");
            xafs.registerEndPointActivation(new EndPointActivation(smef, as));

            smef = new SimulatedMessageEndpointFactory();
            smef.goTill = SimulatedMessageEndpointFactory.GoTill.commit;
            as = new XADiskActivationSpecImpl();
            as.setAreFilesRemote("false");
            as.setFileNamesAndEventInterests(new File(currentWorkingDirectory + SEPERATOR + 2).getAbsolutePath()
                    + "::111");
            xafs.registerEndPointActivation(new EndPointActivation(smef, as));

            xaSession = xafs.createSessionForXATransaction();
            xar = xaSession.getXAResource();
            xid = getNewXid();
            xar.start(xid, XAResource.TMNOFLAGS);
            xaSession.createFile(f[0], false);
            xaSession.createFile(f[1], false);
            xaSession.createFile(f[2], false);
            xaSession.setPublishFileStateChangeEventsOnCommit(true);
            xar.end(xid, XAResource.TMSUCCESS);
            xar.prepare(xid);
            xar.commit(xid, false);

            Thread.sleep(1000);//to let the events get raised to MEPs.

            xafs.shutdown();
            xafs = bootXAFileSystem();
            Thread.sleep(1000);

            xar = xafs.getEventProcessingXAResourceForRecovery();
            preparedXids = xar.recover(XAResource.TMSTARTRSCAN);
            if (preparedXids.length != 1) {
                throw new AssertionError();
            }

            System.out.println("Completing the only one expected prepared txn for inbound case.");
            if (commitDuringRecovery) {
                xar.commit(preparedXids[0], true);
                System.out.println("Committed");
            } else {
                xar.rollback(preparedXids[0]);
                System.out.println("Rolled-back");
            }

            xafs.shutdown();
            xafs = bootXAFileSystemCompletely();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                xafs.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Xid getNewXid() {
        return TransactionInformation.getXidInstanceForLocalTransaction(txnId++);
    }

    private static void assertCommit(File f) {
        if (!f.exists()) {
            throw new AssertionError();
        }
    }

    private static void assertNoCommit(File f) {
        if (f.exists()) {
            throw new AssertionError();
        }
    }

    private static XAFileSystemCommonness bootXAFileSystem() throws Exception {
        XAFileSystemCommonness xaFS;
        StandaloneFileSystemConfiguration configuration = new StandaloneFileSystemConfiguration(XADiskSystemDirectory, "local");
        configuration.setServerPort(9998);
        xaFS = NativeXAFileSystem.bootXAFileSystemStandAlone(configuration);
        return xaFS;
    }

    private static XAFileSystemCommonness bootXAFileSystemCompletely() throws Exception {
        XAFileSystemCommonness xaFS = bootXAFileSystem();
        xaFS.waitForBootup(-1L);
        return xaFS;
    }
}
