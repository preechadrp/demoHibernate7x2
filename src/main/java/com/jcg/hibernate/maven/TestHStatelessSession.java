package com.jcg.hibernate.maven;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.persistence.Tuple;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;

import com.jcg.hibernate.maven.model.User;

public class TestHStatelessSession {

	public static void main(String[] args) {
		System.out.println("demoHibernate");
	}

	public static void testInsert() throws Exception {
		// ทดสอบทำเป็น class เอง
		System.out.println("======testInsert()");
		
		// ทดสอบ insert
		try (HStatelessSession hss = HStatelessSession.sessionDb();) {
			hss.begintrans();

			 // สร้าง ValidatorFactory และ Validator
	        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	        Validator validator = factory.getValidator();

			for (int i = 301; i <= 305; i++) {
				User userObj = new User()
						.setUserid(i)
						.setUsername("Editor " + i)
						.setBirthDay(java.time.LocalDate.now())
						.setCreatedBy("Administrator")
						.setCreatedDate(java.time.OffsetDateTime.now());

				// ตรวจสอบวัตถุ
		        Set<ConstraintViolation<User>> violations = validator.validate(userObj);
		        
		        // แสดงผลข้อผิดพลาด
		        if (!violations.isEmpty()) {
		            for (ConstraintViolation<User> violation : violations) {
		                System.out.println("Validate Msg : " + violation.getMessage());
		            }
		        } else {
		            System.out.println("ข้อมูลถูกต้อง");
		            hss.insert(userObj);// จะเป็นการ insert ลงฐานจริงๆ แต่กรณีนี้อยู่ในการทำ begintransaction
		        }
				
			}

			System.out.println("\n.......Records Saved Successfully To The Database.......\n");

			testReadInTrans(hss);// อ่านมาแสดงก่อน commite ได้เมื่อใช้ StatelessSession //test ok 29/6/66

			hss.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void testReadInTrans(HStatelessSession hss) throws Exception {
		System.out.println("\n........test read before commit........\n");
		try (Stream<User> rst = hss.getSession().createSelectionQuery("from User", User.class).getResultStream();) {
			rst.forEach(e -> {
				System.out.println(e.getUserid() + " : " + e.getUsername());
			});
		}
	}

	public static void testRead() throws Exception {
		System.out.println("======testRead()");
		// ทดสอบอ่านข้อมูล
		try (HStatelessSession hss = HStatelessSession.sessionDb();) {

			// test HQL/JPQL ผ่านแล้ว
			System.out.println("=== createQuery , getResultStream ===");
			try (Stream<User> rst = hss.getSession().createSelectionQuery("from User", User.class).getResultStream();) {
				rst.forEach(e -> {
					System.out.println(e.getUserid() + " : " + e.getUsername());
				});
			}

			// test native query ผ่านแล้ว
			System.out.println("=== createNativeQuery , getResultStream ===");
			try (Stream<User> rst = hss.getSession().createNativeQuery("select * from user_table", User.class).getResultStream();) {
				rst.forEach(u -> {
					System.out.println(u.getUserid() + " : " + u.getUsername());
				});
			}

			// test native query ผ่านแล้ว + ScrollMode.FORWARD_ONLY
			System.out.println("=== createNativeQuery , ScrollMode.FORWARD_ONLY ===");
			try (ScrollableResults<User> rst = hss.getSession().createNativeQuery("select * from user_table", User.class).scroll(ScrollMode.FORWARD_ONLY)) {
				while (rst.next()) {
					System.out.println(rst.get().getUserid());
			    }
			}
			
			System.out.println("=== start read to tuple ===");
			System.out.println("=== tuple 1 : อ่านเข้า tuple ใช้ NativeQuery ===");
			try (Stream<Tuple> rst = hss.getSession().createNativeQuery("select * from user_table", Tuple.class).getResultStream();) {
				rst.forEach((record) -> {
					System.out.println(record.get("user_id", Integer.class) + "," + record.get("user_name", String.class));
				});
			} catch (Exception e2) {
				System.out.println("=== tuple error 1 ===");
				e2.printStackTrace();
			}
			
			System.out.println("=== tuple 2 : อ่านเข้า tuple ใช้ hqlString และดึงบางฟิลด์ และอ่านเข้า List<Map<String,Object>>  ===");
			//ต้องใส่ as ต่อจากฟิลด์ด้วย เช่น as userid เป็นต้น
			try (Stream<Tuple> rst = hss.getSession().createSelectionQuery("select u.userid as userid, u.username as username from User u", Tuple.class).getResultStream();) {
				//แสดง
				//rst.forEach((record) -> {
				//	System.out.println(record.get("userid", Integer.class) + "," + record.get("username", String.class));
				//});
				
				//นำเข้า map
				List<Map<String,Object>> list = rst.map(tp -> {
					Map<String,Object> map = new HashMap<>();
		            map.put("userid", tp.get("userid"));
		            map.put("username", tp.get("username"));
		            return map;
				}).toList();
				if (list.size() > 0) {
					for (Map<String, Object> map : list) {
						System.out.println(map.get("userid") + "," + map.get("username"));
					}
				}
				
			} catch (Exception e2) {
				System.out.println("=== tuple error 2===");
				e2.printStackTrace();
			}
			
			System.out.println("=== end read to tuple ===");

		}

	}

	public static void testUpdate() throws Exception {
		System.out.println("======testUpdate()");
		// ทดสอบปรับปรุงข้อมูล
		try (HStatelessSession hss = HStatelessSession.sessionDb();) {
			hss.begintrans();

			// แบบที่ 1 ค้นหาด้วย key // test ผ่านแล้ว
			User us1 = (User) hss.getSession().get(User.class, 304);
			us1.setUsername("user 304 x");
			hss.update(us1);

			// แบบที่ 1 // test HQL/JPQL + parameter ผ่านแล้ว
			Query<User> qr1 = hss.getSession().createQuery("from User where userid =:userid ", User.class);
			qr1.setParameter("userid", 305);
			try (Stream<User> rst = qr1.getResultStream();) {
				rst.forEach(e -> {
					e.setUsername("user 305x");
					hss.update(e);
				});
			}

			hss.commit();
		}

	}

	public static void testClone() throws Exception {
		System.out.println("======testClone()");
		// ทดสอบ clone
		try (HStatelessSession hss = HStatelessSession.sessionDb();) {
			hss.begintrans();

			// test HQL/JPQL ผ่านแล้ว
			Query<User> qr1 = hss.getSession().createQuery("from User where userid =:userid ", User.class);
			qr1.setParameter("userid", 305);
			try (Stream<User> rst = qr1.getResultStream();) {
				rst.forEach(e -> {
					e.setUserid(306);//เปลี่ยน key
					e.setUsername("clone from user 305x");
					hss.insert(e);
					
					e.setUserid(307);//เปลี่ยน key
					e.setUsername("clone from user 305x");
					hss.insert(e);
				});
			}

			hss.commit();
		}

	}

	public static void testDelete() throws Exception {
		System.out.println("======testDelete()");
		// ทดสอบ ลบ
		try (HStatelessSession hss = HStatelessSession.sessionDb();) {
			hss.begintrans();

			// แบบที่ 1 // test ผ่านแล้ว
			User us1 = (User) hss.getSession().get(User.class, 306);
			hss.delete(us1);
			
			// แบบที่ 2 //test HQL/JPQL ผ่านแล้ว
			Query<User> qr1 = hss.getSession().createQuery("from User where userid =:userid ", User.class);
			qr1.setParameter("userid", 307);
			try (Stream<User> rst = qr1.getResultStream();) {
				rst.forEach(e -> {
					hss.delete(e);
				});
			}

			hss.commit();
		}

	}

	public static void testdeleteALL() throws Exception {
		System.out.println("======testdeleteALL()");

		try (HStatelessSession hss = HStatelessSession.sessionDb();) {
			hss.begintrans();

			//แบบ hql
			int eff = hss.getSession().createMutationQuery("delete from User").executeUpdate();
			//แบบมีเงื่อนไข
			//int eff = hss.getSession().createMutationQuery("delete from User u where u.createdBy = :by")
			//	       .setParameter("by", "system").executeUpdate();
			
			//แบบ native sql
			//int eff = hss.getSession().createNativeMutationQuery("delete from user_table").executeUpdate();

			System.out.println("eff : "+eff+" records");

			hss.commit();
		}

	}
	
}
