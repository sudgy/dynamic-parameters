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

public class DoubleParameterTest {
    @Test public void test_base()
    {
        DoubleParameter param = new DoubleParameter(1.0, "");
        assertEquals(param.get_value().doubleValue(), 1.0, "DoubleParameter should start with its default value.");
    }
    @Test public void test_bounds()
    {
        TestDialog dialog = new TestDialog();
        DoubleParameter param = new DoubleParameter(1.5, "");
        param.add_to_dialog(dialog); // Bounds only works through the dialog
        param.set_bounds(0.0, 1.0);
        assertTrue(param.get_error() != null, "After setting bounds such that the value is outside the bounds, there should be an error.");
        param.set_bounds(0.0, 2.0);
        assertTrue(param.get_error() == null, "After setting bounds such that the value is inside the bounds, there should be no error.");
    }
    @Test public void test_dialog()
    {
        TestDialog dialog = new TestDialog();
        DoubleParameter param = new DoubleParameter(1.0, "");
        param.add_to_dialog(dialog);
        dialog.get_double(0).value = 1.5;
        param.read_from_dialog();
        assertEquals(param.get_value().doubleValue(), 1.5, "DoubleParameter should read from dialogs correctly.");

        param.set_bounds(0.0, 2.0);

        dialog.get_double(0).value = 2.5;
        param.read_from_dialog();
        assertTrue(param.get_error() != null, "After inputting a number outside the range, there should be an error.");
        dialog.get_double(0).value = 1.5;
        param.read_from_dialog();
        assertTrue(param.get_error() == null, "After being out of range, going back inside it should make no error.");

        dialog.get_double(0).value = null;
        param.read_from_dialog();
        assertTrue(param.get_error() != null, "After inputting an invalid number, there should be an error.");
        dialog.get_double(0).value = 1.5;
        param.read_from_dialog();
        assertTrue(param.get_error() == null, "After inputting a valid number, there not should be an error.");
    }
    @Test public void test_prefs()
    {
        Context context = new Context(PrefService.class);
        TestDialog dialog = new TestDialog();
        DoubleParameter param1 = new DoubleParameter(1.0, "");
        DoubleParameter param2 = new DoubleParameter(2.0, "");
        context.inject(param1);
        context.inject(param2);
        param1.add_to_dialog(dialog); // Bounds only works through the dialog
        param2.add_to_dialog(dialog); // Ditto
        param1.save_to_prefs(getClass(), "a");
        param2.save_to_prefs(getClass(), "b");
        param2.read_from_prefs(getClass(), "a");
        assertEquals(param2.get_value().doubleValue(), 1.0, "DoubleParameter should read from prefs correctly.");

        param1.set_bounds(0.0, 1.5);
        param1.read_from_prefs(getClass(), "b");
        assertTrue(param1.get_error() != null, "After reading an invalid number from prefs, there should be an error.");
        param1.read_from_prefs(getClass(), "a");
        assertTrue(param1.get_error() == null, "After reading a valid number from prefs, there should be no error.");
    }
}
