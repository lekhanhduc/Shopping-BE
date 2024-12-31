package vn.khanhduc.shoppingbackendservice.common;

import java.util.Random;

public class GenerateOtp {

    private GenerateOtp() {}

    private static final Random RANDOM = new Random();

    public static String generate() {
        StringBuilder otp = new StringBuilder();
        for(int i = 0; i <= 5; i++) {
            otp.append(RANDOM.nextInt(10));
        }
        return otp.toString();
    }
}
