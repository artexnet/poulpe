/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.web.controller.zkutils;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.zk.ui.event.EventQueues;

import java.util.Map;

/**
 * This class represent the wrapper around {@link org.zkoss.bind.BindUtils}. BindUtils is utility which needs to help
 * developer to use zk bind, but because BindUtils contains static methods, it do our code hard to testing. This wrapper
 * needs to testing, it can be easy mocked with mockito or other mock framwerks.
 *
 * @author Vermut
 */
public class BindUtilsWrapper {

    /**
     * Post a global command to corresponding event queue
     *
     * @param queueName  the queue name, null for default queue name
     * @param queueScope the queue scope, null for default queue scope (i.e. {@link EventQueues#DESKTOP})
     * @param cmdName    the global command name
     * @param args       arguments, could get the data in command method by {@link BindingParam}
     * @see org.zkoss.bind.BindUtils#postGlobalCommand(String, String, String, Map)
     */
    @SuppressWarnings("static-method")
    public void postGlobalCommand(String queueName, String queueScope, String cmdName, Map<String, Object> args) {
        BindUtils.postGlobalCommand(queueName, queueScope, cmdName, args);
    }

    /**
     * Post a notify change to corresponding event queue to notify a bean's property changing
     *
     * @param queueName  the queue name, null for default queue name
     * @param queueScope the queue scope, null for default queue scope (i.e. {@link EventQueues#DESKTOP})
     * @param bean       the bean instance
     * @param property   the property name of bean
     * @see org.zkoss.bind.BindUtils#postNotifyChange(String, String, Object, String)
     */
    @SuppressWarnings("static-method")
    public void postNotifyChange(String queueName, String queueScope, Object bean, String property) {
        BindUtils.postNotifyChange(queueName, queueScope, bean, property);
    }

}
