package com.leyi.test.server.impl;

import com.leyi.test.interfaces.TestService;

public class TestServiceImpl implements TestService {
    @Override
    public byte[] random(String req, int len) {
//        System.out.println("random");
        return new byte[len];
    }
}
