/*
 * Copyright 2017 original authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.particleframework.aop.hotswap

import org.particleframework.aop.HotSwappableInterceptedProxy
import org.particleframework.aop.internal.AopAttributes
import org.particleframework.context.BeanContext
import org.particleframework.context.DefaultBeanContext
import spock.lang.Specification

/**
 * @author Graeme Rocher
 * @since 1.0
 */
class ProxyHotswapSpec extends Specification {

    void "test AOP setup attributes"() {
        given:
        BeanContext beanContext = new DefaultBeanContext().start()
        def newInstance = new HotswappableProxyingClass()

        when:
        HotswappableProxyingClass foo = beanContext.getBean(HotswappableProxyingClass)
        def attrs = AopAttributes.get(HotswappableProxyingClass, "test", String)
        then:
        foo instanceof HotSwappableInterceptedProxy
        foo.interceptedTarget().getClass() == HotswappableProxyingClass
        foo.test("test") == "Name is changed"
        foo.test2("test") == "Name is test"
        foo.interceptedTarget().invocationCount == 2

        foo.swap(newInstance)
        foo.interceptedTarget().invocationCount == 0
        foo.interceptedTarget() != foo
        foo.interceptedTarget().is(newInstance)
        AopAttributes.@attributes.get().values().first().values == attrs

        when:
        AopAttributes.remove(HotswappableProxyingClass, "test", String)

        then:
        AopAttributes.@attributes.get() == null
    }
}