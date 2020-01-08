package com.wl4g.devops.tool.common.cli;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author vjay
 * @date 2020-01-08 17:16:00
 */
public class SshjUtil {


    public static void main(String... args)
            throws IOException {
        final SSHClient ssh = new SSHClient();
        //ssh.loadKnownHosts();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.connect("10.0.0.160",22);

        //ssh.authPassword("root","hwj13535248668");
        //or
        KeyProvider keyProvider = ssh.loadKeys(privateKey, publicKey, null);
        ssh.authPublickey("root",keyProvider);


        if(!ssh.isAuthenticated()){
            System.out.println("auth fail");
           return;
        }

        Session session = null;
        try {
            session = ssh.startSession();
            //ssh.authPublickey(System.getProperty("user.name"));
            //Session.Command cmd = session.exec("source /etc/profile\ncd /root/git/vjay\nmvn clean");
            Session.Command cmd = session.exec("ls");
            System.out.println("successStr=\n"+IOUtils.readFully(cmd.getInputStream()).toString());
            System.out.println("failStr=\n"+IOUtils.readFully(cmd.getErrorStream()).toString());
            cmd.join(5, TimeUnit.SECONDS);
            System.out.println("\n** exit status: " + cmd.getExitStatus());
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (IOException e) {
                // Do Nothing
            }

            ssh.disconnect();
        }
    }

    private static final String privateKey = "-----BEGIN RSA PRIVATE KEY-----\n"
            + "MIIEpQIBAAKCAQEAwawifYZlHNdmkdMmXdi6wslkfvvAVjGo4cBPtrOFonD0Paex\n"
            + "tVRckfkj6rCu4IkKOq6HFBBf1peYVHojLFUm4FGC+YatxoLcdExBj8A/oMVsWN8a\n"
            + "ZWv5RH0lqUPZyuefqIrD+pos0R1hJtEDh5cKKT+Ae7kOP2+pX0QeGu0F/z9jozPo\n"
            + "PiM5DaoM0xaDqhmn1dnY03X3TAY8/V9Oy1zSRslXoiF2EmfTiaHBlvCeK5WhCiMd\n"
            + "5Xfn26Eiw3RBePh/eGiXjgv2ILZA7pnoINCa+PXI6VW6mthHQ8GJ7w+2afCGZBun\n"
            + "hxpGPEKih8YGNHBlGKUPale0pPMqI703iENZrQIDAQABAoIBAQCD2/qvk+0Lsev3\n"
            + "pNceVgzxycROYIEXLkBZU2Hydk+pxVXFFIN9fa55BDNb+mdWIHeCdIkrM+rMY/Im\n"
            + "sfF4oZEScOzHjtaJrVcDJ1gL00x+3WtjJqMGInlYFAysLbH+36xoR/IekRGqXmJi\n"
            + "1zOcAU29v6pukhQNRK0AW5RTqMTIfuQeR7Utpq4QNk8BSfDKJcyVoiTpaJ3Pv4AG\n"
            + "rPkIgtIqtmDJmp6MbMVtJA8AyxYrAiJbHntVeRddj2NkrScK/J4RG+5U/9Jj5cpB\n"
            + "VA21C7pt4lvUVuAPQ+esFLiwxBlIfW+sS5jaKCbJaeBL78xOiaLBJNWfrGR4USdu\n"
            + "k0P8o/sBAoGBAO6YeX2TDyVH/8XsSq9nPs+hu85wMqWohswG9R6oxcurVJLLKjdd\n"
            + "vqwpFaOUaQHcZr7iN2jGWqxxjFRW5qnwevk8uyXs9joSKV2C7KUdxuP9dtuqJNyl\n"
            + "+p1qN57+J69WXQNJoMKmiICK43fW89CoNucqJqrmsLzBkQiFmtR83ORxAoGBAM/M\n"
            + "xBTaVPrJYXxx10TZo+NcN5dv4FfDmK/Uz6v2F4y8xoRxpde8yeo0xj9CM8J2SX8w\n"
            + "712GLWI03m8RsYg/g2qRueK34lrA0eJ8GGUV2SJOqOQqlv1FJJVDp3658Cm0IyDT\n"
            + "CvyHJyrGmJljUhTgnwMuuzcp1wAfenWFp6OaAfb9AoGAXZMxOr29V+rH9nD4zZgZ\n"
            + "e0c8J/e69VuGGmi0I+UfRgSY88V4diRvDohCc1hWYqN1LHH+Nzpr/2u9FKrMZmPp\n"
            + "ZuyZnYM1Aoty67jYZN2rzmju/7HYKS1zf99TlyiomcyuSAbNZOn5aSiPk8Wa8/+1\n"
            + "IK5YYfh94lmsLwJvOd0KqRECgYEAxUa22L02dCh/Pm+tWRXt+0lvFXwG1gtBh5xX\n"
            + "0/97+Aa3yMFEGv6GCq0zkJa/INy/hdrlRDrAFz3t9jAsBReXIbNbcBv27wWjvIrn\n"
            + "dgA59dILkSHF2oir5HEoMK1BjbYQq3bwNTHyQy/ra6PZJyzgiVryLbqw/NLlpXDP\n"
            + "6Aer2dkCgYEAwKka1EYm5/N4krwsVvNBWD4Xgt4dtkGkQYkhyXZIqGTLFntIdVig\n"
            + "jKoQ6kaFTPaSST4kWNoXxNWvBDjarOriPa//St+l5fsEjfhjF1CfCS5aKvKfIwmP\n"
            + "jZss7kAharoCjXmxdyqPBEjJPHMts7d93olfGDGCvFrZnEfuD+zUcmU=\n" + "-----END RSA PRIVATE KEY-----";

    private static final String publicKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDBrCJ9hmUc12aR0yZd2LrCyWR++8BWMajhwE+2s4WicPQ9p7G1VFyR+SPqsK7giQo6rocUEF/Wl5hUeiMsVSbgUYL5hq3Ggtx0TEGPwD+gxWxY3xpla/lEfSWpQ9nK55+oisP6mizRHWEm0QOHlwopP4B7uQ4/b6lfRB4a7QX/P2OjM+g+IzkNqgzTFoOqGafV2djTdfdMBjz9X07LXNJGyVeiIXYSZ9OJocGW8J4rlaEKIx3ld+fboSLDdEF4+H94aJeOC/YgtkDumegg0Jr49cjpVbqa2EdDwYnvD7Zp8IZkG6eHGkY8QqKHxgY0cGUYpQ9qV7Sk8yojvTeIQ1mt heweijie@armvm.com";
}
