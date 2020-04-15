package com.virusX.passwordBro;

import java.security.SecureRandom;

class Generate {
    private int strength;
    final private int numericStart = 48;
    final private int numericEnd = 57;
    final private int lowerCaseStart = 97;
    final private int lowerCaseEnd = 122;
    final private int upperCaseStart = 65;
    final private int upperCaseEnd = 90;
    final private int specialChar = 64;

    private int easyLength;
    private int modLength;
    private int hardLength;

    Generate(int strength, int easyLength, int modLength, int hardLength) {
        this.strength = strength;
        this.easyLength = easyLength;
        this.modLength = modLength;
        this.hardLength = hardLength;
    }

    String generate(){
        String password;
        if (strength == 1) {
            password = generatePassEasy();
        } else if (strength == 2) {
            password = generatePassMod();
        } else {
            password = generatePassHard();
        }
        return password;
    }

    private int getRandom(int start, int end, boolean caseValue) {
        SecureRandom random = new SecureRandom();
        if(caseValue) {
            return random.nextInt(end);
        } else {
            int diff = end - start + 1;
            return random.nextInt(diff) + start;
        }
    }

    private String generatePassEasy() {
        StringBuilder pass = new StringBuilder();
        int specialCharCount = 0, charCount = 0;
        while (pass.length() != easyLength) {
            if(charCount == easyLength - 2 && specialCharCount == 0) {
                pass.append((char) specialChar);
                break;
            }

            int caseValue = getRandom(0,3, true);
            //0 = numeric, 1 = lowerCase, 2 = '@'

            if(caseValue == 0) {
                int asciiValue = getRandom(numericStart, numericEnd, false);
                pass.append((char) asciiValue);
                charCount++;
            } else if(caseValue == 1) {
                int asciiValue = getRandom(lowerCaseStart, lowerCaseEnd, false);
                pass.append((char) asciiValue);
                charCount++;
            } else if(caseValue == 2 && specialCharCount < 1) {
                pass.append((char) specialChar);
                specialCharCount++;
                charCount++;
            }
        }
        return pass.toString();
    }

    private String generatePassMod() {
        StringBuilder pass = new StringBuilder();
        int specialCharCount = 0, charCount = 0;
        while (pass.length() != modLength) {
            if(charCount == modLength - 1 && specialCharCount == 0) {
                pass.append((char) specialChar);
                break;
            }

            int caseValue = getRandom(0,4, true);
            //0 = numeric, 1 = lowerCase; 2 = upperCase, 3 = '@'

            if(caseValue == 0) {
                int asciiValue = getRandom(numericStart, numericEnd, false);
                pass.append((char) asciiValue);
                charCount++;
            } else if(caseValue == 1) {
                int asciiValue = getRandom(lowerCaseStart, lowerCaseEnd, false);
                pass.append((char) asciiValue);
                charCount++;
            } else if(caseValue == 2) {
                int asciiValue = getRandom(upperCaseStart, upperCaseEnd, false);
                pass.append((char) asciiValue);
                charCount++;
            } else if(caseValue == 3 && specialCharCount < 1) {
                pass.append((char) specialChar);
                specialCharCount++;
                charCount++;
            }
        }
        return pass.toString();
    }

    private String generatePassHard() {
        StringBuilder pass = new StringBuilder();
        int specialCharCount = 0;
        while(pass.length() != hardLength) {
            int caseValue = getRandom(0,5, true);
            //0 = numeric, 1 = lowerCase; 2 = upperCase, 3 = '@', 4 = specialChars

            if(caseValue == 0) {
                int asciiValue = getRandom(numericStart, numericEnd, false);
                pass.append((char) asciiValue);
            } else if(caseValue == 1) {
                int asciiValue = getRandom(lowerCaseStart, lowerCaseEnd, false);
                pass.append((char) asciiValue);
            } else if(caseValue == 2) {
                int asciiValue = getRandom(upperCaseStart, upperCaseEnd, false);
                pass.append((char) asciiValue);
            } else if(caseValue == 3 && specialCharCount < 1) {
                pass.append((char) specialChar);
                specialCharCount++;
            } else {
                int specialCharEnd = 38;
                int specialCharStart = 35;
                int asciiValue = getRandom(specialCharStart, specialCharEnd, false);
                pass.append((char) asciiValue);
            }
        }
        return pass.toString();
    }
}