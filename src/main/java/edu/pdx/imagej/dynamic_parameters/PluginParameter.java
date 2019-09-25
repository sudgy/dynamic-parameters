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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.PluginService;
import org.scijava.plugin.Parameter;
import org.scijava.prefs.PrefService;

/** PluginParameter is a class that that lets you choose from several plugins.
 * To use it, make your own custom plugin type that extends from
 * {@link ParameterPlugin}.  Then you can use that type in a PluginParameter and
 * it will give you a choice between all of the plugins that the SciJava context
 * finds of that type.  Whichever plugin is currently selected will have its
 * {@link ParameterPlugin#param parameter} shown.
 * <p>
 * When choosing the plugin on the dialog, the plugins will be sorted by
 * priority, and the label used will be the name of the plugin as given by the
 * <code>name</code> field of the <code>@Plugin</code> annotation.  If no name
 * was given, it will fall back to the class name.
 *
 * @param <T> The plugin type to choose from
 */
@Plugin(type = DParameter.class)
public class PluginParameter<T extends ParameterPlugin>
             extends HoldingParameter<T>
{
    /** Create a PluginParameter.
     *
     * @param label The label for the choice on the dialog.  It will also be
     *              used as the label for saving to prefs.
     * @param cls The plugin class that this parameter is holding, which must be
     *            <code>T.class</code>.
     */
    public PluginParameter(String label, Class<T> cls)
    {
        super(label);
        M_class = cls;
    }
    /** Create all of the parameters and plugins.  This will find all enabled
     * plugins that have type T and create them all, and then create and add a
     * choice parameter and all of the parameters for each plugin.
     */
    @Override
    public void initialize()
    {
        for (PluginInfo<T> info : P_pluginService.getPluginsOfType(M_class)) {
            if (P_prefs.getBoolean(PluginParameter.class, info.getClassName(),
                                   true)) {
                String name = info.getName();
                if (name == null || name.isEmpty()) name = info.getClassName();
                M_plugins.put(name, P_pluginService.createInstance(info));
            }
        }
        if (M_plugins.size() > 1) {
            String[] choices = new String[M_plugins.size()];
            int i = 0;
            for (String name : M_plugins.keySet()) {
                choices[i++] = name;
            }
            M_choice = addParameter(ChoiceParameter.class, label(),
                                     choices, choices[0]);
        }
        for (Entry<String, T> entry : M_plugins.entrySet()) {
            if (entry.getValue().param() != null) {
                M_parameters.put(entry.getKey(), entry.getValue().param());
                addPremadeParameter(entry.getValue().param());
            }
        }
        setVisibilities();
    }
    /** {@inheritDoc} */
    @Override
    public void readFromDialog()
    {
        super.readFromDialog();
        setVisibilities();
    }
    /** {@inheritDoc} */
    @Override
    public void readFromPrefs(Class<?> c, String name)
    {
        super.readFromPrefs(c, name);
        setVisibilities();
    }
    /** Get the plugin that is currently selected. */
    @Override
    public T getValue()
    {
        // If only one choice
        if (M_choice == null) return M_plugins.values().iterator().next();
        else return M_plugins.get(M_choice.getValue());
    }

    /** Get all of the plugins that are being selected from.
     *
     * @return An Iterable that iterates through all of the plugins.
     */
    public Iterable<T> getAllPlugins()
    {
        return M_plugins.values();
    }

    /** Enable or disable a plugin.  If a plugin is disabled, it cannot be
     * chosen.  If you wish to enable/disable a plugin without an instance of
     * <code>PluginParameter</code>, you can use {@link setEnabled(PrefService,
     * Class, boolean) setEnabled(PrefService, Class&lt;?&gt;, boolean)}.
     *
     * @param plugin The class for the plugin type to enable/disable.
     * @param enabled The new value for the enabled status of the plugin.
     */
    public void setEnabled(Class<? extends T> plugin, boolean enabled)
    {
        setEnabled(P_prefs, plugin, enabled);
    }
    /** Enable or disable a plugin.  If a plugin is disabled, it cannot be
     * chosen.
     *
     * @param prefs The <code>PrefService</code> to use to save the enabled
     *              value.
     * @param plugin The class for the plugin type to enable/disable.
     * @param enabled The new value for the enabled status of the plugin.
     */
    public static void setEnabled(PrefService prefs, Class<?> plugin,
                                   boolean enabled)
    {
        prefs.put(PluginParameter.class, plugin.getName(), enabled);
    }

    private void setVisibilities()
    {
        if (M_choice == null) return; // If only one choice
        for (DParameter param : M_parameters.values()) {
            param.setNewVisibility(false);
        }
        DParameter current = M_parameters.get(M_choice.getValue());
        if (current != null) current.setNewVisibility(true);
    }

    private Class<T>                    M_class;
    private ChoiceParameter             M_choice;
    private HashMap<String, T>          M_plugins    = new LinkedHashMap<>();
    private HashMap<String, DParameter> M_parameters = new HashMap<>();

    @Parameter private PluginService P_pluginService;
    @Parameter private PrefService P_prefs;
}
