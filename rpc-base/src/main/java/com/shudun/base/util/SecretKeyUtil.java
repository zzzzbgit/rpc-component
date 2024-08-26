package com.shudun.base.util;

import com.shudun.base.dto.RpcRequest;
import com.shudun.base.exception.VerifySignatureException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.Security;

public class SecretKeyUtil {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static String sign(byte[] secretKey, RpcRequest rpcRequest) {
        return Tools.byteToHex(hmacSM3(secretKey, getSignContent(rpcRequest)));
    }

    public static boolean verify(byte[] secretKey, RpcRequest rpcRequest, String sm3Hash) {
        if (sm3Hash.equals(sign(secretKey, rpcRequest))) {
            int minutes = (int) ((System.currentTimeMillis() - rpcRequest.getTime()) / (1000 * 60));
            if (minutes > 60) {
                throw new VerifySignatureException("签名时间异常!");
            }
        } else {
            throw new VerifySignatureException("签名结果验证失败!");
        }
        return true;
    }

    private static byte[] getSignContent(RpcRequest rpcRequest) {
        String request = String.valueOf(rpcRequest.getTime())
                + rpcRequest.getRequestId()
                + rpcRequest.getInterfaceName()
                + rpcRequest.getMethodName()
                + rpcRequest.getHasCode();
        return hashSM3(request.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] hmacSM3(byte[] key, byte[] srcData) {
        KeyParameter keyParameter = new KeyParameter(key);
        SM3Digest digest = new SM3Digest();
        HMac mac = new HMac(digest);
        mac.init(keyParameter);
        mac.update(srcData, 0, srcData.length);
        byte[] result = new byte[mac.getMacSize()];
        mac.doFinal(result, 0);
        return result;
    }

    private static byte[] hashSM3(byte[] srcData) {
        SM3Digest digest = new SM3Digest();
        digest.update(srcData, 0, srcData.length);
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return hash;
    }
}