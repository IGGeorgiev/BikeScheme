package bikescheme;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class EventTest {

    @Test
    public void test1() {
        assertEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a, b, c, d") );
    }
    @Test
    public void test2() {
        assertNotEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a, b, c, e") );
    }
    @Test
    public void equalsTest() {
        //Expect True on equal events
        assertTrue(
                new Event("1 00:00,C,i,m, a, b, c, d").equals(
                new Event("1 00:00,C,i,m, a, b, c, d")));
        //Expect False on similar, but not equal events
        assertFalse(
                new Event("1 00:00,C,i,m, a, b, c, d").equals(
                new Event("2 00:00,C,i,m, a, b, c, d")));
        assertFalse(
                new Event("1 00:00,C,i,m, a, b, c, d").equals(
                new Event("1 00:01,C,i,m, a, b, c, d")));
        assertFalse(
                new Event("1 00:00,C,i,m, a, b, c, d").equals(
                new Event("1 00:00,C,i,m, c, b, c, d")));
        //Assert False on completely different events
        assertFalse(
                new Event("1 00:00,C,i,m, a, b, c, d").equals(
                new Event("2 13:21,S,v,w, c, d, d, a")));
        //Assert False on different data type
        assertFalse(
                new Event("1 00:00,C,i,m, a, b, c, d").equals(
                "This will not equal"));
    }
    
    @Test
    public void listEqualTest(){
        List<Event> l1 = Arrays.asList(new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a, b, c, f"));
        
        List<Event> eql1 = Arrays.asList(new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a, b, c, f")); 
        
        List<Event> eql1_rearanged = Arrays.asList(new Event("1 00:00,C,i,m, a, b, c, f"),
                new Event("1 00:00,C,i,m, a, b, c, d"));
        
        List<Event> l2 = Arrays.asList(new Event("1 00:01,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a, b, c, d"));
       
        List<Event> diffLenTol1 = Arrays.asList(new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a, b, c, d"),new Event("1 00:00,C,i,m, a, b, c, d"));
        
        List<Event> eql2_rearanged = Arrays.asList(new Event("1 00:00,C,i,m, a, b, c, d"),
                                new Event("1 00:01,C,i,m, a, b, c, d"));
        
        //This tests two equivalent lists and therefore should be true
        assertTrue(Event.listEqual(l1,eql1));
        //This tests two similar lists of varying length, which results in a false assertion
        assertFalse(Event.listEqual(l1, diffLenTol1));
        //This test tests two differing lists and should result in false
        assertFalse(Event.listEqual(l1, l2));
        //This test tests whether the equivalence works if two things in the same time-stamp are
        //rearranged and we expect true
        assertTrue(Event.listEqual(l1, eql1_rearanged));
        //This test should return True as the two lists are the same, however this is not the
        //case due to the non-chronologically ordered time-stamps in eql2_rearranged
        assertFalse(Event.listEqual(l2, eql2_rearanged));
    }
    
}
