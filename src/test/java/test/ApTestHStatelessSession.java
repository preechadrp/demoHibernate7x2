package test;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.jcg.hibernate.maven.TestHStatelessSession;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApTestHStatelessSession {
		
	@Test
	@Order(1)
	public void F00testInsert() throws Exception {
		TestHStatelessSession.testdeleteALL(); //test ผ่าน 29/6/66
	}
	
	@Test
	@Order(2)
	public void F01testInsert() throws Exception {
		TestHStatelessSession.testInsert(); //test ผ่าน 29/6/66
	}
	
	@Test
	@Order(3)
	public void F02testRead() throws Exception {
		TestHStatelessSession.testRead(); //test ผ่าน 29/6/66
	}
	
	@Test
	@Order(4)
	public void F03testUpdate() throws Exception {
		TestHStatelessSession.testUpdate();//test ผ่าน 29/6/66 
	}
	
	@Test
	@Order(5)
	public void F04testClone() throws Exception {
		TestHStatelessSession.testClone();//test ผ่าน 29/6/66 
	}
	
	@Test
	@Order(6)
	public void F05testDelete() throws Exception {
		TestHStatelessSession.testDelete();//test ผ่าน 29/6/66 
	}

}
