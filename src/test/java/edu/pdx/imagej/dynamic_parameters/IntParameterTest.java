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

public class IntParameterTest {
    @Test public void testBase()
    {
        IntParameter param = new IntParameter(1, "");
        assertEquals(param.getValue().intValue(), 1, "IntParameter should start with its default value.");
    }
    @Test public void testBounds()
    {
        TestDialog dialog = new TestDialog();
        IntParameter param = new IntParameter(2, "");
        param.addToDialog(dialog); // Bounds only works through the dialog
        param.setBounds(0, 1);
        assertTrue(param.getError() != null, "After setting bounds such that the value is outside the bounds, there should be an error.");
        param.setBounds(0, 3);
        assertTrue(param.getError() == null, "After setting bounds such that the value is inside the bounds, there should be no error.");
    }
    @Test public void testDialog()
    {
        TestDialog dialog = new TestDialog();
        IntParameter param = new IntParameter(1, "");
        param.addToDialog(dialog);
        dialog.getInteger(0).value = 2;
        param.readFromDialog();
        assertEquals(param.getValue().intValue(), 2, "IntParameter should read from dialogs correctly.");

        param.setBounds(0, 3);

        dialog.getInteger(0).value = 4;
        param.readFromDialog();
        assertTrue(param.getError() != null, "After inputting a number outside the range, there should be an error.");
        dialog.getInteger(0).value = 2;
        param.readFromDialog();
        assertTrue(param.getError() == null, "After being out of range, going back inside it should make no error.");

        dialog.getInteger(0).value = null;
        param.readFromDialog();
        assertTrue(param.getError() != null, "After inputting an invalid number, there should be an error.");
        dialog.getInteger(0).value = 2;
        param.readFromDialog();
        assertTrue(param.getError() == null, "After inputting a valid number, there should not be an error.");
    }
    @Test public void testPrefs()
    {
        Context context = new Context(PrefService.class);
        TestDialog dialog = new TestDialog();
        IntParameter param1 = new IntParameter(1, "");
        IntParameter param2 = new IntParameter(3, "");
        context.inject(param1);
        context.inject(param2);
        param1.addToDialog(dialog); // Bounds only works through the dialog
        param2.addToDialog(dialog); // Ditto
        param1.saveToPrefs(getClass(), "a");
        param2.saveToPrefs(getClass(), "b");
        param2.readFromPrefs(getClass(), "a");
        assertEquals(param2.getValue().intValue(), 1, "IntParameter should read from prefs correctly.");

        param1.setBounds(0, 2);
        param1.readFromPrefs(getClass(), "b");
        assertTrue(param1.getError() != null, "After reading an invalid number from prefs, there should be an error.");
        param1.readFromPrefs(getClass(), "a");
        assertTrue(param1.getError() == null, "After reading a valid number from prefs, there should be no error.");
    }
}
