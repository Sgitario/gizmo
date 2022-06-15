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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class MethodWithParameterizedReturnTypeTestCase {

    @Test
    public void testMethodWithParamType() {
        try (ClassCreator creator = ClassCreator.builder().className("com.MyTest").build()) {
            MethodCreator method = creator.getMethodCreator("m", new ParameterizedClass(List.class, String.class));

            Assert.assertEquals("()Ljava/util/List<Ljava/lang/String;>;", method.getSignature());
        }
    }
}