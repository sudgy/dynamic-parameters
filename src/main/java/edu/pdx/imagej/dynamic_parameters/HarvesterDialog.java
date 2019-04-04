

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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import java.util.function.Supplier;
import java.util.ArrayList;
import java.text.ParseException;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.gui.DialogListener;

/** <code>HarvesterDialog</code> is the canonical implementation of
 * {@link DPDialog}.  It is what the {@link Harvester} uses to populate the
 * parameters.
 */
class HarvesterDialog implements DPDialog, DialogListener, ChangeListener {
    HarvesterDialog(String name)
    {
        M_gd = new GenericDialog(name);
        M_gd.addDialogListener(this);
    }
    void show()
    {
        M_gd.showDialog();
    }
    void dispose()
    {
        M_gd.dispose();
    }
    boolean was_canceled()
    {
        return M_gd.wasCanceled();
    }
    boolean was_finished()
    {
        return M_gd.wasCanceled() || M_gd.wasOKed();
    }
    int width()
    {
        return M_gd.getSize().width;
    }
    void set_width(int width)
    {
        M_gd.setSize(width, M_gd.getSize().height);
    }
    void set_enabled(boolean enable)
    {
        M_gd.getButtons()[0].setEnabled(enable);
    }
    @Override
    public int string_width(String string)
    {
        return M_gd.getGraphics().getFontMetrics().stringWidth(string);
    }
    void set_harvester(Harvester harvester)
    {
        M_harvester = harvester;
        M_gd.addWindowListener(harvester);
    }
    void remove_harvester(Harvester harvester)
    {
        M_gd.removeWindowListener(harvester);
    }
    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
    {
        return M_harvester.dialogItemChanged(this, e);
    }
    @Override
    public void stateChanged(ChangeEvent e)
    {
        set_enabled(M_harvester.dialogItemChanged(this, e));
    }
    /** {@inheritDoc} */
    @Override
    public Supplier<Boolean> add_boolean(String label, boolean default_value)
    {
        ++gridy;
        M_gd.addCheckbox(label, default_value);
        Checkbox last_checkbox = (Checkbox)M_gd.getCheckboxes().lastElement();
        return () -> last_checkbox.getState();
    }
    /** {@inheritDoc} */
    @Override
    public Supplier<String> add_choice(String label, String default_value, String[] choices)
    {
        ++gridy;
        M_gd.addChoice(label, choices, default_value);
        Choice last_choice = (Choice)M_gd.getChoices().lastElement();
        return () -> last_choice.getSelectedItem();
    }
    /** {@inheritDoc} */
    @Override
    public Supplier<Integer> add_choice_index(String label, String default_value, String[] choices)
    {
        ++gridy;
        M_gd.addChoice(label, choices, default_value);
        Choice last_choice = (Choice)M_gd.getChoices().lastElement();
        return () -> last_choice.getSelectedIndex();
    }
    /** {@inheritDoc} */
    @Override
    public DialogNumber<Double> add_double(String label, double default_value, String units, int decimals)
    {
        double step_size = Math.pow(10, -decimals);
        SpinnerNumberModel model = new SpinnerNumberModel(default_value,
                                                          -Double.MAX_VALUE,
                                                          Double.MAX_VALUE,
                                                          step_size);
        JSpinner spinner = new JSpinner(model);
        add_fake_number(label, spinner, units);
        return new DialogDouble(spinner, model, step_size);
    }
    /** {@inheritDoc} */
    @Override
    public DialogNumber<Integer> add_integer(String label, int default_value, String units)
    {
        SpinnerNumberModel model = new SpinnerNumberModel(default_value,
                                                          Integer.MIN_VALUE,
                                                          Integer.MAX_VALUE,
                                                          1);
        JSpinner spinner = new JSpinner(model);
        add_fake_number(label, spinner, units);
        return new DialogInt(spinner, model, 1);
    }
    /** {@inheritDoc} */
    @Override
    public void add_panel(Panel panel)
    {
        ++gridy;
        M_gd.addPanel(panel);
    }
    /** {@inheritDoc} */
    @Override
    public Supplier<String> add_radio_buttons(String label, String default_value, String[] choices, int rows, int columns)
    {
        ++gridy;
        M_gd.addRadioButtonGroup(label, choices, rows, columns, default_value);
        CheckboxGroup radio = (CheckboxGroup)M_gd.getRadioButtonGroups().lastElement();
        return () -> radio.getSelectedCheckbox().getLabel();
    }
    /** {@inheritDoc} */
    @Override
    public Supplier<String> add_text_box(String label, String default_value)
    {
        ++gridy;
        M_gd.addStringField(label, default_value);
        TextField text = (TextField)M_gd.getStringFields().lastElement();
        return () -> text.getText();
    }
    /** {@inheritDoc} */
    @Override
    public Label add_message(String message)
    {
        ++gridy;
        M_gd.addMessage(message);
        return (Label)M_gd.getMessage();
    }
    /** {@inheritDoc} */
    @Override
    public Label add_message(String message, Color color)
    {
        ++gridy;
        M_gd.addMessage(message, Font.decode(null), color);
        return (Label)M_gd.getMessage();
    }

