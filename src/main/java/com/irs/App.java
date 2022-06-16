package com.irs;

import java.io.IOException;
import java.util.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * IRS Project
 * Scrape all providers and display them by zip code
 */
public class App {
    static Boolean isExit = false;
    static HashMap<String, ProviderList> listOfProviders = new HashMap<>(); // key: zipcode, value: list of Providers

    static final String url = "https://www.irs.gov/efile-index-taxpayer-search";
    static final String resultId = "solr-results-summary";
    static final String infoClassName = "views-field views-field-nothing-1 views-align-left";

    //public static int getZipCode(Scanner scanner)
    //public static int getSortOption(Scanner scanner)
    //public static Document getDocument(String url)
    //public static void addProviders(ArrayList<Element> providersInfo, String zipCode)
    //public static void printTable(String zipCode, boolean isReverse)
    //public static void sortProviders(String zipCode, int option)

    public static void main( String[] args ) {
        System.out.println("""

                         ************************ IRS PROJECT ************************
                         
                         Enter your zip code to find the closest authorized IRS e-file
                         providers, where you can electronically file your tax return.
                          
                         Enter "exit" to exit the program.
                          
                         *************************************************************
                         """);

        try (Scanner scanner = new Scanner(System.in)) {
            while(!isExit) {
                //Take zipcode from user
                int zipCodeNum = getZipCode(scanner);
                
                if(!isExit) {
                    String zipCode = String.format("%05d", zipCodeNum);
                    
                    if(!listOfProviders.containsKey(zipCode)) {
                        listOfProviders.put(zipCode, new ProviderList());

                        Document document = getDocument(url + "?zip=" + zipCode + "&state=All");
                        ArrayList<Element> firstPage = document.getElementsByClass(infoClassName);

                        if(firstPage.size() > 0) {
                            // resultSummary = "Found 'number of provider' Matching Items" 
                            String resultSummary = Objects.requireNonNull(document.getElementById(resultId)).text();

                            // extract number of providers from resultSummary
                            int total = 0;
                            int index = 6; 

                            while(resultSummary.charAt(index) != ' ') {
                                total = total*10 + (resultSummary.charAt(index) - '0');
                                index++;
                            }

                            int pages = total / 10 + 1; // each page has 10 providers
                             
                            addProviders(firstPage, zipCode);

                            for(int i = 1; i < pages; i++) {
                                Document d = getDocument(url + "?zip=" + zipCode + "&state=All&page=" + i);
                                ArrayList<Element> pageInfo = d.getElementsByClass(infoClassName);
                                addProviders(pageInfo, zipCode);
                            }
                        }
                    }
                    
                    // display result
                    if(listOfProviders.get(zipCode).size() == 0)
                        System.out.println("Not Found any result!");

                    else { 
                        boolean isNext = false;
                        printTable(zipCode, false);
                        do {
                            int option = getSortOption(scanner);
                            if( option > 0) {
                                if(option == 5)
                                    isNext = true;
                                else
                                    sortProviders(zipCode, option);
                            }
                        } while(!isNext && !isExit); 
                    }
                }
            }
        }
        
        System.out.println("""
                            ********************************************* 
                            ***************** Thank You ***************** 
                            *********************************************
                            """);
    }

    // take zip code from customer
    public static int getZipCode(Scanner scanner) {
        int number = -1;
        boolean isValid = false;
        do {
            System.out.print("Zip Code: ");

            if(!scanner.hasNextInt()) {
                String input = scanner.nextLine();
                if(input.equals("exit"))
                    isExit = true;
            }
            else {
                number = scanner.nextInt();
                if(number >= 0 && number <= 99999)
                    isValid = true;
                scanner.nextLine();
            }

            if(!isValid && !isExit)
                System.out.println("Please enter only number in range of 00000 - 99999!\n");

        } while (!isValid && !isExit);

        return number;
    }

    // take sort option
    public static int getSortOption(Scanner scanner) {
        int number = -1;
        boolean isValid = false;
        System.out.println("""
                         Sort by Name of Business: press 1 for ASC or 2 for DESC order.
                         Sort by Point of Contact: press 3 for ASC or 4 for DESC order.
                         Press 5 for another search or Enter 'exit' to quit.""");

        do {
            System.out.print("Enter your option: ");
            if(!scanner.hasNextInt()) {
                String input = scanner.nextLine();
                if(input.equals("exit"))
                    isExit = true;
            }
            else {
                number = scanner.nextInt();
                if(number > 0 && number < 6)
                    isValid = true;
                scanner.nextLine();
            }

            if(!isValid && !isExit)
                System.out.println("Please enter only number in range of 1 - 5!\n");

        } while (!isValid && !isExit);

        System.out.println();
        return number;
    }

    // send request to IRS to get data
    public static Document getDocument(String url) {
        Connection conn = Jsoup.connect(url);
        Document document = null;

        try {
            document = conn.get();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }

        return document;
    }

    // append Providers into Map based on zipcode
    public static void addProviders(ArrayList<Element> providersInfo, String zipCode) {
        ArrayList<Provider> providers = listOfProviders.get(zipCode).providers;

        for(Element e : providersInfo) {
            String[] info = e.html().split("<br> ");
            providers.add(new Provider(info));
        } 
    }

    //print table
    public static void printTable(String zipCode, boolean isReverse) {
        String line = new String(new char[192]).replace('\0', '-');

        //Header
        System.out.println(line);
        System.out.printf("%-37s | %-76s | %-25s | %-14s | %s%n",
        "Name of Business", "Address City/State/ZIP", "Point of Contact" ,"Telephone", "Type of Service");
        System.out.println(line);

        //Body
        ArrayList<Provider> providers = listOfProviders.get(zipCode).providers;
        if(!isReverse)
            for (Provider provider : providers) System.out.println(provider.toString());
        else 
            for(int i = providers.size() - 1; i >= 0; i--)
                System.out.println(providers.get(i).toString());

        System.out.println();
    }

    //Sort data
    public static void sortProviders(String zipCode, int option) {
        ProviderList p = listOfProviders.get(zipCode);
        boolean isReverse = false;
        switch (option) {
            case 1 -> System.out.println("Sorted by Name of Business in ASC order.");
            case 2 -> System.out.println("Sorted by Name of Business in DESC order.");
            case 3 -> System.out.println("Sorted by Point of Contact in ASC order.");
            case 4 -> System.out.println("Sorted by Point of Contact in DESC order.");
            default -> System.out.println("Not an option");
        }

        if(option == 2 || option == 4)
            isReverse = true;
        
        if(option < 3) {
            if(!p.isSortedByName)
                p.providers.sort(Provider.BusinessNameComparator);
            p.isSortedByName = true;
            p.isSortedByContact = false;
        }
        else {
            if(!p.isSortedByContact)
                p.providers.sort(Provider.PointOfContactComparator);
            p.isSortedByName = false;
            p.isSortedByContact = true;
        }

        printTable(zipCode, isReverse);
    }

}
