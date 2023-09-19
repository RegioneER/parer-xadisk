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

package org.xadisk.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;
import org.xadisk.bridge.proxies.impl.RemoteTransactionInformation;
import org.xadisk.filesystem.exceptions.AncestorPinnedException;
import org.xadisk.filesystem.exceptions.DeadLockVictimizedException;
import org.xadisk.filesystem.exceptions.DirectoryPinningFailedException;
import org.xadisk.filesystem.exceptions.LockingFailedException;
import org.xadisk.filesystem.exceptions.LockingTimedOutException;
import org.xadisk.filesystem.exceptions.TransactionRolledbackException;
import org.xadisk.filesystem.exceptions.TransactionTimeoutException;
import org.xadisk.filesystem.workers.DeadLockDetector;

public class NativeConcurrencyControl implements ConcurrencyControl {

    private final ResourceDependencyGraph resourceDependencyGraph;
    private final WorkManager workManager;
    private final DeadLockDetector deadLockDetector;
    private final LockTreeNode rootNode;
    private final ConcurrentHashMap<File, LockTreeNode> pinnedDirectories =
            new ConcurrentHashMap<File, LockTreeNode>();

    public NativeConcurrencyControl(FileSystemConfiguration configuration, WorkManager workManager,
            WorkListener workListener, NativeXAFileSystem nativeXAFileSystem) throws WorkException {
        resourceDependencyGraph = new ResourceDependencyGraph();
        deadLockDetector = new DeadLockDetector(configuration.getDeadLockDetectorInterval(), resourceDependencyGraph,
                nativeXAFileSystem, this);
        this.workManager = workManager;
        this.rootNode = new LockTreeNode(null, false, null);
        this.workManager.startWork(deadLockDetector, WorkManager.INDEFINITE, null, workListener);
    }

    public Lock acquireFileLock(TransactionInformation requestor, File f, long time, boolean exclusive) throws
            LockingFailedException, InterruptedException, TransactionRolledbackException,
            DeadLockVictimizedException, TransactionTimeoutException {
        if (exclusive) {
            return acquireExclusiveLock(requestor, f, time);
        } else {
            return acquireSharedLock(requestor, f, time);
        }
    }

    private LockTreeNode traverseDownToFileNode(File f, boolean checkForPins, TransactionInformation requestor) throws AncestorPinnedException {
        List<String> pathElements = new ArrayList<String>();
        File currentFile = f;
        while (currentFile != null) {
            File parentFile = currentFile.getParentFile();
            if (parentFile == null) {
                pathElements.add(currentFile.getAbsolutePath());
            } else {
                pathElements.add(currentFile.getName());
            }
            currentFile = parentFile;
        }

        LockTreeNode currentNode = rootNode;
        List<LockTreeNode> nodesOnPath = new ArrayList<LockTreeNode>();
        for (int i = pathElements.size() - 1; i >= 0; i--) {
            currentNode = currentNode.getChild(pathElements.get(i));
            nodesOnPath.add(currentNode);
        }
        if (checkForPins) {
            for (LockTreeNode nodeOnPath : nodesOnPath) {
                if (nodeOnPath.isPinnedByOtherTransaction(requestor)) {
                    throw new AncestorPinnedException(f.getAbsolutePath(), nodeOnPath.getPath().getAbsolutePath());
                }
            }
        }
        return currentNode;
    }

    private Lock acquireSharedLock(TransactionInformation requestor, File f, long time) throws
            LockingFailedException, InterruptedException, TransactionRolledbackException,
            DeadLockVictimizedException, TransactionTimeoutException {
        LockTreeNode fileNode = traverseDownToFileNode(f, true, requestor);
        NativeLock lock = fileNode.getLock();

        try {
            lock.startSynchBlock();
            long remainingTime = time;
            boolean indefiniteWait = (time == 0);
            resourceDependencyGraph.addDependency(requestor, lock);
            while (lock.isExclusive()) {
                try {
                    long now1 = System.currentTimeMillis();
                    lock.waitTillReadable(remainingTime);
                    if (!lock.isExclusive()) {
                        break;
                    }
                    long now2 = System.currentTimeMillis();
                    if (!indefiniteWait) {
                        remainingTime = remainingTime - (now2 - now1);
                        if (remainingTime <= 0) {
                            removeDependencyFromRDG(requestor);
                            throw new LockingTimedOutException(f.getAbsolutePath());
                        }
                    }
                } catch (InterruptedException ie) {
                    byte interruptCause = requestor.getNodeInResourceDependencyGraph().getInterruptCause();
                    removeDependencyFromRDG(requestor);
                    if (interruptCause == ResourceDependencyGraph.Node.INTERRUPTED_DUE_TO_DEADLOCK) {
                        throw new DeadLockVictimizedException(f.getAbsolutePath());
                    } else if (interruptCause == ResourceDependencyGraph.Node.INTERRUPTED_DUE_TO_TIMEOUT) {
                        throw new TransactionTimeoutException();
                    }
                    throw ie;
                }
            }
            removeDependencyFromRDG(requestor);
            resolveConcurrenyWithDirectoryPin(lock, fileNode, requestor);
            return lock;
        } finally {
            lock.endSynchBlock();
        }
    }

