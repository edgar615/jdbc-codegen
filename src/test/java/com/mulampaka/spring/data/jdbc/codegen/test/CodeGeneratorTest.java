/**
 * 
 * Copyright 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author Kalyan Mulampaka
 */
package com.mulampaka.spring.data.jdbc.codegen.test;

import com.mulampaka.spring.data.jdbc.codegen.CodeGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:dao.xml"})
////@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
//@TransactionConfiguration(defaultRollback = true)
//@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
//        TransactionalTestExecutionListener.class})
public class CodeGeneratorTest
{

	final static Logger logger = LoggerFactory.getLogger(CodeGeneratorTest.class);

	private CodeGenerator generator;

	public CodeGeneratorTest ()
	{

	}

	@Test
	public void generate () throws Exception
	{
		generator = new CodeGenerator ();
		generator.setPropertiesFile ("src/test/resources/codegenerator-test.properties");
        generator.generate ();
	}


}
