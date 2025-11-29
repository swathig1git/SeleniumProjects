package org.utils;

public class SAKSStringUtils {
    public static String  convertBrandNametoURL(String brandName){
        return  brandName.replaceAll(" \\+ ", "-")  // replace ' + ' with '-'
                    .replace(" ", "-")         // replace all spaces with '-'
                    .replace("'", "")          // remove apostrophes
                    .replace("à", "a")         // replace à with a
                    .replace("&", "and")
                    .toLowerCase();      // replace & with 'and'
    }
}