    private void resolveConcurrenyWithDirectoryPin(NativeLock lock, LockTreeNode fileNode, TransactionInformation requestor) throws AncestorPinnedException {
        lock.addHolder(requestor);
        if (fileNode.isPinnedByOtherTransaction(requestor)) {
            lock.removeHolder(requestor);
            throw new AncestorPinnedException(fileNode.getPath().getAbsolutePath(), "<unknown>");
        }
    }

    private Lock acquireExclusiveLock(TransactionInformation requestor, File f, long time)
            throws LockingFailedException, InterruptedException, TransactionRolledbackException,
            DeadLockVictimizedException, TransactionTimeoutException {
        LockTreeNode fileNode = traverseDownToFileNode(f, true, requestor);
        NativeLock lock = fileNode.getLock();
        try {
            lock.startSynchBlock();
            if (canUpgradeLock(lock, requestor)) {
                //upgrade case, so lock was already acquired; no need to handle directory pinning case.
                lock.setExclusive(true);
                lock.markUpgraded();
                return lock;
            }
            long remainingTime = time;
            boolean indefiniteWait = (time == 0);
            resourceDependencyGraph.addDependency(requestor, lock);
            while (!(lock.getNumHolders() == 0 || canUpgradeLock(lock, requestor))) {
                try {
                    long now1 = System.currentTimeMillis();
                    lock.waitTillWritable(remainingTime);
                    if (lock.getNumHolders() == 0 || canUpgradeLock(lock, requestor)) {
                        break;
                    }
                    long now2 = System.currentTimeMillis();
                    if (!indefiniteWait) {
                        remainingTime = remainingTime - (now2 - now1);
                        if (remainingTime <= 0) {
                            removeDependencyFromRDG(requestor);
                            throw new LockingTimedOutException(f.getAbsolutePath());
                        }
                    }
                } catch (InterruptedException ie) {
                    byte interruptCause = requestor.getNodeInResourceDependencyGraph().getInterruptCause();
                    removeDependencyFromRDG(requestor);
                    if (interruptCause == ResourceDependencyGraph.Node.INTERRUPTED_DUE_TO_DEADLOCK) {
                        throw new DeadLockVictimizedException(f.getAbsolutePath());
                    } else if (interruptCause == ResourceDependencyGraph.Node.INTERRUPTED_DUE_TO_TIMEOUT) {
                        throw new TransactionTimeoutException();
                    }
                    throw ie;
                }
            }
            removeDependencyFromRDG(requestor);
            //don't maky any changes in lock's state before "resolveCorrency" so that we don't need to clean them up.
            if (canUpgradeLock(lock, requestor)) {
                lock.markUpgraded();
            } else {
                resolveConcurrenyWithDirectoryPin(lock, fileNode, requestor);
            }
            lock.setExclusive(true);
            return lock;
        } finally {
            lock.endSynchBlock();
        }
    }

    public void releaseLock(TransactionInformation releasor, Lock lock) {
        NativeLock nativeLock = (NativeLock) lock;
        try {
            nativeLock.startSynchBlock();
            //TODO: write a good code to delete unnecessary entries from the fileLocks map.
            nativeLock.removeHolder(releasor);
            if (nativeLock.isExclusive()) {
                nativeLock.reset();
                nativeLock.notifyReadWritable();
            } else {
                nativeLock.notifyWritable();
            }
        } finally {
            nativeLock.endSynchBlock();
        }
    }

