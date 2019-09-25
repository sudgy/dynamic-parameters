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
import org.scijava.Priority;
import org.scijava.plugin.AbstractRichPlugin;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;
import org.scijava.prefs.PrefService;

public class PluginParameterTest {
    @Test public void testSingle()
    {
        Context context = new Context(PluginService.class, PrefService.class);
        PluginParameter<TestPluginType2> param
            = new PluginParameter<>("", TestPluginType2.class);
        context.inject(param);
        param.initialize();

        TestDialog dialog = new TestDialog();
        param.addToDialog(dialog);
        try {
            dialog.getString(0);
            assertTrue(false, "PluginParameter should not give a choice when "
                + "there is only one thing to choose from.");
        }
        catch (IndexOutOfBoundsException e) {}
    }
    @Test public void testMulti()
    {
        Context context = new Context(PluginService.class, PrefService.class);
        PluginParameter<TestPluginType1> param
            = new PluginParameter<>("", TestPluginType1.class);
        context.inject(param);
        param.setEnabled(TestPlugin1.class, true);
        param.initialize();

        TestDialog dialog = new TestDialog();
        param.addToDialog(dialog);
        assertEquals(dialog.getString(0).value, "2", "PluginParameter should "
            + "start on the highest-priority plugin.");

        dialog.getString(0).value = "1";
        param.readFromDialog();
        assertTrue(param.getValue() != null, "PluginParameter should find all "
            + "plugins of the given type.");

        dialog.getString(0).value = "0";
        param.readFromDialog();
        assertTrue(param.getValue() == null, "An incorrect value in "
             + "PluginParameter should cause a null value.");
    }
    @Test public void testSubParam()
    {
        Context context = new Context(PluginService.class, PrefService.class);
        PluginParameter<TestPluginType1> param
            = new PluginParameter<>("", TestPluginType1.class);
        context.inject(param);
        param.setEnabled(TestPlugin1.class, true);
        param.initialize();
        param.refreshVisibility();

        TestDialog dialog = new TestDialog();
        param.addToDialog(dialog);
        assertEquals(3, dialog.getInteger(0).value.intValue(),
            "PluginParameter should add the sub parameter.");
        try {
            dialog.getDouble(0);
            assertTrue(false, "PluginParameter should not add a non-visible "
                + "sub parameter.");
        }
        catch (IndexOutOfBoundsException e) {}

        dialog.getString(0).value = "1";
        param.readFromDialog();
        assertTrue(param.visibilityChanged(), "Changing the selected parameter"
            + " should cause visibilityChanged() to be true.");
        param.refreshVisibility();

        dialog = new TestDialog();
        param.addToDialog(dialog);
        assertEquals(4.0, dialog.getDouble(0).value.doubleValue(),
            "PluginParameter should add the sub parameter after it changed.");
        try {
            dialog.getInteger(0);
            assertTrue(false, "PluginParameter should not add a non-visible "
                + "sub parameter after it changed.");
        }
        catch (IndexOutOfBoundsException e) {}

        dialog.getString(0).value = "4";
        param.readFromDialog();
        assertTrue(param.getValue() instanceof TestPlugin4, "PluginParameter "
            + "should work correctly even if the current plugin has no "
            + "parameter.");
    }
    @Test public void testEnabled()
    {
        Context context = new Context(PluginService.class, PrefService.class);
        PluginParameter<TestPluginType1> param
            = new PluginParameter<>("", TestPluginType1.class);
        context.inject(param);
        param.setEnabled(TestPlugin1.class, false);
        param.initialize();
        TestDialog dialog = new TestDialog();
        param.addToDialog(dialog);
        dialog.getString(0).value = "1";
        param.readFromDialog();
        assertTrue(param.getValue() == null);

        param = new PluginParameter<>("", TestPluginType1.class);
        context.inject(param);
        param.setEnabled(TestPlugin1.class, true);
        param.initialize();
        dialog = new TestDialog();
        param.addToDialog(dialog);
        dialog.getString(0).value = "1";
        param.readFromDialog();
        assertTrue(param.getValue() != null);
    }
}
