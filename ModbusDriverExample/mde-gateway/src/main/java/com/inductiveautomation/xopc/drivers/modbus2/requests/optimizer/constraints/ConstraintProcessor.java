/*******************************************************************************
 * INDUCTIVE AUTOMATION PUBLIC LICENSE 
 * 
 * BY DOWNLOADING, INSTALLING AND/OR IMPLEMENTING THIS SOFTWARE YOU AGREE 
 * TO THE FOLLOWING LICENSE: 
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are 
 * met: 
 * 
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions in 
 * binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or 
 * other materials provided with the distribution. Neither the name of 
 * Inductive Automation nor the names of its contributors may be used to 
 * endorse or promote products derived from this software without specific 
 * prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS 
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED 
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL INDUCTIVE 
 * AUTOMATION BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * LICENSEE SHALL INDEMNIFY, DEFEND AND HOLD HARMLESS INDUCTIVE AUTOMATION, 
 * ITS SHAREHOLDERS, OFFICERS, DIRECTORS, EMPLOYEES, AGENTS, ATTORNEYS, 
 * SUCCESSORS AND ASSIGNS FROM ANY AND ALL claims, debts, liabilities, 
 * demands, suits and causes of action, known or unknown, in any way 
 * relating to the LICENSEE'S USE OF THE SOFTWARE IN ANY FORM OR MANNER
 * WHATSOEVER AND FOR any act or omission related thereto.
 ******************************************************************************/
package com.inductiveautomation.xopc.drivers.modbus2.requests.optimizer.constraints;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;

public class ConstraintProcessor<T> {

	/**
	 * Takes a List<T> and applies the first Constraint<T> and then each of the following
	 * Constraint<T> in order to the results of the previous Constraint<T>.
	 * 
	 * For example: You have a List<Integer> containing [0..9]. The first constraint splits things
	 * by whether they were less than 5 or not. The second constraint splits things by whether they
	 * are even or odd.
	 * 
	 * <pre>
	 * Initial		  After first constraint				After second constraint
	 * 
	 * [0..9] 	-->   [ [0..4], [5..9] ] 	-->    [ [0, 2, 4], [1, 3], [5, 7, 9], [6, 8] ]
	 * </pre>
	 * 
	 * @param items
	 *            The items to constrain.
	 * @param constraints
	 *            The constraints to place on the items.
	 * 
	 * @return A List<List<T>> representing the original items split into groups by the constraints
	 *         given.
	 */
	public List<List<T>> process(List<T> items, List<Constraint<T>> constraints) {
		List<List<T>> processed = new ArrayList<List<T>>();

		if (constraints.isEmpty()) {
			processed.add(items);
			return processed;
		}

		Iterator<Constraint<T>> iter = constraints.iterator();

		Constraint<T> first = iter.next();
		processed = first.constrain(items);

		while (iter.hasNext()) {
			List<List<T>> constrained = new ArrayList<List<T>>();
			Constraint<T> constraint = iter.next();

			for (List<T> toConstrain : processed) {
				constrained.addAll(constraint.constrain(toConstrain));
			}

			processed = constrained;
		}

		return processed;
	}

	public List<List<T>> process(List<T> items, Constraint<T>... constraints) {
		List<List<T>> processed = new ArrayList<List<T>>();

		if (constraints.length == 0) {
			processed.add(items);
			return processed;
		}

		Iterator<Constraint<T>> iter = Iterators.forArray(constraints);

		Constraint<T> first = iter.next();
		processed = first.constrain(items);

		while (iter.hasNext()) {
			List<List<T>> constrained = new ArrayList<List<T>>();
			Constraint<T> constraint = iter.next();

			for (List<T> toConstrain : processed) {
				constrained.addAll(constraint.constrain(toConstrain));
			}

			processed = constrained;
		}

		return processed;
	}

}
