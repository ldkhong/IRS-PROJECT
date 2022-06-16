package com.irs;

import java.util.Comparator;

public class Provider {
    private final String businessName;
    private final String address;
    private final String cityStateZipcode;
    private final String pointOfContact;
    private final String phone;
    private final String typeOfService;

    /**
     * Parameter Constructor
     * @param info = {businessName, address, cityStateZipcode, pointOfContact, phone, typeOfService}
     *  */
    public Provider(String[] info) {
        this.businessName = info[0].replace("&amp;", "&");
        this.address = info[1].replace("<br>", ", ").replace("&amp;", "&");
        this.cityStateZipcode = info[2];
        this.pointOfContact = info[3];
        this.phone = (info[4].length() < 10)? info[4] : info[4].substring(3, info[4].length() - 4); // remove <a> </a> in phone
        this.typeOfService = info[5];
    }

    // Getters
    public String getBusinessName() { return businessName;}
    public String getPointOfContact() { return pointOfContact; }

    /*  public String getAddress() { return address; }
        public String getCityStateZipcode() { return cityStateZipcode;}
        public String getPhone() { return phone;}
        public String getTypeOfService() {return typeOfService;} */

    /*Comparator for sorting the list by Name of Business*/
    public static Comparator<Provider> BusinessNameComparator = (p1, p2) -> {
       String businessName1 = p1.getBusinessName().toUpperCase();
       String businessName2 = p2.getBusinessName().toUpperCase();

       //ascending order
       return businessName1.compareTo(businessName2);
    };
    
    /*Comparator for sorting the list by Point Of Contact*/
    public static Comparator<Provider> PointOfContactComparator = (p1, p2) -> {
        String pointOfContact1 = p1.getPointOfContact().toUpperCase();
        String pointOfContact2 = p2.getPointOfContact().toUpperCase();

        //ascending order
        return pointOfContact1.compareTo(pointOfContact2);
    };

    @Override
    public String toString() {
        int max = 28;
        return String.format("%-37s | %-76s | %-25s | %-14s | %s", 
        businessName, address + ", " + cityStateZipcode,  pointOfContact , phone,
                typeOfService.substring(0, Math.min(max, typeOfService.length())));
        //typeOfService.substring(0, Math.min(max, typeOfService.length()))
    }
}
