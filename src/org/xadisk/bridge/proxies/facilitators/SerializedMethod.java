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

package org.xadisk.bridge.proxies.facilitators;

import java.io.Serializable;

public class SerializedMethod implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final String className;
    private final String methodName;
    private final String[] parameterTypesNames;

    public SerializedMethod(String className, String methodName, String[] parameterTypesNames) {
        this.className = className;
        this.methodName = methodName;
        this.parameterTypesNames = parameterTypesNames;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getParameterTypesNames() {
        return parameterTypesNames;
    }
}