    private void add_fake_number(String label, JSpinner spinner, String units)
    {
        spinner.setPreferredSize(new Dimension(200, spinner.getPreferredSize().height));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)spinner.getEditor();
        JTextField text_field = editor.getTextField();
        spinner.addChangeListener(this);
        ++gridy;
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 0, 3, 0);
        c.anchor = GridBagConstraints.EAST;
        c.gridwidth = 1;
        c.gridy = gridy - 1;
        M_gd.add(new Label(DParameter.display_label(label)), c);
        c.gridx = GridBagConstraints.RELATIVE;
        c.anchor = GridBagConstraints.WEST;
        Panel panel = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(spinner);
        panel.add(new Label(" " + units));
        M_gd.add(panel, c);
        M_gd.addMessage("");
    }

    private GenericDialog M_gd;
    private Harvester M_harvester;
    private int gridy;

    private abstract class DialogNum<T extends Number & Comparable<T>> implements DialogNumber<T>, DocumentListener {
        public DialogNum(JSpinner spinner, SpinnerNumberModel model, T step_size)
        {
            M_spinner = spinner;
            M_model = model;
            M_step_size = step_size;
            listen();
        }
        @Override
        public T get()
        {
            String text = get_text_field().getText();
            return parse(text);
        }
        @Override
        public void set_bounds(T min, T max)
        {
            M_model = new SpinnerNumberModel(get(), min, max, M_step_size);
            M_spinner.setModel(M_model);
            listen();
        }
        @Override public void insertUpdate(DocumentEvent ev) {document_changed(ev);}
        @Override public void removeUpdate(DocumentEvent ev) {document_changed(ev);}
        @Override public void changedUpdate(DocumentEvent ev) {document_changed(ev);}

        private void document_changed(DocumentEvent ev)
        {
            try {
                String text = ev.getDocument().getText(0, ev.getDocument().getLength());
                set_enabled(M_harvester.dialogItemChanged(HarvesterDialog.this, ev));
            }
            // Shouldn't ever happen?
            catch (BadLocationException e) {}
        }
        private void listen()
        {
            get_text_field().getDocument().addDocumentListener(this);
        }
        private JTextField get_text_field()
        {
            JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)M_spinner.getEditor();
            return editor.getTextField();
        }
        abstract protected T parse(String text);

        private JSpinner M_spinner;
        protected SpinnerNumberModel M_model;
        private T M_step_size;
    }
    private class DialogDouble extends DialogNum<Double> {
        public DialogDouble(JSpinner spinner, SpinnerNumberModel model, Double step_size)
        {super(spinner, model, step_size);}
        @Override
        public boolean in_bounds(Double value)
        {
            return (value.compareTo((Double)M_model.getMinimum()) >= 0 &&
                    value.compareTo((Double)M_model.getMaximum()) <= 0);
        }
        @Override
        protected Double parse(String text)
        {
            try {
                return Double.valueOf(text);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
    }
    private class DialogInt extends DialogNum<Integer> {
        public DialogInt(JSpinner spinner, SpinnerNumberModel model, Integer step_size)
        {super(spinner, model, step_size);}
        @Override
        public boolean in_bounds(Integer value)
        {
            return (value.compareTo((Integer)M_model.getMinimum()) >= 0 &&
                    value.compareTo((Integer)M_model.getMaximum()) <= 0);
        }
        @Override
        protected Integer parse(String text)
        {
            try {
                return Integer.valueOf(text);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
