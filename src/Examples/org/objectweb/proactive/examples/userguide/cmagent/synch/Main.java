/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */

package org.objectweb.proactive.examples.userguide.cmagent.synch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PADeployment;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.examples.userguide.cmagent.migration.CMAgentMigrator;
import org.objectweb.proactive.examples.userguide.cmagent.simple.State;
import org.objectweb.proactive.ActiveObjectCreationException;


public class Main {
    private static VirtualNode deploy(String descriptor) {
        ProActiveDescriptor pad;
        VirtualNode vn;
        try {
            //create object representation of the deployment file
            pad = PADeployment.getProactiveDescriptor(descriptor);
            //active all Virtual Nodes
            pad.activateMappings();
            //get the first Node available in the first Virtual Node 
            //specified in the descriptor file
            vn = pad.getVirtualNodes()[0];
            return vn;
        } catch (NodeException nodeExcep) {
            System.err.println(nodeExcep.getMessage());
        } catch (ProActiveException proExcep) {
            System.err.println(proExcep.getMessage());
        }
        return null;
    }

    public static void main(String args[]) {
            String currentState = new String();
            VirtualNode vn = deploy(args[0]);
            Vector<CMAgentChained> agents = new Vector<CMAgentChained>();
            //create the active objects
            try {
                //create a collection of active objects
                for (Node node : vn.getNodes()) {
            		CMAgentChained ao = (CMAgentChained) PAActiveObject.newActive(CMAgentChained.class.getName(),
            				null);
            		agents.add(ao);
            			
            		//connect to the neighbour
            		int size = agents.size();
            		if (size > 1) {
            			CMAgentChained lastAgent = agents.get(size-1);
            			CMAgentChained previousAgent = agents.get(size-2);
            			lastAgent.setPreviousNeighbour(previousAgent);
            		}
            	}
            	//start chained call            	
            	Vector <State> states = agents.get(agents.size()/2).getAllPreviousStates();
            	for (State s:states){
            		System.out.println(s.toString());
            	}
            	
            	states = agents.get(agents.size()/2).getAllNextStates();
            	for (State s:states){
            		System.out.println(s.toString());
            	}
            	
			} catch (ActiveObjectCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
}