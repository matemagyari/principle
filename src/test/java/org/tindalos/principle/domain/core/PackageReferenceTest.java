package org.tindalos.principle.domain.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PackageReferenceTest {

	private PackageReferenceScala testObj = ref("aa.bb.cc");
	
	@Test
	public void child() {
		assertMatches("aa.bb.cc.dd", testObj.child("dd")); 
	}
	@Test
	public void createChild() {
		assertEquals("aa.bb.cc.dd", testObj.createChild("dd")); 
	}
	
	@Test
	public void firstPartOfRelativeNameTo() {
		assertEquals("bb", testObj.firstPartOfRelativeNameTo(ref("aa"))); 
	}
	@Test
	public void isDescendantOf() {
		assertTrue(testObj.isDescendantOf(ref("aa.bb"))); 
		assertTrue(testObj.isDescendantOf(ref("aa"))); 
		assertFalse(testObj.isDescendantOf(ref("aa.bbb"))); 
		assertFalse(testObj.isDescendantOf(testObj)); 
		assertFalse(testObj.isDescendantOf(ref("aa.bb.cc.dd"))); 
		assertFalse(testObj.isDescendantOf(ref("aa.bb.ccc"))); 
	}
	@Test
	public void isDirectParentOf() {
		assertTrue(testObj.isDirectParentOf(ref("aa.bb.cc.dd"))); 
		assertFalse(testObj.isDirectParentOf(ref("aa.bb.cc.dd.ee"))); 
		assertFalse(testObj.isDirectParentOf(testObj)); 
	}
	
	@Test
	public void isNotAnAncestorOf() {
		assertTrue(testObj.isNotAnAncestorOf(ref("aa.bb.ccc"))); 
		assertTrue(testObj.isNotAnAncestorOf(ref("xxxx"))); 
		assertTrue(testObj.isNotAnAncestorOf(testObj)); 
		assertTrue(testObj.isNotAnAncestorOf(ref("aa"))); 

		assertFalse(testObj.isNotAnAncestorOf(ref("aa.bb.cc.dd.ee"))); 
		assertFalse(testObj.isNotAnAncestorOf(ref("aa.bb.cc.dd")));
		
	}
	
	@Test
	public void oneContainsAnother() {
		assertFalse(testObj.oneContainsAnother(ref("aa.bb.ccc"))); 
		assertFalse(ref("aa.bb.ccc").oneContainsAnother(testObj)); 
		
		assertTrue(testObj.oneContainsAnother(ref("aa"))); 
		assertTrue(testObj.oneContainsAnother(ref("aa.bb"))); 
		assertTrue(testObj.oneContainsAnother(ref("aa.bb.cc.dd"))); 
		assertTrue(ref("aa").oneContainsAnother(testObj)); 
		assertTrue(ref("aa.bb").oneContainsAnother(testObj)); 
		assertTrue(ref("aa.bb.cc.dd").oneContainsAnother(testObj)); 
		assertTrue(testObj.oneContainsAnother(testObj)); 
	}
	
	
	@Test
	public void pointsInside() {
		assertTrue(testObj.pointsInside(ref("aa"))); 
		assertTrue(testObj.pointsInside(ref("aa.bb"))); 

		assertFalse(testObj.pointsInside(testObj));
		assertFalse(testObj.pointsInside(ref("aa.bbb"))); 
	}
	
	@Test
	public void pointsToThatOrInside() {
		assertTrue(testObj.pointsToThatOrInside(ref("aa"))); 
		assertTrue(testObj.pointsToThatOrInside(ref("aa.bb"))); 
		
		assertTrue(testObj.pointsToThatOrInside(testObj));
		assertFalse(testObj.pointsToThatOrInside(ref("aa.bbb"))); 
	}
	
	@Test
	public void relativeNameTo() {
		assertEquals("bb.cc", testObj.relativeNameTo(ref("aa"))); 
		assertEquals("cc", testObj.relativeNameTo(ref("aa.bb"))); 
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void invalidRelativeNameTo() {
		testObj.relativeNameTo(ref("aa.b")); 
	}
	
	@Test
	public void startsWith() {
		assertTrue(testObj.startsWith("aa")); 
		assertTrue(testObj.startsWith("aa.")); 
		assertTrue(testObj.startsWith("aa.bb")); 
		assertTrue(testObj.startsWith("aa.bb.cc")); 
		assertFalse(testObj.startsWith("aa.bb.cc.")); 
		assertFalse(testObj.startsWith("aa.bb.dd")); 
		assertFalse(testObj.startsWith("xxxx")); 
	}
	
	@Test
	public void compareTo() {
		
		assertEquals(0, ref("aa.bb").compareTo(ref("aa.bb")));
		assertEquals(-1, ref("aa.bb.cc").compareTo(ref("aa.bb.dd")));
		assertEquals(1, ref("aa.bb.dd").compareTo(ref("aa.bb.cc")));
		
	}
	
	private static void assertMatches(String expected, PackageReferenceScala actual) {
		assertEquals(ref(expected), actual);
	}
	
	private static PackageReferenceScala ref(String str) {
	    return new PackageReferenceScala(str);
	}

}
