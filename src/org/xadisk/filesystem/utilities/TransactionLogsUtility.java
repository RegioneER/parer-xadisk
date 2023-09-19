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

package org.xadisk.filesystem.utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.xadisk.filesystem.DurableDiskSession;
import org.xadisk.filesystem.TransactionInformation;

public class TransactionLogsUtility {

    public static void addLogPositionToTransaction(TransactionInformation xid, int logFileIndex,
            long localPosition, Map<TransactionInformation, ArrayList<Long>> transactionLogPositions) {
        ArrayList<Long> temp = transactionLogPositions.get(xid);
        if (temp == null) {
            temp = new ArrayList<Long>(25);
            transactionLogPositions.put(xid, temp);
        }
        temp.add((long) logFileIndex);
        temp.add(localPosition);
    }

    public static void deleteLogsIfPossible(TransactionInformation xid, Map<TransactionInformation, ArrayList<Integer>> transactionsAndLogsOccupied,
            Map<Integer, Integer> transactionLogsAndOpenTransactions, int currentLogIndex,
            String transactionLogBaseName, DurableDiskSession durableDiskSession) throws IOException {
        ArrayList<Integer> logsOccupied = transactionsAndLogsOccupied.get(xid);
        if (logsOccupied == null) {
            return;
        }
        for (Integer logFileIndex : logsOccupied) {
            Integer numTxns = transactionLogsAndOpenTransactions.get(logFileIndex);
            if (numTxns == null) {
                continue;//already deleted.
            }
            numTxns--;
            if (numTxns == 0 && currentLogIndex != logFileIndex) {
                durableDiskSession.deleteFileDurably(new File(transactionLogBaseName + "_" + logFileIndex));
                transactionLogsAndOpenTransactions.remove(logFileIndex);
            } else {
                transactionLogsAndOpenTransactions.put(logFileIndex, numTxns);
            }
        }
    }

    public static void deleteLastLogIfPossible(int logFileIndex, Map<Integer, Integer> transactionLogsAndOpenTransactions,
            String transactionLogBaseName, DurableDiskSession durableDiskSession) throws IOException {
        Integer numTxns = transactionLogsAndOpenTransactions.get(logFileIndex);
        if (numTxns != null && numTxns == 0) {
            durableDiskSession.deleteFileDurably(new File(transactionLogBaseName + "_" + logFileIndex));
            transactionLogsAndOpenTransactions.remove(logFileIndex);
        }
    }

    public static void trackTransactionLogsUsage(TransactionInformation xid, Map<TransactionInformation, ArrayList<Integer>> transactionsAndLogsOccupied,
            Map<Integer, Integer> transactionLogsAndOpenTransactions, int logFileIndex) {
        boolean txnFirstTimeInThisLog = false;
        ArrayList<Integer> logsOccupied = transactionsAndLogsOccupied.get(xid);
        if (logsOccupied == null) {
            logsOccupied = new ArrayList<Integer>(2);
            transactionsAndLogsOccupied.put(xid, logsOccupied);
        }
        if (!logsOccupied.contains(logFileIndex)) {
            logsOccupied.add(logFileIndex);
            txnFirstTimeInThisLog = true;
        }
        if (txnFirstTimeInThisLog) {
            Integer numTxns = transactionLogsAndOpenTransactions.get(logFileIndex);
            if (numTxns == null) {
                numTxns = Integer.valueOf(0);
            }
            numTxns++;
            transactionLogsAndOpenTransactions.put(logFileIndex, numTxns);
        }
    }
}
