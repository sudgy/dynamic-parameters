/* Copyright (C) 2019 Portland State University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * For any questions regarding the license, please contact the Free Software
 * Foundation.  For any other questions regarding this program, please contact
 * David Cohoe at dcohoe@pdx.edu.
 */

package edu.pdx.imagej.dynamic_parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import org.scijava.Context;
import org.scijava.prefs.PrefService;

public class BoolParameterTest {
    @Test public void testBase()
    {
        BoolParameter param = new BoolParameter("", true);
        assertEquals(param.getValue(), true, "BoolParameter should start with its default value, true.");
        param = new BoolParameter("", false);
        assertEquals(param.getValue(), false, "BoolParameter should start with its default value, false.");
    }
    @Test public void testDialog()
    {
        TestDialog dialog = new TestDialog();
        BoolParameter param = new BoolParameter("", true);
        param.addToDialog(dialog);
        dialog.getBoolean(0).value = false;
        param.readFromDialog();
        assertEquals(param.getValue(), false, "BoolParameter should read from dialogs correctly.");
    }
    @Test public void testPrefs()
    {
        Context context = new Context(PrefService.class);
        BoolParameter param1 = new BoolParameter("", true);
        BoolParameter param2 = new BoolParameter("", false);
        context.inject(param1);
        context.inject(param2);
        param1.saveToPrefs(getClass(), "a");
        param2.readFromPrefs(getClass(), "b");
        assertEquals(param2.getValue(), false, "BoolParameter.readFromPrefs should not read prefs with a different label.");
        param2.readFromPrefs(getClass(), "a");
        assertEquals(param2.getValue(), true, "BoolParameter.readFromPrefs should work.");
    }
}
