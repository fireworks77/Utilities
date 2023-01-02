package UtilityToolsTests;

import Utilities.Utilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class UtilityToolsTests {

    private final Utilities utilityTools = new Utilities();

    @BeforeEach
    private void beforeEach(){
        System.out.println("=======================Start At: " + utilityTools.getTimeStampString() +
                "=======================");
    }

    @Test
    @DisplayName("Utility Tools remove duplicates tests")
    void RemoveDuplicatesTests(){
        for(int j=0;j<10;j++){
            ArrayList<String> testData = new ArrayList<String>();
            int testSize = 10000000;
            for(int i=0; i<testSize; i++){
                testData.add("test"+String.valueOf(i));
                testData.add("test"+String.valueOf(i));
            }
            ArrayList<String> results = utilityTools.filterDuplicates(testData, false);
            System.out.println(results.size());
            assert(results.size() == testSize);
        }
    }

    @AfterEach
    private void AfterEach(){
        System.out.println("=======================Finished At: " + utilityTools.getTimeStampString() +
                "=======================");
    }

}
