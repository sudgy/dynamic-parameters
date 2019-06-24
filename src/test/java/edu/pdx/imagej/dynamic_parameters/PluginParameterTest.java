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
    @Test public void test_single()
    {
        Context context = new Context(PluginService.class, PrefService.class);
        PluginParameter<TestPluginType2> param
            = new PluginParameter<>("", TestPluginType2.class);
        context.inject(param);
        param.initialize();

        TestDialog dialog = new TestDialog();
        param.add_to_dialog(dialog);
        try {
            dialog.get_string(0);
            assertTrue(false, "PluginParameter should not give a choice when "
                + "there is only one thing to choose from.");
        }
        catch (IndexOutOfBoundsException e) {}
    }
    @Test public void test_multi()
    {
        Context context = new Context(PluginService.class, PrefService.class);
        PluginParameter<TestPluginType1> param
            = new PluginParameter<>("", TestPluginType1.class);
        context.inject(param);
        param.set_enabled(TestPlugin1.class, true);
        param.initialize();

        TestDialog dialog = new TestDialog();
        param.add_to_dialog(dialog);
        assertEquals(dialog.get_string(0).value, "2", "PluginParameter should "
            + "start on the highest-priority plugin.");

        dialog.get_string(0).value = "1";
        param.read_from_dialog();
        assertTrue(param.get_value() != null, "PluginParameter should find all "
            + "plugins of the given type.");

        dialog.get_string(0).value = "0";
        param.read_from_dialog();
        assertTrue(param.get_value() == null, "An incorrect value in "
             + "PluginParameter should cause a null value.");
    }
    @Test public void test_sub_param()
    {
        Context context = new Context(PluginService.class, PrefService.class);
        PluginParameter<TestPluginType1> param
            = new PluginParameter<>("", TestPluginType1.class);
        context.inject(param);
        param.set_enabled(TestPlugin1.class, true);
        param.initialize();
        param.refresh_visibility();

        TestDialog dialog = new TestDialog();
        param.add_to_dialog(dialog);
        assertEquals(3, dialog.get_integer(0).value.intValue(),
            "PluginParameter should add the sub parameter.");
        try {
            dialog.get_double(0);
            assertTrue(false, "PluginParameter should not add a non-visible "
                + "sub parameter.");
        }
        catch (IndexOutOfBoundsException e) {}

        dialog.get_string(0).value = "1";
        param.read_from_dialog();
        assertTrue(param.visibility_changed(), "Changing the selected parameter"
            + " should cause visibility_changed() to be true.");
        param.refresh_visibility();

        dialog = new TestDialog();
        param.add_to_dialog(dialog);
        assertEquals(4.0, dialog.get_double(0).value.doubleValue(),
            "PluginParameter should add the sub parameter after it changed.");
        try {
            dialog.get_integer(0);
            assertTrue(false, "PluginParameter should not add a non-visible "
                + "sub parameter after it changed.");
        }
        catch (IndexOutOfBoundsException e) {}

        dialog.get_string(0).value = "4";
        param.read_from_dialog();
        assertTrue(param.get_value() instanceof TestPlugin4, "PluginParameter "
            + "should work correctly even if the current plugin has no "
            + "parameter.");
    }
    @Test public void test_enabled()
    {
        Context context = new Context(PluginService.class, PrefService.class);
        PluginParameter<TestPluginType1> param
            = new PluginParameter<>("", TestPluginType1.class);
        context.inject(param);
        param.set_enabled(TestPlugin1.class, false);
        param.initialize();
        TestDialog dialog = new TestDialog();
        param.add_to_dialog(dialog);
        dialog.get_string(0).value = "1";
        param.read_from_dialog();
        assertTrue(param.get_value() == null);

        param = new PluginParameter<>("", TestPluginType1.class);
        context.inject(param);
        param.set_enabled(TestPlugin1.class, true);
        param.initialize();
        dialog = new TestDialog();
        param.add_to_dialog(dialog);
        dialog.get_string(0).value = "1";
        param.read_from_dialog();
        assertTrue(param.get_value() != null);
    }
}
