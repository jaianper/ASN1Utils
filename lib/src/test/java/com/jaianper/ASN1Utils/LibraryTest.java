package com.jaianper.ASN1Utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {
    @Test void someLibraryMethodReturnsTrue() {
        Test3 classUnderTest;
        try {
            classUnderTest = new Test3();
            assertTrue(classUnderTest.testFile3(), "someLibraryMethod should return 'true'");
        } catch (Exception ex) {
            Logger.getLogger(LibraryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
