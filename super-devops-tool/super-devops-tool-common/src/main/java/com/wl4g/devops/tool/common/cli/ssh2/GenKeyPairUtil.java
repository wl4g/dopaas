package com.wl4g.devops.tool.common.cli.ssh2;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Signature;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class GenKeyPairUtil {

    public static final String defauleComment = "genBySys";

    public static void main(String[] args) throws Exception {
        SSHKeyPair generate = generate();

        System.out.println(generate.getPublicKey());
        System.out.println(generate.getPrivateKey());
    }

    public static SSHKeyPair generate() throws JSchException {
        return generate(defauleComment);
    }

    public static SSHKeyPair generate(String comment) throws JSchException {
        SSHKeyPair sshKeyPair = new SSHKeyPair();
        int type = KeyPair.RSA;
        JSch jsch = new JSch();

        KeyPair kpair = KeyPair.genKeyPair(jsch, type);

        //私钥
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//向OutPutStream中写入
        kpair.writePrivateKey(baos);
        String privateKey = baos.toString();
        //公钥
        baos = new ByteArrayOutputStream();
        kpair.writePublicKey(baos, comment);
        String publicKey = baos.toString();
        //System.out.println("Finger print: " + kpair.getFingerPrint());
        kpair.dispose();
        //得到公钥字符串
        //String publicKeyString = RSAEncrypt.loadPublicKeyByFile(filePath,filename + ".pub");
        //System.out.println(publicKeyString.length());
        sshKeyPair.setPublicKey(publicKey);
        //System.out.println(publicKeyString);
        //得到私钥字符串
        //String privateKeyString = RSAEncrypt.loadPrivateKeyByFile(filePath,filename);
        //System.out.println(privateKeyString.length());
        //System.out.println(privateKeyString);
        sshKeyPair.setPrivateKey(privateKey);

        return sshKeyPair;
    }

    public static class SSHKeyPair {

        private String privateKey;

        private String publicKey;

        public SSHKeyPair() {

        }

        public SSHKeyPair(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
    }


}
