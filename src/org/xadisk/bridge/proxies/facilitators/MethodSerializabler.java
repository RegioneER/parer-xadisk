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

import java.io.IOException;
import java.lang.reflect.Method;

public class MethodSerializabler implements Serializabler<Method, SerializedMethod> {

    public MethodSerializabler() {
    }

    public SerializedMethod serialize(Method method) {
        String methodName = method.getName();
        String className = method.getDeclaringClass().getName();
        Class[] parameterTypes = method.getParameterTypes();
        String[] parameterTypesNames = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypesNames.length; i++) {
            parameterTypesNames[i] = parameterTypes[i].getName();
        }
        return new SerializedMethod(className, methodName, parameterTypesNames);
    }

    public Method reconstruct(SerializedMethod serializedMethod) throws IOException {
        try {
            Class clazz = Class.forName(serializedMethod.getClassName());
            String[] parameterTypesNames = serializedMethod.getParameterTypesNames();
            Class parameterTypes[] = new Class[parameterTypesNames.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                parameterTypes[i] = Class.forName(parameterTypesNames[i]);
            }
            return clazz.getMethod(serializedMethod.getMethodName(), parameterTypes);
        } catch (Throwable t) {
            IOException ioException = new IOException("A Method object could not be constructed from the object stream.");
            ioException.initCause(t);
            throw ioException;
        }
    }
}
