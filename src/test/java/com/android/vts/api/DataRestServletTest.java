/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.vts.api;

import com.android.vts.entity.DeviceInfoEntity;
import com.android.vts.entity.TestEntity;
import com.android.vts.entity.TestRunEntity;
import com.android.vts.util.ObjectifyTestBase;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

import static com.googlecode.objectify.ObjectifyService.factory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class DataRestServletTest extends ObjectifyTestBase {

    private Gson gson;

    @Mock private HttpServletRequest request;

    @Mock private HttpServletResponse response;

    /** It be executed before each @Test method */
    @BeforeEach
    void setUpExtra() {
        gson = new Gson();

        factory().register(TestEntity.class);
        factory().register(TestRunEntity.class);
        factory().register(DeviceInfoEntity.class);

        Key testParentKey = Key.create(TestEntity.class, "test1");
        Key testRunParentKey = Key.create(testParentKey, TestRunEntity.class, 1);
        DeviceInfoEntity deviceInfoEntity1 =
                new DeviceInfoEntity(
                        testRunParentKey,
                        "pi",
                        "sailfish",
                        "sailfish-userdebug",
                        "4585723",
                        "64",
                        "arm64-v8a");
        deviceInfoEntity1.setId(2384723984L);
        deviceInfoEntity1.save();

        DeviceInfoEntity deviceInfoEntity2 =
                new DeviceInfoEntity(
                        testRunParentKey,
                        "master",
                        "walleye",
                        "aosp_arm64_ab-userdebug",
                        "4585723",
                        "64",
                        "arm64-v8a");
        deviceInfoEntity2.setId(2384723422L);
        deviceInfoEntity2.save();
    }

    @Test
    public void testBranchData() throws IOException, ServletException {

        when(request.getPathInfo()).thenReturn("/branch");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        DataRestServlet dataRestServlet = new DataRestServlet();
        dataRestServlet.doGet(request, response);
        String result = sw.getBuffer().toString().trim();

        LinkedList resultList = gson.fromJson(result, LinkedList.class);

        assertEquals(resultList.size(), 2);
        assertEquals(resultList.get(0), "master");
        assertEquals(resultList.get(1), "pi");
    }

    @Test
    public void testDeviceData() throws IOException, ServletException {

        when(request.getPathInfo()).thenReturn("/device");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        DataRestServlet dataRestServlet = new DataRestServlet();
        dataRestServlet.doGet(request, response);
        String result = sw.getBuffer().toString().trim();

        LinkedList resultList = gson.fromJson(result, LinkedList.class);

        String value = "1";
        int charValue = value.charAt(0);
        String next = String.valueOf((char) (charValue + 1));
        System.out.println(next);

        String s = "asb";
        System.out.println(s.replace("[.]$", ""));
        assertTrue(s.charAt(0) == s.charAt(s.length() - 1));
        assertEquals(resultList.size(), 2);
        assertEquals(resultList.get(0), "aosp_arm64_ab-userdebug");
        assertEquals(resultList.get(1), "sailfish-userdebug");
    }
}
