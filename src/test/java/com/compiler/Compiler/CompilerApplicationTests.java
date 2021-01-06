package com.compiler.Compiler;

import com.compiler.Compiler.controllers.NewRestController;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;

import java.util.HashMap;

@SpringBootTest
class CompilerApplicationTests {

	@Test
	void contextLoads() {

	}

	@Autowired
	private NewRestController controller;

	@Test
	void restTest(){
		MockHttpSession s = new MockHttpSession();
		final String output =controller.compile(s,"a='ter'");
		Assert.assertEquals("",output);
	}

	@Test
	void restAndReplTest(){
		MockHttpSession s = new MockHttpSession();
		final String output =controller.compile(s,"a='ter':'11'");
		Assert.assertEquals("",output);

		final String output2 = controller.repl(s,":eval a 'ter'");
		Assert.assertEquals("'11'",output2);
	}



}