    public void releaseRenamePinOnDirectories(ArrayList<File> dirs) {
        for (File dir : dirs) {
            releaseRenamePinOnDirectory(dir);
        }
    }

    public void releaseRenamePinOnDirectory(File dir) {
        LockTreeNode dirNode = pinnedDirectories.remove(dir);
        unpinDirectoryTree(dirNode);
    }

    private void unpinDirectoryTree(LockTreeNode dirNode) {
        dirNode.releasePin();
        Collection<LockTreeNode> children = dirNode.getAllChildren();
        for (LockTreeNode child : children) {
            child.releasePin();
            unpinDirectoryTree(child);
        }
    }

    public void pinDirectoryForRename(File dir, TransactionInformation requestor)
            throws DirectoryPinningFailedException, AncestorPinnedException {
        LockTreeNode dirNode = traverseDownToFileNode(dir, true, requestor);
        pinDirectoryTree(dirNode, requestor, dir.getAbsolutePath());
        pinnedDirectories.put(dir, dirNode);//to keep a "strong" ref to this dirNode.
    }

    private void pinDirectoryTree(LockTreeNode dirNode, TransactionInformation requestor, String dirToRename)
            throws DirectoryPinningFailedException {
        pinLockTreeNode(dirNode, requestor, dirToRename);
        Collection<LockTreeNode> children = dirNode.getAllChildren();
        try {
            for (LockTreeNode child : children) {
                try {
                    pinDirectoryTree(child, requestor, dirToRename);
                } catch (DirectoryPinningFailedException dpfe) {
                    child.releasePin();
                    throw dpfe;
                }
            }
        } catch (DirectoryPinningFailedException dpfe) {
            dirNode.releasePin();
            throw dpfe;
        }
    }

    private void pinLockTreeNode(LockTreeNode node, TransactionInformation requestor, String dirToRename) throws DirectoryPinningFailedException {
        if (node.isPinnedByOtherTransaction(requestor)) {
            throw new DirectoryPinningFailedException(dirToRename, node.getPath().getAbsolutePath());
        }
        if (!node.attemptPinning(requestor)) {
            throw new DirectoryPinningFailedException(dirToRename, node.getPath().getAbsolutePath());
        }
        NativeLock lock = node.getLock();
        TransactionInformation holders[];
        try {
            lock.startSynchBlock();
            HashSet<TransactionInformation> holdersSet = lock.getHolders();
            holders = holdersSet.toArray(new TransactionInformation[0]);
        } finally {
            lock.endSynchBlock();
        }
        for (int i = 0; i < holders.length; i++) {
            if (!holders[i].equals(requestor)) {
                node.releasePin();
                throw new DirectoryPinningFailedException(dirToRename, node.getPath().getAbsolutePath());
            }
        }
    }

    private boolean canUpgradeLock(NativeLock lock, TransactionInformation requestor) {
        return lock.getNumHolders() == 1 && lock.isAHolder(requestor);
    }

    private void removeDependencyFromRDG(TransactionInformation requestor) {
        ResourceDependencyGraph.Node node = requestor.getNodeInResourceDependencyGraph();
        synchronized (node.getInterruptFlagLock()) {
            resourceDependencyGraph.removeDependency(requestor);
            if (node.getInterruptCause() != 0) {
                Thread.interrupted();
            }
        }
    }

    public ResourceDependencyGraph getResourceDependencyGraph() {
        return resourceDependencyGraph;
    }

    public void shutdown() {
        deadLockDetector.release();
    }

    public void interruptTransactionIfWaitingForResourceLock(TransactionInformation xid, byte cause) {
        ResourceDependencyGraph.Node node1 = getNodeForTransaction(xid);
        if (node1 != null) {
            synchronized (node1.getInterruptFlagLock()) {
                ResourceDependencyGraph.Node node2 = getNodeForTransaction(xid);
                if (node1 == node2) {
                    node1.setInterruptCause(cause);
                    node1.getThreadWaitingForLock().interrupt();
                }
            }
        }
    }

    private ResourceDependencyGraph.Node getNodeForTransaction(TransactionInformation xid) {
        if (xid instanceof RemoteTransactionInformation) {
            return resourceDependencyGraph.getNode(xid);
        } else {
            return xid.getNodeInResourceDependencyGraph();
        }
    }
}
