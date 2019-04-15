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

public class HoldingParameterTest {
    @Test public void test_add_to_dialog()
    {
        TestHoldingParameter hold = new TestHoldingParameter();
        TestParameter param1 = hold.add_parameter(TestParameter.class);
        TestParameter param2 = hold.add_parameter(TestParameter.class);
        param2.set_new_visibility(false);
        param2.refresh_visibility();
        hold.add_to_dialog(null);
        assertTrue(param1.has_added_to_dialog(), "Visible parameters should be added to the dialog.");
        assertTrue(!param2.has_added_to_dialog(), "Invisible parameters should not be added to the dialog.");
    }
    @Test public void test_read_from_dialog()
    {
        TestHoldingParameter hold = new TestHoldingParameter();
        TestParameter param1 = hold.add_parameter(TestParameter.class);
        TestParameter param2 = hold.add_parameter(TestParameter.class);
        param2.set_new_visibility(false);
        param2.refresh_visibility();
        hold.read_from_dialog();
        assertTrue(param1.has_read_from_dialog(), "Visible parameters should be read from the dialog.");
        assertTrue(!param2.has_read_from_dialog(), "Invisible parameters should not be read from the dialog.");
    }
    @Test public void test_save_to_prefs()
    {
        TestHoldingParameter hold = new TestHoldingParameter();
        TestParameter param1 = hold.add_parameter(TestParameter.class);
        TestParameter param2 = hold.add_parameter(TestParameter.class);
        param2.set_new_visibility(false);
        param2.refresh_visibility();
        hold.save_to_prefs(null, null);
        assertTrue(param1.has_saved_to_prefs(), "Visible parameters should be saved to prefs.");
        assertTrue(param2.has_saved_to_prefs(), "Invisible parameters should be saved to prefs.");
    }
    @Test public void test_read_from_prefs()
    {
        TestHoldingParameter hold = new TestHoldingParameter();
        TestParameter param1 = hold.add_parameter(TestParameter.class);
        TestParameter param2 = hold.add_parameter(TestParameter.class);
        param2.set_new_visibility(false);
        param2.refresh_visibility();
        hold.read_from_prefs(null, null);
        assertTrue(param1.has_read_from_prefs(), "Visible parameters should be saved to prefs.");
        assertTrue(param2.has_read_from_prefs(), "Invisible parameters should be saved to prefs.");
    }
    @Test public void test_error()
    {
        TestHoldingParameter hold = new TestHoldingParameter();
        TestParameter param1 = hold.add_parameter(TestParameter.class);
        TestParameter param2 = hold.add_parameter(TestParameter.class);
        param2.set_new_visibility(false);
        param2.refresh_visibility();

        param1.set_error("a");
        assertEquals(hold.get_error(), "a", "A parameter's error should be propagated.");
        param1.set_error(null);
        assertTrue(hold.get_error() == null, "With no more errors in the parameters, there should be no error.");

        param2.set_error("b");
        assertTrue(hold.get_error() == null, "If an invisible parameter has an error, it should not be an error.");
        param2.set_error(null);

        hold.set_error("c");
        assertEquals(hold.get_error(), "c", "The holding parameter should be able to have its own error.");
        param1.set_error("d");
        assertEquals(hold.get_error(), "c", "The holding parameter's error should override a parameter's error.");
        hold.set_error(null);
        assertEquals(hold.get_error(), "d", "After removing the holding parameter's error, a parameter's error should be the error.");
    }
    // This class is basically HoldingParameter, but with all protected things public
    private static class TestHoldingParameter extends HoldingParameter<Boolean> {
        public TestHoldingParameter() {super("");}
        @Override public Boolean get_value() {return null;}
        @Override public <T extends DParameter<?>> T add_parameter(Class<T> cls, Object... args) {return super.add_parameter(cls, args);}
        @Override public boolean remove_parameter(DParameter<?> param) {return super.remove_parameter(param);}
        @Override public DParameter<?> remove_parameter(int index) {return super.remove_parameter(index);}
        @Override public void clear_parameters() {super.clear_parameters();}
    }
    // This class just override all of the functions to say that they have happened
    private static class TestParameter extends AbstractDParameter<Boolean> {
        public TestParameter() {super("");}
        @Override public Boolean get_value() {return null;}
        @Override public void save_to_prefs(Class<?> cls, String name)
            {M_has_saved_to_prefs = true;}
        @Override public void read_from_prefs(Class<?> cls, String name)
            {M_has_read_from_prefs = true;}
        @Override public void add_to_dialog(DPDialog dialog)
            {M_has_added_to_dialog = true;}
        @Override public void read_from_dialog()
            {M_has_read_from_dialog = true;}
        public boolean has_saved_to_prefs()
        {
            boolean result = M_has_saved_to_prefs;
            M_has_saved_to_prefs = false;
            return result;
        }
        public boolean has_read_from_prefs()
        {
            boolean result = M_has_read_from_prefs;
            M_has_read_from_prefs = false;
            return result;
        }
        public boolean has_added_to_dialog()
        {
            boolean result = M_has_added_to_dialog;
            M_has_added_to_dialog = false;
            return result;
        }
        public boolean has_read_from_dialog()
        {
            boolean result = M_has_read_from_dialog;
            M_has_read_from_dialog = false;
            return result;
        }

        private boolean M_has_saved_to_prefs = false;
        private boolean M_has_read_from_prefs = false;
        private boolean M_has_added_to_dialog = false;
        private boolean M_has_read_from_dialog = false;
    }
}
