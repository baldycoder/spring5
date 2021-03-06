package org.springframework.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.MissingRequiredPropertiesException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.demo.aware.RoleSonAware;
import org.springframework.demo.config.MyConfig;
import org.springframework.demo.config.MyPropertySource;
import org.springframework.demo.custom.editor.People;
import org.springframework.demo.entity.MyEntity;
import org.springframework.demo.entity.Person;
import org.springframework.demo.entity.User;
import org.springframework.demo.factoryMethod.Student;
import org.springframework.demo.override.MyClassPathXmlApplicationContext;
import org.springframework.demo.tag.MLX;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ：a123145
 * @date ：Created in 2022/5/20 18:46
 * @description：学习Spring源码测试类
 * @modified By：`
 * @version: 1.0
 */

public class MyTest {
	/**
	 * 测试静态工厂，非静态工厂。debug跟交易
	 */
	@Test
	void factoryMethodTest(){
		BeanFactory beanFactory = new ClassPathXmlApplicationContext("META-INF/spring.xml");
		Student student = beanFactory.getBean("student", Student.class);
		System.out.println(student);
		Student student2 = beanFactory.getBean("student2", Student.class);
		System.out.println(student2);
	}

	@Test
	void componentParseTest(){
		BeanFactory beanFactory = new ClassPathXmlApplicationContext("META-INF/spring.xml");
		MyEntity myEntity = beanFactory.getBean(MyEntity.class);
		MyPropertySource myPropertySource = beanFactory.getBean(MyPropertySource.class);
		//User user = beanFactory.getBean(User.class);
		User lucy = beanFactory.getBean("lucy", User.class);
		System.out.println(myEntity);
		System.out.println(myPropertySource);
		System.out.println(lucy);
	}

	/**
	 * 自定义属性解析器
	 * 1、xml中配置的CustomEditorConfigurer 其实是一个BeanFactoryPostProcessor
	 * 2、实际是在invokeBeanFactoryPostProcessor中调用执行的
	 * 3、前序步骤需要通过PropertyEditorRegistry 绑定属性与Editor关系
	 */
	@Test
	void customPropertyEditorTest(){
		BeanFactory beanFactory = new ClassPathXmlApplicationContext("META-INF/spring.xml");
		//实时加载
		People people = beanFactory.getBean(People.class);
		//People{name='老毛', address=Address{province='北京市', city='东城区', town='和平里'}}
		System.out.println(people);

	}

	@Test
	void sourceCodeEntry(){
		BeanFactory beanFactory = new ClassPathXmlApplicationContext("META-INF/spring.xml");
		//实时加载
		User user = (User)beanFactory.getBean("user");
		MLX ouba = (MLX)beanFactory.getBean("ouba");
		System.out.println("ouba:"+ouba);
		System.out.println("实时查找：" + user);
	}

	//测试重写扩展槽 initPropertySources方法
	@Test
	void testInitPropertySources(){
		try {
			BeanFactory beanFactory = new MyClassPathXmlApplicationContext("META-INF/spring.xml");
		}catch (MissingRequiredPropertiesException e){
			System.out.println(e.getMessage());
			System.out.println(e.getMissingRequiredProperties());
			System.out.println(e.getCause());
			System.out.println(e.getLocalizedMessage());
			System.out.println("调用MyClassPathXmlApplicationContext失败");
		}
	}

	@Test
	void testUpdateManualSingletonNames(){
		//满足Set部不为空的集合，执行clear方法
		Set<String> strings = updateManualSingletonNames(Set::clear, set -> !set.isEmpty());
		System.out.println(strings);
	}
	@Test
	void testIgnoreDependencyInterface(){
		BeanFactory beanFactory = new ClassPathXmlApplicationContext("META-INF/spring.xml");

		Person person = beanFactory.getBean("person", Person.class);
		System.out.println("person:" + person);
	}

	//满足Predicate条件的集合，执行Consumer的方法
	public static Set<String> updateManualSingletonNames(Consumer<Set<String>> action, Predicate<Set<String>> condition) {
		Set<String> manualSingletonNames = new LinkedHashSet<>(3);
		List<String> list = Stream.of("1", "2", "3").collect(Collectors.toList());
		manualSingletonNames.addAll(list);

		if (condition.test(manualSingletonNames)) {
			Set<String> updatedSingletons = new LinkedHashSet<>(manualSingletonNames);
			action.accept(updatedSingletons);
			manualSingletonNames = updatedSingletons;
		}
		return manualSingletonNames;
	}

}
