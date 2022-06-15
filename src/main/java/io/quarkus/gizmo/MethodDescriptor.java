/*
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.quarkus.gizmo;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

public class MethodDescriptor {

    private final String declaringClass;
    private final String name;
    private final String returnType;
    private final Object rawReturnType;
    private final String[] parameterTypes;
    private final Object[] rawParameterTypes;
    private final String descriptor;

    private MethodDescriptor(String declaringClass, String name, Object returnType, Object... parameterTypes) {
        this.declaringClass = declaringClass;
        this.name = name;
        this.rawReturnType = returnType;
        this.rawParameterTypes = parameterTypes;
        // Return type does not support parameterized types
        this.returnType = DescriptorUtils.objectToDescriptor(returnType);
        this.parameterTypes = DescriptorUtils.objectsToDescriptor(parameterTypes);
        this.descriptor = DescriptorUtils.methodSignatureToDescriptor(this.returnType, this.parameterTypes);
        for (String p : this.parameterTypes) {
            if (p.length() != 1) {
                if (!(p.startsWith("L") && p.endsWith(";") || p.startsWith("["))) {
                    throw new IllegalArgumentException("Invalid parameter type " + p + " it must be in the JVM descriptor format");
                }
            }
        }
        if (this.returnType.length() != 1) {
            if (!(this.returnType.startsWith("L") && this.returnType.endsWith(";") || this.returnType.startsWith("["))) {
                throw new IllegalArgumentException("Invalid return type " + this.returnType + " it must be in the JVM descriptor format");
            }
        }
    }

    private MethodDescriptor(MethodInfo info) {
        this.name = info.name();
        this.rawReturnType = info.returnType();
        this.returnType = DescriptorUtils.typeToString(info.returnType());
        this.rawParameterTypes = new Object[info.parameters().size()];
        String[] paramTypes = new String[info.parameters().size()];
        for (int i = 0; i < paramTypes.length; ++i) {
            Type paramType = info.parameters().get(i);
            paramTypes[i] = DescriptorUtils.typeToString(paramType);
            this.rawParameterTypes[i] = paramType;
        }
        this.parameterTypes = paramTypes;
        this.declaringClass = info.declaringClass().toString().replace('.', '/');
        this.descriptor = DescriptorUtils.methodSignatureToDescriptor(returnType, parameterTypes);
    }

    public static MethodDescriptor ofMethod(String declaringClass, String name, String returnType, String... parameterTypes) {
        return new MethodDescriptor(DescriptorUtils.objectToInternalClassName(declaringClass), name, returnType, parameterTypes);
    }

    public static MethodDescriptor ofMethod(Class<?> declaringClass, String name, Class<?> returnType, Class<?>... parameterTypes) {
        return new MethodDescriptor(DescriptorUtils.objectToInternalClassName(declaringClass), name, returnType, parameterTypes);
    }

    public static MethodDescriptor ofMethod(Method method) {
        return ofMethod(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes());
    }

    public static MethodDescriptor ofMethod(Object declaringClass, String name, Object returnType, Object... parameterTypes) {
        return new MethodDescriptor(DescriptorUtils.objectToInternalClassName(declaringClass), name, returnType, parameterTypes);
    }

    public static MethodDescriptor ofConstructor(String declaringClass, String... parameterTypes) {
        return ofMethod(declaringClass, "<init>", void.class.getName(), parameterTypes);
    }

    public static MethodDescriptor ofConstructor(Class<?> declaringClass, Class<?>... parameterTypes) {
        return ofMethod(declaringClass, "<init>", void.class, (Object[]) parameterTypes);
    }

    public static MethodDescriptor ofConstructor(Object declaringClass, Object... parameterTypes) {
        return ofMethod(declaringClass, "<init>", void.class, (Object[]) parameterTypes);
    }

    public static MethodDescriptor of(MethodInfo methodInfo) {
        return new MethodDescriptor(methodInfo);
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public Object getRawReturnType() {
        return rawReturnType;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getRawParameterTypes() {
        return rawParameterTypes;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MethodDescriptor && equals((MethodDescriptor) o);
    }

    public boolean equals(MethodDescriptor o) {
        return o == this || o != null
            && declaringClass.equals(o.declaringClass)
            && name.equals(o.name)
            && descriptor.equals(o.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaringClass, name, descriptor);
    }

    @Override
    public String toString() {
        return "MethodDescriptor{" +
                "name='" + name + '\'' +
                ", returnType='" + returnType + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                '}';
    }

    public String getDescriptor() {
        return descriptor;

    }
}
