package com.gmugu.dakaqi.util;

import org.junit.Test;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.junit.Assert.*;
import static com.gmugu.dakaqi.util.RSAUtil.*;
/**
 * Created by mugu on 16/11/24.
 */
public class RSAUtilTest {
    @Test
    public void genKeyPair() throws Exception {
        RSAUtil.genKeyPair("./");
    }

    @Test
    public void test() throws Exception {
//        RSAPrivateKey privateKey = loadPrivateKeyByStr("MIICXQIBAAKBgQDzR0PBMuB9rZkbFapOO3IFjhVRdO4NP+WPpp3IUsIIFLIQ9Kgg\n" +
//                "RLnXjzb16+Z1hyZaXcHKAm5cq6znYVA848+EhbKNJVrVDNZxCLLZfi37iwoMzY9V\n" +
//                "oHU9z2kkK1X7uBQNqkJDjvXc2v7sRrg9iV2aEBxMj94XkYC5kVLTneH4jQIDAQAB\n" +
//                "AoGAR8XjqHEhKBL6U7JNn20MU7U6duC/hqsuR/ZJIJvB4O5yJmx32aVNCtqWxXEG\n" +
//                "efAYkZqNnNKgyrhfd5gPOxee1g/2rkddqbVW33zQ+I4huqIEympwa6Kzr0P+wQYY\n" +
//                "hVRWeYAog/blO8TmGrRQIHA3l2TXJfjQpna3G5K5K7nlsEUCQQD6QCEqrifx4URN\n" +
//                "wIYJgaw13IM0zd2wWn9WqZfbw5t6pfQTBcpCut9EUS/p7fo2xy0hwH2bS/boBRhd\n" +
//                "b28c//wfAkEA+N4gv19MnER1pfsxKYSKEdvhEmjCB1Y7nAG4+N4T9v6einWNBTGl\n" +
//                "vORDrBMqweXb3YXqNEmvwfhE/JZQsfV10wJABwK86yEt5UzWSQdXufR0Cp4+3r/V\n" +
//                "shcW8iqWIoX8WRdFGzNSU0RA9P0BRqBwHhC+Zu+plSwvophh8lwo1LsnKQJBAIqg\n" +
//                "YSl40lnxoH82rynrVGWiT3tLs1kW5dl+9CRcwE5DgtippkYQY7wWgsOlYZnkilaZ\n" +
//                "M96wdSDdRMBWktiuXFUCQQDDN1nsPAOzapSQXqNp/X/b6SpLYlVW+iBKcmwwA9rk\n" +
//                "HViYSOXg3uxQwEFxK9YfyVz6mfBI85jQVSfBql6/Dhr5");

//        RSAPublicKey publicKey = loadPublicKeyByStr("AAAAB3NzaC1yc2EAAAADAQABAAAAgQDzR0PBMuB9rZkbFapOO3IFjhVRdO4NP+WPpp3IUsIIFLIQ9KggRLnXjzb16+Z1hyZaXcHKAm5cq6znYVA848+EhbKNJVrVDNZxCLLZfi37iwoMzY9VoHU9z2kkK1X7uBQNqkJDjvXc2v7sRrg9iV2aEBxMj94XkYC5kVLTneH4jQ==");
//        byte[] encrypt = encrypt(privateKey, "haha".getBytes());
//        assertEquals(decrypt(publicKey,encrypt),"haha");

        assertEquals(new String(decrypt(loadPublicKeyByStr(loadPublicKeyByFile("./")),encrypt(loadPrivateKeyByStr(loadPrivateKeyByFile("./")),"sdf".getBytes()))),"sdf");
    }

}