package test;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.jcg.hibernate.maven.TestHSession;

public class ApTestHSession {

	@Test
	@Order(1)
	public void test1() {
		TestHSession.test1(); //test ผ่าน
	}
	
	@Test
	@Order(2)
	public void testInsert() throws Exception {
		TestHSession.testInsert(); //test ผ่าน
	}
	
	@Test
	@Order(3)
	public void testRead() throws Exception {
		TestHSession.testRead(); //test ผ่าน
	}
	
	@Test
	@Order(4)
	public void testUpdate() throws Exception {
		TestHSession.testUpdate(); //test ผ่าน
	}
	
}
