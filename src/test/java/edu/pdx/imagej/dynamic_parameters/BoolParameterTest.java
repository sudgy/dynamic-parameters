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
    @Test public void test_base()
    {
        BoolParameter param = new BoolParameter("", true);
        assertEquals(param.get_value(), true, "BoolParameter should start with its default value, true.");
        param = new BoolParameter("", false);
        assertEquals(param.get_value(), false, "BoolParameter should start with its default value, false.");
    }
    @Test public void test_dialog()
    {
        TestDialog dialog = new TestDialog();
        BoolParameter param = new BoolParameter("", true);
        param.add_to_dialog(dialog);
        dialog.get_boolean(0).value = false;
        param.read_from_dialog();
        assertEquals(param.get_value(), false, "BoolParameter should read from dialogs correctly.");
    }
    @Test public void test_prefs()
    {
        Context context = new Context(PrefService.class);
        BoolParameter param1 = new BoolParameter("", true);
        BoolParameter param2 = new BoolParameter("", false);
        context.inject(param1);
        context.inject(param2);
        param1.save_to_prefs(getClass(), "a");
        param2.read_from_prefs(getClass(), "b");
        assertEquals(param2.get_value(), false, "BoolParameter.read_from_prefs should not read prefs with a different label.");
        param2.read_from_prefs(getClass(), "a");
        assertEquals(param2.get_value(), true, "BoolParameter.read_from_prefs should work.");
    }
}
